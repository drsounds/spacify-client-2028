package se.spacify.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

public class ToolBar extends JToolBar {

	private static final long serialVersionUID = -3117479158547825878L;
	

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		((MainWindow)(SwingUtilities.getWindowAncestor(this))).getSkin().paintToolBar(this, g2);
		g2.dispose();
	}

}
