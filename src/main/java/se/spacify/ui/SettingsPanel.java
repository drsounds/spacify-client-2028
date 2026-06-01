package se.spacify.ui;

import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final JButton accentBtn;

    public SettingsPanel() {
        setLayout(new BorderLayout(12, 0));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        setPreferredSize(new Dimension(0, 112));

        // ── Background tint sliders ──────────────────────────────────────────
        JPanel tintSection = new JPanel(new GridBagLayout());
        tintSection.setOpaque(false);
        tintSection.setBorder(titledBorder("Background Tint"));

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(1, 5, 1, 5);

        JSlider hueSlider   = mkSlider(0, 360, (int)(ThemeManager.getHue()        * 360));
        JSlider satSlider   = mkSlider(0, 100, (int)(ThemeManager.getSaturation() * 100));
        JSlider lightSlider = mkSlider(0, 100, (int)(ThemeManager.getLightness()  * 100));

        addRow(tintSection, c, 0, "Hue",        hueSlider);
        addRow(tintSection, c, 1, "Saturation", satSlider);
        addRow(tintSection, c, 2, "Lightness",  lightSlider);

        // ── Mode (dark / light) ──────────────────────────────────────────────
        JPanel modeSection = new JPanel();
        modeSection.setLayout(new BoxLayout(modeSection, BoxLayout.Y_AXIS));
        modeSection.setOpaque(false);
        modeSection.setBorder(titledBorder("Mode"));

        JRadioButton darkBtn  = new JRadioButton("Dark",  ThemeManager.isDarkMode());
        JRadioButton lightBtn = new JRadioButton("Light", !ThemeManager.isDarkMode());
        darkBtn.setOpaque(false);
        lightBtn.setOpaque(false);
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(darkBtn);
        modeGroup.add(lightBtn);
        modeSection.add(darkBtn);
        modeSection.add(Box.createVerticalStrut(2));
        modeSection.add(lightBtn);

        // ── Accent color picker ──────────────────────────────────────────────
        JPanel accentSection = new JPanel();
        accentSection.setLayout(new BoxLayout(accentSection, BoxLayout.Y_AXIS));
        accentSection.setOpaque(false);
        accentSection.setBorder(titledBorder("Accent Color"));

        accentBtn = new JButton();
        accentBtn.setBackground(ThemeManager.getAccentColor());
        accentBtn.setPreferredSize(new Dimension(48, 48));
        accentBtn.setMaximumSize(new Dimension(48, 48));
        accentBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        accentBtn.setFocusPainted(false);
        accentBtn.setToolTipText("Click to choose accent color");
        accentBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(
                SwingUtilities.getWindowAncestor(this), "Accent Color",
                ThemeManager.getAccentColor());
            if (chosen != null) {
                ThemeManager.setAccentColor(chosen);
                accentBtn.setBackground(chosen);
            }
        });
        accentSection.add(Box.createVerticalGlue());
        accentSection.add(accentBtn);
        accentSection.add(Box.createVerticalGlue());

        // ── Listeners ────────────────────────────────────────────────────────
        hueSlider.addChangeListener(e   -> ThemeManager.setHue(hueSlider.getValue() / 360f));
        satSlider.addChangeListener(e   -> ThemeManager.setSaturation(satSlider.getValue() / 100f));
        lightSlider.addChangeListener(e -> ThemeManager.setLightness(lightSlider.getValue() / 100f));
        darkBtn.addActionListener(e     -> ThemeManager.setDarkMode(true));
        lightBtn.addActionListener(e    -> ThemeManager.setDarkMode(false));

        JPanel right = new JPanel(new BorderLayout(8, 0));
        right.setOpaque(false);
        right.add(modeSection,   BorderLayout.WEST);
        right.add(accentSection, BorderLayout.EAST);

        add(tintSection, BorderLayout.CENTER);
        add(right,       BorderLayout.EAST);
    }

    private static JSlider mkSlider(int min, int max, int val) {
        JSlider s = new JSlider(min, max, val);
        s.setOpaque(false);
        s.setPreferredSize(new Dimension(220, 22));
        return s;
    }

    private static void addRow(JPanel panel, GridBagConstraints c, int row,
                               String label, JSlider slider) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(lbl.getFont().deriveFont(11f));
        lbl.setPreferredSize(new Dimension(68, 20));
        panel.add(lbl, c);
        c.gridx = 1; c.weightx = 1;
        panel.add(slider, c);
    }

    private static TitledBorder titledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), title);
        b.setTitleFont(b.getTitleFont().deriveFont(10f));
        return b;
    }
}
