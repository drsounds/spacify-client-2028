package se.spacify.service.media;

import se.spacify.db.entity.LocalFile;
import se.spacify.service.ServiceManager;

/**
 * Routes a library "play" request to a registered service. Given an ISRC it
 * asks every {@link MusicService} in turn whether it can resolve the
 * recording/track ({@link MusicService#lookup}); the first that can plays it.
 * Falls back to direct URI / local-file playback when no ISRC match exists.
 */
public final class PlaybackCoordinator {

    private PlaybackCoordinator() {}

    /**
     * Look up the ISRC across all registered music services and play it on the
     * first that resolves it.
     *
     * @return true if a service handled the request.
     */
    public static boolean playByIsrc(String isrc) {
        if (isrc == null || isrc.isBlank()) return false;
        for (MusicService ms : ServiceManager.getInstance().getServices(MusicService.class)) {
            if (ms.lookup(isrc) != null) {
                ms.loadByIsrc(isrc);
                ms.play();
                return true;
            }
        }
        return false;
    }

    /** Play an arbitrary spacify: URI on the primary media service. */
    public static boolean playUri(String uri) {
        if (uri == null) return false;
        MediaService ms = ServiceManager.getInstance().getService(MediaService.class);
        if (ms == null) return false;
        ms.loadUri(uri);
        ms.play();
        return true;
    }

    /** Play a local file directly via the local music service. */
    public static boolean playLocalFile(LocalFile file) {
        LocalMusicService local = ServiceManager.getInstance().getService(LocalMusicService.class);
        if (local == null || file == null) return false;
        local.loadLocalFile(file);
        local.play();
        return true;
    }
}
