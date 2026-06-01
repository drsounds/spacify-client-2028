package se.spacify.views;

import se.spacify.navigation.SPView;

import javax.swing.*;
import java.awt.*;

public class LibraryView extends SPView {

    private final JPanel panel;

    public LibraryView() {
        panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        
        JLabel title = new JLabel("Your Library");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setForeground(Color.WHITE);
        //panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Title", "Artist", "Album"};
        Object[][] data = {
            {"Midnight City", "M83", "Hurry Up, We're Dreaming"},
            {"Breathe", "Pink Floyd", "Dark Side of the Moon"},
            {"Teardrop", "Massive Attack", "Mezzanine"},
            {"Pyramid Song", "Radiohead", "Amnesiac"},
            {"Dissolved Girl", "Massive Attack", "Mezzanine"},
        };
        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
    }

    @Override
    public boolean acceptsUri(String uri) {
        return uri != null && uri.matches("spacify:library.*");
    }

    @Override
    public void navigate(String uri) {}

    @Override
    public JComponent getComponent() { return panel; }

    @Override
    public String getTitle() { return "Your Library"; }
}
