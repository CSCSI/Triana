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

import org.trianacode.gui.SpringUtilities;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.hci.color.ColorManager;
import org.trianacode.gui.hci.color.ColorModel;
import org.trianacode.gui.hci.color.ColorTable;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Hashtable;

/**
 * The main options panel for Triana user settings
 *
 * @author Matthew Shields
 * @version $Revsion$
 */
public class OptionsPanel extends ParameterPanel implements ActionListener, WindowListener {

    private static String VALIDATE_TOOLS = "validate_external_tool_locations";
    private String invalidToolName;

    private ParameterWindow paramwin;
    private ToolTable tools;

    private JCheckBox autoconnectChk;
    private JCheckBox restoreChk;
    private JCheckBox enableTipsChk;
    private JCheckBox enableExtendedTipsChk;
    private JCheckBox showNodeEditIconsChk;
    private JCheckBox convertToDoubleChk;
    private JCheckBox smoothCables;
    private JTextField htmlViewerTextField;
    private JTextField htmlEditorTextField;
    private JTextField codeEditorTextField;
    private JTextField javacTextField;
    private JCheckBox validateToolsChk;
    private JButton classpathButton;
    private ColorModel[] registeredColorModels;
    private Hashtable colorModelPanels = new Hashtable();
    private Dimension swatchSize = new Dimension(50, 20);
    private JPanel generalPanel;
    private JPanel colorPanel;
    private JPanel externalPanel;

    public void okClicked() {
        GUIEnv.getApplicationFrame().repaintWorkspace();
        super.okClicked();
    }

    public OptionsPanel(ToolTable tools) {
        this.tools = tools;
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
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("General", getGeneralPanel());
        //tabs.addTab("External Tools", getExternalTools());
        tabs.addTab("Colours", getColourChooser());

        this.setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }

    /**
     * Instantiates the general options panel
     */
    private JPanel getGeneralPanel() {
        if (generalPanel == null) {
            JPanel panel = new JPanel(new GridLayout(7, 1));
            autoconnectChk = addCheckBox(panel, Env.getString("autoConnect"), GUIEnv.isAutoConnect());
            restoreChk = addCheckBox(panel, Env.getString("restoreLast"), GUIEnv.restoreLast());
            enableTipsChk = addCheckBox(panel, Env.getString("showToolTips"), GUIEnv.showPopUpDescriptions());
            enableExtendedTipsChk = addCheckBox(panel, Env.getString("showExtendedTips"),
                    GUIEnv.showExtendedDescriptions());
            showNodeEditIconsChk = addCheckBox(panel, Env.getString("showNodeEditIcons"), GUIEnv.showNodeEditIcons());
            convertToDoubleChk = addCheckBox(panel, Env.getString("convertToDouble"), Env.getConvertToDouble());
            smoothCables = addCheckBox(panel, "Smooth Cables", GUIEnv.isSmoothCables());
            generalPanel = new JPanel(new BorderLayout());
            generalPanel.add(panel, BorderLayout.NORTH);
        }
        return generalPanel;
    }

    private JPanel getColourChooser() {
        if (colorPanel == null) {
            colorPanel = new JPanel(new BorderLayout());
            final JPanel centre = new JPanel();
            colorPanel.add(centre, BorderLayout.CENTER);
            registeredColorModels = ColorManager.getRegisteredColorModels();
            String[] modelNames = new String[registeredColorModels.length];
            for (int i = 0; i < registeredColorModels.length; i++) {
                ColorModel registeredColorModel = registeredColorModels[i];
                modelNames[i] = registeredColorModel.getModelName();
            }
            final JList modelList = new JList(modelNames);
            modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            modelList.setVisibleRowCount(-1);
            modelList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    centre.removeAll();
                    centre.add(getColorModelView(registeredColorModels[modelList.getSelectedIndex()]));
                    repaint();
                }
            });
            JScrollPane scroller = new JScrollPane(modelList);
            colorPanel.add(scroller, BorderLayout.WEST);
            modelList.setSelectedIndex(0);
            JButton resetBtn = new JButton(Env.getString("Reset"));
            resetBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ColorTable.instance().reset();
                    centre.removeAll();
                    colorModelPanels.clear();
                    centre.add(getColorModelView(registeredColorModels[modelList.getSelectedIndex()]));
                    repaint();
                }
            });
            JPanel small = new JPanel();
            small.add(resetBtn);
            colorPanel.add(small, BorderLayout.EAST);
        }
        return colorPanel;
    }

    private JPanel getColorModelView(ColorModel registeredColorModel) {
        JPanel panel = (JPanel) colorModelPanels.get(registeredColorModel.getModelName());
        if (panel == null) {
            String[] colorNames = registeredColorModel.getColorNames();
            panel = new JPanel(new SpringLayout());
            for (int i = 0; i < colorNames.length; i++) {
                String colorName = colorNames[i];
                panel.add(new JLabel(colorName));
                JPanel swatch = new JPanel();
                swatch.setBackground(ColorTable.instance().getColor(registeredColorModel, colorName));
                swatch.setBorder(BorderFactory.createEtchedBorder());
                panel.add(swatch);
                JButton chooserBtn = new JButton(GUIEnv.getIcon("dots.png"));
                panel.add(chooserBtn);
                chooserBtn.addActionListener(new ColorChangeListener(registeredColorModel, colorName, swatch));
                swatch.setPreferredSize(swatchSize);
            }
            SpringUtilities.makeCompactGrid(panel, colorNames.length, 3, 2, 2, 2, 2);
            colorModelPanels.put(registeredColorModel.getModelName(), panel);
        }

        return panel;
    }

    /**
     * Simple option validation, checks to see if external tools exist or if the fields have been left with their
     * default values. It is up to the user to check that the external tool is capable of interacting with triana.
     *
     * @return true if the simple validation is succesful, false otherwise.
     */
    public boolean validateChanges() {
        boolean valid = true;
        //Env.setUserProperty(VALIDATE_TOOLS, String.valueOf(validateToolsChk.isSelected()));
        GUIEnv.setAutoConnect(autoconnectChk.isSelected());
        GUIEnv.setRestoreLast(restoreChk.isSelected());
        GUIEnv.setPopUpDescriptions(enableTipsChk.isSelected());
        GUIEnv.setExtendedDescriptions(enableExtendedTipsChk.isSelected());
        GUIEnv.setNodeEditIcons(showNodeEditIconsChk.isSelected());
        GUIEnv.setSmoothCables(smoothCables.isSelected());
        Env.setConvertToDouble(convertToDoubleChk.isSelected());

//        if (validateToolsChk.isSelected() && !testValidTool(htmlViewerTextField)) {
//            invalidToolName = htmlViewerTextField.getText();
//            htmlViewerTextField.setText(Env.getString("defaultViewer"));
//            htmlViewerTextField.setCaretPosition(0);
//            valid = false;
//        } else {
//            if(htmlViewerTextField.getText().trim().length() == 0) {
//                htmlViewerTextField.setText(Env.getString("defaultViewer"));
//            } else {
//                GUIEnv.setHTMLViewerCommand(htmlViewerTextField.getText());
//            }
//        }
//
//        if (validateToolsChk.isSelected() && !testValidTool(htmlEditorTextField)) {
//            if (valid) {
//                valid = false;
//                invalidToolName = htmlEditorTextField.getText();
//                htmlEditorTextField.setText(Env.getString("defaultEditor"));
//                htmlEditorTextField.setCaretPosition(0);
//            }
//        } else {
//            if (htmlEditorTextField.getText().trim().length() == 0) {
//                htmlEditorTextField.setText(Env.getString("defaultEditor"));
//            } else {
//                GUIEnv.setHTMLEditorCommand(htmlEditorTextField.getText());
//            }
//        }
//
//        if (validateToolsChk.isSelected() && !testValidTool(codeEditorTextField)) {
//            if (valid) {
//                valid = false;
//                invalidToolName = codeEditorTextField.getText();
//                codeEditorTextField.setText(Env.getString("defaultEditor"));
//                codeEditorTextField.setCaretPosition(0);
//            }
//        } else {
//            if (codeEditorTextField.getText().trim().length() == 0) {
//                codeEditorTextField.setText(Env.getString("defaultEditor"));
//            } else {
//                GUIEnv.setJavaEditorCommand(codeEditorTextField.getText());
//            }
//        }
//
//        if (validateToolsChk.isSelected() && !testValidTool(javacTextField)) {
//            if (valid) {
//                valid = false;
//                invalidToolName = javacTextField.getText();
//                javacTextField.setText(Env.getCompilerCommand());
//                javacTextField.setCaretPosition(0);
//            }
//        } else {
//            if (javacTextField.getText().trim().length() == 0) {
//                javacTextField.setText(Env.getCompilerCommand());
//            } else {
//                Env.setCompilerCommand(javacTextField.getText());
//            }
//        }
        return true;
    }


    /**
     * @return the name of the last tool to fail validation
     */
    public String getInvalidToolName() {
        return invalidToolName;
    }

    /**
     * @see #validateChanges tester for the individual fields
     */
    private boolean testValidTool(JTextField tool) {
        if (tool.getText().trim().length() == 0) {
            return true;
        }
        if (tool.getText().equals(Env.getString("defaultEditor"))
                || tool.getText().equals(Env.getString("defaultViewer"))) {
            return true;
        } else if ((new File(tool.getText())).exists()) {
            return true;
        }
        return false;
    }

    /**
     * Utility method to add a JCheckbox to a panel
     */
    private JCheckBox addCheckBox(JPanel parent, String text, boolean selected) {
        JCheckBox chk = new JCheckBox(text, selected);
        parent.add(chk);
        return chk;
    }

    /**
     * initialises and returns the panel that contains the external tools
     */
    private JPanel getExternalTools() {
        if (externalPanel == null) {
            JPanel formpanel = new JPanel(new FormLayout(3, 3));

            // Help Viewer
            formpanel.add(new JLabel(Env.getString("helpViewer"), JLabel.LEFT));

            JPanel intern = new JPanel(new BorderLayout());
            htmlViewerTextField = new JTextField(GUIEnv.getHTMLViewerCommand());
            intern.add(htmlViewerTextField, BorderLayout.CENTER);

            JButton browseButton = new JButton(GUIEnv.getIcon("dots.png"));
            browseButton.setActionCommand(Env.getString("helpViewer"));
            browseButton.addActionListener(this);
            browseButton.setMargin(new Insets(6, 4, 2, 4));

            intern.add(browseButton, BorderLayout.EAST);
            formpanel.add(intern);

            // HTML Editor
            formpanel.add(new JLabel(Env.getString("htmlEditor"), JLabel.LEFT));

            intern = new JPanel(new BorderLayout());
            htmlEditorTextField = new JTextField(GUIEnv.getHTMLEditorCommand());
            intern.add(htmlEditorTextField, BorderLayout.CENTER);

            browseButton = new JButton(GUIEnv.getIcon("dots.png"));
            browseButton.setActionCommand(Env.getString("htmlEditor"));
            browseButton.addActionListener(this);
            browseButton.setMargin(new Insets(6, 4, 2, 4));

            intern.add(browseButton, BorderLayout.EAST);
            formpanel.add(intern);

            // Code editor
            formpanel.add(new JLabel(Env.getString("codeEditor"), JLabel.LEFT));

            intern = new JPanel(new BorderLayout());
            codeEditorTextField = new JTextField(GUIEnv.getJavaEditorCommand());
            intern.add(codeEditorTextField, BorderLayout.CENTER);

            browseButton = new JButton(GUIEnv.getIcon("dots.png"));
            browseButton.setActionCommand(Env.getString("codeEditor"));
            browseButton.addActionListener(this);
            browseButton.setMargin(new Insets(6, 4, 2, 4));

            intern.add(browseButton, BorderLayout.EAST);
            formpanel.add(intern);

            // Java Compiler
            formpanel.add(new JLabel(Env.getString("javaCompiler"), JLabel.LEFT));

            intern = new JPanel(new BorderLayout());
            javacTextField = new JTextField(Env.getCompilerCommand());
            intern.add(javacTextField, BorderLayout.CENTER);

            browseButton = new JButton(GUIEnv.getIcon("dots.png"));
            browseButton.setActionCommand(Env.getString("javaCompiler"));
            browseButton.addActionListener(this);
            browseButton.setMargin(new Insets(6, 4, 2, 4));

            intern.add(browseButton, BorderLayout.EAST);
            formpanel.add(intern);
            formpanel.setBorder(new EmptyBorder(0, 0, 7, 0));

            // Classpath Button
            formpanel.add(new JLabel(""));
            classpathButton = new JButton(Env.getString("compilerClasspath"));
            intern = new JPanel(new BorderLayout());
            intern.add(classpathButton, BorderLayout.EAST);
            classpathButton.addActionListener(this);
            formpanel.add(intern);


            // Validate Tools & Reset
            validateToolsChk = new JCheckBox("Validate External Tool Locations",
                    Env.getBooleanUserProperty(VALIDATE_TOOLS, true));
            intern = new JPanel(new BorderLayout());
            intern.add(validateToolsChk, BorderLayout.WEST);
            browseButton = new JButton(Env.getString("restoreDefaults"));
            browseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    resetToDefaults();
                }
            });
            intern.add(browseButton, BorderLayout.EAST);

            JPanel finalPanel = new JPanel(new BorderLayout());
            finalPanel.add(formpanel, BorderLayout.CENTER);
            finalPanel.add(intern, BorderLayout.SOUTH);
            finalPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

            externalPanel = finalPanel;
        }
        return externalPanel;
    }

    private void resetToDefaults() {
        htmlViewerTextField.setText(Env.getString("defaultViewer"));
        htmlViewerTextField.setCaretPosition(0);
        htmlEditorTextField.setText(Env.getString("defaultEditor"));
        htmlEditorTextField.setCaretPosition(0);
        codeEditorTextField.setText(Env.getString("defaultEditor"));
        codeEditorTextField.setCaretPosition(0);
        javacTextField.setText(Env.getDefaultCompilerCommand());
        javacTextField.setCaretPosition(0);
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

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == classpathButton) {
//            handleClasspath();
//        } else {
//            TFileChooser chooser = new TFileChooser();
//            chooser.setMultiSelectionEnabled(false);
//            chooser.setDialogTitle("Select " + e.getActionCommand());
//            chooser.setApproveButtonText(Env.getString("OK"));
//            int result = chooser.showOpenDialog(this);
//
//            if (result == JFileChooser.APPROVE_OPTION) {
//                if (e.getActionCommand().equals(Env.getString("helpViewer"))) {
//                    htmlViewerTextField.setText(chooser.getSelectedFile().getAbsolutePath());
//                    htmlViewerTextField.setCaretPosition(0);
//
//                } else if (e.getActionCommand().equals(Env.getString("htmlEditor"))) {
//                    htmlEditorTextField.setText(chooser.getSelectedFile().getAbsolutePath());
//                    htmlEditorTextField.setCaretPosition(0);
//
//                } else if (e.getActionCommand().equals(Env.getString("codeEditor"))) {
//                    codeEditorTextField.setText(chooser.getSelectedFile().getAbsolutePath());
//                    codeEditorTextField.setCaretPosition(0);
//
//                } else if (e.getActionCommand().equals(Env.getString("javaCompiler"))) {
//                    javacTextField.setText(chooser.getSelectedFile().getAbsolutePath());
//                    javacTextField.setCaretPosition(0);
//
//                }
//            }
//        }
    }

    private void handleClasspath() {
        ClassPathPanel panel = new ClassPathPanel(tools);
        panel.init();
        paramwin = new ParameterWindow(this, WindowButtonConstants.OK_CANCEL_BUTTONS, true);
        paramwin.setParameterPanel(panel);
        paramwin.addWindowListener(this);
        paramwin.setTitle(Env.getString("selectPath"));
        panel.setClasspath(Env.getClasspath());
        paramwin.setLocation((paramwin.getToolkit().getScreenSize().width / 2) - (paramwin.getSize().width / 2),
                (paramwin.getToolkit().getScreenSize().height / 2) - (paramwin.getSize().height / 2));

        paramwin.setVisible(true);

    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
//        if (paramwin.isAccepted()) {
//            ClassPathPanel classPathPanel = ((ClassPathPanel) paramwin.getParameterPanel());
//            if (classPathPanel.isRetainCPCheck()) {
//                Env.setClasspath(classPathPanel.getClasspath());
//            }
//        }
    }

    private class ColorChangeListener implements ActionListener {

        private ColorModel model;
        private String colorName;
        private JPanel swatch;

        public ColorChangeListener(ColorModel model, String colorName, JPanel swatch) {
            this.model = model;
            this.colorName = colorName;
            this.swatch = swatch;
        }

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            Color color = JColorChooser.showDialog(swatch, colorName, swatch.getBackground());
            if (color != null) {
                swatch.setBackground(color);
                ColorTable.instance().setColor(model, colorName, color);
                swatch.repaint();
            }
        }
    }

}
