package se.spacify.ui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;

import se.spacify.controls.Panel;
import se.spacify.controls.VerticalPanel;

public class LeftMenuPanel extends Panel {

	private static final long serialVersionUID = 4940799816568272634L;
	private Panel topSpacePanel;
	private VerticalPanel menuPanel;
	private JComponent bottomSpacePanel;
	public LeftMenuPanel() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setOpaque(false);
		topSpacePanel = new Panel();
		topSpacePanel.setOpaque(false);
		topSpacePanel.setMinimumSize(new Dimension(0, 2500));
		topSpacePanel.setMaximumSize(new Dimension(Short.MAX_VALUE,2500));
		add(topSpacePanel);
		menuPanel = new VerticalPanel();
		add(menuPanel);
		bottomSpacePanel = new Panel();
		bottomSpacePanel.setOpaque(false);
		bottomSpacePanel.setMinimumSize(new Dimension(0, 2500));
		bottomSpacePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 2500));
		add(bottomSpacePanel);
	}
	
}
