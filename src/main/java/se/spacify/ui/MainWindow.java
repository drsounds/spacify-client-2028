package se.spacify.ui;

import se.spacify.navigation.SPViewStack;
import se.spacify.service.ServiceManager;
import se.spacify.service.media.MediaService;
import se.spacify.service.media.MediaService.PlaybackState;
import se.spacify.ui.theme.ThemeManager;
import se.spacify.views.*;
import se.spacify.views.library.*;
import se.spacify.views.web.SPWebView;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class MainWindow extends JFrame {

    private final SPViewStack    viewStack;
    private final PlayerBar      playerBar;
    private final NowPlayingView nowPlayingView;
    private final Sidebar        sidebar;
    private final JSlider progress;
    private boolean updatingProgress = false;
	private JSplitPane leftSplit;
	private JSplitPane mainSplit;

    public MainWindow() {
        super("Spacify");
        setUndecorated(true);  // remove native title bar + border on all platforms
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        // 1px border so the window edge is visible against the desktop
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40), 1));

        viewStack      = new SPViewStack();
        nowPlayingView = new NowPlayingView();
        viewStack.registerView(nowPlayingView);
        viewStack.registerView(new SearchView());
        viewStack.registerView(new ArtistsLibraryView());
        viewStack.registerView(new RecordingsLibraryView());
        viewStack.registerView(new ReleasesLibraryView());
        viewStack.registerView(new LocalFileLibraryView());
        viewStack.registerView(new ReleaseDetailView());
        viewStack.registerView(new ArtistDetailView());
        viewStack.registerView(new TracksLibraryView());
        viewStack.registerView(new SPWebView(viewStack));
        viewStack.registerView(new PlaylistView());

        getContentPane().setLayout(new BorderLayout());

        NavigationBar navBar = new NavigationBar(viewStack);
        add(navBar, BorderLayout.NORTH);

        sidebar = new Sidebar(viewStack);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(viewStack, BorderLayout.CENTER);

        leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, centerPanel);
        leftSplit.setDividerLocation(220);
        leftSplit.setDividerSize(1);
        leftSplit.setBorder(null);
        leftSplit.setContinuousLayout(true);

        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, new NowPlayingPanel(viewStack));
        mainSplit.setDividerLocation(880);
        mainSplit.setDividerSize(1);
        mainSplit.setBorder(null);
        mainSplit.setContinuousLayout(true);

        add(mainSplit, BorderLayout.CENTER);

        progress = new JSlider(0, 1000, 0);
        progress.setOpaque(false); 
        progress.setAlignmentX(CENTER_ALIGNMENT);
        add(progress, BorderLayout.SOUTH);

        playerBar = new PlayerBar();
        add(playerBar, BorderLayout.SOUTH);

        // Debounced Nimbus L&F reinstall — the only way to flush SynthStyleFactory
        // caches so all Nimbus-painted panels pick up updated colours.
        // Debouncing prevents stutter during slider drags.
        Timer themeRebuildTimer = new Timer(250, e -> rebuildTheme());
        themeRebuildTimer.setRepeats(false);
        ThemeManager.addChangeListener(themeRebuildTimer::restart);

        // Wire any already-registered MediaService
        MediaService ms = ServiceManager.getInstance().getService(MediaService.class);
        if (ms != null) wireMediaService(ms);

        ServiceManager.getInstance().activateFeatures(viewStack, sidebar.getRootNode());

        // Glass-pane resize handler — intercepts edge events, redispatches others
        WindowResizer.install(this);

        navigate("spacify:now-playing");
    }

    public void navigate(String uri) {
    	viewStack.navigate(uri);
    	/*if (uri.startsWith("spacify:now-playing")) {
    		sidebar.setVisible(false);
    	} else {
    		sidebar.setVisible(true);
    		leftSplit.setDividerLocation(100);
    	}*/
    }

    /**
     * Wire this bar to a MediaService.
     * Playback events update labels and progress; controls drive the service.
     */
    public void setMediaService(MediaService ms) {
 
        progress.addChangeListener(e -> {
            if (!updatingProgress && !progress.getValueIsAdjusting()) {
                long dur = ms.getDurationMs();
                if (dur > 0) ms.seek((long)(progress.getValue() / 1000.0 * dur));
            }
        });

        ms.addPlaybackListener(new MediaService.PlaybackListener() {
            @Override
            public void onStateChanged(PlaybackState state) {
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
            
                });
            }

            @Override
            public void onError(Exception e) {
              
            }
        });
    }

    private void rebuildTheme() {
        // 1. Reinstall Nimbus to clear its SynthStyleFactory painter cache
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
        // 2. Re-apply our colour overrides to the freshly-installed L&F defaults
        ThemeManager.applyToDefaults();
        // 3. Propagate to all components
        SwingUtilities.updateComponentTreeUI(this);
    }

    /** Connects a MediaService to PlayerBar and NowPlayingView. */
    public void wireMediaService(MediaService ms) {
        playerBar.setMediaService(ms);
        nowPlayingView.setMediaService(ms);
    }

    public SPViewStack    getViewStack()     { return viewStack; }
    public PlayerBar      getPlayerBar()     { return playerBar; }
    public NowPlayingView getNowPlayingView() { return nowPlayingView; }
    public Sidebar        getSidebar()       { return sidebar; }
}
