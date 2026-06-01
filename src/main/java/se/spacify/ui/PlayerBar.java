package se.spacify.ui;

import se.spacify.service.media.MediaService;
import se.spacify.service.media.MediaService.PlaybackState;
import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class PlayerBar extends JPanel {

    private static final Color HIGHLIGHT = new Color(255, 255, 255, 35);

    // Fields exposed for service wiring
    private final JLabel  trackNameLabel;
    private final JLabel  artistLabel;
    private final JButton playPauseBtn;
    private final JSlider progress;

    private boolean updatingProgress = false;  // guard against slider feedback loop

    public PlayerBar() {
        setLayout(new BorderLayout(12, 0));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setPreferredSize(new Dimension(0, 72));
        setOpaque(true);

        // Left: track info
        JPanel trackInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        trackInfo.setOpaque(false);
        trackInfo.setPreferredSize(new Dimension(200, 0));
        trackNameLabel = new JLabel("No track playing");
        trackNameLabel.setForeground(Color.WHITE);
        trackNameLabel.setFont(trackNameLabel.getFont().deriveFont(Font.BOLD, 13f));
        artistLabel = new JLabel("");
        artistLabel.setForeground(new Color(180, 180, 180));
        artistLabel.setFont(artistLabel.getFont().deriveFont(11f));
        trackInfo.add(trackNameLabel);
        trackInfo.add(artistLabel);

        // Center: playback controls + progress
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setOpaque(false);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        buttons.add(makeControlButton("⏮"));
        buttons.add(makeControlButton("⏪"));
        playPauseBtn = makeControlButton("▶");
        playPauseBtn.setFont(playPauseBtn.getFont().deriveFont(16f));
        buttons.add(playPauseBtn);
        buttons.add(makeControlButton("⏩"));
        buttons.add(makeControlButton("⏭"));

        progress = new JSlider(0, 1000, 0);
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
        add(controls,  BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        ThemeManager.addChangeListener(this::repaint);
    }

    /**
     * Wire this bar to a MediaService.
     * Playback events update labels and progress; controls drive the service.
     */
    public void setMediaService(MediaService ms) {
        // Playback button
        playPauseBtn.addActionListener(e -> {
            if (ms.getPlaybackState() == PlaybackState.PLAYING) ms.pause();
            else ms.play();
        });

        // Progress seek
        progress.addChangeListener(e -> {
            if (!updatingProgress && !progress.getValueIsAdjusting()) {
                long dur = ms.getDurationMs();
                if (dur > 0) ms.seek((long)(progress.getValue() / 1000.0 * dur));
            }
        });

        ms.addPlaybackListener(new MediaService.PlaybackListener() {
            @Override
            public void onStateChanged(PlaybackState state) {
                SwingUtilities.invokeLater(() ->
                    playPauseBtn.setText(state == PlaybackState.PLAYING ? "⏸" : "▶"));
            }

            @Override
            public void onPositionChanged(long posMs, long durMs) {
                if (durMs <= 0) return;
                SwingUtilities.invokeLater(() -> {
                    updatingProgress = true;
                    progress.setValue((int)(posMs * 1000.0 / durMs));
                    updatingProgress = false;
                });
            }

            @Override
            public void onTrackChanged(String title, String artist, String album) {
                SwingUtilities.invokeLater(() -> {
                    trackNameLabel.setText(title  != null ? title  : "");
                    artistLabel.setText(artist != null ? artist : "");
                });
            }

            @Override
            public void onError(Exception e) {
                SwingUtilities.invokeLater(() -> trackNameLabel.setText("Error: " + e.getMessage()));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        Color tintColor = ThemeManager.getTintColor();
        g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, h, tintColor));
        g2.fillRect(0, 0, w, h);
        g2.setColor(HIGHLIGHT);
        g2.drawLine(0, h - 1, w, h - 1);
        g2.dispose();
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
