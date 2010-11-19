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


import org.trianacode.gui.util.Env;
import org.trianacode.gui.windows.WizardInterface;
import org.trianacode.gui.windows.WizardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

/**
 * The final tool wizard panel prompting the user to press finish to generate code
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class FinalPanel extends JPanel implements WizardPanel, ChangeListener, ActionListener {

    /**
     * a reference to the tool panel used for extracting class name etc.
     */
    private UnitPanel toolpanel;
    private GUIPanel guiPanel;

    /**
     * the labels displaying tool name, tool box and author
     */
    private JLabel toolname = new JLabel("", JLabel.LEFT);
    private JLabel toolbox = new JLabel("", JLabel.LEFT);
    private JLabel packageName = new JLabel("", JLabel.LEFT);
    private JLabel author = new JLabel("", JLabel.LEFT);
    private JPanel fileListPanel = null;
    private Vector dirItems = null;
    private Vector fileItems = null;
    private String helpFileName = "";
    private String srcFileName = "";
    private String srcFileDir = "";
    private JCheckBox placeholderChk = new JCheckBox(Env.getString("genToolPlace"), true);
    private JTextField toolPlaceHolder = new JTextField(20);
    private String currentPlaceHolder = "";
    private String guiFileName = "";
    private String baseToolboxPath;
    private String basePackagePath;

    /**
     * a flag indicating whether the final panel is displayed
     */
    private boolean displayed = false;

    /**
     * an interface to the main wizard window
     */
    private WizardInterface wizard;


    /**
     * Constructs a panel for editing general properties of a tool.
     */
    public FinalPanel(UnitPanel toolpanel, GUIPanel guiPanel) {
        this.toolpanel = toolpanel;
        this.guiPanel = guiPanel;
        initLayout();
    }

    public boolean isPlaceholderChecked() {
        return placeholderChk.isSelected();
    }

    /**
     * Lays out the panel
     */
    private void initLayout() {
        setLayout(new BorderLayout());

        JPanel toolDetails = new JPanel(new BorderLayout());
        JPanel instruct = new JPanel(new GridLayout(2, 1));
        instruct.add(new JLabel(Env.getString("toolDefComplete"), JLabel.CENTER));
        instruct.add(new JLabel(Env.getString("selectToGenerate"), JLabel.CENTER));
        instruct.setBorder(new EmptyBorder(0, 0, 10, 0));
        toolDetails.add(instruct, BorderLayout.NORTH);

        JPanel labels = new JPanel(new GridLayout(4, 1));
        labels.add(new JLabel(Env.getString("toolname") + ": ", JLabel.LEFT));
        labels.add(new JLabel(Env.getString("toolboxpath") + ": ", JLabel.LEFT));
        labels.add(new JLabel(Env.getString("unitPackage") + ": ", JLabel.LEFT));
        labels.add(new JLabel(Env.getString("author") + ": ", JLabel.LEFT));
        labels.setBorder(new EmptyBorder(0, 0, 0, 3));
        toolDetails.add(labels, BorderLayout.WEST);

        JPanel contain = new JPanel(new GridLayout(4, 1));
        contain.add(toolname);
        contain.add(toolbox);
        contain.add(packageName);
        contain.add(author);
        toolDetails.add(contain, BorderLayout.CENTER);
        add(toolDetails, BorderLayout.NORTH);

        JPanel placeholderPanel = new JPanel(new BorderLayout());
        placeholderChk.setToolTipText(Env.getString("genToolPlaceTip"));
        placeholderChk.addChangeListener(this);
        placeholderPanel.add(placeholderChk, BorderLayout.NORTH);
        JPanel inner = new JPanel(new BorderLayout());
        inner.add(toolPlaceHolder, BorderLayout.NORTH);
        toolPlaceHolder.addActionListener(this);
        placeholderPanel.add(inner, BorderLayout.CENTER);
        add(placeholderPanel, BorderLayout.CENTER);


        fileListPanel = new JPanel();
        fileListPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        add(fileListPanel, BorderLayout.SOUTH);
    }

    public void setWizardInterface(WizardInterface wizard) {
        this.wizard = wizard;
    }

    public WizardInterface getWizardInterface() {
        return wizard;
    }


    public boolean isFinishEnabled() {
        return displayed;
    }

    public boolean isNextEnabled() {
        return false;
    }

    public void panelDisplayed() {
        toolname.setText(toolpanel.getUnitName());
        toolbox.setText(toolpanel.getToolBox());
        packageName.setText(toolpanel.getUnitPackage());
        author.setText(toolpanel.getAuthor());
        if (toolPlaceHolder.getText().equals("")) {
            if (toolpanel.getUnitPackage().equals("")) {
                currentPlaceHolder = toolpanel.getUnitName();
            } else {
                currentPlaceHolder = toolpanel.getUnitPackage() + "." + toolpanel.getUnitName();
            }

            toolPlaceHolder.setText(currentPlaceHolder);
            currentPlaceHolder = getPlaceHolderFile();
        }
        createFileList();

        displayed = true;
        wizard.notifyButtonStateChange();
    }

    public void panelHidden() {
        displayed = false;
        wizard.notifyButtonStateChange();
    }

    /**
     * @return An array in order of the directories and sub directories that need to be created
     */
    public String[] getDirectoriesToCreate() {
        return (String[]) dirItems.toArray(new String[dirItems.size()]);
    }

    /**
     * @return an array item[0] is the source code file name, item[1] is the help file name
     */
    public String[] getFilesToCreate() {
        return (String[]) fileItems.toArray(new String[fileItems.size()]);
    }

    public String getHelpFileName() {
        return helpFileName;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public String getSrcFileDir() {
        return srcFileDir;
    }

    public String getGuiFileName() {
        return guiFileName;
    }

    public String getPlaceHolderToolName() {
        return toolPlaceHolder.getText();
    }

    /**
     * repacks the builder window to preferred size;
     */


    private void repack() {
        Component comp = getParent();

        while ((comp != null) && (!(comp instanceof Window))) {
            comp = comp.getParent();
        }

        ((Window) comp).pack();
    }

    /**
     * Parse the directories and files to be created and display them in the list component
     */
    private void createFileList() {
        dirItems = new Vector();
        fileItems = new Vector();
        baseToolboxPath = toolpanel.getToolBox();
        if (!baseToolboxPath.endsWith(Env.separator())) {
            baseToolboxPath = baseToolboxPath + Env.separator();
        }
        String[] splitter = toolpanel.getUnitPackage().split(".");
        basePackagePath = baseToolboxPath;
        for (int i = 0; i < splitter.length; i++) {
            basePackagePath = basePackagePath + splitter[i] + Env.separator();
            checkAndAddDir(basePackagePath);
        }

        if (placeholderChk.isSelected()) {
            checkAndAddFile(getPlaceHolderFile());
            currentPlaceHolder = getPlaceHolderFile();
        }

        String baseSrcFileDir = basePackagePath + "src" + Env.separator();
        checkAndAddDir(baseSrcFileDir);
        srcFileName = baseSrcFileDir + toolpanel.getUnitName() + ".java";
        checkAndAddFile(srcFileName);

        if (guiPanel.isGenerateCustomPanel()) {
            guiFileName = baseSrcFileDir + guiPanel.getCustomPanelName() + ".java";
            checkAndAddFile(guiFileName);
        }

        String pathname = basePackagePath + "classes" + Env.separator();
        checkAndAddDir(pathname);

        pathname = basePackagePath + "help" + Env.separator();
        checkAndAddDir(pathname);
        helpFileName = pathname + toolpanel.getHelpFile();
        checkAndAddFile(helpFileName);
        addFilesToPanel();
    }

    public String getPlaceHolderFile() {
        return baseToolboxPath + toolPlaceHolder.getText().replace('.', File.separatorChar) + ".xml";
    }

    private void checkAndAddDir(String dirName) {
        if (!dirItems.contains(dirName)) {
            if (!(new File(dirName)).exists()) {
                dirItems.add(dirName);
            }
        }
    }

    private void checkAndAddFile(String fileName) {
        if (!fileItems.contains(fileName)) {
            fileItems.add(fileName);
        }
    }

    private void addFilesToPanel() {
        fileListPanel.removeAll();
        ArrayList files = new ArrayList(dirItems);
        files.addAll(fileItems);
        Collections.sort(files);

        fileListPanel.setLayout(new GridLayout(1 + files.size(), 1));

        JPanel labelpanel = new JPanel(new BorderLayout());
        fileListPanel.add(labelpanel);

        boolean overwritten = false;

        for (Iterator iterator = files.iterator(); iterator.hasNext();) {
            String s = (String) iterator.next();
            File file = new File(s);
            JLabel label = new JLabel(s);

            if ((file.exists()) && (!file.isDirectory())) {
                label.setForeground(Color.red);
                overwritten = true;
            }

            fileListPanel.add(label);
        }

        if (overwritten) {
            labelpanel.add(new JLabel(Env.getString("createFollowing") + "/"), BorderLayout.WEST);

            JLabel label = new JLabel(Env.getString("overwritten"));
            label.setForeground(Color.red);
            labelpanel.add(label, BorderLayout.CENTER);
        } else {
            labelpanel.add(new JLabel(Env.getString("createFollowing")), BorderLayout.WEST);
        }


        repack();
    }

    public void stateChanged(ChangeEvent e) {
        toolPlaceHolder.setEnabled(placeholderChk.isSelected());
        String placeNameStr = getPlaceHolderFile();
        if (placeholderChk.isSelected()) {
            fileItems.remove(currentPlaceHolder);
            if (!fileItems.contains(placeNameStr)) {
                fileItems.add(placeNameStr);
            }
        } else {
            fileItems.remove(placeNameStr);
        }
        addFilesToPanel();
    }

    public void actionPerformed(ActionEvent e) {
        fileItems.remove(currentPlaceHolder);
        currentPlaceHolder = getPlaceHolderFile();
        fileItems.add(currentPlaceHolder);
        addFilesToPanel();
    }

}
