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


import java.awt.Container;
import java.awt.Window;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskDisposedEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;

/**
 * A base panel that provides methods for updating task parameters.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public abstract class ParameterPanel extends JPanel
        implements WindowButtonConstants, TaskListener {

    /**
     * the task which this unit panel is being created for.
     */
    private Task task;

    /**
     * the menu bar for this parameter panel
     */
    private JMenuBar menubar;

    /**
     * a hashtable of parameter changes that are not yet commited. These changes are committed by seleting apply or ok
     * on the window.
     */
    private Hashtable params = new Hashtable();

    /**
     * a flag indicating whether parameter changes are automatically committed
     */
    private ParameterWindowInterface window;


    /**
     * Creates a Triana parameter panel associated with the specified task.
     */
    public ParameterPanel() {
        super();
    }


    public void setTask(Task task) {
        if (this.task != null) {
            throw (new RuntimeException("Error: Task for " + getClass().getName() + " already set"));
        }

        this.task = task;
        task.addTaskListener(this);
    }


    /**
     * @return the Task associated with this ParameterPanel
     */
    public Task getTask() {
        return task;
    }

    /**
     * Sets a paremeter in the associated task to the specified value.
     * <p/>
     * If the panel is attached to a group then parameter names of the form taskname.paramname or
     * groupname.taskname.paramname will set the parameter within the specified sub task. If the panel isn't attached to
     * a group then the groupname.taskname section is ignored.
     *
     * @param name  the name of the parameter to be set
     * @param value the value the parameter is set to
     */
    public void setParameter(String name, Object value) {
        if (name != null && value != null) {
            params.put(name, value);

            if (isAutoCommit()) {
                getTask(name).setParameter(getParameterName(name), value);
            }
        }
    }

    /**
     * Removes a paremeter in the associated task.
     *
     * @param name the name of the parameter to be removed
     */
    public void removeParameter(String name) {
        params.put(name, new NullToken());

        if (isAutoCommit()) {
            getTask(name).removeParameter(getParameterName(name));
        }
    }

    /**
     * Returns the value of a parameter in the associated task
     *
     * @param name the name of the parameter to return
     * @return the parameter value
     */
    public Object getParameter(String name) {
        Task task = getTask(name);

        if (task != null) {
            return task.getParameter(getParameterName(name));
        } else {
            return null;
        }
    }

    /**
     * Returns true if a value is set for a parameter in the associated task
     *
     * @param name the name of the parameter
     * @return true if a value is set for the parameter
     */
    public boolean isParameterName(String name) {
        Task task = getTask(name);

        if (task != null) {
            return task.isParameterName(getParameterName(name));
        } else {
            return false;
        }
    }


    /**
     * This method returns WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS by default. It should be overridden if the
     * panel has different preferred set of buttons.
     *
     * @return the panels preferred button combination (as defined in Windows Constants).
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS;
    }

    /**
     * This method returns true by default. It should be overridden if the panel prefers to be allowed to be hidden
     * behind the main triana window.
     *
     * @return true by default
     */
    public boolean isAlwaysOnTopPreferred() {
        return true;
    }

    /**
     * This method returns true by default. It should be overridden if the panel does not want the user to be able to
     * change the auto commit state
     */
    public boolean isAutoCommitVisible() {
        return true;
    }

    /**
     * This method returns false by default. It should be overridden if the panel wants parameter changes to be commited
     * automatically
     */
    public boolean isAutoCommitByDefault() {
        return false;
    }


    /**
     * Sets the component that determines whether parameter changes are committed automatically
     */
    public void setWindowInterface(ParameterWindowInterface comp) {
        window = comp;
    }

    /**
     * @return the component that determines whether parameter changes are committed automatically
     */
    public ParameterWindowInterface getWindowInterface() {
        return window;
    }

    /**
     * @return true if parameters are automatically committed
     */
    public boolean isAutoCommit() {
        if (window != null) {
            return window.isAutoCommit();
        } else {
            return isAutoCommitByDefault();
        }
    }


    /**
     * This method is called when the task is set for this panel. It is overridden to create the panel layout.
     */
    public abstract void init();

    /**
     * This method is called when the panel is reset or cancelled. It should reset all the panels components to the
     * values specified by the associated task, e.g. a component representing a parameter called "noise" should be set
     * to the value returned by a getTool().getParameter("noise") call.
     */
    public abstract void reset();

    /**
     * This method is called when the panel is finished with. It should dispose of any components (e.g. windows) used by
     * the panel.
     */
    public abstract void dispose();


    /**
     * Disposes of the parameter panel, calls dispose on the subclassing panel
     */
    public void disposePanel() {
        if (task != null) {
            task.removeTaskListener(this);
        }

        dispose();
    }


    /**
     * This method is called when a parameter in the associated task is updated. It should be overridden to update the
     * GUI in response to the parameter update
     */
    public void parameterUpdate(String paramname, Object value) {
    }


    /**
     * Called when the ok button is clicked on the parameter window. Calls applyClicked by default to commit any
     * parameter changes.
     */
    public void okClicked() {
        commitParameterChanges();
        params.clear();
    }

    /**
     * Called when the cancel button is clicked on the parameter window. Parameter changes are not commited.
     */
    public void cancelClicked() {
        reset();
        params.clear();
    }

    /**
     * Called when the apply button is clicked on the parameter window. Commits any parameter changes.
     */
    public void applyClicked() {
        commitParameterChanges();
        params.clear();
    }


    /**
     * Commits the any parameter changes to the task.
     */
    void commitParameterChanges() {
        if (getTask() != null) {
            Enumeration enumeration = params.keys();
            String key;
            Task task;
            String paramname;

            while (enumeration.hasMoreElements()) {
                key = (String) enumeration.nextElement();
                Object value = params.get(key);

                task = getTask(key);
                paramname = getParameterName(key);

                if (value instanceof NullToken) {
                    task.removeParameter(paramname);
                } else {
                    task.setParameter(paramname, value);
                }
            }
        }
    }

    /**
     * Turns a key in the form groupname.taskname.paramname into a parameter name
     */
    private String getParameterName(String key) {
        String[] parts = key.split("\\.");

        return parts[parts.length - 1];
    }

    /**
     * Turns a key in the form groupname.taskname.paramname into a task interface
     */
    private Task getTask(String key) {
        String[] parts = key.split("\\.");
        Task task = getTask();

        if (!(task instanceof TaskGraph)) {
            return task;
        }

        if (parts.length == 1) {
            return task;
        } else {
            for (int count = 0; count < parts.length - 1; count++) {
                if (!(task instanceof TaskGraph)) {
                    throw (new RuntimeException(
                            "Invalid parameter key: " + key + " (" + task.getToolName() + " not a group task)"));
                } else if (((TaskGraph) task).getTask(parts[count]) == null) {
                    throw (new RuntimeException(
                            "Invalid parameter key: " + key + " (" + task.getToolName() + " not found in group)"));
                } else {
                    task = ((TaskGraph) task).getTask(parts[count]);
                }
            }
        }

        return task;
    }


    /**
     * Convinence method that returns the window the panel is in.
     */
    protected Window getWindow() {
        Container parent = getParent();

        while ((parent != null) && (!(parent instanceof Window))) {
            parent = parent.getParent();
        }

        if (parent != null) {
            return (Window) parent;
        } else {
            return null;
        }
    }

    /**
     * @return the menu bar for this panel (null if none)
     */
    public JMenuBar getMenuBar() {
        return menubar;
    }

    /**
     * Sets the menu bar for this component
     */
    public void setMenuBar(JMenuBar menubar) {
        this.menubar = menubar;
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
        if (event.getTask() == this.task) {
            final String paramname = event.getParameterName();
            final Object value = event.getNewValue();

            if (SwingUtilities.isEventDispatchThread()) {
                parameterUpdate(paramname, value);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        parameterUpdate(paramname, value);
                    }
                });
            }
        }
    }

    /**
     * Called before a data input node is removed.
     */
    public void nodeRemoved(TaskNodeEvent event) {
    }

    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
    }

    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
    }


    private class NullToken {
    }

}







