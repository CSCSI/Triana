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
package org.trianacode.gui.action.taskgraph;

import org.trianacode.gui.Display;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.FormLayout;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.constants.ControlToolConstants;
import org.trianacode.taskgraph.service.TrianaClient;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.ToolTableListener;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action class to handle all "select all" actions.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 * @created May 2, 2003: 3:49:12 PM
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class GroupAction extends AbstractAction implements ActionDisplayOptions {

    private ToolSelectionHandler selhandler;
    private ToolTable tools;


    public GroupAction(ToolSelectionHandler sel, ToolTable tools) {
        this.selhandler = sel;
        this.tools = tools;

        putValue(SHORT_DESCRIPTION, Env.getString("GroupTip"));
        putValue(ACTION_COMMAND_KEY, Env.getString("Group"));
        putValue(SMALL_ICON, GUIEnv.getIcon("group.png"));
        putValue(NAME, Env.getString("Group"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        Tool[] tools = selhandler.getSelectedTools();

        if ((tools.length > 0) && (selhandler.getSelectedTrianaClient() != null)) {
            try {
                Task[] tasks = new Task[tools.length];
                System.arraycopy(tools, 0, tasks, 0, tools.length);

                TaskGraph taskgraph = tasks[0].getParent();
                boolean parenterror = (taskgraph == null);

                for (int count = 1; (count < tasks.length) && (!parenterror); count++)
                    parenterror = parenterror || (tasks[count].getParent() == null) || (tasks[count].getParent() != taskgraph);

                if (parenterror)
                    new ErrorDialog("Error: Cannot group tasks with different parent taskgraphs/null parent taskgraphs");
                else
                    groupTasks(tasks, taskgraph, selhandler.getSelectedTrianaClient());
            } catch (ArrayStoreException except) {
                new ErrorDialog("Error: Cannot group non-instantiated tools");
            }
        }
    }

    /**
     * Groups the specified tasks
     */
    public void groupTasks(Task[] tasks, TaskGraph taskgraph, TrianaClient client) {
        NewGroupPanel panel = new NewGroupPanel();
        panel.init();

        ParameterWindow window = new ParameterWindow(GUIEnv.getApplicationFrame(), panel.getPreferredButtons(), true);
        window.setParameterPanel(panel);
        window.setTitle(Env.getString("Group"));
        Display.centralise(window);

        window.pack();
        window.setVisible(true);

        if (window.isAccepted()) {
            try {
                String[] tasknames = new String[tasks.length];

                if (tasks.length > 0) {
                    for (int count = 0; count < tasks.length; count++)
                        tasknames[count] = tasks[count].getToolName();

                    TaskGraph group = taskgraph.groupTasks(tasknames, panel.getGroupName());
                    TaskLayoutUtils.translateToOrigin(group.getTasks(false));
                    TaskLayoutUtils.resolveGroupNodes(group);

                    if (panel.getControlTool() != null) {
                        group.createControlTask(panel.getControlTool(), false);
                        TaskGraphUtils.connectControlTask(group);
                    }

                    GUIEnv.getApplicationFrame().addChildTaskGraphPanel(group, client);
                }
            } catch (TaskGraphException except) {
                new ErrorDialog(Env.getString("taskError"), except.getMessage());
            }
        }
    }


    private class NewGroupPanel extends ParameterPanel implements ToolTableListener {

        private JTextField name = new JTextField("New Group", 18);
        private JComboBox controllist = new JComboBox(new DefaultComboBoxModel());

        /**
         * This method returns true by default. It should be overridden if the panel
         * does not want the user to be able to change the auto commit state
         */
        public boolean isAutoCommitVisible() {
            return false;
        }

        /**
         * This method returns WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS by
         * default. It should be overridden if the panel has different preferred set
         * of buttons.
         *
         * @return the panels preferred button combination (as defined in Windows Constants).
         */
        public byte getPreferredButtons() {
            return WindowButtonConstants.OK_CANCEL_BUTTONS;
        }


        /**
         * @return the name for the group
         */
        public String getGroupName() {
            return name.getText();
        }

        /**
         * @return the control tool for the group (or null if none)
         */
        public Tool getControlTool() {
            Object obj = controllist.getSelectedItem();

            if ((obj == null) || (!(obj instanceof Tool)))
                return null;
            else
                return (Tool) obj;
        }


        /**
         * This method is called when the task is set for this panel. It is overridden
         * to create the panel layout.
         */
        public void init() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(4, 4, 4, 4));

            JPanel formpanel = new JPanel(new FormLayout(8, 3));
            formpanel.add(new JLabel(Env.getString("nameForNewGroup")));
            formpanel.add(name);
            formpanel.add(new JLabel(Env.getString("controlTool")));
            formpanel.add(controllist);

            JLabel iconlabel = new JLabel(UIManager.getIcon("OptionPane.questionIcon"));
            iconlabel.setVerticalAlignment(JLabel.CENTER);
            iconlabel.setHorizontalAlignment(JLabel.RIGHT);
            iconlabel.setBorder(new EmptyBorder(10, 10, 10, 10));

            add(formpanel, BorderLayout.CENTER);
            add(iconlabel, BorderLayout.WEST);

            populateControlCombo();
            tools.addToolTableListener(this);
        }

        private void populateControlCombo() {
            DefaultComboBoxModel model = (DefaultComboBoxModel) controllist.getModel();
            model.addElement("NONE");

            String[] toolnames = tools.getToolNames();
            Tool tool;

            for (int count = 0; count < toolnames.length; count++) {
                tool = tools.getTool(toolnames[count]);
                handlePopulateTool(tool);
            }
        }

        private void handlePopulateTool(Tool tool) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) controllist.getModel();

            if (tool.isRenderingHint(ControlToolConstants.CONTROL_TOOL_RENDERING_HINT))
                model.addElement(tool);
        }

        /**
         * This method is called when the panel is reset or cancelled. It should reset
         * all the panels components to the values specified by the associated task,
         * e.g. a component representing a parameter called "noise" should be set to
         * the value returned by a getTool().getParameter("noise") call.
         */
        public void reset() {
        }

        /**
         * This method is called when the panel is finished with. It should dispose
         * of any components (e.g. windows) used by the panel.
         */
        public void dispose() {
            tools.removeToolTableListener(this);
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
            DefaultComboBoxModel model = (DefaultComboBoxModel) controllist.getModel();
            model.removeElement(tool);
        }

        /**
         * Called when a Tool Box is added
         */
        public void toolBoxAdded(String toolbox) {
        }

        /**
         * Called when a Tool Box is Removed
         */
        public void toolBoxRemoved(String toolbox) {
        }


    }
}
