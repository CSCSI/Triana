package org.trianacode.shiwaall.gui.models;

import org.trianacode.gui.main.imp.MainTrianaTask;
import org.trianacode.gui.main.imp.TrianaToolLayout;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;

import javax.swing.*;
import java.awt.*;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Sep 9, 2010
 * Time: 1:39:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxFileTrianaTask extends MainTrianaTask {

    /** The task. */
    private Task task;
    
    /** The collection component. */
    private JLabel collectionComponent = null;
    
    /** The counter. */
    int counter = 0;

    /**
     * Constructs a new MainTrianaTask for viewing the specified task.
     *
     * @param task the task
     */
    public DaxFileTrianaTask(Task task) {
        super(task);
        this.task = task;
        if (isCollection()) {
            setCollection();
        }
    }

    /**
     * Gets the task.
     *
     * @return the task
     */
    public Task getTask() {
        return task;
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.main.imp.TrianaTask#processParameterUpdate(org.trianacode.taskgraph.event.ParameterUpdateEvent)
     */
    protected void processParameterUpdate(final ParameterUpdateEvent evt) {
        if (evt.getTask() == this.task && evt.getParameterName().equals("collection")) {
            Boolean b = (Boolean) evt.getNewValue();
            if (b) {
                setCollection();
            } else {
                if (collectionComponent != null) {
                    remove(collectionComponent);
                    collectionComponent = null;
                    invalidateSize();
                }
            }
        }
    }

    /**
     * Sets the collection.
     */
    protected void setCollection() {

        if (collectionComponent == null) {
            collectionComponent = new JLabel();
            collectionComponent.setPreferredSize(new Dimension(getWidth(), 3));
            add(collectionComponent, TrianaToolLayout.TOP);
            invalidateSize();
        }
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.main.imp.TrianaTool#drawRectangle(java.awt.Graphics, java.awt.Color, java.awt.Color)
     */
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


    /**
     * Checks if is collection.
     *
     * @return true, if is collection
     */
    private boolean isCollection() {
        Object o = getTask().getParameter("collection");
        if (o != null) {
            boolean value = (Boolean) o;
            return value;
        }
        return false;
    }
}
