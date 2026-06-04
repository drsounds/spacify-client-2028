package se.spacify.plugin.localmusic;

import se.spacify.plugin.Plugin;
import se.spacify.plugin.PluginContext;
import se.spacify.service.media.LocalMusicService;

/**
 * Built-in plugin providing local file playback. Registers the
 * {@link LocalMusicService} (the music-streaming aspect) so the player bar,
 * now-playing queue and {@code PlaybackCoordinator} can resolve and play tracks.
 */
public class LocalMusicPlugin implements Plugin {

    @Override
    public void onActivate(PluginContext ctx) {
        ctx.registerService(new LocalMusicService());
    }
}
