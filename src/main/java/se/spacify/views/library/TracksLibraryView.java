package se.spacify.views.library;

import se.spacify.db.DatabaseManager;
import se.spacify.db.LibraryRepository;
import se.spacify.db.entity.Recording;
import se.spacify.db.entity.Release;
import se.spacify.db.entity.Track;
import se.spacify.service.media.PlaybackCoordinator;
import se.spacify.service.media.PlayQueueItem;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Library view listing tracks joined to their recording (name, artists) and
 * release (album), with full CRUD. Also serves as the default library landing
 * view ({@code spacify:library}).
 */
public class TracksLibraryView extends AbstractLibraryView {

    private final List<Track> rows = new ArrayList<>();

    @Override protected String[] getColumns() {
        return new String[]{"#", "Recording", "Artists", "Album"};
    }

    @Override
    protected void reload() {
        rows.clear();
        model.setRowCount(0);
        try {
            for (Track t : DatabaseManager.getInstance().trackDao().queryForAll()) {
                rows.add(t);
                Recording rec = t.getRecording();
                model.addRow(new Object[]{
                    t.getTrackNumber(),
                    rec != null ? rec.getTitle() : "",
                    rec != null ? LibraryRepository.artistNamesForRecording(rec) : "",
                    t.getRelease() != null ? t.getRelease().getTitle() : ""
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    @Override
    protected void onAdd() {
        try {
            List<Recording> recordings = DatabaseManager.getInstance().recordingDao().queryForAll();
            List<Release>   releases   = DatabaseManager.getInstance().releaseDao().queryForAll();
            if (recordings.isEmpty() || releases.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                    "Add at least one recording and one release first.",
                    "Cannot add track", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JTextField number = new JTextField();
            JTextField side   = new JTextField();
            JTextField duration = new JTextField();
            JComboBox<Recording> recCombo = new JComboBox<>(recordings.toArray(new Recording[0]));
            JComboBox<Release>   relCombo = new JComboBox<>(releases.toArray(new Release[0]));
            if (!FormDialog.show(panel, "New Track",
                    new String[]{"Track number", "Side", "Duration (m:ss)", "Recording", "Release"},
                    new JComponent[]{number, side, duration, recCombo, relCombo})) return;

            Track t = new Track();
            t.setTrackNumber(parseInt(number.getText()));
            t.setSide(blankToNull(side.getText()));
            t.setDurationMs(parseDuration(duration.getText()));
            t.setRecording((Recording) recCombo.getSelectedItem());
            t.setRelease((Release) relCombo.getSelectedItem());
            DatabaseManager.getInstance().trackDao().create(t);
        } catch (Exception e) {
            showError(e);
        }
    }

    @Override
    protected void onEdit(int row) {
        Track t = rows.get(row);
        try {
            List<Recording> recordings = DatabaseManager.getInstance().recordingDao().queryForAll();
            List<Release>   releases   = DatabaseManager.getInstance().releaseDao().queryForAll();
            JTextField number = new JTextField(String.valueOf(t.getTrackNumber()));
            JTextField side   = new JTextField(t.getSide());
            JTextField duration = new JTextField(fmtDuration(t.getDurationMs()));
            JComboBox<Recording> recCombo = new JComboBox<>(recordings.toArray(new Recording[0]));
            JComboBox<Release>   relCombo = new JComboBox<>(releases.toArray(new Release[0]));
            selectById(recCombo, t.getRecording() != null ? t.getRecording().getId() : -1);
            selectReleaseById(relCombo, t.getRelease() != null ? t.getRelease().getId() : -1);
            if (!FormDialog.show(panel, "Edit Track",
                    new String[]{"Track number", "Side", "Duration (m:ss)", "Recording", "Release"},
                    new JComponent[]{number, side, duration, recCombo, relCombo})) return;

            t.setTrackNumber(parseInt(number.getText()));
            t.setSide(blankToNull(side.getText()));
            t.setDurationMs(parseDuration(duration.getText()));
            t.setRecording((Recording) recCombo.getSelectedItem());
            t.setRelease((Release) relCombo.getSelectedItem());
            DatabaseManager.getInstance().trackDao().update(t);
        } catch (Exception e) {
            showError(e);
        }
    }

    @Override
    protected void onDelete(int row) {
        Track t = rows.get(row);
        if (!confirmDelete("track #" + t.getTrackNumber())) return;
        try {
            DatabaseManager.getInstance().trackDao().delete(t);
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
            // Resolve across services by ISRC, then by title/artist metadata.
            String isrc   = rec != null ? rec.getIsrc()  : null;
            String title  = rec != null ? rec.getTitle() : null;
            String artist = rec != null ? LibraryRepository.primaryArtistForRecording(rec) : null;
            if (!PlaybackCoordinator.play(isrc, title, artist)) {
                PlaybackCoordinator.playUri(t.getPlayUri());
            }
        });
    }

    private static void selectById(JComboBox<Recording> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) { combo.setSelectedIndex(i); return; }
        }
    }

    private static void selectReleaseById(JComboBox<Release> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) { combo.setSelectedIndex(i); return; }
        }
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    @Override public boolean acceptsUri(String uri) {
        return uri != null && uri.matches("spacify:library(:tracks)?");
    }
    @Override public String getTitle() { return "Tracks"; }
}
