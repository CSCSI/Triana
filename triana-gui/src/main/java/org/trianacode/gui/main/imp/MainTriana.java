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
package org.trianacode.gui.main.imp;


import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.MainTrianaKeyMapFactory;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.action.ToolSelectionListener;
import org.trianacode.gui.action.clipboard.ClipboardActionInterface;
import org.trianacode.gui.action.clipboard.ClipboardPasteInterface;
import org.trianacode.gui.hci.Clipboard;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.hci.color.ColorManager;
import org.trianacode.gui.hci.tools.TaskGraphViewManager;
import org.trianacode.gui.hci.tools.UpdateActionConstants;
import org.trianacode.gui.main.*;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.event.*;
import org.trianacode.taskgraph.service.TrianaClient;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.util.Env;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A MainTriana is an area which contains Triana algorithms.  Here units are placed at certain positions and connected
 * together in a left-to-right ordering.  If the data which a sending unit is compatable to the data the receiving unit
 * can receive then a connection takes place which is indicated by a line being drawn between the output node on the
 * sending unit and the input node on the receiving unit.</p> <p/> A MainTriana is only concerned with the GUI side of
 * connecting units together. I have tried to separate the GUI from the actual triana engine as much as possible in this
 * release so that we create things like a scripting language etc. at a later stage. </p><p>
 *
 * @author Ian Taylor <<<<<<< MainTriana.java
 * @version $Revision: 4048 $ >>>>>>> 1.13.2.1
 */
public class MainTriana extends JPanel
        implements TaskGraphPanel, ShowToolPanel, SelectionBoxInterface,
        IndicationCableInterface, TaskGraphListener, TaskListener, NodeListener,
        ClipboardActionInterface, ClipboardPasteInterface, ToolSelectionHandler {


    /**
     * The TaskGraphImp associated with this main triana
     */
    private TaskGraph taskgraph;

    /**
     * The client that directs actions to the Triana server
     */
    private TrianaClient client;

    /**
     * A hashtable of the triana tools in this main triana
     */
    private Hashtable tooltable = new Hashtable();

    /**
     * the layout manage for the main triana
     */
    private MainTrianaLayout layout;

    /**
     * The Hashtable of cables currently being drawn
     */
    private Hashtable drawingCables = new Hashtable();

    /**
     * The cable dragged when creating a new cable
     */
    private DrawCable indiccable;

    /**
     * A hashtable of for show tool monitors keyed by group node
     */
    private Hashtable showmonitors = new Hashtable();

    /**
     * An array of the show cables
     */
    private Hashtable showcables = new Hashtable();


    private boolean drawIndicationCableInterface = false;


    /**
     * the origin of the temporary selection box (null if not drawn)
     */
    private Point selorigin = null;

    /**
     * the dimension of the temporary selection box (null if not drawn)
     */
    private Dimension seldimension = null;

    /**
     * flag indicating whether smooth cables are currently used
     */
    private boolean smooth = false;


    /**
     * Creates a MainTriana for an OCL session.
     */
    public MainTriana(TaskGraph taskgraph, TrianaClient client) {
        super();

        layout = new MainTrianaLayout(TrianaLayoutConstants.DEFAULT_NODE_SIZE.width,
                TrianaLayoutConstants.DEFAULT_TOOL_SIZE.width / 2);
        setLayout(layout);

        addActionMaps();

        this.taskgraph = taskgraph;
        this.client = client;

        taskgraph.addTaskGraphListener(this);

        taskgraph.addTaskListener(this);
        repaint();
    }

    /**
     * Set the key binding maps for this component.
     */
    private void addActionMaps() {
        MainTrianaKeyMapFactory keymaps = new MainTrianaKeyMapFactory(this, ActionDisplayOptions.DISPLAY_NAME);
        InputMap inputMap = keymaps.getInputMap();
        inputMap.setParent(this.getInputMap());
        this.setInputMap(JComponent.WHEN_FOCUSED, inputMap);
        ActionMap actMap = keymaps.getActionMap();
        actMap.setParent(this.getActionMap());
        this.setActionMap(actMap);
    }

    /**
     * Called by application frame immediately after the Main Triana has been added to a frame, used to initialise the
     * gui and populate the main triana.
     */
    public void init() {
        populateMainTriana();
        initGroupNodes();
        invalidate();
        validate();
        repaint();
    }

    /**
     * instantiates the main triana tools for tasks represented by the taskgraph
     */
    private void populateMainTriana() {
        Task[] tasks = taskgraph.getTasks(false);

        for (int count = 0; count < tasks.length; count++) {
            initializeTaskComponent(tasks[count]);
        }

        Cable[] cables = TaskGraphUtils.getInternalCables(tasks);
        TaskComponent sendtask;
        TaskComponent rectask;
        NodeComponent sendnode;
        NodeComponent recnode;

        for (int count = 0; count < cables.length; count++) {
            sendtask = getTaskComponent(cables[count].getSendingTask());
            rectask = getTaskComponent(cables[count].getReceivingTask());

            sendnode = sendtask.getNodeComponent(cables[count].getSendingNode());
            recnode = rectask.getNodeComponent(cables[count].getReceivingNode());

            drawingCables.put(cables[count],
                    CableFactory.createDrawCable(cables[count], sendnode.getComponent(), recnode.getComponent(), this));
        }
    }

    /**
     * creates node added events for each group input/output node
     */
    private void initGroupNodes() {
        Node[] nodes = taskgraph.getDataInputNodes();
        for (int count = 0; count < nodes.length; count++) {
            nodeAdded(new TaskNodeEvent(TaskNodeEvent.NODE_ADDED, taskgraph, nodes[count], count, count));

            if (nodes[count].isConnected()) {
                nodeConnected(new NodeEvent(nodes[count]));
            }
        }

        nodes = taskgraph.getDataOutputNodes();
        for (int count = 0; count < nodes.length; count++) {
            nodeAdded(new TaskNodeEvent(TaskNodeEvent.NODE_ADDED, taskgraph, nodes[count], count, count));

            if (nodes[count].isConnected()) {
                nodeConnected(new NodeEvent(nodes[count]));
            }
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
     * @return this container
     */
    public Container getContainer() {
        return this;
    }

    /**
     * @return the TaskGraph associated with this MainTriana
     */
    public TaskGraph getTaskGraph() {
        return taskgraph;
    }

    /**
     * @return the layout details for this taskgraph panel
     */
    public TaskLayoutDetails getLayoutDetails() {
        return layout;
    }

    /**
     * @return the TrianaClient associated with this MainTriana
     */
    public TrianaClient getTrianaClient() {
        return client;
    }


    /**
     * Add a show tool and the cable connecting that showtool
     */
    public void addShowTool(ForShowComponent showtool, DrawCable cable) {
        showcables.put(showtool, cable);

        if (showtool.getGroupNode().isInputNode()) {
            add(showtool.getComponent(), MainTrianaLayout.LEFT_FORSHOW);
        } else {
            add(showtool.getComponent(), MainTrianaLayout.RIGHT_FORSHOW);
        }
    }

    /**
     * Remove a show tool and the cable connecting that showtool
     */
    public void removeShowTool(Component showtool) {
        if (showcables.containsKey(showtool)) {
            showcables.remove(showtool);
            remove(showtool);
        }
    }


    /**
     * @return the origin of the selection box
     */
    public Point getSelectionOrigin() {
        return selorigin;
    }

    /**
     * @return the size of the selection box
     */
    public Dimension getSelectionDimension() {
        return seldimension;
    }

    /**
     * @return true if there is a current selection box
     */
    public boolean isSelectionBox() {
        return (selorigin != null) && (seldimension != null);
    }

    /**
     * Sets the origin and dimensions of the selection box
     */
    public void setSelectionBox(Point origin, Dimension size) {
        this.selorigin = origin;
        this.seldimension = size;
        repaint();
    }

    /**
     * Clears the selection box
     */
    public void clearSelectionBox() {
        this.selorigin = null;
        this.seldimension = null;
        repaint();
    }


    /**
     * @return the type for the specified cable
     */
    private String getCableType(Cable cable) {
        Node receivenode = cable.getReceivingNode();
        String type = DrawCable.DEFAULT_TYPE;

        if (receivenode.isParameterNode() && receivenode.isEssential()) {
            type = Cable.CONTROL_CABLE_TYPE;
        }

        return type;
    }


    /**
     * Sets the start component and endpoint of the indication cable
     */
    public void drawIndicationCableInterface(Component start, Point end) {
        if ((indiccable == null) || (indiccable.getStartComponent() != start)) {
            indiccable = CableFactory.createDrawCable(start, this);
        }
        indiccable.setColor(Color.darkGray);

        indiccable.setRelativeEndPoint(end);
        drawIndicationCableInterface = true;

        repaint();
    }

    /**
     * Clears the indication cable
     */
    public void clearIndicationCableInterface() {
        drawIndicationCableInterface = false;
        indiccable = null;

        repaint();
    }


    /**
     * @return the node the indic cable is currently over (null if no indic cable or not over a node component).
     */
    public NodeComponent getIndicationNode() {
        if ((drawIndicationCableInterface) && (indiccable != null)) {
            Point endpoint = indiccable.getEndPoint();
            Component subcomp = getComponentAt(endpoint);
            Component comp;

            do {
                comp = subcomp;

                if (comp == null) {
                    return null;
                } else if (comp instanceof NodeComponent) {
                    return (NodeComponent) comp;
                } else if (!(comp instanceof Container)) {
                    return null;
                }

                subcomp = comp.getComponentAt(SwingUtilities.convertPoint(this, endpoint, comp));
            } while (comp != subcomp);
        }

        return null;
    }


    /**
     * Overrides the default paint() method and just calls the update method.
     */
    public void paintComponent(Graphics graphs) {
        setBackground(getBackgroundColor());
        super.paintComponent(graphs);

        paintSelected(graphs);
        paintCables(graphs);
        paintShowCables(graphs);
        paintIndicationCableInterface(graphs);
        paintSelectionBox(graphs);
    }


    /**
     * Paints the selected tools in  front of the other tools
     */
    long time = System.currentTimeMillis();

    private void paintSelected(Graphics graphs) {
        TaskComponent[] comps = getSelectedComponents();

        long timeNow = System.currentTimeMillis();
        if (timeNow - time > 5000) {
            //         System.out.println("Painting at : " + timeNow);
            time = timeNow;
            for (int count = 0; count < comps.length; count++) {
                comps[count].getComponent().repaint();
            }
        }
    }

    /**
     * Paints the cables
     */
    private void paintCables(Graphics graphs) {
        validateCables();

        Enumeration enumeration = drawingCables.elements();
        DrawCable cable;

        while (enumeration.hasMoreElements()) {
            cable = ((DrawCable) enumeration.nextElement());

            cable.setWidth(Math.max((int) (layout.getZoom() * cable.getDefaultWidth()), 1));
            cable.setColor(getCableColor(cable.getCable()));
            cable.drawCable(graphs);
        }
    }

    private void validateCables() {
        if (smooth != GUIEnv.isSmoothCables()) {
            Enumeration enumeration = drawingCables.keys();
            DrawCable cable;
            Object key;

            while (enumeration.hasMoreElements()) {
                key = enumeration.nextElement();
                cable = (DrawCable) drawingCables.get(key);
                drawingCables.put(key, CableFactory.createDrawCable(cable.getCable(), cable.getStartComponent(),
                        cable.getEndComponent(), cable.getSurface()));
            }

            smooth = GUIEnv.isSmoothCables();
            repaint();
        }
    }

    /**
     * Paints the for show tools and for show cables
     */
    private void paintShowCables(Graphics graphs) {
        if (showcables.size() > 0) {
            DrawCable[] cables = (DrawCable[]) showcables.values().toArray(new DrawCable[showcables.values().size()]);

            for (int count = 0; count < cables.length; count++) {
                cables[count].setWidth(
                        Math.max((int) (layout.getZoom() * cables[count].getDefaultWidth() * (2.0 / 3.0)), 1));
                cables[count].drawCable(graphs);
            }
        }
    }


    /**
     * Paints the temporary box for selecting componenet
     */
    private void paintSelectionBox(Graphics graphs) {
        if (isSelectionBox()) {
            int x = Math.min(selorigin.x, selorigin.x + seldimension.width);
            int y = Math.min(selorigin.y, selorigin.y + seldimension.height);

            Color col = graphs.getColor();

            graphs.setColor(getBackground().darker());
            graphs.drawRect(x, y, Math.abs(seldimension.width), Math.abs(seldimension.height));

            graphs.setColor(col);
        }
    }

    /**
     * Paints the indication cable when two tasks are being connected
     *
     * @param graphs
     */
    private void paintIndicationCableInterface(Graphics graphs) {
        if (drawIndicationCableInterface) {
            indiccable.setWidth((int) (layout.getZoom() * indiccable.getDefaultWidth()));
            indiccable.drawCable(graphs);
        }
    }


    /**
     * initializes a main triana tool at the specified location
     */
    private TaskComponent initializeTaskComponent(Task task) {
        TaskComponent comp = TaskGraphViewManager.getTaskComponent(task);

        tooltable.put(task, comp);
        task.addTaskListener(this);

        add(comp.getComponent(), MainTrianaLayout.TASK);
        doLayout();

        return comp;
    }

    /**
     * @return an array of the selected Main Triana Tools.
     */
    public TaskComponent[] getSelectedComponents() {
        TaskComponent[] comps = getTaskComponents();
        ArrayList selected = new ArrayList();

        for (int count = 0; count < comps.length; count++) {
            if (comps[count].isSelected()) {
                selected.add(comps[count]);
            }
        }

        return (TaskComponent[]) selected.toArray(new TaskComponent[selected.size()]);
    }


    /**
     * Puts the currently selected tasks in the clipboard.
     */
    public void copySelected() throws TaskGraphException {
        TaskComponent[] tools = getSelectedComponents();
        Task[] tasks = new Task[tools.length];

        for (int count = 0; count < tasks.length; count++) {
            tasks[count] = tools[count].getTaskInterface();
        }

        Clipboard.putTools(tasks, false);
    }

    /**
     * Puts the currently selected tasks in the clipboard and removes them from the taskgraph
     */
    public void cutSelected() throws TaskGraphException {
        copySelected();
        deleteSelected();
    }

    /**
     * Adds the tasks stored in the clipboard to the taskgraph
     */
    public void paste() throws TaskGraphException {
        boolean autocon = GUIEnv.isAutoConnect();
        GUIEnv.setAutoConnect(false);

        TaskComponent[] tools = getSelectedComponents();

        for (int count = 0; count < tools.length; count++) {
            tools[count].setSelected(false);
        }

        TaskGraphUtils.createTasks(Clipboard.getTools(false), taskgraph, false);

        GUIEnv.setAutoConnect(autocon);
    }

    /**
     * Deletes highlighted MainTrianaTools.
     */
    public void deleteSelected() {
        TaskComponent[] tools = getSelectedComponents();

        for (int count = 0; count < tools.length; count++) {
            taskgraph.removeTask(tools[count].getTaskInterface());
        }
    }


    /**
     * Copy selected Tools to the Clipboard.
     */
    public void copyToClipboard() throws TaskGraphException {
        copySelected();
    }

    /**
     * Copy selected Tools to the Clipboard and delete them from the Container they are located in.
     */
    public void cutToClipboard() throws TaskGraphException {
        cutSelected();
    }

    /**
     * Delete the selected Tool.
     */
    public void deleteTools(boolean files) {
        deleteSelected();
    }

    /**
     * Rename the selected Tool or Group.
     */
    public void renameTool() {
        TaskComponent[] selectedTools = getSelectedComponents();
        for (int i = 0; i < selectedTools.length; i++) {
            TaskComponent selectedTool = selectedTools[i];
            Task task = selectedTool.getTaskInterface();
            String name = (String) JOptionPane.showInputDialog(GUIEnv.getApplicationFrame(),
                    Env.getString("newNameFor") + " " + task.getToolName() + "?",
                    Env.getString("Rename"),
                    JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon(), null, task.getToolName());
            if ((name != null) && (!name.equals(task.getToolName()))) {
                task.setToolName(name);
            }
        }
    }

    /**
     * Paste Tools from the Clipboard to this container.
     */
    public void pasteFromClipboard() throws TaskGraphException {
        paste();
    }


    /**
     * @return true if only a single tool is selected
     */
    public boolean isSingleSelectedTool() {
        return getSelectedComponents().length == 1;
    }

    /**
     * @return the currently selected tool (null if none selected)
     */
    public Tool getSelectedTool() {
        TaskComponent[] taskcomps = getSelectedComponents();

        if (taskcomps.length > 0) {
            return taskcomps[0].getTaskInterface();
        } else {
            return null;
        }
    }

    /**
     * @return an array of the currently selected tools
     */
    public Tool[] getSelectedTools() {
        TaskComponent[] taskcomps = getSelectedComponents();
        Task[] tasks = new Task[taskcomps.length];

        for (int count = 0; count < tasks.length; count++) {
            tasks[count] = taskcomps[count].getTaskInterface();
        }

        return tasks;
    }

    /**
     * @return the triana client responsible for the selected tools (null if none)
     */
    public TrianaClient getSelectedTrianaClient() {
        return getTrianaClient();
    }

    /**
     * @return the currently selected taskgraph (usually parent of selected tool)
     */
    public TaskGraph getSelectedTaskgraph() {
        return taskgraph;
    }


    /**
     * @return the task component associated with the specified task
     */
    public TaskComponent getTaskComponent(Task task) {
        return (TaskComponent) tooltable.get(task);
    }

    /**
     * @return an array of all the task components
     */
    public TaskComponent[] getTaskComponents() {
        return (TaskComponent[]) tooltable.values().toArray(new TaskComponent[tooltable.values().size()]);
    }

    /**
     * @return the number of task components
     */
    public int getTaskComponentCount() {
        return tooltable.size();
    }


    /**
     * @return the cable colour associated with the specified cable, or null if unknown
     */
    public Color getCableColor(Cable cable) {
        return ColorManager.getColor(cable);
    }

    public Color getBackgroundColor() {
        return ColorManager.getBackgroundColor();
    }


    /**
     * Called when a new task is created in a taskgraph.
     */
    public void taskCreated(TaskGraphTaskEvent event) {
        final TaskGraphTaskEvent evt = event;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Task task = evt.getTask();
                TaskComponent comp;

                if (!TaskGraphUtils.isControlTask(task)) {
                    comp = initializeTaskComponent(task);

                    invalidate();
                    validate();
                    repaint();

                    Action action = TaskGraphViewManager
                            .getUpdateAction(task, UpdateActionConstants.TASK_CREATED_ACTION);

                    if (action != null) {
                        TaskComponent[] comps = getTaskComponents();

                        for (int count = 0; count < comps.length; count++) {
                            comps[count].setSelected(comps[count] == comp);
                        }

                        ActionEvent actevt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                (String) action.getValue(Action.ACTION_COMMAND_KEY));
                        action.actionPerformed(actevt);
                    }
                }
            }
        });
    }

    /**
     * Called when a task is removed from a taskgraph. Note that this method is called when tasks are removed from a
     * taskgraph due to being grouped (they are place in the group's taskgraph).
     */
    public void taskRemoved(TaskGraphTaskEvent event) {
        final TaskGraphTaskEvent evt = event;
        final TaskListener listener = this;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Task task = evt.getTask();

                task.removeTaskListener(listener);

                TaskComponent comp = getTaskComponent(task);
                tooltable.remove(task);

                if (comp != null) {
                    remove(comp.getComponent());
                    comp.dispose();
                }

                invalidate();
                validate();
                repaint();
            }
        });
    }

    /**
     * Called when a new connection is made between two tasks.
     */
    public void cableConnected(TaskGraphCableEvent event) {
        final TaskGraphCableEvent evt = event;
        final JPanel panel = this;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Cable cable = evt.getCable();

                if (cable.isConnected()) {
                    TaskComponent sendtask = getTaskComponent(cable.getSendingTask());
                    TaskComponent rectask = getTaskComponent(cable.getReceivingTask());

                    if ((sendtask != null) && (rectask != null)) {
                        String type = getCableType(cable);

                        NodeComponent sendnode = sendtask.getNodeComponent(cable.getSendingNode());
                        NodeComponent recnode = rectask.getNodeComponent(cable.getReceivingNode());

                        drawingCables.put(cable,
                                CableFactory.createDrawCable(cable, sendnode.getComponent(), recnode.getComponent(),
                                        panel));
                        repaint();
                    }
                }
            }
        });
    }

    /**
     * Called before a connection between two tasks is removed.
     */
    public void cableDisconnected(TaskGraphCableEvent event) {
        if (drawingCables.containsKey(event.getCable())) {
            drawingCables.remove(event.getCable());
            repaint();
        }
    }

    /**
     * Called when a connection is reconnected to a different task.
     */
    public void cableReconnected(TaskGraphCableEvent event) {
        if (drawingCables.containsKey(event.getCable())) {
            drawingCables.remove(event.getCable());
        }

        cableConnected(event);
    }

    /**
     * Called when the control task is connected/disconnected or unstable
     */
    public void controlTaskStateChanged(ControlTaskStateEvent event) {
    }


    /**
     * Called when the core properties of a task change i.e. its name, whether it is running continuously etc.
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
        String paramname = event.getParameterName();

        if (paramname.startsWith(Task.OUTPUT_TYPE)) {
            repaint();
        }
    }


    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
        if (event.getTask() == taskgraph) {
            showmonitors.put(event.getNode(), new ForShowMonitor(event.getNode(), this));
        }
    }

    /**
     * Called before a data input node is removed.
     */
    public void nodeRemoved(TaskNodeEvent event) {
        if (event.getTask() == taskgraph) {
            if (showmonitors.containsKey(event.getNode())) {
                ((ForShowMonitor) showmonitors.get(event.getNode())).dispose();
            }

            showmonitors.remove(event.getNode());
        }
    }

    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
    }


    /**
     * Called when a node is connected to a cable.
     */
    public void nodeConnected(NodeEvent event) {
        if (event.getNode().getTask() == taskgraph) {
            event.getNode().getCable().getSendingTask().addTaskListener(this);
            repaint();
        }
    }

    /**
     * Called before a node is diconnected from a cable.
     */
    public void nodeDisconnected(NodeEvent event) {
        if (event.getNode().getTask() == taskgraph) {
            Cable cable = event.getNode().getCable();

            if ((cable != null) && (cable.getSendingTask() != taskgraph)) {
                cable.getSendingTask().removeTaskListener(this);
            }

            repaint();
        }
    }

    /**
     * Called when the name of the parameter the node is inputting/outputting is set.
     */
    public void parameterNameSet(NodeEvent event) {
    }

    /**
     * Called when one of a group node's parents changes
     */
    public void nodeParentChanged(NodeEvent event) {
    }

    /**
     * Called when one of a group node's child changes
     */
    public void nodeChildChanged(NodeEvent event) {
    }


    public void dispose() {
        Enumeration enumeration = showmonitors.elements();
        while (enumeration.hasMoreElements()) {
            ((ForShowMonitor) enumeration.nextElement()).dispose();
        }

        showmonitors.clear();
        showcables.clear();

        Task[] tasks = taskgraph.getTasks(true);
        TaskComponent comp;

        for (int count = 0; count < tasks.length; count++) {
            tasks[count].removeTaskListener(this);

            comp = getTaskComponent(tasks[count]);

            if (comp != null) {
                remove(comp.getComponent());
                tooltable.remove(tasks[count]);
                comp.dispose();
            }
        }

        taskgraph.removeTaskGraphListener(this);
        taskgraph.removeTaskListener(this);
    }


}

