package se.spacify.ui.theme;

import java.awt.Color;

public class ColorUtils {
	public static Color lighten(Color a, float ratio) {
        return new Color(
            Math.min(255, (int)(a.getRed()   * ratio)),
            Math.min(255, (int)(a.getGreen() * ratio)),
            Math.min(255, (int)(a.getBlue()  * ratio))
        );
	}
	public static Color darken(Color a, float ratio) {
        return new Color(
            Math.min(255, (int)(a.getRed()   * ratio)),
            Math.min(255, (int)(a.getGreen() * ratio)),
            Math.min(255, (int)(a.getBlue()  * ratio))
        );
	}
}
