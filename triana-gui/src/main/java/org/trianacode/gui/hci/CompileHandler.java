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
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.util.CompileUtil;
import org.trianacode.util.CompilerException;
import org.trianacode.util.Env;

import java.io.*;

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
     * the currently loaded tools
     */
    private ToolTable tools;

    /**
     * Compile options
     */
    private boolean compileSource = true;
    private String destinationDir;
    private String sourceFilePath;
    private String sourceDir;
    private String toolFile;
    private String toolName;
    private String toolPackage;
    private String toolBox;
    private String unitPackage;
    private boolean compileGUI = true;

    private boolean automatedCompile = false;
    private boolean generateXML = true;

    private String unitName;
    private CompileUtil compiler;

    private ScrollingMessageFrame errorFrame;
    private Tool targetTool;

    /**
     * Construct a compile handler that will be used to automatically compile tools. Set the tool and then call compile,
     * errors will be reported but ignored, messages will be appended to a dialog.
     *
     * @param tools            the system ToolTable
     * @param automatedCompile if <b>true</b> reports will be append to a dialog and compilation won't be interupted
     */
    public CompileHandler(ToolTable tools, boolean automatedCompile) {
        this(tools, automatedCompile, true);
    }

    public CompileHandler(ToolTable tools, boolean automatedCompile, boolean generateXML) {
        this.tools = tools;
        this.automatedCompile = automatedCompile;
        this.generateXML = generateXML;

        if (automatedCompile)
            init();
    }

    /**
     * Construct a compiler handler for compiling a yet to be specified tool
     */
    public CompileHandler(ToolTable tools) {
        this(null, tools);
    }

    /**
     * Construct a compiler handler for compiling a specified tool
     */
    public CompileHandler(Tool tool, ToolTable tools) {
        this(tools, false, true);

        if (panel == null) {
            panel = new CompilePanel(tools);
            panel.init();
        }

        compileTargetTool(tool);

        showParameterWindow();
    }

    private void init() {
        destinationDir = Env.getToolClassesDestinationDir();
        compiler = new CompileUtil(destinationDir, true, true);
        compiler.setCompilerLocation(Env.getCompilerCommand());
        compiler.setCompilerClasspath(Env.getClasspath() + System.getProperty("path.separator") + destinationDir);
        compiler.setCompilerArguments(Env.getJavacArgs());

        errorFrame = new ScrollingMessageFrame("Automated Compile - Error Messages");

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
                toolFile = tool.getToolXMLFileName();
                sourceDir = toolBox + File.separatorChar + unitPackage + File.separatorChar + "src";
                sourceFilePath = sourceDir + File.separatorChar + unitName + ".java";

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
            destinationDir = panel.getDestinationDir();
            sourceFilePath = panel.getSourceFilePath();
            sourceDir = panel.getSourceDir();
            toolFile = panel.getToolFile();
            toolName = panel.getToolName();
            toolPackage = panel.getToolPackage();
            toolBox = panel.getToolBox();
            unitPackage = panel.getUnitPackage();
            compileGUI = panel.isCompileGUI();
            generateXML = panel.isGenerateXML();
            unitName = panel.getUnitName();

            compile();

        }
    }

    public void compile() {
        try {
            if (compileSource) {
                if (!(new File(destinationDir).exists()))
                    new File(destinationDir).mkdirs();

                if (!automatedCompile) {
                    compiler = new CompileUtil(sourceFilePath, destinationDir, true);
                    compiler.setCompilerLocation(panel.getCompilerCommand());
                    compiler.setCompilerClasspath(panel.getCompilerClasspath() + System.getProperty("path.separator") + destinationDir);
                    compiler.setCompilerArguments(panel.getCompilerArguments());
                } else
                    compiler.setJavaFile(sourceFilePath);

                compiler.setSourcepath(sourceDir);
                compiler.compile();
            }

            if (generateXML) {
                Tool prototype = null;

                if (new File(toolFile).exists()) {
                    try {
                        XMLReader reader = new XMLReader(new FileReader(toolFile));
                        prototype = reader.readComponent();
                    } catch (IOException except) {
                    }
                }

                if (prototype == null)
                    prototype = new ToolImp();

                prototype.setToolName(toolName);
                prototype.setToolPackage(toolPackage);
                prototype.setToolBox(toolBox);
                prototype.setProxy(new JavaProxy(parseSrcForQualifiedUnit(), unitPackage));

                Tool tool = generateToolXML(prototype, toolFile, tools);
                compileGUI(tool);
            }
//        } catch (NullPointerException except) {
//            if (automatedCompile)
//                errorFrame.println("NPE, cannot instantiate: " + unitPackage.replace(File.separatorChar, '.') + "." + unitName);
//            else {
//                new ErrorDialog("NPE, cannot instantiate: " + unitPackage.replace(File.separatorChar, '.') + "." + unitName);
//                showParameterWindow();
//            }
        } catch (IOException except) {
            if (automatedCompile)
                errorFrame.println(except.getMessage());
            else {
                new ErrorDialog(Env.getString("compileError"), except.getMessage());
                showParameterWindow();
            }
        } catch (TaskGraphException except) {
            if (automatedCompile)
                errorFrame.println(except.getMessage());
            else {
                new ErrorDialog(Env.getString("taskGraphError"), except.getMessage());
                showParameterWindow();
            }
        } catch (CompilerException except) {
            if (automatedCompile)
                errorFrame.println(except.getMessage());
            else
                showParameterWindow();
        }

    }


    private String parseSrcForQualifiedUnit() throws IOException {
        String qualUnitName = "";
        String sourceFile = sourceFilePath;

        BufferedReader reader = FileUtils.createReader(sourceFile);
        StreamTokenizer token = new StreamTokenizer(reader);
        token.eolIsSignificant(false);
        token.slashSlashComments(true);
        token.slashStarComments(true);
        token.lowerCaseMode(false);
        token.wordChars((int) '_', (int) '_');

        boolean found = false;
        int currTok;
        try {
            while ((!found) && ((currTok = token.nextToken()) != StreamTokenizer.TT_EOF)) {
                if ((currTok == StreamTokenizer.TT_WORD) && (token.sval.equals("package"))) {
                    token.nextToken();
                    qualUnitName = token.sval;
                    found = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeReader(reader);
        }

        if (!found)
            qualUnitName = unitPackage;

        if (qualUnitName.equals(""))
            return unitName;
        else
            return qualUnitName + '.' + unitName;
    }

    public void compileGUI(Tool tool) throws FileNotFoundException, CompilerException {
        if (compileGUI && tool.isParameterName(Tool.PARAM_PANEL_CLASS)) {
            String panelclass = (String) tool.getParameter(Tool.PARAM_PANEL_CLASS);
            panelclass = panelclass.substring(panelclass.lastIndexOf('.') + 1);

            String sourcepath = sourceDir + Env.separator() + panelclass + ".java";

            if (!(new File(sourcepath).exists())) {
                String errorMsg = Env.getString("compileError3") + ", " + sourcepath + " does not exist";
                if (automatedCompile) {
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
                compiler = new CompileUtil(sourcepath, destinationDir, true);
                compiler.setCompilerLocation(panel.getCompilerCommand());
                compiler.setCompilerClasspath(panel.getCompilerClasspath() + System.getProperty("path.separator") + destinationDir);
                compiler.setCompilerArguments(panel.getCompilerArguments());
            }
            compiler.setSourcepath(sourceDir);
            compiler.compile();
        }
    }

    /**
     * Generate an XML file for the specified tool.
     *
     * @see Tool getPrototypeTool(String unitname, String unitpack, String toolname, String toolpack)
     */
    public Tool generateToolXML(Tool prototype, String xmlfile, ToolTable tools) throws TaskException {
        prototype.setToolXMLFileName(xmlfile);

        TaskGraph taskgraph = TaskGraphManager.createTaskGraph(TaskGraphManager.TOOL_DEF_FACTORY_TYPE);

        Task task = taskgraph.createTask(prototype, false);

        tools.refreshLocation(xmlfile, prototype.getToolBox());

        return task;
    }

}
