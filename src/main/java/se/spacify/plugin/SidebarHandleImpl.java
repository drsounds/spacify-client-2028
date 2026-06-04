package se.spacify.plugin;

import se.spacify.navigation.SidebarNode;
import se.spacify.ui.Sidebar;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * {@link SidebarHandle} backed by a live {@link DefaultMutableTreeNode} in the
 * {@link Sidebar}. Plugins only see the interface; this keeps the Swing tree
 * details on the host side.
 */
final class SidebarHandleImpl implements SidebarHandle {

    private final Sidebar sidebar;
    private final DefaultMutableTreeNode node;

    SidebarHandleImpl(Sidebar sidebar, DefaultMutableTreeNode node) {
        this.sidebar = sidebar;
        this.node = node;
    }

    /** The top-level tree node, used by the manager for teardown. */
    DefaultMutableTreeNode node() { return node; }

    @Override
    public SidebarHandle child(String uri) {
        DefaultMutableTreeNode found = find(node, uri);
        return found != null ? new SidebarHandleImpl(sidebar, found) : null;
    }

    @Override
    public void setChildren(List<SidebarNode> children) {
        sidebar.setNodeChildren(node, children);
    }

    @Override
    public void expand() {
        sidebar.expandNode(node);
    }

    private static DefaultMutableTreeNode find(DefaultMutableTreeNode n, String uri) {
        for (int i = 0; i < n.getChildCount(); i++) {
            DefaultMutableTreeNode c = (DefaultMutableTreeNode) n.getChildAt(i);
            if (c.getUserObject() instanceof SidebarNode sn && uri.equals(sn.getUri())) return c;
            DefaultMutableTreeNode deep = find(c, uri);
            if (deep != null) return deep;
        }
        return null;
    }
}
