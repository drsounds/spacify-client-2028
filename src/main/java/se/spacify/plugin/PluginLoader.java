package se.spacify.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Discovers {@link PluginDescriptor}s from the three sources (built-in bundle,
 * {@code <app>/plugins}, {@code ~/Bungalow}) and builds an isolated
 * {@link URLClassLoader} (parent = application loader) for each jar plugin.
 */
final class PluginLoader {

    private final ClassLoader parent = PluginLoader.class.getClassLoader();

    /** All discoverable descriptors, built-ins first. */
    List<PluginDescriptor> discover() {
        List<PluginDescriptor> out = new ArrayList<>(BuiltinPluginRegistry.descriptors());
        out.addAll(scanDir(PluginPaths.appPluginsDir(), PluginDescriptor.Source.APP_DIR));
        out.addAll(scanDir(PluginPaths.bungalow(),      PluginDescriptor.Source.EXTERNAL));
        return out;
    }

    private List<PluginDescriptor> scanDir(Path dir, PluginDescriptor.Source source) {
        List<PluginDescriptor> out = new ArrayList<>();
        if (dir == null || !Files.isDirectory(dir)) return out;
        try (Stream<Path> jars = Files.list(dir)) {
            jars.filter(p -> p.toString().toLowerCase().endsWith(".jar"))
                .sorted()
                .forEach(p -> {
                    PluginDescriptor d = fromJar(p.toFile(), source);
                    if (d != null) out.add(d);
                });
        } catch (Exception ignored) {}
        return out;
    }

    /** Build a descriptor for a single jar, or null if it isn't a valid plugin. */
    PluginDescriptor fromJar(File jar, PluginDescriptor.Source source) {
        try {
            PluginManifest mf = PluginManifest.fromJar(jar);
            if (mf == null) return null;
            URLClassLoader cl = new URLClassLoader(
                new URL[]{ jar.toURI().toURL() }, parent);
            return new PluginDescriptor(mf.getId(), mf.getName(), mf.getVersion(),
                mf.getMainClass(), source, jar, cl);
        } catch (Exception e) {
            return null;
        }
    }
}
