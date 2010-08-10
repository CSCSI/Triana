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
package org.trianacode.gui.hci;

import java.io.File;

import org.trianacode.gui.TrianaDialog;
import org.trianacode.gui.panels.PastePanel;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.ParameterWindowListener;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;

/**
 * Handler class responsible for displaying the paste UI and responding to it.
 *
 * @author Matthew Shields
 * @version $Revsion:$
 */
public class PasteHandler {

    /**
     * The paste UI
     */
    private PastePanel panel = null;

    /**
     * the main window
     */
    private ParameterWindow paramwin;

    /**
     * the tool table
     */
    private ToolTable tooltable;

    /**
     * the paste handler title
     */
    private String title;

    /**
     * Constructor
     *
     * @param tooltable the currently loaded tools
     * @param title     the title of the paste window
     */
    public PasteHandler(ToolTable tooltable, String title) {
        this.tooltable = tooltable;
        this.title = title;
    }


    /**
     * @param tools       the tools being pasted
     * @param toolPackage the suggested package for the tools to be pasted into
     */
    public void handlePaste(Tool[] tools, String toolPackage) {
        panel = new PastePanel(tooltable, toolPackage);
        panel.init();

        paramwin = new ParameterWindow(GUIEnv.getApplicationFrame(), WindowButtonConstants.OK_CANCEL_BUTTONS, true);
        paramwin.setTitle(title);
        paramwin.setParameterPanel(panel);
        paramwin.addParameterWindowListener(new ParameterWindowListenerImp(tools));

        paramwin.setLocation((paramwin.getToolkit().getScreenSize().width / 2) - (paramwin.getSize().width / 2),
                (paramwin.getToolkit().getScreenSize().height / 2) - (paramwin.getSize().height / 2));

        paramwin.setVisible(true);
    }


    private class ParameterWindowListenerImp implements ParameterWindowListener {

        private Tool[] tools;

        public ParameterWindowListenerImp(Tool[] tools) {
            this.tools = tools;
        }

        public void parameterWindowHidden(ParameterWindow window) {
            if (paramwin.isAccepted()) {
                String toolbox = panel.getToolBox();
                String[] packageNames = panel.getUnitPackageNames();
                String pack = convertToPackageNameStr(packageNames);

                if (panel.createMissingDirs()) {
                    createDirs(toolbox, packageNames);
                }

                if (pack != null) {
                    for (int count = 0; count < tools.length; count++) {
                        String loc = tooltable.getPasteFileLocation(tools[count].getToolName(), pack, toolbox);
                        if (!loc.equals(tools[count].getDefinitionPath())) {
                            if (TrianaDialog.isOKtoWriteIfExists(loc)) {
                                tools[count].setToolPackage(pack);
                                tooltable.insertTool(tools[count], pack, toolbox);
                            }
                        }
                    }
                }
            }
        }

        /**
         * Create any missing directories in the toolbox that are needed for a real package structure.
         *
         * @param toolbox
         * @param packagenames
         */
        private void createDirs(String toolbox, String[] packagenames) {
            String path = toolbox;
            for (int i = 0; i < packagenames.length; i++) {
                if (!path.endsWith(Env.separator())) {
                    path += Env.separator();
                }
                path += packagenames[i];
            }
            File packDir = new File(path);
            if (!(packDir.exists() && packDir.isDirectory())) {
                packDir.mkdirs();
            }
        }

        /**
         * Converts an array of package names to a '.' delimited string
         */
        private String convertToPackageNameStr(String[] packageNames) {
            String pack = "";
            for (int i = 0; i < packageNames.length - 1; i++) {
                pack += packageNames[i];
                pack += ".";
            }
            if (packageNames.length != 0) {
                pack += packageNames[packageNames.length - 1];
            }
            return pack;
        }
    }

}