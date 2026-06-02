package se.spacify.service.media;

import se.spacify.db.entity.LocalFile;
import se.spacify.service.ServiceManager;

/**
 * Routes a library "play" request to a registered service. It first asks every
 * {@link MusicService} to resolve the row by ISRC ({@link MusicService#lookup}),
 * then falls back to a title/artist metadata lookup
 * ({@link MusicService#lookupByTitleArtist}); the first service that resolves
 * the request plays it. Also supports direct URI / local-file playback.
 */
public final class PlaybackCoordinator {

    private PlaybackCoordinator() {}

    /**
     * Resolve a track across all registered services, by ISRC first and then by
     * title/artist metadata, playing it on the first service that resolves it.
     *
     * @return true if a service handled the request.
     */
    public static boolean play(String isrc, String title, String artist) {
        return playByIsrc(isrc) || playByMetadata(title, artist);
    }

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

    /**
     * Look up by title/artist metadata across all registered music services and
     * play it on the first that resolves it.
     *
     * @return true if a service handled the request.
     */
    public static boolean playByMetadata(String title, String artist) {
        if (title == null || title.isBlank()) return false;
        for (MusicService ms : ServiceManager.getInstance().getServices(MusicService.class)) {
            if (ms.lookupByTitleArtist(title, artist) != null) {
                ms.loadByTitleArtist(title, artist);
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
