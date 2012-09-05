package org.trianacode.shiwaall.gui.models;

import org.trianacode.gui.hci.tools.RegisterableToolComponentModel;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Sep 9, 2010
 * Time: 2:53:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxFileComponentModel implements RegisterableToolComponentModel {

    /** The Constant DAX_FILE_RENDERING_HINT. */
    public static final String DAX_FILE_RENDERING_HINT = "DAX File";

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.tools.ToolComponentModel#getTreeIcon(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public Icon getTreeIcon(Tool tool) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.tools.ToolComponentModel#getTreeToolTip(org.trianacode.taskgraph.tool.Tool, boolean)
     */
    @Override
    public String getTreeToolTip(Tool tool, boolean extended) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.tools.ToolComponentModel#getTreePopup(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public JPopupMenu getTreePopup(Tool tool) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.tools.ToolComponentModel#getWorkspaceToolTip(org.trianacode.taskgraph.Task, boolean)
     */
    @Override
    public String getWorkspaceToolTip(Task task, boolean extended) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.tools.ToolComponentModel#getWorkspacePopup(org.trianacode.taskgraph.Task)
     */
    @Override
    public JPopupMenu getWorkspacePopup(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.tools.ToolComponentModel#getTaskAction(org.trianacode.taskgraph.Task)
     */
    @Override
    public Action getTaskAction(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.tools.ToolComponentModel#getTaskComponent(org.trianacode.taskgraph.Task)
     */
    @Override
    public TaskComponent getTaskComponent(Task task) {
        return new DaxFileTrianaTask(task);
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.tools.ToolComponentModel#isUpdateIcon(org.trianacode.taskgraph.Task, java.lang.String)
     */
    @Override
    public int isUpdateIcon(Task task, String action) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.tools.ToolComponentModel#getUpdateAction(org.trianacode.taskgraph.Task, java.lang.String)
     */
    @Override
    public Action getUpdateAction(Task task, String action) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.tools.RegisterableToolComponentModel#getRegistrationString()
     */
    @Override
    public String getRegistrationString() {
        return DAX_FILE_RENDERING_HINT;
    }
}
