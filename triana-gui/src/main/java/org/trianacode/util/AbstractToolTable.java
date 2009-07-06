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

import org.trianacode.taskgraph.tool.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 25, 2009: 5:15:07 PM
 * @date $Date:$ modified by $Author:$
 */

public abstract class AbstractToolTable implements ToolTable {


    static {
        ClassLoaders.addClassLoader(ToolClassLoader.getLoader());
    }

    /**
     * A hashtable of tool boxes keyed by their path
     */
    protected Map<String, Toolbox> toolboxes = new ConcurrentHashMap<String, Toolbox>();

    /**
     * An array list of tool listeners
     */
    protected Vector<ToolTableListener> listeners = new Vector<ToolTableListener>();

    protected ToolFormatHandler toolHandler = new FileToolFormatHandler();


    /**
     * Utility method to load all the tools found in the named tool box, ignoring the default toolboxes. It empties all
     * caches so should not be used inconjunction with other load tool mechanisms.
     *
     * @param toolbox the tool box to load from.
     */
    public void blockingLoadToolsFromToolBox(Toolbox toolbox) {
        blockingLoadToolsFromToolBox(toolbox, true);
    }

    protected void blockingLoadToolsFromToolBox(Toolbox toolbox, boolean clearTables) {
        if (clearTables) {
            toolHandler.clear();
        }
        toolboxes.put(toolbox.getPath(), toolbox);
        addTools();
    }

    /**
     * Utility method to load all the tools in the known tool boxes, blocking until finished. This method avoids using
     * any the threaded reload tools methods so should not be used to load tools from toolboxes that may change
     * over time.
     */
    public void blockingLoadTools() {
        for (Toolbox toolbox : toolboxes.values()) {
            blockingLoadToolsFromToolBox(toolbox, false);
        }
    }

    public ToolFormatHandler getToolFormatHandler() {
        return toolHandler;
    }

    /**
     * Add a listener to be notified when new tools are discovered
     */
    public void addToolTableListener(ToolTableListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a tool listener
     */
    public void removeToolTableListener(ToolTableListener listener) {
        listeners.remove(listener);
    }


    /**
     * A method to lookup the xml file location given the tool name.
     *
     * @param toolname The tool we are looking for
     * @return the file name or null if not found
     */
    public String getToolBoxLocation(String toolname) {
        Tool tool = toolHandler.getTool(toolname);
        if (tool != null) {
            return tool.getDefinitionPath();
        }
        return null;
    }

    /**
     * A method to lookup the tool with the given the tool name.
     *
     * @param toolName The name of the tool we are looking for
     * @return The Tool or null if not found
     */
    public Tool getTool(String toolName) {
        return toolHandler.getTool(toolName);
    }

    /**
     * @return an array of the tools with the specified name
     */
    public Tool[] getTools(String toolName) {
        Tool tool = toolHandler.getTool(toolName);
        if (tool != null) {
            return new Tool[]{tool};
        }
        return new Tool[0];
    }

    /**
     * @return an array of the tools with the specified name
     */
    public Tool[] getTools() {
        return toolHandler.getTools();
    }


    /**
     * @return true if a tool with the given name exists
     */
    public boolean isTool(String toolName) {
        return toolHandler.getTool(toolName) != null;
    }

    /**
     * returns an array of strings of qualified tool names.
     */
    public String[] getToolNames() {
        return toolHandler.getToolNames();
    }


    /**
     * Inserts a copy of the tool into the specified package. Combined with delete
     * tool this can be used to cut/copy tools.
     *
     * @param tool    the tool being pasted
     * @param pack    the package of the pasted tool
     * @param toolbox the toolbox the tool is pasted into (ignore if irrelevant)
     */
    public abstract void insertTool(Tool tool, String pack, String toolbox);

    /**
     * Deletes the specified tool.
     *
     * @param tool tool to be deleted
     */
    public abstract void deleteTool(Tool tool, boolean files);

    /**
     * Generate a new file location to store a pasted tool
     *
     * @param toolname tool to be created
     * @param pack     tool package name
     * @param toolbox  toolbox to create the tool in
     * @return a unused file to store the xml for the specified tool in
     */
    public abstract String getPasteFileLocation(String toolname, String pack, String toolbox);


    /**
     * Notifies the tool listeners when a tool is added
     *
     * @param tool the tool added to the ToolTable
     */
    protected void notifyToolAdded(Tool tool) {
        ToolTableListener[] lists = listeners.toArray(new ToolTableListener[listeners.size()]);

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
        ToolTableListener[] lists = listeners.toArray(new ToolTableListener[listeners.size()]);

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
        ToolTableListener[] lists = listeners.toArray(new ToolTableListener[listeners.size()]);

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
        ToolTableListener[] lists = listeners.toArray(new ToolTableListener[listeners.size()]);

        for (int count = 0; count < lists.length; count++) {
            lists[count].toolBoxRemoved(toolbox);
        }
    }

    /**
     * @return a list of the current tool box paths
     */
    public Toolbox[] getToolBoxes() {
        return toolboxes.values().toArray(new Toolbox[toolboxes.size()]);
    }

    public String[] getToolBoxPaths() {
        return toolboxes.keySet().toArray(new String[toolboxes.size()]);
    }

    /**
     * @return the tool box path of the specified type (null if no tool box specified for that
     *         type)
     */
    public Toolbox getToolBox(String type) {
        for (String s : toolboxes.keySet()) {
            Toolbox box = toolboxes.get(s);
            if (box.getType().equals(type)) {
                return box;
            }
        }
        return null;
    }

    /**
     * @return the type for the specified tool box path if no type
     *         specified for the tool box path)
     */
    public String getToolBoxType(String toolbox) {
        Toolbox tb = toolboxes.get(toolbox);
        if (tb != null) {
            return tb.getType();
        }
        return "";
    }

    /**
     * Returns the list of current toolbox types
     *
     * @return an array of the current tool box type values
     */
    public String[] getToolBoxTypes() {
        List<String> types = new ArrayList<String>();
        for (String s : toolboxes.keySet()) {
            Toolbox tb = toolboxes.get(s);
            types.add(tb.getType());
        }
        return types.toArray(new String[toolboxes.size()]);
    }

    /**
     * Add a tool box path to the current tool boxes
     */
    public void addToolBox(Toolbox... boxes) {
        for (Toolbox box : boxes) {
            if (toolboxes.get(box.getPath()) == null) {
                toolboxes.put(box.getPath(), box);
                notifyToolboxAdded(box);
            }
        }
    }

    /**
     * Removes a tool box from the current tool boxes and from the tool box types if it has a type
     * set.
     *
     * @param path the path of the tool box to remove
     */
    public void removeToolBox(String path) {
        Toolbox box = toolboxes.get(path);
        if (box != null && !box.getType().equals(DEFAULT_TOOLBOX)) {
            toolboxes.remove(path);
            notifyToolboxRemoved(box);
        }

    }


    /**
     * Notifies the tool table to update the tool loaded from the specified location, such as when a
     * tool is created. The location should be in a form understanded by the tool table (e.g. XML
     * file location, tool server network address), and is ignored if not understood.
     *
     * @param location the location of the file
     * @param toolbox  the toolbox the location is in (specify null if unknown)
     */
    public abstract void refreshLocation(String location, String toolbox);

    /**
     * Attempts to find a toolbox that contains the specified location
     *
     * @param location the location being searched for in existing toolboxes
     * @return the found toolbox location
     */
    public String findToolBox(String location) {
        String locpath = new File(location).getAbsolutePath();
        Iterator<String> it = toolboxes.keySet().iterator();
        while (it.hasNext()) {
            String path = it.next();
            Toolbox tb = toolboxes.get(path);
            if (!tb.isVirtual()) {
                if (locpath.startsWith(new File(path).getAbsolutePath())) {
                    return path;
                }
            }
        }
        return null;

    }


    /**
     * Add a tool to the tool tables that is located at the specified location
     *
     * @param toolFile the tool file
     * @param toolbox  toolbox to which the tool is to be added
     */
    protected abstract List<Tool> addTools(File toolFile, String toolbox) throws ToolException;

    /**
     * Removes the tool
     *
     * @param tool the Tool.
     */
    protected abstract void purgeTool(Tool tool);


    /**
     * Removes references to the tool
     *
     * @param tool the Tool.
     */
    protected abstract void purgeToolRef(Tool tool);

    /**
     * Add tools to the tool table using the addTool method for all tools in all tool boxes.
     */
    protected abstract void addTools();
}
