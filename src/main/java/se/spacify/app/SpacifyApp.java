package se.spacify.app;

import se.spacify.ui.MainWindow;

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

        // Dark Nimbus overrides
        UIManager.put("nimbusBase", new java.awt.Color(18, 18, 18));
        UIManager.put("nimbusBlueGrey", new java.awt.Color(30, 30, 30));
        UIManager.put("control", new java.awt.Color(30, 30, 30));
        UIManager.put("text", new java.awt.Color(210, 210, 210));
        UIManager.put("nimbusFocus", new java.awt.Color(30, 215, 96));
        UIManager.put("nimbusSelectionBackground", new java.awt.Color(30, 215, 96));
        UIManager.put("textForeground", new java.awt.Color(210, 210, 210));
        UIManager.put("nimbusDisabledText", new java.awt.Color(100, 100, 100));

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}

