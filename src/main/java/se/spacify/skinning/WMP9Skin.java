package se.spacify.skinning;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

import javax.swing.JPanel;

import se.spacify.ui.GlassPanel;
import se.spacify.ui.GlossyButton;
import se.spacify.ui.TabButton;
import se.spacify.ui.ToolBar;
import se.spacify.ui.theme.ThemeManager;

public class WMP9Skin extends Skin {

	private static final int ARC = 12;
	@Override
	public void paintTopBar(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
        Color tintColor = ThemeManager.getTintColor();
        g2.setPaint(new GradientPaint(0, 0, tintColor, 0, h, Color.WHITE));
	}
	@Override
	public void paintHeader(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
		g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h, ThemeManager.accentLight(2f)));
		g2.fillRect(0, 0, w, h);
	}
	@Override
	public void paintGlassPanel(GlassPanel control, Graphics2D g2) {
		int w = control.getWidth(), h = control.getHeight();
		Path2D shape = control.shape(w, h);

		// Base accent gradient, antialiased to the rounded/diagonal outline.
		g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, h, ThemeManager.getTintColor()));
		g2.fill(shape);

		// Glossy white overlays, confined to the shape.
		g2.setClip(shape);
		g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 0), 0, h, new Color(255, 255, 255, 255)));
		g2.fillRect(0, h / 2, w, h - (h / 2));
		g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 127), 0, h, new Color(255, 255, 255, 0)));
		g2.fillRect(0, 1, w, h / 2);
	}

	@Override
	public void paintToolBar(ToolBar control, Graphics2D g2) {
		int w = control.getWidth(), h = control.getHeight();
		g2.setPaint(ThemeManager.getTintColor());
		g2.fillRect(0, 0, w, h);
	}
	@Override
	public void paintFooter(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
       
        g2.setPaint(Color.BLACK);
		g2.fillRect(0, 0, w, h);
		
		g2.setPaint(new GradientPaint(0, 0, ThemeManager.getTintColor(), 0, h, new Color(255, 255, 255, 0)));
		g2.fillRect(0, 0, w, h / 2);
	
		g2.setPaint(new GradientPaint(0, h / 2, new Color(255, 255, 255, 0), 0, h,  ThemeManager.accentLight(2f)));
		g2.fillRect(0, h / 2, w, (h / 2));
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
			g2.setPaint(new RadialGradientPaint((float)(w / 2), (float)h, 20f, new float[] {}, new Color[] { ThemeManager.getTintColor(), ThemeManager.accentDark(0.5f)}));
			g2.fill(tab);
			g2.setPaint(new GradientPaint(0f, 0f, ThemeManager.getAccentColor(), (float)(w), h / 2f, new Color(255, 255 ,255, 0)));
	
		} else if (control.isHovered()) {
			g2.setColor(new Color(255, 255, 255, 45));
			g2.fill(tab);
		} else {
		}
	 	
	}
	@Override
	public void paintGlossyButton(GlossyButton control, Graphics2D g2, int x, int y, int d) {
		// TODO Auto-generated method stub
		
	}
}
