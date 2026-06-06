package se.spacify.controls;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import se.spacify.skinning.Skin;
import se.spacify.ui.MainWindow;
import se.spacify.ui.theme.ThemeManager;

public class ToolBar extends JToolBar {

	private static final long serialVersionUID = -3117479158547825878L;

	public MainWindow getMainWindow() {
		return ((MainWindow)(SwingUtilities.getWindowAncestor(this)));
	}

	public Skin getSkin() {
		return getMainWindow().getSkin();
	}
	public ToolBar() {
		super();
		setFloatable(false);
		setOpaque(true);
		setBackground(ThemeManager.getTintColor());
	}
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		getSkin().paintToolBar(this, g2);
		g2.dispose();
	}

}
