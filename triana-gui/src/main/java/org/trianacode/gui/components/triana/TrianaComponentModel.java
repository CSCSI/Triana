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

package org.trianacode.gui.components.triana;

import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ActionTable;
import org.trianacode.gui.action.Actions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.action.taskgraph.ResolveNodesAction;
import org.trianacode.gui.action.tools.GroupEditorAction;
import org.trianacode.gui.action.tools.RunContinuouslyMenuItem;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.hci.tools.BrokenToolMonitor;
import org.trianacode.gui.hci.tools.ComponentSelectionHandler;
import org.trianacode.gui.hci.tools.ToolComponentModel;
import org.trianacode.gui.hci.tools.UpdateActionConstants;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.gui.main.imp.MainTriana;
import org.trianacode.gui.main.imp.MainTrianaTask;
import org.trianacode.gui.util.Env;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.imp.ToolTipWriter;
import org.trianacode.taskgraph.service.TrianaClient;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;

import javax.swing.*;

/**
 * The default ToolComponentModel for a task
 */

public class TrianaComponentModel implements
        ToolComponentModel, OpenGroupComponentModel {

    public ImageIcon GROUP_ICON = GUIEnv.getIcon("groupleaf.png");
    public ImageIcon BROKEN_ICON = GUIEnv.getIcon("brokenleaf.png");

    private ToolTable tools;

    private JPopupMenu toolWorkspacePopup;
    private JPopupMenu groupWorkspacePopup;

    private JPopupMenu toolTreePopup;
    private JPopupMenu groupTreePopup;

    private JPopupMenu openGroupPopup;
    private JPopupMenu multipleSelectedPopup;


    public TrianaComponentModel(ToolTable tools, ToolSelectionHandler workspace, ToolSelectionHandler tree) {
        this.tools = tools;

        initToolWorkspacePopup(workspace);
        initGroupWorkspacePopup(workspace);

        initToolTreePopup(tree);
        initGroupTreePopup();

        initOpenGroupPopup(workspace);
        initMultipleSelectedPopup();
    }

    /**
     * Initialises the menu that appears when the workspace is right-clicked on
     */
    private void initOpenGroupPopup(ToolSelectionHandler selhandler) {
        JMenuItem groupeditor = new JMenuItem(
                new GroupEditorAction(selhandler, tools, ActionDisplayOptions.DISPLAY_NAME));
        JMenuItem resolvenodes = new JMenuItem(new ResolveNodesAction(selhandler, ActionDisplayOptions.DISPLAY_NAME));
        JMenuItem save = new JMenuItem(ActionTable.getAction(Actions.SAVE_ACTION));
        JMenuItem selectall = new JMenuItem(ActionTable.getAction(Actions.SELECT_ALL_ACTION));
        JMenuItem paste = new JMenuItem(ActionTable.getAction(Actions.PASTE_ACTION));

        openGroupPopup = new JPopupMenu("Group Menu");
        openGroupPopup.add(groupeditor);
        openGroupPopup.add(resolvenodes);
        openGroupPopup.add(new JSeparator());
        openGroupPopup.add(save);
        openGroupPopup.add(new JSeparator());
        openGroupPopup.add(selectall);
        openGroupPopup.add(paste);
    }

    /**
     * Initialises the menu that appears when multiple selected tools in the workspace are right-clicked on
     */
    private void initMultipleSelectedPopup() {
        JMenuItem group = new JMenuItem(ActionTable.getAction(Actions.GROUP_ACTION));
        JMenuItem cut = new JMenuItem(ActionTable.getAction(Actions.CUT_ACTION));
        JMenuItem copy = new JMenuItem(ActionTable.getAction(Actions.COPY_ACTION));
        JMenuItem delete = new JMenuItem(ActionTable.getAction(Actions.DELETE_ACTION));
        JMenuItem pasteInto = new JMenuItem(ActionTable.getAction(Actions.PASTE_INTO_ACTION));
        JMenuItem compile = new JMenuItem(ActionTable.getAction(Actions.COMPILE_ACTION));


        multipleSelectedPopup = new JPopupMenu("Selected");
        multipleSelectedPopup.add(group);
        multipleSelectedPopup.add(new JSeparator());
        multipleSelectedPopup.add(cut);
        multipleSelectedPopup.add(copy);
        multipleSelectedPopup.add(pasteInto);
        multipleSelectedPopup.add(delete);
        multipleSelectedPopup.addSeparator();
        multipleSelectedPopup.add(compile);

    }


    /**
     * Initialises the menu that appears when a tool is right-clicked in the workspace
     */
    private void initToolWorkspacePopup(ToolSelectionHandler selhandler) {
        JMenuItem properties = new JMenuItem(ActionTable.getAction(Actions.PROPERTIES_ACTION));
        JMenuItem nodeeditor = new JMenuItem(ActionTable.getAction(Actions.NODE_EDITOR_ACTION));
        JMenuItem historytrack = new JMenuItem(ActionTable.getAction(Actions.HISTORY_TRACKING_ACTION));
        JMenuItem runcont = new RunContinuouslyMenuItem(selhandler);
        //JMenuItem publish = new JMenuItem(ActionTable.getAction(Actions.CREATE_SERVICE_ACTION));
        JMenuItem script = new JMenuItem(ActionTable.getAction(Actions.RUN_SCRIPT_ACTION));
        JMenuItem cut = new JMenuItem(ActionTable.getAction(Actions.CUT_ACTION));
        JMenuItem copy = new JMenuItem(ActionTable.getAction(Actions.COPY_ACTION));
        JMenuItem pasteInto = new JMenuItem(ActionTable.getAction(Actions.PASTE_INTO_ACTION));
        JMenuItem delete = new JMenuItem(ActionTable.getAction(Actions.DELETE_ACTION));
        JMenuItem rename = new JMenuItem(ActionTable.getAction(Actions.RENAME_ACTION));
        JMenuItem help = new JMenuItem(ActionTable.getAction(Actions.HELP_ACTION));

        toolWorkspacePopup = new JPopupMenu("Task Menu");
        toolWorkspacePopup.add(properties);
        toolWorkspacePopup.add(new JSeparator());
        toolWorkspacePopup.add(nodeeditor);
        toolWorkspacePopup.add(historytrack);
        toolWorkspacePopup.add(runcont);
        toolWorkspacePopup.add(new JSeparator());
        //toolWorkspacePopup.add(publish);
        toolWorkspacePopup.add(script);
        toolWorkspacePopup.add(new JSeparator());
        toolWorkspacePopup.add(cut);
        toolWorkspacePopup.add(copy);
        toolWorkspacePopup.add(delete);
        toolWorkspacePopup.add(pasteInto);
        toolWorkspacePopup.add(rename);
        toolWorkspacePopup.add(new JSeparator());
        toolWorkspacePopup.add(help);
    }

    /**
     * Initialises the menu that appears when a group is right-clicked in the workspace
     */
    private void initGroupWorkspacePopup(ToolSelectionHandler selhandler) {
        JMenuItem open = new JMenuItem(ActionTable.getAction(Actions.OPEN_ACTION));
        open.setText(Env.getString("ViewGroup"));
        JMenuItem properties = new JMenuItem(ActionTable.getAction(Actions.PROPERTIES_ACTION));
        JMenuItem control = new JMenuItem(ActionTable.getAction(Actions.CONTROL_PROERTIES_ACTION));
        //JMenuItem publish = new JMenuItem(ActionTable.getAction(Actions.CREATE_SERVICE_ACTION));
        JMenuItem script = new JMenuItem(ActionTable.getAction(Actions.RUN_SCRIPT_ACTION));
        JMenuItem ungroup = new JMenuItem(ActionTable.getAction(Actions.UNGROUP_ACTION));
        JMenuItem cut = new JMenuItem(ActionTable.getAction(Actions.CUT_ACTION));
        JMenuItem copy = new JMenuItem(ActionTable.getAction(Actions.COPY_ACTION));
        JMenuItem pasteInto = new JMenuItem(ActionTable.getAction(Actions.PASTE_INTO_ACTION));
        JMenuItem delete = new JMenuItem(ActionTable.getAction(Actions.DELETE_ACTION));

        JMenuItem rename = new JMenuItem(ActionTable.getAction(Actions.RENAME_ACTION));

        groupWorkspacePopup = new JPopupMenu("Group Menu");
        groupWorkspacePopup.add(open);
        groupWorkspacePopup.add(new JSeparator());
        groupWorkspacePopup.add(properties);
        groupWorkspacePopup.add(control);
        groupWorkspacePopup.add(ungroup);
        groupWorkspacePopup.add(new JSeparator());
        //groupWorkspacePopup.add(publish);
        groupWorkspacePopup.add(script);
        groupWorkspacePopup.add(new JSeparator());
        groupWorkspacePopup.add(cut);
        groupWorkspacePopup.add(copy);
        groupWorkspacePopup.add(pasteInto);
        groupWorkspacePopup.add(delete);
        groupWorkspacePopup.add(rename);
    }

    /**
     * Initialises the menu that appears when a tool is right-clicked in the tree
     */
    private void initToolTreePopup(ToolSelectionHandler selhandler) {
        JMenuItem cut = new JMenuItem(ActionTable.getAction(Actions.CUT_ACTION));
        JMenuItem copy = new JMenuItem(ActionTable.getAction(Actions.COPY_ACTION));
        JMenuItem delete = new JMenuItem(ActionTable.getAction(Actions.DELETE_ACTION));
        JMenuItem deleteRefs = new JMenuItem(ActionTable.getAction(Actions.DELETE_REFERENCES_ACTION));

        JMenuItem pasteInto = new JMenuItem(ActionTable.getAction(Actions.PASTE_INTO_ACTION));
        JMenuItem rename = new JMenuItem(ActionTable.getAction(Actions.RENAME_ACTION));
        //JMenuItem editdesc = new JMenuItem(ActionTable.getAction(Actions.EDIT_DESC_ACTION));
        //JMenuItem editgui = new JMenuItem(ActionTable.getAction(Actions.EDIT_GUI_ACTION));
        //JMenuItem editsource = new JMenuItem(ActionTable.getAction(Actions.EDIT_SOURCE_ACTION));
        //JMenuItem edithtml = new JMenuItem(ActionTable.getAction(Actions.EDIT_HTML_ACTION));
        //JMenuItem editxml = new JMenuItem(ActionTable.getAction(Actions.EDIT_XML_ACTION));
        JMenuItem compile = new JMenuItem(ActionTable.getAction(Actions.COMPILE_ACTION));
        JMenuItem help = new JMenuItem(ActionTable.getAction(Actions.HELP_ACTION));

        toolTreePopup = new JPopupMenu("Tool Menu");
        toolTreePopup.add(cut);
        toolTreePopup.add(copy);
        toolTreePopup.add(pasteInto);
        toolTreePopup.add(delete);
        toolTreePopup.add(deleteRefs);
        toolTreePopup.add(rename);
        toolTreePopup.addSeparator();
        //toolTreePopup.add(editdesc);
        //toolTreePopup.add(editgui);
        //toolTreePopup.add(editsource);
        //toolTreePopup.add(edithtml);
        //toolTreePopup.add(editxml);
        toolTreePopup.addSeparator();
        toolTreePopup.add(compile);
        toolTreePopup.addSeparator();
        toolTreePopup.add(help);
    }

    /**
     * Initialises the menu that appears when a group is right-clicked in the tree
     */
    private void initGroupTreePopup() {
        JMenuItem open = new JMenuItem(ActionTable.getAction(Actions.OPEN_ACTION));
        open.setText(Env.getString("OpenGroup"));
        JMenuItem cut = new JMenuItem(ActionTable.getAction(Actions.CUT_ACTION));
        JMenuItem copy = new JMenuItem(ActionTable.getAction(Actions.COPY_ACTION));
        JMenuItem delete = new JMenuItem(ActionTable.getAction(Actions.DELETE_ACTION));
        JMenuItem deleteRefs = new JMenuItem(ActionTable.getAction(Actions.DELETE_REFERENCES_ACTION));

        JMenuItem pasteInto = new JMenuItem(ActionTable.getAction(Actions.PASTE_INTO_ACTION));
        JMenuItem rename = new JMenuItem(ActionTable.getAction(Actions.RENAME_ACTION));
        //JMenuItem editdesc = new JMenuItem(ActionTable.getAction(Actions.EDIT_DESC_ACTION));
        //JMenuItem editgui = new JMenuItem(ActionTable.getAction(Actions.EDIT_GUI_ACTION));
        //JMenuItem edithtml = new JMenuItem(ActionTable.getAction(Actions.EDIT_HTML_ACTION));
        //JMenuItem editxml = new JMenuItem(ActionTable.getAction(Actions.EDIT_XML_ACTION));
        JMenuItem help = new JMenuItem(ActionTable.getAction(Actions.HELP_ACTION));

        groupTreePopup = new JPopupMenu("Tool Menu");
        groupTreePopup.add(open);
        groupTreePopup.addSeparator();
        groupTreePopup.add(cut);
        groupTreePopup.add(copy);
        groupTreePopup.add(pasteInto);
        groupTreePopup.add(delete);
        groupTreePopup.add(deleteRefs);
        groupTreePopup.add(rename);
        groupTreePopup.addSeparator();
        //groupTreePopup.add(editdesc);
        //groupTreePopup.add(editgui);
        //groupTreePopup.add(edithtml);
        //groupTreePopup.add(editxml);
        groupTreePopup.addSeparator();
        groupTreePopup.add(help);
    }


    /**
     * @return the icon for the specified tool (if null is returned then the default leaf icon is used)
     */
    public Icon getTreeIcon(Tool tool) {
        if (tool instanceof TaskGraph) {
            return GROUP_ICON;
        } else if (BrokenToolMonitor.isBroken(tool)) {
            return BROKEN_ICON;
        } else {
            return null;
        }
    }

    /**
     * @return the tool tip for the specified tool
     */
    public String getTreeToolTip(Tool tool, boolean extended) {
        return ToolTipWriter.getTreeTip(tool, extended);
    }

    /**
     * @return the popup menu the tool when in the tree
     */
    public JPopupMenu getTreePopup(Tool tool) {
        if (tool instanceof TaskGraph) {
            return groupTreePopup;
        } else {
            return toolTreePopup;
        }
    }


    /**
     * @return the tool tip for the tool when on the workspace
     */
    public String getWorkspaceToolTip(Task task, boolean extended) {
        return ToolTipWriter.getToolTip(task, extended);
    }

    /**
     * @return the popup menu the tool when in the workspace
     */
    public JPopupMenu getWorkspacePopup(Task task) {
        if (task instanceof TaskGraph) {
            return groupWorkspacePopup;
        } else {
            return toolWorkspacePopup;
        }
    }


    /**
     * @return the popup menu for the open group (if null is returned then the default popup menu is used, return a
     *         empty popup menu for no popup)
     */
    public JPopupMenu getOpenGroupPopup(TaskGraph task) {
        return openGroupPopup;
    }

    /**
     * @return the popup menu when multiple tasks are selected in the workspace (if null is returned then the default
     *         popup menu is used, return a empty popup menu for no popup)
     */
    public JPopupMenu getMultipleSelectedPopup(Task[] tasks) {
        return multipleSelectedPopup;
    }


    /**
     * @return the action that is invoked when the task is activated (e.g. double-clicked). If null is returned the
     *         default tool action is used; to do nothing return a DoNothingAction.
     */
    public Action getTaskAction(Task task) {
        return ActionTable.getAction(Actions.PROPERTIES_ACTION);
    }


    /**
     * @return the task component used to represent the specified task (if null is returned then the default component
     *         is used)
     */
    public TaskComponent getTaskComponent(Task task) {
        return new MainTrianaTask(task);
    }


    /**
     * Called to determine if an icon for the specified action should be displayed, e.g. ADD_INPUT_NODE_ACTION. Returns
     * whether the icon should be displayed (DISPLAY_ICON), should not be displayed (HIDE_ICON) or the action is unknown
     * and should be deffered to the default handler (UNKNOWN_ACTION). If the default handler does not know the action
     * then the icon is not displayed.
     *
     * @param action the update action (e.g. ADD_INPUT_NODE_ACTION)
     * @return either DISPLAY_ICON, HIDE_ICON or UNKNOWN_ACTION
     */
    public int isUpdateIcon(Task task, String action) {
        boolean display = false;

        if (action.equals(UpdateActionConstants.INCREASE_INPUT_NODES_ACTION)) {
            display = (task.getDataInputNodeCount() < task.getMaxDataInputNodes());
        } else if (action.equals(UpdateActionConstants.DECREASE_INPUT_NODES_ACTION)) {
            display = (task.getDataInputNodeCount() > task.getMinDataInputNodes());
        } else if (action.equals(UpdateActionConstants.INCREASE_OUTPUT_NODES_ACTION)) {
            display = (task.getDataOutputNodeCount() < task.getMaxDataOutputNodes());
        } else if (action.equals(UpdateActionConstants.DECREASE_OUTPUT_NODES_ACTION)) {
            display = (task.getDataOutputNodeCount() > task.getMinDataOutputNodes());
        } else {
            return UNKNOWN_ACTION;
        }

        if ((task instanceof TaskGraph) || (!GUIEnv.showNodeEditIcons())) {
            return HIDE_ICON;
        }

        if (display) {
            return DISPLAY_ICON;
        } else {
            return HIDE_ICON;
        }
    }

    /**
     * Called to determine the action that is invoked when an update action is choosen.
     *
     * @param action the update action (e.g. ADD_INPUT_NODE_ACTION)
     * @return either the action or null if unknown.
     */
    public Action getUpdateAction(Task task, String action) {
        if (action.equals(UpdateActionConstants.INCREASE_INPUT_NODES_ACTION)) {
            return ActionTable.getAction(Actions.INC_INPUT_NODES_ACTION);
        } else if (action.equals(UpdateActionConstants.DECREASE_INPUT_NODES_ACTION)) {
            return ActionTable.getAction(Actions.DEC_INPUT_NODES_ACTION);
        } else if (action.equals(UpdateActionConstants.INCREASE_OUTPUT_NODES_ACTION)) {
            return ActionTable.getAction(Actions.INC_OUTPUT_NODES_ACTION);
        } else if (action.equals(UpdateActionConstants.DECREASE_OUTPUT_NODES_ACTION)) {
            return ActionTable.getAction(Actions.DEC_OUTPUT_NODES_ACTION);
        }

        return null;
    }


    /**
     * @return the action that is invoked when a group is activated (e.g. the workspace is double-clicked). If null is
     *         returned the default workspace action is used.
     */
    public Action getOpenGroupAction(TaskGraph taskgraph) {
        return new GroupEditorAction(new ComponentSelectionHandler(taskgraph), tools,
                ActionDisplayOptions.DISPLAY_NAME);
    }

    /**
     * @return the taskgraph component used to represent the specified taskgraph (if null is returned then the default
     *         component is used)
     */
    public TaskGraphPanel getOpenGroupComponent(TaskGraph taskgraph, TrianaClient client) {
        return new MainTriana((TaskGraph) taskgraph, client);
    }


}
