package se.spacify.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** Join between a Release and an Artist, with role metadata. */
@DatabaseTable(tableName = "release_artist_credits")
public class ReleaseArtistCredit {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, columnName = "release_id")
    private Release release;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, columnName = "artist_id")
    private Artist artist;

    @DatabaseField
    private boolean primary;

    /** e.g. "performer", "composer", "producer". */
    @DatabaseField(canBeNull = true)
    private String role;

    public ReleaseArtistCredit() {}

    public ReleaseArtistCredit(Release release, Artist artist, boolean primary, String role) {
        this.release  = release;
        this.artist   = artist;
        this.primary  = primary;
        this.role     = role;
    }

    public int     getId()               { return id; }
    public Release getRelease()          { return release; }
    public void    setRelease(Release v) { this.release = v; }
    public Artist  getArtist()           { return artist; }
    public void    setArtist(Artist v)   { this.artist = v; }
    public boolean isPrimary()           { return primary; }
    public void    setPrimary(boolean v) { this.primary = v; }
    public String  getRole()             { return role; }
    public void    setRole(String v)     { this.role = v; }
}
