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
package org.trianacode.gui.action.tools;

import org.trianacode.gui.appmaker.CommandLineWizard;
import org.trianacode.gui.hci.CompileHandler;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.tool.FileToolboxLoader;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class that handles events and actions for the Tools menu, including New Tool, Compile and Edit Tool Box Paths.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public class ToolsMenuHandler implements ActionListener {

    private ToolTable tools;

    public ToolsMenuHandler(ToolTable tools) {
        this.tools = tools;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        String label = e.getActionCommand();
        if (label.equals(Env.getString("newUnit"))) {
            newTool();
        } else if (label.equals(Env.getString("compileGenerate"))) {
            compile();
        } else if (label.equals(Env.getString("compileAll"))) {
            rebuildAllTools();
        } else if (label.equals(Env.getString("editToolBoxPaths"))) {
            editToolBoxPaths(FileToolboxLoader.LOCAL_TYPE);
        } else if (label.equals(Env.getString("generateCommandLineApp"))) {
            generateCommandLine();
        } else {
            editToolBoxPaths(label);
        }
    }

    private void rebuildAllTools() {
        final CompileHandler compiler = new CompileHandler(true, tools);
        Thread thread = new Thread() {
            public void run() {

                Tool[] toolsByName = tools.getTools();
                for (int j = 0; j < toolsByName.length; j++) {
                    Tool tool = toolsByName[j];
                    compiler.compileTargetTool(tool);
                }

            }
        };
        thread.setName("Toolbox Rebuild Thread");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }


    /**
     * Generate the XML for a tool and add it to the toolbox
     */
    public void compile() {
        final Tool[] ts = GUIEnv.getApplicationFrame().getSelectedTools();
        if (ts == null || ts.length == 0) {
            JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(), "Please Select a Tool from the workspace",
                    "Select", JOptionPane.INFORMATION_MESSAGE,
                    GUIEnv.getTrianaIcon());
        } else {
            final CompileHandler compiler = new CompileHandler(true, tools);
            Thread thread = new Thread() {
                public void run() {
                    for (int i = 0; i < ts.length; i++) {
                        Tool selectedTool = ts[i];
                        if ((selectedTool != null) && (!(selectedTool instanceof TaskGraph))) {
                            compiler.compileTargetTool(selectedTool);
                        }
                    }
                }
            };
            thread.setName("Build Tools Thread");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }

    public void editToolBoxPaths(String label) {
        new ToolBoxHandler(tools, label);
    }

    /**
     * Generate a command line application using the command line app wizard
     */
    private void generateCommandLine() {
        new CommandLineWizard(tools, GUIEnv.getApplicationFrame());
    }

    /**
     * Uses tool wizard to generate a new tool
     */
    public void newTool() {
        //TODO
        //new UnitWizard(tools, GUIEnv.getApplicationFrame());
    }

}
