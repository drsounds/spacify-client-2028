package se.spacify.skinning;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import se.spacify.ui.TabButton;
import se.spacify.ui.theme.ThemeManager;

public abstract class Skin {
	public abstract void paintTopBar(JPanel control, Graphics2D g2);
	public abstract void paintHeader(JPanel header, Graphics2D g2);
	public abstract void paintFooter(JPanel footer, Graphics2D g2);
	public abstract void paintTabButton(TabButton button, Graphics2D g2);
}
