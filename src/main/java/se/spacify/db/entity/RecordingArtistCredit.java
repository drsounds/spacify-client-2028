package se.spacify.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** Join between a Recording and an Artist, with role metadata. */
@DatabaseTable(tableName = "recording_artist_credits")
public class RecordingArtistCredit {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, columnName = "recording_id")
    private Recording recording;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, columnName = "artist_id")
    private Artist artist;

    /** True if this is the primary/credited artist (as opposed to a featured artist). */
    @DatabaseField
    private boolean primary;

    /** e.g. "performer", "composer", "producer", "conductor", "lyricist". */
    @DatabaseField(canBeNull = true)
    private String role;

    public RecordingArtistCredit() {}

    public RecordingArtistCredit(Recording recording, Artist artist, boolean primary, String role) {
        this.recording = recording;
        this.artist    = artist;
        this.primary   = primary;
        this.role      = role;
    }

    public int       getId()                { return id; }
    public Recording getRecording()         { return recording; }
    public void      setRecording(Recording v) { this.recording = v; }
    public Artist    getArtist()            { return artist; }
    public void      setArtist(Artist v)    { this.artist = v; }
    public boolean   isPrimary()            { return primary; }
    public void      setPrimary(boolean v)  { this.primary = v; }
    public String    getRole()              { return role; }
    public void      setRole(String v)      { this.role = v; }
}
