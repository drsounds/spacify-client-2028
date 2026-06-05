package se.spacify.ui;

import se.spacify.navigation.SPViewStack;
import se.spacify.service.media.PlayQueue;
import se.spacify.service.media.PlayQueueItem;
import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Right-hand play-queue panel. Mirrors the library table styling (theme colours,
 * striped rows, accent selection) but lists the active {@link PlayQueue} with
 * Name / Artists / Duration columns. The currently-playing entry is highlighted,
 * and double-clicking a row jumps playback to that position.
 */
public class NowPlayingPanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable            table;
    private final JScrollPane       scroll;
    private final JLabel            emptyLabel;
	private JToolBar topToolbar;
	private JToolBar bottomToolbar;

    public NowPlayingPanel(SPViewStack viewStack) {
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(220, 0));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        
        topToolbar = new ToolBar();
        topToolbar.setFloatable(false);
        topToolbar.setOpaque(true);
        topToolbar.setBackground(ThemeManager.getTintColor());
        add(topToolbar);

        JButton title = new JButton(UIManager.getIcon("FileView.fileIcon"));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 11f));
        
        // ── Queue table ──────────────────────────────────────────────────────
        model = new DefaultTableModel(new String[]{"Name", "Duration"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(1).setMaxWidth(64);
        // Transparent so the panel's gradient shows through behind the rows.
        table.setOpaque(false);

        QueueCellRenderer renderer = new QueueCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) PlayQueue.getInstance().playAt(row);
                }
            }
        });

        scroll = new JScrollPane(table);
        // Non-UIResource empty border so the Nimbus reinstall on theme change
        // doesn't re-install a default scroll-pane border.
        scroll.setBorder(BorderFactory.createEmptyBorder());
        // Transparent viewport/scroll-pane so the gradient shows behind the rows.
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        // Repaint the whole viewport on scroll so the gradient doesn't smear.
        scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        emptyLabel = new JLabel("Nothing playing");
        emptyLabel.setForeground(new Color(100, 100, 100));
        emptyLabel.setFont(emptyLabel.getFont().deriveFont(12f));
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(topToolbar,  BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        bottomToolbar = new ToolBar();
        bottomToolbar.setFloatable(false);
        bottomToolbar.setOpaque(true);
        bottomToolbar.setBackground(ThemeManager.getTintColor());
        bottomToolbar.add(new JButton("Sync"));
        add(bottomToolbar, BorderLayout.SOUTH);

        topToolbar.add(title);
        
        updateColors();
        refresh();

        PlayQueue.getInstance().addChangeListener(() ->
            SwingUtilities.invokeLater(this::refresh));
        ThemeManager.addChangeListener(() -> { updateColors(); refresh(); });
    }

    /** Rebuild the table rows from the current queue and keep the selection on the playing row. */
    private void refresh() {
        List<PlayQueueItem> items = PlayQueue.getInstance().getItems();
        model.setRowCount(0);
        for (PlayQueueItem it : items) {
            model.addRow(new Object[]{
                it.getName(), fmtDuration(it.getDurationMs())
            });
        }
        int current = PlayQueue.getInstance().getCurrentIndex();
        if (current >= 0 && current < model.getRowCount()) {
            table.setRowSelectionInterval(current, current);
        } else {
            table.clearSelection();
        }

        boolean empty = items.isEmpty();
        // Show the placeholder in the centre while the queue is empty.
        if (empty && scroll.getParent() == this) {
            remove(scroll);
            add(emptyLabel, BorderLayout.CENTER);
        } else if (!empty && emptyLabel.getParent() == this) {
            remove(emptyLabel);
            add(scroll, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    private void updateColors() {
        Color fg   = ThemeManager.getForeground();
        Color grid = ThemeManager.getGridColor();
        table.setForeground(fg);
        table.setGridColor(grid);
        table.getTableHeader().setBackground(grid);
        table.getTableHeader().setForeground(fg);
        table.repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setOpaque(false);   // we paint our own gradient in paintComponent
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ((MainWindow)(SwingUtilities.getWindowAncestor(this))).getSkin().paintPlaylist(this, g2);
        
        g2.dispose();
    }

    private static String fmtDuration(long ms) {
        if (ms <= 0) return "";
        long s = ms / 1000;
        return String.format("%d:%02d", s / 60, s % 60);
    }

    /**
     * Theme-aware renderer that keeps the gradient visible: the playing row is
     * filled with the accent colour, alternate rows get a subtle translucent
     * stripe (when striping is on), and every other row stays transparent.
     */
    private static final class QueueCellRenderer extends DefaultTableCellRenderer {
        private boolean stripe;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            boolean playing = row == PlayQueue.getInstance().getCurrentIndex();
            stripe = false;
            if (playing) {
                setOpaque(true);
                setBackground(ThemeManager.getNowPlayingBackground());
                setForeground(ThemeManager.getNowPlayingForeground());
            } else {
                setOpaque(false);   // let the gradient show through
                setForeground(ThemeManager.getForeground());
                stripe = ThemeManager.isStripedRows() && (row % 2 == 1);
            }
            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (stripe) {
                g.setColor(STRIPE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paintComponent(g);
        }

        private static final Color STRIPE = new Color(0, 0, 0, 38);
    }
}
