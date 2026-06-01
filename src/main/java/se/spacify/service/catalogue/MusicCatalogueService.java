package se.spacify.service.catalogue;

import se.spacify.db.entity.Artist;
import se.spacify.db.entity.Recording;
import se.spacify.db.entity.Release;
import se.spacify.service.Service;

import java.util.List;

/**
 * Abstract catalogue-browsing service.
 * Implementations can connect to Discogs, MusicBrainz, etc.
 */
public abstract class MusicCatalogueService extends Service {

    public abstract List<Release>   searchReleases(String query);
    public abstract List<Recording> searchRecordings(String query);
    public abstract List<Artist>    searchArtists(String query);

    public abstract Release   getReleaseByMbid(String mbid);
    public abstract Recording getRecordingByIsrc(String isrc);
    public abstract Artist    getArtistByIsni(String isni);
}
