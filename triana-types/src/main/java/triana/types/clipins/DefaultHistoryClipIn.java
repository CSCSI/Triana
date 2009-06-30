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
package triana.types.clipins;


import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.clipin.AttachInfo;
import org.trianacode.taskgraph.clipin.ClipInBucket;
import org.trianacode.taskgraph.clipin.HistoryClipIn;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The history clip-in dynamically builds a taskgraph containing
 * the tasks that it passes through. This taskgraph provides a history of how
 * the data set it is attached to was generated. When run this taskgraph should
 * reproduce the data set.
 * <p/>
 * When data is generated from multiple input data sets, the histroy clip-ins
 * from the inputs are combined to produce a single taskgraph.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 5th February 2002
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class DefaultHistoryClipIn implements HistoryClipIn {

    /**
     * the dynamically created history taskgraph
     */
    private TaskGraph taskgraph;

    /**
     * a hashtable lookup between an original instance id and the new task
     * in the history
     */
    private Hashtable tasklookup = new Hashtable();

    /**
     * an list of the control task keys
     */
    private ArrayList controlkeys = new ArrayList();

    /**
     * the name of the task the clip-in was last output from
     */
    private String outtask = null;

    /**
     * the index of the node that the clip-in was output from
     */
    private int outindex = -1;


    public DefaultHistoryClipIn() {
        try {
            taskgraph = (TaskGraph) TaskGraphManager.createTaskGraph();
        } catch(TaskException except) {
            except.printStackTrace();
        }
    }

    /**
     * @return the history taskgraph that has been constructed
     */
    public TaskGraph getHistory() {
        return removeControlTasks(taskgraph);
    }


    /**
     * @return the history taskgraph that has been constructed without
     *         removing the control tasks
     */
    TaskGraph getFullHistory() {
        return taskgraph;
    }

    /**
     * @return an array of the control task keys;
     */
    ControlKey[] getControlKeys() {
        return (ControlKey[]) controlkeys.toArray(new ControlKey[controlkeys.size()]);
    }

    /**
     * @return a new history key for the task
     */
    String createHistoryKey(Task task) {
        return task.getInstanceID() + "_" + task.getExecutionCount();
    }

    /**
     * @return the history key for the task represented by the specified task in
     *         the history
     */
    String getHistoryKey(Task task) {
        Enumeration enumeration = tasklookup.keys();
        String key;

        while (enumeration.hasMoreElements()) {
            key = (String) enumeration.nextElement();

            if (tasklookup.get(key) == task)
                return key;
        }

        return null;
    }


    /**
     * This method is called before the clip-in enters a task's
     * clip-in bucket. This occurs when either the data it is attached
     * to is input by the task, or when the unit directly adds the
     * clip-in to its bucket.
     *
     * @param info info about the task the clip-in is being attached to
     */
    public void initializeAttach(AttachInfo info) {
        try {
            Task current = taskgraph.createTask(info.getTaskInterface(), false);
            String key = createHistoryKey(info.getTaskInterface());
            tasklookup.put(key, current);

            if (TaskGraphUtils.isControlTask(info.getTaskInterface()))
                controlkeys.add(new ControlKey(key, info.getTaskInterface().getParent().getDataInputNodeCount()));

            if (outindex != -1) {
                Node innode = current.getInputNode(info.getNode().getAbsoluteNodeIndex());
                Node outnode = taskgraph.getTask(outtask).getOutputNode(outindex);

                taskgraph.connect(outnode, innode);
            }

            outtask = current.getToolName();
            outindex = -1;

            ClipInBucket bucket = info.getClipInBucket();

            if (bucket.isClipInName(HISTORY_CLIPIN_NAME)) {
                DefaultHistoryClipIn oldhistory = (DefaultHistoryClipIn) bucket.getClipIn(HISTORY_CLIPIN_NAME);
                Task[] tasks = oldhistory.getFullHistory().getTasks(false);

                for (int count = 0; count < tasks.length; count++) {
                    if (!tasklookup.containsKey(oldhistory.getHistoryKey(tasks[count]))) {
                        Task newtask = taskgraph.createTask(tasks[count], false);
                        tasklookup.put(oldhistory.getHistoryKey(tasks[count]), newtask);
                    }
                }

                Cable[] cables = TaskGraphUtils.getInternalCables(tasks);
                for (int count = 0; count < cables.length; count++) {
                    Task newsendtask = (Task) tasklookup.get(oldhistory.getHistoryKey(cables[count].getSendingTask()));
                    Task newrectask = (Task) tasklookup.get(oldhistory.getHistoryKey(cables[count].getReceivingTask()));

                    Node sendnode = newsendtask.getOutputNode(cables[count].getSendingNode().getAbsoluteNodeIndex());
                    Node recnode = newrectask.getInputNode(cables[count].getReceivingNode().getAbsoluteNodeIndex());
                    taskgraph.connect(sendnode, recnode);
                }

                ControlKey[] oldkeys = oldhistory.getControlKeys();
                for (int count = 0; count < oldkeys.length; count++)
                    if (!controlkeys.contains(oldkeys[count]))
                        controlkeys.add(oldkeys[count]);
            }
        } catch (TaskGraphException except) {
            throw(new RuntimeException("Error constructing history: " + except.getMessage()));
        }
    }


    /**
     * This method is called when the clip-in is removed from a
     * task's clip-in bucket. This occurs when either the data it is
     * attached to is output by the task, or when the unit directly
     * remove the clip-in from its bucket.
     *
     * @param info info about the task the clip-in is being removed from
     */
    public void finalizeAttach(AttachInfo info) {
        if (info.getNode() != null)
            outindex = info.getNode().getAbsoluteNodeIndex();
    }

    /**
     * Clones the ClipIn to an identical one. This is a copy by value,
     * not by reference. This method must be implemented for each class
     * in a way that depends on the contents of the ClipIn.
     *
     * @return a copy by value of the current ClipIn
     */
    public Object clone() {
        try {
            DefaultHistoryClipIn clone = new DefaultHistoryClipIn();
            clone.taskgraph = (TaskGraph) TaskGraphUtils.copyTaskGraph(taskgraph, TaskGraphManager.NON_RUNNABLE_FACTORY_TYPE);
            clone.outtask = outtask;
            clone.outindex = outindex;

            Enumeration enumeration = tasklookup.keys();
            String id;
            Task task;

            while (enumeration.hasMoreElements()) {
                id = (String) enumeration.nextElement();
                task = (Task) tasklookup.get(id);

                clone.tasklookup.put(id, clone.taskgraph.getTask(task.getToolName()));
            }

            ControlKey[] keys = getControlKeys();
            for (int count = 0; count < keys.length; count++)
                clone.controlkeys.add(keys[count]);

            return clone;
        } catch (TaskGraphException except) {
            throw(new RuntimeException("Error cloning history clip-in: " + except.getMessage()));
        }
    }


    /**
     * Removes the control tasks from the specified history taskgraph
     */
    TaskGraph removeControlTasks(TaskGraph history) {
        try {
            TaskGraph copy = (TaskGraph) TaskGraphUtils.copyTaskGraph(history, TaskGraphManager.NON_RUNNABLE_FACTORY_TYPE);
            ControlKey[] keys = getControlKeys();
            Task task;

            for (int count = 0; count < keys.length; count++) {
                if (tasklookup.containsKey(keys[count].key)) {
                    task = (Task) tasklookup.get(keys[count].key);

                    if (copy.containsTask(task.getToolName()))
                        removeControlTask(copy, copy.getTask(task.getToolName()), keys[count].groupinput);
                }
            }

            return copy;
        } catch (TaskGraphException except) {
            throw(new RuntimeException("Error removing control tasks from history clip-in: " + except.getMessage()));
        }
    }

    /**
     * Removes the specified control task from the copy taskgraph
     */
    private void removeControlTask(TaskGraph copy, Task control, int groupinput) throws CableException {
        Node[] nodes = control.getDataInputNodes();
        Node sendnode;
        Node recnode;

        int groupoutput = control.getDataInputNodeCount() - groupinput;

        for (int count = 0; count < nodes.length; count++) {
            if (nodes[count].isConnected()) {
                sendnode = nodes[count].getCable().getSendingNode();

                // start/end loop
                if (count < groupinput)
                    recnode = control.getDataOutputNode(groupoutput + count);
                else
                    recnode = control.getDataOutputNode(count - groupinput);

                if (recnode.isConnected())
                    copy.connect(sendnode, recnode.getCable().getReceivingNode());
                else {
                    // in loop/no loop
                    if ((count < groupinput) && (count < groupoutput))
                        recnode = control.getDataOutputNode(count);
                    else if ((count > groupinput) && (count - groupinput < groupinput));
                        recnode = control.getDataOutputNode(count - groupinput + groupoutput);

                    if (recnode.isConnected())
                        copy.connect(sendnode, recnode.getCable().getReceivingNode());
                }
            }
        }

        copy.removeTask(control);
    }


    private class ControlKey {

        public String key;
        public int groupinput;

        public ControlKey(String key, int groupinput) {
            this.key = key;
            this.groupinput = groupinput;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ControlKey)) return false;

            final ControlKey controlKey = (ControlKey) o;

            if (key != null ? !key.equals(controlKey.key) : controlKey.key != null) return false;

            return true;
        }

        public int hashCode() {
            return (key != null ? key.hashCode() : 0);
        }

    }

}
