package se.spacify.controls;

import se.spacify.skinning.Skin;
import se.spacify.ui.MainWindow;

public interface Control {
	public Skin getSkin();
	public MainWindow getMainWindow();
}
