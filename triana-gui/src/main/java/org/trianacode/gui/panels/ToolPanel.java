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
import org.trianacode.taskgraph.tool.ToolTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

/**
 * A simple panel for specifying a tool name (optional), package and tool box. This can be used in various tool
 * creation/paste into type panels.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */


public class ToolPanel extends JPanel implements ActionListener, ItemListener {

    private static String LAST_TOOLBOX = "lastToolBox";


    private ToolTable tools;

    private JTextField toolname = new JTextField(20);
    private JTextField pack = new JTextField(20);
    private JComboBox toolbox;

    private JButton packbrowse = new JButton(GUIEnv.getIcon("dots.png"));

    /**
     * Creates a default tool panel (tool name, package and tool box)
     */
    public ToolPanel(ToolTable tools) {
        this.tools = tools;
        initPanel(true);
    }

    /**
     * Creates a tool panel, optionally including a tool name field
     */
    public ToolPanel(ToolTable tools, boolean toolname) {
        this.tools = tools;
        initPanel(toolname);
    }

    /**
     * Creates a tool panel with default package and tool box settings (tool name is not shown)
     */
    public ToolPanel(ToolTable tools, String pack, String toolbox) {
        this.tools = tools;

        initPanel(false);
        setPackage(pack);
        setToolBox(toolbox);
    }

    /**
     * Creates a tool panel with default tool name, package and tool box settings
     */
    public ToolPanel(ToolTable tools, String toolname, String pack, String toolbox) {
        this.tools = tools;

        initPanel(true);
        setToolName(toolname);
        setPackage(pack);
        setToolBox(toolbox);
    }


    private void initPanel(boolean istoolname) {
        setLayout(new BorderLayout());

        JPanel formpanel = new JPanel(new FormLayout(3, 3));

        if (istoolname) {
            JPanel toolpanel = new JPanel(new BorderLayout());
            toolpanel.add(toolname, BorderLayout.WEST);

            formpanel.add(new JLabel(Env.getString("toolname")));
            formpanel.add(toolpanel);
        }

        JPanel packpanel = new JPanel(new BorderLayout());
        packpanel.add(pack, BorderLayout.CENTER);
        packpanel.add(packbrowse, BorderLayout.EAST);
        packbrowse.setMargin(new Insets(6, 4, 2, 4));
        packbrowse.addActionListener(this);

        JPanel packpanel2 = new JPanel(new BorderLayout());
        packpanel2.add(packpanel, BorderLayout.WEST);

        formpanel.add(new JLabel(Env.getString("package")));
        formpanel.add(packpanel2);

        toolbox = new JComboBox(tools.getToolBoxPaths());
        toolbox.addItemListener(this);
        String lastToolBox = (String) Env.getUserProperty(LAST_TOOLBOX);

        if (lastToolBox != null) {
            toolbox.setSelectedItem(lastToolBox);
        }
        formpanel.add(new JLabel(Env.getString("toolbox")));
        formpanel.add(toolbox);

        add(formpanel, BorderLayout.NORTH);
    }


    public void setToolName(String toolname) {
        this.toolname.setText(toolname);
    }

    public String getToolName() {
        return toolname.getText();
    }


    public void setPackage(String pack) {
        this.pack.setText(pack);
    }

    public String getPackage() {
        return pack.getText();
    }


    public void setToolBox(String toolbox) {
        this.toolbox.setSelectedItem(toolbox);
    }

    public String getToolBox() {
        return toolbox.getSelectedItem().toString();
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == packbrowse) {
            JFileChooser chooser = new JFileChooser((String) toolbox.getSelectedItem());
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle(Env.getString("selectPackage"));
            chooser.setFileHidingEnabled(false);

            int choice = chooser.showDialog(this, Env.getString("OK"));

            if (choice == JFileChooser.APPROVE_OPTION) {
                String packageName = stripPackageName(chooser.getSelectedFile().getPath());
                if (packageName == null) {
                    packageName = "";
                    JOptionPane.showMessageDialog(this, "The selected unit package is not in your tool box path",
                            "Warning", JOptionPane.WARNING_MESSAGE, GUIEnv.getTrianaIcon());
                }

                packageName = packageName.replace(File.separatorChar, '.');
                if (packageName.startsWith(".")) {
                    packageName = packageName.substring(1);
                }

                pack.setText(packageName);
            }
        }
    }

    /**
     * Attempt to return the package name minus a unitPackage path, null if the unitPackage path doesn't exist
     *
     * @param fullPath the absolute path
     * @return The package name or null
     */
    private String stripPackageName(String fullPath) {
        String result = null;
        for (int i = 0; i < toolbox.getItemCount(); i++) {
            String tbox = (String) toolbox.getItemAt(i);
            if (fullPath.startsWith(tbox)) {
                result = fullPath.substring(tbox.length(), fullPath.length());
                break;
            }
        }
        return result;
    }

    /**
     * Invoked when an item has been selected or deselected by the user. The code written for this method performs the
     * operations that need to occur when an item is selected (or deselected).
     */
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getSource();
        if (source == toolbox) {
            Object selectedItem = toolbox.getSelectedItem();
            if (selectedItem != null) {
                Env.setUserProperty(LAST_TOOLBOX, selectedItem);
            }
        }
    }

}
