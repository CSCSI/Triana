package org.trianacode.gui.desktop;

import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.TaskGraph;

import java.awt.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 10, 2010
 */
public interface DesktopViewManager {

    public Container getWorkspace();

    public DesktopView newDesktopView(TaskGraphPanel panel);

    public void remove(DesktopView view);

    public DesktopView getTaskgraphViewFor(TaskGraph taskgraph);

    public void addDesktopViewListener(DesktopViewListener listener);

    public void removeDesktopViewListener(DesktopViewListener listener);

    public DesktopView[] getViews();

    public void setSelected(DesktopView panel, boolean selected);

    public DesktopView getSelected();

    public DesktopView getDesktopViewFor(TaskGraphPanel panel);

    public String getTitle(DesktopView view);

    public void setTitle(DesktopView view, String title);

    public void desktopRemoved();

    public void desktopAdded();

    public DesktopView getDropTarget(int x, int y, Component source);


}
