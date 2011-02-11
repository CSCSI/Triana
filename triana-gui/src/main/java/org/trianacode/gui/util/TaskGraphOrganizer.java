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

package org.trianacode.gui.util;


import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;

/**
 * Algorithms for organizing the task gtaph layout
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class TaskGraphOrganizer {
    private TaskGraphPanel panel;
    private TaskGraph taskgraph;

    public TaskGraphOrganizer(TaskGraphPanel panel) {
        this.panel = panel;
        this.taskgraph = panel.getTaskGraph();

    }

    public void organizeTaskGraph() {
        treeOrganize(taskgraph);
    }


    /**
     * Organises the taskgraph (and its sub-taskgraphs) into a tree pattern.
     */
    public void treeOrganize(TaskGraph taskgraph) {
        Task[] endtasks = getEndTasks(taskgraph);
        ArrayList handled = new ArrayList();
        float top = 0;
        Dimension2D size;

        for (int count = 0; count < endtasks.length; count++) {
            size = organizeTask(endtasks[count], handled, top);
            top += size.getHeight() + 0.5;
        }

        Task[] tasks = taskgraph.getTasks(false);
        for (int count = 0; count < tasks.length; count++) {
            if (tasks[count] instanceof TaskGraph) {
                treeOrganize((TaskGraph) tasks[count]);
            }
        }
    }

    /**
     * @return an array of tasks that do not output data to other tasks
     */
    private Task[] getEndTasks(TaskGraph taskgraph) {
        Task[] tasks = taskgraph.getTasks(false);
        Node[] outnodes;
        ArrayList endtasks = new ArrayList();
        boolean endtask;

        for (int count = 0; count < tasks.length; count++) {
            outnodes = tasks[count].getDataOutputNodes();
            endtask = true;

            for (int nodecount = 0; (nodecount < outnodes.length) && endtask; nodecount++) {
                if (outnodes[nodecount].isConnected()) {
                    endtask = false;
                }
            }

            if (endtask) {
                endtasks.add(tasks[count]);
            }
        }

        return (Task[]) endtasks.toArray(new Task[endtasks.size()]);
    }

    /**
     * Recursively organize the task and its subtasks, returning the area of the organized tasks
     */
    private Dimension2D organizeTask(Task task, ArrayList handled, double top) {
        Node[] innodes = task.getInputNodes();
        Dimension2D subsize;
        double width = 0;
        double height = 0;

        handled.add(task);
        TaskComponent tc = panel.getTaskComponent(task);
        Component c = tc.getComponent();
        for (int count = 0; count < innodes.length; count++) {
            if ((innodes[count].isConnected()) && (!handled.contains(innodes[count].getCable().getSendingTask()))) {
                subsize = organizeTask(innodes[count].getCable().getSendingTask(), handled, height + top);

                if (subsize.getWidth() > width) {
                    width = subsize.getWidth();
                }


                height += subsize.getHeight() + 0.5;
            }
        }

        height = Math.max(height - 0.5, 1);
        width += 0.5;

        task.setParameter(Task.GUI_X, String.valueOf(width));
        task.setParameter(Task.GUI_Y, String.valueOf(top + (height / 2) - 0.5));

        return new Dimension2DImp(width + 1, height);
    }


    private static class Dimension2DImp extends Dimension2D {

        private double height = 0;
        private double width = 0;


        public Dimension2DImp() {
        }

        public Dimension2DImp(double width, double height) {
            this.width = width;
            this.height = height;
        }


        public double getHeight() {
            return height;
        }

        public double getWidth() {
            return width;
        }

        public void setSize(double width, double height) {
            this.width = width;
            this.height = height;
        }

    }


}
