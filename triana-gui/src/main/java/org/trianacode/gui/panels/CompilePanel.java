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
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.ToolTableListener;
import org.trianacode.taskgraph.tool.Toolbox;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * A panel for compiling tools and generating XML
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 2nd October
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class CompilePanel extends ParameterPanel
        implements ActionListener, ItemListener, ToolTableListener, WindowListener, FocusListener {

    public final static String JAVA_SUFFIX = ".java";
    public final static String XML_SUFFIX = ".xml";

    private static String LAST_UNITNAME = "";
    private static String LAST_UNITPACK = "";

    private static String LAST_TOOLNAME = "";
    private static String LAST_TOOLPACK = "";
    private static String LAST_TOOLFILE = "";

    private static String LAST_ARG = null;
    private static String LAST_CLASSPATH = null;

    private static boolean LAST_COMPILE = true;
    private static boolean LAST_COMPILE_GUI = true;


    /**
     * the current tool boxes
     */
    private ToolTable tools;

    /**
     * the input componenets
     */
    private JTextField unitname = new JTextField(LAST_UNITNAME, 15);
    private JTextField unitpack = new JTextField(LAST_UNITPACK, 15);
    private JButton unitbrowse = new JButton(GUIEnv.getIcon("dots.png"));

    private JCheckBox compile = new JCheckBox(Env.getString("compileSource"));
    private JTextField command = new JTextField(25);
    private JTextField classpath = new JTextField(25);
    private JButton classButton = new JButton(GUIEnv.getIcon("dots.png"));
    private JTextField arg = new JTextField(25);
    private JButton argButton = new JButton(GUIEnv.getIcon("dots.png"));

    private JButton compilerbrowse = new JButton(GUIEnv.getIcon("dots.png"));

    private JTextField toolname = new JTextField(LAST_TOOLNAME, 15);
    private JTextField toolpack = new JTextField(LAST_TOOLPACK, 15);
    private JTextField toolfile = new JTextField(LAST_TOOLFILE, 20);
    private JButton filebrowse = new JButton(GUIEnv.getIcon("dots.png"));

    private JCheckBox compileGUI = new JCheckBox(Env.getString("compileGUI"));
    private JComboBox toolBoxChooser;
    private ParameterWindow paramwin;


    public CompilePanel(ToolTable tools) {
        this.tools = tools;
        tools.addToolTableListener(this);
    }

    /**
     * This method is called when the panel is finished with.
     */
    public void dispose() {
        tools.removeToolTableListener(this);
    }

    /**
     * This method is creates the panel layout
     */
    public void init() {
        setLayout(new BorderLayout());

        JPanel mainpanel1 = new JPanel(new BorderLayout());
        mainpanel1.add(getSourcePanel(), BorderLayout.NORTH);
        mainpanel1.add(getCompilePanel(), BorderLayout.CENTER);

        JPanel mainpanel2 = new JPanel(new BorderLayout());
        mainpanel2.add(mainpanel1, BorderLayout.NORTH);
        mainpanel2.add(getGeneratePanel(), BorderLayout.CENTER);
        mainpanel2.add(getGUIPanel(), BorderLayout.SOUTH);

        add(mainpanel2, BorderLayout.NORTH);
    }

    /**
     * Constructs the panel for inputting the source file
     */
    private JPanel getSourcePanel() {
        JPanel formpanel = new JPanel(new FormLayout(3, 3));

        JPanel namepanel = new JPanel(new BorderLayout());
        namepanel.add(unitname, BorderLayout.WEST);
        unitname.setText(LAST_UNITNAME);
        unitname.addFocusListener(this);

        JPanel packpanel = new JPanel(new BorderLayout());
        packpanel.add(unitpack, BorderLayout.CENTER);
        unitpack.addFocusListener(this);
        packpanel.add(unitbrowse, BorderLayout.EAST);
        unitpack.setText(LAST_UNITPACK);

        unitbrowse.setMargin(new Insets(6, 4, 2, 4));
        unitbrowse.addActionListener(this);

        JPanel packcont = new JPanel(new BorderLayout());
        packcont.add(packpanel, BorderLayout.WEST);

        JPanel toolboxpanel = new JPanel(new BorderLayout());
        toolBoxChooser = new JComboBox(tools.getToolBoxPaths());
        toolBoxChooser.setSelectedItem(Env.getLastWorkingToolbox());
        toolBoxChooser.addItemListener(this);

        toolboxpanel.add(toolBoxChooser, BorderLayout.NORTH);

        formpanel.add(new JLabel(Env.getString("unitName")));
        formpanel.add(namepanel);

        formpanel.add(new JLabel(Env.getString("unitPackage")));
        formpanel.add(packcont);

        formpanel.add(new JLabel(Env.getString("toolbox")));
        formpanel.add(toolboxpanel);

        JPanel sourcecont2 = new JPanel(new BorderLayout());
        sourcecont2.add(formpanel, BorderLayout.NORTH);
        sourcecont2.setBorder(new EmptyBorder(0, 0, 5, 0));

        return sourcecont2;
    }


    /**
     * Constructs the panel for setting compiling options
     */
    private JPanel getCompilePanel() {
        JPanel labelpanel = new JPanel(new GridLayout(3, 1));
        labelpanel.add(new JLabel(Env.getString("compilerCommand")));
        labelpanel.add(new JLabel(Env.getString("compilerClasspath")));
        labelpanel.add(new JLabel(Env.getString("compilerArguments")));
        labelpanel.setBorder(new EmptyBorder(0, 30, 0, 3));

        JPanel commandpanel = new JPanel(new BorderLayout());
        commandpanel.add(command, BorderLayout.CENTER);
        commandpanel.add(compilerbrowse, BorderLayout.EAST);
        commandpanel.setBorder(new EmptyBorder(0, 0, 3, 0));

        JPanel commandcont = new JPanel(new BorderLayout());
        commandcont.add(commandpanel, BorderLayout.WEST);

        compilerbrowse.setMargin(new Insets(6, 4, 2, 4));
        compilerbrowse.addActionListener(this);

        command.setText(Env.getCompilerCommand());

        JPanel classpanel = new JPanel(new BorderLayout());
        classpanel.add(classpath, BorderLayout.CENTER);
        classpanel.add(classButton, BorderLayout.EAST);
        classButton.setActionCommand(Env.getString("compilerClasspath"));
        classpanel.setBorder(new EmptyBorder(0, 0, 3, 0));

        classButton.setMargin(new Insets(6, 4, 2, 4));
        classButton.addActionListener(this);


        if (LAST_CLASSPATH == null)
            classpath.setText(Env.getClasspath());
        else
            classpath.setText(LAST_CLASSPATH);

        JPanel argpanel = new JPanel(new BorderLayout());
        argpanel.add(arg, BorderLayout.CENTER);
        argpanel.add(argButton, BorderLayout.EAST);
        argButton.setActionCommand("compilerArguments");
        argButton.setMargin(new Insets(6, 4, 2, 4));
        argButton.addActionListener(this);

        if (LAST_ARG == null)
            arg.setText(Env.getJavacArgs());
        else
            arg.setText(LAST_ARG);

        command.setCaretPosition(0);
        classpath.setCaretPosition(0);
        arg.setCaretPosition(0);

        JPanel comppanel = new JPanel(new GridLayout(3, 1));
        comppanel.add(commandcont);
        comppanel.add(classpanel);
        comppanel.add(argpanel);

        compile.addItemListener(this);
        compile.setSelected(LAST_COMPILE);

        JPanel compcont = new JPanel(new BorderLayout());
        compcont.add(compile, BorderLayout.NORTH);
        compcont.add(labelpanel, BorderLayout.WEST);
        compcont.add(comppanel, BorderLayout.CENTER);

        JPanel compcont2 = new JPanel(new BorderLayout());
        compcont2.add(compcont, BorderLayout.NORTH);
        compcont2.setBorder(new EmptyBorder(0, 0, 5, 0));

        return compcont2;
    }

    /**
     * Constructs the panel for generating tool xml file
     */
    private JPanel getGeneratePanel() {
        JPanel labelpanel = new JPanel(new GridLayout(3, 1));
        labelpanel.add(new JLabel(Env.getString("toolName")));
        labelpanel.add(new JLabel(Env.getString("toolPackage")));
        labelpanel.add(new JLabel(Env.getString("toolFile")));
        labelpanel.setBorder(new EmptyBorder(0, 30, 0, 3));

        JPanel namepanel = new JPanel(new BorderLayout());
        namepanel.add(toolname, BorderLayout.WEST);
        namepanel.setBorder(new EmptyBorder(0, 0, 3, 0));
        toolname.setText(LAST_TOOLNAME);
        toolname.addFocusListener(this);

        JPanel packpanel = new JPanel(new BorderLayout());
        packpanel.add(toolpack, BorderLayout.WEST);
        toolpack.addFocusListener(this);
        packpanel.setBorder(new EmptyBorder(0, 0, 3, 0));
        toolpack.setText(LAST_TOOLPACK);

        JPanel filepanel = new JPanel(new BorderLayout());
        filepanel.add(toolfile, BorderLayout.CENTER);
        filepanel.add(filebrowse, BorderLayout.EAST);
        toolfile.setText(LAST_TOOLFILE);

        JPanel filecont = new JPanel(new BorderLayout());
        filecont.add(filepanel, BorderLayout.WEST);

        filebrowse.setMargin(new Insets(6, 4, 2, 4));
        filebrowse.addActionListener(this);

        toolname.setCaretPosition(0);
        toolpack.setCaretPosition(0);
        toolfile.setCaretPosition(0);
        JPanel genpanel = new JPanel(new GridLayout(3, 1));
        genpanel.add(namepanel);
        genpanel.add(packpanel);
        genpanel.add(filecont);

        JPanel gencont = new JPanel(new BorderLayout());
        gencont.add(labelpanel, BorderLayout.WEST);
        gencont.add(genpanel, BorderLayout.CENTER);

        JPanel gencont2 = new JPanel(new BorderLayout());
        gencont2.add(gencont, BorderLayout.NORTH);
        gencont2.setBorder(new EmptyBorder(0, 0, 5, 0));

        return gencont2;
    }

    /**
     * Constructs the panel for compiling the GUI
     */
    private JPanel getGUIPanel() {
        JPanel guipanel = new JPanel(new BorderLayout());
        guipanel.add(compileGUI, BorderLayout.WEST);

        compileGUI.setSelected(LAST_COMPILE_GUI);

        return guipanel;
    }

    /**
     * @return true if source compile is selected
     */
    public boolean isCompileSource() {
        return compile.isSelected();
    }


    /**
     * @return true if compile GUI is selected
     */
    public boolean isCompileGUI() {
        return compileGUI.isSelected();
    }


    /**
     * @return the source file path
     */
    public String getSourceFilePath() {
        return getSourceDir() + File.separatorChar + unitname.getText().replace('.', File.separatorChar) + JAVA_SUFFIX;
    }

    /**
     * @return the base directory for the package in the selected tool box
     */
    public String getBaseDir() {
        String packloc = unitpack.getText().replace('.', File.separatorChar);
        return (String) toolBoxChooser.getSelectedItem() + File.separatorChar + packloc;
    }

    /**
     * @return the source directory
     */
    public String getSourceDir() {
        String pathname = getBaseDir() + File.separatorChar + "src";
        if (!(new File(pathname).exists()))
            (new File(pathname)).mkdir();
        return pathname;
    }

    /**
     * @return the name for this unit
     */
    public String getUnitName() {
        return unitname.getText();
    }

    /**
     * Sets the name for the unit
     */
    public void setUnitName(String unitname) {
        this.unitname.setText(unitname);
        this.unitname.setCaretPosition(0);
    }

    /**
     * @return the package for this unit
     */
    public String getUnitPackage() {
        return unitpack.getText();
    }

    /**
     * Sets the name for the unit
     */
    public void setUnitPackage(String unitpack) {
        this.unitpack.setText(unitpack);
        this.unitpack.setCaretPosition(0);
    }

    /**
     * @return the toolbox for this tool
     */
    public String getToolBox() {
        return (String) toolBoxChooser.getSelectedItem();
    }

    /**
     * Sets the current toolbox
     */
    public void setToolBox(String toolbox) {
        toolBoxChooser.setSelectedItem(toolbox);
    }

    /**
     * @return the compiler command
     */
    public String getCompilerCommand() {
        return command.getText();
    }

    /**
     * Sets the compiler command
     */
    public void setCompilerCommand(String command) {
        this.command.setText(command);
        this.command.setCaretPosition(0);
    }

    /**
     * @return the compiler class path
     */
    public String getCompilerClasspath() {
        return classpath.getText();
    }

    /**
     * Sets the compiler class path
     */
    public void setCompilerClasspath(String classpath) {
        this.classpath.setText(classpath);
        this.classpath.setCaretPosition(0);
    }

    /**
     * @return the compiler arguments
     */
    public String getCompilerArguments() {
        return arg.getText();
    }

    /**
     * Sets the compiler arguments
     */
    public void setCompilerArguments(String arg) {
        this.arg.setText(arg);
        this.arg.setCaretPosition(0);
    }

    /**
     * @return the name of this tool
     */
    public String getToolName() {
        return toolname.getText();
    }

    /**
     * Sets the tool name
     */
    public void setToolName(String toolname) {
        this.toolname.setText(toolname);
        this.toolname.setCaretPosition(0);
    }

    /**
     * @return the package for this tool
     */
    public String getToolPackage() {
        return toolpack.getText();
    }

    /**
     * Sets the package for the tool
     */
    public void setToolPackage(String pack) {
        this.toolpack.setText(pack);
        this.toolpack.setCaretPosition(0);
    }

    /**
     * @return the xml file for this tool
     */
    public String getToolFile() {
        return toolfile.getText();
    }

    /**
     * Sets the xml file for the tool
     */
    public void setToolFile(String toolfile) {
        this.toolfile.setText(toolfile);
        this.toolfile.setCaretPosition(0);
    }


    /**
     * This method is called when the panel is reset or cancelled.
     */
    public void reset() {
    }


    /**
     * Auto commit is turned off
     */
    public boolean isAutoCommitByDefault() {
        return true;
    }

    /**
     * Auto commit is hidden
     */
    public boolean isAutoCommitVisible() {
        return false;
    }


    /**
     * Called when the ok button is clicked on the parameter window. Calls applyClicked by default to commit any
     * parameter changes.
     */
    public void okClicked() {
        LAST_UNITNAME = unitname.getText();
        LAST_UNITPACK = unitpack.getText();

        LAST_TOOLNAME = toolname.getText();
        LAST_TOOLPACK = toolpack.getText();
        LAST_TOOLFILE = toolfile.getText();

        Env.setCompilerCommand(command.getText());
        LAST_CLASSPATH = classpath.getText();
        LAST_ARG = arg.getText();

        LAST_COMPILE = compile.isSelected();
        LAST_COMPILE_GUI = compileGUI.isSelected();

        super.okClicked();
    }


    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == compile) {
            command.setEnabled(compile.isSelected());
            classpath.setEnabled(compile.isSelected());
            arg.setEnabled(compile.isSelected());
        }

        if (event.getSource() == toolBoxChooser) {
            Env.setLastWorkingToolbox((String) toolBoxChooser.getSelectedItem());
        }
    }


    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == unitbrowse)
            handleBrowseSource();
        else if (event.getSource() == compilerbrowse)
            handleBrowseCompiler();
        else if (event.getSource() == filebrowse)
            handleBrowseFile();
        else if (event.getSource() == classButton)
            showClasspathPanel();
        else if (event.getSource() == argButton)
            showTextEditPanel(argButton, arg.getText());
    }

    /**
     * Called when a new tool is added
     */
    public void toolAdded(Tool tool) {
        // no-op
    }

    /**
     * Called when a tool is removed
     */
    public void toolRemoved(Tool tool) {
        // no-op
    }

    /**
     * Called when a Tool Box is added
     */
    public void toolBoxAdded(Toolbox toolbox) {
        toolBoxChooser.addItem(toolbox.getPath());
    }

    /**
     * Called when a Tool Box is Removed
     */
    public void toolBoxRemoved(Toolbox toolbox) {
        toolBoxChooser.removeItem(toolbox.getPath());
    }

    /**
     * Handles browse for source file events
     */
    private void handleBrowseSource() {
        TFileChooser chooser = new TFileChooser(Env.UNIT_DIRECTORY);
        chooser.setFileFilter(new JavaFileFilter());
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle(Env.getString("selectSource"));
        chooser.setFileHidingEnabled(false);

        chooser.setCurrentDirectory(new File((String) toolBoxChooser.getSelectedItem()));

        int result = chooser.showDialog(this, Env.getString("OK"));

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String absName = file.getAbsolutePath();
            if (absName.startsWith((String) toolBoxChooser.getSelectedItem())) {
                String toolboxStr = (String) toolBoxChooser.getSelectedItem();
                String srcStr = Env.separator() + "src" + Env.separator();
                int endofpack = absName.indexOf(srcStr);

                String packageNameStr = "";
                String unitNameStr;

                if (endofpack > toolboxStr.length() + 1)
                    packageNameStr = absName.substring(toolboxStr.length() + 1, endofpack).replace(File.separatorChar, '.');

                unitNameStr = absName.substring(endofpack + srcStr.length(), absName.lastIndexOf('.')).replace(File.separatorChar, '.');

                unitname.setText(unitNameStr);
                if (unitNameStr.lastIndexOf('.') == -1)
                    toolname.setText(unitNameStr);
                else
                    toolname.setText(unitNameStr.substring(unitNameStr.lastIndexOf('.') + 1));

                String toolFileStr = absName.substring(0, endofpack + 1) + toolname.getText() + XML_SUFFIX;

                unitpack.setText(packageNameStr);
                toolpack.setText(packageNameStr);
                toolfile.setText(toolFileStr);
            } else {
                JOptionPane.showMessageDialog(this, "The selected unit package is not in your tool box path",
                        "Warning", JOptionPane.WARNING_MESSAGE, GUIEnv.getTrianaIcon());
                unitpack.setText("");
                toolpack.setText("");
            }

            unitname.setCaretPosition(0);
            unitpack.setCaretPosition(0);
            toolname.setCaretPosition(0);
            toolpack.setCaretPosition(0);
            toolfile.setCaretPosition(0);
        }
    }

    /**
     * Handles browse for compiler events
     */
    private void handleBrowseCompiler() {
        TFileChooser chooser = new TFileChooser(Env.COMPILER_DIRECTORY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle(Env.getString("selectCompiler"));

        int result = chooser.showDialog(this, Env.getString("OK"));

        if (result == JFileChooser.APPROVE_OPTION) {
            command.setText(chooser.getSelectedFile().getAbsolutePath());
            command.setCaretPosition(0);
        }
    }

    /**
     * Handles browse for file events
     */
    private void handleBrowseFile() {
        TFileChooser chooser = new TFileChooser(Env.TOOL_DIRECTORY);
        chooser.setFileFilter(new XMLFileFilter());
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle(Env.getString("selectToolFile"));

        int result = chooser.showDialog(this, Env.getString("OK"));

        if (result == JFileChooser.APPROVE_OPTION) {
            toolfile.setText(chooser.getSelectedFile().getAbsolutePath());
            toolfile.setCaretPosition(0);
        }
    }

    private void showClasspathPanel() {
        ClassPathPanel panel = new ClassPathPanel(tools);
        panel.init();
        paramwin = new ParameterWindow(this, WindowButtonConstants.OK_CANCEL_BUTTONS, false);
        paramwin.setParameterPanel(panel);
        paramwin.addWindowListener(this);
        paramwin.setTitle(classButton.getActionCommand());
        panel.setClasspath(classpath.getText());
        paramwin.setLocation((paramwin.getToolkit().getScreenSize().width / 2) - (paramwin.getSize().width / 2),
                (paramwin.getToolkit().getScreenSize().height / 2) - (paramwin.getSize().height / 2));

        paramwin.setVisible(true);
    }

    private void showTextEditPanel(JButton pressed, String text) {
        TextAreaPanel panel = new TextAreaPanel();
        panel.init();

        paramwin = new ParameterWindow(GUIEnv.getApplicationFrame(), WindowButtonConstants.OK_CANCEL_BUTTONS, false);
        paramwin.setParameterPanel(panel);
        paramwin.addWindowListener(this);
        paramwin.setTitle(pressed.getActionCommand());
        panel.setText(text);


        paramwin.setLocation((paramwin.getToolkit().getScreenSize().width / 2) - (paramwin.getSize().width / 2),
                (paramwin.getToolkit().getScreenSize().height / 2) - (paramwin.getSize().height / 2));

        paramwin.setVisible(true);

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
        if (paramwin.isAccepted()) {
            if (paramwin.getTitle().equals(classButton.getActionCommand())) {
                ClassPathPanel classPathPanel = ((ClassPathPanel) paramwin.getParameterPanel());
                String classpathStr = classPathPanel.getClasspath();
                classpath.setText(classpathStr);
                if (classPathPanel.isRetainCPCheck())
                    Env.setClasspath(classpathStr);
            } else if (paramwin.getTitle().equals(argButton.getActionCommand())) {
                arg.setText(((TextAreaPanel) paramwin.getParameterPanel()).getText());
            }
        }
    }

    public void windowOpened(WindowEvent e) {
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
        if (e.getSource() == unitname) {
            toolname.setText(unitname.getText());
            toolname.setCaretPosition(0);
        }

        if (e.getSource() == unitpack) {
            toolpack.setText(unitpack.getText());
            toolpack.setCaretPosition(0);
        }

        if (getToolBox() != null) {
            String text = checkAppendFileSeparator(getToolBox());
            text = text + checkAppendFileSeparator(getToolPackage().replace('.', File.separatorChar));
            text = text + getToolName() + XML_SUFFIX;
            toolfile.setText(text);
            toolfile.setCaretPosition(0);
        }
    }

    private String checkAppendFileSeparator(String path) {
        if (path.endsWith(Env.separator()) || path.equals(""))
            return path;
        else
            return path + Env.separator();
    }

    private static class JavaFileFilter extends javax.swing.filechooser.FileFilter {

        public boolean accept(File file) {
            return file.getName().endsWith(JAVA_SUFFIX) || file.isDirectory();
        }

        public String getDescription() {
            return ("Java Source Files");
        }

    }

    private static class XMLFileFilter extends javax.swing.filechooser.FileFilter {

        public boolean accept(File file) {
            return file.getName().endsWith(XML_SUFFIX) || file.isDirectory();
        }

        public String getDescription() {
            return ("XML Files");
        }

    }

}
