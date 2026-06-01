package se.spacify.ui.theme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    private static float hue        = 0.0f;  // 0-1  (background tint)
    private static float saturation = 0.0f;  // 0-1  (background tint)
    private static float lightness  = 0.5f;  // 0-1  (background tint)
    private static boolean darkMode = true;
    private static Color accentColor = new Color(30, 215, 96);  // Spotify green default

    private static final List<Runnable> listeners = new ArrayList<>();

    public static void setHue(float h)           { hue = h;           applyToDefaults(); notify_(); }
    public static void setSaturation(float s)    { saturation = s;    applyToDefaults(); notify_(); }
    public static void setLightness(float l)     { lightness = l;     applyToDefaults(); notify_(); }
    public static void setDarkMode(boolean d)    { darkMode = d;      applyToDefaults(); notify_(); }
    public static void setAccentColor(Color c)   { accentColor = c;   applyToDefaults(); notify_(); }

    public static float   getHue()          { return hue; }
    public static float   getSaturation()   { return saturation; }
    public static float   getLightness()    { return lightness; }
    public static boolean isDarkMode()      { return darkMode; }
    public static Color   getAccentColor()  { return accentColor; }

    public static void addChangeListener(Runnable r) { listeners.add(r); }

    /** Call once at startup to seed UIManager before the first window is built. */
    public static void applyToDefaults() {
        UIDefaults d = UIManager.getLookAndFeelDefaults();
        if (darkMode) {
            float bg  = 0.07f + lightness * 0.10f;   // 0.07 – 0.17
            float mid = bg + 0.05f;
            float acc = 0.28f + lightness * 0.10f;   // 0.28 – 0.38

            put(d, "control",        hsl(hue, saturation * 0.12f, bg));
            put(d, "nimbusBase",     hsl(hue, saturation,         acc));
            put(d, "nimbusBlueGrey", hsl(hue, saturation * 0.20f, mid));
            put(d, "text",                        new Color(210, 210, 210));
            put(d, "textForeground",              new Color(210, 210, 210));
            put(d, "nimbusDisabledText",          new Color(100, 100, 100));
            put(d, "nimbusSelectedText",          Color.WHITE);
            put(d, "nimbusFocus",               accentColor);
            put(d, "nimbusSelectionBackground", accentColor);
        } else {
            float bg  = 0.84f + lightness * 0.08f;   // 0.84 – 0.92
            float acc = 0.42f + lightness * 0.10f;   // 0.42 – 0.52

            put(d, "control",        hsl(hue, saturation * 0.08f, bg));
            put(d, "nimbusBase",     hsl(hue, saturation,         acc));
            put(d, "nimbusBlueGrey", hsl(hue, saturation * 0.18f, 0.68f));
            put(d, "text",                        new Color(30, 30, 30));
            put(d, "textForeground",              new Color(30, 30, 30));
            put(d, "nimbusDisabledText",          new Color(130, 130, 130));
            put(d, "nimbusSelectedText",          Color.BLACK);
            put(d, "nimbusFocus",               accentColor);
            put(d, "nimbusSelectionBackground", accentColor);
        }
    }

    /** Color derived from the background-tint HSL sliders — used for chrome gradients. */
    public static Color getTintColor() {
        float l = darkMode ? (0.28f + lightness * 0.10f) : (0.42f + lightness * 0.10f);
        return hsl(hue, saturation, l);
    }

    // ── HSL conversion ────────────────────────────────────────────────────────

    public static Color hsl(float h, float s, float l) {
        if (s == 0f) { int v = (int)(l * 255); return new Color(v, v, v); }
        float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
        float p = 2 * l - q;
        return new Color(
            clamp(hueToRgb(p, q, h + 1f / 3)),
            clamp(hueToRgb(p, q, h)),
            clamp(hueToRgb(p, q, h - 1f / 3))
        );
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1; if (t > 1) t -= 1;
        if (t < 1f/6) return p + (q - p) * 6 * t;
        if (t < 1f/2) return q;
        if (t < 2f/3) return p + (q - p) * (2f/3 - t) * 6;
        return p;
    }

    private static float clamp(float v) { return Math.max(0, Math.min(1, v)); }

    private static void put(UIDefaults d, String key, Object val) {
        d.put(key, val);
        UIManager.put(key, val);   // also override in the global layer
    }

    private static void notify_() {
        for (Runnable r : listeners) r.run();
    }
}
