package org.trianacode.pegasus.gui;

import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.extensions.Extension;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 31/01/2012
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class Daxifier implements Extension {
    @Override
    public void init(ToolSelectionHandler selhandler) {
    }

    @Override
    public Action getTreeAction(Tool tool) {
        return null;
    }

    @Override
    public Action getWorkspaceAction(Task tool) {
        return null;
    }

    @Override
    public Action getWorkflowAction(int type) {
        if (type == Extension.TOOL_TYPE) {
            return (Action) new DaxifyWorkflow();
        } else return null;
    }
}
