package se.spacify.controls;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import se.spacify.ui.theme.ThemeManager;

public class SplitPane extends JSplitPane {

	private static final long serialVersionUID = -5587739050405239941L;
	public SplitPane() {
		
		
	}
	public SplitPane(int horizontalSplit, JComponent leftSplit, JComponent c) {
		super(horizontalSplit, leftSplit, c);
		// TODO Auto-generated constructor stub
	}
	@Override
	public Color getBackground() {
		if (ThemeManager.isDarkMode()) {
			return new Color(235, 234, 219);
		} else {
			return ThemeManager.getTintColor();
		}
	}
}
