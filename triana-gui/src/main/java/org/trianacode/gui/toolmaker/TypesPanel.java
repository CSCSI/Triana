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
package org.trianacode.gui.toolmaker;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import org.trianacode.util.Env;

/**
 * The tool wizard panel for editing the input/output types of a tool
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class TypesPanel extends JPanel implements ActionListener {

    /**
     * Output policies
     */
    public static final int COPY_OUTPUT = 0;
    public static final int CLONE_MULTIPLE_OUTPUT = 1;
    public static final int CLONE_ALL_OUTPUT = 2;


    public static String NONE = "<-- None -->";


    /**
     * an array of the triana.types
     */
    private String[] types;

    /**
     * the unit panel, used to determine the number of input/output nodes
     */
    private UnitPanel unitpanel;


    /**
     * the lists of input and output types
     */
    private JList intypes = new JList(new DefaultListModel());
    private JList outtypes = new JList(new DefaultListModel());

    /**
     * the output policy combo box
     */
    private JComboBox outpolicy = new JComboBox(new DefaultComboBoxModel());

    /**
     * the buttons for adding/removing input and output types
     */
    private JButton addin = new JButton(Env.getString("add"));
    private JButton addout = new JButton(Env.getString("add"));
    private JButton removein = new JButton(Env.getString("remove"));
    private JButton removeout = new JButton(Env.getString("remove"));


    /**
     * Constructs a panel for editing the input/output types of a tool.
     */
    public TypesPanel(String[] types, UnitPanel unitpanel) {
        this.types = types;
        this.unitpanel = unitpanel;

        initLayout();
    }


    /**
     * layout the panel
     */
    private void initLayout() {
        setLayout(new BorderLayout());
        JPanel cont = new JPanel(new GridLayout(2, 1, 0, 5));

        JPanel inpanel = getInputTypesPanel();
        JPanel outpanel = getOutputTypesPanel();

        cont.add(inpanel);
        cont.add(outpanel);

        add(cont, BorderLayout.CENTER);
        add(getOutputPolicyPanel(), BorderLayout.SOUTH);
    }


    /**
     * @return a panel for selecting the input types
     */
    private JPanel getInputTypesPanel() {
        JPanel buttonpanel = new JPanel(new GridLayout(2, 1));
        buttonpanel.add(addin);
        buttonpanel.add(removein);

        addin.addActionListener(this);
        removein.addActionListener(this);

        JPanel buttoncont = new JPanel(new BorderLayout());
        buttoncont.add(buttonpanel, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(intypes, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        intypes.setPrototypeCellValue("1234567890123456789012345");
        intypes.setVisibleRowCount(6);
        ((DefaultListModel) intypes.getModel()).addElement(NONE);

        JPanel inpanel = new JPanel(new BorderLayout());
        inpanel.add(new JLabel(Env.getString("inputTypes"), JLabel.LEFT), BorderLayout.NORTH);
        inpanel.add(scroll, BorderLayout.CENTER);
        inpanel.add(buttoncont, BorderLayout.EAST);

        return inpanel;
    }

    /**
     * @return a panel for selecting the output types
     */
    private JPanel getOutputTypesPanel() {
        JPanel buttonpanel = new JPanel(new GridLayout(2, 1));
        buttonpanel.add(addout);
        buttonpanel.add(removeout);

        addout.addActionListener(this);
        removeout.addActionListener(this);

        JPanel buttoncont = new JPanel(new BorderLayout());
        buttoncont.add(buttonpanel, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(outtypes, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outtypes.setPrototypeCellValue("1234567890123456789012345");
        outtypes.setVisibleRowCount(6);
        ((DefaultListModel) outtypes.getModel()).addElement(NONE);

        JPanel outpanel = new JPanel(new BorderLayout());
        outpanel.add(new JLabel(Env.getString("outputTypes"), JLabel.LEFT), BorderLayout.NORTH);
        outpanel.add(scroll, BorderLayout.CENTER);
        outpanel.add(buttoncont, BorderLayout.EAST);

        return outpanel;
    }

    /**
     * @return a panel for setting the output policy
     */
    private JPanel getOutputPolicyPanel() {
        JPanel combopanel = new JPanel(new BorderLayout());
        combopanel.add(outpolicy, BorderLayout.WEST);
        combopanel.setBorder(new EmptyBorder(10, 5, 0, 0));

        DefaultComboBoxModel model = (DefaultComboBoxModel) outpolicy.getModel();
        model.addElement(Env.getString("copyOutput"));
        model.addElement(Env.getString("cloneMultipleOutput"));
        model.addElement(Env.getString("cloneAllOutput"));
        outpolicy.setSelectedItem(Env.getString("cloneMultipleOutput"));

        JPanel labelpanel = new JPanel(new GridLayout(1, 1));
        JLabel label = new JLabel("Output Policy", JLabel.LEFT);
        label.setVerticalAlignment(JLabel.CENTER);
        labelpanel.add(label);

        JPanel policypanel = new JPanel(new BorderLayout());
        policypanel.add(labelpanel, BorderLayout.WEST);
        policypanel.add(combopanel, BorderLayout.CENTER);

        return policypanel;
    }

    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == addin) || (event.getSource() == addout)) {
            Component parent = this.getParent();

            while (!(parent instanceof Window)) {
                parent = parent.getParent();
            }

            AddTypeDialog dialog;
            String title;
            boolean input;

            if (event.getSource() == addin) {
                title = "Add Input Type";
                input = true;
            } else {
                title = "Add Output Type";
                input = false;
            }

            if (parent instanceof Frame) {
                dialog = new AddTypeDialog((Frame) parent, title, input);
            } else {
                dialog = new AddTypeDialog((Dialog) parent, title, input);
            }

            dialog.setLocation(parent.getLocation().x + (parent.getSize().width / 2) - (dialog.getSize().width / 2),
                    parent.getLocation().y + (parent.getSize().height / 2) - (dialog.getSize().height / 2));
            dialog.show();

            if (dialog.isAccepted()) {
                Type type = dialog.getType();
                DefaultListModel addlist;

                if (event.getSource() == addin) {
                    addlist = (DefaultListModel) intypes.getModel();
                } else {
                    addlist = (DefaultListModel) outtypes.getModel();
                }

                addlist.removeElement(NONE);

                addInNodeOrder(type, addlist);
            }
        }

        if (event.getSource() == removein) {
            Object[] remove = intypes.getSelectedValues();

            for (int count = 0; count < remove.length; count++) {
                ((DefaultListModel) intypes.getModel()).removeElement(remove[count]);
            }

            if (intypes.getModel().getSize() == 0) {
                ((DefaultListModel) intypes.getModel()).addElement(NONE);
            }
        }

        if (event.getSource() == removeout) {
            Object[] remove = outtypes.getSelectedValues();

            for (int count = 0; count < remove.length; count++) {
                ((DefaultListModel) outtypes.getModel()).removeElement(remove[count]);
            }

            if (outtypes.getModel().getSize() == 0) {
                ((DefaultListModel) outtypes.getModel()).addElement(NONE);
            }
        }
    }

    /**
     * Adds the type to the list so that the list remains in node order
     */
    private void addInNodeOrder(Type type, DefaultListModel addlist) {
        Type curtype;
        int count = 0;
        boolean added = false;

        while ((count < addlist.getSize()) && (!added)) {
            curtype = (Type) addlist.getElementAt(count);

            if (type.getIndex() < curtype.getIndex()) {
                addlist.add(count, type);
                added = true;
            } else {
                count++;
            }
        }

        if (!added) {
            addlist.addElement(type);
        }
    }


    /**
     * @return the input types for each node (indexed by node index)
     */
    public String[][] getNodeInputTypes() {
        return getNodeTypes(intypes);

    }

    /**
     * @return the output types for each node (indexed by node index)
     */
    public String[][] getNodeOutputTypes() {
        return getNodeTypes(outtypes);
    }

    /**
     * @return the input types for nodes not specified in getNodeInputTypes();
     */
    public String[] getInputTypes() {
        return getOtherTypes(intypes);
    }

    /**
     * @return the output types for nodes not specified in getNodeInputTypes();
     */
    public String[] getOutputTypes() {
        return getOtherTypes(outtypes);
    }

    /**
     * @return the output policy
     */
    public int getOutputPolicy() {
        if (outpolicy.getSelectedItem().equals(Env.getString("copyOutput"))) {
            return COPY_OUTPUT;
        } else if (outpolicy.getSelectedItem().equals(Env.getString("cloneMultipleOutput"))) {
            return CLONE_MULTIPLE_OUTPUT;
        } else {
            return CLONE_ALL_OUTPUT;
        }
    }


    private String[][] getNodeTypes(JList typelist) {
        if (((DefaultListModel) typelist.getModel()).contains(NONE)) {
            return new String[0][0];
        }

        Object[] types = ((DefaultListModel) typelist.getModel()).toArray();
        ArrayList mainlist = new ArrayList();
        ArrayList allnodes = new ArrayList();
        ArrayList curnode;
        int used = 0;
        int nodecount = 0;

        for (int count = 0; count < types.length; count++) {
            if (((Type) types[count]).isAllNodes()) {
                allnodes.add(((Type) types[count]).getType());
                used++;
            } else if (((Type) types[count]).isOtherNodes()) {
                used++;
            }
        }

        while (used < types.length) {
            curnode = new ArrayList();
            curnode.addAll(allnodes);

            for (int count = 0; count < types.length; count++) {
                if (((Type) types[count]).getIndex() == nodecount) {
                    curnode.add(((Type) types[count]).getType());
                    used++;
                }
            }

            mainlist.add(curnode);
            nodecount++;
        }

        String[][] nodetypes = new String[mainlist.size()][];

        for (int count = 0; count < mainlist.size(); count++) {
            nodetypes[count] = (String[]) ((ArrayList) mainlist.get(count))
                    .toArray(new String[((ArrayList) mainlist.get(count)).size()]);
        }

        return nodetypes;
    }

    private String[] getOtherTypes(JList typelist) {
        if (((DefaultListModel) typelist.getModel()).contains(NONE)) {
            return new String[0];
        }

        Object[] types = ((DefaultListModel) typelist.getModel()).toArray();
        ArrayList otherlist = new ArrayList();

        for (int count = 0; count < types.length; count++) {
            if (((Type) types[count]).isAllNodes()) {
                otherlist.add(((Type) types[count]).getType());
            } else if (((Type) types[count]).isOtherNodes()) {
                otherlist.add(((Type) types[count]).getType());
            }
        }

        return (String[]) otherlist.toArray(new String[otherlist.size()]);
    }


    private class Type {

        public final static int ALL_NODES = -1;
        public final static int OTHER_NODES = Integer.MAX_VALUE;

        private int index;
        private String type;

        public Type(int index, String type) {
            this.index = index;
            this.type = type;
        }

        public boolean isAllNodes() {
            return index == ALL_NODES;
        }

        public boolean isOtherNodes() {
            return index == OTHER_NODES;
        }

        public int getIndex() {
            return index;
        }

        public String getType() {
            return type;
        }


        public String toString() {
            if (index == ALL_NODES) {
                return "[All] " + type;
            } else if (index == OTHER_NODES) {
                return "[OTHER] " + type;
            } else {
                return "[" + index + "] " + type;
            }
        }
    }


    private class AddTypeDialog extends Dialog implements ActionListener {

        private String ALL_NODES = "All Nodes";
        private String OTHER_NODES = "Other Nodes";

        private boolean input;

        private JComboBox nodelist = new JComboBox(new DefaultComboBoxModel());
        private JComboBox typelist = new JComboBox(new DefaultComboBoxModel());

        private JButton ok = new JButton(Env.getString("OK"));
        private JButton cancel = new JButton(Env.getString("Cancel"));

        private boolean accepted = false;


        public AddTypeDialog(Frame frame, String title, boolean input) {
            super(frame, title, true);

            this.input = input;

            initLayout();
        }

        public AddTypeDialog(Dialog dialog, String title, boolean input) {
            super(dialog, title, true);

            this.input = input;

            initLayout();
        }


        private void initLayout() {
            setLayout(new BorderLayout());

            initNodeList();
            initTypeList();

            JPanel labelpanel = new JPanel(new GridLayout(2, 1, 0, 3));
            labelpanel.add(new JLabel("Node"));
            labelpanel.add(new JLabel("Data Type"));

            JPanel comppanel = new JPanel(new GridLayout(2, 1, 0, 3));
            comppanel.add(nodelist);
            comppanel.add(typelist);

            JPanel buttonpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonpanel.add(ok);
            buttonpanel.add(cancel);

            ok.addActionListener(this);
            cancel.addActionListener(this);

            JPanel mainpanel = new JPanel(new BorderLayout(3, 3));
            mainpanel.add(labelpanel, BorderLayout.WEST);
            mainpanel.add(comppanel, BorderLayout.CENTER);
            mainpanel.add(buttonpanel, BorderLayout.SOUTH);
            mainpanel.setBorder(new EmptyBorder(3, 3, 3, 3));

            add(mainpanel, BorderLayout.CENTER);

            pack();
        }

        private void initNodeList() {
            DefaultComboBoxModel model = (DefaultComboBoxModel) nodelist.getModel();
            model.addElement(ALL_NODES);
            model.addElement(OTHER_NODES);

            int max;

            if (input) {
                if (unitpanel.getMaximumInputNodes() == Integer.MAX_VALUE) {
                    max = unitpanel.getDefaultInputNodes();
                } else {
                    max = unitpanel.getMaximumInputNodes();
                }
            } else {
                if (unitpanel.getMaximumOutputNodes() == Integer.MAX_VALUE) {
                    max = unitpanel.getDefaultOutputNodes();
                } else {
                    max = unitpanel.getMaximumOutputNodes();
                }
            }

            for (int count = 0; count < max; count++) {
                model.addElement(String.valueOf(count));
            }
        }

        private void initTypeList() {
            DefaultComboBoxModel model = (DefaultComboBoxModel) typelist.getModel();
            typelist.setEditable(true);

            for (int count = 0; count < types.length; count++) {
                model.addElement(types[count]);
            }
        }


        public boolean isAccepted() {
            return accepted;
        }


        public Type getType() {
            String nodestr = (String) nodelist.getSelectedItem();
            int nodeindex;

            if (nodestr.equals(ALL_NODES)) {
                nodeindex = Type.ALL_NODES;
            } else if (nodestr.equals(OTHER_NODES)) {
                nodeindex = Type.OTHER_NODES;
            } else {
                nodeindex = Integer.parseInt(nodestr);
            }

            return new Type(nodeindex, (String) typelist.getSelectedItem());
        }

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == ok) {
                accepted = true;
                setVisible(false);
                dispose();
            } else if (event.getSource() == cancel) {
                accepted = false;
                setVisible(false);
                dispose();
            }
        }


    }
}














