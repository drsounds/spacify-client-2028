package se.spacify.app;

import se.spacify.config.ConfigManager;
import se.spacify.db.DatabaseManager;
import se.spacify.service.ServiceManager;
import se.spacify.service.media.LocalMusicService;
import se.spacify.ui.MainWindow;
import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;

public class SpacifyApp {
    public static void main(String[] args) {
        // ── Look and feel ─────────────────────────────────────────────────────
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fall back to default L&F
        }

        // ── Theme ─────────────────────────────────────────────────────────────
        ConfigManager.load();
        ThemeManager.applyToDefaults();
        ThemeManager.addChangeListener(ConfigManager::save);

        // ── Database ──────────────────────────────────────────────────────────
        try {
            DatabaseManager.getInstance().init();
        } catch (Exception e) {
            System.err.println("Warning: could not initialise library database: " + e.getMessage());
        }

        // ── Services ──────────────────────────────────────────────────────────
        ServiceManager sm = ServiceManager.getInstance();
        sm.register(new LocalMusicService());
        sm.startAll();

        // ── Register shutdown hook ────────────────────────────────────────────
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            sm.shutdownAll();
            se.spacify.web.CefRuntime.dispose();
            DatabaseManager.getInstance().close();
        }));

        // ── UI ────────────────────────────────────────────────────────────────
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
