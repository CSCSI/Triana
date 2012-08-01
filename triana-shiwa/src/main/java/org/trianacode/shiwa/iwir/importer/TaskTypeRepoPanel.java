package org.trianacode.shiwa.iwir.importer;

import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.extensions.Extension;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.DisplayDialog;
import org.trianacode.shiwa.executionServices.TaskTypeToolDescriptor;
import org.trianacode.shiwa.iwir.importer.utils.TaskTypeRepo;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 31/07/2012
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class TaskTypeRepoPanel extends AbstractAction implements Extension, ActionDisplayOptions {

    public TaskTypeRepoPanel(){
        this(DISPLAY_BOTH);
    }

    public TaskTypeRepoPanel(int displayOption){
        putValue(SHORT_DESCRIPTION, "TaskType Repository");
        putValue(NAME, "TaskType Repository");
        if ((displayOption == DISPLAY_ICON) || (displayOption == DISPLAY_BOTH)) {
            putValue(SMALL_ICON, GUIEnv.getIcon("upload_small.png"));
        }
    }

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
            return this;
        } else return null;
    }

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

        DisplayDialog displayDialog = new DisplayDialog(mainPanel, "TaskType Repo");
    }
}
