package se.spacify.ui;

import javax.swing.*;
import java.awt.*;

public class PlayerBar extends JPanel {

    public PlayerBar() {
        setLayout(new BorderLayout(12, 0));
        setBackground(new Color(24, 24, 24));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 50, 50)),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        setPreferredSize(new Dimension(0, 72));

        // Left: track info
        JPanel trackInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        trackInfo.setOpaque(false);
        trackInfo.setPreferredSize(new Dimension(200, 0));
        JLabel trackName = new JLabel("No track playing");
        trackName.setForeground(Color.WHITE);
        trackName.setFont(trackName.getFont().deriveFont(Font.BOLD, 13f));
        JLabel artist = new JLabel("");
        artist.setForeground(new Color(160, 160, 160));
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
        JSlider volume = new JSlider(0, 100, 70);
        volume.setPreferredSize(new Dimension(100, 20));
        volume.setOpaque(false);
        rightPanel.add(volIcon);
        rightPanel.add(volume);

        add(trackInfo, BorderLayout.WEST);
        add(controls, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private JButton makeControlButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(new Color(210, 210, 210));
        btn.setFont(btn.getFont().deriveFont(14f));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
