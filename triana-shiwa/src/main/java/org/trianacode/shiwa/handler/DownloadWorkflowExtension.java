package org.trianacode.shiwa.handler;

import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.extensions.Extension;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 06/07/2011
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
public class DownloadWorkflowExtension implements Extension {
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
            return (Action) new DownloadWorkflow();
        } else return null;
    }
}
