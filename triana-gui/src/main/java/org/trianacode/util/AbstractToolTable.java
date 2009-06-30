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

import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.ToolTableListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
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


    private static ArrayList<String> standardToolBoxes = new ArrayList<String>(5);

    /**
     * Set up the directories that are to be excluded when searching for tool files
     */
    static {
        standardToolBoxes.add(DEFAULT_TOOLBOX);
        standardToolBoxes.add(DATA_TOOLBOX);
        standardToolBoxes.add(USER_TOOLBOX);
        standardToolBoxes.add(REMOTE_TOOLBOX);
        standardToolBoxes.add(NO_TYPE_SET);
    }

    /**
     * An array list of all the current toolbox paths
     */
    protected Vector<String> toolboxes = new Vector<String>();
    /**
     * A hashtable of tool boxes keyed by their type
     */
    protected Map<String, String> types = new ConcurrentHashMap<String, String>();
    /**
     * A hashtable of tools, indexed by their qualified tool name (package_name.toolname)
     */
    protected Map<String, ToolInfo> toolTable = new ConcurrentHashMap<String, ToolInfo>(100);
    /**
     * An array list of tool listeners
     */
    protected Vector<ToolTableListener> listeners = new Vector<ToolTableListener>();

    /**
     * Utility method to load all the tools found in the named tool box, ignoring the default toolboxes. It empties all
     * caches so should not be used inconjunction with other load tool mechanisms.
     *
     * @param toolbox the tool box to load from.
     */
    public void blockingLoadToolsFromToolBox(String toolbox) {
        blockingLoadToolsFromToolBox(toolbox, true);
    }

    protected void blockingLoadToolsFromToolBox(String toolbox, boolean clearTables) {
        if (clearTables) {
            toolboxes.clear();
            toolTable.clear();
        }
        toolboxes.add(toolbox);
        addTools();
    }

    /**
     * Utility method to load all the tools in the known tool boxes, blocking until finished. This method avoids using
     * any the threaded reload tools methods so should not be used to load tools from toolboxes that may change
     * over time.
     */
    public void blockingLoadTools() {
        for (String toolbox : toolboxes) {
            blockingLoadToolsFromToolBox(toolbox, false);
        }
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
        if (toolTable.containsKey(toolname)) {
            return (toolTable.get(toolname)).getXMLFileName();
        } else {
            return null;
        }
    }

    /**
     * A method to lookup the tool with the given the tool name.
     *
     * @param toolName The name of the tool we are looking for
     * @return The Tool or null if not found
     */
    public Tool getTool(String toolName) {
        if (toolTable.containsKey(toolName)) {
            return (toolTable.get(toolName)).getTool();
        } else {
            return null;
        }
    }

    /**
     * @return an array of the tools with the specified name
     */
    public Tool[] getTools(String toolName) {
        if (toolTable.containsKey(toolName)) {
            return new Tool[]{(toolTable.get(toolName)).getTool()};
        } else {
            return new Tool[0];
        }
    }


    /**
     * @return true if a tool with the given name exists
     */
    public boolean isTool(String toolName) {
        return toolTable.containsKey(toolName);
    }

    /**
     * returns an array of strings of qualified tool names.
     */
    public String[] getToolNames() {
        return (String[]) toolTable.keySet().toArray(new String[toolTable.size()]);
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
    public abstract void deleteTool(Tool tool);

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
        ToolTableListener[] lists = (ToolTableListener[]) listeners.toArray(new ToolTableListener[listeners.size()]);

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
        ToolTableListener[] lists = (ToolTableListener[]) listeners.toArray(new ToolTableListener[listeners.size()]);

        for (int count = 0; count < lists.length; count++) {
            lists[count].toolRemoved(tool);
        }
    }

    /**
     * Notifies a listener when tool box is added
     *
     * @param toolbox the toolbox added to the ToolTable
     */
    protected void notifyToolboxAdded(String toolbox) {
        ToolTableListener[] lists = (ToolTableListener[]) listeners.toArray(new ToolTableListener[listeners.size()]);

        for (int count = 0; count < lists.length; count++) {
            lists[count].toolBoxAdded(toolbox);
        }
    }

    /**
     * Notifies a listener when tool box is removed
     *
     * @param toolbox the toolbox removed from the ToolTable
     */
    protected void notifyToolboxRemoved(String toolbox) {
        ToolTableListener[] lists = (ToolTableListener[]) listeners.toArray(new ToolTableListener[listeners.size()]);

        for (int count = 0; count < lists.length; count++) {
            lists[count].toolBoxRemoved(toolbox);
        }
    }

    /**
     * @return a list of the current tool box paths
     */
    public String[] getToolBoxes() {
        return (String[]) toolboxes.toArray(new String[toolboxes.size()]);
    }

    /**
     * @return the tool box path of the specified type (null if no tool box specified for that
     *         type)
     */
    public String getToolBox(String type) {
        if (types.containsKey(type)) {
            return types.get(type);
        } else {
            return null;
        }
    }

    /**
     * @return the type for the specified tool box path ({@link #NO_TYPE_SET NO_TYPE_SET} if no type
     *         specified for the tool box path)
     */
    public String getToolBoxType(String toolbox) {
        for (String type : types.keySet()) {
            if (types.containsKey(type) && (types.get(type).equals(toolbox))) {
                return type;
            }
        }
        return NO_TYPE_SET;
    }

    /**
     * Associates a toolbox type with a toolbox. Only one toolbox can be associated with a type but
     * many types van be associated witha single toolbox.
     * <p/>
     * If the tool box to be set is not in the list of current tool boxes then it is added first.
     *
     * @param toolbox the tool box we want associated with a type
     * @param type    the type we want to associate with the toolbox
     * @return the previous toolbox of the specified type or null if it did not have one.
     */
    public String setToolBoxType(String toolbox, String type) {
        addToolBox(toolbox);
        return types.put(type, toolbox);
    }

    /**
     * Returns the list of current toolbox types
     *
     * @return an array of the current tool box type values
     */
    public String[] getToolBoxTypes() {
        Set<String> typeKeys = types.keySet();
        return (String[]) typeKeys.toArray(new String[types.keySet().size()]);
    }

    /**
     * Add a tool box path to the current tool boxes
     */
    public void addToolBox(String path) {
        if (!toolboxes.contains(path)) {
            toolboxes.add(path);
            notifyToolboxAdded(path);
        }
    }

    /**
     * Removes a tool box from the current tool boxes and from the tool box types if it has a type
     * set.
     *
     * @param path the path of the tool box to remove
     */
    public void removeToolBox(String path) {
        if (toolboxes.contains(path)) {
            String toolBoxType = getToolBoxType(path);
            if (standardToolBoxes.contains(toolBoxType)) {
                types.put(toolBoxType, "");
            } else {
                types.remove(toolBoxType);
            }
            toolboxes.remove(path);
            notifyToolboxRemoved(path);
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
        String[] toolboxes = getToolBoxes();
        String toolbox = null;

        for (int count = 0; (count < toolboxes.length) && (toolbox == null); count++) {
            if (locpath.startsWith(new File(toolboxes[count]).getAbsolutePath())) {
                toolbox = toolboxes[count];
            }
        }

        return toolbox;
    }


    /**
     * Add a tool to the tool tables that is located at the specified location
     *
     * @param toolFile the tool file
     * @param toolbox  toolbox to which the tool is to be added
     */
    protected abstract void addTool(File toolFile, String toolbox);

    /**
     * Removes the tool at the specified location from the tool/location tables if it has been
     * deleted
     *
     * @param location the location of the Tool.
     */
    protected abstract void purgeTool(String location);

    /**
     * Add tools to the tool table using the addTool method for all tools in all tool boxes.
     */
    protected abstract void addTools();
}
