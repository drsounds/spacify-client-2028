package se.spacify.db;

import com.j256.ormlite.dao.Dao;
import se.spacify.db.entity.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Read/write helpers that compute the joined, display-oriented data the
 * library views need (artist-name strings from the credit join tables,
 * the album a recording appears on, etc.) and that resolve free-text artist
 * names into {@link Artist} rows when persisting credits.
 */
public final class LibraryRepository {

    private LibraryRepository() {}

    private static DatabaseManager db() { return DatabaseManager.getInstance(); }

    // ── Joined display helpers ──────────────────────────────────────────────────

    /** Comma-joined artist names for a recording, primary credits first. */
    public static String artistNamesForRecording(Recording r) {
        try {
            List<RecordingArtistCredit> credits =
                db().recordingArtistCreditDao().queryForEq("recording_id", r.getId());
            return credits.stream()
                .sorted(Comparator.comparing(RecordingArtistCredit::isPrimary).reversed())
                .map(c -> c.getArtist() != null ? c.getArtist().getName() : "")
                .filter(n -> !n.isBlank())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        } catch (Exception e) {
            return "";
        }
    }

    /** Comma-joined artist names for a release, primary credits first. */
    public static String artistNamesForRelease(Release r) {
        try {
            List<ReleaseArtistCredit> credits =
                db().releaseArtistCreditDao().queryForEq("release_id", r.getId());
            return credits.stream()
                .sorted(Comparator.comparing(ReleaseArtistCredit::isPrimary).reversed())
                .map(c -> c.getArtist() != null ? c.getArtist().getName() : "")
                .filter(n -> !n.isBlank())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        } catch (Exception e) {
            return "";
        }
    }

    /** Comma-joined titles of releases this recording appears on (via tracks). */
    public static String albumForRecording(Recording r) {
        try {
            List<Track> tracks = db().trackDao().queryForEq("recording_id", r.getId());
            LinkedHashSet<String> titles = new LinkedHashSet<>();
            for (Track t : tracks) {
                if (t.getRelease() != null && t.getRelease().getTitle() != null) {
                    titles.add(t.getRelease().getTitle());
                }
            }
            return String.join(", ", titles);
        } catch (Exception e) {
            return "";
        }
    }

    // ── Artist resolution ───────────────────────────────────────────────────────

    /** Find an artist by exact name, creating one if none exists. */
    public static Artist findOrCreateArtist(String name) throws SQLException {
        String trimmed = name.trim();
        List<Artist> existing = db().artistDao().queryForEq("name", trimmed);
        if (!existing.isEmpty()) return existing.get(0);
        Artist a = new Artist(trimmed);
        db().artistDao().create(a);
        return a;
    }

    /** Parse a comma-separated artist list into trimmed, non-blank names. */
    public static List<String> parseArtistNames(String csv) {
        List<String> out = new ArrayList<>();
        if (csv == null) return out;
        for (String part : csv.split(",")) {
            String n = part.trim();
            if (!n.isEmpty()) out.add(n);
        }
        return out;
    }

    /** Replace a recording's primary artist credits with the given names. */
    public static void setRecordingArtists(Recording r, List<String> names) throws SQLException {
        Dao<RecordingArtistCredit, Integer> dao = db().recordingArtistCreditDao();
        dao.delete(dao.queryForEq("recording_id", r.getId()));
        for (String n : names) {
            Artist a = findOrCreateArtist(n);
            dao.create(new RecordingArtistCredit(r, a, true, "performer"));
        }
    }

    /** Replace a release's primary artist credits with the given names. */
    public static void setReleaseArtists(Release r, List<String> names) throws SQLException {
        Dao<ReleaseArtistCredit, Integer> dao = db().releaseArtistCreditDao();
        dao.delete(dao.queryForEq("release_id", r.getId()));
        for (String n : names) {
            Artist a = findOrCreateArtist(n);
            dao.create(new ReleaseArtistCredit(r, a, true, "performer"));
        }
    }
}
