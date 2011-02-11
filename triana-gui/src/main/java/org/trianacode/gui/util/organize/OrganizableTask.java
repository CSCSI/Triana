package org.trianacode.gui.util.organize;

import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskLayoutDetails;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 20, 2010
 */
public class OrganizableTask {

    private Component component;
    private Task task;
    protected double edgedx = -1.0;
    protected double edgedy = -1.0;
    protected double repulsiondx = -1.0;
    protected double repulsiondy = -1.0;
    private Point2D point;

    /**
     * movement speed, x
     */
    protected double dx = -1.0;

    /**
     * movement speed, y
     */
    protected double dy = -1.0;

    public OrganizableTask(Component component, Task task) {
        this.component = component;
        this.task = task;
        //this.point = component.getLocation();
        String x = (String) task.getParameter(Task.GUI_X);
        String y = (String) task.getParameter(Task.GUI_Y);
        this.point = new Point2D.Double(Double.parseDouble(x), Double.parseDouble(y));
        System.out.println("OrganizableTask.OrganizableTask point for task " + task.getToolName() + " :" + point);
        System.out.println("OrganizableTask.OrganizableTask component point for task " + task.getToolName() + " :" + component.getLocation());

    }

    public Component getComponent() {
        return component;
    }

    public Task getTask() {
        return task;
    }

    public double getEdgedx() {
        return edgedx;
    }

    public double getEdgedy() {
        return edgedy;
    }

    public double getRepulsiondx() {
        return repulsiondx;
    }

    public double getRepulsiondy() {
        return repulsiondy;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public void updatePoint(TaskLayoutDetails layout) {
        double x = (point.getX() / layout.getTaskDimensions().getWidth()) + layout.getLeftBorder();
        double y = (point.getY() / layout.getTaskDimensions().getHeight()) + layout.getTopBorder();
        task.setParameter(Task.GUI_X, String.valueOf(x));
        task.setParameter(Task.GUI_Y, String.valueOf(y));
        //component.setLocation((Point) point);
    }

    public Point2D getPoint() {
        return point;
    }

    public int getConnectionCount() {
        int total = 0;
        Node[] nodes = task.getInputNodes();
        for (Node node : nodes) {
            if (node.isConnected()) {
                total++;
            }
        }
        nodes = task.getOutputNodes();
        for (Node node : nodes) {
            if (node.isConnected()) {
                total++;
            }
        }
        return total;

    }
}
