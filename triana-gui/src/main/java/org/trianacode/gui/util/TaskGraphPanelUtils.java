package org.trianacode.gui.util;

import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.main.TaskGraphPanel;
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
                        }
                        if (y < minY) {
                            minY = y;
                        }
                        if (x + w > maxX) {
                            maxX = x + w;
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
        return new Rectangle2D.Double(minX, minY, width, height);
    }


}
