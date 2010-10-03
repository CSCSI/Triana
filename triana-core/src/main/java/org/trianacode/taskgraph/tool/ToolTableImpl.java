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

package org.trianacode.taskgraph.tool;

import org.apache.commons.logging.Log;
import org.trianacode.config.TrianaProperties;
import org.trianacode.discovery.protocols.tdp.imp.trianatools.ToolResolver;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.util.UrlUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class ToolTableImpl implements ToolTable, ToolListener {

    private static Log log = Loggers.TOOL_LOGGER;

    // The standard tool box types
    public static final String USER_TOOLBOX = "user";
    TrianaProperties properties;


    /**
     * An array list of tool listeners
     */
    protected Vector<ToolListener> listeners = new Vector<ToolListener>();

    private ToolResolver resolver;

    public ToolTableImpl(ToolResolver resolver) {
        this.resolver = resolver;
        properties = resolver.getProperties();
        resolver.addToolListener(this);
    }

    public TrianaProperties getProperties() {
        return properties;
    }

    /**
     * Add a listener to be notified when new tools are discovered
     */
    public void addToolTableListener(ToolListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a tool listener
     */
    public void removeToolTableListener(ToolListener listener) {
        listeners.remove(listener);
    }


    public ToolResolver getToolResolver() {
        return resolver;
    }

    /**
     * A method to lookup the tool with the given the tool name.
     *
     * @param toolName The name of the tool we are looking for
     * @return The Tool or null if not found
     */
    public Tool getTool(String toolName) {
        return resolver.getTool(toolName);
    }

    /**
     * @return an array of the tools that share the same file
     */
    public Tool[] getTools(URL definitionPath) {
        List<Tool> ret = resolver.getTools(definitionPath);
        return ret.toArray(new Tool[ret.size()]);

    }

    /**
     * @return an array of the tools with the specified name
     */
    public Tool[] getTools() {
        List<Tool> ret = resolver.getTools();
        return ret.toArray(new Tool[ret.size()]);

    }


    /**
     * @return true if a tool with the given name exists
     */
    public boolean isTool(String toolName) {
        return resolver.getTool(toolName) != null;
    }

    /**
     * returns an array of strings of qualified tool names.
     */
    public String[] getToolNames() {
        List<String> ret = resolver.getToolNames();
        return ret.toArray(new String[ret.size()]);
    }

    public void refreshLocation(URL location, String toolbox) {
        if (toolbox == null) {
            toolbox = findToolBox(location);
        }
        if (toolbox != null) {
            resolver.refreshTools(location, toolbox);
        }
    }


    /**
     * Inserts a copy of the tool into the specified package. Combined with delete tool this can be used to cut/copy
     * tools.
     *
     * @param tool    the tool being pasted
     * @param pack    the package of the pasted tool
     * @param toolbox the toolbox the tool is pasted into (ignore if irrelevant)
     */
    public void insertTool(Tool tool, String pack, String toolbox) {
        log.debug("ToolTableImp.insertTool full name:" + tool.getQualifiedToolName());
        if (resolver.getTool(tool.getQualifiedToolName()) != null) {
            log.debug("Not pasting. Tool already exists with name " + tool.getQualifiedToolName());
            return;
        }

        String location = getPasteFileLocation(tool.getToolName(), pack, toolbox);
        tool.setDefinitionPath(UrlUtils.toURL(location));
        tool.setDefinitionType(Tool.DEFINITION_TRIANA_XML);

        try {
            XMLWriter writer = new XMLWriter(new BufferedWriter(new FileWriter(location)));
            writer.writeComponent(tool);
            writer.close();
        }
        catch (IOException except) {
            throw (new RuntimeException("Error writing xml for " + tool.getToolName() + ": " + except.getMessage()));
        }
        refreshLocation(UrlUtils.toURL(location), toolbox);
    }

    /**
     * Deletes the specified tool.
     *
     * @param tool tool to be deleted
     */
    public void deleteTool(Tool tool, boolean files) {
        URL path = tool.getDefinitionPath();

        if ((path != null) && (!path.equals(""))) {

            File file = UrlUtils.getFile(path);
            if (files) {
                if (file.exists()) {
                    file.delete();
                }
                purgeTool(tool);
            } else {
                //Env.addExcludedTool(path);
                purgeToolRef(tool);
            }

        }
    }

    public boolean isModifiable(Tool tool) {
        return resolver.isModifiable(tool);
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
     * Notifies the tool listeners when a tool is added
     *
     * @param tool the tool added to the ToolTable
     */
    protected void notifyToolsAdded(List<Tool> tool) {
        ToolListener[] lists = listeners.toArray(new ToolListener[listeners.size()]);

        for (int count = 0; count < lists.length; count++) {
            lists[count].toolsAdded(tool);
        }
    }

    /**
     * Notifies the tool listeners when a tool is removed
     *
     * @param tool the Tool removed from the ToolTable
     */
    protected void notifyToolsRemoved(List<Tool> tool) {
        ToolListener[] lists = listeners.toArray(new ToolListener[listeners.size()]);

        for (int count = 0; count < lists.length; count++) {
            lists[count].toolsRemoved(tool);
        }
    }


    /**
     * Notifies the tool listeners when a tool is added
     *
     * @param tool the tool added to the ToolTable
     */
    protected void notifyToolAdded(Tool tool) {
        ToolListener[] lists = listeners.toArray(new ToolListener[listeners.size()]);

        for (int count = 0; count < lists.length; count++) {
            lists[count].toolAdded(tool);
        }
    }

    /**
     * Notifies the tool listeners when a tool is removed
     *
     * @param tool the Tool removed from the ToolTable
     */
    protected void notifyToolRemoved(Tool tool) {
        ToolListener[] lists = listeners.toArray(new ToolListener[listeners.size()]);

        for (int count = 0; count < lists.length; count++) {
            lists[count].toolRemoved(tool);
        }
    }

    /**
     * Notifies a listener when tool box is added
     *
     * @param toolbox the toolbox added to the ToolTable
     */
    protected void notifyToolboxAdded(Toolbox toolbox) {
        ToolListener[] lists = listeners.toArray(new ToolListener[listeners.size()]);

        for (int count = 0; count < lists.length; count++) {
            lists[count].toolBoxAdded(toolbox);
        }
    }

    /**
     * Notifies a listener when tool box is removed
     *
     * @param toolbox the toolbox removed from the ToolTable
     */
    protected void notifyToolboxRemoved(Toolbox toolbox) {
        ToolListener[] lists = listeners.toArray(new ToolListener[listeners.size()]);

        for (int count = 0; count < lists.length; count++) {
            lists[count].toolBoxRemoved(toolbox);
        }
    }

    /**
     * @return a list of the current tool box paths
     */
    public Toolbox[] getToolBoxes() {
        List<Toolbox> ret = resolver.getToolboxes();
        return ret.toArray(new Toolbox[ret.size()]);
    }

    public String[] getToolBoxPaths() {
        List<String> ret = resolver.getToolboxPaths();
        return ret.toArray(new String[ret.size()]);
    }

    /**
     * @return the tool box path of the specified type (null if no tool box specified for that type)
     */
    public Toolbox getToolBox(String type) {

        List<Toolbox> l = resolver.getToolboxes();
        for (Toolbox toolbox : l) {
            if (toolbox.getType().equals(type)) {
                return toolbox;
            }
        }
        return null;
    }

    /**
     * @return the type for the specified tool box path if no type specified for the tool box path)
     */
    public String getToolBoxType(String toolbox) {
        Toolbox tb = resolver.getToolbox(toolbox);
        if (tb != null) {
            return tb.getType();
        }
        return "";
    }

    /**
     * Add a tool box path to the current tool boxes
     */
    public void addToolBox(Toolbox... boxes) {
        for (Toolbox box : boxes) {
            resolver.addToolbox(box);
        }
    }

    /**
     * Removes a tool box from the current tool boxes and from the tool box types if it has a type set.
     *
     * @param path the path of the tool box to remove
     */
    public boolean removeToolBox(String path) {
        Toolbox box = resolver.getToolbox(path);
        if (box != null && !box.getType().equals(USER_TOOLBOX)) {
            resolver.removeToolbox(box);
            return true;
        }
        return false;
    }


    /**
     * Attempts to find a toolbox that contains the specified location
     *
     * @param location the location being searched for in existing toolboxes
     * @return the found toolbox location
     */
    public String findToolBox(URL location) {
        if (UrlUtils.getExistingFile(location) != null) {
            try {
                String locpath = new File(location.toURI()).getAbsolutePath();
                List<String> paths = resolver.getToolboxPaths();
                for (String path : paths) {
                    File f = UrlUtils.getExistingFile(UrlUtils.toURL(path));
                    if (f != null) {
                        if (locpath.startsWith(f.getAbsolutePath())) {
                            return path;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    /**
     * Removes the tool
     *
     * @param tool the Tool.
     */
    protected void purgeTool(Tool tool) {
        resolver.deleteTool(tool);
    }


    /**
     * Removes references to the tool
     *
     * @param tool the Tool.
     */
    protected void purgeToolRef(Tool tool) {
        resolver.removeTool(tool);
    }

    public void toolsAdded(List<Tool> tools) {
        notifyToolsAdded(tools);
    }

    public void toolsRemoved(List<Tool> tools) {
        notifyToolsRemoved(tools);
    }

    public void toolAdded(Tool tool) {
        notifyToolAdded(tool);
    }

    public void toolRemoved(Tool tool) {
        notifyToolRemoved(tool);
    }

    public void toolBoxAdded(Toolbox toolbox) {
        notifyToolboxAdded(toolbox);
    }

    public void toolBoxRemoved(Toolbox toolbox) {
        notifyToolboxRemoved(toolbox);
    }
}
