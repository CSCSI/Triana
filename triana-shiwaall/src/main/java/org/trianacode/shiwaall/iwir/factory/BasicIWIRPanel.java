package org.trianacode.shiwaall.iwir.factory;

import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.shiwaall.iwir.execute.Executable;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.proxy.java.JavaProxy;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class BasicIWIRPanel extends ParameterPanel {

    public static final String CONDITION = "condition";

    @Override
    public void init() {
        setLayout(new GridLayout(1, 1));

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));


        JLabel jLabel = new JLabel("Qualified name : " + getTask().getQualifiedTaskName());
        jPanel.add(jLabel);

        String unitString = (getTask() == null) ? "null" : ((JavaProxy) getTask().getProxy()).getFullUnitName();
                jPanel.add(new JLabel("Has unit : " + unitString));

        jPanel.add(new JLabel("Has taskType : " + getTask().getParameter(Executable.TASKTYPE)));

        JTextArea jTextArea = new JTextArea();
        jPanel.add(jTextArea);
        jTextArea.append(getDescription());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        String condition = (String) getTask().getParameter(CONDITION);
        if (condition != null) {
            System.out.println(condition);
            infoPanel.add(new JLabel("Condition : " + condition));
        }

        Executable executable = (Executable) getTask().getParameter(Executable.EXECUTABLE);
        if ((executable != null)){
            infoPanel.add(new JLabel("Executable : " + executable.getPrimaryExec()));
            infoPanel.add(new JLabel("Working Dir : " + executable.getWorkingDir().getAbsolutePath()));
            infoPanel.add(new JLabel("Args : " + Arrays.toString(executable.getArgs())));
        }

        jPanel.add(infoPanel);
        this.add(jPanel);
    }


    private String getDescription() {
        Executable executable = (Executable) getTask().getParameter(Executable.EXECUTABLE);

        String inputNodeString = "";
        String nodeName = null;
        for (Node node : getTask().getInputNodes()) {
            if ((executable != null)){
                nodeName = executable.getPorts().get(node.getName());
            }
            inputNodeString += "\n          " + node.getName() + " : (" + nodeName + ")";
        }

        String outputNodeString = "";
        for (Node node : getTask().getOutputNodes()) {
            if ((executable != null)){
                nodeName = executable.getPorts().get(node.getName());
            }
            outputNodeString += "\n          " + node.getName() + " : (" + nodeName + ")";
        }
        return "\nTask " + getTask().getDisplayName() +
                "\n     Input nodes : " + inputNodeString +
                "\n     Output nodes : " + outputNodeString;
    }

    @Override
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
