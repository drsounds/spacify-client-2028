package se.spacify.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import se.spacify.ui.theme.ThemeManager;

public class TopBar extends JPanel {
	public TopBar() {
		
	}

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();

        ((MainWindow)(SwingUtilities.getWindowAncestor(this))).getSkin().paintTopBar(this, g2);
		
        g2.dispose();
    }

}
