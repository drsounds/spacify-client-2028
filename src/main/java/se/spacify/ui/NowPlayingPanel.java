package se.spacify.ui;

import se.spacify.navigation.NavigationListener;
import se.spacify.navigation.SPViewStack;
import se.spacify.navigation.SidebarNode;
import se.spacify.ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NowPlayingPanel extends JPanel implements NavigationListener {


    public NowPlayingPanel(SPViewStack viewStack) {
    	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(50, 50, 50)),
            BorderFactory.createEmptyBorder(16, 12, 16, 12)
        ));

        JLabel title = new JLabel("Now Playing");
        title.setForeground(new Color(160, 160, 160));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 11f));
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel placeholder = new JLabel("Nothing playing");
        placeholder.setForeground(new Color(100, 100, 100));
        placeholder.setFont(placeholder.getFont().deriveFont(12f));
        placeholder.setAlignmentX(LEFT_ALIGNMENT);

        add(title);
        add(Box.createVerticalStrut(8));
        add(placeholder);

        ThemeManager.addChangeListener(this::repaint);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        Color tintColor = ThemeManager.getTintColor();
        g2.setPaint(new GradientPaint(0, 0, tintColor, 0, h, ThemeManager.accentLight(2f)));
        g2.fillRect(0, 0, w, h);
      
        g2.dispose();
    }
	@Override
	public void onNavigate(String uri, boolean canGoBack, boolean canGoForward) {
		// TODO Auto-generated method stub
		
	}
}