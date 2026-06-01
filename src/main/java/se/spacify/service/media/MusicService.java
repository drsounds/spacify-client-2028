package se.spacify.service.media;

import se.spacify.db.entity.Recording;

/**
 * Abstract music-playback service.
 * Adds ISRC-based and title-based loading on top of MediaService.
 */
public abstract class MusicService extends MediaService {

    /** Load and queue the recording identified by its ISRC code. */
    public abstract void loadByIsrc(String isrc);

    /** Best-effort lookup by title + artist name (fuzzy / exact depends on impl). */
    public abstract void loadByTitleArtist(String title, String artist);

    /**
     * Look up a Recording by ISRC without loading it for playback.
     * Returns null if not found.
     */
    public abstract Recording lookup(String isrc);
}
