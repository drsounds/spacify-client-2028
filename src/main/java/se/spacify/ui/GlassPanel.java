package se.spacify.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import se.spacify.ui.theme.ThemeManager;

public class GlassPanel extends JPanel {

	private static final long serialVersionUID = -6838852001495396343L;

    public GlassPanel() {
   
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        
        Color tintColor = ThemeManager.getTintColor();
        g2.setPaint(new GradientPaint(0, 0, ThemeManager.accentLight(2f), 0, h, tintColor));
        g2.fillRect(0, 0, w, h);
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 0), 0, h, new Color(255, 255, 255, 255)));
        g2.fillRect(0, (h / 2), w, (h / 2));

        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 127), 0, h, new Color(255, 255, 255, 0)));
        g2.fillRect(0, 1, w, (h / 2));

        g2.dispose();
    }

}
