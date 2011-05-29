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
package org.trianacode.taskgraph.imp;

import org.trianacode.taskgraph.Cable;
import org.trianacode.taskgraph.CableException;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.event.CableEvent;
import org.trianacode.taskgraph.event.CableListener;

import java.util.ArrayList;

/**
 * A cable linking two Tasks in a TaskGraphImp. The Tasks are linked through their input/output Nodes.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class CableImp implements Cable {

    public static final String NON_RUNNABLE_CABLE = "NonRunnable";

    /**
     * the node that sends data to this cable
     */
    private Node sendnode;

    /**
     * the node that received data from this cable
     */
    private Node recnode;

    /**
     * an array list of the cable's listeners
     */
    private ArrayList listeners = new ArrayList();


    /**
     * @return the type of the cable
     */
    public String getType() {
        return NON_RUNNABLE_CABLE;
    }


    /**
     * Connect two nodes with this cable.
     */
    public void connect(Node sendnode, Node recnode) throws CableException {
        if ((sendnode == null) || (recnode == null)) {
            throw (new CableException("Cannot connect to null node"));
        }

        this.sendnode = sendnode;
        this.recnode = recnode;

        sendnode.connect(this);
        recnode.connect(this);
    }


    /**
     * Reconnects the sender to this cable
     */
    public void reconnect(Node node) throws CableException {
        if (node == null) {
            throw (new CableException("Cannot reconnect to null node"));
        }

        if (node.isOutputNode()) {
            if (sendnode != null) {
                sendnode.disconnect();
            }

            sendnode = node;
        } else {
            if (recnode != null) {
                recnode.disconnect();
            }

            recnode = node;
        }

        node.connect(this);

        notifyCableReconnected();
    }

    /**
     * Disconnect this cable from its nodes. Note that a disconnected cable cannot be reconnected.
     */
    public void disconnect() throws CableException {
        if (isConnected()) {
            sendnode.disconnect();
            recnode.disconnect();

            notifyCableDisconnected();
        }
    }


    /**
     * Adds a cable listener to this cable.
     */
    public void addCableListener(final CableListener listener) {
        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                if (!listeners.contains(listener)) {
                    listeners.add(listener);
                }
            }
        });
    }

    /**
     * Removes a cable listener from this cable.
     */
    public void removeCableListener(final CableListener listener) {
        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                listeners.remove(listener);
            }
        });
    }


    /**
     * @return true if this cable is connected.
     */
    public boolean isConnected() {
        return ((sendnode != null) && (recnode != null)
                && sendnode.isConnected() && recnode.isConnected()
                && sendnode.getCable() == this && recnode.getCable() == this);
    }


    /**
     * @return the node which sends data along this cable
     */
    public Node getSendingNode() {
        return sendnode;
    }

    /**
     * @return the task which sends data (via a node) along this cable
     */
    public Task getSendingTask() {
        if (sendnode != null) {
            return sendnode.getTask();
        } else {
            return null;
        }
    }


    /**
     * @return the node which receives data along this cable
     */
    public Node getReceivingNode() {
        return recnode;
    }

    /**
     * @return the task which receives data (via a node) along this cable
     */
    public Task getReceivingTask() {
        if (recnode != null) {
            return recnode.getTask();
        } else {
            return null;
        }
    }


    /**
     * @return true if the specified node sends data to or receives data from this cable
     */
    public boolean contains(Node node) {
        return (sendnode == node.getBottomLevelNode()) || (recnode == node.getBottomLevelNode());
    }

    /**
     * @return true if the specified task sends data to or receives data from this cable
     */
    public boolean connects(Task task) {
        Node node = sendnode;
        boolean contain = false;

        while ((!contain) && (node != null)) {
            contain = (node.getTask() == task);
            node = node.getParentNode();
        }

        node = recnode;

        while ((!contain) && (node != null)) {
            contain = (node.getTask() == task);
            node = node.getParentNode();
        }

        return contain;
    }


    /**
     * Notifies all the cable listeners that a the cable has been reconnected to different nodes;
     */
    private void notifyCableReconnected() {
        final Cable cable = this;

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                CableListener[] copy = (CableListener[]) listeners.toArray(new CableListener[listeners.size()]);
                CableEvent event = new CableEvent(cable);

                for (int count = 0; count < copy.length; count++) {
                    copy[count].cableReconnected(event);
                }

            }
        });
    }

    /**
     * ~«
     * Notifies all the cable listeners that a the cable has been disconnected.
     */
    private void notifyCableDisconnected() {
        final Cable cable = this;

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                CableListener[] copy = (CableListener[]) listeners.toArray(new CableListener[listeners.size()]);
                CableEvent event = new CableEvent(cable);

                for (int count = 0; count < copy.length; count++) {
                    copy[count].cableDisconnected(event);
                }
            }
        });
    }


    public String toString() {
        if (isConnected()) {
            return getSendingNode() + "-->" + getReceivingNode();
        } else {
            return getSendingNode() + "-x-" + getReceivingNode();
        }
    }
}
