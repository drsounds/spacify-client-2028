package se.spacify.controls;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

public class ToolButton extends Button { 
	private static final long serialVersionUID = 1L;
	public ToolButton() {
		super();
	}
	public ToolButton(String text) {
		super(text);
	}
	public ToolButton(Icon icon, String text) {
		super(icon, text);
	}
	public ToolButton(Icon icon) {
		super(icon);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g.create();
		
		getSkin().paintToolButton(this, g2);

		g2.dispose();

		super.paintComponent(g2);
	}
	
}
