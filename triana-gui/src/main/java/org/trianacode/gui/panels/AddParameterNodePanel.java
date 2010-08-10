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
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.ParameterNode;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.util.Env;

/**
 * A panel that allows you to add parameter nodes to a task. The nodes can either be parameter only, parameter+trigger,
 * clip-in only, clip-in+trigger or trigger only.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class AddParameterNodePanel extends ParameterPanel implements ItemListener {

    public static String EMPTY_LIST_STRING = "<-- None -->";

    private JList paramlist = new JList(new DefaultListModel());

    private JTextField clipinname = new JTextField(20);

    private JRadioButton paramnode;
    private JRadioButton paramtriggernode;
    private JRadioButton clipinnode;
    private JRadioButton clipintriggernode;
    private JRadioButton triggernode;

    /**
     * a flag indicating whether an input node or output node is being added
     */
    private boolean input;

    /**
     * the names of previously added parameter input nodes
     */
    private ArrayList innames;

    /**
     * Creates an add parameter node panel for input/output nodes. If input nodes is chosen then the names of previously
     * added parameter input nodes are excluded for being added again.
     */
    public AddParameterNodePanel(boolean input) {
        this.input = input;
    }


    /**
     * Creates an add parameter node panel for input nodes, excluding the specified names of parameter nodes previously
     * added
     */
    public AddParameterNodePanel(String[] paramnames) {
        this.input = true;
        this.innames = new ArrayList();

        for (int count = 0; count < paramnames.length; count++) {
            innames.add(paramnames[count]);
        }
    }


    /**
     * @return true if the node is a parameter node (as opposed to a clip-in /trigger only node)
     */
    public boolean isParameterNode() {
        return (!input) || paramnode.isSelected() || paramtriggernode.isSelected();
    }

    /**
     * @return true if the node is a parameter node (as opposed to a clip-in /trigger only node)
     */
    public boolean isClipInNode() {
        return input && (clipinnode.isSelected() || clipintriggernode.isSelected());
    }

    /**
     * @return true if the node is a trigger node
     */
    public boolean isTriggerNode() {
        return input && (paramtriggernode.isSelected() || clipintriggernode.isSelected() || triggernode.isSelected());
    }

    /**
     * @return an array of the parameter nodes being added (null if a clip-in/ trigger only node)
     */
    public String[] getParameterNames() {
        if (!isParameterNode()) {
            return null;
        } else if (((DefaultListModel) paramlist.getModel()).contains(EMPTY_LIST_STRING)) {
            return new String[0];
        } else {
            String[] copy = new String[paramlist.getSelectedValues().length];
            System.arraycopy(paramlist.getSelectedValues(), 0, copy, 0, copy.length);
            return copy;
        }
    }

    /**
     * @return the name of the clip in node (null if a parameter/trigger only node)
     */
    public String getClipInName() {
        return clipinname.getText();
    }


    /**
     * This method returns WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS by default. It should be overridden if the
     * panel has different preferred set of buttons.
     *
     * @return the panels preferred button combination (as defined in Windows Constants).
     * @see WindowButtonConstants
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_CANCEL_BUTTONS;
    }

    /**
     * This method returns true by default. It should be overridden if the panel does not want the user to be able to
     * change the auto commit state
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    /**
     * This method is called when the task is set for this panel. It is overridden to create the panel layout.
     */
    public void init() {
        setLayout(new BorderLayout(3, 3));

        paramlist.setPrototypeCellValue("0123456789012345678901234");
        paramlist.setVisibleRowCount(8);

        JScrollPane scroll = new JScrollPane(paramlist, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        String[] paramnames = getAddParameters();

        for (int count = 0; count < paramnames.length; count++) {
            ((DefaultListModel) paramlist.getModel()).addElement(paramnames[count]);
        }

        if (paramnames.length == 0) {
            ((DefaultListModel) paramlist.getModel()).addElement(EMPTY_LIST_STRING);
        }

        if (input) {
            initInputPanel(scroll);
        } else {
            add(new JLabel("Parameters"), BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
        }
    }


    private void initInputPanel(JScrollPane scroll) {
        JPanel scrollPanel = new JPanel(new BorderLayout());
        scrollPanel.add(scroll, BorderLayout.CENTER);
        scrollPanel.setBorder(new EmptyBorder(0, 30, 0, 0));
        add(scrollPanel, BorderLayout.CENTER);

        paramnode = new JRadioButton(Env.getString("parameterNode"));
        paramtriggernode = new JRadioButton(Env.getString("parameterTriggerNode"));
        clipinnode = new JRadioButton(Env.getString("clipinNode"));
        clipintriggernode = new JRadioButton(Env.getString("clipinTriggerNode"));
        triggernode = new JRadioButton(Env.getString("triggerNode"));

        ButtonGroup group = new ButtonGroup();
        group.add(paramnode);
        group.add(paramtriggernode);
        group.add(clipinnode);
        group.add(clipintriggernode);
        group.add(triggernode);

        paramnode.addItemListener(this);
        paramtriggernode.addItemListener(this);
        clipinnode.addItemListener(this);
        clipintriggernode.addItemListener(this);
        triggernode.addItemListener(this);
        paramnode.setSelected(true);

        JPanel north = new JPanel(new BorderLayout());
        north.add(paramnode, BorderLayout.WEST);

        JPanel cont1 = new JPanel(new GridLayout(2, 1));
        cont1.add(paramtriggernode);
        cont1.add(triggernode);

        JPanel south1 = new JPanel(new BorderLayout());
        south1.add(cont1, BorderLayout.NORTH);

        JPanel south = new JPanel(new BorderLayout());
        south.add(south1, BorderLayout.WEST);

        add(north, BorderLayout.NORTH);
        add(south, BorderLayout.SOUTH);
    }

    /**
     * Contains parameter
     */
    private String[] getAddParameters() {
        String[] paramnames = getTask().getParameterNames();
        ArrayList copy = new ArrayList();

        if ((input) && (innames == null)) {
            this.innames = new ArrayList();
            ParameterNode[] nodes = getTask().getParameterInputNodes();

            for (int count = 0; count < nodes.length; count++) {
                innames.add(nodes[count].getParameterName());
            }
        }

        for (int count = 0; count < paramnames.length; count++) {
            if (isUserAccessible(paramnames[count]) && ((innames == null) || (!innames.contains(paramnames[count])))) {
                copy.add(paramnames[count]);
            }
        }

        return (String[]) copy.toArray(new String[copy.size()]);
    }

    /**
     * @return true if the parameter is user accessible
     */
    private boolean isUserAccessible(String paramname) {
        return (getTask().getParameterType(paramname).equals(Tool.USER_ACCESSIBLE) ||
                getTask().getParameterType(paramname).equals(Tool.TRANSIENT_ACCESSIBLE)) && (!paramname
                .equals("TRIGGER"));
    }


    /**
     * This method is called when the panel is reset or cancelled. It should reset all the panels components to the
     * values specified by the associated task, e.g. a component representing a parameter called "noise" should be set
     * to the value returned by a getTool().getParameter("noise") call.
     */
    public void reset
            () {
    }

    /**
     * This method is called when the panel is finished with. It should dispose of any components (e.g. windows) used by
     * the panel.
     */
    public void dispose
            () {
    }


    public void itemStateChanged
            (ItemEvent
                    event) {
        paramlist.setEnabled(isParameterNode());
    }

}
