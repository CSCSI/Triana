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

import org.trianacode.gui.action.FormatKeyStroke;
import org.trianacode.gui.action.tools.DecInNodeAction;
import org.trianacode.gui.action.tools.DecOutNodeAction;
import org.trianacode.gui.action.tools.IncInNodeAction;
import org.trianacode.gui.action.tools.IncOutNodeAction;
import org.trianacode.gui.util.Env;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.ParameterNode;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.TaskGraphUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;


/**
 * NodeEditor is the panel which pops up when units are double clicked which allows the input/output nodes to be edited
 * by the user at run-time.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public class NodeEditor extends ParameterPanel implements java.awt.event.ActionListener {

    public static int DEFAULT_NODE_LIMIT = 100;
    public static String EMPTY_LIST_STRING = "<-- None -->";


    private javax.swing.JComboBox outNodes;
    private javax.swing.JComboBox inNodes;

    private javax.swing.JButton addInp;
    private javax.swing.JButton removeInp;
    private javax.swing.JButton addOut;
    private javax.swing.JButton removeOut;

    private JTabbedPane all;

    private JList inList = new JList(new DefaultListModel());
    private JList outList = new JList(new DefaultListModel());

    private JPanel inputParPanel = new JPanel();
    private JPanel outputParPanel = new JPanel();
    private JPanel nodes = new JPanel();


    /**
     * Creates a NodeEditor.
     */
    public NodeEditor() {
        super();
    }

    /**
     * Hide auto-commit
     */
    public boolean isAutoCommitVisible() {
        return false;
    }


    public void init() {
        setName(Env.getString("NodeEditorTitle") + " " + getTask().getToolName());
        setLayout(new BorderLayout());

        initOutputParameterPanel();
        initInputParameterPanel();
        initNodesPanel();

        all = new JTabbedPane();
        all.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        all.setName("Input / Output Node Settings");
        all.setAutoscrolls(true);
        all.addTab(Env.getString("nodes"), null, nodes, "Select Number of Input/Output Nodes");
        all.addTab(Env.getString("inParams"), null, inputParPanel, "Select Input Nodes for Parameter Input");
        all.addTab(Env.getString("outParams"), null, outputParPanel, "Select Output Nodes for Parameter Output");

        add(all, BorderLayout.CENTER);
        all.getSelectedComponent().setVisible(true);
    }

    /**
     * Initialises the input parameter panel
     */

    public void initInputParameterPanel() {
        inputParPanel.setLayout(new BorderLayout(3, 3));

        JPanel buttonpanel = new JPanel(new GridLayout(2, 1, 0, 3));
        JPanel buttoncont = new JPanel(new BorderLayout());

        addInp = new JButton(Env.getString("add"));
        addInp.addActionListener(this);
        buttonpanel.add(addInp);

        removeInp = new JButton(Env.getString("remove"));
        removeInp.addActionListener(this);
        buttonpanel.add(removeInp);

        buttoncont.add(buttonpanel, BorderLayout.SOUTH);

        inputParPanel.add(buttoncont, BorderLayout.EAST);

        if (getTask().getParameterInputNodeCount() == 0) {
            ((DefaultListModel) inList.getModel()).addElement(EMPTY_LIST_STRING);
        } else {
            ParameterNode[] inparams = getTask().getParameterInputNodes();
            for (int count = 0; count < inparams.length; ++count) {
                ((DefaultListModel) inList.getModel()).addElement(new ParameterListElement(inparams[count]));
            }
        }

        inList.setPrototypeCellValue("123456789012345");

        JScrollPane scroll = new JScrollPane(inList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        inputParPanel.add(new JLabel("Input Parameters"), BorderLayout.NORTH);
        inputParPanel.add(scroll, BorderLayout.CENTER);
        inputParPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    /**
     * Initialises the output parameter panel
     */

    public void initOutputParameterPanel() {
        outputParPanel.setLayout(new BorderLayout(3, 3));

        JPanel buttonpanel = new JPanel(new GridLayout(2, 1, 0, 3));
        JPanel buttoncont = new JPanel(new BorderLayout());

        addOut = new JButton(Env.getString("add"));
        addOut.addActionListener(this);
        buttonpanel.add(addOut);

        removeOut = new JButton(Env.getString("remove"));
        removeOut.addActionListener(this);
        buttonpanel.add(removeOut);

        buttoncont.add(buttonpanel, BorderLayout.SOUTH);

        outputParPanel.add(buttoncont, BorderLayout.EAST);

        if (getTask().getParameterOutputNodeCount() == 0) {
            ((DefaultListModel) outList.getModel()).addElement(EMPTY_LIST_STRING);
        } else {
            ParameterNode[] outparams = getTask().getParameterOutputNodes();
            for (int count = 0; count < outparams.length; ++count) {
                ((DefaultListModel) outList.getModel()).addElement(new ParameterListElement(outparams[count]));
            }
        }

        outList.setPrototypeCellValue("12345678901234567890");
        outList.setVisibleRowCount(8);

        JScrollPane scroll = new JScrollPane(outList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outputParPanel.add(new JLabel("Output Parameters"), BorderLayout.NORTH);
        outputParPanel.add(scroll, BorderLayout.CENTER);
        outputParPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    /**
     * Changes the NodeCable Editor user interface to show 'out' output nodes selected
     */
    public void outSelect(int out) {
        outNodes.setSelectedItem(String.valueOf(out));
    }

    public void initNodesPanel() {
        Task task = getTask();

        nodes.removeAll();
        nodes.setLayout(new BorderLayout());

        inNodes = new JComboBox();
        outNodes = new JComboBox();

        for (int i = task.getMinDataInputNodes(); i <= Math.min(task.getMaxDataInputNodes(), DEFAULT_NODE_LIMIT); ++i) {
            inNodes.addItem(String.valueOf(i));
        }

        inNodes.setSelectedItem(String.valueOf(task.getDataInputNodeCount()));

        if (task.getMinDataInputNodes() == task.getMaxDataInputNodes()) {
            inNodes.setEnabled(false);
        }

        for (int i = task.getMinDataOutputNodes(); i <= Math.min(task.getMaxDataOutputNodes(), DEFAULT_NODE_LIMIT);
             ++i) {
            outNodes.addItem(String.valueOf(i));
        }

        outNodes.setSelectedItem(String.valueOf(task.getDataOutputNodeCount()));

        if (task.getMinDataOutputNodes() == task.getMaxDataOutputNodes()) {
            outNodes.setEnabled(false);
        }

        JPanel choiceBoxes = new JPanel(new FlowLayout());

        JLabel input = new JLabel(Env.getString("InputNodes"), JLabel.CENTER);
        JLabel output = new JLabel(Env.getString("OutputNodes"), JLabel.CENTER);

        choiceBoxes.add(input);
        choiceBoxes.add(inNodes);
        choiceBoxes.add(outNodes);
        choiceBoxes.add(output);

        nodes.add(choiceBoxes, BorderLayout.NORTH);
        //nodes.add(getAcceleratorPanel(), BorderLayout.CENTER);
        nodes.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    /**
     * @return a label panel with the shortcut keys on it
     */
    private JPanel getAcceleratorPanel() {
        JPanel accPanel = new JPanel(new GridLayout(3, 3, 5, 5));

        accPanel.add(new JLabel(""));
        String label = Env.getString("InputNodes");
        accPanel.add(new JLabel(label));
        label = Env.getString("OutputNodes");
        ;
        accPanel.add(new JLabel(label));
        label = Env.getString("increment");
        accPanel.add(new JLabel(label));
        label = FormatKeyStroke
                .keyStroke2String((KeyStroke) (new IncInNodeAction(null)).getValue(AbstractAction.ACCELERATOR_KEY));
        accPanel.add(new JLabel(label));
        label = FormatKeyStroke
                .keyStroke2String((KeyStroke) (new IncOutNodeAction(null)).getValue(AbstractAction.ACCELERATOR_KEY));
        accPanel.add(new JLabel(label));
        label = Env.getString("decrement");
        accPanel.add(new JLabel(label));
        label = FormatKeyStroke
                .keyStroke2String((KeyStroke) (new DecInNodeAction(null)).getValue(AbstractAction.ACCELERATOR_KEY));
        accPanel.add(new JLabel(label));
        label = FormatKeyStroke
                .keyStroke2String((KeyStroke) (new DecOutNodeAction(null)).getValue(AbstractAction.ACCELERATOR_KEY));
        accPanel.add(new JLabel(label));

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(new JLabel(Env.getString("shortcuts"), JLabel.CENTER), BorderLayout.NORTH);
        JPanel inner = new JPanel(new BorderLayout());
        inner.add(accPanel, BorderLayout.NORTH);
        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    public void reset() {
    }


    /**
     * Called when the OK button on the unit panel is clicked. Applies node changes to the task.
     */
    public void okClicked() {
        super.okClicked();
        applyNodeChanges();
    }

    /**
     * Called when the Apply button on the unit panel is clicked. Applies node changes to the task.
     */
    public void applyClicked() {
        super.applyClicked();
        applyNodeChanges();
    }


    /**
     * This method adds and removes input/output nodes and parameter nodes.
     */
    private void applyNodeChanges() {
        try {
            TaskGraphUtils.disconnectControlTask(getTask().getParent());

            if (getTask().getMaxDataInputNodes() != getTask().getMinDataOutputNodes()) {
                int nodecount = Integer.parseInt((String) inNodes.getSelectedItem());

                while (getTask().getDataInputNodeCount() < nodecount) {
                    getTask().addDataInputNode();
                }

                while (getTask().getDataInputNodeCount() > nodecount) {
                    getTask().removeDataInputNode(getTask().getDataInputNode(getTask().getDataInputNodeCount() - 1));
                }
            }

            // Add or remove output nodes as required

            if (getTask().getMaxDataOutputNodes() != getTask().getMinDataOutputNodes()) {
                int nodecount = Integer.parseInt((String) outNodes.getSelectedItem());

                while (getTask().getDataOutputNodeCount() < nodecount) {
                    getTask().addDataOutputNode();
                }

                while (getTask().getDataOutputNodeCount() > nodecount) {
                    getTask().removeDataOutputNode(getTask().getDataOutputNode(getTask().getDataOutputNodeCount() - 1));
                }
            }

            // Set input parameter names, adding or removing nodes as required

            DefaultListModel model = ((DefaultListModel) inList.getModel());
            ParameterListElement elem;

            for (int count = 0; count < model.getSize(); count++) {
                if (!model.elementAt(count).equals(EMPTY_LIST_STRING)) {
                    elem = (ParameterListElement) model.elementAt(count);

                    if (count < getTask().getParameterInputNodeCount()) {
                        getTask().getParameterInputNode(count).setParameterName(elem.getParameterName());
                    } else {
                        getTask().addParameterInputNode(elem.getParameterName());
                    }

                    if (elem.isTrigger()) {
                        getTask().getParameterInputNode(count).setTriggerNode(true);
                    }
                }
            }

            while (getTask().getParameterInputNodeCount() > model.getSize()) {
                getTask().removeParameterInputNode(
                        getTask().getParameterInputNode(getTask().getParameterInputNodeCount() - 1));
            }

            if (model.contains(EMPTY_LIST_STRING) && (getTask().getParameterInputNodeCount() == 1)) {
                getTask().removeParameterInputNode(getTask().getParameterInputNode(0));
            }

            // Set output parameter names, adding or removing nodes as required

            model = ((DefaultListModel) outList.getModel());

            for (int count = 0; count < model.getSize(); count++) {
                if (!model.elementAt(count).equals(EMPTY_LIST_STRING)) {
                    elem = (ParameterListElement) model.elementAt(count);

                    if (count < getTask().getParameterOutputNodeCount()) {
                        getTask().getParameterOutputNode(count).setParameterName(elem.getParameterName());
                    } else {
                        getTask().addParameterOutputNode(elem.getParameterName());
                    }
                }
            }

            while (getTask().getParameterOutputNodeCount() > model.getSize()) {
                getTask().removeParameterOutputNode(
                        getTask().getParameterOutputNode(getTask().getParameterOutputNodeCount() - 1));
            }

            if (model.contains(EMPTY_LIST_STRING) && (getTask().getParameterOutputNodeCount() == 1)) {
                getTask().removeParameterOutputNode(getTask().getParameterOutputNode(0));
            }

            TaskGraphUtils.connectControlTask(getTask().getParent());
        } catch (TaskGraphException except) {
            except.printStackTrace();
            getTask().getParent().removeControlTask();
        }

        String incount = String.valueOf(getTask().getDataInputNodeCount());

        if (((DefaultComboBoxModel) inNodes.getModel()).getIndexOf(incount) == -1) {
            ((DefaultComboBoxModel) inNodes.getModel()).addElement(incount);
        }

        inNodes.setSelectedItem(incount);

        String outcount = String.valueOf(getTask().getDataOutputNodeCount());

        if (((DefaultComboBoxModel) outNodes.getModel()).getIndexOf(outcount) == -1) {
            ((DefaultComboBoxModel) outNodes.getModel()).addElement(outcount);
        }

        outNodes.setSelectedItem(outcount);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addInp) {
            addParameter(inList, true);
        } else if (e.getSource() == removeInp) {
            if (inList.getSelectedIndex() > -1) {
                Object[] selected = inList.getSelectedValues();

                for (int count = 0; count < selected.length; count++) {
                    ((DefaultListModel) inList.getModel()).removeElement(selected[count]);
                }
            }

            if (inList.getModel().getSize() == 0) {
                ((DefaultListModel) inList.getModel()).addElement(EMPTY_LIST_STRING);
            }
        } else if (e.getSource() == addOut) {
            addParameter(outList, false);
        } else if (e.getSource() == removeOut) {
            if (outList.getSelectedIndex() > -1) {
                Object[] selected = outList.getSelectedValues();

                for (int count = 0; count < selected.length; count++) {
                    ((DefaultListModel) outList.getModel()).removeElement(selected[count]);
                }
            }

            if (outList.getModel().getSize() == 0) {
                ((DefaultListModel) outList.getModel()).addElement(EMPTY_LIST_STRING);
            }
        }
    }

    /**
     * Displays a list of available parameters, and adds the selected parameter.
     */
    private void addParameter(JList list, boolean input) {
        AddParameterNodePanel panel;

        if (input) {
            String[] existlist = new String[0];

            if (!((DefaultListModel) list.getModel()).contains(EMPTY_LIST_STRING)) {
                existlist = new String[((DefaultListModel) list.getModel()).size()];
                Enumeration enumeration = ((DefaultListModel) list.getModel()).elements();

                for (int count = 0; count < existlist.length; count++) {
                    existlist[count] = ((ParameterListElement) enumeration.nextElement()).getParameterName();
                }
            }

            panel = new AddParameterNodePanel(existlist);
        } else {
            panel = new AddParameterNodePanel(false);
        }

        panel.setTask(getTask());
        panel.init();

        ParameterWindow window = new ParameterWindow(this, WindowButtonConstants.OK_CANCEL_BUTTONS, true);
        window.setLocation(getLocationOnScreen().x + 150, getLocationOnScreen().y + 40);
        window.setParameterPanel(panel);
        window.setTitle(Env.getString("addParameterNode"));
        window.setVisible(true);

        if (window.isAccepted()) {
            if (((DefaultListModel) list.getModel()).contains(EMPTY_LIST_STRING)) {
                ((DefaultListModel) list.getModel()).removeElement(EMPTY_LIST_STRING);
            }

            if (panel.isParameterNode()) {
                String[] selected = panel.getParameterNames();

                for (int count = 0; count < selected.length; count++) {
                    ((DefaultListModel) list.getModel())
                            .addElement(new ParameterListElement(selected[count], panel.isTriggerNode()));
                }
            } else {
                ((DefaultListModel) list.getModel())
                        .addElement(new ParameterListElement(ParameterNode.TRIGGER_PARAM, panel.isTriggerNode()));
            }

            if (((DefaultListModel) list.getModel()).size() == 0) {
                ((DefaultListModel) list.getModel()).addElement(EMPTY_LIST_STRING);
            }
        }
    }

    /**
     * Called when the panel is finished with.
     */
    public void dispose() {
    }


    public class ParameterListElement {

        private String paramname;
        private boolean trigger;


        public ParameterListElement(ParameterNode node) {
            paramname = node.getParameterName();
            trigger = node.isTriggerNode();
        }

        public ParameterListElement(String paramname, boolean trigger) {
            this.paramname = paramname;
            this.trigger = trigger;
        }


        public String getParameterName() {
            return paramname;
        }

        public boolean isTrigger() {
            return trigger;
        }


        public String toString() {
            if (isTrigger() && paramname.equals(ParameterNode.TRIGGER_PARAM)) {
                return "[TRIGGER]";
            } else if (isTrigger()) {
                return paramname + " [TRIGGER]";
            } else {
                return paramname;
            }
        }
    }

}
