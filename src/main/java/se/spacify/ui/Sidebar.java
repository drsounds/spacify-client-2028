package se.spacify.ui;

import se.spacify.db.DatabaseManager;
import se.spacify.db.entity.Artist;
import se.spacify.db.entity.Release;
import se.spacify.library.LibraryEvents;
import se.spacify.navigation.NavigationListener;
import se.spacify.navigation.SPViewStack;
import se.spacify.navigation.SidebarNode;
import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Sidebar extends JPanel implements NavigationListener {

    private final SPViewStack viewStack;
    private final JTree       tree;
    private final JScrollPane scroll;
    private final DefaultMutableTreeNode root;
    private final DefaultMutableTreeNode releasesNode;
    private final DefaultMutableTreeNode artistsNode;
    private boolean suppressSelection = false;
	private JToolBar toolbar;
	private JTextField searchField;

    public Sidebar(SPViewStack viewStack) {
        this.viewStack = viewStack;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(220, 0));
        setOpaque(true);

        root = new DefaultMutableTreeNode("root");

        DefaultMutableTreeNode library = nodeFor(new SidebarNode("Your Library", "spacify:library"));
        root.add(nodeFor(new SidebarNode("Player",     "spacify:now-playing")));
        
        library.add(nodeFor(new SidebarNode("Tracks",     "spacify:library:tracks")));
        library.add(nodeFor(new SidebarNode("Recordings", "spacify:library:recordings")));
        releasesNode = nodeFor(new SidebarNode("Releases", "spacify:library:releases"));
        artistsNode  = nodeFor(new SidebarNode("Artists",  "spacify:library:artists"));
        library.add(releasesNode);
        library.add(artistsNode);
        library.add(nodeFor(new SidebarNode("Local Files", "spacify:library:local")));
        root.add(library);
        DefaultMutableTreeNode downloads = nodeFor(new SidebarNode("Downloads", "spacify:downloads"));
        root.add(downloads);
         DefaultMutableTreeNode sites = nodeFor(new SidebarNode("Sites", "spacify:"));
        root.add(sites);
        populateReleases();
        populateArtists();

        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setOpaque(true);
        toolbar.setBackground(ThemeManager.getTintColor());
        add(toolbar, BorderLayout.NORTH);
        

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
        toolbar.add(searchField);


        tree = new JTree(root);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        tree.setRowHeight(28);
        tree.setOpaque(true);
        tree.setCellRenderer(new ThemedTreeCellRenderer());

        // Expand the top-level library node, but leave the (potentially long)
        // Releases/Artists lists collapsed until the user opens them.
        tree.expandPath(new TreePath(library.getPath()));

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tree.getRowForLocation(e.getX(), e.getY());
                if (row < 0) return;
                TreePath path = tree.getPathForRow(row);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.getUserObject() instanceof SidebarNode sn && sn.getUri() != null) {
                    suppressSelection = true;
                    viewStack.navigate(sn.getUri());
                    suppressSelection = false;
                }
            }
        });

        scroll = new JScrollPane(tree);
        scroll.setBorder(null);
        // Keep scroll and viewport opaque so they paint a background;
        // colours are kept in sync by updateColors() below.
        scroll.setOpaque(true);
        scroll.getViewport().setOpaque(true);
        add(scroll, BorderLayout.CENTER);

        viewStack.addNavigationListener(this);

        updateColors();
        ThemeManager.addChangeListener(this::updateColors);
        LibraryEvents.addListener(this::refreshLibrary);
    }

    // ── Dynamic library subtrees (albums & artists) ───────────────────────────

    /** Repopulate the Releases and Artists subtrees from the database. */
    private void refreshLibrary() {
        populateReleases();
        populateArtists();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.nodeStructureChanged(releasesNode);
        model.nodeStructureChanged(artistsNode);
    }

    private void populateReleases() {
        releasesNode.removeAllChildren();
        try {
            for (Release r : DatabaseManager.getInstance().releaseDao().queryForAll()) {
                releasesNode.add(nodeFor(new SidebarNode(
                    r.getTitle(), "spacify:library:release:" + r.getId())));
            }
        } catch (Exception ignored) {}
    }

    private void populateArtists() {
        artistsNode.removeAllChildren();
        try {
            for (Artist a : DatabaseManager.getInstance().artistDao().queryForAll()) {
                artistsNode.add(nodeFor(new SidebarNode(
                    a.getName(), "spacify:library:artist:" + a.getId())));
            }
        } catch (Exception ignored) {}
    }

    // ── Theme-aware colour sync ───────────────────────────────────────────────

    private void updateColors() {
        Color bg = ThemeManager.getBackground();
        setBackground(bg);
        tree.setBackground(bg);
        scroll.setBackground(bg);
        scroll.getViewport().setBackground(bg);
        tree.repaint();
    }

    // ── Custom cell renderer ──────────────────────────────────────────────────

    private static final class ThemedTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            setOpaque(true);
            if (sel) {
                setBackground(ThemeManager.getAccentColor());
                setForeground(Color.WHITE);
            } else {
                setBackground(ThemeManager.getBackground());
                setForeground(ThemeManager.getForeground());
            }
            setBorderSelectionColor(ThemeManager.getAccentColor());
            return this;
        }
    }

    // ── Sidebar tree model ────────────────────────────────────────────────────

    private DefaultMutableTreeNode nodeFor(SidebarNode sn) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(sn);
        for (SidebarNode child : sn.getChildren()) node.add(nodeFor(child));
        return node;
    }

    public DefaultMutableTreeNode getRootNode() { return root; }

    // ── NavigationListener ────────────────────────────────────────────────────

    @Override
    public void onNavigate(String uri, boolean canGoBack, boolean canGoForward) {
        if (suppressSelection) return;
        selectNodeForUri(uri, (DefaultMutableTreeNode) tree.getModel().getRoot());
    }

    private boolean selectNodeForUri(String uri, DefaultMutableTreeNode node) {
        if (node.getUserObject() instanceof SidebarNode sn && uri.equals(sn.getUri())) {
            TreePath path = new TreePath(node.getPath());
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
            return true;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            if (selectNodeForUri(uri, (DefaultMutableTreeNode) node.getChildAt(i))) return true;
        }
        return false;
    }
}
