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

import org.trianacode.gui.windows.WizardInterface;
import org.trianacode.gui.windows.WizardPanel;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 *
 *
 * @author      Ian Wang
 * @created     4th November 2003
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */

public class CommandFinishPanel extends JPanel implements WizardPanel {

    private CommandFilePanel comfilepanel;
    private CompilationPanel comppanel;

    private JLabel taskgraphfilename = new JLabel();
    private JLabel appname = new JLabel();
    private JLabel apppackage = new JLabel();
    private JLabel outputdirname = new JLabel();

    private JPanel filespanel = new JPanel();

    /**
     * an interface to the main wizard window
     */
    private WizardInterface wizard;

    /**
     * a flag indicating whether the finish panel is visible.
     */
    private boolean displayed = false;


    public CommandFinishPanel(CommandFilePanel comfilepanel, CompilationPanel comppanel) {
        this.comfilepanel = comfilepanel;
        this.comppanel = comppanel;

        initLayout();
    }

    private void initLayout() {
        JPanel toppanel = new JPanel(new GridLayout(2, 1));
        toppanel.add(new JLabel(Env.getString("commandLineDefComplete"), JLabel.CENTER));
        toppanel.add(new JLabel(Env.getString("selectToGenerate"), JLabel.CENTER));

        JPanel labelpanel = new JPanel(new GridLayout(4, 1));
        labelpanel.add(new JLabel(Env.getString("taskgraphFile") + " :"));
        labelpanel.add(new JLabel(Env.getString("applicationName") + " :"));
        labelpanel.add(new JLabel(Env.getString("applicationPackage") + " :"));
        labelpanel.add(new JLabel(Env.getString("outputDir") + " :"));
        labelpanel.setBorder(new EmptyBorder(0, 0, 0, 3));

        JPanel fieldpanel = new JPanel(new GridLayout(4, 1));
        fieldpanel.add(taskgraphfilename);
        fieldpanel.add(appname);
        fieldpanel.add(apppackage);
        fieldpanel.add(outputdirname);

        setLayout(new BorderLayout(0, 5));
        add(toppanel, BorderLayout.NORTH);
        add(labelpanel, BorderLayout.WEST);
        add(fieldpanel, BorderLayout.CENTER);
        add(filespanel, BorderLayout.SOUTH);
    }

    private void initFilesPanel() {
        File[] files = getFiles();
        boolean overwrite = false;

        filespanel.removeAll();
        filespanel.setLayout(new GridLayout(files.length + 1, 1));

        JPanel labelpanel = new JPanel(new BorderLayout());
        filespanel.add(labelpanel);

        JLabel label;

        for (int count = 0; count < files.length; count++) {
            label = new JLabel(files[count].getAbsolutePath());

            if ((files[count].exists()) && (!files[count].isDirectory())) {
                label.setForeground(Color.red);
                overwrite = true;
            }

            filespanel.add(label);
        }

        if (overwrite) {
            labelpanel.add(new JLabel(Env.getString("createFollowing") + "/"), BorderLayout.WEST);

            label = new JLabel(Env.getString("overwritten"));
            label.setForeground(Color.red);
            labelpanel.add(label, BorderLayout.CENTER);
        } else
            labelpanel.add(new JLabel(Env.getString("createFollowing")), BorderLayout.WEST);

        repack();
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

    private File[] getFiles() {
        File outputdir = new File(comfilepanel.getJavaFileName()).getParentFile();
        ArrayList files = new ArrayList();
        String dirname;

        while ((outputdir != null) && (!outputdir.exists())) {
            files.add(0, outputdir);

            dirname = outputdir.getAbsolutePath();

            if (dirname.endsWith(File.separator))
                dirname = dirname.substring(0, dirname.lastIndexOf(File.separatorChar));

            dirname = dirname.substring(0, dirname.lastIndexOf(File.separatorChar) + 1);
            outputdir = new File(dirname);
        }

        File outfile = new File(comfilepanel.getJavaFileName());
        files.add(outfile);

        if (comppanel.isCompile())
            files.add(new File(outfile.getAbsolutePath().substring(0, outfile.getAbsolutePath().lastIndexOf(".")) + ".class"));

        if (comfilepanel.isGenerateBatchFile())
            files.add(new File(comfilepanel.getBatchFileName()));

        if (comfilepanel.isGenerateShellScript())
            files.add(new File(comfilepanel.getShellScriptName()));

        return (File[]) files.toArray(new File[files.size()]);
    }


    /**
     * repacks the builder window to preferred size;
     */
    private void repack() {
        Component comp = getParent();

        while ((comp != null) && (!(comp instanceof Window)))
            comp = comp.getParent();

        ((Window) comp).pack();
    }


    public void panelDisplayed() {
        taskgraphfilename.setText(comfilepanel.getTaskgraphFileName());
        appname.setText(comfilepanel.getApplicationName());
        apppackage.setText(comfilepanel.getApplicationPackage());
        outputdirname.setText(comfilepanel.getOutputDirectory());

        displayed = true;
        wizard.notifyButtonStateChange();

        initFilesPanel();
    }

    public void panelHidden() {
        displayed = false;
        wizard.notifyButtonStateChange();
    }


}
