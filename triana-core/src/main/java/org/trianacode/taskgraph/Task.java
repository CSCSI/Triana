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
package org.trianacode.taskgraph;

import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.tool.Tool;

/**
 * The interface to tasks within a taskgraph
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public interface Task extends Tool {

    public static enum ConnectionStatus {
        NOT_READY,
        NOT_CONNECTED
    }

    // task parameter name

    /**
     * GUI Position in the Triana co-ordinate space - relative to size of a task component
     */
    public static final String GUI_X = "guiX";
    public static final String GUI_Y = "guiY";
    public static final String DEPRECATED_GUI_XPOS = "guiXPos"; // deprecated
    public static final String DEPRECATED_GUI_YPOS = "guiYPos"; // deprecated

    public static final String EXECUTION_REQUEST_COUNT = "executionRequestCount";
    public static final String EXECUTION_COUNT = "executionCount";
    public static final String EXECUTION_STATE = "executionState";
    public static final String ERROR_MESSAGE = "errorMessage";

    public static final String OUTPUT_TYPE = "outputType";


    // the parameter that specified the default node requirement
    public static final String DEFAULT_NODE_REQUIREMENT = "defaultNodeRequirement";

    // the parameter that specifies whether data is required from a particular node
    // (e.g. requiredNode1 = ESSENTIAL_IF_CONNECTED)
    public static final String NODE_REQUIREMENT = "nodeRequirement";

    // parameter requirement options
    public static final String ESSENTIAL = "essential";
    public static final String ESSENTIAL_IF_CONNECTED = "essentialIfConnected";
    public static final String OPTIONAL = "optional";

    /**
     * @return the qualified name for this task (e.g. groupname1.groupname2.toolname).
     */
    public String getQualifiedTaskName();


    /**
     * Notifies task listeners that the proxy has been updated
     */
    public void updateProxy();


    /**
     * @return the taskgraph that this task is located within
     */
    public TaskGraph getParent();

    public TaskGraph getUltimateParent();

    /**
     * All copies of a task within a parameter space (sharing the same parameters) have the same intance id.
     *
     * @return the instance id of this task
     */
    public String getInstanceID();


    /**
     * Adds a task listener to this task.
     */
    public void addTaskListener(TaskListener listener);

    /**
     * Removes a task listener from this task.
     */
    public void removeTaskListener(TaskListener listener);


    /**
     * Adds a data input node.
     */
    public Node addDataInputNode() throws NodeException;

    /**
     * Removes a data input node.
     */
    public void removeDataInputNode(Node node);

    /**
     * @return the data input node at the specified index
     */
    public Node getDataInputNode(int index);

    /**
     * @return an array of data input nodes
     */
    public Node[] getDataInputNodes();

    /**
     * Adds a data output node.
     */
    public Node addDataOutputNode() throws NodeException;

    /**
     * Removes a data output node.
     */
    public void removeDataOutputNode(Node node);

    /**
     * @return the data output node at the specified index
     */
    public Node getDataOutputNode(int index);

    /**
     * @return an array of data output nodes
     */
    public Node[] getDataOutputNodes();

    /**
     * Adds a parameter input node for the specified parameter name.
     */
    public ParameterNode addParameterInputNode(String paramname) throws NodeException;

    /**
     * Removes a parameter input node.
     */
    public void removeParameterInputNode(ParameterNode node);

    /**
     * @return the parameter input node at the specified index
     */
    public ParameterNode getParameterInputNode(int index);

    /**
     * @return an array of parameter input nodes
     */
    public ParameterNode[] getParameterInputNodes();


    /**
     * Adds a parameter output node for the specified paramter name.
     */
    public ParameterNode addParameterOutputNode(String paramname) throws NodeException;

    /**
     * Removes a parameter output node.
     */
    public void removeParameterOutputNode(ParameterNode node);

    /**
     * @return the parameter output node at the specified index
     */
    public ParameterNode getParameterOutputNode(int index);

    /**
     * @return an array of parameter output nodes
     */
    public ParameterNode[] getParameterOutputNodes();


    /**
     * @return all input nodes (data and parameter)
     */
    public Node[] getInputNodes();

    /**
     * @return the input node at the specified absolute index (data/parameter)
     */
    public Node getInputNode(int absoluteindex);

    /**
     * @return all output nodes (data and parameter)
     */
    public Node[] getOutputNodes();

    /**
     * @return the output node at the specified absolute index (data/parameter)
     */
    public Node getOutputNode(int absoluteindex);


    /**
     * Removes the specified node
     */
    public void removeNode(Node node);


    /**
     * @return true if this task is set to run continuously
     */
    public boolean isRunContinuously();

    /**
     * Sets whether this task runs continuously.
     */
    public void setRunContinuously(boolean state);


    /**
     * Sets the default input node requirement for this task (ESSENTIAL, ESSENTIAL_IF_CONNECTED or OPTIONAL)
     */
    public void setDefaultNodeRequirement(String requirement);

    /**
     * @return the default requirement for this task's input nodes
     */
    public String getDefaultNodeRequirement();

    /**
     * Sets the node requirement for the specified input node index (ESSENTIAL, ESSENTIAL_IF_CONNECTED or OPTIONAL)
     */
    public void setNodeRequirement(int index, String requirement);

    /**
     * @return the requirement for this specified input node index.
     */
    public String getNodeRequirement(int index);


    /**
     * @return the number of times the task has been requested to execut
     */
    public int getExecutionRequestCount();

    /**
     * @return the number of times the task has been executed
     */
    public int getExecutionCount();

    /**
     * @return the current execution state of the task
     */
    public ExecutionState getExecutionState();

    /**
     * @return the error message associated with the current error (null if the task is not in an error state)
     */
    public String getErrorMessage();

    /**
     * =====================from Task======================
     */


    /**
     * Initialisation method is called immediately after the parent is set
     */
    public void init() throws TaskException;

    /**
     * Sets the parent for this task
     */
    public void setParent(TaskGraph taskgraph);


    /**
     * Returns the index of the specified node within the data input/output and parameter input/output nodes; or -1 if
     * not attached to this task. The index returned is not unique, e.g. the first data input node and the first data
     * output nodes will both return 0.
     */
    public int getNodeIndex(Node node);

    /**
     * This is a convience method to provide backward compatibility with TrianaGUI, in which parameter nodes where
     * indexed after data nodes.
     * <p/>
     * The absolute index of a data node is the same as its standard index. The absolute index of a parameter node is
     * its standard index + the total number of data input nodes.
     *
     * @return the absolute index of this node within its associated task.
     */
    public int getAbsoluteNodeIndex(Node node);


    /**
     * @return true if the specified node is a data input node for this task
     */
    public boolean isDataInputNode(Node node);

    /**
     * @return true if the specified node is a data output node for this task
     */
    public boolean isDataOutputNode(Node node);

    /**
     * @return true if the specified node is a parameter input node for this task
     */
    public boolean isParameterInputNode(Node node);

    /**
     * @return true if the specified node is a parameter output node for this task
     */
    public boolean isParameterOutputNode(Node node);


    /**
     * Cleans up any operations associated with this task
     */
    public void dispose();

    public TaskGraphContext getContext();

    public Object getContextProperty(String name);

    public void setContextProperty(String name, Object value);

    public void setSubTitle(String subtext);

    public String getSubTitle();

}
