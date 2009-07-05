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

import org.trianacode.gui.panels.CompilePanel;
import org.trianacode.gui.panels.ScrollingMessageFrame;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.ParameterWindowListener;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolClassLoader;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.CompileUtil;
import org.trianacode.util.CompilerException;
import org.trianacode.util.Env;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * The class responsible for co-ordinating the compiling of tools and the
 * generation of tool XML
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 2nd October 2002
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class CompileHandler implements ParameterWindowListener {

    /**
     * the main compiler panel
     */
    private CompilePanel panel;


    /**
     * Compile options
     */
    private boolean compileSource = true;
    private String sourceFilePath;
    private String destFilePath;
    private String sourceDir;
    private String toolFile;
    private String toolName;
    private String toolPackage;
    private String toolBox;
    private String unitPackage;
    private boolean compileGUI = true;

    private boolean automatedCompile = false;

    private String unitName;
    private CompileUtil compiler;

    private ScrollingMessageFrame errorFrame;
    private Tool targetTool;

    /**
     * Construct a compile handler that will be used to automatically compile tools. Set the tool and then call compile,
     * errors will be reported but ignored, messages will be appended to a dialog.
     *
     * @param automatedCompile if <b>true</b> reports will be append to a dialog and compilation won't be interupted
     */
    public CompileHandler(boolean automatedCompile) {
        this.automatedCompile = automatedCompile;

        if (automatedCompile) {
            init();
        }
    }

    /**
     * Construct a compiler handler for compiling a specified tool
     */
    public CompileHandler(Tool tool, ToolTable tools) {
        this(false);

        if (panel == null) {
            panel = new CompilePanel(tools);
            panel.init();
        }

        compileTargetTool(tool);

        showParameterWindow();
    }

    private void init() {
        compiler = new CompileUtil(true);
        compiler.setCompilerLocation(Env.getCompilerCommand());
        compiler.setCompilerClasspath(Env.getClasspath() + System.getProperty("path.separator") + ToolClassLoader.getLoader().getClassPath());
        compiler.setCompilerArguments(Env.getJavacArgs());

        errorFrame = new ScrollingMessageFrame("Automated Compile - Error Messages", false);

    }


    public void compileTargetTool(Tool tool) {
        if ((tool != null) && (tool.getProxy() instanceof JavaProxy)) {
            if (targetTool == null)
                targetTool = tool;
            synchronized (targetTool) {
                targetTool = tool;
                JavaProxy proxy = (JavaProxy) tool.getProxy();
                unitName = proxy.getUnitName();
                toolName = tool.getToolName();

                if (unitName.indexOf('.') > 0)
                    unitName = unitName.substring(unitName.lastIndexOf('.') + 1);

                if (toolName.indexOf('.') > 0)
                    toolName = toolName.substring(toolName.lastIndexOf('.') + 1);

                unitPackage = proxy.getUnitPackage().replace('.', File.separatorChar);
                toolBox = tool.getToolBox();
                toolPackage = tool.getToolPackage();
                toolFile = tool.getDefinitionPath();
                sourceDir = computeSrcFileLocation(toolBox, unitPackage);
                if (sourceDir != null) {
                    sourceFilePath = sourceDir + File.separatorChar + unitName + ".java";
                }
                destFilePath = computeDestFileLocation(toolBox);

                if (panel != null) {
                    panel.setUnitName(unitName);
                    panel.setUnitPackage(unitPackage);
                    panel.setToolBox(toolBox);
                    panel.setToolName(toolName);
                    panel.setToolPackage(toolPackage);
                    panel.setToolFile(toolFile);
                }

                if (automatedCompile)
                    compile();
            }
        }
    }

    private String computeDestFileLocation(String toolbox) {
        File f = new File(toolbox);
        if (!checkDir(f)) {
            return null;
        }
        File cls = new File(f, "classes");
        if (!checkDir(cls)) {
            cls = findDir(f, "classes");
        }
        if (cls == null) {
            cls = new File(f, "classes");
            cls.mkdirs();
        }
        return cls.getAbsolutePath();
    }

    private String computeSrcFileLocation(String toolBox, String packageString) {
        File f = new File(toolBox);
        if (!checkDir(f)) {
            return null;
        }
        File src = new File(toolBox, "src");
        if (!checkDir(src)) {
            src = findDir(f, "src");
        }
        File srcDir = src;

        File main = new File(src, "main");
        if (checkDir(main)) {
            File java = new File(main, "java");
            if (checkDir(java)) {
                // looks like maven
                srcDir = java;
            }
        }
        return srcDir.getAbsolutePath() + File.separator + packageString;
    }

    private File findDir(File parent, String name) {
        File[] files = parent.listFiles(new FilenameFilter() {

            public boolean accept(File file, String s) {
                if (!file.isDirectory() || s.startsWith(".") || s.startsWith("CVS")) {
                    return false;
                }
                return true;
            }
        });
        if (files == null) {
            return null;
        }
        for (File file : files) {
            if (file.getName().equals(name)) {
                return file;
            }
        }
        for (File file : files) {

            File f = findDir(file, name);
            if (f != null) {
                return f;
            }

        }
        return null;
    }

    private boolean checkDir(File f) {
        if (!f.exists() || f.length() == 0 || !f.isDirectory()) {
            return false;
        }
        return true;
    }


    private void showParameterWindow() {
        ParameterWindow paramwin;

        paramwin = new ParameterWindow(GUIEnv.getApplicationFrame(), WindowButtonConstants.OK_CANCEL_BUTTONS, false);
        paramwin.setTitle(Env.getString("compileGenerate"));
        paramwin.setParameterPanel(panel);
        paramwin.addParameterWindowListener(this);

        paramwin.setLocation((paramwin.getToolkit().getScreenSize().width / 2) - (paramwin.getSize().width / 2),
                (paramwin.getToolkit().getScreenSize().height / 2) - (paramwin.getSize().height / 2));

        paramwin.setVisible(true);

    }


    public void parameterWindowHidden(ParameterWindow paramwin) {
        if (paramwin.isAccepted()) {
            if (panel.isCompileSource()) {
                if (!(new File(panel.getSourceFilePath()).exists())) {
                    new ErrorDialog(Env.getString("compileError"), Env.getString("compileError2") + ":\n " + panel.getSourceFilePath());
                    paramwin.setVisible(true);
                    return;
                }
            }
            compileSource = panel.isCompileSource();
            sourceFilePath = panel.getSourceFilePath();
            sourceDir = panel.getSourceDir();
            toolFile = panel.getToolFile();
            toolName = panel.getToolName();
            toolPackage = panel.getToolPackage();
            toolBox = panel.getToolBox();
            unitPackage = panel.getUnitPackage();
            compileGUI = panel.isCompileGUI();
            unitName = panel.getUnitName();

            compile();

        }
    }

    public void compile() {
        try {
            if (compileSource) {
                if (!automatedCompile) {
                    compiler = new CompileUtil(sourceFilePath, true);
                    compiler.setCompilerLocation(panel.getCompilerCommand());
                    compiler.setCompilerClasspath(panel.getCompilerClasspath() + System.getProperty("path.separator") + ToolClassLoader.getLoader().getClassPath());
                    compiler.setCompilerArguments(panel.getCompilerArguments());
                } else
                    compiler.setJavaFile(sourceFilePath);
                compiler.setDestDir(destFilePath);
                compiler.setSourcepath(sourceDir);
                compiler.compile();
            }

            if (compileGUI) {

                //compileGUI(tool);
            }

        } catch (IOException except) {
            if (automatedCompile) {
                errorFrame.setVisible(true);
                errorFrame.println(except.getMessage());
            } else {
                except.printStackTrace();
                new ErrorDialog(Env.getString("compileError"), except.getMessage());
                showParameterWindow();
            }
        } /*catch (TaskGraphException except) {
            if (automatedCompile)
                errorFrame.println(except.getMessage());
            else {
                new ErrorDialog(Env.getString("taskGraphError"), except.getMessage());
                showParameterWindow();
            }
        } */ catch (CompilerException except) {
            if (automatedCompile) {
                errorFrame.setVisible(true);

                errorFrame.println(except.getMessage());
            } else
                showParameterWindow();
        }

    }


    public void compileGUI(Tool tool) throws FileNotFoundException, CompilerException {
        if (compileGUI && tool.isParameterName(Tool.PARAM_PANEL_CLASS)) {
            String panelclass = (String) tool.getParameter(Tool.PARAM_PANEL_CLASS);
            panelclass = panelclass.substring(panelclass.lastIndexOf('.') + 1);

            String sourcepath = sourceDir + Env.separator() + panelclass + ".java";

            if (!(new File(sourcepath).exists())) {
                String errorMsg = Env.getString("compileError3") + ", " + sourcepath + " does not exist";
                if (automatedCompile) {
                    errorFrame.setVisible(true);
                    errorFrame.println(errorMsg);
                } else {
                    new ErrorDialog(Env.getString("compileError"), errorMsg);
                    showParameterWindow();
                }
                return;
            }

            if (automatedCompile) {
                compiler.setJavaFile(sourcepath);
            } else {
                compiler = new CompileUtil(sourcepath, true);
                compiler.setCompilerLocation(panel.getCompilerCommand());
                //TODO
                compiler.setCompilerClasspath(panel.getCompilerClasspath() + System.getProperty("path.separator") + ToolClassLoader.getLoader().getClassPath());
                compiler.setCompilerArguments(panel.getCompilerArguments());
            }
            compiler.setDestDir(destFilePath);
            compiler.setSourcepath(sourceDir);
            compiler.compile();
        }
    }


}
