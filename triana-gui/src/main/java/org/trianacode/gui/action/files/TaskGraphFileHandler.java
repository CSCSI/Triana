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
package org.trianacode.gui.action.files;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicFileChooserUI;
import org.trianacode.gui.Display;
import org.trianacode.gui.TrianaDialog;
import org.trianacode.gui.action.SelectionManager;
import org.trianacode.gui.extensions.FileImportExportDecorator;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.hci.TrianaProgressBar;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.panels.ParameterPanelImp;
import org.trianacode.gui.panels.TFileChooser;
import org.trianacode.gui.panels.ToolPanel;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.SaveToolDialog;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;

/**
 * A class to handle the loading and saving of TaskGraph files.
 *
 * @author Matthew Shields
 * @version $Revision: 4051 $
 */
public class TaskGraphFileHandler implements SelectionManager {

    private static String IMPORT_TOOL_DIR = "importToolFrom";
    private static String IMPORT_TASKGRAPH_DIR = "importTaskGraphFrom";
    private static String EXPORT_TASKGRAPH_DIR = "exportTaskGraphTo";

    public TaskGraphFileHandler() {
    }

    /**
     * @return The object that is selected for this handler.
     */
    public Object getSelectionHandler() {
        return this;
    }

    /**
     * Save a taskgraph/group to a file.
     *
     * @param task         The Triana Tool object we want to save
     * @param file         the file to save to
     * @param tools        The tooltable (can be null)
     * @param updateRecent if true then the file is added to the recent items list
     */
    public static void saveTaskGraphAs(final Tool task, final String file, final ToolTable tools,
                                       final boolean updateRecent) {
        XMLWriter writer = null;
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
            writer = new XMLWriter(fileWriter);
            writer.writeComponent(task);

            if (tools != null) {
                tools.refreshLocation(new File(file).getAbsolutePath(), null);
            }

            if (updateRecent) {
                GUIEnv.getApplicationFrame().getTrianaMenuBar().updateRecentMenu(file);
            }
        }
        catch (IOException e) {
            e.printStackTrace(System.out);
        }
        finally {
            try {
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }

    }

    /**
     * Load a taskgraph from a file
     *
     * @param file the file to load from
     */
    public static void backgroundOpenTaskgraph(final File file) {
        backgroundOpenTaskgraph(file, true);
    }

    /**
     * Open a group from file/network/toolbox or view a group on a MainTriana.
     */
    public static void open() {
        File selectedFile = selectFile();
        if (selectedFile != null) {
            backgroundOpenTaskgraph(selectedFile);
        }
    }

    private static File selectFile() {
        TFileChooser chooser = new TFileChooser(Env.TASKGRAPH_DIRECTORY);
        chooser.setDialogTitle("Open TaskGraph");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new XMLFileFilter());

        int result = chooser.showOpenDialog(GUIEnv.getApplicationFrame());
        File selectedFile = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
        }
        return selectedFile;
    }


    public static TaskGraph openTaskgraph(final File file, final boolean updateRecent) {
        return openTaskGraph(file, updateRecent, true);
    }

    /**
     * Open a taskgraph file and render it.
     *
     * @param file         the file which the taskgraph is to be loaded from
     * @param updateRecent update the recently opened menu item
     * @param runnable     if false then the taskgraph is non-runnable, used for debugging or visualisation
     * @return a reference to the loaded taskgraph
     */
    private static TaskGraph openTaskGraph(File file, boolean updateRecent, boolean runnable) {
        XMLReader reader = null;
        TrianaProgressBar progressBar = null;
        try {
            BufferedReader filereader = new BufferedReader(new FileReader(file));
            reader = new XMLReader(filereader);
            if (updateRecent) {
                GUIEnv.getApplicationFrame().getTrianaMenuBar().updateRecentMenu(file.getAbsolutePath());
            }
        }
        catch (IOException e) {
            System.out.println(file + " : not found");
        }

        try {
            Tool tool = reader.readComponent();

            if (tool instanceof TaskGraph) {
                progressBar = new TrianaProgressBar("loading: " + file.getName(), false);
                tool.setDefinitionPath(file.getPath());
                TaskGraph initgraph = null;
                if (runnable) {
                    GUIEnv.getApplicationFrame().addParentTaskGraphPanel((TaskGraph) tool);
                } else {
                    GUIEnv.getApplicationFrame().addNoExecParentTaskGraphPanel((TaskGraph) tool);
                }
                return initgraph;
            } else {
                JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(),
                        "Error: " + file.getName() + " is not a valid taskgraph file", "Open Error",
                        JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
            }
        }
        catch (TaskGraphException except) {
            new ErrorDialog(Env.getString("inputError"), except.getMessage());
        }
        catch (IOException e) {
            System.err.println(e.getMessage() + " in file: " + file);
            e.printStackTrace(System.out);
        }
        finally {
            if (progressBar != null) {
                progressBar.disposeProgressBar();
            }
            try {
                reader.close();
            }
            catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
        return null;
    }

    private static void backgroundOpenTaskgraph(final File file, final boolean updateRecent) {
        Thread thread = new Thread() {
            public void run() {
                openTaskgraph(file, updateRecent);
            }
        };
        thread.setName("TaskGraphLoader" + file);
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    /**
     * Show the import file dialog and import the workflow/task graph using the selected
     * <code>FileImportExportDecorator.importWorkflow</code> method.
     */
    public static void importTaskgraph() {
        final TFileChooser fc = new TFileChooser(IMPORT_TASKGRAPH_DIR);
        final FileImportExportDecorator chooser = new FileImportExportDecorator(fc);
        int result = chooser.showImportDialog(GUIEnv.getApplicationFrame());
        if (result == TFileChooser.APPROVE_OPTION) {
            Thread thread = new Thread() {
                public void run() {
                    TrianaProgressBar progressBar = null;
                    String filename = "";
                    try {
                        filename = fc.getSelectedFile().getName();
                        progressBar = new TrianaProgressBar("loading: " + filename, false);
                        GUIEnv.getApplicationFrame().addParentTaskGraphPanel(chooser.importWorkflow());
                    }
                    catch (TaskGraphException except) {
                        new ErrorDialog(Env.getString("inputError"), except.getMessage());
                    }
                    catch (IOException e) {
                        System.out.println(e.getMessage() + " in file: " + filename);
                        e.printStackTrace(System.out);
                    }
                    finally {
                        if (progressBar != null) {
                            progressBar.disposeProgressBar();
                        }
                    }

                }
            };
            thread.setName("Import Task Graph");
            thread.setPriority(Thread.NORM_PRIORITY);
            thread.start();
        }
    }

    /**
     * Show the export file dialog and export the current selected task graph using the selected
     * <code>FileImportExportDecorator.exportWorkflow</code> method.
     */
    public static void exportTaskgraph() {
        final TFileChooser fc = new TFileChooser(EXPORT_TASKGRAPH_DIR);
        final FileImportExportDecorator chooser = new FileImportExportDecorator(fc);
        int result = chooser.showExportDialog(GUIEnv.getApplicationFrame());

        if (result == TFileChooser.APPROVE_OPTION) {
            Thread thread = new Thread() {
                public void run() {
                    TrianaProgressBar pb = null;
                    String filename = "";
                    try {
                        // Bug fix for not picking up file name text.
                        if (fc.getUI() instanceof BasicFileChooserUI) {
                            filename = ((BasicFileChooserUI) fc.getUI()).getFileName();
                        }

                        pb = new TrianaProgressBar("exporting: " + filename, false);
                        chooser.exportWorkflow(GUIEnv.getApplicationFrame().getSelectedTaskGraphPanel().getTaskGraph());
                    }
                    catch (IOException e) {
                        ErrorDialog.show("Error Exporting TaskGraph", e.getMessage());
                        e.printStackTrace();
                    }
                    catch (TaskGraphException e) {
                        ErrorDialog.show("Error Exporting TaskGraph", e.getMessage());
                        e.printStackTrace();
                    }
                    finally {
                        if (pb != null) {
                            pb.disposeProgressBar();
                        }
                    }
                }
            };
            thread.setName("Export Task Graph");
            thread.setPriority(Thread.NORM_PRIORITY);
            thread.start();
        }
    }

    /**
     * Show the import fie dialog and import the tools from the selected file using the selected
     * <code>FileImportExportDecorator.importTools</code> method. The imported tools are saved to the user selected
     * toolbox in Triana format.
     *
     * @param tooltable
     */
    public void importTools(final ToolTable tooltable) {
        final TFileChooser fc = new TFileChooser(IMPORT_TOOL_DIR);
        final FileImportExportDecorator chooser = new FileImportExportDecorator(fc);
        int result = chooser.showImportToolDialog(GUIEnv.getApplicationFrame());
        if (result == TFileChooser.APPROVE_OPTION) {
            final ImportInfoHandler handler = new ImportInfoHandler(tooltable, chooser.getDefaultToolPackage());

            if (handler.isApproved()) {
                Thread thread = new Thread() {
                    public void run() {
                        TrianaProgressBar progressBar = null;
                        String filename = "";
                        try {
                            filename = fc.getSelectedFile().getName();
                            progressBar = new TrianaProgressBar("loading: " + filename, false);
                            Tool[] tools = chooser.importTools(handler.getPackage());
                            for (int i = 0; i < tools.length; i++) {
                                Tool tool = tools[i];
                                XMLWriter writer = null;
                                String dir = handler.getToolBox() + Env.separator() + tool.getToolPackage().replace('.',
                                        File.separatorChar);
                                File f = new File(dir);
                                if (f.exists() || f.mkdirs()) {
                                    try {
                                        String toolfile = dir + Env.separator() + tool.getToolName() + ".xml";
                                        if (TrianaDialog.isOKtoWriteIfExists(toolfile)) {
                                            f = new File(toolfile);
                                            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(f));
                                            writer = new XMLWriter(fileWriter);
                                            writer.writeComponent(tool);
                                            tooltable.refreshLocation(toolfile, handler.getToolBox());
                                        }
                                    }
                                    catch (IOException e) {
                                        e.printStackTrace(System.out);
                                    }

                                }

                            }
                        }
                        catch (TaskGraphException except) {
                            new ErrorDialog(Env.getString("inputError"), except.getMessage());
                        }
                        catch (IOException e) {
                            System.out.println(e.getMessage() + " in file: " + filename);
                            e.printStackTrace(System.out);
                        }
                        finally {
                            if (progressBar != null) {
                                progressBar.disposeProgressBar();
                            }
                        }
                    }
                };
                thread.setName("Import Tools");
                thread.setPriority(Thread.NORM_PRIORITY);
                thread.start();
            }
        }
    }

    /**
     * rudimentary at the moment just pops up a dialog, no checking to see if this is an existing group that has been
     * modified
     */
    public static void saveTaskGraph(TaskGraph taskgraph, ToolTable tools, boolean saveas) {
        TaskGraph group = taskgraph;

        if (group.getParent() != null) {
            TaskGraph parent = group.getParent();

            while (parent.getParent() != null) {
                parent = parent.getParent();
            }

            String[] options = new String[]{group.getToolName(), parent.getToolName() + " (root)", "Cancel"};
            String title = "Save...";

            if (saveas) {
                title = "Save As...";
            }

            int choice = JOptionPane.showOptionDialog(GUIEnv.getApplicationFrame(),
                    "Save current group or root taskgraph?", title, JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon(), options, options[0]);

            if (choice == 1) {
                group = parent;
            } else if (choice == 2) {
                return;
            }
        }
        String definitionPath = group.getDefinitionPath();

        boolean showdialog = saveas || (definitionPath == null) || (definitionPath.equals("") || !group
                .getDefinitionType().equals(Tool.DEFINITION_TRIANA_XML));
        boolean writeFile = true;

        if (showdialog) {
            writeFile = false;
            SaveToolDialog dialog = new SaveToolDialog(group, tools);
            if (dialog.isGo()) {
                String toolbox = group.getToolBox();
                File dir = new File(toolbox, group.getToolPackage().replace(".", File.separator));
                dir.mkdirs();
                definitionPath = dir.getAbsolutePath() + File.separator + group.getToolName() + ".xml";
                group.setDefinitionPath(definitionPath);
                if (TrianaDialog.isOKtoWriteIfExists(definitionPath)) {
                    writeFile = true;
                } else {
                    writeFile = false;
                }
            }
        }

        if (writeFile) {
            backGroundSaveTaskGraph(taskgraph, definitionPath, tools, true);
        }
    }


    private static void backGroundSaveTaskGraph(final Tool task, final String file, final ToolTable tools,
                                                final boolean updateRecent) {
        Thread thread = new Thread() {
            public void run() {
                saveTaskGraphAs(task, file, tools, updateRecent);
            }
        };
        thread.setName("Load TaskGraph: " + file);
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    /**
     * Utility method for opening a non-functioning taskgraph for display purposes only
     */
    public static void openForDisplayOnly() {
        final File selectedFile = selectFile();
        if (selectedFile != null) {
            Thread thread = new Thread() {
                public void run() {
                    openTaskGraph(selectedFile, false, false);
                }
            };
            thread.setName("Load TaskGraph: " + selectedFile);
            thread.setPriority(Thread.NORM_PRIORITY);
            thread.start();
        }
    }

    private static class XMLFileFilter extends FileFilter {

        private static final String XML_EXTENSION = ".xml";

        /**
         * Whether the given file is accepted by this filter.
         */
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().toLowerCase().endsWith(XML_EXTENSION);
        }

        /**
         * The description of this filter. For example: "JPG and GIF Images"
         *
         * @see javax.swing.filechooser.FileView#getName
         */
        public String getDescription() {
            return "XML Files";
        }

    }


    private class ImportInfoHandler {

        private ToolPanel toolpanel;
        private ParameterWindow window;

        public ImportInfoHandler(ToolTable tools, String defpack) {
            toolpanel = new ToolPanel(tools, false);
            toolpanel.setPackage(defpack);

            ParameterPanel panel = new ParameterPanelImp();
            panel.setLayout(new BorderLayout());
            panel.add(toolpanel, BorderLayout.CENTER);

            window = new ParameterWindow(GUIEnv.getApplicationFrame(), WindowButtonConstants.OK_CANCEL_BUTTONS, true);
            window.setTitle("Import Into...");
            window.setParameterPanel(panel);

            Display.centralise(window);
            window.setVisible(true);
        }


        public boolean isApproved() {
            return window.isAccepted();
        }


        public String getPackage() {
            return toolpanel.getPackage();
        }

        public String getToolBox() {
            return toolpanel.getToolBox();
        }
    }

}
