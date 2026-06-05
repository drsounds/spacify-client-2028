package se.spacify.views.library;

import se.spacify.db.DatabaseManager;
import se.spacify.db.LibraryRepository;
import se.spacify.db.entity.Recording;
import se.spacify.db.entity.Release;
import se.spacify.db.entity.Track;
import se.spacify.service.media.PlaybackCoordinator;
import se.spacify.service.media.PlayQueueItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Read-only detail page for a single release, reached from the sidebar via
 * {@code spacify:library:release:<id>}. Lists the album's tracks; double-click
 * resolves and plays a track across the registered music services.
 */
public class ReleaseDetailView extends AbstractLibraryView {

    private static final Pattern URI = Pattern.compile("spacify:library:release:(\\d+)");

    private final List<Track> rows = new ArrayList<>();
    private int releaseId = -1;

    @Override protected boolean isEditable() { return false; }

    @Override protected String[] getColumns() {
        return new String[]{"#", "Recording", "Artists"};
    }

    @Override
    public void navigate(String uri) {
        Matcher m = URI.matcher(uri);
        releaseId = m.matches() ? Integer.parseInt(m.group(1)) : -1;
    }

    @Override
    protected void reload() {
        rows.clear();
        model.setRowCount(0);
        if (releaseId < 0) { setHeader(null); return; }
        try {
            Release release = DatabaseManager.getInstance().releaseDao().queryForId(releaseId);
            if (release == null) { setHeader("Release not found"); return; }
            String artists = LibraryRepository.artistNamesForRelease(release);
            setHeader(artists.isBlank() ? release.getTitle() : release.getTitle() + " — " + artists);

            List<Track> tracks = DatabaseManager.getInstance().trackDao().queryForEq("release_id", releaseId);
            tracks.sort(Comparator.comparingInt(Track::getTrackNumber));
            for (Track t : tracks) {
                rows.add(t);
                Recording rec = t.getRecording();
                model.addRow(new Object[]{
                    t.getTrackNumber(),
                    rec != null ? rec.getTitle() : "",
                    rec != null ? LibraryRepository.artistNamesForRecording(rec) : ""
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    @Override
    protected PlayQueueItem queueItemAt(int row) {
        Track t = rows.get(row);
        Recording rec = t.getRecording();
        String name    = rec != null ? rec.getTitle() : "";
        String artists = rec != null ? LibraryRepository.artistNamesForRecording(rec) : "";
        return new PlayQueueItem(t.getPlayUri(), name, artists, t.getDurationMs(), () -> {
            String isrc   = rec != null ? rec.getIsrc()  : null;
            String title  = rec != null ? rec.getTitle() : null;
            String artist = rec != null ? LibraryRepository.primaryArtistForRecording(rec) : null;
            if (!PlaybackCoordinator.play(isrc, title, artist)) {
                PlaybackCoordinator.playUri(t.getPlayUri());
            }
        });
    }

    @Override public boolean acceptsUri(String uri) { return uri != null && URI.matcher(uri).matches(); }
    @Override public String getTitle() { return "Release"; }
}
