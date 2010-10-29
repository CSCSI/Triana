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
package org.trianacode.gui.action.tools;

import org.trianacode.enactment.TrianaRun;
import org.trianacode.gui.Display;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.gui.panels.FormLayout;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.panels.ParameterPanelManager;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.constants.ScriptConstants;
import org.trianacode.taskgraph.service.NonRunnableClient;
import org.trianacode.taskgraph.service.SchedulerException;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolListener;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.Toolbox;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Action class to handle running Triana scripts.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public class RunScriptAction extends AbstractAction implements ActionDisplayOptions {

    private ToolSelectionHandler selectionHandler;
    private ToolTable tools;


    public RunScriptAction(ToolSelectionHandler selhandler, ToolTable tools) {
        this.selectionHandler = selhandler;
        this.tools = tools;

        putValue(SHORT_DESCRIPTION, Env.getString("RunScriptTip"));
        putValue(ACTION_COMMAND_KEY, Env.getString("runScript"));
        putValue(NAME, Env.getString("runScript") + "...");
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        if (selectionHandler.isSingleSelectedTool() && (selectionHandler.getSelectedTool() instanceof Task)) {
            handleRunScript((Task) selectionHandler.getSelectedTool(), e.getSource());
        }
    }

    /**
     * Handles running a script on the specified task. The script is choosen via a GUI.
     */
    public void handleRunScript(Task task, Object source) {
        RunScriptPanel panel = new RunScriptPanel((Task) task, source);
        panel.init();

        ParameterWindow scriptWindow = new ParameterWindow(GUIEnv.getApplicationFrame(), panel.getPreferredButtons(),
                true);
        scriptWindow.setTitle(Env.getString("runScript") + ": " + task.getToolName());
        scriptWindow.setParameterPanel(panel);

        Point loc = Display.getAnchorPoint(source, scriptWindow);
        loc.translate(140, 40);

        scriptWindow.setLocation(loc);
        scriptWindow.setVisible(true);
        scriptWindow.requestFocus();
    }


    /**
     * Handles running the specified script on the specified task
     */
    public static void runScript(Task task, TaskGraph script, ToolTable tools, boolean replace, boolean open,
                                 boolean view) {
        runScript(task, script, tools, replace, open, view, null);
    }

    /**
     * Handles running the specified script on the specified task, anchoring the script window to the specified source.
     */
    public static void runScript(final Task task, final TaskGraph script, final ToolTable tools,
                                 final boolean replace, final boolean open, final boolean view,
                                 final Object source) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    Object[] result = executeScript(task, script, tools, view, source);
                    handleScriptResult(result, task, script.getToolName(), replace, open);
                } catch (TaskGraphException except) {
                    ErrorDialog.show(GUIEnv.getApplicationFrame(), except);
                } catch (SchedulerException except) {
                    ErrorDialog.show(GUIEnv.getApplicationFrame(), except);
                }
            }
        };

        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    /**
     * Shows the parameter window for and then executes a script.
     *
     * @return the output from the script, or null if script execution is cancelled
     */
    private static Object[] executeScript(final Task task, final TaskGraph script,
                                          final ToolTable tools, final boolean view, final Object source)
            throws TaskGraphException, SchedulerException {
        GUIEnv.getApplicationFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        TrianaRun run = new TrianaRun(script);
        run.setDummyToolName("Script");

        if (view) {
            GUIEnv.getApplicationFrame()
                    .addChildTaskGraphPanel((TaskGraph) run.getTaskGraph(), new NonRunnableClient(tools));
        } else {
            GUIEnv.getApplicationFrame().registerTrianaClient(run.getTaskGraph(), new NonRunnableClient(tools));
        }

        boolean accepted = showParameterWindow(run.getTaskGraph(), source);

        Object[] result = new Object[run.getOutputNodeCount()];

        GUIEnv.getApplicationFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        if (accepted) {
            run.runTaskGraph();
            run.sendInputData(0, task);
        }

        while (!run.isFinished()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException except) {
            }

            receiveOutputData(run, result);

        }

        receiveOutputData(run, result);

        if (view) {
            TaskGraphPanel panel = GUIEnv.getTaskGraphPanelFor(run.getTaskGraph());
            if (panel != null) {
                GUIEnv.getApplicationFrame().closeTaskGraphPanel(panel);
            }
        } else {
            GUIEnv.getApplicationFrame().unregisterTrianaClient(run.getTaskGraph());
        }

        run.dispose();
        return result;
    }

    private static void receiveOutputData(TrianaRun run, Object[] result) {
        for (int count = 0; count < run.getOutputNodeCount(); count++) {
            if (run.isOutputReady(count)) {
                result[count] = run.receiveOutputData(count);
            }
        }
    }


    /**
     * Show the parameter window for the specified task, and wait until the panel is either accepted or rejected.
     *
     * @return true if the panel is accepted
     */
    private static boolean showParameterWindow(Task task, Object source) {
        ParameterPanel panel = ParameterPanelManager.getParameterPanel(task);

        if (panel != null) {
            ParameterWindow paramWindow = new ParameterWindow(GUIEnv.getApplicationFrame(),
                    WindowButtonConstants.OK_CANCEL_BUTTONS, true);
            paramWindow.setAutoDispose(true);
            paramWindow.setTitle(task.getToolName());
            paramWindow.setParameterPanel(panel);
            paramWindow.setAutoCommit(false);
            paramWindow.setAutoCommitVisible(false);

            Point loc = Display.getAnchorPoint(source, paramWindow);
            loc.translate(140, 40);

            paramWindow.setLocation(loc);
            paramWindow.setVisible(true);

            while (paramWindow.isVisible()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException except) {
                }
            }

            return paramWindow.isAccepted();
        } else {
            return true;
        }
    }


    /**
     * Handles the result from a script, such as replacing original task, opening output view and distributing
     * protoservices.
     */
    private static void handleScriptResult(Object[] result, final Task task, String scriptname,
                                           final boolean replace, final boolean open) throws TaskGraphException {
        if ((result != null) && (result.length > 0)) {
            Task newtask;
            int handleresult = 0;

            if (replace && (result[0] instanceof Tool)) {
                newtask = TaskLayoutUtils.replaceTask(task, (Tool) result[0], false);

                if ((open) && (newtask instanceof TaskGraph)) {
                    GUIEnv.getApplicationFrame()
                            .addChildTaskGraphPanel((TaskGraph) newtask, GUIEnv.getTrianaClientFor(newtask));
                } else if (open) {
                    JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(),
                            "Cannot display script output as it is not a TaskGraph",
                            Env.getString("runScript") + ": " + scriptname, JOptionPane.ERROR_MESSAGE,
                            GUIEnv.getTrianaIcon());
                }

                checkProtoService(newtask, scriptname);
                handleresult++;
            }

            if (open) {
                for (int count = handleresult; count < result.length; count++) {
                    if (result[count] instanceof TaskGraph) {
                        newtask = GUIEnv.getApplicationFrame().addParentTaskGraphPanel((TaskGraph) result[count]);
                        checkProtoService(newtask, scriptname);
                    }
                }
            }
        }
    }

    private static void checkProtoService(Task newtask, String scriptname) {
        /*if (ProtoServiceHandler.containsProtoService(newtask)) {
            String[] options = {"Distribute Now?", "Distribute Later?"};
            int result = JOptionPane.showOptionDialog(GUIEnv.getApplicationFrame(), "Script output (" + newtask.getToolName() + ") contains one or more ProtoServices. Would you like to...", Env.getString("runScript") + ": " + scriptname, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (result == 0)
                ProtoServiceHandler.distributeProtoServices(newtask);
        }*/
    }


    private class RunScriptPanel extends ParameterPanel implements ToolListener, TreeSelectionListener {

        private Task runtask;
        private Object source;

        private JTree scripttree = new JTree(new ScriptTreeModel());

        private JLabel name = new JLabel();
        private JLabel pack = new JLabel("                    ");
        private JTextArea desc = new JTextArea(2, 25);
        private JCheckBox replace;
        private JCheckBox open = new JCheckBox("Open script output window");
        private JCheckBox view = new JCheckBox("View script execution");

        private TaskGraph curselect;


        public RunScriptPanel(Task runtask) {
            this.runtask = runtask;
            this.replace = new JCheckBox("Replace " + runtask.getToolName() + " with script output ");
        }

        public RunScriptPanel(Task runtask, Object source) {
            this(runtask);
            this.source = source;
        }

        public boolean isAutoCommitVisible() {
            return false;
        }

        public byte getPreferredButtons() {
            return WindowButtonConstants.OK_CANCEL_BUTTONS;
        }


        /**
         * This method is called when the task is set for this panel. It is overridden to create the panel layout.
         */
        public void init() {
            setLayout(new BorderLayout(8, 0));

            populateScriptList();
            tools.addToolTableListener(this);

            JScrollPane scroll = new JScrollPane(scripttree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scripttree.setPreferredSize(new JLabel("01234567890123456789012345678901234567890").getPreferredSize());
            scripttree.setVisibleRowCount(10);
            scripttree.addTreeSelectionListener(this);
            scripttree.setRootVisible(true);
            scripttree.expandRow(0);

            JPanel infopanel = new JPanel(new FormLayout(3, 5));
            infopanel.add(new JLabel("Name:"));
            infopanel.add(name);
            infopanel.add(new JLabel("Package:"));
            infopanel.add(pack);

            JScrollPane dscroll = new JScrollPane(desc, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            desc.setLineWrap(true);
            desc.setWrapStyleWord(true);
            desc.setBackground(name.getBackground());
            dscroll.setBorder(new EmptyBorder(0, 0, 0, 0));

            JPanel descpanel = new JPanel(new BorderLayout());
            descpanel.add(new JLabel("Description:"), BorderLayout.NORTH);
            descpanel.add(dscroll, BorderLayout.CENTER);

            JPanel choicepanel = new JPanel(new GridLayout(3, 1));
            choicepanel.add(replace);
            choicepanel.add(open);
            choicepanel.add(view);
            choicepanel.setBorder(new EmptyBorder(5, 0, 0, 0));
            replace.setEnabled(false);
            open.setEnabled(false);
            view.setEnabled(false);

            JPanel infocont = new JPanel(new BorderLayout(0, 3));
            infocont.add(infopanel, BorderLayout.NORTH);
            infocont.add(descpanel, BorderLayout.CENTER);
            infocont.add(choicepanel, BorderLayout.SOUTH);

            JPanel infocont2 = new JPanel(new BorderLayout());
            infocont2.add(infocont, BorderLayout.NORTH);

            desc.setEditable(false);

            add(new JLabel("Scripts"), BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
            add(infocont2, BorderLayout.EAST);
        }

        private void populateScriptList() {
            String[] toolnames = tools.getToolNames();
            Tool tool;

            for (int count = 0; count < toolnames.length; count++) {
                tool = tools.getTool(toolnames[count]);
                handlePopulateTool(tool);
            }
        }

        private void handlePopulateTool(Tool tool) {
            ScriptTreeModel model = (ScriptTreeModel) scripttree.getModel();
            RenderingHint hint;
            String pack = "Unknown";

            if ((tool.isRenderingHint(ScriptConstants.SCRIPT_RENDERING_HINT)) && (tool instanceof TaskGraph)) {
                hint = tool.getRenderingHint(ScriptConstants.SCRIPT_RENDERING_HINT);

                if (hint.isRenderingDetail(ScriptConstants.SCRIPT_PACKAGE)) {
                    pack = (String) hint.getRenderingDetail(ScriptConstants.SCRIPT_PACKAGE);
                }

                if (runtask instanceof TaskGraph) {
                    model.insertTool(tool, pack);
                } else {
                    if ((!hint.isRenderingDetail(ScriptConstants.TASKGRAPHS_ONLY)) || (!new Boolean(
                            (String) hint.getRenderingDetail(ScriptConstants.TASKGRAPHS_ONLY)).booleanValue())) {
                        model.insertTool(tool, pack);
                    }
                }
            }
        }


        private void populateInfoPanel(TaskGraph taskgraph) {
            if (taskgraph != null) {
                TaskGraph group = taskgraph;
                RenderingHint hint = group.getRenderingHint(ScriptConstants.SCRIPT_RENDERING_HINT);

                name.setText(group.getToolName());
                pack.setText(group.getToolPackage());
                desc.setText(group.getPopUpDescription());
                open.setSelected(hint.isRenderingDetail(ScriptConstants.OPEN_TASKGRAPH) && (new Boolean(
                        (String) hint.getRenderingDetail(ScriptConstants.OPEN_TASKGRAPH)).booleanValue()));
                replace.setSelected(hint.isRenderingDetail(ScriptConstants.REPLACE_TASK) && (new Boolean(
                        (String) hint.getRenderingDetail(ScriptConstants.REPLACE_TASK)).booleanValue()));

                open.setEnabled(taskgraph.getDataOutputNodeCount() > 0);
                replace.setEnabled(taskgraph.getDataOutputNodeCount() > 0);
                view.setEnabled(true);

                curselect = taskgraph;
            } else {
                name.setText("");
                pack.setText("");
                desc.setText("");
                open.setEnabled(false);
                replace.setEnabled(false);
                view.setEnabled(false);

                curselect = null;
            }
        }


        /**
         * This method is called when the panel is reset or cancelled. It should reset all the panels components to the
         * values specified by the associated task, e.g. a component representing a parameter called "noise" should be
         * set to the value returned by a getTool().getParameter("noise") call.
         */
        public void reset() {
        }

        /**
         * This method is called when the panel is finished with. It should dispose of any components (e.g. windows)
         * used by the panel.
         */
        public void dispose() {
            tools.removeToolTableListener(this);
        }


        /**
         * Called when the ok button is clicked on the parameter window. Calls applyClicked by default to commit any
         * parameter changes.
         */
        public void okClicked() {
            if (curselect != null) {
                runScript(runtask, (TaskGraph) curselect, tools, replace.isEnabled() && replace.isSelected(),
                        open.isEnabled() && open.isSelected(), view.isEnabled() && view.isSelected(), source);
            }
        }


        @Override
        public void toolsAdded(java.util.List<Tool> tools) {
            for (Tool tool : tools) {
                handlePopulateTool(tool);
            }
        }

        @Override
        public void toolsRemoved(List<Tool> tools) {
        }

        /**
         * Called when a new tool is added
         */
        public void toolAdded(Tool tool) {
            handlePopulateTool(tool);
        }

        /**
         * Called when a tool is removed
         */
        public void toolRemoved(Tool tool) {
        }


        /**
         * Called when a Tool Box is added
         */
        public void toolBoxAdded(Toolbox toolbox) {
        }

        /**
         * Called when a Tool Box is Removed
         */
        public void toolBoxRemoved(Toolbox toolbox) {
        }

        @Override
        public void toolboxNameChanging(Toolbox toolbox, String newName) {
        }

        @Override
        public void toolboxNameChanged(Toolbox toolbox, String newName) {
        }


        /**
         * Called whenever the value of the selection changes.
         *
         * @param event the event that characterizes the change.
         */
        public void valueChanged(TreeSelectionEvent event) {
            if (event.getSource() == scripttree) {
                TreePath path = event.getNewLeadSelectionPath();

                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

                    if ((node != null) && node.isLeaf()) {
                        populateInfoPanel((TaskGraph) node.getUserObject());
                    } else {
                        populateInfoPanel(null);
                    }
                } else {
                    populateInfoPanel(null);
                }
            }
        }

    }


    private class ScriptTreeModel extends DefaultTreeModel {

        public ScriptTreeModel() {
            super(new DefaultMutableTreeNode("Scripts"));
        }


        public void insertTool(Tool tool, String pack) {
            MutableTreeNode parent = handlePackageNode(pack);
            insertChild(parent, tool);
        }


        private MutableTreeNode handlePackageNode(String pack) {
            String[] packs = pack.split("\\.");
            MutableTreeNode parent = (MutableTreeNode) getRoot();
            MutableTreeNode child;

            for (int count = 0; count < packs.length; count++) {
                child = getChild(parent, packs[count]);

                if (child == null) {
                    child = insertChild(parent, packs[count]);
                }

                parent = child;
            }

            return parent;
        }

        private MutableTreeNode getChild(TreeNode parent, String pack) {
            for (int count = 0; count < parent.getChildCount(); count++) {
                if (parent.getChildAt(count).toString().equals(pack)) {
                    return (MutableTreeNode) parent.getChildAt(count);
                }
            }

            return null;
        }

        private MutableTreeNode insertChild(MutableTreeNode parent, String pack) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(pack);
            boolean added = false;

            for (int count = 0; (count < parent.getChildCount()) && (!added); count++) {
                if (parent.getChildAt(count).toString().compareToIgnoreCase(pack) > 0) {
                    insertNodeInto(node, parent, count);
                    added = true;
                }
            }

            if (!added) {
                insertNodeInto(node, parent, parent.getChildCount());
            }

            return node;
        }

        private MutableTreeNode insertChild(MutableTreeNode parent, Tool tool) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(tool);
            DefaultMutableTreeNode temp;
            boolean added = false;

            for (int count = 0; (count < parent.getChildCount()) && (!added); count++) {
                temp = (DefaultMutableTreeNode) parent.getChildAt(count);

                if ((temp.getUserObject() instanceof Tool) && (temp.toString().compareToIgnoreCase(tool.toString())
                        > 0)) {
                    insertNodeInto(node, parent, count);
                    added = true;
                }
            }

            if (!added) {
                insertNodeInto(node, parent, parent.getChildCount());
            }

            return node;
        }

    }

}
