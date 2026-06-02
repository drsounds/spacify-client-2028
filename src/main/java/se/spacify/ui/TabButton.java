package se.spacify.ui;

import se.spacify.ui.theme.ThemeManager;

import javax.swing.JToggleButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

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
		setBorder(BorderFactory.createEmptyBorder(6, 18, 4, 18));
		setFont(getFont().deriveFont(12f));
		setForeground(ThemeManager.getForeground());
		ThemeManager.addChangeListener(() -> {
			setForeground(ThemeManager.getForeground());
			repaint();
		});
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		return new Dimension(d.width, 30);
	}

	@Override
	public void setSelected(boolean b) {
		super.setSelected(b);
		setFont(getFont().deriveFont(b ? Font.BOLD : Font.PLAIN));
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int w = getWidth(), h = getHeight();

		// Top-rounded, bottom-sharp shape flush with the panel's bottom edge.
		Path2D tab = new Path2D.Float();
		tab.moveTo(0, h);
		tab.lineTo(0, ARC);
		tab.quadTo(0, 0, ARC, 0);
		tab.lineTo(w - ARC, 0);
		tab.quadTo(w, 0, w, ARC);
		tab.lineTo(w, h);
		tab.closePath();

		if (isSelected()) {
			g2.setColor(ThemeManager.getBackground());
			g2.fill(tab);
			g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 60),
					0, h / 2f, new Color(255, 255, 255, 0)));
			g2.fill(tab);
		} else if (getModel().isRollover()) {
			g2.setColor(new Color(255, 255, 255, 45));
			g2.fill(tab);
		}
		g2.dispose();

		super.paintComponent(g);   // draws text/icon (content area is disabled)
	}
}
