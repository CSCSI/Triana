package org.trianacode.shiwa.handler;


import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.extensions.Extension;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 22/02/2011
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
public class PublishWorkflowExtension implements Extension {
    @Override
    public void init(ToolSelectionHandler selhandler) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Action getTreeAction(Tool tool) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Action getWorkspaceAction(Task tool) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Action getWorkflowAction(int type) {
        if (type == Extension.TOOL_TYPE) {
            return (Action) new PublishWorkflow152();
        } else return null;
    }
}
