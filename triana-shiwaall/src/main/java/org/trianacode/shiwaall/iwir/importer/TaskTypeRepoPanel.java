package org.trianacode.shiwaall.iwir.importer;

import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.extensions.Extension;
import org.trianacode.gui.panels.DisplayDialog;
import org.trianacode.shiwaall.executionServices.TaskTypeToolDescriptor;
import org.trianacode.shiwaall.iwir.execute.Executable;
import org.trianacode.shiwaall.iwir.importer.utils.TaskTypeRepo;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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
        mainPanel.add(new JLabel("TaskType  :   Triana Class : Executable"));


        String[] columnNames = {"TaskType", "Triana Class", "Executable"};
        ArrayList<Object[]> types = new ArrayList<Object[]>();

        for(TaskTypeToolDescriptor descriptor : TaskTypeRepo.getAllDescriptors()){
            String classname = "";
            Executable exec = null;
            if(descriptor.getToolClass() != null){
                classname = descriptor.getToolClass().getCanonicalName();
            }

            Object[] thisType;
            if(descriptor.getExecutable() != null){
                exec = descriptor.getExecutable();
                thisType = new Object[]{descriptor.getTasktype(), classname, exec};
            } else {
                thisType = new Object[]{descriptor.getTasktype(), classname, ""};
            }
            types.add(thisType);
        }
        Object[][] data = new Object[types.size()][3];
        types.toArray(data);

        JTable jTable = new JTable(data, columnNames);
        DefaultTableModel defaultTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int a, int b){
                return false;
            }
        };
//        jTable.setModel(defaultTableModel);
//        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable.setShowGrid(true);
//        jTable.addMouseListener(new RowListener(mainPanel));
//        jTable.setPreferredScrollableViewportSize(new Dimension(300, 70));
//        jTable.setFillsViewportHeight(true);

        JScrollPane jScrollPane = new JScrollPane(jTable);
        mainPanel.add(jScrollPane);


        DisplayDialog displayDialog = new DisplayDialog(mainPanel, "TaskType Repo", null);
    }

    private class RowListener extends MouseAdapter {

        private JPanel mainPanel;

        public RowListener(JPanel mainPanel) {
            this.mainPanel = mainPanel;
        }

        @Override
        public void mousePressed(MouseEvent event){
            JTable jTable = (JTable) event.getSource();
            int selection = jTable.getSelectedRow();


        }

    }
}
