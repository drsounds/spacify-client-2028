package se.spacify.service.media;

/**
 * One entry in the {@link PlayQueue}. Carries the display metadata shown in the
 * queue table (name / artists / duration) together with the action that
 * actually starts playback of this entry, so the queue stays decoupled from the
 * concrete entity types (Track, Recording, LocalFile, …) that produce it.
 *
 * <p>The {@code key} is a stable play-identity (typically the {@code spacify:}
 * play URI) used to recognise the currently-playing entry across views — see
 * {@link PlayQueue#isCurrentKey(String)}.
 */
public final class PlayQueueItem {

    private final String   key;
    private final String   name;
    private final String   artists;
    private final long     durationMs;
    private final Runnable playAction;

    public PlayQueueItem(String key, String name, String artists, long durationMs, Runnable playAction) {
        this.key        = key;
        this.name       = name       != null ? name    : "";
        this.artists    = artists    != null ? artists : "";
        this.durationMs = durationMs;
        this.playAction = playAction;
    }

    /** Stable play-identity (e.g. the play URI); may be null if unknown. */
    public String getKey()        { return key; }
    public String getName()       { return name; }
    public String getArtists()    { return artists; }
    public long   getDurationMs() { return durationMs; }

    /** Starts playback of this entry on its originating service. */
    public void play() { if (playAction != null) playAction.run(); }
}
