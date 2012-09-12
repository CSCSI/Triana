package org.trianacode.shiwaall.iwir.importer;

import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.extensions.Extension;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.DisplayDialog;
import org.trianacode.shiwaall.executionServices.TaskTypeToolDescriptor;
import org.trianacode.shiwaall.iwir.execute.Executable;
import org.trianacode.shiwaall.iwir.importer.utils.TaskTypeRepo;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

// TODO: Auto-generated Javadoc
/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 31/07/2012
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class TaskTypeRepoPanel extends AbstractAction implements Extension, ActionDisplayOptions {

    private Object[][] data = null;
    private TaskTypeToolDescriptor selectedDescriptor = null;

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
        showPanel();
    }

    private void showPanel() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//        mainPanel.add(new JLabel("TaskType  :   Triana Class : Executable"));


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
                thisType = new Object[]{descriptor, classname, exec};
            } else {
                thisType = new Object[]{descriptor, classname, ""};
            }
            types.add(thisType);
        }
        data = new Object[types.size()][3];
        types.toArray(data);

        JTable jTable = new JTable();
        DefaultTableModel defaultTableModel = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int a, int b){
                return false;
            }
        };
        defaultTableModel.setDataVector(data, columnNames);
        jTable.setModel(defaultTableModel);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable.setShowGrid(true);
//        jTable.setPreferredScrollableViewportSize(new Dimension(300, 70));
//        jTable.setFillsViewportHeight(true);

        JScrollPane jScrollPane = new JScrollPane(jTable);
        mainPanel.add(jScrollPane);
        JTextArea jTextArea = new JTextArea();
        jTextArea.setRows(8);

        jTable.addMouseListener(new RowListener(jTextArea));
        mainPanel.add(jTextArea);


        JButton addTool = new JButton("Add to Taskgraph");
        addTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApplicationFrame applicationFrame =
                        GUIEnv.getApplicationFrame();
                if(applicationFrame != null){
                    TaskGraph taskGraph = applicationFrame.getSelectedDesktopView().getTaskgraphPanel().getTaskGraph();
                    Tool tool = TaskTypeRepo.getToolFromDescriptor(selectedDescriptor, taskGraph.getProperties());

                    try {
                        Task task = taskGraph.createTask(tool);

                        System.out.println(selectedDescriptor.getExecutable().getPorts());

                    } catch (TaskException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        mainPanel.add(addTool);
        addTool.setEnabled(GUIEnv.getApplicationFrame() != null);

        DisplayDialog displayDialog = new DisplayDialog(mainPanel, "TaskType Repo", null);
    }

    private class RowListener extends MouseAdapter {

        private JTextArea jTextArea;

        public RowListener(JTextArea jTextArea) {
            this.jTextArea = jTextArea;
        }

        @Override
        public void mouseClicked(MouseEvent event){
            JTable jTable = (JTable) event.getSource();
            int selection = jTable.getSelectedRow();
            System.out.println(selection);

            if(selection <= (data.length -1)){
                Object[] description = data[selection];
                if(description != null && description[0] != null){
                    selectedDescriptor = (TaskTypeToolDescriptor) description[0];
                    printDescrption();
                }
                System.out.println(Arrays.deepToString(data));
            }
        }

        private void printDescrption() {
            System.out.println(selectedDescriptor.getTasktype());
            TaskTypeToolDescriptor toolDescriptor = selectedDescriptor;
            StringBuilder descriptionStrings = new StringBuilder();

            descriptionStrings.append("Tasktype : " + toolDescriptor.getTasktype() + "\n");
            descriptionStrings.append("Class : " + toolDescriptor.getToolClass() + "\n");
            descriptionStrings.append("Properties : " + toolDescriptor.getProperties() + "\n");

            if(toolDescriptor.getExecutable() != null){
                descriptionStrings.append(
                        "Executable" + toolDescriptor.getExecutable().getPrimaryExec() + "\n");
                descriptionStrings.append(
                        "Runtime folder " + toolDescriptor.getExecutable().getWorkingDir().getAbsolutePath() + "\n");
            } else {
                descriptionStrings.append("Executable : null\n");
            }

            jTextArea.setText(descriptionStrings.toString());
        }

    }

    public static void main(String[] args) {
        TaskTypeRepoPanel taskTypeRepoPanel = new TaskTypeRepoPanel();
        taskTypeRepoPanel.showPanel();
    }
}
