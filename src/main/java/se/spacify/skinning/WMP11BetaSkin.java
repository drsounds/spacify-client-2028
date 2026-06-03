package se.spacify.skinning;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import se.spacify.ui.TabButton;
import se.spacify.ui.theme.ThemeManager;

public class WMP11BetaSkin extends Skin {
	@Override
	public void paintHeader(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
		g2.setPaint(ThemeManager.getAccentColor());
		g2.fillRect(0, 0, w, h);
		
		g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, h, new Color(255, 255, 255 ,0)));
		g2.fillRect(0, 0, w, h / 2);
	
		g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, h, new Color(255, 255, 255, 0)));
		g2.fillRect(0, h / 2, w, (h / 2));
	}

	@Override
	public void paintTopBar(JPanel control, Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintFooter(JPanel footer, Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintTabButton(TabButton button, Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}
}
