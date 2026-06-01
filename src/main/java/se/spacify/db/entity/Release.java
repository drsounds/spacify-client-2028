package se.spacify.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "releases")
public class Release {

    public enum ReleaseType { ALBUM, SINGLE, EP, COMPILATION, BROADCAST, OTHER }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String title;

    /** MusicBrainz Release MBID (UUID string). */
    @DatabaseField(unique = true, canBeNull = true)
    private String mbid;

    /** UPC or EAN barcode. */
    @DatabaseField(unique = true, canBeNull = true)
    private String upc;

    /** ISO 8601 date string (YYYY-MM-DD or YYYY-MM or YYYY). */
    @DatabaseField(canBeNull = true)
    private String releaseDate;

    @DatabaseField(dataType = DataType.ENUM_STRING, canBeNull = true)
    private ReleaseType type;

    @ForeignCollectionField(eager = false)
    private ForeignCollection<Track> tracks;

    @ForeignCollectionField(eager = false)
    private ForeignCollection<ReleaseArtistCredit> artistCredits;

    public Release() {}

    public Release(String title) { this.title = title; }

    public int         getId()                  { return id; }
    public String      getTitle()               { return title; }
    public void        setTitle(String v)        { this.title = v; }
    public String      getMbid()                { return mbid; }
    public void        setMbid(String v)         { this.mbid = v; }
    public String      getUpc()                 { return upc; }
    public void        setUpc(String v)          { this.upc = v; }
    public String      getReleaseDate()         { return releaseDate; }
    public void        setReleaseDate(String v)  { this.releaseDate = v; }
    public ReleaseType getType()                { return type; }
    public void        setType(ReleaseType v)    { this.type = v; }
    public ForeignCollection<Track>               getTracks()        { return tracks; }
    public ForeignCollection<ReleaseArtistCredit> getArtistCredits() { return artistCredits; }

    @Override public String toString() { return title; }
}
