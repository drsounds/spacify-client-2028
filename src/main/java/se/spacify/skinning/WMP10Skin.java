package se.spacify.skinning;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JPanel;

import se.spacify.ui.TabButton;
import se.spacify.ui.theme.ThemeManager;

public class WMP10Skin extends Skin {

	private static final int ARC = 12;
	@Override
	public void paintTopBar(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
		g2.setPaint(new GradientPaint(0, 0, ThemeManager.getTintColor(), 0, h, Color.WHITE));
		g2.fillRect(0, 0, w, h);
	}
	@Override
	public void paintHeader(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
		g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h, ThemeManager.getTintColor()));
		g2.fillRect(0, 0, w, h);
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 127), 0, h, new Color(255, 255, 255, 0)));
        g2.fillRect(0, 1, w, (h / 2));
	}
	@Override
	public void paintFooter(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
       
		g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h, ThemeManager.getTintColor()));
        g2.fillRect(0, 0, w, h);
        g2.setPaint(new GradientPaint(0, 0, ThemeManager.getTintColor(), 0, 18, Color.WHITE));
        g2.fillRect(0, 0, w, 18);
        
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 127), 0, h, new Color(255, 255, 255, 0)));
        g2.fillRect(0, 1, w, (h / 2));
	}
	@Override
	public void paintTabButton(TabButton control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();

		// Top-rounded, bottom-sharp shape flush with the panel's bottom edge.
		Path2D tab = new Path2D.Float();
		tab.moveTo(0, h);
		tab.lineTo(0, ARC);
		tab.quadTo(0, 0, ARC, 0);
		tab.lineTo(w - ARC, 0);
		tab.quadTo(w, 0, w, ARC);
		tab.lineTo(w, h);
		tab.closePath();
	
		if (control.isSelected()) {
			g2.setColor(ThemeManager.getTintColor());
			g2.fill(tab);
			g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 60),
					0, h / 2f, new Color(255, 255, 255, 0)));
			g2.fill(tab);
		} else if (control.getModel().isRollover()) {
			g2.setColor(new Color(255, 255, 255, 45));
			g2.fill(tab);
		} else {
		}
	}
}
