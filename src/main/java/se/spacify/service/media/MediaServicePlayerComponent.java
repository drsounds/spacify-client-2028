package se.spacify.service.media;

import javax.swing.JPanel;

/**
 * The per-service playback surface shown at the bottom of the right-hand
 * Now Playing panel. A {@link MediaService} that needs a visual surface (e.g. an
 * embedded web player) returns one from {@link MediaService#getPlayerComponent()};
 * audio-only services (local files) return {@code null} and contribute no surface.
 *
 * <p>The host shows exactly one component at a time — the one belonging to the
 * service that resolved the current playable (see
 * {@link PlaybackCoordinator#getActiveService()}). {@link #onActivated()} is
 * called when this component becomes that surface and {@link #onDeactivated()}
 * when another service takes over, so a component can start/suspend heavy
 * resources (such as a CEF browser) accordingly.
 */
public abstract class MediaServicePlayerComponent extends JPanel {

    private static final long serialVersionUID = 1L;

    protected MediaServicePlayerComponent() {
        setOpaque(false);
    }

    /** Called when this component becomes the active (visible) playback surface. */
    public void onActivated() {}

    /** Called when another service's component replaces this one. */
    public void onDeactivated() {}
}
