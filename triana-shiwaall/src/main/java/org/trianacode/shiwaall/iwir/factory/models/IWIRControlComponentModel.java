package org.trianacode.shiwaall.iwir.factory.models;

import org.trianacode.gui.hci.tools.RegisterableToolComponentModel;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 11/08/2011
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
public class IWIRControlComponentModel implements RegisterableToolComponentModel {
    public static final String IWIR_CONTROL_RENDERING_HINT = "IWIR_Control";

    @Override
    public String getRegistrationString() {
        return IWIR_CONTROL_RENDERING_HINT;
    }

    @Override
    public Icon getTreeIcon(Tool tool) {
        return null;
    }

    @Override
    public String getTreeToolTip(Tool tool, boolean extended) {
        return null;
    }

    @Override
    public JPopupMenu getTreePopup(Tool tool) {
        return null;
    }

    @Override
    public String getWorkspaceToolTip(Task task, boolean extended) {
        return null;
    }

    @Override
    public JPopupMenu getWorkspacePopup(Task task) {
        return null;
    }

    @Override
    public Action getTaskAction(Task task) {
        return null;
    }

    @Override
    public TaskComponent getTaskComponent(Task task) {
        return new IWIRControlTrianaTask(task);
    }

    @Override
    public int isUpdateIcon(Task task, String action) {
        return 0;
    }

    @Override
    public Action getUpdateAction(Task task, String action) {
        return null;
    }
}
