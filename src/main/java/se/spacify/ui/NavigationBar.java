package se.spacify.ui;

import se.spacify.navigation.NavigationListener;
import se.spacify.navigation.SPViewStack;
import se.spacify.ui.theme.ThemeManager;
import se.spacify.web.FaviconFetcher;
import se.spacify.web.StoreCatalog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NavigationBar extends JPanel implements NavigationListener {

    private static final Color CHROME_DARK  = new Color(14, 14, 14);
    private static final Color HIGHLIGHT    = new Color(255, 255, 255, 35);

    private final SPViewStack viewStack;
    private final JButton backBtn;
    private final JButton forwardBtn;
    private final JTextField uriField;
    private final JTextField searchField;
	private JButton nowPlayingTabButton;
	private JButton libraryTabButton;
    /** Cached store favicons, fetched off the EDT. */
    private final Map<String, Icon> faviconCache = new HashMap<>();

    public NavigationBar(SPViewStack viewStack) {
        this.viewStack = viewStack;
        setLayout(new BorderLayout(8, 0));
        setPreferredSize(new Dimension(0, 56));
        setOpaque(true);

        JButton sidebarToggle = makeNavButton("☰");
        sidebarToggle.setToolTipText("Show/hide the sidebar");
        sidebarToggle.addActionListener(e -> {
            if (viewStack.getMainWindow() != null) viewStack.getMainWindow().toggleSidebar();
        });

        backBtn = makeNavButton("◄");
        forwardBtn = makeNavButton("►");
        backBtn.setEnabled(false);
        forwardBtn.setEnabled(false);

        backBtn.addActionListener((ActionEvent e) -> viewStack.back());
        forwardBtn.addActionListener((ActionEvent e) -> viewStack.forward());

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        navButtons.setOpaque(false);
        navButtons.add(sidebarToggle);
        navButtons.add(backBtn);
        navButtons.add(forwardBtn);
        navButtons.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        uriField = new JTextField("spacify:home");
        uriField.setFont(uriField.getFont().deriveFont(12f));
        uriField.setPreferredSize(new Dimension(260, 28));
        uriField.addActionListener(e -> viewStack.navigate(uriField.getText().trim()));

        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search...");
        searchField.setPreferredSize(new Dimension(180, 28));
        searchField.addActionListener(e -> {
            String q = searchField.getText().trim();
            if (!q.isEmpty()) {
                String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
                viewStack.navigate("spacify:search?q=" + encoded);
            }
        });

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        center.setOpaque(false);
        navButtons.add(uriField);
        
        /*nowPlayingTabButton = makeNavButton("Now Playing");
        center.add(nowPlayingTabButton);
        nowPlayingTabButton.addActionListener(e -> {
        	viewStack.getMainWindow().navigate("spacify:now-playing");
        });
        libraryTabButton = makeNavButton("Library");
        libraryTabButton.addActionListener(e -> {
        	viewStack.getMainWindow().navigate("spacify:library");
        });
        center.add(libraryTabButton);
        */
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        right.setOpaque(false);
     
        //right.add(searchField);
        GlassPanel storePanel = new GlassPanel();
        storePanel.setPreferredSize(new Dimension(200, 56));
        storePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JButton storesBtn = makeNavButton("Stores ▾");
        storesBtn.setPreferredSize(new Dimension(120, 32));
        storesBtn.setToolTipText("Open a music service");
        storesBtn.addActionListener(e -> buildStoresMenu().show(storesBtn, 0, storesBtn.getHeight()));
        storePanel.add(storesBtn);

        add(navButtons, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        right.add(storePanel, BorderLayout.CENTER);

        loadFavicons();

        viewStack.addNavigationListener(this);
        ThemeManager.addChangeListener(this::repaint);
    }

    /** Build the stores popup from the catalogue, using cached favicons. */
    private JPopupMenu buildStoresMenu() {
        JPopupMenu menu = new JPopupMenu();
        for (StoreCatalog.Store store : StoreCatalog.STORES) {
            JMenuItem item = new JMenuItem(store.name(), faviconCache.get(store.host()));
            item.addActionListener(e -> {
                if (viewStack.getMainWindow() != null) viewStack.getMainWindow().navigate(store.uri());
                else viewStack.navigate(store.uri());
            });
            menu.add(item);
        }
        return menu;
    }

    /** Fetch each store's favicon off the EDT and cache it for the popup. */
    private void loadFavicons() {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                for (StoreCatalog.Store store : StoreCatalog.STORES) {
                    byte[] png = FaviconFetcher.fetch(store.host());
                    if (png != null) {
                        Icon icon = new ImageIcon(png);
                        SwingUtilities.invokeLater(() -> faviconCache.put(store.host(), icon));
                    }
                }
                return null;
            }
        }.execute();
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        
        Color tintColor = ThemeManager.getTintColor();
        g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, h, tintColor));
        g2.fillRect(0, 0, w, h);
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 10), 0, h, new Color(255, 255, 255, 0)));
        g2.fillRect(0, 1, w, (h / 2));
        // 1 px white sheen along the very bottom edge
        g2.setColor(HIGHLIGHT);
        g2.drawLine(0, h - 1, w, h - 1);

        g2.dispose();
    }

    private JButton makeNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setFont(btn.getFont().deriveFont(11f));
        return btn;
    }

    @Override
    public void onNavigate(String uri, boolean canGoBack, boolean canGoForward) {
        backBtn.setEnabled(canGoBack);
        forwardBtn.setEnabled(canGoForward);
        if (uri != null) uriField.setText(uri);
    }
}
