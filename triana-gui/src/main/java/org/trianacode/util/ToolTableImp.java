/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.util;

import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.*;
import org.trianacode.taskgraph.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 25, 2009: 5:13:34 PM
 * @date $Date:$ modified by $Author:$
 */

public class ToolTableImp extends AbstractToolTable {
    static Logger log = Logger.getLogger("org.trianacode.util.ToolTableImp");


    private ReloadToolsThread reload;

    public static String[] excludedDirectories = {"CVS", ".", "src"};

    public void init() {
        log.fine("initialising local tool table");
        reload = new ReloadToolsThread();
        reload.start();
    }


    /**
     * Add a tool box path to the current tool boxes
     * This also gets the tool class loader to find paths within
     * the tool box to add to its classpath
     * And gets the types map to parse all the class files and jar files
     * and categorize them according to their classes,superclass and interfaces.
     */
    public void addToolBox(Toolbox... box) {
        super.addToolBox(box);
        for (Toolbox toolbox : box) {
            if (!toolbox.isVirtual()) {
                File f = new File(toolbox.getPath());
                f.mkdirs();
                ToolClassLoader.getLoader().addToolBox(toolbox.getPath());
                if (f.exists() && f.length() > 0) {
                    try {
                        TypesMap.load(f);
                    } catch (IOException e) {
                        log.warning("Error loading types map: " + FileUtils.formatThrowable(e));
                    }
                }
            }
        }
        if (reload != null) {
            reload.reloadTools();
        }
    }

    /**
     * Remove a tool box path from the current tool boxes
     */
    public boolean removeToolBox(String path) {
        if (super.removeToolBox(path)) {
            if (reload != null) {
                reload.reloadTools();
            }
            return true;
        }
        return false;
    }

    /**
     * Inserts a copy of the tool into the specified package. Combined with delete
     * tool this can be used to cut/copy tools.
     *
     * @param tool    the tool being pasted
     * @param pack    the package of the pasted tool
     * @param toolbox the toolbox the tool is pasted into (ignore if irrelevant)
     */
    public void insertTool(Tool tool, String pack, String toolbox) {
        log.fine("ToolTableImp.insertTool full name:" + tool.getQualifiedToolName());
        if (toolHandler.getTool(tool.getQualifiedToolName()) != null) {
            log.fine("Not pasting. Tool already exists with name " + tool.getQualifiedToolName());
            return;
        }
        String location = getPasteFileLocation(tool.getToolName(), pack, toolbox);
        tool.setDefinitionPath(location);
        tool.setDefinitionType(Tool.DEFINITION_TRIANA_XML);

        try {
            XMLWriter writer = new XMLWriter(new BufferedWriter(new FileWriter(location)));
            writer.writeComponent(tool);
            writer.close();
        }
        catch (IOException except) {
            throw (new RuntimeException("Error writing xml for " + tool.getToolName() + ": " + except.getMessage()));
        }

        refreshLocation(location, toolbox);
    }

    /**
     * Deletes the specified tool.
     *
     * @param tool tool to be deleted
     */
    public void deleteTool(Tool tool, boolean files) {
        String path = tool.getDefinitionPath();

        if ((path != null) && (!path.equals(""))) {

            File file = new File(path);
            if (files) {
                if (file.exists()) {
                    file.delete();
                }
                purgeTool(tool);
            } else {
                Env.addExcludedTool(path);
                purgeToolRef(tool);
            }

        }
    }

    /**
     * Generate a new file location to store a pasted tool
     *
     * @param toolname tool to be created
     * @param pack     tool package name
     * @param toolbox  toolbox to create the tool in
     * @return a unused file to store the xml for the specified tool in
     */
    public String getPasteFileLocation(String toolname, String pack, String toolbox) {
        String location = toolbox;

        if (!location.endsWith(File.separator)) {
            location += File.separator;
        }

        location += pack.replace('.', File.separatorChar);

        // if paste directory doesn't exist place in toolbox root
        if (!new File(location).exists()) {
            location = toolbox;
        }

        String filename = location + File.separatorChar + convertToolName(toolname);

        if (!new File(filename + XMLWriter.XML_FILE_SUFFIX).exists()) {
            return filename + XMLWriter.XML_FILE_SUFFIX;
        }

        int count = 1;

        while (new File(filename + count + XMLWriter.XML_FILE_SUFFIX).exists()) {
            count++;
        }

        return filename + count + XMLWriter.XML_FILE_SUFFIX;
    }

    /**
     * Removes invalid characeters from a tool name
     *
     * @param toolname the tool name to be processed
     * @return processed tool name
     */
    private static String convertToolName(String toolname) {
        toolname = toolname.replace('+', ' ');
        toolname = toolname.replace('?', ' ');
        toolname = toolname.replace('/', ' ');
        toolname = toolname.replace('\\', ' ');
        toolname = toolname.replace('"', ' ');
        toolname = toolname.replace('\'', ' ');
        return toolname.replaceAll(" ", "");
    }

    /**
     * Add a tool to the tool tables that is located at the specified location
     *
     * @param toolFile the tool file
     * @param toolbox  toolbox to which the tool is to be added
     */
    protected List<Tool> addTools(File toolFile, String toolbox) {
        if ((!toolFile.exists()) || toolFile.isDirectory()) {
            log.fine("dumping file..." + toolFile);
            Env.removeExcludedTool(toolFile.getAbsolutePath());
            Tool[] referenced = toolHandler.getTools(toolFile.getAbsolutePath());
            for (Tool tool : referenced) {
                toolHandler.remove(tool);
            }
            return null;
        }
        List<ToolFormatHandler.ToolStatus> stats = null;
        try {
            stats = toolHandler.add(toolFile, toolbox);
        } catch (ToolException e) {
            //notifyToolRemoved(stats.getTool());

        }
        List<Tool> ret = new ArrayList<Tool>();
        if (stats != null) {
            for (ToolFormatHandler.ToolStatus stat : stats) {
                log.fine("ToolTableImp.addTool stats:" + stat.getStatus());
                if (stat.getStatus() == ToolFormatHandler.ToolStatus.Status.NOT_MODIFIED) {
                    continue;
                } else if (stat.getStatus() == ToolFormatHandler.ToolStatus.Status.NOT_ADDED) {
                    notifyToolRemoved(stat.getTool());
                } else if (stat.getStatus() == ToolFormatHandler.ToolStatus.Status.OK) {
                    notifyToolAdded(stat.getTool());
                }
                ret.add(stat.getTool());
            }

        }
        return ret;
    }

    protected String getToolPackageName(String xmlFilePath, String toolName) {
        String fullPath = xmlFilePath.replace(Env.separator() + toolName + XMLReader.XML_FILE_SUFFIX, "");
        return ToolTableUtils.qualifyPath(fullPath, this);
    }

    /**
     * Notifies the tool table to update the tool loaded from the specified location, such as when a
     * tool is created. The location should be in a form understanded by the tool table (e.g. XML
     * file location, tool server network address), and is ignored if not understood.
     *
     * @param location the location of the file
     * @param toolbox  the toolbox the location is in (specify null if unknown)
     */
    public void refreshLocation(String location, String toolbox) {
        if (toolbox == null) {
            toolbox = findToolBox(location);
        }

        if (toolbox != null) {
            List<Tool> tool = addTools(new File(location), toolbox);
            /*for (Tool tool1 : tool) {
                if (tool1 != null) {
                    purgeTool(tool1);
                }
            }*/
        }
    }

    /**
     * Removes the tool at the specified location from the tool/location tables if it has been
     * deleted
     */
    protected void purgeTool(Tool tool) {
        toolHandler.delete(tool);
        notifyToolRemoved(tool);
    }

    protected void purgeToolRef(Tool tool) {
        toolHandler.remove(tool);
        notifyToolRemoved(tool);
    }

    protected void addTools() {
        for (String s : toolboxes.keySet()) {
            Toolbox tb = toolboxes.get(s);
            if (!tb.isVirtual()) {
                List<File> files = find(new File(s), new String[]{".xml", ".jar", ".class"}, excludedDirectories);
                for (int i = 0; i < files.size(); i++) {
                    addTools(files.get(i), s);
                }
            }
        }
    }

    private List<File> find(File f, final String[] exts, final String[] extDirs) {
        ArrayList<File> files = new ArrayList<File>();

        if (f.isDirectory()) {
            File[] fs = f.listFiles(new FilenameFilter() {
                public boolean accept(File file, String s) {
                    if (file.isDirectory()) {
                        for (String extDir : extDirs) {
                            if (file.getName().startsWith(extDir)) {
                                return false;
                            }
                        }
                        return true;
                    } else {
                        for (String ext : exts) {
                            if (file.getName().endsWith(ext)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
            for (File file : fs) {
                files.addAll(find(file, exts, extDirs));
            }
        } else {
            files.add(f);
        }
        return files;
    }


    /**
     * A low priority thread that looks for new tools added into the toolboxes
     */
    private class ReloadToolsThread extends Thread {

        private boolean minpriority = false;
        private boolean reloadtools = true;

        private int sleepcount = 0;

        public ReloadToolsThread() {
            setName("Reload Tools Thread");
            setPriority(Thread.NORM_PRIORITY);
        }


        public void reloadTools() {
            loadTools();
            reload.setPriority(Thread.NORM_PRIORITY);
            minpriority = false;
            reloadtools = true;
        }


        public void run() {
            while (true) {
                loadTools();
                try {
                    Thread.sleep(1000 * 60);
                }
                catch (InterruptedException except) {
                }
            }
        }


        private void loadTools() {
            addTools();

            if ((!reloadtools) && (!minpriority)) {
                setPriority(Thread.MIN_PRIORITY);
                minpriority = true;
            } else {
                reloadtools = false;
            }
        }


    }
}
