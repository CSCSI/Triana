package org.trianacode.shiwa.iwir.factory.models;

import org.trianacode.gui.main.imp.MainTrianaTask;
import org.trianacode.taskgraph.Task;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 11/08/2011
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class IWIRControlTrianaTask extends MainTrianaTask {

    /**
     * Constructs a new MainTrianaTask for viewing the specified task
     */
    public IWIRControlTrianaTask(Task task) {
        super(task);
    }

    @Override()
    protected void drawRectangle(Graphics graphics, Color color, Color original) {
        graphics.setColor(color);
        graphics.fill3DRect(0, 0, getSize().width, getSize().height, !isSelected());
        graphics.setColor(original);
    }
}
