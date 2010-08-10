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
package org.trianacode.gui.appmaker;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.ClassPathPanel;
import org.trianacode.gui.panels.TFileChooser;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.ParameterWindowListener;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;

/**
 * The panel for specifying the taskgraph that is executed from the command line.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class CompilationPanel extends JPanel
        implements ActionListener, ParameterWindowListener, ItemListener {

    private JCheckBox enable = new JCheckBox();

    private JTextField javacfield = new JTextField(25);
    private JTextField classpathfield = new JTextField(25);
    private JTextField argfield = new JTextField(25);
    private JButton javacbrowse = new JButton(GUIEnv.getIcon("dots.png"));
    private JButton classpathbrowse = new JButton(GUIEnv.getIcon("dots.png"));

    private ParameterWindow classpathwin;

    /**
     * the main tool table (used by the class path panel to retrieve the tool box paths)
     */
    private ToolTable tools;


    public CompilationPanel(ToolTable tools) {
        this.tools = tools;
        initLayout();
    }


    public boolean isCompile() {
        return enable.isSelected();
    }

    public String getJavaCompiler() {
        return javacfield.getText();
    }

    public String getClasspath() {
        return classpathfield.getText();
    }

    public String getArguments() {
        return argfield.getText();
    }


    private void initLayout() {
        setLayout(new BorderLayout());

        add(getCompilerPanel(), BorderLayout.NORTH);
    }

    private JPanel getCompilerPanel() {
        JPanel enablepanel = new JPanel(new BorderLayout());
        enablepanel.add(enable, BorderLayout.WEST);
        enablepanel.add(new JLabel(Env.getString("compileApplicationSource")), BorderLayout.CENTER);
        enablepanel.setBorder(new EmptyBorder(0, 0, 3, 0));
        enable.addItemListener(this);
        enable.setSelected(true);

        JPanel labelpanel = new JPanel(new GridLayout(3, 1));
        labelpanel.add(new JLabel(Env.getString("compiler")));
        labelpanel.add(new JLabel(Env.getString("classpath")));
        labelpanel.add(new JLabel(Env.getString("arguments")));
        labelpanel.setBorder(new EmptyBorder(0, 15, 0, 3));

        JPanel fieldpanel = new JPanel(new GridLayout(3, 1, 0, 3));

        JPanel javacpanel = new JPanel(new BorderLayout());
        javacpanel.add(javacfield, BorderLayout.CENTER);
        javacpanel.add(javacbrowse, BorderLayout.EAST);
        javacfield.setText(Env.getCompilerCommand());
        javacbrowse.addActionListener(this);
        javacbrowse.setMargin(new Insets(6, 4, 2, 4));

        JPanel classpathpanel = new JPanel(new BorderLayout());
        classpathpanel.add(classpathfield, BorderLayout.CENTER);
        classpathpanel.add(classpathbrowse, BorderLayout.EAST);
        classpathfield.setText(Env.getClasspath());
        classpathbrowse.addActionListener(this);
        classpathbrowse.setMargin(new Insets(6, 4, 2, 4));

        JPanel argpanel = new JPanel(new BorderLayout());
        argpanel.add(argfield, BorderLayout.WEST);
        argfield.setText(Env.getJavacArgs());

        fieldpanel.add(javacpanel);
        fieldpanel.add(classpathpanel);
        fieldpanel.add(argpanel);

        JPanel mainpanel = new JPanel(new BorderLayout());
        mainpanel.add(enablepanel, BorderLayout.NORTH);
        mainpanel.add(labelpanel, BorderLayout.WEST);
        mainpanel.add(fieldpanel, BorderLayout.CENTER);

        JPanel mainpanel2 = new JPanel(new BorderLayout());
        mainpanel2.add(mainpanel, BorderLayout.WEST);
        mainpanel2.setBorder(new EmptyBorder(0, 0, 8, 0));

        return mainpanel2;
    }


    private void handleBrowseCompiler() {
        TFileChooser chooser = new TFileChooser(Env.COMPILER_DIRECTORY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle(Env.getString("selectCompiler"));

        int result = chooser.showDialog(this, Env.getString("OK"));

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            javacfield.setText(file.getAbsolutePath());
            javacfield.setCaretPosition(0);
        }
    }

    private void handleBrowseClasspath() {
        ClassPathPanel panel = new ClassPathPanel(tools);
        panel.init();
        classpathwin = new ParameterWindow(this, WindowButtonConstants.OK_CANCEL_BUTTONS, false);
        classpathwin.setParameterPanel(panel);
        classpathwin.addParameterWindowListener(this);
        classpathwin.setTitle(Env.getString("classpath") + "...");
        panel.setClasspath(classpathfield.getText());
        classpathwin
                .setLocation((classpathwin.getToolkit().getScreenSize().width / 2) - (classpathwin.getSize().width / 2),
                        (classpathwin.getToolkit().getScreenSize().height / 2) - (classpathwin.getSize().height / 2));

        classpathwin.setVisible(true);
    }


    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == javacbrowse) {
            handleBrowseCompiler();
        } else if (event.getSource() == classpathbrowse) {
            handleBrowseClasspath();
        }
    }

    public void parameterWindowHidden(ParameterWindow window) {
        if (window == classpathwin) {
            if (classpathwin.isAccepted()) {
                ClassPathPanel classPathPanel = ((ClassPathPanel) classpathwin.getParameterPanel());
                String classpathStr = classPathPanel.getClasspath();
                classpathfield.setText(classpathStr);

                if (classPathPanel.isRetainCPCheck()) {
                    Env.setClasspath(classpathStr);
                }
            }

            classpathwin.dispose();
        }
    }

    /**
     * Invoked when an item has been selected or deselected by the user. The code written for this method performs the
     * operations that need to occur when an item is selected (or deselected).
     */
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == enable) {
            javacfield.setEnabled(enable.isSelected());
            javacbrowse.setEnabled(enable.isSelected());
            classpathfield.setEnabled(enable.isSelected());
            classpathbrowse.setEnabled(enable.isSelected());
            argfield.setEnabled(enable.isSelected());
        }
    }
}
