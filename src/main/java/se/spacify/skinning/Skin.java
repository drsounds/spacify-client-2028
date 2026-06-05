package se.spacify.skinning;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import se.spacify.ui.GlassPanel;
import se.spacify.ui.GlossyButton;
import se.spacify.ui.TabButton;
import se.spacify.ui.ToolBar;
import se.spacify.ui.theme.ThemeManager;

public abstract class Skin {
	public abstract void paintTopBar(JPanel control, Graphics2D g2);
	public abstract void paintHeader(JPanel header, Graphics2D g2);
	public abstract void paintFooter(JPanel footer, Graphics2D g2);
	public abstract void paintTabButton(TabButton button, Graphics2D g2);
	public abstract void paintGlossyButton(GlossyButton control, Graphics2D g2, int x, int y, int d);
	public abstract void paintPlaylist(JPanel control, Graphics2D g2);
	public abstract void paintGlassPanel(GlassPanel control, Graphics2D g2);
	public abstract void paintToolBar(ToolBar control, Graphics2D g2);
}
