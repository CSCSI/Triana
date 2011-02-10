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

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.util.Env;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.tool.*;
import org.trianacode.taskgraph.util.FileUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.Vector;

/**
 * Panel for displaying, adding and removing local toolbox paths
 *
 * @author Matthew Shields
 * @version $Revsion:$
 */
public class ToolBoxPanel extends ParameterPanel implements ActionListener, ListSelectionListener {

    private ToolTable tools = null;
    private JList toolboxList;
    private Vector toolBoxItems;
    private JButton remBtn;
    private JTextField nameField = new NormalizedField(20, new char[]{'_', '-'});
    private JTextField pathField = new JTextField(20);
    private JTextField typeField = new JTextField(20);


    /**
     * This method returns true by default. It should be overridden if the panel does not want the user to be able to
     * change the auto commit state
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS;
    }

    public void applyClicked() {
        apply();
    }


    /**
     * Called when the ok button is clicked on the parameter window. Calls applyClicked by default to commit any
     * parameter changes.
     */
    public void okClicked() {
        apply();

    }

    private void apply() {
        if (typeField.getText() == null || typeField.getText().length() == 0) {
            return;
        }
        ToolboxLoader loader = ToolboxLoaderRegistry.getLoader(typeField.getText());
        String name = nameField.getText();
        if (name.length() == 0) {
            name = null;
        }
        String path = pathField.getText();
        if (path == null || path.length() == 0) {
            return;
        }

        Toolbox t = tools.getToolResolver().getToolbox(path);
        if (t != null) {
            tools.getToolResolver().setToolboxName(t, name);
        } else {
            t = loader.loadToolbox(path, name, tools.getProperties());
            tools.getToolResolver().addToolbox(t);
            toolBoxItems.add(path);
            toolboxList.revalidate();
            toolboxList.repaint();
        }

    }

    /**
     * Constructor
     *
     * @param tools the interface to the current tools manager
     */
    public ToolBoxPanel(ToolTable tools) {
        this.tools = tools;
    }

    /**
     * This method is called when the task is set for this panel. It is overridden to create the panel layout.
     */
    public void init() {
        setLayout(new BorderLayout());
        typeField.setEditable(false);

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.Y_AXIS));
        buttonpanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        buttonpanel.add(Box.createVerticalGlue());
        Collection<ToolboxLoader> loaders = ToolboxLoaderRegistry.getLoaders();
        for (ToolboxLoader loader : loaders) {
            String type = loader.getType();
            JButton addBtn = new JButton("Add " + type);
            if (type.equals(FileToolboxLoader.LOCAL_TYPE)) {
                addBtn.addActionListener(new LocalActionListener(this));
            } else {
                addBtn.addActionListener(new OtherActionListener(this, type));
            }
            buttonpanel.add(addBtn);
        }
        remBtn = new JButton("Remove");
        remBtn.addActionListener(this);
        buttonpanel.add(remBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttonpanel, BorderLayout.SOUTH);
        add(panel, BorderLayout.EAST);

        String prototype = "01234567890123456789012345";
        Toolbox[] toolBoxes = tools.getToolBoxes();
        toolBoxItems = new Vector();
        String[] namedTypes = new String[toolBoxes.length];
        int x = 0;
        for (Toolbox toolBox : toolBoxes) {
            toolBoxItems.add(toolBox.getPath());
            namedTypes[x++] = toolBox.getType();
        }

        for (int count = 0; count < toolBoxItems.size(); count++) {
            if (((String) toolBoxItems.elementAt(count)).length() > prototype.length()) {
                prototype = ((String) toolBoxItems.elementAt(count));
            }
        }

        toolboxList = new JList(toolBoxItems);
        toolboxList.setVisibleRowCount(10);
        toolboxList.setPrototypeCellValue(prototype);
        toolboxList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        toolboxList.addListSelectionListener(this);

        JScrollPane scroll = new JScrollPane(toolboxList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);
        JPanel details = new LabelledTextFieldPanel(new String[]{"Location", "Name", "Type"}, new JTextField[]{pathField, nameField, typeField});

        details.setBorder(new EmptyBorder(3, 3, 3, 3));
        add(details, BorderLayout.SOUTH);

    }

    /**
     * This method is called when the panel is reset or cancelled. It should reset all the panels components to the
     * values specified by the associated task, e.g. a component representing a parameter called "noise" should be set
     * to the value returned by a getTool().getParameter("noise") call.
     */
    public void reset() {
    }

    /**
     * This method is called when the panel is finished with. It should dispose of any components (e.g. windows) used by
     * the panel.
     */
    public void dispose() {
    }


    private class LocalActionListener implements ActionListener {

        private JPanel parent;

        private LocalActionListener(JPanel parent) {
            this.parent = parent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TFileChooser chooser = new TFileChooser(Env.TOOLBOX_DIRECTORY);
            chooser.setMultiSelectionEnabled(true);
            chooser.setDialogTitle(Env.getString("selectToolBoxPath"));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setApproveButtonText(Env.getString("OK"));

            int result = chooser.showOpenDialog(parent);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                String current = selectedFile.getAbsolutePath();

                if (toolBoxItems.contains(current)) { //already in list
                    JOptionPane.showMessageDialog(parent, Env.getString("toolpathexists"), "Information",
                            JOptionPane.INFORMATION_MESSAGE,
                            GUIEnv.getTrianaIcon());
                } else if (isSubPath(current)) { //contained as a subpath of item in list
                    JOptionPane.showMessageDialog(parent, Env.getString("toolpathsub"), "Information",
                            JOptionPane.INFORMATION_MESSAGE,
                            GUIEnv.getTrianaIcon());
                } else if (isSuperPath(current)) { //is super path of element
                    JOptionPane.showMessageDialog(parent, Env.getString("toolpathsuper"), "Information",
                            JOptionPane.INFORMATION_MESSAGE,
                            GUIEnv.getTrianaIcon());
                } else {
                    pathField.setText(current);
                    typeField.setText(FileToolboxLoader.LOCAL_TYPE);
                    nameField.setText(selectedFile.getName());
                }
            }
        }
    }

    private class OtherActionListener implements ActionListener {

        private JPanel parent;
        private String type;

        private OtherActionListener(JPanel parent, String type) {
            this.parent = parent;
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            UnknownToolBoxTypePanel panel = new UnknownToolBoxTypePanel(type);
            panel.init();
            ParameterWindow paramwin = new ParameterWindow(parent, panel.getPreferredButtons(), true);
            paramwin.setTitle(Env.getString("editToolBoxPaths"));
            paramwin.setParameterPanel(panel);
            paramwin.setLocationRelativeTo(GUIEnv.getApplicationFrame());
            paramwin.setAlwaysOnTop(true);
            paramwin.setVisible(true);
            String location = panel.getPath();
            String type = panel.getType();
            String name = panel.getName();
            if (name == null || name.length() == 0) {
                name = "no-name";
            }
            if (location == null || location.length() == 0) {
                return;
            }
            if (toolBoxItems.contains(location)) {
                return;
            }

            nameField.setText(name);
            pathField.setText(location);
            typeField.setText(type);
        }
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {

        String selected = (String) toolboxList.getSelectedValue();
        if (selected != null) {
            Toolbox tb = tools.getToolResolver().getToolbox(selected);
            if (tb != null) {
                tools.getToolResolver().removeToolbox(tb);
                toolBoxItems.remove(selected);
            }
        }

        toolboxList.setListData(toolBoxItems);
        updateFields();
    }

    private boolean isSubPath(String child) {
        for (int i = 0; i < toolBoxItems.size(); i++) {
            String s = (String) toolBoxItems.elementAt(i);
            if (FileUtils.isParent(s, child)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSuperPath(String parent) {
        Vector toBeRemoved = new Vector();
        for (int i = 0; i < toolBoxItems.size(); i++) {
            String s = (String) toolBoxItems.elementAt(i);
            if (FileUtils.isParent(parent, s)) {
                toBeRemoved.add(s);
            }
        }
        if (toBeRemoved.size() > 0) {
            toolBoxItems.removeAll(toBeRemoved);
            toolBoxItems.add(parent);
            return true;
        }
        return false;
    }

    private void updateFields() {
        String selected = (String) toolboxList.getSelectedValue();
        if (selected == null) {
            pathField.setText("");
            nameField.setText("");
            typeField.setText("");
        } else {
            Toolbox tb = tools.getToolResolver().getToolbox(selected);
            pathField.setText(tb.getPath());
            nameField.setText(tb.getName());
            typeField.setText(tb.getType());
        }
    }


    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String val = (String) toolboxList.getSelectedValue();
            if (val != null) {
                Toolbox tb = tools.getToolResolver().getToolbox(val);
                pathField.setText(tb.getPath());
                nameField.setText(tb.getName());
                typeField.setText(tb.getType());
            }
        }
    }
}
