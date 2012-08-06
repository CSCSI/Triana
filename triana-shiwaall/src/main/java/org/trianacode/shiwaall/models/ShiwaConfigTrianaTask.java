package org.trianacode.shiwaall.models;

import org.trianacode.gui.main.imp.MainTrianaTask;
import org.trianacode.taskgraph.Task;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 17/08/2011
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class ShiwaConfigTrianaTask extends MainTrianaTask {

    public ShiwaConfigTrianaTask(Task task) {
        super(task);
    }

    @Override()
    protected void drawRectangle(Graphics graphics, Color color, Color original) {
        graphics.setColor(color);
        graphics.fill3DRect(0, 0, getSize().width, getSize().height, !isSelected());
        graphics.setColor(original);
    }
}
