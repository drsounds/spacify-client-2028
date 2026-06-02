package se.spacify.navigation;

import javax.swing.*;

import se.spacify.ui.MainWindow;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class SPViewStack extends JPanel {

    private final List<SPView> registeredViews = new ArrayList<>();
    private final Deque<String> backStack = new ArrayDeque<>();
    private final Deque<String> forwardStack = new ArrayDeque<>();
    private final List<NavigationListener> listeners = new ArrayList<>();

    private String currentUri = null;
    private SPView currentView = null;
    
    public MainWindow getMainWindow() {
    	Container parent = getParent();
    	while (parent != null && parent != this) {
    		if (parent instanceof MainWindow) {
    			return (MainWindow)parent;
    		}
    		parent = parent.getParent();
    	}
    	return null;
    }

    public SPViewStack() {
        setLayout(new BorderLayout());
    }

    public void registerView(SPView view) {
        registeredViews.add(view);
    }

    public void navigate(String uri) {
        navigate(uri, true);
    }

    private void navigate(String uri, boolean pushHistory) {
        if (uri == null || uri.equals(currentUri)) return;

        SPView matched = null;
        for (SPView v : registeredViews) {
            if (v.acceptsUri(uri)) {
                matched = v;
                break;
            }
        }
        if (matched == null) return;

        if (pushHistory && currentUri != null) {
            backStack.push(currentUri);
            forwardStack.clear();
        }

        if (currentView != null) {
            currentView.onHide();
            remove(currentView.getComponent());
        }

        currentUri = uri;
        currentView = matched;
        currentView.navigate(uri);
        currentView.onShow();

        add(currentView.getComponent(), BorderLayout.CENTER);
        revalidate();
        repaint();

        notifyListeners();
    }

    public void back() {
        if (backStack.isEmpty()) return;
        String prev = backStack.pop();
        if (currentUri != null) forwardStack.push(currentUri);
        navigate(prev, false);
    }

    public void forward() {
        if (forwardStack.isEmpty()) return;
        String next = forwardStack.pop();
        if (currentUri != null) backStack.push(currentUri);
        navigate(next, false);
    }

    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    public boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    public String getCurrentUri() {
        return currentUri;
    }

    public void addNavigationListener(NavigationListener l) {
        listeners.add(l);
    }

    private void notifyListeners() {
        for (NavigationListener l : listeners) {
            l.onNavigate(currentUri, canGoBack(), canGoForward());
        }
    }
}
