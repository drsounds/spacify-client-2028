package se.spacify.ui;

import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final JPanel swatch;

    public SettingsPanel() {
        setLayout(new BorderLayout(16, 0));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));
        setPreferredSize(new Dimension(0, 118));

        // ── Sliders section ──────────────────────────────────────────────────
        JPanel sliderSection = new JPanel(new GridBagLayout());
        sliderSection.setOpaque(false);
        sliderSection.setBorder(titledBorder("Color Accent"));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(1, 6, 1, 6);

        JSlider hueSlider   = hslSlider(0, 360, (int)(ThemeManager.getHue()        * 360));
        JSlider satSlider   = hslSlider(0, 100, (int)(ThemeManager.getSaturation() * 100));
        JSlider lightSlider = hslSlider(0, 100, (int)(ThemeManager.getLightness()  * 100));

        addSliderRow(sliderSection, lc, 0, "Hue",        hueSlider);
        addSliderRow(sliderSection, lc, 1, "Saturation", satSlider);
        addSliderRow(sliderSection, lc, 2, "Lightness",  lightSlider);

        // ── Swatch ───────────────────────────────────────────────────────────
        swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(44, 44));
        swatch.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        swatch.setBackground(ThemeManager.getAccentColor());

        JPanel swatchWrap = new JPanel();
        swatchWrap.setLayout(new BoxLayout(swatchWrap, BoxLayout.Y_AXIS));
        swatchWrap.setOpaque(false);
        swatchWrap.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        JLabel swatchLabel = new JLabel("Preview");
        swatchLabel.setFont(swatchLabel.getFont().deriveFont(10f));
        swatchLabel.setForeground(new Color(150, 150, 150));
        swatchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        swatch.setAlignmentX(Component.CENTER_ALIGNMENT);
        swatchWrap.add(swatchLabel);
        swatchWrap.add(Box.createVerticalStrut(4));
        swatchWrap.add(swatch);

        // ── Mode panel ───────────────────────────────────────────────────────
        JPanel modePanel = new JPanel();
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));
        modePanel.setOpaque(false);
        modePanel.setBorder(titledBorder("Mode"));

        JRadioButton darkBtn  = new JRadioButton("Dark",  ThemeManager.isDarkMode());
        JRadioButton lightBtn = new JRadioButton("Light", !ThemeManager.isDarkMode());
        darkBtn.setOpaque(false);
        lightBtn.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        group.add(darkBtn);
        group.add(lightBtn);
        modePanel.add(darkBtn);
        modePanel.add(Box.createVerticalStrut(2));
        modePanel.add(lightBtn);

        // ── Wire listeners ───────────────────────────────────────────────────
        hueSlider.addChangeListener(e -> {
            ThemeManager.setHue(hueSlider.getValue() / 360f);
            refreshSwatch();
        });
        satSlider.addChangeListener(e -> {
            ThemeManager.setSaturation(satSlider.getValue() / 100f);
            refreshSwatch();
        });
        lightSlider.addChangeListener(e -> {
            ThemeManager.setLightness(lightSlider.getValue() / 100f);
            refreshSwatch();
        });
        darkBtn.addActionListener(e  -> ThemeManager.setDarkMode(true));
        lightBtn.addActionListener(e -> ThemeManager.setDarkMode(false));

        add(sliderSection, BorderLayout.CENTER);
        add(swatchWrap,    BorderLayout.WEST);
        add(modePanel,     BorderLayout.EAST);
    }

    private void refreshSwatch() {
        swatch.setBackground(ThemeManager.getAccentColor());
        swatch.repaint();
    }

    private static JSlider hslSlider(int min, int max, int val) {
        JSlider s = new JSlider(min, max, val);
        s.setOpaque(false);
        s.setPreferredSize(new Dimension(240, 22));
        return s;
    }

    private static void addSliderRow(JPanel panel, GridBagConstraints c, int row,
                                     String label, JSlider slider) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(lbl.getFont().deriveFont(11f));
        lbl.setPreferredSize(new Dimension(70, 20));
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
