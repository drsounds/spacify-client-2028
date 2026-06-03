package se.spacify.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import se.spacify.ui.theme.ThemeManager;

public class TopBar extends JPanel {
	public TopBar() {
		
	}

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        
        //Color tintColor = ThemeManager.getTintColor();
        //g2.setPaint(new GradientPaint(0, 0, tintColor, 0, h, ThemeManager.accentLight(2f)));
        g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, h, Color.WHITE));
        g2.fillRect(0, 0, w, h);

        g2.dispose();
    }

}
