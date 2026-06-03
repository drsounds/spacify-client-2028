package se.spacify.ui;

import se.spacify.ui.theme.ThemeManager;

import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * A WMP-style tab: rounded top corners, sharp bottom, meant to sit flush on the
 * bottom edge of the navigation bar. The selected (active) tab is filled with
 * the content background so it reads as the foremost tab; unselected tabs are
 * flat and show a subtle highlight on hover.
 */
public class TabButton extends JToggleButton {

	private static final long serialVersionUID = 1L;
	private static final int ARC = 12;

	public TabButton(String text) {
		super(text);
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFocusPainted(false);
		setOpaque(false);
		setRolloverEnabled(true);
		setBorder(BorderFactory.createEmptyBorder(10, 32, 8, 32));
		setFont(getFont().deriveFont(14f));
		setForeground(ThemeManager.getForeground());
		ThemeManager.addChangeListener(() -> {
			if (isSelected()) {
				setForeground(Color.WHITE);	
			} else {
				setForeground(ThemeManager.accentDark(0.2f));
			}
			repaint();
		});
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		return new Dimension(d.width, 50);
	}

	@Override
	public void setSelected(boolean b) {
		super.setSelected(b);
		if (b) {
			setForeground(Color.WHITE);	
		} else {
			setForeground(ThemeManager.accentDark(0.2f));
		}
		//setFont(getFont().deriveFont(b ? Font.BOLD : Font.PLAIN));
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		

		((MainWindow)(SwingUtilities.getWindowAncestor(this))).getSkin().paintTabButton(this, g2);
			
		g2.dispose();

		super.paintComponent(g);   // draws text/icon (content area is disabled)
	}
}
