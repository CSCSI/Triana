package org.trianacode.shiwaall.iwir.factory;

import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.shiwaall.iwir.execute.Executable;
import org.trianacode.shiwaall.iwir.execute.ExecutableNode;
import org.trianacode.taskgraph.event.*;
import org.trianacode.taskgraph.proxy.java.JavaProxy;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class BasicIWIRPanel.
 */
public class BasicIWIRPanel extends ParameterPanel implements TaskListener{

    /** The Constant CONDITION. */
    public static final String CONDITION = "condition";

    private Executable executable = null;
    private JTextField typeField;
    private JTextField execField;
    private JTextField dirField;
    private JTextField argsField;
    private HashMap<ExecutableNode, JTextField> nodeFields;
    private JPanel mainPanel = null;

    @Override
    public void applyClicked(){

        String[] args = argsField.getText().split(" ");
        executable.setArgs(args);

        File dir = new File(dirField.getText());
        if(dir.exists() && dir.isDirectory()){
            executable.setWorkingDir(dir);
        }

        for(ExecutableNode executableNode : nodeFields.keySet()){
            JTextField field = nodeFields.get(executableNode);
            executableNode.setFilename(field.getText());
        }

        executable.setPrimaryExec(execField.getText());
        setParameter(Executable.TASKTYPE, typeField.getText());

        setParameter(Executable.EXECUTABLE, executable);

        super.applyClicked();
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.panels.ParameterPanel#init()
     */
    @Override
    public void init() {

        executable = (Executable) getTask().getParameter(Executable.EXECUTABLE);

        executable.init(getTask());

        setLayout(new GridLayout(1, 1));

        updateMainPanel();

        getTask().addTaskListener(this);


        this.add(mainPanel);
    }

    public void updateMainPanel(){
        if(mainPanel != null){
            this.remove(mainPanel);
        }

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel taskInfo = new JPanel(new GridLayout(3,1));

        JLabel jLabel = new JLabel("Qualified name : " + getTask().getQualifiedTaskName());
        taskInfo.add(jLabel);

        String unitString = (getTask() == null) ? "null" : ((JavaProxy) getTask().getProxy()).getFullUnitName();
        taskInfo.add(new JLabel("Has unit : " + unitString));

        JPanel taskTypePanel = new JPanel(new GridLayout(1,2));
        typeField = new JTextField();
        String type = (String) getTask().getParameter(Executable.TASKTYPE);
        taskTypePanel.add(new JLabel("Has taskType : "));
        typeField.setText(type);
        taskTypePanel.add(typeField);
        taskInfo.add(taskTypePanel);

        mainPanel.add(taskInfo);

        nodeFields = new HashMap<ExecutableNode, JTextField>();
        mainPanel.add(getDescriptionPanel(executable));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        String condition = (String) getTask().getParameter(CONDITION);
        if (condition != null) {
            System.out.println(condition);
            infoPanel.add(new JLabel("Condition : " + condition));
        }

        if ((executable != null)){
            JPanel execPanel = new JPanel(new GridLayout(1,2));
            execPanel.add(new JLabel("Executable : "));
            execField = new JTextField(executable.getPrimaryExec());
            execPanel.add(execField);
            infoPanel.add(execPanel);

            JPanel workingDirPanel = new JPanel(new GridLayout(1,2));
            workingDirPanel.add(new JLabel("Working Dir : "));

            dirField = new JTextField();
            String dir = "";
            if(executable.getWorkingDir() != null){
                dir = executable.getWorkingDir().getAbsolutePath();
            }
            dirField.setText(dir);
            workingDirPanel.add(dirField);
            infoPanel.add(workingDirPanel);

            JPanel argsPanel = new JPanel(new GridLayout(1,2));
            argsPanel.add(new JLabel("Args : "));
            String[] args = executable.getArgs();
            String toString = "";
            for(String s : args){
                toString += (s + " ");
            }
            argsField = new JTextField(toString);
            argsPanel.add(argsField);
            infoPanel.add(argsPanel);

//            infoPanel.add(new JLabel("Working Dir : " + executable.getWorkingDir().getAbsolutePath()));
//            infoPanel.add(new JLabel("Args : " + Arrays.toString(executable.getArgs())));
        }

        mainPanel.add(infoPanel);
        this.add(mainPanel);
        this.validate();
    }

    private JPanel getDescriptionPanel(Executable executable) {
        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.Y_AXIS));

//        if(executable == null){
//
//            JTextArea jTextArea = new JTextArea();
//            descriptionPanel.add(jTextArea);
//            jTextArea.append(getDescription());
//
//        } else {

            descriptionPanel.add(new JLabel("Inputs : "));
            addNodeDescriptions(descriptionPanel, executable.getInputNodes());

            descriptionPanel.add(new JLabel("Outputs : "));
            addNodeDescriptions(descriptionPanel, executable.getOutputNodes());

//        }

        return descriptionPanel;
    }

    private void addNodeDescriptions(JPanel descriptionPanel, ArrayList<ExecutableNode> nodes) {
        for(ExecutableNode executableNode : nodes){
            JPanel execNodePanel = new JPanel(new GridLayout(1, 3));

            JPanel nodePanel = new JPanel(new GridLayout(1,2));
            nodePanel.add(new JLabel("Node : "));
            if(executableNode.getNode() != null){
                nodePanel.add(new JLabel(executableNode.getNode().getName()));
            }
            execNodePanel.add(nodePanel);

            if(executableNode.getAbstractPort() != null) {
                execNodePanel.add(new JLabel("IWIR Port : " + executableNode.getAbstractPort().getUniqueId()));
            }

            JPanel namePanel = new JPanel(new GridLayout(1, 2));
            JLabel nameLabel = new JLabel("Filename : ");
            namePanel.add(nameLabel);
            JTextField nameField = new JTextField("");
            if(executableNode.getFilename() != null){
                nameField.setText(executableNode.getFilename());
            }
            nodeFields.put(executableNode, nameField);
            namePanel.add(nameField);
            execNodePanel.add(namePanel);

            descriptionPanel.add(execNodePanel);
        }
    }


    /**
     * Gets the description.
     *
     * @return the description
     */
//    private String getDescription() {
//        Executable executable = (Executable) getTask().getParameter(Executable.EXECUTABLE);
//
//        String inputNodeString = "";
//        String nodeName = null;
//        for (Node node : getTask().getInputNodes()) {
//            if ((executable != null)){
//                nodeName = executable.getPorts().get(node.getName());
//            }
//            inputNodeString += "\n          " + node.getName() + " : (" + nodeName + ")";
//        }
//
//        String outputNodeString = "";
//        for (Node node : getTask().getOutputNodes()) {
//            if ((executable != null)){
//                nodeName = executable.getPorts().get(node.getName());
//            }
//            outputNodeString += "\n          " + node.getName() + " : (" + nodeName + ")";
//        }
//        return "\nTask " + getTask().getDisplayName() +
//                "\n     Input nodes : " + inputNodeString +
//                "\n     Output nodes : " + outputNodeString;
//    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.panels.ParameterPanel#reset()
     */
    @Override
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.panels.ParameterPanel#dispose()
     */
    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void taskPropertyUpdate(TaskPropertyEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void parameterUpdated(ParameterUpdateEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void nodeAdded(TaskNodeEvent event) {
        executable.addExecutableNodeMapping(event.getNode(), "");
        this.setParameter(Executable.EXECUTABLE, executable);
        updateMainPanel();
    }

    @Override
    public void nodeRemoved(TaskNodeEvent event) {
        executable.removeExecutableNode(event.getNode());
        this.setParameter(Executable.EXECUTABLE, executable);
        updateMainPanel();

    }

    @Override
    public void taskDisposed(TaskDisposedEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
