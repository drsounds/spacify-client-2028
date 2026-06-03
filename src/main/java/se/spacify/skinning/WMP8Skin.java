package se.spacify.skinning;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

import javax.swing.JPanel;

import se.spacify.ui.TabButton;
import se.spacify.ui.theme.ThemeManager;

public class WMP8Skin extends Skin {

	private static final int ARC = 12;
	@Override
	public void paintTopBar(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
        Color tintColor = ThemeManager.getTintColor();
        g2.setPaint(new GradientPaint(0, 0, tintColor, 0, h, ThemeManager.accentLight(2f)));
	}
	@Override
	public void paintHeader(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
		Color tintColor = ThemeManager.getTintColor();
		g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, h, tintColor));
	}
	@Override
	public void paintFooter(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();

    	Color tintColor = ThemeManager.getTintColor();        
    	g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, h, tintColor));
	}
	@Override
	public void paintTabButton(TabButton control, Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
			g2.setPaint(new RadialGradientPaint((float)(w / 2), (float)h, 20f, new float[] {}, new Color[] { ThemeManager.getTintColor(), ThemeManager.accentDark(0.5f)}));
			g2.fill(tab);
			g2.setPaint(new GradientPaint(0f, 0f, ThemeManager.getAccentColor(), (float)(w), h / 2f, new Color(255, 255 ,255, 0)));
	
		} else if (control.getModel().isRollover()) {
			g2.setColor(new Color(255, 255, 255, 45));
			g2.fill(tab);
		} else {
		}
	 	
	}
}
