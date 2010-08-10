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
package org.trianacode.taskgraph.event;

import java.util.EventObject;

import org.trianacode.taskgraph.Cable;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.TaskGraph;

/**
 * The event generated when a cable is connected/disconnected within a taskgraph
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class TaskGraphCableEvent extends EventObject {

    public static final int CABLE_CONNECTED = 0;
    public static final int CABLE_RECONNECTED_INPUT = 1;
    public static final int CABLE_RECONNECTED_OUTPUT = 2;
    public static final int CABLE_DISCONNECTED = 3;

    public static final int NON_GROUP_EVENT = 0;
    public static final int GROUP_EVENT = 1;
    public static final int UNGROUP_EVENT = 2;


    private int id;
    private TaskGraph taskgraph;

    private Cable cable;
    private String sendid;
    private int sendnode;
    private String recid;
    private int recnode;
    private int groupevent;

    /**
     * Constructs a task graph cable event
     *
     * @param id        the id of this event (either CABLE_CONNECTED, CABLE_RECONNECTED_INPUT, CABLE_RECONNECTED_OUTPUT
     *                  or CABLE_DISCONNECTED)
     * @param taskgraph the source of the task graph event
     * @param cable     the cable being connected/disconnected
     */
    public TaskGraphCableEvent(int id, TaskGraph taskgraph, Cable cable, int groupevent) {
        super(taskgraph);

        this.id = id;
        this.taskgraph = taskgraph;
        this.cable = cable;
        this.sendid = cable.getSendingTask().getInstanceID();
        this.sendnode = cable.getSendingNode().getAbsoluteNodeIndex();
        this.recid = cable.getReceivingTask().getInstanceID();
        this.recnode = cable.getReceivingNode().getAbsoluteNodeIndex();
        this.groupevent = groupevent;
    }

    /**
     * Constructs a task graph cable event
     *
     * @param id        the id of this event (either CABLE_CONNECTED, CABLE_RECONNECTED_INPUT, CABLE_RECONNECTED_OUTPUT
     *                  or CABLE_DISCONNECTED)
     * @param taskgraph the source of the task graph event
     * @param sendid    the instance id of the sending task
     * @param sendnode  the absolute index of the sending node
     * @param recid     the instance id of the receiving task
     * @param recnode   the absolute index of the receiving node
     */
    public TaskGraphCableEvent(int id, TaskGraph taskgraph, String sendid, int sendnode, String recid, int recnode,
                               int groupevent) {
        super(taskgraph);

        this.id = id;
        this.cable = getCable(taskgraph, sendid, sendnode, recid, recnode);
        this.taskgraph = taskgraph;
        this.sendid = sendid;
        this.sendnode = sendnode;
        this.recid = recid;
        this.recnode = recnode;
        this.groupevent = groupevent;
    }

    /**
     * @return the cable for this event (could be null if no longer valid)
     */
    private Cable getCable(TaskGraph taskgraph, String sendid, int sendnode, String recid, int recnode) {
        try {
            Node send = taskgraph.getTaskInstance(sendid).getOutputNode(sendnode);
            Node rec = taskgraph.getTaskInstance(recid).getInputNode(recnode);

            if (send.getCable() == rec.getCable()) {
                return send.getCable();
            } else {
                return null;
            }
        } catch (Exception except) {
            return null;
        }
    }


    /**
     * @return the id of this event (either CABLE_CONNECTED, CABLE_RECONNECTED_INPUT, CABLE_RECONNECTED_OUTPUT or
     *         CABLE_DISCONNECTED)
     */
    public int getID() {
        return id;
    }

    /**
     * @return true if this cable event is due to the cable being grouped (GROUP_EVENT) or ungrouped (UNGROUP_EVENT).
     *         Returns NON_GROUP_EVENT otherwise;
     */
    public int getGroupEventID() {
        return groupevent;
    }


    /**
     * @return the source of the task graph event
     */
    public TaskGraph getTaskGraph() {
        return taskgraph;
    }

    /**
     * @return the cable being disconnected
     */
    public Cable getCable() {
        return cable;
    }


    /**
     * @return the instance id of the cable's sending task
     */
    public String getSendingTaskID() {
        return sendid;
    }

    /**
     * @return the absolute index of the cable's sending node
     */
    public int getSendingNodeIndex() {
        return sendnode;
    }

    /**
     * @return the instance of the cable's receiving task
     */
    public String getReceivingTaskID() {
        return recid;
    }

    /**
     * @return the absolute index of the cable's receiving node
     */
    public int getReceivingNodeIndex() {
        return recnode;
    }


}
