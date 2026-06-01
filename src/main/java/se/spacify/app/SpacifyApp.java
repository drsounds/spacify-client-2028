package se.spacify.app;

import se.spacify.config.ConfigManager;
import se.spacify.ui.MainWindow;
import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;

public class SpacifyApp {
    public static void main(String[] args) {
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

        ConfigManager.load();          // restore saved HSL + accent (calls applyToDefaults internally)
        ThemeManager.applyToDefaults(); // ensure defaults applied even when no config file exists
        ThemeManager.addChangeListener(ConfigManager::save);  // auto-save on every change

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}

