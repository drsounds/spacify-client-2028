package se.spacify.views;

import se.spacify.navigation.PlayerView;
import se.spacify.navigation.SPView;
import se.spacify.ui.SettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NowPlayingView extends SPView {

    private final JPanel panel;
    private final JPanel playerContainer;
    private final JComboBox<String> viewSelector;
    private final List<PlayerView> playerViews = new ArrayList<>();
    private PlayerView activeView;

    public NowPlayingView() {
        panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // ── Player area ──────────────────────────────────────────────────────
        playerContainer = new JPanel(new BorderLayout());
        playerContainer.setOpaque(false);

        // Selector bar shown only when more than one PlayerView is registered
        viewSelector = new JComboBox<>();
        viewSelector.setVisible(false);
        viewSelector.addActionListener(e -> {
            int idx = viewSelector.getSelectedIndex();
            if (idx >= 0 && idx < playerViews.size()) {
                activateView(playerViews.get(idx));
            }
        });

        JPanel selectorBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        selectorBar.setOpaque(false);
        selectorBar.add(new JLabel("View:"));
        selectorBar.add(viewSelector);

        JPanel playerWrapper = new JPanel(new BorderLayout());
        playerWrapper.setOpaque(false);
        playerWrapper.add(selectorBar,    BorderLayout.NORTH);
        playerWrapper.add(playerContainer, BorderLayout.CENTER);

        // ── Settings strip ───────────────────────────────────────────────────
        SettingsPanel settings = new SettingsPanel();

        panel.add(playerWrapper, BorderLayout.CENTER);
        panel.add(settings,      BorderLayout.SOUTH);

        // Default view
        addPlayerView(new DefaultPlayerView());
    }

    public void addPlayerView(PlayerView view) {
        playerViews.add(view);
        viewSelector.addItem(view.getName());
        viewSelector.setVisible(playerViews.size() > 1);
        if (activeView == null) activateView(view);
    }

    private void activateView(PlayerView view) {
        if (activeView != null) {
            activeView.onHide();
            playerContainer.remove(activeView.getComponent());
        }
        activeView = view;
        playerContainer.add(view.getComponent(), BorderLayout.CENTER);
        playerContainer.revalidate();
        playerContainer.repaint();
        view.onShow();
    }

    @Override
    public boolean acceptsUri(String uri) {
        return uri != null && uri.matches("spacify:(home|now-playing)");
    }

    @Override
    public void navigate(String uri) {}

    @Override
    public JComponent getComponent() { return panel; }

    @Override
    public String getTitle() { return "Now Playing"; }
}
