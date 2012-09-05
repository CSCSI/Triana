package org.trianacode.shiwaall.iwir.importer;

import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.extensions.Extension;
import org.trianacode.gui.panels.DisplayDialog;
import org.trianacode.shiwaall.executionServices.TaskTypeToolDescriptor;
import org.trianacode.shiwaall.iwir.importer.utils.TaskTypeRepo;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.awt.event.ActionEvent;

// TODO: Auto-generated Javadoc
/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 31/07/2012
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class TaskTypeRepoPanel extends AbstractAction implements Extension, ActionDisplayOptions {

    /**
     * Instantiates a new task type repo panel.
     */
    public TaskTypeRepoPanel(){
        this(DISPLAY_BOTH);
    }

    /**
     * Instantiates a new task type repo panel.
     *
     * @param displayOption the display option
     */
    public TaskTypeRepoPanel(int displayOption){
        putValue(SHORT_DESCRIPTION, "TaskType Repository");
        putValue(NAME, "TaskType Repository");
        if ((displayOption == DISPLAY_ICON) || (displayOption == DISPLAY_BOTH)) {
        }
    }

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
            return this;
        } else return null;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        for(TaskTypeToolDescriptor descriptor : TaskTypeRepo.getAllDescriptors()){
            String classname = "";
            if(descriptor.getToolClass() != null){
                classname = descriptor.getToolClass().getCanonicalName();
            }
            mainPanel.add(new JLabel(descriptor.getTasktype() + " " + classname));
        }

        DisplayDialog displayDialog = new DisplayDialog(mainPanel, "TaskType Repo", null);
    }
}
