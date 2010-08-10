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
package org.trianacode.gui.panels;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.ParameterNode;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;

/**
 * Editor panel to change nodes and parameter nodes for a group
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class GroupEditor extends ParameterPanel implements ActionListener {

    public static String EMPTY_LIST_STRING = "<-- None -->";

    private TaskGraph taskgraph;
    private ToolTable tools;

    private JList datain = new JList(new DefaultListModel());
    private JList dataout = new JList(new DefaultListModel());

    private JTextField controlfield = new JTextField(14);

    private JTabbedPane tabs;

    /**
     * Creates a NodeEditor.
     */
    public GroupEditor(TaskGraph taskgraph, ToolTable tools) {
        super();

        this.taskgraph = taskgraph;
        this.tools = tools;
    }


    /**
     * Hide auto-commit
     */
    public boolean isAutoCommitVisible() {
        return false;
    }


    public void init() {
        Task task = getTask();

        setName(Env.getString("GroupEditorTitle") + " " + task.getToolName());
        setLayout(new BorderLayout());

        initList(datain, true);
        initList(dataout, false);

        JPanel datainpanel = initPanel(datain);
        JPanel dataoutpanel = initPanel(dataout);
        JPanel controlpanel = initControlPanel();

        tabs = new JTabbedPane();
        tabs.setBorder(new javax.swing.border.MatteBorder(1, 1, 1, 1, getBackground()));
        tabs.setName("Input / Output NodeCable Settings");
        tabs.setAutoscrolls(true);

        tabs.addTab("In Nodes", null, datainpanel, "Set the group input nodes");
        tabs.addTab("Out Nodes", null, dataoutpanel, "Set the group output nodes");
        tabs.addTab("Control", null, controlpanel, "Set the control task for the group");

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel initPanel(JList list) {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scroll = new JScrollPane(list);
        list.setPrototypeCellValue("1234567890123456789012");
        list.setVisibleRowCount(10);

        JPanel listpanel = new JPanel(new BorderLayout());
        listpanel.add(scroll, BorderLayout.CENTER);
        listpanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        panel.add(listpanel, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(4, 1));

        JButton add = new JButton(Env.getString("add"));
        buttons.add(add);
        add.addActionListener(this);

        JButton remove = new JButton(Env.getString("remove"));
        buttons.add(remove);
        remove.addActionListener(this);

        JButton moveup = new JButton(Env.getString("moveup"));
        buttons.add(moveup);
        moveup.addActionListener(this);

        JButton movedown = new JButton(Env.getString("movedown"));
        buttons.add(movedown);
        movedown.addActionListener(this);

        JPanel buttoncont = new JPanel(new BorderLayout());
        buttoncont.add(buttons, BorderLayout.SOUTH);

        panel.add(buttoncont, BorderLayout.EAST);

        return panel;
    }


    private void initList(JList list, boolean input) {
        ((DefaultListModel) list.getModel()).clear();
        Node nodes[];

        if (input) {
            nodes = getInputNodes();

            for (int count = 0; count < nodes.length; count++) {
                if (nodes[count].isParameterNode()) {
                    ((DefaultListModel) list.getModel()).addElement(
                            taskgraph.getTask(nodes[count]).getToolName() + " [param" + nodes[count].getNodeIndex()
                                    + "-" + ((ParameterNode) nodes[count]).getParameterName() + "]");
                } else {
                    ((DefaultListModel) list.getModel()).addElement(
                            taskgraph.getTask(nodes[count]).getToolName() + " [in" + nodes[count].getNodeIndex() + "]");
                }
            }
        } else {
            nodes = getOutputNodes();

            for (int count = 0; count < nodes.length; count++) {
                if (nodes[count].isParameterNode()) {
                    ((DefaultListModel) list.getModel()).addElement(
                            taskgraph.getTask(nodes[count]).getToolName() + " [param" + nodes[count].getNodeIndex()
                                    + "-" + ((ParameterNode) nodes[count]).getParameterName() + "]");
                } else {
                    ((DefaultListModel) list.getModel()).addElement(
                            taskgraph.getTask(nodes[count]).getToolName() + " [out" + nodes[count].getNodeIndex()
                                    + "]");
                }
            }
        }
    }


    private Node[] getInputNodes() {
        Node nodes[] = getTask().getDataInputNodes();

        if (taskgraph.isControlTaskConnected()) {
            Task looptask = taskgraph.getControlTask();

            for (int count = 0; count < nodes.length; count++) {
                nodes[count] = looptask.getDataOutputNode(count + getTask().getDataOutputNodeCount()).getCable()
                        .getReceivingNode();
            }
        } else {
            for (int count = 0; count < nodes.length; count++) {
                nodes[count] = nodes[count].getParentNode();
            }
        }

        return nodes;
    }

    private Node[] getOutputNodes() {
        Node nodes[] = getTask().getDataOutputNodes();

        if (taskgraph.isControlTaskConnected()) {
            Task looptask = taskgraph.getControlTask();

            for (int count = 0; count < nodes.length; count++) {
                nodes[count] = looptask.getDataInputNode(count + getTask().getDataInputNodeCount()).getCable()
                        .getSendingNode();
            }
        } else {
            for (int count = 0; count < nodes.length; count++) {
                nodes[count] = nodes[count].getParentNode();
            }
        }

        return nodes;
    }

    private JPanel initControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(3, 0));
        panel.add(new JLabel("Control Task"), BorderLayout.WEST);
        panel.add(controlfield, BorderLayout.CENTER);

        Task task = taskgraph.getControlTask();

        if (task != null) {
            controlfield.setText(task.getQualifiedToolName());
        }

        JPanel control = new JPanel(new BorderLayout());
        control.add(panel, BorderLayout.NORTH);
        control.setBorder(new EmptyBorder(3, 3, 3, 3));

        return control;
    }


    public void reset() {
        initList(datain, true);
        initList(dataout, false);
    }

    public void dispose() {
    }


    /**
     * Called when the OK button on the unit panel is clicked. Applies node changes to the task.
     */
    public void okClicked() {
        super.okClicked();
        applyChanges();
    }

    /**
     * Called when the Apply button on the unit panel is clicked. Applies node changes to the task.
     */
    public void applyClicked() {
        super.applyClicked();
        applyChanges();
    }

    /**
     * Applies changes to the nodes and control task
     */
    private void applyChanges() {
        try {
            TaskGraphUtils.disconnectControlTask(taskgraph);
            applyNodeChanges();
            applyControlChanges();
            TaskGraphUtils.connectControlTask(taskgraph);
        } catch (TaskGraphException except) {
            except.printStackTrace();
            taskgraph.removeControlTask();
        }
    }

    /**
     * Applies changes to the control task
     */
    private void applyControlChanges() throws TaskException {
        if (controlfield.getText().equals("")) {
            taskgraph.removeControlTask();
        } else {
            Task task = taskgraph.getControlTask();

            if ((task == null) || (!task.getQualifiedToolName().equals(controlfield.getText()))) {
                Tool tool = tools.getTool(controlfield.getText());

                if (tool != null) {
                    taskgraph.createControlTask(tool, false);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Control Task: " + controlfield.getText(),
                            "Group Editor Error", JOptionPane.ERROR_MESSAGE,
                            GUIEnv.getTrianaIcon());
                }
            }

            controlfield.setText(taskgraph.getControlTask().getQualifiedToolName());
        }
    }


    /**
     * This method adds and removes input/output nodes and parameter nodes.
     */
    private void applyNodeChanges() {
        try {
            TaskGraph group = (TaskGraph) getTask();
            Node parentnode;
            Task task;
            String entry;


            Node[] groupin = group.getDataInputNodes();
            Node[] groupout = group.getDataOutputNodes();

            Enumeration enumeration = ((DefaultListModel) datain.getModel()).elements();
            int count = 0;

            while (enumeration.hasMoreElements()) {
                entry = (String) enumeration.nextElement();

                if (!entry.equals(EMPTY_LIST_STRING)) {
                    task = taskgraph.getTask(getTaskName(entry));

                    if (isParameterNode(entry)) {
                        parentnode = task.getParameterInputNode(getNodeIndex(entry));
                    } else {
                        parentnode = task.getDataInputNode(getNodeIndex(entry));
                    }

                    if ((parentnode.getChildNode() != null) && (parentnode.getChildNode().getTask() == group)) {
                        if (parentnode.getChildNode() != groupin[count]) {
                            group.swapGroupNodeParents(groupin[count], parentnode.getChildNode());
                        }
                    } else if (count >= group.getDataInputNodeCount()) {
                        group.addDataInputNode(parentnode);
                    } else {
                        group.setGroupNodeParent(groupin[count], parentnode);
                    }

                    count++;
                }
            }

            while (count < groupin.length) {
                group.removeDataInputNode(groupin[count++]);
            }

            enumeration = ((DefaultListModel) dataout.getModel()).elements();
            count = 0;

            while (enumeration.hasMoreElements()) {
                entry = (String) enumeration.nextElement();

                if (!entry.equals(EMPTY_LIST_STRING)) {
                    task = taskgraph.getTask(getTaskName(entry));

                    if (isParameterNode(entry)) {
                        parentnode = task.getParameterOutputNode(getNodeIndex(entry));
                    } else {
                        parentnode = task.getDataOutputNode(getNodeIndex(entry));
                    }

                    if ((parentnode.getChildNode() != null) && (parentnode.getChildNode().getTask() == group)) {
                        if (parentnode.getChildNode() != groupout[count]) {
                            group.swapGroupNodeParents(groupout[count], parentnode.getChildNode());
                        }
                    } else if (count >= group.getDataOutputNodeCount()) {
                        group.addDataOutputNode(parentnode);
                    } else {
                        group.setGroupNodeParent(groupout[count], parentnode);
                    }

                    count++;
                }
            }

            while (count < groupout.length) {
                group.removeDataOutputNode(groupout[count++]);
            }
        } catch (NodeException except) {
            except.printStackTrace();
        }
    }


    private String getTaskName(String entry) {
        return entry.substring(0, entry.lastIndexOf(" ["));
    }

    private int getNodeIndex(String entry) {
        if (entry.lastIndexOf("[in") > -1) {
            return Integer.parseInt(entry.substring(entry.lastIndexOf("[in") + 3, entry.lastIndexOf(']')));
        } else if (entry.lastIndexOf("[out") > -1) {
            return Integer.parseInt(entry.substring(entry.lastIndexOf("[out") + 4, entry.lastIndexOf(']')));
        } else {
            return Integer.parseInt(entry.substring(entry.lastIndexOf("[param") + 6, entry.lastIndexOf('-')));
        }
    }

    private boolean isParameterNode(String entry) {
        return (entry.indexOf("[param") > -1);
    }


    public void actionPerformed(ActionEvent event) {
        JList select;

        if (tabs.getSelectedIndex() == 0) {
            select = datain;
        } else {
            select = dataout;
        }

        if (event.getActionCommand() == Env.getString("add")) {
            addNode(select, (tabs.getSelectedIndex() % 2) == 0);
        } else if (event.getActionCommand() == Env.getString("remove")) {
            if (select.getSelectedIndex() > -1) {
                ((DefaultListModel) select.getModel()).removeElement(select.getSelectedValue());
            }

            if (select.getModel().getSize() == 0) {
                ((DefaultListModel) select.getModel()).addElement(EMPTY_LIST_STRING);
            }
        } else if ((event.getActionCommand() == Env.getString("moveup")) && (select.getSelectedIndex() > 0)) {
            String entry = (String) select.getSelectedValue();
            int index = select.getSelectedIndex();

            ((DefaultListModel) select.getModel()).removeElementAt(index);
            ((DefaultListModel) select.getModel()).insertElementAt(entry, index - 1);

            select.setSelectedIndex(index - 1);
        } else if ((event.getActionCommand() == Env.getString("movedown")) && (select.getSelectedIndex() > -1)
                && (select.getSelectedIndex() < select.getModel().getSize() - 1)) {
            String entry = (String) select.getSelectedValue();
            int index = select.getSelectedIndex();

            ((DefaultListModel) select.getModel()).removeElementAt(index);
            ((DefaultListModel) select.getModel()).insertElementAt(entry, index + 1);

            select.setSelectedIndex(index + 1);
        }
    }

    /**
     * Displays a list of available parameters, and adds the selected parameter.
     */
    private void addNode(JList list, boolean input) {
        ParameterPanel panel = new ParameterPanelImp();
        panel.setTask(getTask());
        panel.setLayout(new BorderLayout());

        JList nodelist = new JList(new DefaultListModel());
        JScrollPane scroll = new JScrollPane(nodelist, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        Task[] tasks = taskgraph.getTasks(false);
        Node[] nodes;
        String tag;
        String entry;

        for (int count = 0; count < tasks.length; count++) {
            if (input) {
                tag = "in";
                nodes = tasks[count].getDataInputNodes();
            } else {
                tag = "out";
                nodes = tasks[count].getDataOutputNodes();
            }

            for (int nodecount = 0; nodecount < nodes.length; nodecount++) {
                if (!nodes[nodecount].isConnected()) {
                    entry = taskgraph.getTask(nodes[nodecount]).getToolName() + " [" + tag
                            + nodes[nodecount].getNodeIndex() + "]";

                    if (!((DefaultListModel) list.getModel()).contains(entry)) {
                        ((DefaultListModel) nodelist.getModel()).addElement(entry);
                    }
                }
            }
        }

        for (int count = 0; count < tasks.length; count++) {
            if (input) {
                nodes = tasks[count].getParameterInputNodes();
            } else {
                nodes = tasks[count].getParameterOutputNodes();
            }

            for (int nodecount = 0; nodecount < nodes.length; nodecount++) {
                if (!nodes[nodecount].isConnected()) {
                    entry = taskgraph.getTask(nodes[nodecount]).getToolName() + " [param"
                            + nodes[nodecount].getNodeIndex() + "-"
                            + ((ParameterNode) nodes[nodecount]).getParameterName() + "]";

                    if (!((DefaultListModel) list.getModel()).contains(entry)) {
                        ((DefaultListModel) nodelist.getModel()).addElement(entry);
                    }
                }
            }
        }


        if (nodelist.getModel().getSize() == 0) {
            ((DefaultListModel) nodelist.getModel()).addElement(EMPTY_LIST_STRING);
        }

        panel.add(scroll, BorderLayout.CENTER);

        Container parent = getParent();
        while ((parent != null) && (!(parent instanceof Frame))) {
            parent = parent.getParent();
        }

        if (parent != null) {
            ParameterWindow window = new ParameterWindow((Frame) parent, WindowButtonConstants.OK_CANCEL_BUTTONS, true);

            window.setLocation(getLocationOnScreen().x + 150, getLocationOnScreen().y + 40);

            window.setParameterPanel(panel);
            window.setVisible(true);

            Object[] select = nodelist.getSelectedValues();

            for (int count = 0; count < select.length; count++) {
                if ((window.isAccepted()) && (!select[count].equals(EMPTY_LIST_STRING))) {
                    if (((DefaultListModel) list.getModel()).contains(EMPTY_LIST_STRING)) {
                        ((DefaultListModel) list.getModel()).removeElement(EMPTY_LIST_STRING);
                    }

                    ((DefaultListModel) list.getModel()).addElement(select[count]);

                    if (list.getModel().getSize() == 0) {
                        ((DefaultListModel) list.getModel()).addElement(EMPTY_LIST_STRING);
                    }
                }
            }
        }
    }

}
