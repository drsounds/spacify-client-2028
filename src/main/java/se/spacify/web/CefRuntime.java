package se.spacify.web;

import me.friwi.jcefmaven.CefAppBuilder;
import org.cef.CefApp;
import org.cef.CefClient;

import java.io.File;

/**
 * Owns the process-wide {@link CefApp}. The first call to {@link #newClient()}
 * builds it via jcefmaven, which downloads and unpacks the matching native
 * Chromium bundle into {@code ~/.spacify/jcef} (cached for later runs). This
 * call is slow and must run off the EDT. {@link #dispose()} shuts CEF down.
 */
public final class CefRuntime {

    private static CefApp app;

    private CefRuntime() {}

    /** Build CEF if needed and return a fresh client. Heavy on first call. */
    public static synchronized CefClient newClient() throws Exception {
        return app().createClient();
    }

    private static CefApp app() throws Exception {
        if (app == null) {
            CefAppBuilder builder = new CefAppBuilder();
            builder.setInstallDir(new File(System.getProperty("user.home"), ".spacify/jcef"));
            builder.getCefSettings().windowless_rendering_enabled = false;
            app = builder.build();
        }
        return app;
    }

    public static synchronized void dispose() {
        if (app != null) {
            app.dispose();
            app = null;
        }
    }
}
