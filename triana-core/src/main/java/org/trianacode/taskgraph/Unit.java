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

package org.trianacode.taskgraph;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.trianacode.taskgraph.clipin.ClipInStore;
import org.trianacode.taskgraph.service.ControlInterface;
import org.trianacode.taskgraph.service.RunnableInterface;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.util.FileUtils;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public abstract class Unit {

    static Logger log = Logger.getLogger("org.trianacode.taskgraph.Unit");


    /**
     * Parameter types
     */

    // Internal parameter not exposed to user
    public static final String INTERNAL = Tool.INTERNAL;
    // Internal parameter not exposed to user or serialized in tool XML
    public static final String TRANSIENT = Tool.TRANSIENT;
    // Internal parameter only initialized on first getParameter call
    public static final String LATE_INITIALIZE = Tool.LATE_INITIALIZE;

    // User accessible parameter
    public static final String USER_ACCESSIBLE = Tool.USER_ACCESSIBLE;
    // User accessible parameter not serialized in tool XML
    public static final String TRANSIENT_ACCESSIBLE = Tool.TRANSIENT_ACCESSIBLE;


    /**
     * Parameter update polices
     */
    public static final String IMMEDIATE_UPDATE = Tool.IMMEDIATE_UPDATE;
    public static final String PROCESS_UPDATE = Tool.PROCESS_UPDATE;
    public static final String NO_UPDATE = Tool.NO_UPDATE;

    /**
     * Parameter panel instatiation options
     */
    public static final String ON_USER_ACCESS = Tool.ON_USER_ACCESS;
    public static final String ON_TASK_INSTANTATION = Tool.ON_TASK_INSTANTIATION;

    /**
     * Output polices
     */
    public static final String COPY_OUTPUT = Tool.COPY_OUTPUT;
    public static final String CLONE_MULTIPLE_OUTPUT = Tool.CLONE_MULTIPLE_OUTPUT;
    public static final String CLONE_ALL_OUTPUT = Tool.CLONE_ALL_OUTPUT;


    /**
     * Node requirements
     */
    public static final String ESSENTIAL = Task.ESSENTIAL;
    public static final String ESSENTIAL_IF_CONNECTED = Task.ESSENTIAL_IF_CONNECTED;
    public static final String OPTIONAL = Task.OPTIONAL;


    /**
     * Use to access functions within RunnableTask
     */
    private RunnableInterface runnableTask;


    /**
     * used to send display/hide messages to the unit's parameter panel
     */
    private int displayCounter;

    private String toolName = "unknown";

    private String toolPackage = "unknown";

    private Map<String, Object[]> definedParams = new HashMap<String, Object[]>();


    public Unit() {
        setToolName(getClass().getSimpleName());
        setToolPackage(getPackageName(getClass().getName()));
    }


    private String getPackageName(String fullname) {
        if (fullname.endsWith(".class")) {
            fullname = fullname.substring(0, fullname.length() - 6);
        }
        int index = fullname.indexOf(".");
        if (index > 0) {
            return fullname.substring(0, fullname.lastIndexOf("."));
        }
        return fullname;
    }


    /**
     * Set the access to a RunnableTask
     */
    public void setRunnableInterface(RunnableInterface runnableTask) {
        this.runnableTask = runnableTask;
    }

    /**
     * Use to access functions within RunnableTask
     */
    protected RunnableInterface getRunnableInterface() {
        return runnableTask;
    }


    /**
     * Use to access functions within the Task Interface
     */
    public Task getTask() {
        return runnableTask.getTask();
    }

    /**
     * Use to access functions within the Control Interface.
     */
    public ControlInterface getControlInterface() {
        return runnableTask.getControlInterface();
    }


    /**
     * @return the name of this tool.
     */
    public String getToolName() {
        return toolName;
    }

    public String getToolPackage() {
        return toolPackage;
    }

    public void setToolPackage(String toolPackage) {
        this.toolPackage = toolPackage;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    /**
     * This method should return an array of the data types accepted by specific nodes, with the node index providing
     * the index for the array. For nodes not covered by the array, the types specified by getInputTypes() are assumed.
     *
     * @return an array of the input types for this unit
     */
    public String[][] getNodeInputTypes() {
        return new String[0][0];
    }

    /**
     * This method should return an array of the data types accepted by nodes not specified in getNodeInputTypes (e.g.
     * triana.types.VectorType). If no package is specified then triana.types is assumed.
     *
     * @return an array of the input types for this unit
     */
    public abstract String[] getInputTypes();


    /**
     * This method should return an array of the data types output by specific nodes, with the node index providing the
     * index for the array. For nodes not covered by the array, the types specified by getOutputTypes() are assumed.
     *
     * @return an array of the input types for this unit
     */
    public String[][] getNodeOutputTypes() {
        return new String[0][0];
    }

    /**
     * This method should return an array of the data types output by nodes not specified in getNodeOutputTypes() (e.g.
     * triana.types.VectorType). If no package is specified then triana.types is assumed.
     *
     * @return an array of the output types for this unit
     */
    public abstract String[] getOutputTypes();


    /**
     * This function is called when the unit is first created. It should be over-ridden to initialise the tool
     * properties (e.g. default number of nodes) and tool parameters.
     */
    public void init() {
    }

    /**
     * This function is called when the reset is pressed on the gui. It restore the unit to its pre-start state.
     */
    public void reset() {
    }

    /**
     * This function is called when the unit is deleted. It should be over-ridden to clean up the unit (e.g. close open
     * files).
     */
    public void dispose() {

    }

    public void parameterUpdate(String paramname, Object value) {

    }

    public final void parameterUpdated(String paramname, Object value) {
        Object[] existing = definedParams.get(paramname);
        log.fine("param:" + paramname + "\n" +
                "new value:" + value + "\n" +
                "existing type for param: " + getTask().getParameterType(paramname));

        if (existing != null && existing.length == 2 && existing[0] != null && !existing[0].equals(value)) {
            log.fine("setting new type for param " + paramname + " : " + (String) existing[1]);
            getTask().setParameterType(paramname, (String) existing[1]);
        }
        parameterUpdate(paramname, value);
    }


    /**
     * The main unit algorithm. This method should be implemented with the appropriate algorihtm for the unit (if no
     * algorithm is implemented deadlock occurs).
     */
    public abstract void process() throws Exception;

    /**
     * This is called when the network is forcably stopped by the user. This should be over-ridden with the desired
     * tasks.
     */
    public void stopping() {
    }


    /**
     * Stops the network running
     */
    public final void notifyError(String message) {
        getRunnableInterface().notifyError(message);
    }


    /**
     * @return the value of a task parameter. (null if not set)
     */
    public Object getParameter(String paramname) {
        return getTask().getParameter(paramname);
    }

    /**
     * @return the type of a task parameter
     */
    public Object getParameterType(String paramname) {
        return getTask().getParameterType(paramname);
    }


    /**
     * Defines the initial value and type of a parameter. If the parameter is already defined, such as when the unit is
     * recreated from a serialized version, then nothing happens. This method should generally only be used in the init
     * method. Note that a parameterUpdate call is not generated.
     * <p/>
     * The type of the parameter can either be:
     * <p/>
     * USER_ACCESSIBLE - The user is allowed to dynamically update the value INTERNAL - The parameter is hidden from the
     * user TRANSIENT - The parameter is hidden from the user and does not get saved when the taskgraph is serialized
     *
     * @param paramname the name of the parameter
     * @param initvalue the value of the parameter (null if not defined)
     * @param type      see above
     */
    public void defineParameter(String paramname, Object initvalue, String type) {

        Task task = getTask();

        if (!task.isParameterName(paramname)) {
            if (initvalue != null) {
                String reset = addDefinedParam(paramname, initvalue, type);
                getTask().setParameter(paramname, initvalue);
                getTask().setParameterType(paramname, reset);
            }
        }
    }

    private String addDefinedParam(String name, Object value, String type) {
        String reset = type;
        if (type.equals(INTERNAL)) {
            reset = TRANSIENT;
        }
        if (type.equals(USER_ACCESSIBLE)) {
            reset = TRANSIENT_ACCESSIBLE;
        }
        definedParams.put(name, new Object[]{value, type});
        return reset;
    }

    /**
     * @return true if a parameter with the specified name is set
     */
    public boolean isParameter(String paramname) {
        return getTask().isParameterName(paramname);
    }

    /**
     * Sets the value of a task parameter
     */
    public void setParameter(String paramname, Object value) {
        getTask().setParameter(paramname, value);
    }

    /**
     * Removes a task parameter
     */
    public void removeParameter(String paramname) {
        getTask().removeParameter(paramname);
    }


    /**
     * @return the number of data input nodes.
     */
    public int getInputNodeCount() {
        return getTask().getDataInputNodeCount();
    }

    /**
     * @return the number of data output nodes.
     */
    public int getOutputNodeCount() {
        return getTask().getDataOutputNodeCount();
    }


    /**
     * @return true if there is data waiting on the specified node
     */
    public boolean isInputAtNode(int nodeNumber) {
        return getRunnableInterface().isInput(nodeNumber);
    }

    /**
     * Returns the data at input node <i>nodeNumber</i>. If data is not ready, NOT_READY triana type is returned. If
     * there is no cable connected to the input node the NOT_CONNECTED triana type is returned.
     *
     * @param nodeNumber the particular node you want to get the data from.
     */
    public Object getInputAtNode(int nodeNumber) {
        return getRunnableInterface().getInput(nodeNumber);
    }

    /**
     * Outputs the data across all nodes. This passses the given data set to the first output node and then makes copies
     * for any other output nodes. This method blocks until the data is successfully sent.
     *
     * @param data the data to be sent
     */
    public void output(Object data) {
        getRunnableInterface().output(data);
    }

    /**
     * Outputs the data to the given node <i>outputNode</i>.. This method is used to set the data at each particular
     * output node if this is necessary, otherwise use output to copy the data across all nodes. This method blocks
     * until the data is successfully sent.
     *
     * @param outputNode the output node you wish to set
     * @param data       the data to be sent
     */
    public void outputAtNode(int outputNode, Object data) {
        getRunnableInterface().output(outputNode, data, true);
    }

    /**
     * Outputs the data to the given node <i>outputNode</i>. If specified this method blocks until the data is
     * successfully sent (usual behaviour), otherwise, if non-blocking, isOutputSent() can be used to poll whether the
     * data has been successfully sent.
     *
     * @param outputNode the output node you wish to set
     * @param data       the data to be sent
     * @param blocking   true if this method should block until the data is sent
     */
    public void outputAtNode(int outputNode, Object data, boolean blocking) {
        getRunnableInterface().output(outputNode, data, blocking);
    }

    /**
     * @return true if the data sent with an output call has reached its destination
     * @see public void outputAtNode(int outputNode, Object data, boolean blocking)
     */
    public boolean isOutputSent(int outputNode) {
        return getRunnableInterface().isOutputSent(outputNode);
    }


    /**
     * @return the clip-in with the specified name that came attached to the the specified data item (null if not
     *         present). Only clip-ins for data that has been input in the current process can be retrieved.
     */
    public Object getClipIn(Object data, String name) {
        return getRunnableInterface().getClipIn(data, name);
    }

    /**
     * @return the clip-in attached to this task with the specified name (null if not present)
     */
    public Object getClipIn(String name) {
        return getRunnableInterface().getClipIn(name);
    }

    /**
     * Put the specified clip-in into this task's clip-in bucket
     */
    public void putClipIn(String name, Object clipin) {
        getRunnableInterface().putClipIn(name, clipin);
    }

    /**
     * Remove the clip-in with the specified name from this task's clip-in bucket.
     *
     * @return the removed clip-in (or null if unknown)
     */
    public Object removeClipIn(String name) {
        return getRunnableInterface().removeClipIn(name);
    }

    /**
     * @return true if a clip-in with the specified name exists in this task's clip-in bucket
     */
    public boolean isClipInName(String name) {
        return getRunnableInterface().isClipInName(name);
    }


    /**
     * @return a store of the state of the current clip-in bucket
     */
    public ClipInStore extractClipInState() {
        return getRunnableInterface().extractClipInState();
    }

    /**
     * Restores a previously stored clip-in bucket state
     */
    public void restoreClipInState(ClipInStore store) {
        getRunnableInterface().restoreClipInState(store);
    }


    /**
     * @return the tool table
     */
    public ToolTable getToolTable() {
        return getRunnableInterface().getToolTable();
    }


    /**
     * Convienience method for retrieving maximum number of input nodes for this unit.
     */
    public int getMaximumInputNodes() {
        return getTask().getMaxDataInputNodes();
    }

    /**
     * Convienience method for setting the maximum number of input nodes for this unit. This method should only be
     * called from within the init() method.
     */
    public void setMaximumInputNodes(int inodes) {
        defineParameter(Tool.MAX_INPUT_NODES, String.valueOf(inodes), Tool.INTERNAL);
    }


    /**
     * Convienience method for retrieving minimum number of input nodes for this unit.
     */
    public int getMinimumInputNodes() {
        return getTask().getMinDataInputNodes();
    }

    /**
     * Convienience method for setting the minimum number of input nodes for this unit. This method should only be
     * called from within the init() method.
     */
    public void setMinimumInputNodes(int inodes) {
        defineParameter(Tool.MIN_INPUT_NODES, String.valueOf(inodes), Tool.INTERNAL);
    }


    /**
     * Convienience method for retrieving default number of output nodes for this unit.
     */
    public int getDefaultInputNodes() {
        return getTask().getDefaultDataInputNodes();
    }

    /**
     * Convienience method for setting the default number of input nodes for this unit. This method should only be
     * called from within the init() method.
     */
    public void setDefaultInputNodes(int inodes) {
        defineParameter(Tool.DEFAULT_INPUT_NODES, String.valueOf(inodes), Tool.INTERNAL);
    }


    /**
     * Convienience method for retrieving maximum number of output nodes for this unit.
     */
    public int getMaximumOutputNodes() {
        return getTask().getMaxDataOutputNodes();
    }

    /**
     * Convienience method for setting the maximum number of output nodes for this unit. This method should only be
     * called from within the init() method.
     */
    public void setMaximumOutputNodes(int onodes) {
        defineParameter(Tool.MAX_OUTPUT_NODES, String.valueOf(onodes), Tool.INTERNAL);
    }


    /**
     * Convienience method for retrieving minimum number of output nodes for this unit.
     */
    public int getMinimumOutputNodes() {
        return getTask().getMinDataOutputNodes();
    }

    /**
     * Convienience method for setting the minimum number of output nodes for this unit. This method should only be
     * called from within the init() method.
     */
    public void setMinimumOutputNodes(int onodes) {
        defineParameter(Tool.MIN_OUTPUT_NODES, String.valueOf(onodes), Tool.INTERNAL);
    }


    /**
     * Convienience method for retrieving default number of output nodes for this unit.
     */
    public int getDefaultOutputNodes() {
        return getTask().getDefaultDataOutputNodes();
    }

    /**
     * Convienience method for setting the default number of output nodes for this unit. This method should only be
     * called from within the init() method.
     */
    public void setDefaultOutputNodes(int onodes) {
        defineParameter(Tool.DEFAULT_OUTPUT_NODES, String.valueOf(onodes), Tool.INTERNAL);
    }


    /**
     * Convienience method for retrieving the default pop-up description for this unit.
     */
    public String getPopUpDescription() {
        return getTask().getPopUpDescription();
    }

    /**
     * Convienience method for setting the default pop-up description.
     */
    public void setPopUpDescription(String location) {
        getTask().removeParameter(Task.POP_UP_DESCRIPTION);
        defineParameter(Task.POP_UP_DESCRIPTION, location, Tool.INTERNAL);
    }


    /**
     * Convienience method for retrieving default help file location for this unit.
     */
    public String getHelpFileLocation() {
        return getTask().getHelpFile();
    }

    /**
     * Convienience method for setting the default help file location. If only a filename is specified then the toolbox
     * location + /help is assumed. All tools should have help files, which should be written in HTML.
     */
    public void setHelpFileLocation(String location) {
        defineParameter(Task.HELP_FILE_PARAM, location, Tool.INTERNAL);
    }


    /**
     * Covienience method that returns the parameter update policy for this unit.
     */
    public String getParameterUpdatePolicy() {
        return (String) getTask().getParameter(Tool.PARAM_UPDATE_POLICY);
    }

    /**
     * Convienience method that sets the parameter update policy for this unit. This method should only be called from
     * within the init() method.
     */
    public void setParameterUpdatePolicy(String policy) {
        defineParameter(Tool.PARAM_UPDATE_POLICY, policy, Tool.INTERNAL);
    }


    /**
     * Covienience method that returns the output policy for this unit.
     */
    public String getOutputPolicy() {
        return (String) getTask().getParameter(Tool.OUTPUT_POLICY);
    }

    /**
     * Convienience method that sets the output policy for this unit. This method should only be called from within the
     * init() method.
     */
    public void setOutputPolicy(String policy) {
        defineParameter(Tool.OUTPUT_POLICY, policy, Tool.INTERNAL);
    }


    /**
     * Convienience method that sets the information used to create a GUI with GUI Builder V2. This method should only
     * be called from within the init() method.
     */
    public void setGUIBuilderV2Info(String info) {
        getTask().removeParameter(Tool.OLD_GUI_BUILDER);

        defineParameter(Tool.GUI_BUILDER, info, Tool.INTERNAL);
        getTask().removeParameter(Tool.PARAM_PANEL_CLASS);
    }

    /**
     * Convienience method that returns the information used to create a GUI with GUI Builder V2
     */
    public String getGUIBuilderV2Info() {
        return (String) getTask().getParameter(Tool.GUI_BUILDER);
    }

    /**
     * Convienience method that sets the custom parameter panel used. This method should only be called from within the
     * init() method.
     */
    public void setParameterPanelClass(String classname) {
        defineParameter(Tool.PARAM_PANEL_CLASS, classname, Tool.INTERNAL);
        getTask().removeParameter(Tool.GUI_BUILDER);
        getTask().removeParameter(Tool.OLD_GUI_BUILDER);
    }

    /**
     * Covienience method that returns the custom parameter panel class used
     */
    public String getParameterPanelClass() {
        return (String) getTask().getParameter(Tool.PARAM_PANEL_CLASS);
    }

    /**
     * Convienience method that sets the when the parameter panel is instantiated, either ON_USER_ACCESS or
     * ON_TASK_INSTANTIATION. This method should only be called from within the init() method.
     */
    public void setParameterPanelInstantiate(String policy) {
        defineParameter(Tool.PARAM_PANEL_INSTANTIATE, policy, Tool.INTERNAL);
    }

    /**
     * Covienience method that returns the custom parameter panel class used
     */
    public String getParameterPanelInstantiate() {
        return (String) getTask().getParameter(Tool.PARAM_PANEL_INSTANTIATE);
    }


    /**
     * Sets the default node requirements for this unit (ESSENTIAL, ESSENTIAL_IF_CONNECTED or OPTIONAL)
     */
    public void setDefaultNodeRequirement(String requirement) {
        getTask().setDefaultNodeRequirement(requirement);
    }

    /**
     * @return the default node requirements for this unit (ESSENTIAL, ESSENTIAL_IF_CONNECTED or OPTIONAL)
     */
    public String getDefaultNodeRequirement() {
        return getTask().getDefaultNodeRequirement();
    }

    /**
     * Display the parameter panel for this unit. Equivalent to the user double clicking.
     */
    public void showParameterPanel() {
        String value = String.valueOf(++displayCounter);
        if (!isParameter(Tool.PARAM_PANEL_SHOW)) {
            defineParameter(Tool.PARAM_PANEL_SHOW, value, Tool.GUI);
        } else {
            getTask().setParameter(Tool.PARAM_PANEL_SHOW, value);
        }
    }

    public void hideParameterPanel() {
        String value = String.valueOf(++displayCounter);
        if (!isParameter(Tool.PARAM_PANEL_HIDE)) {
            defineParameter(Tool.PARAM_PANEL_HIDE, value, Tool.GUI);
        } else {
            getTask().setParameter(Tool.PARAM_PANEL_HIDE, value);
        }
    }

    public TaskGraphContext getTaskGraphContext() {
        return getTask().getContext();
    }

    public void setVersion(String version) {
        getTask().setVersion(version);
    }

    public String getVersion() {
        return getTask().getVersion();
    }

    public void log(String msg) {
        log.info(msg);
    }

    public void debug(String msg) {
        log.fine(msg);
    }

    public void log(String msg, Throwable t) {
        log.info(msg + " Exception:" + FileUtils.formatThrowable(t));
    }

    public void debug(String msg, Throwable t) {
        log.fine(msg + " Exception:" + FileUtils.formatThrowable(t));
    }

}
