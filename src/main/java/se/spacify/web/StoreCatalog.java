package se.spacify.web;

import java.util.List;

/**
 * Hardcoded list of known music-service "stores" shown in the navigation bar's
 * stores dropdown. Selecting one opens it in the {@code SPServiceWebView} via a
 * {@code spacify:store:<host>} URI. (Temporary — a dynamic service registry
 * will replace this later.)
 */
public final class StoreCatalog {

    public record Store(String name, String host) {
        public String uri() { return SiteUri.STORE_PREFIX + host; }
    }

    public static final List<Store> STORES = List.of(
        new Store("Subvert",  "subvert.fm"),
        new Store("Jamendo",  "jamendo.com"),
        new Store("Bandcamp", "bandcamp.com"),
        new Store("Last.fm",  "last.fm")
    );

    private StoreCatalog() {}
}
