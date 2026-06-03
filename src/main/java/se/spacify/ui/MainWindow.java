package se.spacify.ui;

import se.spacify.navigation.SPViewStack;
import se.spacify.service.ServiceManager;
import se.spacify.service.media.MediaService;
import se.spacify.service.media.MediaService.PlaybackState;
import se.spacify.skinning.Skin;
import se.spacify.skinning.WMP8Skin;
import se.spacify.skinning.WMP9Skin;
import se.spacify.skinning.WMP10Skin;
import se.spacify.skinning.WMP11BetaSkin;
import se.spacify.skinning.WMP11Skin;
import se.spacify.ui.theme.ThemeManager;
import se.spacify.views.*;
import se.spacify.views.library.*;
import se.spacify.views.web.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 2144395787232553079L;

	private static final String LAYOUT_WMP10 = "LAYOUT_WMP10";
	private static final String LAYOUT_WMP11 = "LAYOUT_WMP11";
	private static final String LAYOUT_WMP9 = "LAYOUT_WMP9";
	private static final String LAYOUT_MODE_SPOTIFY = "LAYOUT_SPOTIFY";
	private static final String LAYOUT_MODE_ITUNES = "LAYOUT_ITUNES";

	private final SPViewStack    viewStack;
    private final PlayerBar      playerBar;
    private final NowPlayingView nowPlayingView;
    private final Sidebar        sidebar;
    
    private String layoutMode = LAYOUT_WMP10;
    
    private Skin skin = skinForStyle(ThemeManager.getDesignStyle());
    public Skin getSkin() {
    	return skin;
    }

    /** Maps a {@link ThemeManager} design-style constant to its Skin implementation. */
    private static Skin skinForStyle(String style) {
        if (style == null) return new WMP11Skin();
        switch (style) {
            case ThemeManager.DESIGN_STYLE_WMP8:       return new WMP8Skin();
            case ThemeManager.DESIGN_STYLE_WMP9:       return new WMP9Skin();
            case ThemeManager.DESIGN_STYLE_WMP10:      return new WMP10Skin();
            case ThemeManager.DESIGN_STYLE_WMP11_BETA: return new WMP11BetaSkin();
            case ThemeManager.DESIGN_STYLE_WMP11:      return new WMP11Skin();
            default:                                   return new WMP11Skin();
        }
    }
    
    
    public String getLayoutMode() {
    	return layoutMode;
    }
    

	private JSplitPane leftSplit;
	private JSplitPane mainSplit;
    private boolean userWantsSidebar = true;  // user's manual show/hide preference
    private boolean immersive = false;        // full-width store browsing

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
        viewStack.registerView(new SPServiceWebView(viewStack));
        viewStack.registerView(new PlaylistView());

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        
        TopBar topBar = new TopBar();
        topBar.add(new JButton());
        add(topBar);
        topBar.setMaximumSize(new Dimension(Short.MAX_VALUE, 18));
        topBar.setMinimumSize(new Dimension(0, 18));
        NavigationBar navBar = new NavigationBar(viewStack);
        navBar.setMaximumSize(new Dimension(Short.MAX_VALUE, 28));
        navBar.setMinimumSize(new Dimension(0, 28));
        add(navBar);

        sidebar = new Sidebar(viewStack);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(viewStack);

        leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, centerPanel);
        leftSplit.setDividerLocation(220);
        leftSplit.setDividerSize(1);
        // Empty (non-UIResource) border survives the Nimbus reinstall in
        // rebuildTheme(); a null border would get a default border re-installed.
        leftSplit.setBorder(BorderFactory.createEmptyBorder());
        leftSplit.setContinuousLayout(true);

        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, new NowPlayingPanel(viewStack));
        mainSplit.setDividerLocation(880);
        mainSplit.setDividerSize(1);
        mainSplit.setBorder(BorderFactory.createEmptyBorder());
        mainSplit.setContinuousLayout(true);

        add(mainSplit, BorderLayout.CENTER);

        playerBar = new PlayerBar();
        add(playerBar);
        playerBar.setMaximumSize(new Dimension(Short.MAX_VALUE, 18));
        playerBar.setMinimumSize(new Dimension(0, 18));

        // Debounced Nimbus L&F reinstall — the only way to flush SynthStyleFactory
        // caches so all Nimbus-painted panels pick up updated colours.
        // Debouncing prevents stutter during slider drags.
        Timer themeRebuildTimer = new Timer(250, e -> rebuildTheme());
        themeRebuildTimer.setRepeats(false);
        ThemeManager.addChangeListener(themeRebuildTimer::restart);

        // Swap the active Skin when the selected design style changes.
        ThemeManager.addChangeListener(() -> {
            Skin desired = skinForStyle(ThemeManager.getDesignStyle());
            if (desired.getClass() != skin.getClass()) {
                skin = desired;
                repaint();
            }
        });

        // Wire any already-registered MediaService
        MediaService ms = ServiceManager.getInstance().getService(MediaService.class);
        if (ms != null) wireMediaService(ms);

        ServiceManager.getInstance().activateFeatures(viewStack, sidebar.getRootNode());
        applySidebar(false);
        // Glass-pane resize handler — intercepts edge events, redispatches others
        WindowResizer.install(this);

        // Store pages browse full-width with the side panels collapsed.
        viewStack.addNavigationListener((uri, b, f) ->
            applyImmersive(uri != null && uri.startsWith("spacify:store:")));

        navigate("spacify:now-playing");
    }

    /** Toggle the left sidebar; remembers the user's preference. */
    public void toggleSidebar() {
        setSidebarVisible(!userWantsSidebar);
    }

    /** Explicitly show/hide the left sidebar; remembers the user's preference. */
    public void setSidebarVisible(boolean visible) {
        userWantsSidebar = visible;
        if (!immersive) applySidebar(visible);
    }

    private void applySidebar(boolean visible) {
        sidebar.setVisible(visible);
        leftSplit.setDividerLocation(visible ? 220 : 0);
        leftSplit.revalidate();
        leftSplit.repaint();
    }

    /** Full-width browsing: collapse both side panels for store views. */
    private void applyImmersive(boolean on) {
        if (on == immersive) return;
        immersive = on;
        if (on) {
            applySidebar(false);
            mainSplit.setDividerLocation(1.0);   // collapse the right Now Playing panel
        } else {
            applySidebar(userWantsSidebar);
            mainSplit.setDividerLocation(880);
        }
        mainSplit.revalidate();
        mainSplit.repaint();
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
