package se.spacify.ui;

import se.spacify.navigation.SPViewStack;
import se.spacify.ui.theme.ThemeManager;
import se.spacify.views.*;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private final SPViewStack viewStack;

    public MainWindow() {
        super("Spacify");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        viewStack = new SPViewStack();
        viewStack.registerView(new NowPlayingView());
        viewStack.registerView(new SearchView());
        viewStack.registerView(new LibraryView());
        viewStack.registerView(new PlaylistView());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(18, 18, 18));

        NavigationBar navBar = new NavigationBar(viewStack);
        add(navBar, BorderLayout.NORTH);

        Sidebar sidebar = new Sidebar(viewStack);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(30, 30, 30));
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
        add(new PlayerBar(), BorderLayout.SOUTH);

        ThemeManager.addChangeListener(() ->
            SwingUtilities.updateComponentTreeUI(this));

        viewStack.navigate("spacify:now-playing");
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(24, 24, 24));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(50, 50, 50)),
            BorderFactory.createEmptyBorder(16, 12, 16, 12)
        ));

        JLabel title = new JLabel("Now Playing");
        title.setForeground(new Color(160, 160, 160));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 11f));
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel placeholder = new JLabel("Nothing playing");
        placeholder.setForeground(new Color(100, 100, 100));
        placeholder.setFont(placeholder.getFont().deriveFont(12f));
        placeholder.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(placeholder);
        return panel;
    }
}
