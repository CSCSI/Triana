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

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.WizardListener;
import org.trianacode.gui.windows.WizardWindow;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.CompileUtil;
import org.trianacode.util.CompilerException;
import org.trianacode.util.Env;

import javax.swing.*;
import java.awt.*;
import java.io.*;


/**
 * A class for displaying the command line application wizard, and
 * generating a command line application class.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created Today's date
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class CommandLineWizard implements WizardListener {

    /**
     * Template file names
     */
    public final static String APP_TEMPLATE = "CommandLineApp";
    public final static String BATCH_TEMPLATE = "CommandLineBatch";
    public final static String SCRIPT_TEMPLATE = "CommandLineScript";

    /**
     * Default template tags
     */
    public final static String CLASSNAME = "$CLASSNAME";
    public final static String PACKAGE = "$PACKAGE";
    public final static String TASKGRAPH = "$TASKGRAPH";
    public final static String MAPS = "$MAPS";

    public final static String APPCLASS = "$APPCLASS";

    /**
     * the panels in the command line wizard
     */
    private CommandFilePanel filepanel;
    private ParameterMapPanel mappanel;
    private CompilationPanel comppanel;
    private CommandFinishPanel finishpanel;

    /**
     * the wizard window
     */
    protected WizardWindow window;


    public CommandLineWizard(ToolTable tools, Frame owner) {
        filepanel = new CommandFilePanel();
        mappanel = new ParameterMapPanel(filepanel);
        comppanel = new CompilationPanel(tools);
        finishpanel = new CommandFinishPanel(filepanel, comppanel);

        window = new WizardWindow(owner, new JPanel[]{filepanel, mappanel, comppanel, finishpanel}, "Command Line Application Wizard");
        window.addWizardListener(this);
    }


    /**
     * Generate the source code file
     */
    private boolean generateCode() {
        try {
            String template = Env.getTemplate(APP_TEMPLATE);

            template = changeFileTags(template);
            template = changeMapTags(template);
            template = changeTaskGraphTags(template);

            File saveFile = new File(filepanel.getJavaFileName());
            if (saveFile.exists()) {
                int reply = JOptionPane.showConfirmDialog(null, "Really Overwrite " + saveFile + " ?", "File Exists Warning", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon());

                if (reply == JOptionPane.NO_OPTION) {
                    return false;
                }
            }

            PrintWriter pwriter = new PrintWriter(new FileWriter(saveFile));
            pwriter.print(template);
            pwriter.close();
            return true;
        } catch (IOException except) {
            ErrorDialog.show(except);
            return false;
        }
    }

    /**
     * Changes the file name and package tags
     */
    private String changeFileTags(String template) {
        template = template.replace(CLASSNAME, filepanel.getApplicationName());

        if (filepanel.getApplicationPackage().equals(""))
            template = template.replace(PACKAGE, "");
        else
            template = template.replace(PACKAGE, "package " + filepanel.getApplicationPackage() + ";\n");

        return template;
    }

    /**
     * Change the init maps tag
     */
    private String changeMapTags(String template) {
        String[] maps = mappanel.getMaps();
        String[] params;
        String desc;
        String val;
        String line = "";

        line += "        exec.setNumberOfRequiredArguments(" + mappanel.getNumberOfRequiredArguments() + ");\n\n";

        for (int mcount = 0; mcount < maps.length; mcount++) {
            params = mappanel.getMappedParameters(maps[mcount]);
            desc = mappanel.getDescription(maps[mcount]);

            if (!desc.equals("")) {
                line += "        exec.setDescription(\"" + maps[mcount] + "\"";
                line += ", \"" + desc + "\");\n";
            }

            for (int pcount = 0; pcount < params.length; pcount++) {
                val = mappanel.getMappedValue(maps[mcount], params[pcount]);

                line += "        exec.mapParameter(\"" + maps[mcount] + "\"";
                line += ", \"" + params[pcount] + "\"";

                if (val != null)
                    line += ", \"" + val + "\"";

                line += ");\n";
            }
        }

        // remove last newline
        line = line.substring(0, line.length() - 1);

        template = template.replace(MAPS, line);
        return template;
    }

    /**
     * Change the taskgraph string tag
     */
    private String changeTaskGraphTags(String template) throws IOException {
        String taskgraphstr = "";

        BufferedReader reader = new BufferedReader(new FileReader(filepanel.getTaskgraphFileName()));

        String line = reader.readLine();

        while (line != null) {
            line = replaceChars(line);

            taskgraphstr += "        str += \"" + line + "\";\n";

            line = reader.readLine();
        }

        template = template.replace(TASKGRAPH, taskgraphstr);
        return template;
    }

    private String replaceChars(String line) {
        int ptr = getNextCharPos(0, line);

        while (ptr > -1) {
            line = line.substring(0, ptr) + '\\' + line.substring(ptr);
            ptr = getNextCharPos(ptr + 2, line);
        }

        return line;
    }

    private int getNextCharPos(int ptr, String line) {
        if (ptr >= line.length())
            return -1;

        int qptr = line.indexOf('"', ptr);
        int sptr = line.indexOf('\\', ptr);

        if (qptr == -1)
            return sptr;
        else if (sptr == -1)
            return qptr;
        else
            return Math.min(qptr, sptr);
    }


    /**
     * Generate the batch file
     */
    private boolean generateBatchFile() {
        try {
            String template = Env.getTemplate(BATCH_TEMPLATE);

            template = changeBatchTags(template);

            File saveFile = new File(filepanel.getBatchFileName());
            if (saveFile.exists()) {
                int reply = JOptionPane.showConfirmDialog(null, "Really Overwrite " + saveFile + " ?", "File Exists Warning", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon());

                if (reply == JOptionPane.NO_OPTION) {
                    return false;
                }
            }

            PrintWriter pwriter = new PrintWriter(new FileWriter(saveFile));
            pwriter.print(template);
            pwriter.close();
            return true;
        } catch (IOException except) {
            ErrorDialog.show(except);
            return false;
        }
    }

    /**
     * Changes the tags in the batch file template
     */
    private String changeBatchTags(String template) {
        template = template.replace(APPCLASS, filepanel.getApplicationName());

        return template;
    }

    /**
     * Generate the batch file
     */
    private boolean generateShellScript() {
        try {
            String template = Env.getTemplate(SCRIPT_TEMPLATE);

            template = changeScriptTags(template);

            File saveFile = new File(filepanel.getShellScriptName());
            if (saveFile.exists()) {
                int reply = JOptionPane.showConfirmDialog(null, "Really Overwrite " + saveFile + " ?", "File Exists Warning", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon());

                if (reply == JOptionPane.NO_OPTION) {
                    return false;
                }
            }

            PrintWriter pwriter = new PrintWriter(new FileWriter(saveFile));
            pwriter.print(template);
            pwriter.close();
            return true;
        } catch (IOException except) {
            ErrorDialog.show(except);
            return false;
        }
    }

    /**
     * Changes the tags in the batch file template
     */
    private String changeScriptTags(String template) {
        template = template.replace(APPCLASS, filepanel.getApplicationName());

        return template;
    }


    /**
     * Create output directories if they do not exist
     */
    private void createDirs() {
        File file = new File(filepanel.getJavaFileName()).getParentFile();

        if (!file.exists())
            file.mkdirs();
    }

    /**
     * Generate command line application code if finish is selected
     */
    public void finishSelected(WizardWindow window) {
        createDirs();

        if (!generateCode()) {
            window.setVisible(true);
            return;
        }

        if (filepanel.isGenerateBatchFile() && (!generateBatchFile())) {
            window.setVisible(true);
            return;
        }

        if (filepanel.isGenerateShellScript() && (!generateShellScript())) {
            window.setVisible(true);
            return;
        }

        if (comppanel.isCompile()) {
            try {
                CompileUtil compiler = new CompileUtil(true);
                compiler.setJavaFile(filepanel.getJavaFileName());
                compiler.setDestDir(filepanel.getOutputDirectory());
                compiler.setCompilerLocation(comppanel.getJavaCompiler());
                compiler.setCompilerClasspath(comppanel.getClasspath() + System.getProperty("path.separator") + filepanel.getOutputDirectory());
                compiler.setCompilerArguments(comppanel.getArguments());
                compiler.compile();
            } catch (FileNotFoundException except) {
                JOptionPane.showMessageDialog(comppanel, "Error: " + filepanel.getJavaFileName() + " not found!", "File Not Found Error", JOptionPane.ERROR_MESSAGE,
                        GUIEnv.getTrianaIcon());
                window.setVisible(true);
                return;
            } catch (CompilerException except) {
                window.setVisible(true);
                return;
            }
        }

        window.dispose();
    }

    public void cancelSelected(WizardWindow window) {
    }

    public void panelSelected(WizardWindow window, int index) {
    }


    /*public static void main(String[] args) {
        new CommandLineWizard(new ServerToolTable(), null);
    }*/
}
