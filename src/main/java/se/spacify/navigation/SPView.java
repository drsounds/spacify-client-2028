package se.spacify.navigation;

import javax.swing.JComponent;

public abstract class SPView {

    public abstract boolean acceptsUri(String uri);

    public abstract void navigate(String uri);

    public abstract JComponent getComponent();

    public String getTitle() {
        return "";
    }

    public void onShow() {}

    public void onHide() {}
}
