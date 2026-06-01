package se.spacify.service.media;

import se.spacify.service.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract media-playback service.
 * Manages a PlaybackListener list and fires events to all subscribers.
 */
public abstract class MediaService extends Service {

    // ── State enum ────────────────────────────────────────────────────────────

    public enum PlaybackState { IDLE, LOADING, PLAYING, PAUSED, STOPPED, ERROR }

    // ── Listener interface ────────────────────────────────────────────────────

    public interface PlaybackListener {
        void onStateChanged(PlaybackState state);
        void onPositionChanged(long positionMs, long durationMs);
        void onTrackChanged(String title, String artist, String album);
        void onError(Exception e);
    }

    private final List<PlaybackListener> listeners = new ArrayList<>();

    public void addPlaybackListener(PlaybackListener l)    { listeners.add(l); }
    public void removePlaybackListener(PlaybackListener l) { listeners.remove(l); }

    // ── Abstract playback API ─────────────────────────────────────────────────

    public abstract void play();
    public abstract void pause();
    public abstract void stop();
    public abstract void seek(long positionMs);
    public abstract void loadUri(String uri);

    public abstract PlaybackState getPlaybackState();
    public abstract long getPositionMs();
    public abstract long getDurationMs();

    // ── Protected fire helpers ────────────────────────────────────────────────

    protected void fireStateChanged(PlaybackState state) {
        for (PlaybackListener l : List.copyOf(listeners)) l.onStateChanged(state);
    }

    protected void firePositionChanged(long posMs, long durMs) {
        for (PlaybackListener l : List.copyOf(listeners)) l.onPositionChanged(posMs, durMs);
    }

    protected void fireTrackChanged(String title, String artist, String album) {
        for (PlaybackListener l : List.copyOf(listeners)) l.onTrackChanged(title, artist, album);
    }

    protected void fireError(Exception e) {
        for (PlaybackListener l : List.copyOf(listeners)) l.onError(e);
    }
}
