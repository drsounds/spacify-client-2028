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

	private JPanel mainBar;

	private GlassPanel leftPanel;

	private JPanel controls;

	private JPanel buttons;

	private GlassPanel rightPanel;

	private JSlider progress;

	private JPanel progressPanel;

	private JButton backwardButton;

	private JButton forwardButton;

    public PlayerBar() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setPreferredSize(new Dimension(0, 72));
        setOpaque(true);
  
        progressPanel = new JPanel(new BorderLayout());
        progressPanel.setMinimumSize(new Dimension(0, 18));
        progressPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 18));
        progressPanel.setOpaque(false);
        add(progressPanel);
       
        backwardButton = makeControlButton("⏪");
        progressPanel.add(backwardButton, BorderLayout.WEST);
        progress = new JSlider(0, 1000, 0);
        progress.setOpaque(false); 
        progressPanel.add(progress, BorderLayout.CENTER);
        forwardButton = makeControlButton("⏩");
        progressPanel.add(forwardButton, BorderLayout.EAST);
        mainBar = new JPanel();
        mainBar.setLayout(new BoxLayout(mainBar, BoxLayout.LINE_AXIS));
        mainBar.setOpaque(false);
        add(mainBar);
        
        // Left: track info
        leftPanel = new GlassPanel();
		leftPanel.setLayout(new GridLayout(2, 1, 0, 2));
		leftPanel.setOpaque(false);
		leftPanel.setPreferredSize(new Dimension(200, 0));
		leftPanel.setTrailingDiagonal(true);   // sharp left edge, bottom longer than top
        leftPanel.setDiagonalInset(65);
        leftPanel.setPreferredSize(new Dimension(160, 0));
        trackNameLabel = new JLabel("No track playing");
        trackNameLabel.setForeground(Color.WHITE);
        trackNameLabel.setFont(trackNameLabel.getFont().deriveFont(Font.BOLD, 13f));
        artistLabel = new JLabel("");
        artistLabel.setForeground(new Color(180, 180, 180));
        artistLabel.setFont(artistLabel.getFont().deriveFont(11f));
        leftPanel.add(trackNameLabel);
        leftPanel.add(artistLabel);

        // Center: playback controls + progress
        controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setOpaque(false);

        buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        buttons.add(makeControlButton("⏮"));
        playPauseBtn = makeControlButton("▶");
        playPauseBtn.setFont(playPauseBtn.getFont().deriveFont(16f));
        buttons.add(playPauseBtn);
        buttons.add(makeControlButton("⏭"));

        controls.add(buttons);
        controls.add(Box.createVerticalStrut(4));
       
        // Right: volume
        //right.add(searchField);
        rightPanel = new GlassPanel();
        rightPanel.setPreferredSize(new Dimension(300, 46));
        rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        rightPanel.setLeadingDiagonal(true);   // sharp left edge, bottom longer than top
        rightPanel.setDiagonalInset(65);
        rightPanel.setPreferredSize(new Dimension(160, 0));
        JLabel volIcon = new JLabel("🔊");
        volIcon.setForeground(Color.WHITE);
        JSlider volume = new JSlider(0, 100, 70);
        volume.setPreferredSize(new Dimension(100, 20));
        volume.setOpaque(false);
        rightPanel.add(volIcon);
        rightPanel.add(volume);

        mainBar.add(leftPanel);
        mainBar.add(controls);
        mainBar.add(rightPanel);

        ThemeManager.addChangeListener(this::repaint);
    }

    /**
     * Wire this bar to a MediaService.
     * Playback events update labels and progress; controls drive the service.
     */
    public void setMediaService(MediaService ms) {
        playPauseBtn.addActionListener(e -> {
            if (ms.getPlaybackState() == PlaybackState.PLAYING) ms.pause();
            else ms.play();
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
        //Color tintColor = ThemeManager.getTintColor();        
        //g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, h, tintColor));
        g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h, ThemeManager.accentLight(2f)));
        g2.fillRect(0, 0, w, h);
        g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, 18, Color.WHITE));
        g2.fillRect(0, 0, w, 18);
        
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 127), 0, h, new Color(255, 255, 255, 0)));
        g2.fillRect(0, 1, w, (h / 2));
        g2.setColor(HIGHLIGHT);
        g2.drawLine(0, h - 1, w, h - 1);
        g2.dispose();
    }

    private JButton makeControlButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(btn.getFont().deriveFont(14f));
        return btn;
    }
}
