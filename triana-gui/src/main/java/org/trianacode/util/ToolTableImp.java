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

import org.trianacode.taskgraph.imp.tool.creators.JavaReader;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.util.FileUtils;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    static Logger logger = Logger.getLogger("org.trianacode.util.ToolTableImp");


    private ReloadToolsThread reload;
    public static String[] excludedDirectories = {"classes", "help", "src", "lib", "CVS", ".svn"};
    /**
     * A hashtable of tools, indexed by their xml file location
     */
    protected Map<String, ToolInfo> locationTable = new ConcurrentHashMap<String, ToolInfo>(100);

    public void init() {
        logger.fine("initialising local tool table");
        reload = new ReloadToolsThread();
        reload.start();
    }


    /**
     * Add a tool box path to the current tool boxes
     */
    public void addToolBox(String path) {
        logger.fine("adding toolbox path:" + path);
        super.addToolBox(path);
        if (reload != null) {
            reload.reloadTools();
        }
    }

    /**
     * Remove a tool box path from the current tool boxes
     */
    public void removeToolBox(String path) {
        super.removeToolBox(path);
        if (reload != null) {
            reload.reloadTools();
        }
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
        String location = getPasteFileLocation(tool.getToolName(), pack, toolbox);

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
                purgeTool(path);
            } else {
                Env.addExcludedTool(path);
                purgeTool(path);
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
    protected void addTool(File toolFile, String toolbox) {
        if ((!toolFile.exists()) || toolFile.isDirectory()) {
            Env.removeExcludedTool(toolFile.getAbsolutePath());
            return;
        }

        String xmlFilePath = toolFile.getAbsolutePath();
        if (Env.isExcludedTool(xmlFilePath)) {
            return;
        }
        // don't load if an up-to-date tool already exists
        if (locationTable.containsKey(toolFile.getAbsolutePath())) {
            ToolInfo tool = locationTable.get(xmlFilePath);

            if (tool.getLastModified() == toolFile.lastModified()) {
                return;
            } else {
                locationTable.remove(tool.getXMLFileName());
                toolTable.remove(tool.getQualifiedName());

                notifyToolRemoved(tool.getTool());
            }
        }

        try {
            XMLReader reader = new XMLReader(new BufferedReader(new FileReader(toolFile)));
            Tool tool = reader.readComponent();

            if (tool != null) {
                if (tool.getToolPackage().equals("")) {
                    tool.setToolPackage(getToolPackageName(xmlFilePath, tool.getToolName()));
                }

                tool.setDefinitionPath(xmlFilePath);
                tool.setToolBox(toolbox);

                ToolInfo toolinfo = new ToolInfo(tool, xmlFilePath);
                locationTable.put(toolinfo.getXMLFileName(), toolinfo);
                toolTable.put(toolinfo.getQualifiedName(), toolinfo);

                notifyToolAdded(tool);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage() + " in xmlFile: " + xmlFilePath);
            e.printStackTrace(System.out);
        }
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
            addTool(new File(location), toolbox);
            purgeTool(location);
        }
    }

    /**
     * Removes the tool at the specified location from the tool/location tables if it has been
     * deleted
     */
    protected void purgeTool(String location) {
        if (locationTable.containsKey(location)) {
            ToolInfo tool = locationTable.get(location);
            if ((!new File(location).exists()) || !toolboxes.contains(tool.getTool().getToolBox())) {
                locationTable.remove(location);
                toolTable.remove(tool.getQualifiedName());
                notifyToolRemoved(tool.getTool());
            }
        }

    }

    protected void addTools() {
        String[] toolboxArray = toolboxes.toArray(new String[toolboxes.size()]);

        for (int count = 0; count < toolboxArray.length; count++) {
            String baseToolboxPath = toolboxArray[count];

            // recursively find all files in toolboxes with .xml suffix

            File[] xmlfiles = FileUtils.listEndsWith(baseToolboxPath, XMLReader.XML_FILE_SUFFIX, excludedDirectories);

            for (int i = 0; i < xmlfiles.length; i++) {
                addTool(xmlfiles[i], baseToolboxPath);

            }
            JavaReader reader = new JavaReader();
            try {
                List<Tool> tools = reader.createTools(baseToolboxPath);
                for (Tool tool : tools) {
                    String def = tool.getDefinitionPath();
                    if (!Env.isExcludedTool(def)) {
                        ToolInfo toolinfo = new ToolInfo(tool, tool.getDefinitionPath());
                        locationTable.put(tool.getDefinitionPath(), toolinfo);
                        toolTable.put(toolinfo.getQualifiedName(), toolinfo);
                        notifyToolAdded(tool);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
                purgeTools();
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


        /**
         * Removes deleted tools from the tools/location tables
         */
        private void purgeTools() {
            String toolnames[] = getToolNames();
            String toolboxloc;

            for (int count = 0; count < toolnames.length; count++) {
                toolboxloc = getToolBoxLocation(toolnames[count]);

                if (toolboxloc != null) {
                    purgeTool(toolboxloc);
                    sleep();
                }
            }
        }

        private void sleep() {
            if (minpriority && (++sleepcount >= 20)) {
                try {
                    sleepcount = 0;
                    Thread.sleep(100);
                }
                catch (InterruptedException except) {
                }
            } else {
                Thread.yield();
            }
        }

    }
}
