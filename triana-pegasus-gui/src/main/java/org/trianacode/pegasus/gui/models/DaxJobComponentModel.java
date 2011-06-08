package org.trianacode.pegasus.gui.models;

import org.trianacode.gui.hci.tools.RegisterableToolComponentModel;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Sep 10, 2010
 * Time: 2:44:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxJobComponentModel implements RegisterableToolComponentModel {

    public static final String DAX_JOB_RENDERING_HINT = "DAX Job";

    @Override
    public String getRegistrationString() {
        return DAX_JOB_RENDERING_HINT;
    }

    @Override
    public Icon getTreeIcon(Tool tool) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getTreeToolTip(Tool tool, boolean extended) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JPopupMenu getTreePopup(Tool tool) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getWorkspaceToolTip(Task task, boolean extended) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JPopupMenu getWorkspacePopup(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Action getTaskAction(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TaskComponent getTaskComponent(Task task) {
        return new DaxJobTrianaTask(task);
    }

    @Override
    public int isUpdateIcon(Task task, String action) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Action getUpdateAction(Task task, String action) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
