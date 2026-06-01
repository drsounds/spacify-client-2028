package se.spacify.navigation;

import java.util.ArrayList;
import java.util.List;

public class SidebarNode {

    private final String label;
    private final String uri;
    private final List<SidebarNode> children = new ArrayList<>();

    public SidebarNode(String label, String uri) {
        this.label = label;
        this.uri = uri;
    }

    public void addChild(SidebarNode child) {
        children.add(child);
    }

    public String getLabel() { return label; }
    public String getUri() { return uri; }
    public List<SidebarNode> getChildren() { return children; }
    public boolean isLeaf() { return children.isEmpty(); }

    @Override
    public String toString() { return label; }
}
