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

import org.trianacode.gui.panels.FormLayout;
import org.trianacode.gui.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * The tool wizard panel for editing the parameters of a tool
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class ParamsPanel extends JPanel implements ActionListener, ParamsPanelInterface {

    public static String NONE = "<-- None -->";


    /**
     * an array of the available data types
     */
    private String[] datatypes;

    /**
     * an array of the available parameter types
     */
    private String[] paramtypes;

    /**
     * parameter update policy combo box
     */
    private JComboBox update = new JComboBox(new DefaultComboBoxModel());

    /**
     * the lists of parameters
     */
    private JList params = new JList(new DefaultListModel());

    /**
     * the buttons for adding/removing parameters
     */
    private JButton add = new JButton(Env.getString("add"));
    private JButton remove = new JButton(Env.getString("remove"));

    /**
     * a hashtable of the parameter type for each parameter
     */
    private Hashtable typetable = new Hashtable();

    /**
     * Constructs a panel for editing the input/output types of a tool.
     */
    public ParamsPanel(String[] datatypes, String[] paramtypes) {
        this.datatypes = datatypes;
        this.paramtypes = paramtypes;
        initLayout();
    }


    /**
     * layout the panel
     */
    private void initLayout() {
        setLayout(new FlowLayout());
        JPanel inpanel = getParamsPanel();
        add(inpanel);
    }


    /**
     * @return a panel for selecting the parameters
     */
    private JPanel getParamsPanel() {
        JPanel buttonpanel = new JPanel(new GridLayout(2, 1));
        buttonpanel.add(add);
        buttonpanel.add(remove);

        add.addActionListener(this);
        remove.addActionListener(this);

        JPanel buttoncont = new JPanel(new BorderLayout());
        buttoncont.add(buttonpanel, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(params, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        params.setPrototypeCellValue("123456789012345678901234567890");
        params.setVisibleRowCount(8);
        ((DefaultListModel) params.getModel()).addElement(NONE);

        JPanel combopanel = new JPanel(new BorderLayout());
        combopanel.add(update, BorderLayout.WEST);
        combopanel.setBorder(new EmptyBorder(10, 5, 0, 0));
        populate(update);

        JPanel labelpanel = new JPanel(new GridLayout(1, 1));
        JLabel label = new JLabel(Env.getString("updatePolicy"), JLabel.LEFT);
        label.setVerticalAlignment(JLabel.CENTER);
        labelpanel.add(label);

        JPanel updatepanel = new JPanel(new BorderLayout());
        updatepanel.add(labelpanel, BorderLayout.WEST);
        updatepanel.add(combopanel, BorderLayout.CENTER);

        JPanel ppanel = new JPanel(new BorderLayout());
        ppanel.add(new JLabel(Env.getString("parameters"), JLabel.LEFT), BorderLayout.NORTH);
        ppanel.add(scroll, BorderLayout.CENTER);
        ppanel.add(buttoncont, BorderLayout.EAST);
        ppanel.add(updatepanel, BorderLayout.SOUTH);

        return ppanel;
    }

    private void populate(JComboBox combo) {
        DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();
        model.addElement(Env.getString("updateAtProcess"));
        model.addElement(Env.getString("updateImmediately"));
        model.addElement(Env.getString("doNotUpdate"));
    }


    /**
     * @return an array of the input types choosen
     */
    public String[] getParameterNames() {
        if (((DefaultListModel) params.getModel()).contains(NONE)) {
            return new String[0];
        }

        Object[] in = ((DefaultListModel) params.getModel()).toArray();
        String[] copy = new String[in.length];

        for (int count = 0; count < in.length; count++) {
            copy[count] = getParamName((String) in[count]);
        }

        return copy;
    }

    /**
     * @return the data type for a parameter
     */
    public String getDataType(String paramname) {
        DefaultListModel model = ((DefaultListModel) params.getModel());
        int count = 0;

        while ((count < model.getSize()) && !getParamName((String) model.elementAt(count)).equals(paramname)) {
            count++;
        }

        if (count >= model.getSize()) {
            return null;
        } else {
            String str = ((String) model.elementAt(count));
            return str.substring(1, str.indexOf(')'));
        }
    }

    /**
     * @return the parameter type for a parameter
     */
    public String getParameterType(String paramname) {
        if (typetable.containsKey(paramname)) {
            return (String) typetable.get(paramname);
        } else {
            return null;
        }
    }


    /**
     * @return the default value for a parameter
     */
    public String getDefaultValue(String paramname) {
        DefaultListModel model = ((DefaultListModel) params.getModel());
        int count = 0;

        while ((count < model.getSize()) && !getParamName((String) model.elementAt(count)).equals(paramname)) {
            count++;
        }

        if (count >= model.getSize()) {
            return null;
        } else {
            String str = ((String) model.elementAt(count));

            if (str.indexOf('=') == -1) {
                return null;
            } else {
                return str.substring(str.indexOf('=') + 2);
            }
        }
    }

    /**
     * Sets the default value for a parameter
     */
    public void setDefaultValue(String paramname, String defval) {
        DefaultListModel model = ((DefaultListModel) params.getModel());
        int count = 0;

        while ((count < model.getSize()) && !getParamName((String) model.elementAt(count)).equals(paramname)) {
            count++;
        }

        if (count < model.getSize()) {
            String datatype = getDataType(paramname);

            model.removeElementAt(count);
            model.insertElementAt(getListEntry(paramname, datatype, defval), count);
        }
    }


    /**
     * @return the param name for the specified list entry
     */
    private String getParamName(String listentry) {
        if (listentry.indexOf('=') != -1) {
            return listentry.substring(listentry.indexOf(' ') + 1, listentry.indexOf('=') - 1);
        } else {
            return listentry.substring(listentry.indexOf(' ') + 1);
        }
    }

    /**
     * @return the parameter update policy
     */
    public int getUpdatePolicy() {
        if (update.getSelectedItem().equals(Env.getString("updateAtProcess"))) {
            return UPDATE_AT_START_OF_PROCESS;
        } else if (update.getSelectedItem().equals(Env.getString("updateImmediately"))) {
            return UPDATE_IMMEDIATELY;
        } else {
            return DO_NOT_UPDATE;
        }
    }


    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == add) {
            Component parent = this.getParent();

            while (!(parent instanceof Window)) {
                parent = parent.getParent();
            }

            ParamDialog dialog;

            if (parent instanceof Frame) {
                dialog = new ParamDialog(datatypes, paramtypes, (Frame) parent);
            } else {
                dialog = new ParamDialog(datatypes, paramtypes, (Dialog) parent);
            }

            dialog.setLocation(parent.getLocation().x + (parent.getSize().width / 2) - (dialog.getSize().width / 2),
                    parent.getLocation().y + (parent.getSize().height / 2) - (dialog.getSize().height / 2));
            dialog.setVisible(true);

            if (dialog.isApproved() && (dialog.getListEntry() != null)) {
                ((DefaultListModel) params.getModel()).removeElement(NONE);
                ((DefaultListModel) params.getModel()).addElement(dialog.getListEntry());
                typetable.put(dialog.getParameterName(), dialog.getParameterType());
            }
        }

        if (event.getSource() == remove) {
            Object[] remove = params.getSelectedValues();

            for (int count = 0; count < remove.length; count++) {
                ((DefaultListModel) params.getModel()).removeElement(remove[count]);
            }

            if ((params.getModel()).getSize() == 0) {
                ((DefaultListModel) params.getModel()).addElement(NONE);
            }
        }
    }


    private static String getListEntry(String paramname, String type, String defval) {
        if (defval.equals("")) {
            return "(" + type + ") " + paramname.trim();
        } else {
            return "(" + type + ") " + paramname.trim() + " = " + defval.trim();
        }
    }


    private class ParamDialog extends JDialog implements ActionListener {

        /**
         * the input fields
         */
        private JTextField paramname = new JTextField(15);
        private JComboBox datatypes = new JComboBox(new DefaultComboBoxModel());
        private JComboBox paramtypes = new JComboBox(new DefaultComboBoxModel());
        private JTextField defaultval = new JTextField(15);

        /**
         * the buttons
         */
        private JButton ok = new JButton(Env.getString("OK"));
        private JButton cancel = new JButton(Env.getString("Cancel"));

        /**
         * a flag indicating whether the dialog has been accepted
         */
        private boolean approved = false;


        /**
         * Constructs a modal param dialog offering the specified data type choices
         */
        public ParamDialog(String[] datatypes, String[] paramtypes, Frame parent) {
            super(parent, Env.getString("addParameter"), true);
            initialise(datatypes, paramtypes);
        }

        /**
         * Constructs a modal param dialog offering the specified data type choices
         */
        public ParamDialog(String[] datatypes, String[] paramtypes, Dialog parent) {
            super(parent, Env.getString("addParameter"), true);
            initialise(datatypes, paramtypes);
        }


        private void initialise(String[] dtypes, String[] ptypes) {
            populate(datatypes, dtypes);
            populate(paramtypes, ptypes);

            JPanel formpanel = new JPanel(new FormLayout(3, 3));

            formpanel.add(new JLabel(Env.getString("parameterName"), JLabel.LEFT));

            JPanel itempanel = new JPanel(new BorderLayout());
            itempanel.add(paramname, BorderLayout.WEST);
            formpanel.add(itempanel);

            formpanel.add(new JLabel(Env.getString("defaultValue"), JLabel.LEFT));

            itempanel = new JPanel(new BorderLayout());
            itempanel.add(defaultval, BorderLayout.WEST);
            formpanel.add(itempanel);

            formpanel.add(new JLabel(Env.getString("dataType"), JLabel.LEFT));

            itempanel = new JPanel(new BorderLayout());
            itempanel.add(datatypes, BorderLayout.WEST);
            formpanel.add(itempanel);

            formpanel.add(new JLabel(Env.getString("paramType"), JLabel.LEFT));

            itempanel = new JPanel(new BorderLayout());
            itempanel.add(paramtypes, BorderLayout.WEST);
            formpanel.add(itempanel);

            JPanel buttonpanel = new JPanel();
            buttonpanel.add(ok);
            buttonpanel.add(cancel);
            ok.addActionListener(this);
            cancel.addActionListener(this);

            JPanel contain = new JPanel(new BorderLayout());
            contain.add(formpanel, BorderLayout.NORTH);
            contain.add(buttonpanel, BorderLayout.SOUTH);

            getContentPane().setLayout(new FlowLayout());
            getContentPane().add(contain);
            pack();
        }

        private void populate(JComboBox combo, String[] items) {
            for (int count = 0; count < items.length; count++) {
                ((DefaultComboBoxModel) combo.getModel()).addElement(items[count]);
            }
        }


        /**
         * @return true if ok was pressed
         */
        public boolean isApproved() {
            return approved;
        }

        /**
         * @return the list entry for this parameter
         */
        public String getListEntry() {
            if (paramname.getText().equals("") || (datatypes.getSelectedIndex() == -1)) {
                return null;
            } else {
                return ParamsPanel
                        .getListEntry(paramname.getText(), (String) datatypes.getSelectedItem(), defaultval.getText());
            }
        }

        /**
         * @return the name of this parameter
         */
        public String getParameterName() {
            if (paramname.getText().equals("") || (datatypes.getSelectedIndex() == -1)) {
                return null;
            } else {
                return paramname.getText();
            }
        }

        /**
         * @return the type for this parameter
         */
        public String getDataType() {
            if (paramname.getText().equals("") || (datatypes.getSelectedIndex() == -1)) {
                return null;
            } else {
                return (String) datatypes.getSelectedItem();
            }
        }

        /**
         * @return the parameter type for this parameter
         */
        public String getParameterType() {
            if (paramname.getText().equals("") || (datatypes.getSelectedIndex() == -1)) {
                return null;
            } else {
                return (String) paramtypes.getSelectedItem();
            }
        }


        public void actionPerformed(ActionEvent event) {
            if (event.getSource().equals(ok)) {
                approved = true;
                setVisible(false);
                dispose();
            } else if (event.getSource().equals(cancel)) {
                setVisible(false);
                dispose();
            }
        }

    }

}














