package se.spacify.ui;

import se.spacify.navigation.SPViewStack;
import se.spacify.service.ServiceManager;
import se.spacify.service.media.MediaService;
import se.spacify.service.media.MediaService.PlaybackState;
import se.spacify.ui.theme.ThemeManager;
import se.spacify.views.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class MainWindow extends JFrame {

    private final SPViewStack    viewStack;
    private final PlayerBar      playerBar;
    private final NowPlayingView nowPlayingView;
    private final Sidebar        sidebar;
    private final JSlider progress;
    private boolean updatingProgress = false;  // guard against slider feedback loop

    public MainWindow() {
        super("Spacify");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        viewStack      = new SPViewStack();
        nowPlayingView = new NowPlayingView();
        viewStack.registerView(nowPlayingView);
        viewStack.registerView(new SearchView());
        viewStack.registerView(new LibraryView());
        viewStack.registerView(new PlaylistView());

        getContentPane().setLayout(new BorderLayout());

        NavigationBar navBar = new NavigationBar(viewStack);
        add(navBar, BorderLayout.NORTH);

        sidebar = new Sidebar(viewStack);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(viewStack, BorderLayout.CENTER);

        JPanel rightPanel = buildRightPanel();

        JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, centerPanel);
        leftSplit.setDividerLocation(220);
        leftSplit.setDividerSize(1);
        leftSplit.setBorder(null);
        leftSplit.setContinuousLayout(true);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, rightPanel);
        mainSplit.setDividerLocation(880);
        mainSplit.setDividerSize(1);
        mainSplit.setBorder(null);
        mainSplit.setContinuousLayout(true);

        add(mainSplit, BorderLayout.CENTER);

        playerBar = new PlayerBar();
        progress = new JSlider(0, 1000, 0);
        progress.setOpaque(false);
        progress.setMaximumSize(new Dimension(400, 20));
        progress.setAlignmentX(CENTER_ALIGNMENT);
        add(progress, BorderLayout.SOUTH);
        add(playerBar, BorderLayout.SOUTH);

        ThemeManager.addChangeListener(() ->
            SwingUtilities.invokeLater(() -> SwingUtilities.updateComponentTreeUI(this)));

        // Wire any already-registered MediaService
        MediaService ms = ServiceManager.getInstance().getService(MediaService.class);
        if (ms != null) wireMediaService(ms);

        // Let registered Features add their views and sidebar nodes
        ServiceManager.getInstance().activateFeatures(viewStack, sidebar.getRootNode());

        viewStack.navigate("spacify:now-playing");
    }

    /**
     * Wire this bar to a MediaService.
     * Playback events update labels and progress; controls drive the service.
     */
    public void setMediaService(MediaService ms) {
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
             
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    /** Connects a MediaService to PlayerBar and NowPlayingView. */
    public void wireMediaService(MediaService ms) {
        playerBar.setMediaService(ms);
        nowPlayingView.setMediaService(ms);
    }

    public SPViewStack    getViewStack()      { return viewStack; }
    public PlayerBar      getPlayerBar()      { return playerBar; }
    public NowPlayingView getNowPlayingView()  { return nowPlayingView; }
    public Sidebar        getSidebar()        { return sidebar; }

    private JPanel buildRightPanel() {
        JPanel panel = new NowPlayingPanel(viewStack);
        
        return panel;
    }
}
