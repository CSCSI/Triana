package org.trianacode.pegasus.dax;

import org.trianacode.taskgraph.Task;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Sep 10, 2010
 * Time: 2:45:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxJobTrianaTask extends DaxFileTrianaTask {

    private Color color = new Color(255, 102, 102);

    public DaxJobTrianaTask(Task task) {
        super(task);
    }

    public Color getToolColor() {
        return color;
    }

    private int getNumberOfJobs() {
        Object o = getTask().getParameter("numberOfJobs");
        if (o != null) {
            int value = (Integer) o;
            if (value > 1) {
                return value;
            }
            return 1;
        }
        return 1;
    }
}
