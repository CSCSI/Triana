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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.TFileChooser;
import org.trianacode.gui.windows.WizardInterface;
import org.trianacode.gui.windows.WizardPanel;
import org.trianacode.util.Env;

/**
 * The panel for specifying the taskgraph that is executed from the command line.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class CommandFilePanel extends JPanel
        implements WizardPanel, ActionListener, FocusListener {

    public final static String XML_SUFFIX = ".xml";
    public final static String JAVA_SUFFIX = ".java";
    public final static String BATCH_SUFFIX = ".bat";
    public final static String SHELL_SUFFIX = "";

    private String filename = "";

    private JTextField taskgraphfield = new JTextField(25);
    private JButton taskgraphbrowse = new JButton(GUIEnv.getIcon("dots.png"));

    private JTextField appnamefield = new JTextField(15);
    private JTextField apppackagefield = new JTextField(15);
    private JTextField outputdirfield = new JTextField(25);
    private JButton outputdirbrowse = new JButton(GUIEnv.getIcon("dots.png"));

    private JCheckBox genbatch = new JCheckBox();
    private JCheckBox genscript = new JCheckBox();

    private ArrayList listeners = new ArrayList();

    /**
     * an interface to the main wizard window
     */
    private WizardInterface wizard;


    public CommandFilePanel() {
        initLayout();
    }


    public void addCommandFileListener(CommandFileListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeCommandFileListener(CommandFileListener listener) {
        listeners.remove(listener);
    }


    public void setWizardInterface(WizardInterface wizard) {
        this.wizard = wizard;
    }

    public WizardInterface getWizardInterface() {
        return wizard;
    }

    public boolean isFinishEnabled() {
        return ((!getTaskgraphFileName().equals("")) && (!getApplicationName().equals("")) &&
                (new File(getTaskgraphFileName()).exists()));
    }

    public boolean isNextEnabled() {
        return isFinishEnabled();
    }


    public String getTaskgraphFileName() {
        return taskgraphfield.getText();
    }

    public String getApplicationName() {
        return appnamefield.getText();
    }

    public String getApplicationPackage() {
        return apppackagefield.getText();
    }

    public String getOutputDirectory() {
        return outputdirfield.getText();
    }

    private File getOutputFile() {
        String filename = getApplicationName() + JAVA_SUFFIX;

        if (!apppackagefield.getText().equals("")) {
            filename = apppackagefield.getText().replace('.', File.separatorChar) + File.separatorChar + filename;
        }

        if (outputdirfield.getText().equals("")) {
            return new File(filename);
        } else {
            return new File(getOutputDirectory() + File.separatorChar + filename);
        }
    }

    public String getJavaFileName() {
        return getOutputFile().getAbsolutePath();
    }


    public boolean isGenerateBatchFile() {
        return genbatch.isSelected();
    }

    public String getBatchFileName() {
        String path = getOutputFile().getAbsolutePath();
        String filename = path.substring(path.lastIndexOf(File.separatorChar) + 1, path.lastIndexOf('.'));
        filename = Character.toLowerCase(filename.charAt(0)) + filename.substring(1);
        return path.substring(0, path.lastIndexOf(File.separatorChar) + 1) + filename + BATCH_SUFFIX;
    }


    public boolean isGenerateShellScript() {
        return genscript.isSelected();
    }

    public String getShellScriptName() {
        String path = getOutputFile().getAbsolutePath();
        String filename = path.substring(path.lastIndexOf(File.separatorChar) + 1, path.lastIndexOf('.'));
        filename = Character.toLowerCase(filename.charAt(0)) + filename.substring(1);
        return path.substring(0, path.lastIndexOf(File.separatorChar) + 1) + filename + SHELL_SUFFIX;
    }


    private void initLayout() {
        setLayout(new BorderLayout());

        add(getTaskgraphPanel(), BorderLayout.NORTH);
        add(getOutputPanel(), BorderLayout.CENTER);
        add(getBatchPanel(), BorderLayout.SOUTH);
    }

    private JPanel getTaskgraphPanel() {
        JPanel labelpanel = new JPanel(new GridLayout(1, 1));
        labelpanel.add(new JLabel(Env.getString("taskgraphFile")));
        labelpanel.setBorder(new EmptyBorder(0, 0, 0, 3));

        JPanel taskgraphpanel = new JPanel(new BorderLayout());
        taskgraphpanel.add(taskgraphfield, BorderLayout.CENTER);
        taskgraphpanel.add(taskgraphbrowse, BorderLayout.EAST);
        taskgraphfield.addFocusListener(this);

        JPanel taskgraphpanel1 = new JPanel(new BorderLayout());
        taskgraphpanel1.add(taskgraphpanel, BorderLayout.WEST);

        taskgraphbrowse.setMargin(new Insets(6, 4, 2, 4));
        taskgraphbrowse.addActionListener(this);

        JPanel fieldpanel = new JPanel(new GridLayout(1, 1));
        fieldpanel.add(taskgraphpanel1);

        JPanel mainpanel = new JPanel(new BorderLayout());
        mainpanel.add(labelpanel, BorderLayout.WEST);
        mainpanel.add(fieldpanel, BorderLayout.CENTER);

        JPanel mainpanel2 = new JPanel(new BorderLayout());
        mainpanel2.add(mainpanel, BorderLayout.WEST);
        mainpanel2.setBorder(new EmptyBorder(0, 0, 8, 0));

        return mainpanel2;
    }

    private JPanel getOutputPanel() {
        JPanel labelpanel = new JPanel(new GridLayout(3, 1, 0, 3));
        labelpanel.add(new JLabel(Env.getString("applicationName")));
        labelpanel.add(new JLabel(Env.getString("applicationPackage")));
        labelpanel.add(new JLabel(Env.getString("outputDir")));
        labelpanel.setBorder(new EmptyBorder(0, 0, 0, 3));

        JPanel appnamepanel = new JPanel(new BorderLayout());
        appnamepanel.add(appnamefield, BorderLayout.WEST);
        appnamefield.addFocusListener(this);

        JPanel apppackagepanel = new JPanel(new BorderLayout());
        apppackagepanel.add(apppackagefield, BorderLayout.WEST);

        JPanel outputdirpanel = new JPanel(new BorderLayout());
        outputdirpanel.add(outputdirfield, BorderLayout.CENTER);
        outputdirpanel.add(outputdirbrowse, BorderLayout.EAST);

        JPanel outputdirpanel1 = new JPanel(new BorderLayout());
        outputdirpanel1.add(outputdirpanel, BorderLayout.WEST);

        outputdirbrowse.setMargin(new Insets(6, 4, 2, 4));
        outputdirbrowse.addActionListener(this);

        JPanel fieldpanel = new JPanel(new GridLayout(3, 1, 0, 3));
        fieldpanel.add(appnamepanel);
        fieldpanel.add(apppackagepanel);
        fieldpanel.add(outputdirpanel1);

        JPanel mainpanel = new JPanel(new BorderLayout());
        mainpanel.add(labelpanel, BorderLayout.WEST);
        mainpanel.add(fieldpanel, BorderLayout.CENTER);

        JPanel mainpanel2 = new JPanel(new BorderLayout());
        mainpanel2.add(mainpanel, BorderLayout.WEST);

        return mainpanel2;
    }

    private JPanel getBatchPanel() {
        JPanel labelpanel = new JPanel(new GridLayout(2, 1, 0, 3));
        labelpanel.add(new JLabel(Env.getString("generateBatchFile")));
        labelpanel.add(new JLabel(Env.getString("generateShellScript")));

        JPanel checkpanel = new JPanel(new GridLayout(2, 1));
        checkpanel.add(genbatch);
        checkpanel.add(genscript);
        genbatch.setSelected(true);
        genscript.setSelected(true);

        JPanel batchpanel = new JPanel(new BorderLayout(3, 0));
        batchpanel.add(labelpanel, BorderLayout.WEST);
        batchpanel.add(checkpanel, BorderLayout.CENTER);
        batchpanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        return batchpanel;
    }


    private void setApplicationName(String filename) {
        String appname;

        if (filename.equals("")) {
            appname = "";
        } else if (filename.indexOf('.') == -1) {
            appname = filename;
        } else {
            appname = filename.substring(0, filename.indexOf('.'));
        }

        if ((!appname.equals("")) && (!Character.isUpperCase(appname.charAt(0)))) {
            appname = Character.toUpperCase(appname.charAt(0)) + appname.substring(1);
        }

        appnamefield.setText(appname);
    }


    private void handleBrowseTaskgraphs() {
        TFileChooser chooser = new TFileChooser(Env.TASKGRAPH_DIRECTORY);
        chooser.setFileFilter(new XMLFileFilter());
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle(Env.getString("selectTaskgraphFile"));

        int result = chooser.showDialog(this, Env.getString("OK"));

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            taskgraphfield.setText(file.getAbsolutePath());
            taskgraphfield.setCaretPosition(0);

            notifyFileChanged(taskgraphfield.getText());
        }
    }

    private void handleBrowseOutputDir() {
        TFileChooser chooser = new TFileChooser(Env.TASKGRAPH_DIRECTORY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle(Env.getString("selectOutputDirectory"));
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int result = chooser.showDialog(this, Env.getString("OK"));

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if (file.isDirectory()) {
                outputdirfield.setText(file.getAbsolutePath());
            } else {
                outputdirfield.setText(file.getParentFile().getAbsolutePath());
                setApplicationName(file.getName());
            }

            outputdirfield.setCaretPosition(0);
            appnamefield.setCaretPosition(0);

            wizard.notifyButtonStateChange();
        }
    }


    public void panelDisplayed() {
    }

    public void panelHidden() {
    }


    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == taskgraphbrowse) {
            handleBrowseTaskgraphs();
        } else if (event.getSource() == outputdirbrowse) {
            handleBrowseOutputDir();
        }
    }


    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        if (event.getSource() == taskgraphfield) {
            notifyFileChanged(taskgraphfield.getText());
        }

        if (event.getSource() == appnamefield) {
            wizard.notifyButtonStateChange();
        }
    }


    private void notifyFileChanged(String filename) {
        if (!filename.equals(this.filename)) {
            this.filename = filename;

            File file = new File(filename);

            setApplicationName(file.getName());

            if (file.getParentFile() != null) {
                outputdirfield.setText(file.getParentFile().getAbsolutePath());
            } else {
                outputdirfield.setText("");
            }

            for (Iterator iter = listeners.iterator(); iter.hasNext();) {
                ((CommandFileListener) iter.next()).commandFileChanged(filename);
            }

            wizard.notifyButtonStateChange();
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
