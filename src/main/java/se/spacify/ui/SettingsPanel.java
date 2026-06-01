package se.spacify.ui;

import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private static final Color BG   = Color.BLACK;
    private static final Color FG   = Color.WHITE;
    private static final Color FG_DIM = new Color(180, 180, 180);

    public SettingsPanel() {
        setLayout(new BorderLayout(12, 0));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 50, 50)),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        setPreferredSize(new Dimension(0, 112));

        // ── Background tint sliders ──────────────────────────────────────────
        JPanel tintSection = new JPanel(new GridBagLayout()) {
            @Override public void updateUI() { super.updateUI(); setOpaque(false); }
        };
        tintSection.setOpaque(false);
        tintSection.setBorder(titledBorder("Background Tint"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(1, 5, 1, 5);

        JSlider hueSlider   = mkSlider(0, 360, (int)(ThemeManager.getHue()        * 360));
        JSlider satSlider   = mkSlider(0, 100, (int)(ThemeManager.getSaturation() * 100));
        JSlider lightSlider = mkSlider(0, 100, (int)(ThemeManager.getLightness()  * 100));

        addRow(tintSection, c, 0, "Hue",        hueSlider);
        addRow(tintSection, c, 1, "Saturation", satSlider);
        addRow(tintSection, c, 2, "Lightness",  lightSlider);

        // ── Mode (dark / light) ──────────────────────────────────────────────
        JPanel modeSection = new JPanel() {
            @Override public void updateUI() { super.updateUI(); setOpaque(false); }
        };
        modeSection.setLayout(new BoxLayout(modeSection, BoxLayout.Y_AXIS));
        modeSection.setOpaque(false);
        modeSection.setBorder(titledBorder("Mode"));

        JRadioButton darkBtn  = whiteRadio("Dark",  ThemeManager.isDarkMode());
        JRadioButton lightBtn = whiteRadio("Light", !ThemeManager.isDarkMode());
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(darkBtn);
        modeGroup.add(lightBtn);
        modeSection.add(darkBtn);
        modeSection.add(Box.createVerticalStrut(2));
        modeSection.add(lightBtn);

        // ── Accent color picker ──────────────────────────────────────────────
        JPanel accentSection = new JPanel() {
            @Override public void updateUI() { super.updateUI(); setOpaque(false); }
        };
        accentSection.setLayout(new BoxLayout(accentSection, BoxLayout.Y_AXIS));
        accentSection.setOpaque(false);
        accentSection.setBorder(titledBorder("Accent Color"));

        JButton accentBtn = new JButton();
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

        JPanel right = new JPanel(new BorderLayout(8, 0)) {
            @Override public void updateUI() { super.updateUI(); setOpaque(false); }
        };
        right.setOpaque(false);
        right.add(modeSection,   BorderLayout.WEST);
        right.add(accentSection, BorderLayout.EAST);

        add(tintSection, BorderLayout.CENTER);
        add(right,       BorderLayout.EAST);
    }

    // Force black bg to survive updateComponentTreeUI
    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(BG);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(BG);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static JSlider mkSlider(int min, int max, int val) {
        JSlider s = new JSlider(min, max, val);
        s.setOpaque(false);
        return s;
    }

    private static void addRow(JPanel panel, GridBagConstraints c, int row,
                               String text, JSlider slider) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        JLabel lbl = whiteLabel(text);
        lbl.setPreferredSize(new Dimension(68, 20));
        panel.add(lbl, c);

        c.gridx = 1; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(slider, c);
    }

    private static JLabel whiteLabel(String text) {
        return new JLabel(text) {
            @Override public void updateUI() {
                super.updateUI();
                setForeground(FG);
            }
        };
    }

    private static JRadioButton whiteRadio(String text, boolean selected) {
        return new JRadioButton(text, selected) {
            @Override public void updateUI() {
                super.updateUI();
                setForeground(FG);
                setOpaque(false);
            }
        };
    }

    private static TitledBorder titledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), title);
        b.setTitleFont(b.getTitleFont().deriveFont(10f));
        b.setTitleColor(FG_DIM);
        return b;
    }
}
