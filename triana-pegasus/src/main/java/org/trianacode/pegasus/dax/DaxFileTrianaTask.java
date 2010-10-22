package org.trianacode.pegasus.dax;

import org.trianacode.gui.main.imp.MainTrianaTask;
import org.trianacode.gui.main.imp.TrianaToolLayout;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Sep 9, 2010
 * Time: 1:39:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxFileTrianaTask extends MainTrianaTask {

    private Task task;
    private Color color = Color.cyan.darker();
    private JLabel collectionComponent = null;
    private int files = 0;

    /**
     * Constructs a new MainTrianaTask for viewing the specified task
     */
    public DaxFileTrianaTask(Task task) {
        super(task);
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    protected void processParameterUpdate(final ParameterUpdateEvent evt) {
        if (evt.getTask() == this.task && evt.getParameterName().equals("collection")) {
            Boolean b = (Boolean) evt.getNewValue();
            if (b) {
                if (collectionComponent == null) {
                    collectionComponent = new JLabel();
                    collectionComponent.setPreferredSize(new Dimension(getWidth(), 3));
                    add(collectionComponent, TrianaToolLayout.TOP);
                    invalidateSize();

                }
            } else {
                if (collectionComponent != null) {
                    remove(collectionComponent);
                    collectionComponent = null;
                    invalidateSize();
                }
            }
        }
    }

    protected void drawRectangle(Graphics g, Color color, Color orig) {
        Color toolColor = color;
        Color shadow = toolColor.darker();
        if (collectionComponent != null) {
            g.setColor(shadow);
            g.fill3DRect(5, 0, getSize().width - 5, getSize().height - 5, !isSelected());
            g.setColor(toolColor);
            g.fill3DRect(0, 5, getSize().width - 2, getSize().height - 2, !isSelected());
        } else {
            g.setColor(toolColor);
            g.fill3DRect(0, 0, getSize().width, getSize().height, !isSelected());
        }

        g.setColor(orig);
    }


    public Color getToolColor() {
        return color;
    }

    private int getNumberOfFiles() {
        Object o = getTask().getParameter("numberOfFiles");
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
