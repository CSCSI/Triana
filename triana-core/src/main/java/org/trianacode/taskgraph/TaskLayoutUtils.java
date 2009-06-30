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

import org.trianacode.taskgraph.tool.Tool;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A set of utils for manipulating the location of tasks (as stored in their
 * GUI_XPOS and DEPRECATED_GUI_YPOS parameters). Note that the methods here assume
 * that visually all the tasks are zero width and height! The actual width and
 * heigth of tasks should be factored in by the graphical engine.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 6th Feb 2003
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class TaskLayoutUtils {

    /**
     * @return the position of the specfied task in the triana co-ordinate
     *         system
     */
    public static TPoint getPosition(Tool task) {
        if (task.isParameterName(Task.GUI_X) && task.isParameterName(Task.GUI_Y))
            return new TPoint(Double.parseDouble((String) task.getParameter(Task.GUI_X)),
                    Double.parseDouble((String) task.getParameter(Task.GUI_Y)));
        else if (task.isParameterName(Task.DEPRECATED_GUI_XPOS) && task.isParameterName(Task.DEPRECATED_GUI_YPOS)) {
            updateDeprecatedPosition(task);
            return getPosition(task);
        } else
            return new TPoint();
    }


    /**
     * @return the position of the specfied task on the triana workspace using
     *         the specified task layout details
     */
    public static TPoint getPosition(Tool task, TaskLayoutDetails layout) {
        if (task.isParameterName(Task.GUI_X) && task.isParameterName(Task.GUI_Y)) {
            TPoint pos = getPosition(task);

            int x = (int) (pos.getX() * layout.getTaskDimensions().getWidth()) + layout.getLeftBorder();
            int y = (int) (pos.getY() * layout.getTaskDimensions().getHeight()) + layout.getTopBorder();

            return new TPoint(x, y);
        } else if (task.isParameterName(Task.DEPRECATED_GUI_XPOS) && task.isParameterName(Task.DEPRECATED_GUI_YPOS)) {
            updateDeprecatedPosition(task);
            return getPosition(task, layout);
        } else
            return new TPoint();
    }

    private static void updateDeprecatedPosition(Tool task) {
        if (task.isParameterName(Task.DEPRECATED_GUI_XPOS) && task.isParameterName(Task.DEPRECATED_GUI_YPOS)) {
            int xpos = Integer.parseInt((String) task.getParameter(Task.DEPRECATED_GUI_XPOS));
            int ypos = Integer.parseInt((String) task.getParameter(Task.DEPRECATED_GUI_YPOS));
            setPosition(task, new TPoint((xpos) / 80, (ypos) / 34));
        }
    }

        /**
        * Sets the position of the specified task using Triana's co-ordinate system.
        */
    public static void setPosition(Task task, TPoint pos) {
        task.setParameterType(Task.GUI_X, Tool.GUI);
        task.setParameterType(Task.GUI_Y, Tool.GUI);

        task.setParameter(Task.GUI_X, String.valueOf(pos.getX()));
        task.setParameter(Task.GUI_Y, String.valueOf(pos.getY()));
    }

    /**
     * Sets the position of the specified task on the triana workspace using
     * the specified task layout details.
     */
    public static void setPosition(Task task, TPoint pos, TaskLayoutDetails details) {
        TPoint point = new TPoint(((pos.getX() - details.getLeftBorder())) / details.getTaskDimensions().getWidth(),
                ((pos.getY() - details.getTopBorder())) / details.getTaskDimensions().getHeight());

        setPosition(task, point);
    }

    /**
     * Sets the position of the specified task using Triana's co-ordinate system.
     */
    public static void setPosition(Tool tool, TPoint pos) {
        tool.setParameterType(Task.GUI_X, Tool.GUI);
        tool.setParameterType(Task.GUI_Y, Tool.GUI);

        tool.setParameter(Task.GUI_X, String.valueOf(pos.getX()));
        tool.setParameter(Task.GUI_Y, String.valueOf(pos.getY()));
    }

    /**
     * Sets the position of the specified task on the triana workspace using
     * the specified task layout details.
     */
    public static void setPosition(Tool tool, TPoint pos, TaskLayoutDetails details) {
        TPoint point = new TPoint(((pos.getX() - details.getLeftBorder())) / details.getTaskDimensions().getWidth(),
                ((pos.getY() - details.getTopBorder())) / details.getTaskDimensions().getHeight());

        setPosition(tool, point);
    }


    /**
     * Translates the task by the specified x and y distances
     */
    public static void translate(Task task, double xdist, double ydist) {
        TPoint point = getPosition(task);
        setPosition(task, new TPoint(point.getX() + xdist, point.getY() + ydist));
    }


    /**
     * Translates the specified tasks by the specified x and y distances
     */
    public static void translate(Task[] tasks, double xdist, double ydist) {
        TPoint point;

        for (int count = 0; count < tasks.length; count++) {
            point = getPosition(tasks[count]);
            setPosition(tasks[count], new TPoint(point.getX() + xdist, point.getY() + ydist));
        }
    }

        /**
     * Translates the specified tasks by the specified x and y distances
     */
    public static void translate(Task[] tasks, int xdist, int ydist, TaskLayoutDetails details) {
        TPoint taskpos;

        for (int count = 0; count < tasks.length; count++) {
            taskpos = getPosition(tasks[count], details);
            taskpos = new TPoint(taskpos.getX() + xdist, taskpos.getY() + ydist);

            setPosition(tasks[count], taskpos, details);
        }
    }


    /**
     * Translates the specified tasks so that the top lefthand corner is at
     * the origin (x=0, y=0).
     */
    public static void translateToOrigin(Task[] tasks) {
        TRectangle grouppos = getBoundingBox(tasks);
        TPoint taskpos;

        for (int count = 0; count < tasks.length; count++) {
            taskpos = getPosition(tasks[count]);
            setPosition(tasks[count], new TPoint(taskpos.getX() - grouppos.getX(), taskpos.getY() - grouppos.getY()));
        }
    }

    /**
     * Translates the specified tasks so that the top lefthand corner is at
     * the specified point
     */
    public static void translateTo(Task[] tasks, TPoint point) {
        TRectangle grouppos = getBoundingBox(tasks);
        TPoint taskpos;

        for (int count = 0; count < tasks.length; count++) {
            taskpos = getPosition(tasks[count]);
            setPosition(tasks[count], new TPoint(taskpos.getX() - grouppos.getX() + point.getX(), taskpos.getY() - grouppos.getY() + point.getY()));
        }
    }


    /**
     * @return the bounding box surrounding the specified tasks
     */
    public static TRectangle getBoundingBox(Task[] tasks) {
        double minx = Double.POSITIVE_INFINITY;
        double miny = Double.POSITIVE_INFINITY;
        double maxx = Double.NEGATIVE_INFINITY;
        double maxy = Double.NEGATIVE_INFINITY;

        TPoint pos;

        for (int count = 0; count < tasks.length; count++) {
            pos = getPosition(tasks[count]);

            if (pos.getX() < minx)
                minx = pos.getX();

            if (pos.getY() < miny)
                miny = pos.getY();

            if (pos.getX() > maxx)
                maxx = pos.getX();

            if (pos.getY() > maxy)
                maxy = pos.getY();
        }

        if ((minx != Double.POSITIVE_INFINITY) && (miny != Double.POSITIVE_INFINITY) &&
                (maxx != Double.NEGATIVE_INFINITY) && (maxy != Double.NEGATIVE_INFINITY))
            return new TRectangle(minx, miny, maxx - minx + 1, maxy - miny + 1);
        else               
            return new TRectangle();
    }

    /**
     * Replaces a current task with a new tool (instance id is preserved)
     */
    public static Task replaceTask(Task curtask, Tool newtool, boolean rename) throws TaskGraphException {
        return replaceTask(curtask, newtool, rename, true);
    }

    /**
     * Replaces a current task with a new tool.
     * <p/>
     * Note that newtool is a task with no parent it is automatically disposed
     * after the replacement task has been created.
     *
     * @param curtask      the task being replaced
     * @param newtool      the tool used to replace the existing task
     * @param rename       true if the new task should take the same name as the existing task
     * @param preserveinst true if the new task should preseve the instance id of newtool
     */
    public static Task replaceTask(Task curtask, Tool newtool, boolean rename, boolean preserveinst) throws TaskGraphException {
        Task t = TaskGraphUtils.replaceTask(curtask, newtool, rename, preserveinst);
        TPoint pos = getPosition(curtask);
        setPosition(newtool, pos);
        return t;
    }

    /**
     * Sets unconnected task nodes as group input/output nodes.
     */
    public static void resolveGroupNodes(TaskGraph taskgraph) throws TaskGraphException {
        if (taskgraph.isControlTaskConnected()) {
            TaskGraphUtils.disconnectControlTask(taskgraph);
            resolveGroupNodesNoControlTask(taskgraph);

            try {
                TaskGraphUtils.connectControlTask(taskgraph);
            } catch (TaskGraphException except) {
                except.printStackTrace();
                taskgraph.removeControlTask();
            }
        } else
            resolveGroupNodesNoControlTask(taskgraph);
    }

    /**
     * Sets unconnected task nodes as group input/output nodes when there is no looping task connected
     */
    private static void resolveGroupNodesNoControlTask(TaskGraph taskgraph) throws NodeException {
        Task[] tasks = taskgraph.getTasks(false);
        TaskGraph grouptask = taskgraph;
        Node[] nodes;
        ArrayList nodelist = new ArrayList();

        for (int count = 0; count < tasks.length; count++) {
            nodes = tasks[count].getInputNodes();

            for (int nodecount = 0; nodecount < nodes.length; nodecount++)
                if ((!nodes[nodecount].isConnected()) && (nodes[nodecount].getChildNode() == null))
                    addToNodeList(nodelist, nodes[nodecount]);

            for (Iterator iter = nodelist.iterator(); iter.hasNext();)
                grouptask.addDataInputNode((Node) iter.next());
        }

        nodelist.clear();

        for (int count = 0; count < tasks.length; count++) {
            nodes = tasks[count].getOutputNodes();

            for (int nodecount = 0; nodecount < nodes.length; nodecount++)
                if ((!nodes[nodecount].isConnected()) && (nodes[nodecount].getChildNode() == null))
                    addToNodeList(nodelist, nodes[nodecount]);

            for (Iterator iter = nodelist.iterator(); iter.hasNext();)
                grouptask.addDataOutputNode((Node) iter.next());
        }
    }

    /**
     * Adds the specified node to the nodelist maintaining the nodelist in
     * ascenting yposition order
     */
    private static void addToNodeList(ArrayList nodelist, Node newnode) {
        boolean insert = false;
        Node curnode;
        TPoint newpoint = getPosition(newnode.getTask());
        TPoint curpoint;

        for (int count = 0; (count < nodelist.size()) && (!insert); count++) {
            curnode = (Node) nodelist.get(count);
            curpoint = getPosition(curnode.getTask());

            if (newpoint.getY() < curpoint.getY()) {
                nodelist.add(count, newnode);
                insert = true;
            } else
            if ((newpoint.getY() == curpoint.getY()) && (newnode.getAbsoluteNodeIndex() < curnode.getAbsoluteNodeIndex()))
            {
                nodelist.add(count, newnode);
                insert = true;
            }
        }

        if (!insert)
            nodelist.add(newnode);
    }
}
