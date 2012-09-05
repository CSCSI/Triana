package org.trianacode.shiwaall.gui;

import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.extensions.Extension;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 31/01/2012
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class Daxifier implements Extension {
    
    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.Extension#init(org.trianacode.gui.action.ToolSelectionHandler)
     */
    @Override
    public void init(ToolSelectionHandler selhandler) {
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.Extension#getTreeAction(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public Action getTreeAction(Tool tool) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.Extension#getWorkspaceAction(org.trianacode.taskgraph.Task)
     */
    @Override
    public Action getWorkspaceAction(Task tool) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.Extension#getWorkflowAction(int)
     */
    @Override
    public Action getWorkflowAction(int type) {
        if (type == Extension.TOOL_TYPE) {
            return (Action) new DaxifyWorkflow();
        } else return null;
    }
}
