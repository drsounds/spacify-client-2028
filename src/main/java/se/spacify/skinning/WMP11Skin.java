package se.spacify.skinning;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.JTable;

import se.spacify.controls.GlassPanel;
import se.spacify.controls.GlossyButton;
import se.spacify.controls.TabButton;
import se.spacify.controls.ToolBar;
import se.spacify.controls.ToolButton;
import se.spacify.ui.theme.ColorUtils;
import se.spacify.ui.theme.ThemeManager;

public class WMP11Skin extends Skin {

	private static final int ARC = 12;
	@Override
	public void paintTopBar(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
		g2.setPaint(new GradientPaint(0, 0, Color.BLACK, 0, h, ColorUtils.saturate(ThemeManager.getTintColor(), 0.5f)));
		g2.fillRect(0, 0, w, h);
	}
	@Override
	public void paintHeader(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
        g2.setPaint(Color.BLACK);
		g2.fillRect(0, 0, w, h);
		
		g2.setPaint(new GradientPaint(0, 0, ColorUtils.saturate(ThemeManager.getTintColor(), 0.5f), 0, h, new Color(0, 0, 0 ,0)));
		g2.fillRect(0, 0, w, h / 2);
	
		g2.setPaint(new GradientPaint(0, h / 2, new Color(0, 0, 0, 0), 0, h,  ThemeManager.getTintColor()));
		g2.fillRect(0, h / 2, w, (h / 2));
	}
	@Override
	public void paintFooter(JPanel control, Graphics2D g2) {
        int w = control.getWidth(), h = control.getHeight();
       
        g2.setPaint(Color.BLACK);
		g2.fillRect(0, 0, w, h);
		
		g2.setPaint(new GradientPaint(0, 0, ColorUtils.saturate(ThemeManager.getTintColor(), 0.5f), 0, h, new Color(0, 0, 0, 0)));
		g2.fillRect(0, 0, w, h / 2);
	
		g2.setPaint(new GradientPaint(0, h / 2, new Color(0, 0, 0, 0), 0, h,  ThemeManager.getTintColor()));
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


	 	if (control.isSelected() || control.isPressedState()) {
			g2.setPaint(new RadialGradientPaint((float)(w / 2), (float)h, 120f, new float[] { 0, 1 }, new Color[] { ThemeManager.getTintColor(), ThemeManager.tintDark(0.5f)}));
			g2.fill(tab);
			g2.setPaint(new GradientPaint(0f, 0f, ThemeManager.getTintColor(), (float)(w), h / 2f, new Color(255, 255 ,255, 0)));

		} else if (control.isHovered()) {
			g2.setColor(new Color(255, 255, 255, 45));
			g2.fill(tab);
		} else {
		}
	 	
	}
	
	@Override
	public void paintGlossyButton(GlossyButton control, Graphics2D g2, int x, int y, int d) {
		// TODO Auto-generated method stub
		Color accent = ThemeManager.getAccentColor();
		Color tint = ThemeManager.getTintColor();

		Color glowColor = new Color(255, 255, 255, 0);
		if (control.getPrimary()) {
			glowColor = ColorUtils.saturate(tint, 125.2f);
		}
		int sheenAlpha = control.getPressed() ? 110 : 220;
		Color[] lights = new Color[] { ColorUtils.alpha(ColorUtils.lighten(glowColor, 25f), sheenAlpha), ColorUtils.alpha(ColorUtils.lighten(glowColor, 25f), 0f) };
		
		int width = control.getWidth(), height = control.getHeight();
		// Darker outer rim, slightly inset face sits on top of it.
		//Ellipse2D outer = new Ellipse2D.Float(x, y, d, d);
		
		g2.setPaint(new GradientPaint(x, y, ThemeManager.tintDark(0.9f), x, y + d, ThemeManager.tintDark(0.5f)));
		//g2.fill(outer);

		int inset = Math.max(2, d / 24);
		int fd = d - inset * 2;
		int fx = x + inset, fy = y + inset;
		Ellipse2D face = new Ellipse2D.Float(x - 4, y - 4, width + 8, height + 8);
		g2.setClip(face);
		
		g2.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 0), width,
				y + height / 2, new Color(255, 255, 255, 127)));
		g2.fillRect(x, y, width + 4, height + 4);

		Ellipse2D face2 = new Ellipse2D.Float(x, y, width, height);
		// Base vertical gradient — brighter when hovered, dimmer/inverted when pressed.
		Color top, bottom;
		if (control.getPressed()) {
			top = ThemeManager.accentDark(0.2f);
			bottom = ThemeManager.scaleRgb(accent, 0.9f);
		} else if (control.getHovered()) {
			top = ThemeManager.accentLight(1.7f);
			bottom = tint;
		} else {
			top = ThemeManager.accentLight(1.4f);
			bottom = tint;
		}
		g2.setPaint(new GradientPaint(fx + 2, fy + 2, top, fx, fy + fd, bottom));
		g2.fill(face2);

		Shape oldClip = g2.getClip();
		g2.setClip(face2);
		g2.setPaint(new GradientPaint(x, y + height, ColorUtils.alpha(glowColor, control.getPressed() ? 30 : 90), x,
				y + height / 2, ColorUtils.alpha(glowColor, 0)));
		g2.fillRect(0, 0, x, height - height / 2);

		// Bottom reflection: faint upward light from the lower edge.
		g2.setPaint(new GradientPaint(fx, fy + fd, ColorUtils.alpha(glowColor, control.getPressed() ? 30 : 90), x,
				y + height / 2, new Color(255, 255, 255, 0)));
		g2.fillRect(x, y + height / 2, x, y - y / 2);

		// Top "bubble" sheen: a bright ellipse narrower than the face, fading down.
		// This is the signature Aqua highlight.
		int hlInsetX = Math.round(fd * 0.16f);
		int hlTop = Math.round(fd * 0.06f);
		int hlW = fd - hlInsetX * 2;
		int hlH = Math.round(fd * 0.5f);
		Ellipse2D sheen = new Ellipse2D.Float(x, y + height - 10, width, height / 1.5f);
		g2.setPaint(
			new RadialGradientPaint(
				control.getWidth() / 2,
				control.getHeight(),
				27,
				new float[] { 0.2f, 1 },
				lights
			)
		);
		g2.fill(sheen);
		
		Ellipse2D sheen2 = new Ellipse2D.Float(0, 0, width, height / 2);
		g2.setPaint(
			new RadialGradientPaint(
				control.getWidth() / 2f,
				control.getHeight() / 2f,
				15,
				new float[] { 0.8f, 1 },
				new Color[] { 
					new Color(255, 255, 255, 0),
					new Color(255, 255, 255, 127)
				}
			)

		);
		g2.fill(sheen2);
		g2.setClip(oldClip);

		// Thin inner ring to crisp up the rim/face boundary.
		g2.setStroke(new java.awt.BasicStroke(1f));
		g2.setColor(new Color(255, 255, 255, 60));
		g2.draw(new Ellipse2D.Float(x + 0.5f, y + 0.5f, width - 1, height - 1));
	}
	

	@Override
	public void paintGlassPanel(GlassPanel control, Graphics2D g2) {
		int w = control.getWidth(), h = control.getHeight();
		Path2D shape = control.shape(w, h);

		// Glossy white overlays, confined to the shape.
		g2.setClip(shape);
        g2.setPaint(Color.BLACK);
		g2.fillRect(0, 0, w, h);
		// Base accent gradient, antialiased to the rounded/diagonal outline.
		g2.setPaint(new GradientPaint(0, 0, ThemeManager.tintLight(2f), 0, h, ThemeManager.getTintColor()));
		g2.fill(shape);

		g2.setPaint(new GradientPaint(0, 0, ThemeManager.getTintColor(), 0, h, new Color(0, 0, 0, 0)));
		g2.fillRect(0, 0, w, h / 2);
	
		g2.setPaint(new GradientPaint(0, h / 2, new Color(0, 0, 0, 0), 0, h,  ThemeManager.getTintColor()));
		g2.fillRect(0, h / 2, w, (h / 2));
	}

	@Override
	public void paintPlaylist(JPanel control, Graphics2D g2) {
		// TODO Auto-generated method stub
		int w = control.getWidth(), h = control.getHeight();
        Color tintColor = ThemeManager.getTintColor();
        g2.setPaint(ThemeManager.getBackground());
        g2.fillRect(0, 0, w, h);
	}
	@Override
	public void paintToolBar(ToolBar control, Graphics2D g2) {
		int w = control.getWidth(), h = control.getHeight();
		g2.setPaint(ThemeManager.getBackground());
		g2.fillRect(0, 0, w, h);
	}

	@Override
	public void paintToolButton(ToolButton control, Graphics2D g2) {
		int w = control.getWidth(), h = control.getHeight();
		Color background = ThemeManager.getBackground();
        ButtonModel model = control.getModel();
		g2.setPaint(background);
		g2.fillRect(0, 0, w, h);
		if (model.isPressed()) {
			background = ColorUtils.darken(background, 0.9f);
			g2.setPaint(background);
			g2.fillRect(0, 0, w, h);
			background = ColorUtils.darken(background, 0.5f);
			g2.setPaint(background);
			g2.fillRect(0, 0, w, 2);
			g2.fillRect(0, 0, 2, h);
			g2.setPaint(ColorUtils.lighten(background, 5));
			g2.fillRect(w - 2, 0, 2, h);
			g2.fillRect(0, h - 2, 2, h);
		} else if (model.isRollover()) {
			background = ColorUtils.darken(background, 2f);
			g2.setPaint(background);
			g2.fillRect(0, 0, w, h);
			background = ColorUtils.lighten(background, 2);
			g2.setPaint(background);
			g2.fillRect(0, 0, w, h);
			g2.setPaint(ColorUtils.darken(background, 3));
			g2.fillRect(0, 0, w, 2);
			g2.fillRect(0, 0, 2, h);
			g2.setPaint(ColorUtils.lighten(background, 3));
			g2.fillRect(w, 0, 2, h);
			g2.fillRect(0, h, 2, h);
			
		}
	}

	@Override
	public void paintTableHeader(JTable table, int width, int height, Graphics2D g2) {
		// TODO Auto-generated method stub
		g2.setPaint(new Color(235, 234, 219));
		g2.fillRect(0, 0, width, height);
		
	}
}
