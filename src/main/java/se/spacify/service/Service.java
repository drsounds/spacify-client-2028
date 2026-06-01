package se.spacify.service;

import javax.swing.*;
import java.awt.Component;
import java.util.function.Consumer;

/**
 * Base class for all Spacify services, modelled on Android's Service lifecycle.
 * Subclasses implement identity, authentication, and lifecycle hooks.
 */
public abstract class Service {

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /** Called once when the service is registered with ServiceManager. */
    public void onCreate() {}

    /** Called when ServiceManager.startAll() is invoked. */
    public void onStart() {}

    /** Called when ServiceManager.stopAll() is invoked. */
    public void onStop() {}

    /** Called once when the service is permanently removed from ServiceManager. */
    public void onDestroy() {}

    // ── Identity ──────────────────────────────────────────────────────────────

    public abstract String getServiceId();

    public abstract String getServiceName();

    /** Optional icon shown in service-selection UI. */
    public ImageIcon getServiceIcon() { return null; }

    // ── Authentication ────────────────────────────────────────────────────────

    public abstract boolean isAuthenticated();

    /**
     * Present whatever authentication UI the service requires.
     * Called on the EDT; the service is responsible for any background work.
     */
    public abstract void login(Component parent, Runnable onSuccess, Consumer<Exception> onError);

    public abstract void logout();

    /**
     * Returns the currently logged-in account object, or null.
     * Subclasses should narrow the return type.
     */
    public abstract Object getAccount();
}
