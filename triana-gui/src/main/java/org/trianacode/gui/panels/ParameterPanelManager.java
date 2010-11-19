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

package org.trianacode.gui.panels;

import org.trianacode.gui.Display;
import org.trianacode.gui.builder.GUICreaterPanel;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.util.Env;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.event.*;
import org.trianacode.taskgraph.service.TrianaClient;
import org.trianacode.taskgraph.tool.ClassLoaders;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The parameter panel manager keeps a hashtable of parameter panels for each task. It monitors all open taskgraphs, and
 * if a task is created with its paramPanelInstantiate parameter set to true, the parameter panel for that task is
 * immediately instantiated. If a parameter panel is not immediately instantiated then it is instantiated when it is
 * first accessed.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class ParameterPanelManager implements TaskGraphListener, TaskListener {

    private static ParameterPanelManager disposemanager = new ParameterPanelManager();

    /**
     * a hashtable of parameter panels keyed task
     */
    private static Hashtable paneltable = new Hashtable();
    /**
     * A hashtable of the open windows, keyed by task
     */
    private static Hashtable windowtable = new Hashtable();
    /**
     * A hashtable of tasks, keyed by their window
     */
    private static Hashtable tasktable = new Hashtable();

    /**
     * Notifies the parameter panel manager to monitor the specified taskgraph
     */
    public void monitorTaskGraph(TaskGraph taskgraph) {
        handleTaskCreated(taskgraph);
        taskgraph.addTaskGraphListener(this);
    }

    /**
     * Notifies the parameter panel manager to stop monitoring the specified taskgraph
     */
    public void unmonitorTaskGraph(TaskGraph taskgraph) {
        taskgraph.removeTaskGraphListener(this);
    }


    /**
     * @return the parameter panel for the specified task (or null if none)
     */
    public static ParameterPanel getParameterPanel(Task task) {
        instantiatePanel(task);

        if (paneltable.containsKey(task)) {
            ParameterPanel paramPanel = (ParameterPanel) paneltable.get(task);

            if (isAlreadyInWindow(paramPanel)) {
                paramPanel = new MessagePanel(
                        "Properties for " + task.getToolName() + " already visible in existing window");
                paramPanel.setTask(task);
                paramPanel.init();
            }

            return paramPanel;
        } else if (task instanceof TaskGraph) {
            return initGroupPanel((TaskGraph) task);
        } else {
            return null;
        }
    }

    private static boolean isAlreadyInWindow(ParameterPanel paramPanel) {
        Container cont = paramPanel.getParent();

        while ((cont != null) && (!(cont instanceof Window))) {
            cont = cont.getParent();
        }

        return (cont instanceof Window);
    }

    /**
     * @return true if the specified task has a parameter panel
     */
    public static boolean isParameterPanel(Task task) {
        return (task.isParameterName(Tool.PARAM_PANEL_CLASS) ||
                task.isParameterName(Tool.GUI_BUILDER) ||
                task.isParameterName(Tool.OLD_GUI_BUILDER) ||
                (task instanceof TaskGraph));
    }

    /**
     * @return true if the panel for the class is instantiate on task creation
     */
    public static boolean isInstantiatePanel(Task task) {
        return (task.isParameterName(Tool.PARAM_PANEL_CLASS) &&
                ((!task.isParameterName(Tool.PARAM_PANEL_INSTANTIATE) ||
                        (task.getParameter(Tool.PARAM_PANEL_INSTANTIATE).equals(Tool.ON_TASK_INSTANTIATION)))));
    }


    private void handleTaskCreated(Task task) {
        if (isInstantiatePanel(task)) {
            instantiatePanel(task);
        }

        if (task instanceof TaskGraph) {
            Task[] tasks = ((TaskGraph) task).getTasks(true);

            for (int count = 0; count < tasks.length; count++) {
                handleTaskCreated(tasks[count]);
            }
        }
    }

    private static void instantiatePanel(Task task) {
        if (!paneltable.containsKey(task)) {
            ParameterPanel paramPanel = null;

            if (task.isParameterName(Tool.GUI_BUILDER)) {
                paramPanel = initGUIBuilderV2Panel((String) task.getParameter(Tool.GUI_BUILDER), task);
            } else if (task.isParameterName(Tool.PARAM_PANEL_CLASS)) {
                paramPanel = initParameterPanel(task);
            } else if (task.isParameterName(Tool.OLD_GUI_BUILDER)) {
                throw (new RuntimeException("Deprecated GUI Builder information in " + task.getToolName()
                        + ": Tool XML must be be regenrated"));
            }

            if (paramPanel != null) {
                paneltable.put(task, paramPanel);
                task.addTaskListener(disposemanager);
            }
        }
    }

    public static void registerPanel(JPanel panel, Task task) {
        if (!paneltable.containsKey(task)) {
            ParameterPanel paramPanel = null;
            if (panel instanceof ParameterPanel) {
                paramPanel = (ParameterPanel) panel;
            } else {
                paramPanel = new PanelHolder(panel);
            }
            paneltable.put(task, paramPanel);
            task.addTaskListener(disposemanager);
        }
    }

    /**
     * Initialises a parameter panel containing sub-parameter panels for all tasks in the group.
     */
    private static ParameterPanel initGroupPanel(TaskGraph taskgraph) {
        Task[] tasks = taskgraph.getTasks(true);
        ParameterPanel paramPanel;
        ArrayList panels = new ArrayList();

        for (int count = 0; count < tasks.length; count++) {
            paramPanel = getParameterPanel(tasks[count]);

            if (paramPanel != null) {
                panels.add(paramPanel);
            }
        }

        ParameterPanel groupPanel = new GroupParameterPanel(
                (ParameterPanel[]) panels.toArray(new ParameterPanel[panels.size()]));
        groupPanel.setTask(taskgraph);
        groupPanel.init();

        return groupPanel;
    }

    /**
     * Initializes a GUI Builder V2 parameter panel for the task.
     */
    protected static ParameterPanel initGUIBuilderV2Panel(String fullline, Task task) {
        Vector<String> guilines = GUICreaterPanel.splitLine(fullline);

        ParameterPanel paramPanel = new GUICreaterPanel(guilines);
        paramPanel.setTask(task);
        paramPanel.init();

        GUICreaterPanel panel = ((GUICreaterPanel) paramPanel);
        for (int i = 0; i < panel.getRows(); ++i) {
            Object rowValue = task.getParameter(panel.getRow(i).getParameterName());
            if (rowValue != null) {
                panel.getRow(i).setValue(rowValue.toString());
            } else {
                String msg = "Error generating panel for: " + task.getToolName() + "\n"
                        + "Unit: " + task.getProxy().toString() + "\n"
                        + "Unit does not have a corresponding parameter for the GUI element: "
                        + panel.getRow(i).getParameterName() + "\n"
                        + "recompile/regenerate unit may fix";
                new ErrorDialog("GUI Builder Error", msg);
            }
        }
        return paramPanel;
    }

    /**
     * Initializes a parameter panel interface for the task.
     */
    protected static ParameterPanel initParameterPanel(Task task) {
        ParameterPanel paramPanel = null;
        try {
            TrianaClient client = GUIEnv.getTrianaClientFor(task);
            //TODO - removed necessity for TrianaClient. Not sure why this would be needed
            //if (client != null) {

            paramPanel = createPanel(task);
            paramPanel.setTask(task);
            paramPanel.init();
            //}
        }
        catch (ClassNotFoundException except) {
            new ErrorDialog(Env.getString("panelNotFoundError"),
                    Env.getString("panelNotFoundError") + ": " + task.getParameter(Tool.PARAM_PANEL_CLASS));
        }
        catch (Exception except) {
            new ErrorDialog(Env.getString("panelInstantiationError"),
                    Env.getString("panelInstantiationError") + ": " + task.getParameter(Tool.PARAM_PANEL_CLASS));
            except.printStackTrace();
        }
        return paramPanel;
    }


    /**
     * Attempt to find and load the ParameterPanel specified by this classname and task.
     */
    public static ParameterPanel createPanel(Task task) throws Exception {
        if (!task.isParameterName(Tool.PARAM_PANEL_CLASS)) {
            throw (new Exception("Error Instantiating Parameter Panel For " + task.getToolName()
                    + " : Parameter panel class not specified"));
        }

        String classname = (String) task.getParameter(Tool.PARAM_PANEL_CLASS);
        Class paramClass;
        try {
            paramClass = ClassLoaders.forName(classname);
        }
        catch (ClassNotFoundException e) {
            throw (new Exception(Env.getString("panelNotFoundError") + ": " + classname));
        }
        JPanel panel;
        try {
            panel = (JPanel) paramClass.newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw (new Exception(Env.getString("panelInstantiationError") + ": " + classname));
        }
        if (!(panel instanceof ParameterPanel)) {
            panel = new PanelHolder(panel);
        }
        return (ParameterPanel) panel;
    }


    /**
     * Called when a new task is created in a taskgraph.
     */
    public void taskCreated(TaskGraphTaskEvent event) {
        if (event.getTask() instanceof TaskGraph) {
            monitorTaskGraph((TaskGraph) event.getTask());
        } else {
            handleTaskCreated(event.getTask());
        }
    }


    /**
     * Called when a task is removed from a taskgraph. Note that this method is called when tasks are removed from a
     * taskgraph due to being grouped (they are place in the groups taskgraph).
     */
    public void taskRemoved(TaskGraphTaskEvent event) {
        if (event.getTask() instanceof TaskGraph) {
            unmonitorTaskGraph((TaskGraph) event.getTask());
        }
    }

    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
        if (paneltable.contains(event.getTask())) {
            ParameterPanel panel = (ParameterPanel) paneltable.remove(event.getTask());
            panel.disposePanel();
        }
    }


    /**
     * Called when a new connection is made between two tasks.
     */
    public void cableConnected(TaskGraphCableEvent event) {
    }

    /**
     * Called before a connection between two tasks is removed.
     */
    public void cableDisconnected(TaskGraphCableEvent event) {
    }

    /**
     * Called when a connection is reconnected to a different task.
     */
    public void cableReconnected(TaskGraphCableEvent event) {
    }

    /**
     * Called when the control task is connected/disconnected or unstable
     */
    public void controlTaskStateChanged(ControlTaskStateEvent event) {
    }


    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
    }

    /**
     * Called before a data input node is removed.
     */
    public void nodeRemoved(TaskNodeEvent event) {
    }

    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
    }

    /**
     * Called when the core properties of a task change i.e. its name, whether it is running continuously etc.
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    public static ParameterWindow showParameterWindowFor(Task task, Object source) {
        if (windowtable.containsKey(task)) {
            ParameterWindow window = (ParameterWindow) windowtable.get(task);

            if (window.isVisible()) {
                window.requestFocus();
            } else {
                window.setVisible(true);
            }

            return window;
        } else {
            ParameterPanel panel = getParameterPanel(task);

            if (panel != null) {
                ParameterWindow paramWindow;

                if (panel.isAlwaysOnTopPreferred()) {
                    paramWindow = new ParameterWindow(GUIEnv.getApplicationFrame(), panel.getPreferredButtons(), false);
                } else {
                    paramWindow = new ParameterWindow(panel.getPreferredButtons());
                }

                windowtable.put(task, paramWindow);
                tasktable.put(paramWindow, task);

                paramWindow.addWindowListener(new ParamWindowListener());
                paramWindow.setAutoDispose(false);
                paramWindow.setTitle(task.getToolName());
                paramWindow.setParameterPanel(panel);

                Point loc = Display.getAnchorPoint(source, paramWindow);
                loc.translate(140, 40);

                paramWindow.setLocation(loc);
                paramWindow.show();

                return paramWindow;
            }

        }

        return null;
    }

    public static void hideParameterWindowFor(Task task) {
        if (windowtable.containsKey(task)) {
            ParameterWindow window = (ParameterWindow) windowtable.get(task);

            window.setVisible(false);
            window.closeWindow();
        }
    }


    private static class MessagePanel extends ParameterPanelImp {

        public MessagePanel(String message) {
            JPanel messpanel = new JPanel(new BorderLayout());
            messpanel.setBorder(new EmptyBorder(3, 3, 3, 3));
            messpanel.add(new JLabel(message), BorderLayout.CENTER);

            add(messpanel);
        }

    }

    private static class ParamWindowListener implements WindowListener {

        /**
         * Invoked when a window has been closed as the result of calling dispose on the window.
         */
        public void windowClosed(WindowEvent event) {
            Task task = (Task) tasktable.remove(event.getWindow());

            if (task != null) {
                windowtable.remove(task);
            }
        }


        public void windowActivated(WindowEvent e) {
        }

        public void windowClosing(WindowEvent e) {
        }

        public void windowDeactivated(WindowEvent e) {
        }

        public void windowDeiconified(WindowEvent e) {
        }

        public void windowIconified(WindowEvent e) {
        }

        public void windowOpened(WindowEvent e) {

        }
    }
}
