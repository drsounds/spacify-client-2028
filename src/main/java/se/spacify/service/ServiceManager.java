package se.spacify.service;

import se.spacify.navigation.SPViewStack;
import se.spacify.navigation.SidebarNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

/**
 * Central singleton registry for Services and Features.
 * Call register() for each, then startAll() to drive onCreate → onStart.
 */
public class ServiceManager {

    private static ServiceManager instance;

    private final Map<String, Service> services = new LinkedHashMap<>();
    private final List<Feature>        features  = new ArrayList<>();

    private ServiceManager() {}

    public static ServiceManager getInstance() {
        if (instance == null) instance = new ServiceManager();
        return instance;
    }

    // ── Service registration ──────────────────────────────────────────────────

    public void register(Service service) {
        services.put(service.getServiceId(), service);
        service.onCreate();
    }

    public void unregister(String serviceId) {
        Service s = services.remove(serviceId);
        if (s != null) { s.onStop(); s.onDestroy(); }
    }

    public void startAll() {
        for (Service s : services.values()) s.onStart();
    }

    public void stopAll() {
        for (Service s : services.values()) s.onStop();
    }

    public void shutdownAll() {
        for (Service s : services.values()) { s.onStop(); s.onDestroy(); }
        services.clear();
    }

    /** Returns the first registered service of the given type, or null. */
    @SuppressWarnings("unchecked")
    public <T extends Service> T getService(Class<T> type) {
        for (Service s : services.values())
            if (type.isInstance(s)) return (T) s;
        return null;
    }

    /** Returns all registered services of the given type. */
    @SuppressWarnings("unchecked")
    public <T extends Service> List<T> getServices(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Service s : services.values())
            if (type.isInstance(s)) result.add((T) s);
        return result;
    }

    public Collection<Service> allServices() { return Collections.unmodifiableCollection(services.values()); }

    // ── Feature registration ──────────────────────────────────────────────────

    public void register(Feature feature) {
        features.add(feature);
        feature.onRegister(this);
    }

    /**
     * Wire all registered features into the running UI.
     * Call this after MainWindow has been built and the SPViewStack is live.
     *
     * @param viewStack     the app's main view stack
     * @param sidebarRoot   root node of the sidebar JTree model
     */
    public void activateFeatures(SPViewStack viewStack, DefaultMutableTreeNode sidebarRoot) {
        for (Feature f : features) {
            f.getViews().forEach(viewStack::registerView);
            for (SidebarNode sn : f.getSidebarNodes()) {
                sidebarRoot.add(buildTreeNode(sn));
            }
        }
    }

    private static DefaultMutableTreeNode buildTreeNode(SidebarNode sn) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(sn);
        for (SidebarNode child : sn.getChildren()) node.add(buildTreeNode(child));
        return node;
    }

    public List<Feature> allFeatures() { return Collections.unmodifiableList(features); }
}
