package se.spacify.service.media;

import se.spacify.db.entity.Recording;

import javax.swing.ImageIcon;

/**
 * A single candidate in the "Play with…" chooser: a {@link MusicService} that
 * reported it can play the requested track, together with the {@link Recording}
 * it matched (used both to show the user what was found and to replay the pick
 * later without re-searching).
 */
public final class ServiceMatch {

    private final MusicService service;
    private final Recording    recording;
    private final boolean      byIsrc;

    public ServiceMatch(MusicService service, Recording recording, boolean byIsrc) {
        this.service   = service;
        this.recording = recording;
        this.byIsrc    = byIsrc;
    }

    public MusicService service()   { return service; }
    public Recording    recording() { return recording; }

    /** Whether the service matched on ISRC (vs. title/artist) — drives how it loads. */
    public boolean byIsrc() { return byIsrc; }

    public String    serviceId()   { return service.getServiceId(); }
    public String    serviceName() { return service.getServiceName(); }
    public ImageIcon icon()        { return service.getServiceIcon(); }
}
