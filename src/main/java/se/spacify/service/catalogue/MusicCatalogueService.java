package se.spacify.service.catalogue;

import se.spacify.db.entity.Artist;
import se.spacify.db.entity.Recording;
import se.spacify.db.entity.Release;
import se.spacify.service.Service;

import java.util.List;

/**
 * Discovery aspect: catalogue browsing and search. Implementations can connect
 * to Discogs, MusicBrainz, etc. A service may combine this aspect with the
 * streaming aspect ({@link se.spacify.service.media.MediaService}) to both find
 * and play music.
 */
public interface MusicCatalogueService extends Service {

    List<Release>   searchReleases(String query);
    List<Recording> searchRecordings(String query);
    List<Artist>    searchArtists(String query);

    Release   getReleaseByMbid(String mbid);
    Recording getRecordingByIsrc(String isrc);
    Artist    getArtistByIsni(String isni);
}
