package se.spacify.controls;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * A button that will popup a menu.
 * The button itself is a JLabel and can be adjusted with all
 * label attributes. The popup menu is returned by getPopup;
 * menu items must be added to it.
 * <p>
 * {@linkplain https://stackoverflow.com/questions/1692677/how-to-create-a-jbutton-with-a-menu}
 * Clicks outside the menu will dismiss it.
*/
public class MenuToolButton extends ToolButton 
        implements MouseListener, PopupMenuListener {
    private static final long serialVersionUID = -4883820504200506311L;
	JPopupMenu popMenu;

    @SuppressWarnings("")
    public MenuToolButton() {
        super();
        popMenu = new JPopupMenu();
        addMouseListener(this);
        popMenu.addPopupMenuListener(this);
    }

    public JPopupMenu getPopup() { return popMenu; }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if ( ! popMenu.isShowing()) {
            popMenu.show(this, 0, getBounds().height);
        }
    }
    @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { 
        SwingUtilities.invokeLater(()->{
            if (popMenu.isShowing()) {
                //  if shpwing, it was hidden and reshown
                //  by a mouse down in the 'this' button
                popMenu.setVisible(false);
            }
        });
    }

    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
    @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }
    @Override public void popupMenuCanceled(PopupMenuEvent e) { }

} // end MenuToolButton