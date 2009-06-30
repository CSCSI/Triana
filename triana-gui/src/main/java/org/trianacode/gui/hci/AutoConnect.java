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
package org.trianacode.gui.hci;

import org.trianacode.gui.main.TrianaLayoutConstants;
import org.trianacode.taskgraph.CableException;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.service.TypeChecking;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Auto connect the nodes of a Task within a Main Triana
 *
<<<<<<< AutoConnect.java
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
=======
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
>>>>>>> 1.1.4.1
 */
public class AutoConnect {

    public static int TOOL_WIDTH = TrianaLayoutConstants.DEFAULT_TOOL_SIZE.width;
    public static int TOOL_HEIGHT = TrianaLayoutConstants.DEFAULT_TOOL_SIZE.height;

    private static double LEFT_SCALE = 2;
    private static double Y_SCALE = 2;

    private int x;
    private int y;


    public void autoConnect(Task task, int x, int y, TaskGraph taskgraph) {
        Node innode = getAvailableNode(task, true);
        Node outnode = getAvailableNode(task, false);
        boolean connection = true;

        this.x = x;
        this.y = y;

        while (connection && ((innode != null) || (outnode != null))) {
            connection = tryConnect(innode, outnode, taskgraph);

            innode = getAvailableNode(task, true);
            outnode = getAvailableNode(task, false);
        }
    }


    /**
     * try to connect a node to either the input or output node specified
     */
    private boolean tryConnect(Node innode, Node outnode, TaskGraph taskgraph) {
        ArrayList ininfo = new ArrayList();
        ArrayList outinfo = new ArrayList();

        if (outnode != null) {
            Node[] innodes = getNodes(taskgraph.getTasks(false), true);
            double inscore;

            for (int count = 0; count < innodes.length; count++) {
                inscore = getConnectionScore(outnode, innodes[count], taskgraph, false);

                if (inscore > Double.NEGATIVE_INFINITY)
                    ininfo.add(new ConnectionInfo(innodes[count], inscore));
            }
        }

        if (innode != null) {
            Node[] outnodes = getNodes(taskgraph.getTasks(false), false);
            double outscore;

            for (int count = 0; count < outnodes.length; count++) {
                outscore = getConnectionScore(outnodes[count], innode, taskgraph, true);

                if (outscore > Double.NEGATIVE_INFINITY)
                    outinfo.add(new ConnectionInfo(outnodes[count], outscore));
            }
        }

        return tryConnect(innode, outnode, ininfo, outinfo, taskgraph);
    }

    /**
     * try to connect either the input or output node specified to the best possible node
     */
    private boolean tryConnect(Node innode, Node outnode,
                               ArrayList ininfo, ArrayList outinfo, TaskGraph taskgraph) {
        boolean tryconnect = true;
        boolean connection = false;

        while (tryconnect && (!connection)) {
            ConnectionInfo bestin = getBestConnectionInfo(ininfo);
            ConnectionInfo bestout = getBestConnectionInfo(outinfo);

            boolean input = false;

            if ((bestin != null) && (bestout != null)) {
                if (bestin.score > bestout.score)
                    input = true;
                else
                    input = false;
            } else if (bestin != null)
                input = true;
            else if (bestout != null)
                input = false;
            else
                tryconnect = false;

            double oldscore = Double.NEGATIVE_INFINITY;
            Node oldnode = null;

            if (tryconnect) {
                if (input) {
                    if (bestin.node.isConnected()) {
                        oldnode = bestin.node.getCable().getSendingNode();
                        oldscore = getConnectionScore(oldnode, bestin.node, taskgraph, true);
                    }

                    if (oldscore < bestin.score) {
                        try {
                            taskgraph.connect(outnode, bestin.node);
                            connection = true;

                            if (oldnode != null)
                                tryConnect(null, oldnode, taskgraph);
                        } catch (CableException except) {
                        }
                    } else
                        ininfo.remove(bestin);
                } else {
                    if (bestout.node.isConnected()) {
                        oldnode = bestout.node.getCable().getReceivingNode();
                        oldscore = getConnectionScore(bestout.node, oldnode, taskgraph, false);
                    }

                    if (oldscore < bestout.score) {
                        try {
                            taskgraph.connect(bestout.node, innode);
                            connection = true;

                            if ((oldnode != null) && (oldnode.getTask() != null))
                                tryConnect(oldnode, null, taskgraph);
                        } catch (CableException except) {
                        }
                    } else
                        outinfo.remove(bestout);
                }
            }
        }

        return connection;

    }


    /**
     * @return an unconnected input/output node on the specified task
     */
    private Node getAvailableNode(Task task, boolean input) {
        Node nodes[];
        Node node = null;

        if (input)
            nodes = task.getDataInputNodes();
        else
            nodes = task.getDataOutputNodes();

        for (int count = 0; ((count < nodes.length) && (node == null)); count++) {
            if (!nodes[count].isConnected())
                node = nodes[count];
        }

        return node;
    }


    /**
     * @return all the input/output nodes in a set of tasks
     */
    private Node[] getNodes(Task[] tasks, boolean input) {
        ArrayList list = new ArrayList();
        Node[] nodes;

        for (int tcount = 0; tcount < tasks.length; tcount++) {
            if (input == true)
                nodes = tasks[tcount].getDataInputNodes();
            else
                nodes = tasks[tcount].getDataOutputNodes();

            for (int ncount = 0; ncount < nodes.length; ncount++)
                list.add(nodes[ncount]);
        }

        return (Node[]) list.toArray(new Node[list.size()]);
    }

    /**
     * @return a score denoting the suitability of connection an input node to an
     *         output node (higher = better)
     */
    private double getConnectionScore(Node outnode, Node innode, TaskGraph taskgraph, boolean input) {
        if (!TypeChecking.isCompatibility(outnode, innode))
            return Double.NEGATIVE_INFINITY;

        if (taskgraph.getTask(outnode) == taskgraph.getTask(innode))
            return Double.NEGATIVE_INFINITY;

        if (isLoop(outnode, innode, new ArrayList()))
            return Double.NEGATIVE_INFINITY;

        if ((!outnode.isBottomLevelNode()) || (!innode.isBottomLevelNode()))
            return Double.NEGATIVE_INFINITY;

        if (outnode.isConnected() && (outnode.getCable().getReceivingTask() == taskgraph.getControlTask()))
            return Double.NEGATIVE_INFINITY;

        if (innode.isConnected() && (innode.getCable().getSendingTask() == taskgraph.getControlTask()))
            return Double.NEGATIVE_INFINITY;

        double inx = x;
        double iny = y;
        double outx = x + TOOL_WIDTH;
        double outy = y;

        try {
            if (outnode.getTask().isParameterName(Task.GUI_X))
                outx = Double.parseDouble((String) outnode.getTask().getParameter(Task.GUI_X)) + TOOL_WIDTH;

            if (outnode.getTask().isParameterName(Task.GUI_Y))
                outy = Double.parseDouble((String) outnode.getTask().getParameter(Task.GUI_Y));

            if (innode.getTask().isParameterName(Task.GUI_X))
                inx = Double.parseDouble((String) innode.getTask().getParameter(Task.GUI_X)) + TOOL_WIDTH;

            if (innode.getTask().isParameterName(Task.GUI_Y))
                iny = Double.parseDouble((String) innode.getTask().getParameter(Task.GUI_Y));

            double xval = Math.abs(outx - inx);
            double yval = Math.abs(outy - iny) * Y_SCALE;

            if (inx < outx)
                xval *= LEFT_SCALE;

            return -xval - (yval * Y_SCALE);
        } catch (Exception except) {
            return Double.NEGATIVE_INFINITY;
        }
    }

    /**
     * @return true if connecting outnode to innode will cause a loop
     */
    public boolean isLoop(Node outnode, Node innode, ArrayList list) {
        if (innode.getTask() == outnode.getTask())
            return true;
        else {
            list.add(outnode.getTask());

            Node[] innodes = outnode.getTask().getDataInputNodes();
            boolean loop = false;

            for (int count = 0; (count < innodes.length) && (!loop); count++)
                if (innodes[count].isConnected() && (!list.contains(innodes[count].getCable().getSendingTask())))
                    loop = isLoop(innodes[count].getCable().getSendingNode(), innode, list);

            return loop;
        }
    }


    /**
     * @return the maximum score in an array list of connection infos
     */
    private ConnectionInfo getBestConnectionInfo(ArrayList list) {
        ConnectionInfo best = null;
        ConnectionInfo info;
        Iterator iter = list.iterator();

        while (iter.hasNext()) {
            info = (ConnectionInfo) iter.next();

            if ((best == null) || (info.score > best.score))
                best = info;
        }

        return best;
    }

    private class ConnectionInfo {

        public Node node;
        public double score;

        public ConnectionInfo(Node node, double score) {
            this.node = node;
            this.score = score;
        }

    }

}
