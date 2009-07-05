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
import org.trianacode.gui.panels.OptionPane;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jul 4, 2009: 11:29:47 AM
 * @date $Date:$ modified by $Author:$
 */

public class SaveToolDialog extends JDialog implements ActionListener {


    /**
     * the main list
     */
    private JComboBox combo = new JComboBox(new DefaultComboBoxModel());

    private boolean go = false;

    /**
     * the ok and cancel buttons
     */
    private JButton ok = new JButton(Env.getString("OK"));
    private JButton cancel = new JButton(Env.getString("Cancel"));


    private Tool tool;
    private ToolTable tools;

    private JButton pkgButton = new JButton(GUIEnv.getIcon("dots.png"));
    private JTextField pkg = new JTextField(25);
    private JTextField name = new JTextField(25);


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
        combo.setPrototypeDisplayValue("01234567890123456789");
        String[] toolboxes = tools.getToolBoxes();
        for (int count = 0; count < toolboxes.length; count++)
            model.addElement(toolboxes[count]);

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
        setLocationRelativeTo(GUIEnv.getApplicationFrame());
        pack();
        setVisible(true);
    }

    private String normalizePackage(String pkg) {
        if (pkg == null || pkg.length() == 0) {
            return "";
        }
        if (pkg.startsWith(File.separator)) {
            pkg = pkg.substring(1);
        }
        if (pkg.endsWith(File.separator)) {
            pkg = pkg.substring(0, pkg.length() - 1);
        }
        pkg = pkg.replace(File.separator, ".");
        pkg = pkg.replaceAll(",\\\\/;:?!@£$%^&*()+=-", "");
        return pkg.toLowerCase();
    }

    private String normalizeName(String name) {
        if (name == null) {
            return "";
        }
        name = name.replace(File.separator, ".");
        name = name.replaceAll(",\\\\/;:?!@£$%^&*()+=-", "");
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

            JFileChooser chooser = new JFileChooser(toolbox);
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnVal = chooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if (f != null) {
                    String pack = f.getAbsolutePath();
                    if (pack.startsWith(toolbox)) {
                        pack = pack.substring(toolbox.length(), pack.length());
                    } else {
                        OptionPane.showInformation("The package you have chosen does not exsit yet.\n" +
                                "It will be created in the selected tool box", "New Package", this);
                    }
                    pkg.setText(normalizePackage(pack));
                } else {
                    pkg.setText("");
                }
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
        tool.setToolBox(toolbox);
        tool.setToolPackage(pkg);
        tool.setToolName(name);
        go = true;

    }

}
