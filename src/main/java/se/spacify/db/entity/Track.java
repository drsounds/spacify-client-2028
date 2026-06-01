package se.spacify.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A Recording's concrete appearance on a Release.
 * Carries position metadata (number, side, duration which may differ from the Recording's).
 */
@DatabaseTable(tableName = "tracks")
public class Track implements Playable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int trackNumber;

    /** Vinyl side / disc number (e.g. "A", "B", "1", "2"). */
    @DatabaseField(canBeNull = true)
    private String side;

    /** Duration as it appears on the release — may differ from Recording.durationMs. */
    @DatabaseField
    private long durationMs;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, columnName = "recording_id")
    private Recording recording;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, columnName = "release_id")
    private Release release;

    public Track() {}

    // ── Playable ──────────────────────────────────────────────────────────────

    @Override public String getPlayUri()    { return recording != null ? recording.getPlayUri() : null; }
    @Override public long   getDurationMs() { return durationMs > 0 ? durationMs : (recording != null ? recording.getDurationMs() : 0); }
    @Override public String getTitle()      { return recording != null ? recording.getTitle() : ""; }

    // ── Getters / setters ─────────────────────────────────────────────────────

    public int       getId()                     { return id; }
    public int       getTrackNumber()            { return trackNumber; }
    public void      setTrackNumber(int v)       { this.trackNumber = v; }
    public String    getSide()                   { return side; }
    public void      setSide(String v)           { this.side = v; }
    public void      setDurationMs(long v)       { this.durationMs = v; }
    public Recording getRecording()              { return recording; }
    public void      setRecording(Recording v)   { this.recording = v; }
    public Release   getRelease()                { return release; }
    public void      setRelease(Release v)       { this.release = v; }

    @Override public String toString() { return trackNumber + ". " + getTitle(); }
}
