/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */
package org.trianacode.gui.action.taskgraph;

import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoMapping;
import org.trianacode.enactment.io.IoType;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.hci.MenuMnemonics;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.util.Env;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.service.ClientException;
import org.trianacode.taskgraph.service.TrianaClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Action class to handle all "run" actions.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public class RunAction extends AbstractAction implements ActionDisplayOptions {

    ToolSelectionHandler selhandler;

    public RunAction(ToolSelectionHandler selhandler) {
        this(selhandler, DISPLAY_BOTH);
    }

    public RunAction(ToolSelectionHandler selhandler, int displayOption, JMenu parentMenu) {
        this(selhandler, displayOption);
        char mnem = MenuMnemonics.getInstance().getNextMnemonic(parentMenu, Env.getString("Run"));
        putValue(MNEMONIC_KEY, new Integer(mnem));
    }

    public RunAction(ToolSelectionHandler selhandler, int displayOption) {
        super();
        this.selhandler = selhandler;
        putValue(SHORT_DESCRIPTION, Env.getString("RunTip"));
        putValue(ACTION_COMMAND_KEY, Env.getString("Run"));
        if ((displayOption == DISPLAY_ICON) || (displayOption == DISPLAY_BOTH)) {
            putValue(SMALL_ICON, GUIEnv.getIcon("play.png"));
        }
        if ((displayOption == DISPLAY_NAME) || (displayOption == DISPLAY_BOTH)) {
            putValue(NAME, Env.getString("Run"));
        }
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        TrianaClient client = selhandler.getSelectedTrianaClient();
        if (client != null) {
            ArrayList<Node> nodeArrayList = getUnconnectedInputs();
            if (nodeArrayList.size() > 0) {
                runWithInputs(client, nodeArrayList);
            } else {
                try {
                    client.run();
                } catch (ClientException e1) {
                    new ErrorDialog(e1.getMessage());
                }
            }
        }
    }

    private ArrayList<Node> getUnconnectedInputs() {
        ArrayList<Node> nodeArrayList = new ArrayList<Node>();
        TaskGraph taskGraph = selhandler.getSelectedTaskgraph();
        for (Node node : taskGraph.getUltimateParent().getInputNodes()) {
            if (!node.isConnected()) {
                nodeArrayList.add(node);
            }
        }
        return nodeArrayList;
    }

    private void runWithInputs(TrianaClient client, ArrayList<Node> nodeArrayList) {
        NodeConfigPanel configPanel = createPanel(nodeArrayList, selhandler.getSelectedTaskgraph().getUltimateParent());
        showConfigWindow(configPanel);
        boolean useConfig = configPanel.getStatus() == NodeConfigPanel.OK;
        if (useConfig) {
            IoConfiguration configuration = configPanel.getIOConfiguration();
            //         run(client, configuration);
            try {
                client.run(configuration);
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
    }

    private NodeConfigPanel createPanel(ArrayList<Node> nodeArrayList, TaskGraph taskGraph) {
        NodeConfigPanel nodeConfigPanel = new NodeConfigPanel(nodeArrayList, taskGraph);
        nodeConfigPanel.init();
        return nodeConfigPanel;
    }

    private void showConfigWindow(NodeConfigPanel nodeConfigPanel) {
        ParameterWindow parameterWindow = new ParameterWindow(GUIEnv.getApplicationFrame(),
                WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS, true);
        parameterWindow.setTitle("InputNode Configuration");
        parameterWindow.setParameterPanel(nodeConfigPanel);

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - parameterWindow.getWidth()) / 2;
        int y = (screenSize.height - parameterWindow.getHeight()) / 2;
        parameterWindow.setLocation(x, y);

        parameterWindow.setVisible(true);
        parameterWindow.requestFocus();
    }


    private class NodeConfigPanel extends ParameterPanel {

        public static final int CANCELED = 0;
        public static final int OK = 1;
        public static final int INCOMPLETE = 2;

        int returnValue = -1;
        ArrayList<Node> nodes;
        ArrayList<NodeEntry> nodeEntries;
        private TaskGraph taskGraph;

        NodeConfigPanel(ArrayList<Node> nodes, TaskGraph taskGraph) {
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
    }

    private class NodeEntry implements ActionListener {
        private Node node;
        private JTextField inlineField;
        private JTextField urlField;
        private JButton fileButton;
        private String variable;
        private boolean url;

        public NodeEntry(Node node) {
            this.node = node;

        }

        public JPanel getPanel() {
            JPanel nodeConfig = new JPanel(new GridLayout(2, 1));
            nodeConfig.setBorder(javax.swing.BorderFactory.createTitledBorder(node.getName()));
            JPanel inlinePanel = new JPanel(new BorderLayout());
            JPanel stringPanel = new JPanel(new BorderLayout());

            ButtonGroup buttonGroup = new ButtonGroup();
            JRadioButton inlineButton = new JRadioButton("Inline");
            inlineButton.addActionListener(this);
            inlineButton.setActionCommand("inline");
            inlineButton.setSelected(true);
            inlineField = new JTextField();

            JRadioButton stringButton = new JRadioButton("URL");
            stringButton.addActionListener(this);
            stringButton.setActionCommand("url");
            urlField = new JTextField();
            urlField.setEnabled(false);
            fileButton = new JButton("...");
            fileButton.addActionListener(this);
            fileButton.setActionCommand("fileButton");
            fileButton.setEnabled(false);

            buttonGroup.add(inlineButton);
            buttonGroup.add(stringButton);

            inlinePanel.add(inlineButton, BorderLayout.WEST);
            inlinePanel.add(inlineField, BorderLayout.CENTER);

            stringPanel.add(stringButton, BorderLayout.WEST);
            stringPanel.add(urlField, BorderLayout.CENTER);
            stringPanel.add(fileButton, BorderLayout.EAST);

            nodeConfig.add(inlinePanel);
            nodeConfig.add(stringPanel);
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
                fileButton.setEnabled(false);
                url = false;
            }
            if (actionEvent.getActionCommand().equals("url")) {
                inlineField.setEnabled(false);
                urlField.setEnabled(true);
                fileButton.setEnabled(true);
                url = true;
            }
            if (actionEvent.getActionCommand().equals("fileButton")) {
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
