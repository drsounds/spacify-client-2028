package se.spacify.views.library;

import se.spacify.library.LibraryEvents;
import se.spacify.navigation.SPView;
import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Base for the data-backed library views. Provides a themed {@link JTable} with
 * an optional header and a CRUD toolbar (Add / Edit / Delete / Scan / Refresh).
 * Read-only views (see {@link #isEditable()}) keep just Refresh; mutating
 * actions broadcast via {@link LibraryEvents} so the sidebar stays in sync.
 */
public abstract class AbstractLibraryView extends SPView {

    protected final JPanel           panel;
    protected final JLabel           headerLabel;
    protected final JTable           table;
    protected final JScrollPane      scroll;
    protected final DefaultTableModel model;

    protected AbstractLibraryView() {
        panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        headerLabel = new JLabel();
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 18f));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        headerLabel.setVisible(false);

        // ── Table ────────────────────────────────────────────────────────────────
        model = new DefaultTableModel(getColumns(), 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) onActivate(row);
                }
            }
        });

        ThemedTableCellRenderer renderer = new ThemedTableCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        scroll = new JScrollPane(table);
        // Non-UIResource empty border so the Nimbus reinstall on theme change
        // doesn't re-install a default scroll-pane border.
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(true);
        scroll.getViewport().setOpaque(true);

        // ── CRUD toolbar ───────────────────────────────────────────────────────
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setOpaque(true);
        toolbar.setBackground(ThemeManager.getTintColor());
        

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> reload());

        if (isEditable()) {
            JButton addBtn    = new JButton("Add");
            JButton editBtn   = new JButton("Edit");
            JButton deleteBtn = new JButton("Delete");
            JButton scanBtn   = new JButton("Scan…");

            scanBtn.addActionListener(e -> LibraryScanAction.run(panel,
                () -> { reload(); LibraryEvents.fireChanged(); }));
            addBtn.addActionListener(e -> { onAdd(); reload(); LibraryEvents.fireChanged(); });
            editBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) { onEdit(row); reload(); LibraryEvents.fireChanged(); }
            });
            deleteBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) { onDelete(row); reload(); LibraryEvents.fireChanged(); }
            });

            toolbar.add(addBtn);
            toolbar.add(editBtn);
            toolbar.add(deleteBtn);
            toolbar.addSeparator();
            toolbar.add(scanBtn);
        }
        toolbar.add(refreshBtn);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(toolbar, BorderLayout.CENTER);
        panel.add(headerLabel, BorderLayout.NORTH);

        panel.add(north, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        updateColors();
        ThemeManager.addChangeListener(this::updateColors);
    }

    /** Sets the page header text; pass null/blank to hide it. */
    protected void setHeader(String text) {
        headerLabel.setText(text == null ? "" : text);
        headerLabel.setVisible(text != null && !text.isBlank());
    }

    /** Whether this view shows Add/Edit/Delete/Scan; read-only views return false. */
    protected boolean isEditable() { return true; }

    // ── Hooks for subclasses ────────────────────────────────────────────────────

    /** Column headers; called once during construction (must be constant). */
    protected abstract String[] getColumns();

    /** Clear and refill {@link #model} from the database. */
    protected abstract void reload();

    protected void onAdd() {}
    protected void onEdit(int row) {}
    protected void onDelete(int row) {}

    /** Invoked when a row is double-clicked; default does nothing. */
    protected void onActivate(int row) {}

    // ── Shared helpers ──────────────────────────────────────────────────────────

    protected static String fmtDuration(long ms) {
        if (ms <= 0) return "";
        long s = ms / 1000;
        return String.format("%d:%02d", s / 60, s % 60);
    }

    /** Parse "m:ss" or a plain seconds value into milliseconds; 0 on failure. */
    protected static long parseDuration(String text) {
        if (text == null) return 0;
        String t = text.trim();
        if (t.isEmpty()) return 0;
        try {
            if (t.contains(":")) {
                String[] p = t.split(":");
                return (Long.parseLong(p[0].trim()) * 60 + Long.parseLong(p[1].trim())) * 1000;
            }
            return (long) (Double.parseDouble(t) * 1000);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    protected void showError(Exception e) {
        JOptionPane.showMessageDialog(panel, e.getMessage(), "Library error",
            JOptionPane.ERROR_MESSAGE);
    }

    protected boolean confirmDelete(String what) {
        return JOptionPane.showConfirmDialog(panel, "Delete " + what + "?", "Confirm delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void updateColors() {
        Color bg   = ThemeManager.getBackground();
        Color fg   = ThemeManager.getForeground();
        Color grid = ThemeManager.getGridColor();

        headerLabel.setForeground(fg);
        table.setBackground(bg);
        table.setForeground(fg);
        table.setGridColor(grid);
        table.getTableHeader().setBackground(grid);
        table.getTableHeader().setForeground(fg);
        scroll.setBackground(bg);
        scroll.getViewport().setBackground(bg);
        table.repaint();
    }

    private static final class ThemedTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                setBackground(ThemeManager.getAccentColor());
                setForeground(Color.WHITE);
            } else {
                setBackground(row % 2 == 0
                    ? ThemeManager.getBackground()
                    : ThemeManager.getAlternateBackground());
                setForeground(ThemeManager.getForeground());
            }
            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            return this;
        }
    }

    // ── SPView ──────────────────────────────────────────────────────────────────

    @Override public void navigate(String uri) {}
    @Override public JComponent getComponent() { return panel; }
    @Override public void onShow() { reload(); }
}
