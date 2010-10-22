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

    private DaxJobSubtitle sub = new DaxJobSubtitle();
    private Color color = Color.pink.darker();

    public DaxJobTrianaTask(Task task) {
        super(task);
        //sub.setVisible(true);
        //add(sub);
        //invalidateSize();
        //validate();
    }

    public Color getToolColor() {
        return color;
    }

    private int getNumberOfJobs() {
        Object o = getTask().getParameter("numberOfJobs");
        //     System.out.println("Returned object from param *numberOfFiles* : " + o.getClass().getCanonicalName() + " : " + o.toString());
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
