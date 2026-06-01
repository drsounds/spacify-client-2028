package se.spacify.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "artists")
public class Artist {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    /** International Standard Name Identifier — nullable until resolved. */
    @DatabaseField(unique = true, canBeNull = true)
    private String isni;

    /** MusicBrainz artist MBID (UUID string). */
    @DatabaseField(unique = true, canBeNull = true)
    private String mbid;

    @ForeignCollectionField(eager = false)
    private ForeignCollection<RecordingArtistCredit> recordingCredits;

    @ForeignCollectionField(eager = false)
    private ForeignCollection<ReleaseArtistCredit> releaseCredits;

    public Artist() {}

    public Artist(String name) { this.name = name; }

    public int    getId()              { return id; }
    public String getName()            { return name; }
    public void   setName(String v)    { this.name = v; }
    public String getIsni()            { return isni; }
    public void   setIsni(String v)    { this.isni = v; }
    public String getMbid()            { return mbid; }
    public void   setMbid(String v)    { this.mbid = v; }
    public ForeignCollection<RecordingArtistCredit> getRecordingCredits() { return recordingCredits; }
    public ForeignCollection<ReleaseArtistCredit>   getReleaseCredits()   { return releaseCredits; }

    @Override public String toString() { return name; }
}
