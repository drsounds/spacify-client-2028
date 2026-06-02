package se.spacify.ui;

import se.spacify.navigation.NavigationListener;
import se.spacify.navigation.SPViewStack;
import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class NavigationBar extends JPanel implements NavigationListener {

    private static final Color HIGHLIGHT = new Color(255, 255, 255, 35);

    private final SPViewStack viewStack;
    private final JButton     backBtn;
    private final JButton     forwardBtn;
    private final JTextField  uriField;
    private final JTextField  searchField;

    public NavigationBar(SPViewStack viewStack) {
        this.viewStack = viewStack;
        setLayout(new BorderLayout(8, 0));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setPreferredSize(new Dimension(0, 56));
        setOpaque(true);

        // ── Nav buttons ───────────────────────────────────────────────────────
        backBtn    = makeNavButton("◄");
        forwardBtn = makeNavButton("►");
        backBtn.setEnabled(false);
        forwardBtn.setEnabled(false);
        backBtn.addActionListener(e    -> viewStack.back());
        forwardBtn.addActionListener(e -> viewStack.forward());

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        navButtons.setOpaque(false);
        navButtons.add(backBtn);
        navButtons.add(forwardBtn);

        // ── URI field ─────────────────────────────────────────────────────────
        uriField = new JTextField("spacify:home");
        uriField.setFont(uriField.getFont().deriveFont(12f));
        uriField.setPreferredSize(new Dimension(260, 28));
        uriField.addActionListener(e -> viewStack.navigate(uriField.getText().trim()));

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        center.setOpaque(false);
        center.add(uriField);

        // ── Search + window controls ──────────────────────────────────────────
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search...");
        searchField.setPreferredSize(new Dimension(160, 28));
        searchField.addActionListener(e -> {
            String q = searchField.getText().trim();
            if (!q.isEmpty()) {
                String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
                viewStack.navigate("spacify:search?q=" + encoded);
            }
        });

        JPanel winControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        winControls.setOpaque(false);
        winControls.add(makeWinBtn(new Color(255, 189, 68),  "─", () -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof JFrame f) f.setExtendedState(JFrame.ICONIFIED);
        }));
        winControls.add(makeWinBtn(new Color(39, 201, 63), "□", () -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof JFrame f) {
                int s = f.getExtendedState();
                f.setExtendedState((s & JFrame.MAXIMIZED_BOTH) != 0 ? JFrame.NORMAL : JFrame.MAXIMIZED_BOTH);
            }
        }));
        winControls.add(makeWinBtn(new Color(255, 96, 92),   "✕", () -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
        }));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.add(searchField);
        right.add(winControls);

        add(navButtons,  BorderLayout.WEST);
        add(center,      BorderLayout.CENTER);
        add(right,       BorderLayout.EAST);

        viewStack.addNavigationListener(this);
        ThemeManager.addChangeListener(this::repaint);

        // Install drag-to-move on all non-interactive sub-components
        WindowMover.install(this);
    }

    // ── Painting ──────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();

        Color tint = ThemeManager.getTintColor();
        g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, h, tint));
        g2.fillRect(0, 0, w, h);

        // Subtle gloss highlight on the upper half
        g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,90), 0, h/2, new Color(255,255,255,0)));
        g2.fillRect(0, 1, w, h / 2);

        // 1px sheen at bottom
        g2.setColor(HIGHLIGHT);
        g2.drawLine(0, h - 1, w, h - 1);
        g2.dispose();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JButton makeNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setFont(btn.getFont().deriveFont(11f));
        return btn;
    }

    /** macOS-style circular window control button. */
    private static JButton makeWinBtn(Color activeColor, String symbol, Runnable action) {
        JButton btn = new JButton() {
            boolean hovered;
            {
                setPreferredSize(new Dimension(14, 14));
                setFocusPainted(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
                addActionListener(e -> action.run());
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight());
                g2.setColor(hovered ? activeColor : new Color(90, 90, 90, 200));
                g2.fillOval(0, 0, d, d);
                if (hovered) {
                    g2.setColor(new Color(0, 0, 0, 120));
                    g2.setFont(new Font("Dialog", Font.BOLD, 7));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(symbol,
                        (d - fm.stringWidth(symbol)) / 2,
                        (d + fm.getAscent() - fm.getDescent()) / 2);
                }
                g2.dispose();
            }
        };
        return btn;
    }

    // ── NavigationListener ────────────────────────────────────────────────────

    @Override
    public void onNavigate(String uri, boolean canGoBack, boolean canGoForward) {
        backBtn.setEnabled(canGoBack);
        forwardBtn.setEnabled(canGoForward);
        if (uri != null) uriField.setText(uri);
    }
}
