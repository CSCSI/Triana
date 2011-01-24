/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.gui.panels;

import java.awt.*;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskDisposedEvent;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class PanelHolder extends ParameterPanel {

    private JPanel panel;


    public PanelHolder(Component panel) {
        if (panel instanceof JPanel)
            this.panel = (JPanel)panel;
        else {
            this.panel = new JPanel();
            this.panel.add(panel);
        }
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, panel);
    }

    /**
     * the task which this unit panel is being created for.
     */
    private Task task;

    /**
     * a flag indicating whether parameter changes are automatically committed
     */
    private ParameterWindowInterface window;


    /**
     * Creates a Triana parameter panel associated with the specified task.
     */
    public PanelHolder() {
        super();
    }

    public JPanel getPanel() {
        return panel;
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

    }

    /**
     * Removes a paremeter in the associated task.
     *
     * @param name the name of the parameter to be removed
     */
    public void removeParameter(String name) {

    }

    /**
     * Returns the value of a parameter in the associated task
     *
     * @param name the name of the parameter to return
     * @return the parameter value
     */
    public Object getParameter(String name) {

        return null;
    }

    /**
     * Returns true if a value is set for a parameter in the associated task
     *
     * @param name the name of the parameter
     * @return true if a value is set for the parameter
     */
    public boolean isParameterName(String name) {

        return false;
    }


    /**
     * This method returns WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS by default. It should be overridden if the
     * panel has different preferred set of buttons.
     *
     * @return the panels preferred button combination (as defined in Windows Constants).
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_BUTTON;
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
        return false;
    }

    /**
     * This method returns false by default. It should be overridden if the panel wants parameter changes to be commited
     * automatically
     */
    public boolean isAutoCommitByDefault() {
        return true;
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
        return false;
    }


    /**
     * This method is called when the task is set for this panel. It is overridden to create the panel layout.
     */
    public void init() {
    }

    /**
     * This method is called when the panel is reset or cancelled. It should reset all the panels components to the
     * values specified by the associated task, e.g. a component representing a parameter called "noise" should be set
     * to the value returned by a getTool().getParameter("noise") call.
     */
    public void reset() {
    }

    /**
     * This method is called when the panel is finished with. It should dispose of any components (e.g. windows) used by
     * the panel.
     */
    public void dispose() {
    }


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

    }

    /**
     * Called when the cancel button is clicked on the parameter window. Parameter changes are not commited.
     */
    public void cancelClicked() {

    }

    /**
     * Called when the apply button is clicked on the parameter window. Commits any parameter changes.
     */
    public void applyClicked() {

    }


    /**
     * Commits the any parameter changes to the task.
     */
    void commitParameterChanges() {

    }

    /**
     * Turns a key in the form groupname.taskname.paramname into a parameter name
     */
    private String getParameterName(String key) {


        return "";
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
        return null;
    }

    /**
     * Sets the menu bar for this component
     */
    public void setMenuBar(JMenuBar menubar) {

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


}
