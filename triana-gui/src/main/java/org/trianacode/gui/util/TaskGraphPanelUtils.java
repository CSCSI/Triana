package org.trianacode.gui.util;

import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.gui.main.ZoomLayout;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 18, 2010
 */
public class TaskGraphPanelUtils {

    public static Rectangle2D getBoundingBox(TaskGraphPanel panel) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        TaskGraph tg = panel.getTaskGraph();
        Task currLeft = null;
        Task currRight = null;
        Container c = panel.getContainer();
        if (tg != null && c != null) {
            Component[] comp = c.getComponents();
            for (Component component : comp) {
                if (component instanceof TaskComponent) {
                    TaskComponent tc = (TaskComponent) component;
                    Component cc = tc.getComponent();
                    Task t = tc.getTaskInterface();
                    TaskGraph parent = t.getParent();
                    if (parent != null && parent == tg) {
                        int x = cc.getX();
                        int y = cc.getY();
                        int w = cc.getWidth();
                        int h = cc.getHeight();
                        if (x < minX) {
                            minX = x;
                            currLeft = t;
                        }
                        if (y < minY) {
                            minY = y;
                        }
                        if (x + w > maxX) {
                            maxX = x + w;
                            currRight = t;
                        }
                        if (y + h > maxY) {
                            maxY = y + h;
                        }
                    }
                }
            }
        }
        if (minX < 0) {
            minX = 0;
        }
        if (minY < 0) {
            minY = 0;
        }
        int width = maxX - minX;
        int height = maxY - minY;
        if (width < 0) {
            width = 0;
        }
        if (height < 0) {
            height = 0;
        }

        if (currLeft != null) {
            Node[] nodes = currLeft.getInputNodes();
            for (Node node : nodes) {
                if (node.isConnected()) {
                    if (minX > 0) {
                        if (c.getLayout() instanceof ZoomLayout) {
                            minX = (int) Math.max(0, minX - (10 * ((ZoomLayout) c.getLayout()).getZoom()));
                        } else {
                            minX = Math.max(0, minX - 10);
                        }
                        break;
                    }
                }
            }
        }
        if (currRight != null) {
            Node[] nodes = currLeft.getOutputNodes();
            for (Node node : nodes) {
                if (node.isConnected()) {
                    if (c.getLayout() instanceof ZoomLayout) {
                        width += 10 * ((ZoomLayout) c.getLayout()).getZoom();
                    } else {
                        width += 10;
                    }
                    break;
                }
            }
        }
        return new Rectangle2D.Double(minX, minY, width, height);
    }


}
