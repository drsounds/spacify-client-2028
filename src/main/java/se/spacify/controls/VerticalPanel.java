package se.spacify.controls;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import se.spacify.ui.MainWindow;

public class VerticalPanel extends Panel {

	private static final long serialVersionUID = -7149779098199783154L;
	private TabButton nowPlayingTab;
	private TabButton libraryTab;
	private TabButton mediaGuideTab;
	public VerticalPanel() {
		// WMP-style tab strip, flush with the bottom edge of the nav bar.
		nowPlayingTab = new TabButton("Now Playing");
		nowPlayingTab.addActionListener(e -> {
			MainWindow mw = getMainWindow().getViewStack().getMainWindow();
			if (mw != null) {
				mw.setSidebarVisible(false);
				mw.navigate("spacify:now-playing");
			}
		});
		libraryTab = new TabButton("Library");
		libraryTab.addActionListener(e -> {
			MainWindow mw = getMainWindow().getViewStack().getMainWindow();
			if (mw != null) {
				mw.setSidebarVisible(true);
				mw.navigate("spacify:library");
			}
		});
		mediaGuideTab = new TabButton("Media Guide");
		mediaGuideTab.addActionListener(e -> {
			MainWindow mw = getMainWindow().getViewStack().getMainWindow();
			if (mw != null) {
				mw.setSidebarVisible(true);
				mw.navigate("spacify:store:www.last.fm");
			}
		});
		JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
		tabBar.setOpaque(false);
		tabBar.add(nowPlayingTab);
		tabBar.add(libraryTab);
		tabBar.add(mediaGuideTab);
		add(tabBar);
	}
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g.create();
		getSkin().paintVerticalPanel(this, g2);
	}
}
