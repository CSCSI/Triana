package org.trianacode.gui.desktop;

import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.util.Env;

import java.awt.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 13, 2010
 */
public class DesktopViewController {

    private static TrianaDesktopViewManager currentView = Env.getDesktopViewManager();

    public static void swapView(final TrianaDesktopViewManager newView, final DesktopViewListener app) {
        if (newView != null && newView != currentView) {
            TrianaDesktopView[] views = currentView.getViews();
            for (TrianaDesktopView view : views) {
                TaskGraphPanel panel = view.getTaskgraphPanel();
                Container c = panel.getContainer();
                if (c != null) {
                    if (c.getParent() != null) {
                        c.getParent().remove(c);
                    }
                    newView.newDesktopView(panel);
                }
            }
            currentView.desktopRemoved();
            newView.addDesktopViewListener(app);
            newView.desktopAdded();
            currentView = newView;
            Env.setDesktopView(currentView);
        }
    }

    public static TrianaDesktopViewManager getCurrentView() {
        return currentView;
    }

    public static void setCurrentView(TrianaDesktopViewManager currentView) {
        DesktopViewController.currentView = currentView;
    }
}
