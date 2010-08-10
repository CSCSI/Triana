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

package org.trianacode.gui.hci.tools;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.RenderingHint;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.service.TrianaClient;
import org.trianacode.taskgraph.tool.Tool;

/**
 * The TaskGraphViewManager maintains the current view for each taskgraph and returns the appropriate tree icon,
 * workspace popup, tool component based on that mode. The TaskGraphViewManager also maintains the default view keyed by
 * taskgraph tool class.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class TaskGraphViewManager {

    /**
     * A list of the available views
     */
    private static ArrayList views = new ArrayList();

    /**
     * The hashtable of the explicitally set views keyed by taskgraph
     */
    private static Hashtable modetable = new Hashtable();

    /**
     * A hashtable of the regustered TaskGraphViews keyed by taskgraph tool class
     */
    private static Hashtable classviews = new Hashtable();

    /**
     * The hashtable of the current views keyed by taskgraph
     */
    private static Hashtable viewcache = new Hashtable();

    /**
     * A list of the tool views in order of registration
     */
    private static ArrayList vieworder = new ArrayList();

    /**
     * The default taskgraph view
     */
    private static TaskGraphView defaultview;


    /**
     * @return the default taskgraph view
     */
    public static TaskGraphView getDefaultTaskgraphView() {
        return defaultview;
    }

    /**
     * Sets the default taskgraph view
     */
    public static void setDefaultTaskGraphView(TaskGraphView view) {
        defaultview = view;
    }


    /**
     * Registers a TaskGraphView for a particular taskgraph tool clas. Note that if a taskgraph has two view then the
     * later registered takes precedence. Automatically adds the view to the available views list.
     */
    public static void registerTaskGraphView(String toolclass, TaskGraphView view) {
        addTaskGraphView(view);
        classviews.put(toolclass, view);
        vieworder.remove(toolclass);
        vieworder.add(toolclass);
        viewcache.clear();
    }

    /**
     * Unregisters the TaskGraphView for a particular taskgraph tool class. Note that this method does not automatically
     * remove the mode from the available views list, use removeTaskGraphView.
     */
    public static void unregisterTaskGraphView(String toolclass) {
        classviews.remove(toolclass);
        vieworder.remove(toolclass);
        viewcache.clear();
    }


    /**
     * Adds the taskgraph view to the available views list
     */
    public static void addTaskGraphView(TaskGraphView view) {
        if (!views.contains(view)) {
            views.add(view);
        }
    }

    /**
     * Removes the taskgraph view from the available views list
     */
    public static void removeTaskGraphView(TaskGraphView view) {
        views.remove(view);
    }

    /**
     * @return a list of the taskgraph views
     */
    public static TaskGraphView[] getTaskGraphViews() {
        return (TaskGraphView[]) views.toArray(new TaskGraphView[views.size()]);
    }


    /**
     * Explicitally sets the view for the specified taskgraph
     */
    public void setTaskGraphView(TaskGraph taskgraph, TaskGraphView view) {
        modetable.put(taskgraph, view);
    }

    /**
     * Returns the view for the specified taskgraph to the default
     */
    public void clearTaskGraphView(TaskGraph taskgraph) {
        modetable.remove(taskgraph);
    }

    /**
     * Returns the current view for the specified taskgraph
     */
    public TaskGraphView getTaskGraphView(TaskGraph taskgraph) {
        return getView(taskgraph);
    }


    /**
     * @return the tree icon for the specified tool (if null is returned then the default leaf icon is used)
     */
    public static Icon getTreeIcon(Tool tool) {
        TaskGraphView mode;

        if (tool instanceof TaskGraph) {
            mode = getView((TaskGraph) tool);
        } else {
            mode = getDefaultTaskgraphView();
        }

        return mode.getTreeIcon(tool);
    }

    /**
     * @return the tool tip for the specified tool when in the tree
     */
    public static String getTreeToolTip(Tool tool, boolean extended) {
        TaskGraphView mode;

        if (tool instanceof TaskGraph) {
            mode = getView((TaskGraph) tool);
        } else {
            mode = getDefaultTaskgraphView();
        }

        return mode.getTreeToolTip(tool, extended);
    }

    /**
     * @return the right-click popup for the specified tool when in the tree
     */
    public static JPopupMenu getTreePopup(Tool tool) {
        TaskGraphView mode;

        if (tool instanceof TaskGraph) {
            mode = getView((TaskGraph) tool);
        } else {
            mode = getDefaultTaskgraphView();
        }

        return mode.getTreePopup(tool);
    }


    /**
     * @return the tool tip for the specified task when on the workspace
     */
    public static String getWorkspaceToolTip(Task task, boolean extended) {
        TaskGraphView mode;

        if (task instanceof TaskGraph) {
            mode = getView((TaskGraph) task);
        } else if (task.getParent() != null) {
            mode = getView(task.getParent());
        } else {
            mode = getDefaultTaskgraphView();
        }

        return mode.getWorkspaceToolTip(task, extended);
    }

    /**
     * @return the right-click popup for the specified task when on the workspace
     */
    public static JPopupMenu getWorkspacePopup(Task task) {
        TaskGraphView mode;

        if (task instanceof TaskGraph) {
            mode = getView((TaskGraph) task);
        } else if (task.getParent() != null) {
            mode = getView(task.getParent());
        } else {
            mode = getDefaultTaskgraphView();
        }

        return mode.getWorkspacePopup(task);
    }


    /**
     * @return the right-click popup menu for an open group (right-click on workspace background)
     */
    public static JPopupMenu getOpenGroupPopup(TaskGraph taskgraph) {
        TaskGraphView mode = getView(taskgraph);
        return mode.getOpenGroupPopup(taskgraph);
    }

    /**
     * @return the right-click popup menu for an multiple selected tasks.
     */
    public static JPopupMenu getMultipleSelectionPopup(TaskGraph taskgraph, Task[] tasks) {
        TaskGraphView mode = getView(taskgraph);
        return mode.getMultipleSelectionPopup(taskgraph, tasks);
    }


    /**
     * @return the action that is invoked when the task is activated (e.g. double-clicked).
     */
    public static Action getTaskAction(Task task) {
        TaskGraphView mode;

        if (task instanceof TaskGraph) {
            mode = getView((TaskGraph) task);
        } else if (task.getParent() != null) {
            mode = getView(task.getParent());
        } else {
            mode = getDefaultTaskgraphView();
        }

        return mode.getTaskAction(task);
    }

    /**
     * The task component used to represent the specified task
     */
    public static TaskComponent getTaskComponent(Task task) {
        TaskGraphView mode;

        if (task instanceof TaskGraph) {
            mode = getView((TaskGraph) task);
        } else if (task.getParent() != null) {
            mode = getView(task.getParent());
        } else {
            mode = getDefaultTaskgraphView();
        }

        return mode.getTaskComponent(task);
    }

    /**
     * @param action the update action (e.g. INCREASE_INPUT_NODES_ACTION as defined in UpdateActionConstants)
     * @return true if the update action icon should be shown for the specified action
     */
    public static boolean isUpdateIcon(Task task, String action) {
        TaskGraphView mode;

        if (task instanceof TaskGraph) {
            mode = getView((TaskGraph) task);
        } else if (task.getParent() != null) {
            mode = getView(task.getParent());
        } else {
            mode = getDefaultTaskgraphView();
        }

        return mode.isUpdateIcon(task, action);
    }

    /**
     * @param action the update action (e.g. INCREASE_INPUT_NODES_ACTION as defined in UpdateActionConstants)
     * @return the action associated with the specified update action.
     */
    public static Action getUpdateAction(Task task, String action) {
        TaskGraphView mode;

        if (task instanceof TaskGraph) {
            mode = getView((TaskGraph) task);
        } else if (task.getParent() != null) {
            mode = getView(task.getParent());
        } else {
            mode = getDefaultTaskgraphView();
        }

        return mode.getUpdateAction(task, action);
    }


    /**
     * The task component used to represent the specified task
     */
    public static TaskGraphPanel getTaskGraphPanel(TaskGraph taskgraph, TrianaClient client) {
        TaskGraphView view = getView(taskgraph);
        return view.getTaskGraphPanel(taskgraph, client);
    }


    private static TaskGraphView getView(TaskGraph taskgraph) {
        TaskGraphView mode;

        if (modetable.containsKey(taskgraph)) {
            mode = (TaskGraphView) modetable.get(taskgraph);
        } else if (viewcache.containsKey(taskgraph)) {
            mode = (TaskGraphView) viewcache.get(taskgraph);
        } else {
            mode = locateView(taskgraph);
            viewcache.put(taskgraph, mode);
        }

        return mode;
    }


    /**
     * @return the taskgraph view to use for the specified taskgraph
     */
    private static TaskGraphView locateView(TaskGraph taskgraph) {
        RenderingHint[] hints = taskgraph.getRenderingHints();
        String hint;
        TaskGraphView view = null;
        int priority = -1;

        for (int count = 0; count < hints.length; count++) {
            hint = hints[count].getRenderingHint();

            if (classviews.containsKey(hint)) {
                if ((view == null) || (vieworder.indexOf(hint) > priority)) {
                    view = (TaskGraphView) classviews.get(hint);
                    priority = vieworder.indexOf(hint);
                }
            }
        }

        if (view == null) {
            view = getDefaultTaskgraphView();
        }

        if (view == null) {
            throw (new RuntimeException("Default taskgraph view view not set on TaskGraphViewManager"));
        }

        return view;
    }

}
