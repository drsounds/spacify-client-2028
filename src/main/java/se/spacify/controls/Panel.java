package se.spacify.controls;

import java.awt.LayoutManager2;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import se.spacify.skinning.Skin;
import se.spacify.ui.MainWindow;

public class Panel extends JPanel implements Control {
	private static final long serialVersionUID = 1L;
	
	public Panel() {
		super();
	}
	public Panel(LayoutManager2 layout) {
		super(layout);
	}

	public Skin getSkin() {
		return getMainWindow().getSkin();
	}
	public MainWindow getMainWindow() {
		return ((MainWindow)(SwingUtilities.getWindowAncestor(this)));
	}
}
