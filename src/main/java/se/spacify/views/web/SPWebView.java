package se.spacify.views.web;

import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;
import se.spacify.navigation.SPView;
import se.spacify.navigation.SPViewStack;
import se.spacify.ui.theme.ThemeManager;
import se.spacify.web.CefRuntime;
import se.spacify.web.SiteUri;

import javax.swing.*;
import java.awt.*;

/**
 * An embedded-browser view for {@code spacify:site:<host>[:path]} URIs.
 *
 * <p>Navigation is bilateral: a spacify URI is translated to https and loaded in
 * CEF; conversely, link clicks / in-page history inside CEF are translated back
 * to {@code spacify:site:} URIs and reflected into the address bar and nav
 * buttons via {@link SPViewStack}. While a site is open the app's back/forward
 * drive the browser's own history (see {@link #handlesHistory()}).
 *
 * <p>CEF is initialised lazily on first navigation (off the EDT, since the first
 * run downloads the native Chromium bundle).
 */
public class SPWebView extends SPView {

    private final SPViewStack viewStack;
    private final JPanel      panel;
    private final JLabel      status;

    private CefClient  client;
    private CefBrowser browser;
    private boolean    initStarted = false;
    private String     pendingUrl;

    public SPWebView(SPViewStack viewStack) {
        this.viewStack = viewStack;
        panel = new JPanel(new BorderLayout());
        status = new JLabel("", SwingConstants.CENTER);
        status.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        panel.add(status, BorderLayout.CENTER);
        updateColors();
        ThemeManager.addChangeListener(this::updateColors);
    }

    // ── SPView ──────────────────────────────────────────────────────────────────

    @Override public boolean acceptsUri(String uri) { return SiteUri.isSiteUri(uri); }

    @Override
    public void navigate(String uri) {
        String url = SiteUri.toUrl(uri);
        if (url == null) return;
        if (browser == null) {
            pendingUrl = url;
            ensureBrowser();
        } else if (!url.equals(browser.getURL())) {
            browser.loadURL(url);
        }
    }

    @Override public JComponent getComponent() { return panel; }
    @Override public String getTitle() { return "Web"; }

    // ── History delegation (app ◄ ► drive the browser) ───────────────────────────

    @Override public boolean handlesHistory() { return browser != null; }
    @Override public boolean canGoBack()      { return browser != null && browser.canGoBack(); }
    @Override public boolean canGoForward()   { return browser != null && browser.canGoForward(); }
    @Override public void    goBack()         { if (browser != null) browser.goBack(); }
    @Override public void    goForward()      { if (browser != null) browser.goForward(); }

    // ── CEF lifecycle ─────────────────────────────────────────────────────────────

    private void ensureBrowser() {
        if (initStarted) return;
        initStarted = true;
        status.setText("Starting browser… (first run downloads Chromium)");

        new SwingWorker<CefClient, Void>() {
            @Override protected CefClient doInBackground() throws Exception {
                return CefRuntime.newClient();   // heavy: builds CefApp on first call
            }
            @Override protected void done() {
                try {
                    client = get();
                    attachBrowser(pendingUrl != null ? pendingUrl : "about:blank");
                } catch (Exception e) {
                    status.setText("<html>Could not start the embedded browser:<br>"
                        + e.getMessage() + "</html>");
                }
            }
        }.execute();
    }

    private void attachBrowser(String url) {
        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser b, CefFrame frame, String newUrl) {
                if (frame == null || !frame.isMain()) return;
                String sp = SiteUri.toSpacifyUri(newUrl);
                if (sp == null) return;
                SwingUtilities.invokeLater(() -> viewStack.updateCurrentUri(sp));
            }
        });

        browser = client.createBrowser(url, false, false);
        panel.remove(status);
        panel.add(browser.getUIComponent(), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
        viewStack.refreshNavState();
    }

    private void updateColors() {
        Color bg = ThemeManager.getBackground();
        panel.setBackground(bg);
        status.setForeground(ThemeManager.getForeground());
        panel.repaint();
    }
}
