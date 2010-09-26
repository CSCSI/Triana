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

import org.trianacode.taskgraph.event.TaskGraphListener;
import org.trianacode.taskgraph.service.ExecutionListener;
import org.trianacode.taskgraph.tool.Tool;

/**
 * An interface to TaskGraphs
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public interface TaskGraph extends Task, ExecutionListener {

    public static final int CONTROL_TASK_CONNECTED = 0;
    public static final int CONTROL_TASK_DISCONNECTED = 1;
    public static final int CONTROL_TASK_UNSTABLE = 2;


    /**
     * Adds a execution listener to this runnable instance
     */
    public void addExecutionListener(ExecutionListener listener);
    /**
     * Removes a execution listener from this runnable instance
     */
    public void removeExecutionListener(ExecutionListener listener);


    /**
     * Constructs a new task for looping over the group, optionally preserving the original instance id in the new task.
     * This method does not connect the control task, which should be done using TaskGraphUtils.connectControlTask()
     */
    public Task createControlTask(Tool tool, boolean preserveinst) throws TaskException;

    /**
     * Removes the loop task from the group
     */
    public void removeControlTask();

    /**
     * @return the task responsible for looping over the group
     */
    public Task getControlTask();

    /**
     * @return true if a loop task exists for this group
     */
    public boolean isControlTask();

    /**
     * @return true if the control task is connected
     */
    public boolean isControlTaskConnected();

    /**
     * @return true if the control task is unstable
     */
    public boolean isControlTaskUnstable();

    /**
     * @return the state of the control task (CONTROL_TASK_CONNECTED, CONTROL_TASK_DISCONNECTED or
     *         CONTROL_TASK_UNSTABLE)
     */
    public int getControlTaskState();

    /**
     * Sets the state of the control task (CONTROL_TASK_CONNECTED, CONTROL_TASK_DISCONNECTED or CONTROL_TASK_UNSTABLE)
     */
    public void setControlTaskState(int state);


    /**
     * Adds a taskgraph listener to this taskgraph.
     */
    public void addTaskGraphListener(TaskGraphListener listener);

    /**
     * Removes a taskgraph listener from this taskgraph.
     */
    public void removeTaskGraphListener(TaskGraphListener listener);


    /**
     * Create a new task in this taskgraph.
     *
     * @return an interface to the new task
     */
    public Task createTask(Tool tool) throws TaskException;

    /**
     * Create a new task in this taskgraph, optionally preserving the instance id from the original task in the new
     * task.
     *
     * @return an interface to the new task
     */
    public Task createTask(Tool tool, boolean preserveinst) throws TaskException;

    /**
     * Remove the specified task from the taskgraph.
     */
    public void removeTask(Task task);


    /**
     * @return an array of all the tasks contained with this taskgraph, optionally including the control task
     */
    public Task[] getTasks(boolean includecontrol);

    /**
     * @return the task with the specified name
     */
    public Task getTask(String name);

    /**
     * @return the task with the specified instance id
     */
    public Task getTaskInstance(String instanceid);

    /**
     * @return the task within this taskgraph linked to the specified node
     */
    public Task getTask(Node node);


    /**
     * @return true if the taskgraph contains the specified task
     */
    public boolean containsTask(String identifier);

    /**
     * @return true if the taskgraph contains the specified task instance
     */
    public boolean containsTaskInstance(String instanceid);

    /**
     * @return true if the taskgraph contains the specified task
     */
    public boolean containsTask(Task task);

    /**
     * @return true if a task represented within this taskgraph is linked to the specified node
     */
    public boolean containsNode(Node node);


    /**
     * Groups the specified tasks, returning the taskgraph created. A default mapping between group nodes and task nodes
     * is created. This can be changed later through setDataInputNode etc. in TaskGraph.
     */
    public TaskGraph groupTasks(String[] tasknames, String groupname) throws TaskGraphException;

    /**
     * Ungroups the specified group task
     */
    public void unGroupTask(String groupname) throws TaskGraphException;


    /**
     * Create a cable connecting sendnode to recnode
     */
    public Cable connect(Node sendnode, Node recnode) throws CableException;

    /**
     * Disconnects the specified cable from its nodes.
     */
    public void disconnect(Cable cable) throws CableException;


    /**
     * ================from TaskGraph==================
     */

    /**
     * Adds a data input node, with the specified node as a parent
     */
    public Node addDataInputNode(Node parent) throws NodeException;

    /**
     * Adds a data output node, with the specified node as a parent
     */
    public Node addDataOutputNode(Node parent) throws NodeException;

    /**
     * Sets the parent of a group input/output node
     */
    public void setGroupNodeParent(Node groupnode, Node parent);

    /**
     * @return the parent of a group input/output node
     */
    public Node getGroupNodeParent(Node groupnode);

    /**
     * Swaps the parents of two group nodes (without disconnecting either). Useful for reordering input/output nodes.
     */
    public void swapGroupNodeParents(Node groupnode1, Node groupnode2);

    /**
     * ================from TaskGraph==================
     */


    /**
     * removes tasks and notifies listeners when the taskgraph is ungrouped, but does not dispose of the tasks
     */
    public void ungroup();

    /**
     * cleans up any operations associated with this taskgraph and the tasks within it
     */
    public void dispose();

}
