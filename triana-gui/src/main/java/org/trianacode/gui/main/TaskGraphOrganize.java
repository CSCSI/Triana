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

package org.trianacode.gui.main;


import org.trianacode.taskgraph.*;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Algorithms for organizing the task gtaph layout
 *
 * @author      Ian Wang
 * @created     19th April 2004
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $

 */

public class TaskGraphOrganize {

    public static final int TREE_ORGANIZE = 0;
    public static final int GRAPH_ORGANIZE = 1;

    private static final int GRAPH_CONFLICT_WEIGHT = 2;


    public static void organizeTaskGraph(int policy, TaskGraph taskgraph) {
        if (policy == TREE_ORGANIZE)
            treeOrganize(taskgraph);
        else
            graphOrganize(taskgraph);
    }


    /**
     * Organises the taskgraph (and its sub-taskgraphs) into a tree
     * pattern.
     */
    public static void treeOrganize(TaskGraph taskgraph) {
        Task[] endtasks = getEndTasks(taskgraph);
        ArrayList handled = new ArrayList();
        float top = 0;
        Dimension2D size;

        for (int count = 0; count < endtasks.length; count++) {
            size = organizeTask(endtasks[count], handled, top);
            top += size.getHeight() + 0.5;
        }

        Task[] tasks = taskgraph.getTasks(false);
        for (int count = 0; count < tasks.length; count++)
            if (tasks[count] instanceof TaskGraph)
                treeOrganize((TaskGraph) tasks[count]);
    }

    /**
     * @return an array of tasks that do not output data to other tasks
     */
    private static Task[] getEndTasks(TaskGraph taskgraph) {
        Task[] tasks = taskgraph.getTasks(false);
        Node[] outnodes;
        ArrayList endtasks = new ArrayList();
        boolean endtask;

        for (int count = 0; count < tasks.length; count++) {
            outnodes = tasks[count].getDataOutputNodes();
            endtask = true;

            for (int nodecount = 0; (nodecount < outnodes.length) && endtask; nodecount++)
                if (outnodes[nodecount].isConnected())
                    endtask = false;

            if (endtask)
                endtasks.add(tasks[count]);
        }

        return (Task[]) endtasks.toArray(new Task[endtasks.size()]);
    }

    /**
     * Recursively organize the task and its subtasks, returning the area
     * of the organized tasks
     */
    private static Dimension2D organizeTask(Task task, ArrayList handled, double top) {
        Node[] innodes = task.getInputNodes();
        Dimension2D subsize;
        double width = 0;
        double height = 0;

        handled.add(task);

        for (int count = 0; count < innodes.length; count++)
            if ((innodes[count].isConnected()) && (!handled.contains(innodes[count].getCable().getSendingTask()))) {
                subsize = organizeTask(innodes[count].getCable().getSendingTask(), handled, height + top);

                if (subsize.getWidth() > width)
                    width = subsize.getWidth();

                height += subsize.getHeight() + 0.5;
            }

        height = Math.max(height - 0.5, 1);
        width += 0.5;

        task.setParameter(Task.GUI_X, String.valueOf(width));
        task.setParameter(Task.GUI_Y, String.valueOf(top + (height / 2) - 0.5));

        return new Dimension2DImp(width + 1, height);
    }


    /**
     * Using a hill-climbing heuristic to organize the taskgraph so that a
     * there is minimal cable conflicts
     */
    private static void graphOrganize(TaskGraph taskgraph) {
        Task tasks[] = taskgraph.getTasks(false);
        Hashtable postable = new Hashtable();

        int gwidth = Math.max((int) Math.ceil(Math.sqrt(tasks.length)) * 3 + 2, 8);
        int gheight = Math.max((int) Math.ceil(Math.sqrt(tasks.length)) * 3 + 2, 8);

        Task[][] layoutgrid = new Task[gwidth][gheight];
        Point pos;

        // initial assignments to the layout grid
        for (int count = 0; count < tasks.length; count++) {
            pos = getFreePosition(layoutgrid);

            layoutgrid[pos.x][pos.y] = tasks[count];
            postable.put(tasks[count], pos);
        }

        // the score of the current graph layout
        Hashtable cablepoints = new Hashtable();
        int[][] scoregrid = initScoreGrid(layoutgrid, tasks, postable, cablepoints);
        int graphscore = getConflictScore(scoregrid);
        int tempscore;

        // hill climb for a number of iterations based on the number of tasks
        int iterations = 100 * tasks.length;
        Task task;
        Point oldpos;
        Point newpos;

        for (int count = 0; count < iterations; count++) {
            task = tasks[(int) Math.floor(Math.random() * tasks.length)];
            oldpos = (Point) postable.get(task);
            newpos = getFreePosition(layoutgrid);

            layoutgrid[oldpos.x][oldpos.y] = null;
            layoutgrid[newpos.x][newpos.y] = task;
            updateScoreGrid(task, scoregrid, postable, newpos, cablepoints);

            tempscore = getConflictScore(scoregrid);

            if (tempscore < graphscore) {
                graphscore = tempscore;
            } else {
                layoutgrid[oldpos.x][oldpos.y] = task;
                layoutgrid[newpos.x][newpos.y] = null;
                updateScoreGrid(task, scoregrid, postable, oldpos, cablepoints);
            }
        }

        // assign tasks to their positions
        for (int count = 0; count < tasks.length; count++) {
            pos = (Point) postable.get(tasks[count]);

            TaskLayoutUtils.setPosition(tasks[count], new TPoint((pos.x - 1) * (0.75), (pos.y - 1) * (0.75)));
        }

        TaskLayoutUtils.translateToOrigin(tasks);
    }


    /**
     * @return an unoccupied space in the layout grid
     */
    private static Point getFreePosition(Task[][] layoutgrid) {
        int x;
        int y;

        do {
            x = (int) Math.floor(Math.random() * ((layoutgrid.length - 1) / 2)) * 2 + 1;
            y = (int) Math.floor(Math.random() * ((layoutgrid[0].length - 1) / 2)) * 2 + 1;
        } while(layoutgrid[x][y] != null);

        return new Point(x, y);
    }

    /**
     * @return the score for the give layout based on the number of cable
     * conflicts
     */
    private static int[][] initScoreGrid(Task[][] layoutgrid, Task[]  tasks, Hashtable postable, Hashtable cablepoints) {
        Cable[] cables = TaskGraphUtils.getInternalCables(tasks);
        int[][] scoregrid = new int[layoutgrid.length][layoutgrid[0].length];

        for (int count1 = 0; count1 < scoregrid.length; count1++)
            for (int count2 = 0; count2 < scoregrid[count1].length; count2++)
                if (layoutgrid[count1][count2] != null)
                    updateScoreGrid(scoregrid, new Point(count1, count2), 1);

        Point[] cablepos;

        for (int count = 0; count < cables.length; count++) {
            cablepos = getCablePositions(cables[count], postable);
            cablepoints.put(cables[count], cablepos);

            for (int pcount = 0; pcount < cablepos.length; pcount++)
                updateScoreGrid(scoregrid, cablepos[pcount], 1);
        }

        return scoregrid;
    }

    /**
     * change the score grid to reflect the changes in the task positions
     */
    private static void updateScoreGrid(Task task, int[][] scoregrid, Hashtable postable, Point newpos, Hashtable cablepoints) {
        Node[] innodes;
        Node[] outnodes;
        innodes = task.getInputNodes();
        outnodes = task.getOutputNodes();

        updateScoreGrid(scoregrid, (Point) postable.get(task), -1);

        postable.put(task, newpos);
        updateScoreGrid(scoregrid, newpos, 1);

        for (int ncount = 0; ncount < innodes.length; ncount++)
            if ((innodes[ncount].isConnected()) && (cablepoints.containsKey(innodes[ncount].getCable())))
                updateCable(innodes[ncount].getCable(), scoregrid, postable, cablepoints);

        for (int ncount = 0; ncount < outnodes.length; ncount++)
            if ((outnodes[ncount].isConnected()) && (cablepoints.containsKey(outnodes[ncount].getCable())))
                updateCable(outnodes[ncount].getCable(), scoregrid, postable, cablepoints);
    }

    /**
     * Update the score grid to reflect the new cable position
     */
    private static void updateCable(Cable cable, int[][] scoregrid, Hashtable postable, Hashtable cablepoints) {
        Point[] points = (Point []) cablepoints.get(cable);

        for (int pcount = 0; pcount < points.length; pcount++)
            updateScoreGrid(scoregrid, points[pcount], -1);

        points = getCablePositions(cable, postable);
        cablepoints.put(cable, points);

        for (int pcount = 0; pcount < points.length; pcount++)
            updateScoreGrid(scoregrid, points[pcount], 1);
    }

    private static void updateScoreGrid(int[][] scoregrid, Point point, int val) {
        if ((point.x >= 0) && (point.x < scoregrid.length) &&
                (point.y >= 0) && (point.y < scoregrid[0].length))
            scoregrid[point.x][point.y] += val;
    }

    /**
     * @return positions the cable occupies
     */
    private static Point[] getCablePositions(Cable cable, Hashtable postable) {
        Task sendtask = cable.getSendingTask();
        Task rectask = cable.getReceivingTask();
        Point sendpos = (Point) postable.get(sendtask);
        Point recpos = (Point) postable.get(rectask);
        ArrayList points = new ArrayList();

        int xdiff = recpos.x - sendpos.x;
        int ydiff = recpos.y - sendpos.y;

        if (xdiff > 0) {
            for (int count = 0; count < xdiff / 2 - 1; count++) {
                points.add(new Point(sendpos.x + count + 1, sendpos.y));
                points.add(new Point(recpos.x - count - 1, recpos.y));
            }

            if (xdiff > 1)
                for (int count = 0; count < Math.abs(ydiff) + 1; count++) {
                    if (ydiff > 0)
                        points.add(new Point(sendpos.x + (xdiff / 2), sendpos.y + count));
                    else
                        points.add(new Point(sendpos.x + (xdiff / 2), sendpos.y - count));
                }
        } else {
            if (Math.abs(ydiff) > 1)
                for (int count = 0; count < Math.abs(ydiff) / 2; count++) {
                    if (ydiff > 0) {
                        points.add(new Point(sendpos.x + 1, sendpos.y + count));
                        points.add(new Point(recpos.x - 1, recpos.y - count));
                    } else {
                        points.add(new Point(sendpos.x + 1, sendpos.y - count));
                        points.add(new Point(recpos.x - 1, recpos.y + count));
                    }
                }

            for (int count = 0; count < Math.abs(xdiff) + 3; count++)
                points.add(new Point(sendpos.x + 1 - count, sendpos.y + (ydiff / 2)));
        }

        return (Point[]) points.toArray(new Point[points.size()]);
    }


    /**
     * @return the score of the score grid reflecting any conflicts
     */
    private static int getConflictScore(int[][] scoregrid) {
        int score = 0;

        for (int count1 = 0; count1 < scoregrid.length; count1++)
            for (int count2 = 0; count2 < scoregrid[count1].length; count2++) {
                score += scoregrid[count1][count2];

                if (scoregrid[count1][count2] > 1)
                    score += (scoregrid[count1][count2] - 1) * GRAPH_CONFLICT_WEIGHT;
            }

        return score;
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
