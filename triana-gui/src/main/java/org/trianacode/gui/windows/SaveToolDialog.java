/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.gui.windows;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.hci.tools.PackageTree;
import org.trianacode.gui.panels.NormalizedField;
import org.trianacode.gui.panels.OptionPane;
import org.trianacode.taskgraph.tool.FileToolboxLoader;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.Toolbox;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class SaveToolDialog extends JDialog implements ActionListener {


    /**
     * the main list
     */
    private JComboBox combo = new JComboBox(new DefaultComboBoxModel());
    private ArrayList<String> toolboxItems = new ArrayList<String>();

    private boolean go = false;

    /**
     * the ok and cancel buttons
     */
    private JButton ok = new JButton(Env.getString("OK"));
    private JButton cancel = new JButton(Env.getString("Cancel"));


    private Tool tool;
    private ToolTable tools;

    private JButton pkgButton = new JButton(GUIEnv.getIcon("dots.png"));
    private JTextField pkg = new NormalizedField(25, new char[]{'_', '.'});
    private JTextField name = new NormalizedField(25, 100, new char[]{'_'}, false);


    public SaveToolDialog(Tool tool, ToolTable tools) throws HeadlessException {
        super(GUIEnv.getApplicationFrame(), "Save Tool", true);
        this.tool = tool;
        this.tools = tools;
        setResizable(false);
        init();
    }


    private void init() {

        JPanel boxes = new JPanel();
        boxes.setLayout(new BorderLayout());
        DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();
        combo.setEditable(false);
        combo.setPrototypeDisplayValue("012345678901234567890123456789012345678901234567890123456789");
        Toolbox[] toolboxes = tools.getToolBoxes(FileToolboxLoader.LOCAL_TYPE);
        Toolbox tb = tool.getToolBox();
        String sel = null;
        for (int count = 0; count < toolboxes.length; count++) {
            if (tb != null && tb.getPath().equals(toolboxes[count].getPath())) {
                sel = tb.getPath();
            }
            model.addElement(toolboxes[count].getPath());
            toolboxItems.add(toolboxes[count].getPath());
        }
        if (sel != null) {
            combo.setSelectedItem(sel);
        }
        JPanel listpanel = new JPanel(new BorderLayout(3, 0));
        listpanel.add(new JLabel("Choose a toolbox:"), BorderLayout.WEST);
        listpanel.add(combo, BorderLayout.CENTER);
        listpanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        boxes.add(listpanel, BorderLayout.CENTER);

        JPanel pkgPanel = new JPanel();
        pkgPanel.setLayout(new BorderLayout());
        pkgPanel.add(new JLabel("Choose a package:"), BorderLayout.WEST);
        pkgPanel.add(pkg, BorderLayout.CENTER);

        pkgPanel.add(pkgButton, BorderLayout.EAST);
        pkgButton.setActionCommand(Env.getString("compilerClasspath"));
        pkgButton.setMargin(new Insets(6, 4, 2, 4));
        pkgButton.addActionListener(this);
        pkgPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());
        namePanel.add(new JLabel("Choose a name:"), BorderLayout.WEST);
        namePanel.add(name, BorderLayout.CENTER);
        namePanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        if (tool.getToolPackage() != null) {
            pkg.setText(tool.getToolPackage());
        }
        if (tool.getToolName() != null) {
            name.setText(tool.getToolName());
        }


        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(3, 3, 3, 3));
        main.add(boxes, BorderLayout.NORTH);
        main.add(pkgPanel, BorderLayout.CENTER);
        main.add(namePanel, BorderLayout.SOUTH);
        JPanel icoPanel = new JPanel(new BorderLayout());
        icoPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        JLabel icoLabel = new JLabel(GUIEnv.getTrianaIcon());
        icoLabel.setBorder(new EmptyBorder(3, 3, 3, 3));
        icoPanel.add(icoLabel, BorderLayout.WEST);

        getContentPane().add(icoPanel, BorderLayout.NORTH);
        getContentPane().add(main, BorderLayout.CENTER);

        JPanel buttonpanel = new JPanel();
        buttonpanel.add(ok);
        buttonpanel.add(cancel);

        ok.addActionListener(this);
        cancel.addActionListener(this);

        getContentPane().add(buttonpanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(GUIEnv.getApplicationFrame());
        setVisible(true);
    }

    private String normalizePackage(String pkg) {
        if (pkg == null || pkg.length() == 0) {
            return "";
        }
        if (pkg.startsWith(".")) {
            pkg = pkg.substring(1);
        }
        if (pkg.endsWith(".")) {
            pkg = pkg.substring(0, pkg.length() - 1);
        }
        return pkg.toLowerCase();
    }

    private String normalizeName(String name) {
        if (name == null) {
            return "";
        }
        name = name.replace(File.separator, ".");
        return name;
    }

    private boolean verifyName(String name) {
        if (name == null || name.length() == 0) {
            return false;
        }
        return true;
    }

    private boolean verifyPackage(String pkg) {
        if (pkg == null || pkg.length() == 0) {
            return false;
        }
        return true;
    }


    public void actionPerformed(ActionEvent event) {
        String toolbox = (String) combo.getSelectedItem();

        if (event.getSource() == pkgButton) {
            PackageTree packageTree = new PackageTree(this.tools);
            String returnVal = packageTree.showPackages();
            if (returnVal != null) {
                pkg.setText(returnVal);
            } else {
                pkg.setText("");
            }
        } else {
            if (event.getSource() == ok) {
                pkg.setText(normalizePackage(pkg.getText()));
                boolean b = verifyPackage(pkg.getText());
                if (!b) {
                    OptionPane.showInformation("Please select or create a valid package name",
                            "Package Name", this);
                } else {
                    name.setText(normalizeName(name.getText()));
                    b = verifyName(name.getText());
                    if (!b) {
                        OptionPane.showInformation("Please create a valid tool name",
                                "Tool Name", this);
                    } else {
                        save(toolbox, pkg.getText(), name.getText());
                        setVisible(false);
                        dispose();
                    }
                }
            } else {
                setVisible(false);
                dispose();
            }
        }
    }


    public boolean isGo() {
        return go;
    }

    private void save(String toolbox, String pkg, String name) {
        tool.setToolPackage(pkg);
        tool.setToolName(name);
        tools.getToolResolver().addTool(tool, tools.getToolResolver().getToolbox(toolbox));
        go = true;

    }

}
