package org.trianacode.gui.desktop;

import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.TaskGraph;

import java.awt.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 10, 2010
 */
public interface TrianaDesktopViewManager {

    public Container getWorkspace();

    public TrianaDesktopView newDesktopView(TaskGraphPanel panel);

    public void remove(TrianaDesktopView view);

    public TrianaDesktopView getTaskgraphViewFor(TaskGraph taskgraph);

    public void addDesktopViewListener(DesktopViewListener listener);

    public void removeDesktopViewListener(DesktopViewListener listener);

    public TrianaDesktopView[] getViews();

    public void setSelected(TrianaDesktopView panel, boolean selected);

    public TrianaDesktopView getSelected();

    public TrianaDesktopView getDesktopViewFor(TaskGraphPanel panel);

    public String getTitle(TrianaDesktopView view);

    public void setTitle(TrianaDesktopView view, String title);

    public void desktopRemoved();

    public void desktopAdded();

    public TrianaDesktopView getDropTarget(int x, int y, Component source);


}
