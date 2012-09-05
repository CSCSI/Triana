package org.trianacode.shiwaall.models;

import org.trianacode.gui.main.imp.MainTrianaTask;
import org.trianacode.taskgraph.Task;

import java.awt.*;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 17/08/2011
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class ShiwaConfigTrianaTask extends MainTrianaTask {

    /**
     * Instantiates a new shiwa config triana task.
     *
     * @param task the task
     */
    public ShiwaConfigTrianaTask(Task task) {
        super(task);
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.main.imp.TrianaTool#drawRectangle(java.awt.Graphics, java.awt.Color, java.awt.Color)
     */
    @Override()
    protected void drawRectangle(Graphics graphics, Color color, Color original) {
        graphics.setColor(color);
        graphics.fill3DRect(0, 0, getSize().width, getSize().height, !isSelected());
        graphics.setColor(original);
    }
}
