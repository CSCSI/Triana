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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.trianacode.gui.components.triana.OpenGroupComponentModel;
import org.trianacode.gui.extensions.ExtensionManager;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.RenderingHint;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.service.TrianaClient;
import org.trianacode.taskgraph.tool.Tool;

/**
 * A ToolComponentModel maintains a list to ToolComponentModels, each of which controls how a particular tool appears in
 * the tool tree/workspace.
 */

public class TaskGraphView {

    /**
     * The name of the mode
     */
    private String name;

    /**
     * A hashtable of the ToolComponentModels keyed by tool class
     */
    private Hashtable toolmodels = new Hashtable();

    /**
     * A hashtable of the OpenGroupComponentModels keyed by tool class
     */
    private Hashtable groupmodels = new Hashtable();

    /**
     * A cache of the tool item model for each tool
     */
    private Hashtable toolcache = new Hashtable();

    /**
     * A cache of the open group model for each taskgraph
     */
    private Hashtable groupcache = new Hashtable();

    /**
     * A list of the classes in order of registration
     */
    private ArrayList classorder = new ArrayList();
    private ArrayList groupclassorder = new ArrayList();

    /**
     * The parent tool component mode (null if root)
     */
    private TaskGraphView parentmode;

    /**
     * The default tool component model
     */
    private ToolComponentModel defaulttool;

    /**
     * The default open group component model
     */
    private OpenGroupComponentModel defaultgroup;


    /**
     * Constructs a root tool component mode
     *
     * @param name the name of the mode
     */
    public TaskGraphView(String name) {
        this.name = name;
    }

    /**
     * Constructs a child tool component mode
     *
     * @param name       the name of the mode
     * @param parentmode the parent tool component mode
     */
    public TaskGraphView(String name, TaskGraphView parentmode) {
        this.name = name;
        this.parentmode = parentmode;
    }


    /**
     * @return the name of this mode
     */
    public String getViewName() {
        return name;
    }


    /**
     * @return the parent tool mode (null if root)
     */
    private TaskGraphView getParentToolMode() {
        return parentmode;
    }


    /**
     * @return the default tool item model
     */
    public ToolComponentModel getDefaultToolModel() {
        return defaulttool;
    }

    /**
     * Sets the default tool item model
     */
    public void setDefaultToolModel(ToolComponentModel model) {
        defaulttool = model;
        toolcache.clear();
    }

    /**
     * @return the default tool item model
     */
    public OpenGroupComponentModel getDefaultOpenGroupModel() {
        return defaultgroup;
    }

    /**
     * Sets the default tool item model
     */
    public void setDefaultOpenGroupModel(OpenGroupComponentModel model) {
        defaultgroup = model;
        groupcache.clear();
    }


    /**
     * Registers a ToolComponentModel for a particular tool class. Note that if a tool has two component models then the
     * later registered takes precedence.
     */
    public void registerToolModel(String toolclass, ToolComponentModel model) {
        toolmodels.put(toolclass, model);
        classorder.remove(toolclass);
        classorder.add(toolclass);
        toolcache.clear();
    }

    /**
     * Unregisters the ToolComponentModel for a particular tool class
     */
    public void unregisterToolModel(String toolclass) {
        toolmodels.remove(toolclass);
        classorder.remove(toolclass);
        toolcache.clear();
    }


    /**
     * Registers a OpenGroupComponentModel for a particular tool class
     */
    public void registerOpenGroupModel(String toolclass, OpenGroupComponentModel model) {
        groupmodels.put(toolclass, model);
        groupclassorder.remove(toolclass);
        groupclassorder.add(toolclass);
        groupcache.clear();
    }

    /**
     * Unregisters the OpenGroupComponentModel for a particular tool class
     */
    public void unregisterOpenGroupModel(String toolclass) {
        groupmodels.remove(toolclass);
        groupclassorder.remove(toolclass);
        groupcache.clear();
    }


    /**
     * @return the tree icon for the specified tool (if null is returned then the default leaf icon is used)
     */
    public Icon getTreeIcon(Tool tool) {
        ToolComponentModel model = getToolComponentModel(tool);
        Icon icon = null;

        if (model != null) {
            icon = model.getTreeIcon(tool);
        }

        if ((icon == null) && (getDefaultToolModel() != null)) {
            icon = getDefaultToolModel().getTreeIcon(tool);
        }

        if (icon != null) {
            return icon;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().getTreeIcon(tool);
        } else {
            return null;
        }
    }


    /**
     * @return the tool tip for the specified tool when in the tree
     */
    public String getTreeToolTip(Tool tool, boolean extended) {
        ToolComponentModel model = getToolComponentModel(tool);
        String tip = null;

        if (model != null) {
            tip = model.getTreeToolTip(tool, extended);
        }

        if ((tip == null) && (getDefaultToolModel() != null)) {
            tip = getDefaultToolModel().getTreeToolTip(tool, extended);
        }

        if (tip != null) {
            return tip;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().getTreeToolTip(tool, extended);
        } else {
            throw (new RuntimeException(
                    "Tool Component Model not set for " + tool.getToolName() + " in " + getViewName()));
        }
    }

    /**
     * @return the right-click popup for the specified tool when in the tree
     */
    public JPopupMenu getTreePopup(Tool tool) {
        ToolComponentModel model = getToolComponentModel(tool);
        JPopupMenu menu = null;

        if (model != null) {
            menu = model.getTreePopup(tool);
        }

        if ((menu == null) && (getDefaultToolModel() != null)) {
            menu = getDefaultToolModel().getTreePopup(tool);
        }

        if (menu != null) {
            addTreeExtensions(menu, tool);
            return menu;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().getTreePopup(tool);
        } else {
            throw (new RuntimeException(
                    "Tool Component Model not set for " + tool.getToolName() + " in " + getViewName()));
        }
    }

    /**
     * Adds a tree extensions menu dor the specified tool
     */
    private void addTreeExtensions(JPopupMenu menu, Tool tool) {
        Action[] actions = ExtensionManager.getTreeExtensions(tool);

        if (actions.length > 0) {
            final JMenu extmenu = new JMenu("Extensions");

            for (int count = 0; count < actions.length; count++) {
                extmenu.add(actions[count]);
            }

            menu.addSeparator();
            menu.add(extmenu);
        }
    }


    /**
     * @return the tool tip for the specified task when on the workspace
     */
    public String getWorkspaceToolTip(Task task, boolean extended) {
        ToolComponentModel model = getToolComponentModel(task);
        String tip = null;

        if (model != null) {
            tip = model.getWorkspaceToolTip(task, extended);
        }

        if ((tip == null) && (getDefaultToolModel() != null)) {
            tip = getDefaultToolModel().getWorkspaceToolTip(task, extended);
        }

        if (tip != null) {
            return tip;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().getWorkspaceToolTip(task, extended);
        } else {
            throw (new RuntimeException(
                    "Tool Component Model not set for " + task.getToolName() + " in " + getViewName()));
        }
    }

    /**
     * @return the right-click popup for the specified task when on the workspace
     */
    public JPopupMenu getWorkspacePopup(Task task) {
        ToolComponentModel model = getToolComponentModel(task);
        JPopupMenu menu = null;

        if (model != null) {
            menu = model.getWorkspacePopup(task);
        }

        if ((menu == null) && (getDefaultToolModel() != null)) {
            menu = getDefaultToolModel().getWorkspacePopup(task);
        }

        if (menu != null) {
            addWorkspaceExtensions(menu, task);
            return menu;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().getWorkspacePopup(task);
        } else {
            throw (new RuntimeException(
                    "Tool Component Model not set for " + task.getToolName() + " in " + getViewName()));
        }
    }

    /**
     * Adds a workspace extensions menu for the specified tool
     */
    private void addWorkspaceExtensions(final JPopupMenu menu, Task task) {
        Action[] actions = ExtensionManager.getWorkspaceExtensions(task);

        if (actions.length > 0) {
            JMenu extmenu = new JMenu("Extensions");

            for (int count = 0; count < actions.length; count++) {
                extmenu.add(actions[count]);
            }

            menu.addSeparator();
            menu.add(extmenu);

            Component separator = menu.getComponent(menu.getComponentCount() - 2);

            menu.addPopupMenuListener(new RemoveExtensionMenuListener(menu, extmenu, separator));
        }
    }


    /**
     * @return the right-click popup menu for an open group (right-click on workspace background)
     */
    public JPopupMenu getOpenGroupPopup(TaskGraph taskgraph) {
        OpenGroupComponentModel model = getOpenGroupComponentModel(taskgraph);
        JPopupMenu menu = null;

        if (model != null) {
            menu = model.getOpenGroupPopup(taskgraph);
        }

        if ((menu == null) && (getDefaultOpenGroupModel() != null)) {
            menu = getDefaultOpenGroupModel().getOpenGroupPopup(taskgraph);
        }

        if (menu != null) {
            return menu;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().getOpenGroupPopup(taskgraph);
        } else {
            throw (new RuntimeException(
                    "Open Group Component Model not set for " + taskgraph + " in " + getViewName()));
        }
    }

    /**
     * @return the right-click popup menu for an multiple selected tasks.
     */
    public JPopupMenu getMultipleSelectionPopup(TaskGraph taskgraph, Task[] tasks) {
        OpenGroupComponentModel model = getOpenGroupComponentModel(taskgraph);
        JPopupMenu menu = null;

        if (model != null) {
            menu = model.getMultipleSelectedPopup(tasks);
        }

        if ((menu == null) && (getDefaultOpenGroupModel() != null)) {
            menu = getDefaultOpenGroupModel().getMultipleSelectedPopup(tasks);
        }

        if (menu != null) {
            return menu;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().getMultipleSelectionPopup(taskgraph, tasks);
        } else {
            throw (new RuntimeException(
                    "Open Group Component Model not set for " + taskgraph + " in " + getViewName()));
        }
    }


    /**
     * @return the action that is invoked when the task is activated (e.g. double-clicked).
     */
    public Action getTaskAction(Task task) {
        ToolComponentModel model = getToolComponentModel(task);
        Action action = null;

        if (model != null) {
            action = model.getTaskAction(task);
        }

        if ((action == null) && (getDefaultToolModel() != null)) {
            action = getDefaultToolModel().getTaskAction(task);
        }

        if (action != null) {
            return action;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().getTaskAction(task);
        } else {
            throw (new RuntimeException(
                    "Tool Component Model not set for " + task.getToolName() + " in " + getViewName()));
        }
    }

    /**
     * @return the task component used to represent the specified task
     */
    public TaskComponent getTaskComponent(Task task) {
        ToolComponentModel model = getToolComponentModel(task);
        TaskComponent comp = null;

        if (model != null) {
            comp = model.getTaskComponent(task);
        }

        if ((comp == null) && (getDefaultToolModel() != null)) {
            comp = getDefaultToolModel().getTaskComponent(task);
        }

        if (comp != null) {
            return comp;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().getTaskComponent(task);
        } else {
            throw (new RuntimeException(
                    "Tool Component Model not set for " + task.getToolName() + " in " + getViewName()));
        }
    }


    /**
     * @param action the update action (e.g. INCREASE_INPUT_NODES_ACTION as defined in UpdateActionConstants)
     * @return true if the update action icon should be shown for the specified action
     */
    public boolean isUpdateIcon(Task task, String action) {
        ToolComponentModel model = getToolComponentModel(task);
        int isicon = ToolComponentModel.UNKNOWN_ACTION;

        if (model != null) {
            isicon = model.isUpdateIcon(task, action);
        }

        if ((isicon == ToolComponentModel.UNKNOWN_ACTION) && (getDefaultToolModel() != null)) {
            isicon = getDefaultToolModel().isUpdateIcon(task, action);
        }

        if (isicon == ToolComponentModel.DISPLAY_ICON) {
            return true;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().isUpdateIcon(task, action);
        } else {
            return false;
        }
    }

    /**
     * @param action the update action (e.g. INCREASE_INPUT_NODES_ACTION as defined in UpdateActionConstants)
     * @return the action associated with the specified update action.
     */
    public Action getUpdateAction(Task task, String action) {
        ToolComponentModel model = getToolComponentModel(task);
        Action act = null;

        if (model != null) {
            act = model.getUpdateAction(task, action);
        }

        if ((act == null) && (getDefaultToolModel() != null)) {
            act = getDefaultToolModel().getUpdateAction(task, action);
        }

        if (act != null) {
            return act;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().getUpdateAction(task, action);
        } else {
            return null;
        }
    }


    /**
     * The task component used to represent the specified task
     */
    public TaskGraphPanel getTaskGraphPanel(TaskGraph taskgraph, TrianaClient client) {
        OpenGroupComponentModel model = getOpenGroupComponentModel(taskgraph);
        TaskGraphPanel comp = null;

        if (model != null) {
            comp = model.getOpenGroupComponent(taskgraph, client);
        }

        if ((comp == null) && (getDefaultOpenGroupModel() != null)) {
            comp = getDefaultOpenGroupModel().getOpenGroupComponent(taskgraph, client);
        }

        if (comp != null) {
            return comp;
        } else if (getParentToolMode() != null) {
            return getParentToolMode().getTaskGraphPanel(taskgraph, client);
        } else {
            throw (new RuntimeException(
                    "Open Group Component Model not set for " + taskgraph + " in " + getViewName()));
        }
    }


    /**
     * @return the tool component model applicable to the specfied tool
     */
    private ToolComponentModel getToolComponentModel(Tool tool) {
        ToolComponentModel model;

        if (toolcache.containsKey(tool)) {
            model = (ToolComponentModel) toolcache.get(tool);
        } else {
            model = locateToolComponentModel(tool);

            if (model != null) {
                toolcache.put(tool, model);
            }
        }

        return model;
    }

    /**
     * @return the open group component model applicable to the specfied taskgraph
     */
    private OpenGroupComponentModel getOpenGroupComponentModel(TaskGraph taskgraph) {
        OpenGroupComponentModel model;

        if (groupcache.containsKey(taskgraph)) {
            model = (OpenGroupComponentModel) groupcache.get(taskgraph);
        } else {
            model = locateOpenGroupComponentModel(taskgraph);

            if (model != null) {
                groupcache.put(taskgraph, model);
            }
        }
        return model;
    }


    /**
     * @return the tool component model used for the specified tool
     */
    private ToolComponentModel locateToolComponentModel(Tool tool) {
        RenderingHint[] hints = tool.getRenderingHints();
        String hint;

        ToolComponentModel model = null;
        int priority = -1;

        for (int count = 0; count < hints.length; count++) {
            hint = hints[count].getRenderingHint();

            if (toolmodels.containsKey(hint)) {
                if ((model == null) || (classorder.indexOf(hint) > priority)) {
                    model = (ToolComponentModel) toolmodels.get(hint);
                    priority = classorder.indexOf(hint);
                }
            }
        }

        if ((model == null) && (getDefaultToolModel() != null)) {
            model = getDefaultToolModel();
        }

        return model;
    }

    /**
     * @return the open group component model used for the specified taskgraph
     */
    private OpenGroupComponentModel locateOpenGroupComponentModel(TaskGraph taskgraph) {
        RenderingHint[] hints = taskgraph.getRenderingHints();
        String hint;
        OpenGroupComponentModel model = null;
        int priority = -1;

        for (int count = 0; count < hints.length; count++) {
            hint = hints[count].getRenderingHint();

            if (groupmodels.containsKey(hint)) {
                if ((model == null) || (groupclassorder.indexOf(hint) > priority)) {
                    model = (OpenGroupComponentModel) groupmodels.get(hint);
                    priority = groupclassorder.indexOf(hint);
                }
            }
        }

        if ((model == null) && (getDefaultOpenGroupModel() != null)) {
            model = getDefaultOpenGroupModel();
        }

        return model;
    }


    private class RemoveExtensionMenuListener implements PopupMenuListener {

        private JPopupMenu menu;
        private JMenu extmenu;
        private Component separator;


        public RemoveExtensionMenuListener(JPopupMenu menu, JMenu extmenu, Component separator) {
            this.menu = menu;
            this.extmenu = extmenu;
            this.separator = separator;
        }

        public void popupMenuCanceled(PopupMenuEvent e) {
        }

        public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
            final PopupMenuListener listener = this;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    menu.removePopupMenuListener(listener);
                    menu.remove(extmenu);
                    menu.remove(separator);
                }
            });
        }

        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

    }


}
