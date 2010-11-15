package org.trianacode.gui.desktop;

import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.util.Env;

import java.awt.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 13, 2010
 */
public class DesktopViewController {

    private static DesktopViewManager currentView = Env.getDesktopViewManager();

    public static void swapView(final DesktopViewManager newView, final DesktopViewListener app) {
        if (newView != null && newView != currentView) {

            newView.addDesktopViewListener(app);
            newView.desktopAdded();
            DesktopView[] views = currentView.getViews();
            for (DesktopView view : views) {
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
            currentView = newView;
            Env.setDesktopView(currentView);
        }
    }

    public static DesktopViewManager getCurrentView() {
        return currentView;
    }

    public static void setCurrentView(DesktopViewManager currentView) {
        DesktopViewController.currentView = currentView;
    }
}
