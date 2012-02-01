package org.trianacode.gui.panels;

import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoMapping;
import org.trianacode.enactment.io.IoType;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.service.TypeChecking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 03/12/2011
 * Time: 01:20
 * To change this template use File | Settings | File Templates.
 */
public class NodeConfigPanel extends ParameterPanel {

    public static final int CANCELED = 0;
    public static final int OK = 1;
    public static final int INCOMPLETE = 2;

    int returnValue = -1;
    ArrayList<Node> nodes;
    ArrayList<NodeEntry> nodeEntries;
    private TaskGraph taskGraph;

    public NodeConfigPanel(ArrayList<Node> nodes, TaskGraph taskGraph) {
        this.nodes = nodes;
        this.taskGraph = taskGraph;
        nodeEntries = new ArrayList<NodeEntry>();
    }

    @Override
    public void init() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("The taskgraph contains unconnected input nodes."));

        for (int i = 0; i < nodes.size(); i++) {
            NodeEntry nodeEntry = new NodeEntry(nodes.get(i));
            nodeEntries.add(nodeEntry);
            panel.add(nodeEntry.getPanel());
        }
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(panel);
    }

    @Override
    public void reset() {
    }

    public void okClicked() {
        returnValue = OK;
        for (NodeEntry nodeEntry : nodeEntries) {
            boolean variableSet = nodeEntry.resolveConfig();
            if (!variableSet) {
                returnValue = INCOMPLETE;
            }
        }
    }

    public void cancelClicked() {
        returnValue = CANCELED;
    }

    @Override
    public void dispose() {
    }

    public int getStatus() {
        return returnValue;
    }

    public IoConfiguration getIOConfiguration() {
        ArrayList<IoMapping> ioMappings = new ArrayList<IoMapping>();
        for (NodeEntry nodeEntry : nodeEntries) {
            IoMapping mapping = new IoMapping(
                    new IoType(nodeEntry.getVariable(),
                            "string",
                            nodeEntry.isUrl()),
                    nodeEntry.getNode().getAbsoluteNodeIndex() + ""
            );
            ioMappings.add(mapping);

        }
        return new IoConfiguration(
                taskGraph.getQualifiedToolName(),
                "0.1",
                ioMappings,
                new ArrayList<IoMapping>()
        );
    }


    private class NodeEntry implements ActionListener {
        private Node node;
        private JTextField inlineField;
        private JTextField urlField;
        private JButton inlineFileButton;
        private JButton urlFileButton;
        private String variable;
        private boolean url;

        public NodeEntry(Node node) {
            this.node = node;

        }

        public JPanel getPanel() {
            JPanel nodeConfig = new JPanel(new GridLayout(2, 1));
            nodeConfig.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    node.getName() + " "
                            + Arrays.toString(
                            TypeChecking.classForTrianaType(
                                    node.getTask().getDataInputTypes(
                                            node.getAbsoluteNodeIndex()
                                    )
                            )
                    )));
            JPanel inlinePanel = new JPanel(new BorderLayout());
            JPanel urlPanel = new JPanel(new BorderLayout());

            ButtonGroup buttonGroup = new ButtonGroup();
            JRadioButton inlineButton = new JRadioButton("Inline String");
            inlineButton.addActionListener(this);
            inlineButton.setActionCommand("inline");
            inlineButton.setSelected(true);
            inlineField = new JTextField();
            inlineField.requestFocus();

            inlineFileButton = new JButton("...");
            inlineFileButton.addActionListener(this);
            inlineFileButton.setActionCommand("inlineFileButton");
            inlineFileButton.setEnabled(true);

            JRadioButton urlButton = new JRadioButton("File Reference - files contents will be read in");
            urlButton.addActionListener(this);
            urlButton.setActionCommand("url");
            urlField = new JTextField();
            urlField.setEnabled(false);

            urlFileButton = new JButton("...");
            urlFileButton.addActionListener(this);
            urlFileButton.setActionCommand("urlFileButton");
            urlFileButton.setEnabled(false);

            buttonGroup.add(inlineButton);
            buttonGroup.add(urlButton);

            inlinePanel.add(inlineButton, BorderLayout.NORTH);
            inlinePanel.add(inlineField, BorderLayout.CENTER);
            inlinePanel.add(inlineFileButton, BorderLayout.EAST);

            urlPanel.add(urlButton, BorderLayout.NORTH);
            urlPanel.add(urlField, BorderLayout.CENTER);
            urlPanel.add(urlFileButton, BorderLayout.EAST);

            nodeConfig.add(inlinePanel);
            nodeConfig.add(urlPanel);
            return nodeConfig;

        }

        public boolean resolveConfig() {
            if (url) {
                variable = urlField.getText();
            } else {
                variable = inlineField.getText();
            }
            return !variable.equals("");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getActionCommand().equals("inline")) {
                inlineField.setEnabled(true);
                urlField.setEnabled(false);
                inlineFileButton.setEnabled(true);
                urlFileButton.setEnabled(false);
                url = false;
            }
            if (actionEvent.getActionCommand().equals("url")) {
                inlineField.setEnabled(false);
                urlField.setEnabled(true);
                urlFileButton.setEnabled(true);
                inlineFileButton.setEnabled(false);
                url = true;
            }
            if (actionEvent.getActionCommand().equals("inlineFileButton")) {
                JFileChooser jFileChooser = new JFileChooser();
                int result = jFileChooser.showOpenDialog(GUIEnv.getApplicationFrame());
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();
                    inlineField.setText(file.toURI().getPath());
                }
            }
            if (actionEvent.getActionCommand().equals("urlFileButton")) {
                JFileChooser jFileChooser = new JFileChooser();
                int result = jFileChooser.showOpenDialog(GUIEnv.getApplicationFrame());
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();
                    urlField.setText(file.toURI().getPath());
                }
            }
        }

        public String getVariable() {
            return variable;
        }

        public boolean isUrl() {
            return url;
        }

        public Node getNode() {
            return node;
        }
    }
}

