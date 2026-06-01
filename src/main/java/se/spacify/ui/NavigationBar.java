package se.spacify.ui;

import se.spacify.navigation.NavigationListener;
import se.spacify.navigation.SPViewStack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class NavigationBar extends JPanel implements NavigationListener {

    private final SPViewStack viewStack;
    private final JButton backBtn;
    private final JButton forwardBtn;
    private final JTextField uriField;
    private final JTextField searchField;

    public NavigationBar(SPViewStack viewStack) {
        this.viewStack = viewStack;
        setLayout(new BorderLayout(8, 0));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setBackground(new Color(24, 24, 24));
        setPreferredSize(new Dimension(0, 56));

        backBtn = makeNavButton("◄");
        forwardBtn = makeNavButton("►");
        backBtn.setEnabled(false);
        forwardBtn.setEnabled(false);

        backBtn.addActionListener((ActionEvent e) -> viewStack.back());
        forwardBtn.addActionListener((ActionEvent e) -> viewStack.forward());

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        navButtons.setOpaque(false);
        navButtons.add(backBtn);
        navButtons.add(forwardBtn);

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
        center.add(uriField);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(searchField);

        add(navButtons, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        viewStack.addNavigationListener(this);
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
