package se.spacify.ui;

import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class PlayerBar extends JPanel {

    private static final Color CHROME_DARK = new Color(14, 14, 14);
    private static final Color HIGHLIGHT   = new Color(255, 255, 255, 35);

    public PlayerBar() {
        setLayout(new BorderLayout(12, 0));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setPreferredSize(new Dimension(0, 72));
        setOpaque(true);

        // Left: track info
        JPanel trackInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        trackInfo.setOpaque(false);
        trackInfo.setPreferredSize(new Dimension(200, 0));
        JLabel trackName = new JLabel("No track playing");
        trackName.setForeground(Color.WHITE);
        trackName.setFont(trackName.getFont().deriveFont(Font.BOLD, 13f));
        JLabel artist = new JLabel("");
        artist.setForeground(new Color(180, 180, 180));
        artist.setFont(artist.getFont().deriveFont(11f));
        trackInfo.add(trackName);
        trackInfo.add(artist);

        // Center: playback controls + progress
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setOpaque(false);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        buttons.add(makeControlButton("⏮"));
        buttons.add(makeControlButton("⏪"));
        JButton playPause = makeControlButton("▶");
        playPause.setFont(playPause.getFont().deriveFont(16f));
        buttons.add(playPause);
        buttons.add(makeControlButton("⏩"));
        buttons.add(makeControlButton("⏭"));

        JSlider progress = new JSlider(0, 100, 0);
        progress.setOpaque(false);
        progress.setMaximumSize(new Dimension(400, 20));
        progress.setAlignmentX(CENTER_ALIGNMENT);

        controls.add(buttons);
        controls.add(Box.createVerticalStrut(4));
        controls.add(progress);

        // Right: volume
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(160, 0));
        JLabel volIcon = new JLabel("🔊");
        volIcon.setForeground(Color.WHITE);
        JSlider volume = new JSlider(0, 100, 70);
        volume.setPreferredSize(new Dimension(100, 20));
        volume.setOpaque(false);
        rightPanel.add(volIcon);
        rightPanel.add(volume);

        add(trackInfo, BorderLayout.WEST);
        add(controls, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        ThemeManager.addChangeListener(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();

        // Gradient: near-black at top → accent-tinted dark at bottom (mirror of header)
        Color bottom = accentDark(0.22f);
        g2.setPaint(new GradientPaint(0, 0, bottom, 0, h, CHROME_DARK));
        g2.fillRect(0, 0, w, h);

        // 1 px white sheen along the very bottom edge
        g2.setColor(HIGHLIGHT);
        g2.drawLine(0, h - 1, w, h - 1);

        g2.dispose();
    }

    private static Color accentDark(float ratio) {
        Color a = ThemeManager.getTintColor();
        float r = 1 - ratio;
        return new Color(
            Math.min(255, (int)(a.getRed()   * ratio + CHROME_DARK.getRed()   * r)),
            Math.min(255, (int)(a.getGreen() * ratio + CHROME_DARK.getGreen() * r)),
            Math.min(255, (int)(a.getBlue()  * ratio + CHROME_DARK.getBlue()  * r))
        );
    }

    private JButton makeControlButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(btn.getFont().deriveFont(14f));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
