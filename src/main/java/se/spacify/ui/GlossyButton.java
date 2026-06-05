package se.spacify.ui;

import se.spacify.ui.theme.ThemeManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * A circular, glossy push button in the Frutiger Aero / Aqua idiom of Windows
 * Media Player 10: a tinted radial face under a bright "bubble" sheen across the
 * top hemisphere, a faint reflection along the bottom, and a darker rim. Colours
 * are pulled from {@link ThemeManager} so the button tracks the active theme.
 *
 * <p>It extends {@link JButton}, so text, icon and {@code ActionListener}s work as
 * usual (the action fires on release). Hover and press feedback is tracked with an
 * explicit mouse listener rather than the button model, because the Synth-based
 * Nimbus L&amp;F doesn't reliably repaint a custom-painted, non-opaque button on
 * those transitions (same approach as {@link TabButton}).
 */
public class GlossyButton extends JButton {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_DIAMETER = 64;

    private int diameter = DEFAULT_DIAMETER;

    private boolean hovered;
    private boolean pressed;

    public boolean getHovered() {
    	return hovered;
    }
    public boolean getPressed() {
    	return pressed;
    }
    
    public GlossyButton() {
        this(null);
    }

    public GlossyButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setHorizontalAlignment(CENTER);
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());

        java.awt.event.MouseAdapter mouse = new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { hovered = false; pressed = false; repaint(); }
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) { pressed = true; repaint(); }
            }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { pressed = false; repaint(); }
        };
        addMouseListener(mouse);

        ThemeManager.addChangeListener(this::repaint);
    }

    /** Diameter of the circular face in pixels; also drives the preferred size. */
    public void setDiameter(int d) {
        this.diameter = Math.max(16, d);
        revalidate();
        repaint();
    }

    public int getDiameter() {
        return diameter;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(diameter, diameter);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Largest centred circle that fits, leaving 1px so the antialiased edge
        // isn't clipped.
        int d = Math.min(getWidth(), getHeight()) - 2;
        if (d > 0) {
            int x = (getWidth() - d) / 2;
            int y = (getHeight() - d) / 2;
            paintGlossy(g2, x, y, d);
        }
        g2.dispose();

        super.paintComponent(g);   // text / icon, drawn over the glossy face
    }

    /** Paint the aqua face: rim, tinted base, top bubble sheen, bottom reflection. */
    private void paintGlossy(Graphics2D g2, int x, int y, int d) {
		((MainWindow)(SwingUtilities.getWindowAncestor(this))).getSkin().paintGlossyButton(this, g2, x, y, d);

    }
}
