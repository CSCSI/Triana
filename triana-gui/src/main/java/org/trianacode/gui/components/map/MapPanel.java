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

package org.trianacode.gui.components.map;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.action.ToolSelectionListener;
import org.trianacode.gui.hci.tools.TaskGraphViewManager;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.RenderingHint;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.TaskLayoutDetails;
import org.trianacode.taskgraph.constants.MapConstants;
import org.trianacode.taskgraph.event.ControlTaskStateEvent;
import org.trianacode.taskgraph.event.TaskGraphCableEvent;
import org.trianacode.taskgraph.event.TaskGraphListener;
import org.trianacode.taskgraph.event.TaskGraphTaskEvent;
import org.trianacode.taskgraph.service.TrianaClient;
import org.trianacode.taskgraph.tool.Tool;

/**
 * A component that layouts the tasks out using geographic co-ordinates.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */


public class MapPanel extends JPanel implements TaskGraphPanel, TaskGraphListener,
        ToolSelectionHandler {


    /**
     * The taskgraph represented by this component
     */
    private TaskGraph taskgraph;

    /**
     * The client responsible for handling the taskgraph represented by this component
     */
    private TrianaClient client;


    /**
     * The layout manager responsible for positioning components on the map
     */
    private MapLayout layout;

    /**
     * The map image
     */
    private Image map;

    /**
     * A hashtable of the components for each task
     */
    private Hashtable comptable = new Hashtable();


    public MapPanel(TaskGraph taskgraph, TrianaClient client) {
        this.taskgraph = taskgraph;
        this.client = client;

        initLayout();
        initMap();

        taskgraph.addTaskGraphListener(this);
    }

    /**
     * Initializes the map layout
     */
    private void initLayout() {
        layout = new MapLayout(new Dimension(800, 600));
        layout.setMapPoint("131.251", new Point(230, 293));

        setLayout(layout);
    }

    /**
     * Initializes and loads the map image file
     */
    private void initMap() {
        if (taskgraph.isRenderingHint(MapConstants.MAP_RENDERING_HINT)) {
            RenderingHint hint = taskgraph.getRenderingHint(MapConstants.MAP_RENDERING_HINT);

            if ((hint != null) && (hint.getRenderingDetail(MapConstants.MAP_IMAGE_URL) != null)) {
                map = loadMap((String) hint.getRenderingDetail(MapConstants.MAP_IMAGE_URL));
            }
        }
    }

    /**
     * Loads and prepares the map image
     */
    private Image loadMap(String url) {
        try {
            map = Toolkit.getDefaultToolkit().getImage(new URL(url));
            prepareImage(map, this);

            return map;
        } catch (MalformedURLException except) {
            except.printStackTrace();
            return null;
        }
    }


    /**
     * Called to initialise the taskgraph panel
     */
    public void init() {
        Task[] tasks = taskgraph.getTasks(false);

        for (int count = 0; count < tasks.length; count++) {
            initializeTaskComponent(tasks[count]);
        }
    }

    /**
     * initializes a main triana tool at the specified location
     */
    private void initializeTaskComponent(Task task) {
        if (task.isRenderingHint(MapConstants.MAP_LOCATION_RENDERING_HINT)) {
            RenderingHint hint = task.getRenderingHint(MapConstants.MAP_LOCATION_RENDERING_HINT);
            String location = (String) hint.getRenderingDetail(MapConstants.MAP_LOCATION);

            TaskComponent comp = TaskGraphViewManager.getTaskComponent(task);
            comptable.put(task, comp);

            add(comp.getComponent(), location);
        }
    }


    /**
     * Adds a listener to be notified when the tool selection changes
     */
    public void addToolSelectionListener(ToolSelectionListener listener) {
    }

    /**
     * Removes a listener from being notified when the tool selection changes
     */
    public void removeToolSelectionListener(ToolSelectionListener listener) {
    }


    /**
     * @return the taskgraph interface this container represents
     */
    public TaskGraph getTaskGraph() {
        return taskgraph;
    }

    /**
     * @return the triana client for this taskgraph
     */
    public TrianaClient getTrianaClient() {
        return client;
    }


    /**
     * @return this container
     */
    public Container getContainer() {
        return this;
    }

    /**
     * @return the layout details for this taskgraph panel
     */
    public TaskLayoutDetails getLayoutDetails() {
        if (layout != null) {
            return new MapLayout(new Dimension(800, 600));
        } else {
            return layout;
        }
    }


    /**
     * @return the task component for the specified task (null if unknown)
     */
    public TaskComponent getTaskComponent(Task task) {
        if (comptable.containsKey(task)) {
            return (TaskComponent) comptable.get(task);
        } else {
            return null;
        }
    }

    /**
     * @return an array of the task components in the taskgraph container
     */
    public TaskComponent[] getTaskComponents() {
        return (TaskComponent[]) comptable.values().toArray(new TaskComponent[comptable.values().size()]);
    }

    /**
     * @return the number of task components in the taskgraph container
     */
    public int getTaskComponentCount() {
        return comptable.size();
    }


    /**
     * @return true if only a single tool is selected
     */
    public boolean isSingleSelectedTool() {
        return false;
    }

    /**
     * @return the currently selected tool (null if none selected)
     */
    public Tool getSelectedTool() {
        return null;
    }

    /**
     * @return an array of the currently selected tools
     */
    public Tool[] getSelectedTools() {
        return new Tool[0];
    }

    /**
     * @return the currently selected taskgraph (usually parent of selected tool)
     */
    public TaskGraph getSelectedTaskgraph() {
        return taskgraph;
    }


    /**
     * @return the triana client responsible for the selected tools (null if none)
     */
    public TrianaClient getSelectedTrianaClient() {
        return client;
    }


    /**
     * Paints the map
     */
    protected void paintComponent(Graphics graphs) {
        super.paintComponent(graphs);

        if (map != null) {
            Dimension size = getSize();
            if ((map.getWidth(this) != -1) && (map.getHeight(this) != -1)) {
                graphs.drawImage(map, 0, 0, size.width, size.height, this);
            }
        }
    }


    /**
     * Called when a new task is created in a taskgraph.
     */
    public void taskCreated(TaskGraphTaskEvent event) {
        final TaskGraphTaskEvent evt = event;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Task task = evt.getTask();

                if (!TaskGraphUtils.isControlTask(task)) {
                    initializeTaskComponent(task);
                }
            }
        });
    }

    /**
     * Called when a task is removed from a taskgraph. Note that this method is called when tasks are removed from a
     * taskgraph due to being grouped (they are placed in the new groups taskgraph).
     */
    public void taskRemoved(TaskGraphTaskEvent event) {
        final TaskGraphTaskEvent evt = event;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Task task = evt.getTask();

                TaskComponent comp = getTaskComponent(task);
                comptable.remove(task);

                if (comp != null) {
                    remove(comp.getComponent());
                    comp.dispose();
                }
            }
        });
    }

    /**
     * Called when a new connection is made between two tasks.
     */
    public void cableConnected(TaskGraphCableEvent event) {
    }

    /**
     * Called when a connection is reconnected to a different task.
     */
    public void cableReconnected(TaskGraphCableEvent event) {
    }

    /**
     * Called before a connection between two tasks is removed.
     */
    public void cableDisconnected(TaskGraphCableEvent event) {
    }

    /**
     * Called when the control task is connected/disconnected or unstable
     */
    public void controlTaskStateChanged(ControlTaskStateEvent event) {
    }


    /**
     * Dispose and clean-up the taskgraph container
     */
    public void dispose() {
        Task[] tasks = taskgraph.getTasks(true);
        TaskComponent comp;

        for (int count = 0; count < tasks.length; count++) {
            comp = getTaskComponent(tasks[count]);

            if (comp != null) {
                remove(comp.getComponent());
                comptable.remove(tasks[count]);
                comp.dispose();
            }
        }

        taskgraph.removeTaskGraphListener(this);
    }

}
