package org.trianacode.shiwa.iwir.factory;

import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.taskgraph.Node;

import javax.swing.*;
import java.awt.*;

public class BasicIWIRPanel extends ParameterPanel {

    public static final String CONDITION = "condition";

    @Override
    public void init() {
        setLayout(new GridLayout(1, 1));

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(4, 1));

        JLabel jLabel = new JLabel("Qualified name : " + getTask().getQualifiedTaskName());
        jPanel.add(jLabel);

        jPanel.add(new JLabel("Has task : " + (getTask() != null)));

        JTextArea jTextArea = new JTextArea();
        jPanel.add(jTextArea);
        jTextArea.append(getDescription());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 1));

        String condition = (String) getTask().getParameter(CONDITION);
        if (condition != null) {
            System.out.println(condition);
            infoPanel.add(new JLabel(condition));
        }

        jPanel.add(infoPanel);
        this.add(jPanel);
    }


    private String getDescription() {
        String inputNodeString = "";
        for (Node node : getTask().getInputNodes()) {
            inputNodeString += "\n" + node.getName();
        }
        String outputNodeString = "";
        for (Node node : getTask().getOutputNodes()) {
            outputNodeString += "\n" + node.getName();
        }
        String string = "\nTask " + getTask().getDisplayName() +
                "\n     Input nodes : " + inputNodeString +
                "\n     Output nodes : " + outputNodeString;
        return string;
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
