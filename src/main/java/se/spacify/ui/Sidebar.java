package se.spacify.ui;

import se.spacify.navigation.NavigationListener;
import se.spacify.navigation.SPViewStack;
import se.spacify.navigation.SidebarNode;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Sidebar extends JPanel implements NavigationListener {

    private final SPViewStack viewStack;
    private final JTree tree;
    private boolean suppressSelection = false;

    public Sidebar(SPViewStack viewStack) {
        this.viewStack = viewStack;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(220, 0));

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        root.add(nodeFor(new SidebarNode("Now Playing", "spacify:now-playing")));

        DefaultMutableTreeNode library = nodeFor(new SidebarNode("Your Library", "spacify:library"));
        library.add(nodeFor(new SidebarNode("Liked Songs", "spacify:library:liked")));
        library.add(nodeFor(new SidebarNode("Albums", "spacify:library:albums")));
        library.add(nodeFor(new SidebarNode("Artists", "spacify:library:artists")));
        root.add(library);

        DefaultMutableTreeNode playlists = nodeFor(new SidebarNode("Playlists", "spacify:library:playlists"));
        playlists.add(nodeFor(new SidebarNode("Chill Mix", "spacify:playlist:chill-mix")));
        playlists.add(nodeFor(new SidebarNode("Workout", "spacify:playlist:workout")));
        playlists.add(nodeFor(new SidebarNode("Late Night", "spacify:playlist:late-night")));
        root.add(playlists);

        tree = new JTree(root);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        tree.setRowHeight(28);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        tree.setCellRenderer(renderer);

        for (int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);

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

        JScrollPane scroll = new JScrollPane(tree);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        viewStack.addNavigationListener(this);
    }

    private DefaultMutableTreeNode nodeFor(SidebarNode sn) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(sn);
        for (SidebarNode child : sn.getChildren()) {
            node.add(nodeFor(child));
        }
        return node;
    }

    @Override
    public void onNavigate(String uri, boolean canGoBack, boolean canGoForward) {
        if (suppressSelection) return;
        // Select matching tree node when navigation happens externally
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
