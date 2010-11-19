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

package org.trianacode.gui.hci;

import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.gui.util.Env;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.RenderingHintImp;
import org.trianacode.taskgraph.proxy.IncompatibleProxyException;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * A gui class with static methods for handling taskgraph operations, such as create task and connect nodes.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class TaskGraphHandler {

    static Logger logger = Logger.getLogger("org.trianacode.gui.hci.TaskGraphHandler");


    /**
     * Creates a unit from a ToolImp object and places it on this MainTriana at the position indicated and sets it to be
     * the only selected ToolImp. A reference to the toolbox which the tool came from and the position within that
     * toolbox is also given.
     */
    public static void createTask(final Tool tool, final TaskGraphPanel panel, final int x, final int y) {
        Thread taskThread = new Thread(new Runnable() {
            public void run() {

                TrianaProgressBar progressBar = null;

                try {
                    Tool newtool;

                    if (tool instanceof TaskGraph) {
                        newtool = ((TaskGraph) tool);
                    } else {
                        RenderingHint hint = getTaskGraphFactoryHint(tool, panel);

                        if (hint == null) {
                            return;
                        }

                        newtool = TaskGraphUtils.cloneTool(tool);
                        ((Tool) newtool).addRenderingHint(hint);
                    }

                    boolean pause = (!(newtool instanceof TaskGraph)) ||
                            (TaskGraphUtils.getAllTasksCount((TaskGraph) newtool, true) <= 10);
                    progressBar = new TrianaProgressBar("Loading: " + newtool.getToolName(), pause);

                    TaskComponent[] comps = panel.getTaskComponents();

                    for (int count = 0; count < comps.length; count++) {
                        comps[count].setSelected(false);
                    }

                    if (newtool instanceof Tool) {
                        TaskLayoutUtils.setPosition((Tool) newtool, new TPoint(x, y), panel.getLayoutDetails());
                    }

                    Task task = panel.getTaskGraph().createTask(newtool, false);

                    if (GUIEnv.isAutoConnect()) {
                        boolean connect = (panel.getTaskGraph().getControlTask() != task);

                        if (connect) {
                            TPoint pos = TaskLayoutUtils.getPosition(task, panel.getLayoutDetails());
                            new AutoConnect()
                                    .autoConnect(task, (int) pos.getX(), (int) pos.getY(), panel.getTaskGraph());
                        }
                    }

                    panel.getContainer().requestFocus();
                } catch (TaskException except) {
                    new ErrorDialog(Env.getString("taskError") + ": " + tool.getToolName(), except.getMessage());
                } finally {
                    if (progressBar != null) {
                        progressBar.disposeProgressBar();
                    }
                }
            }
        });
        taskThread.setName("TrianaTaskCreation");
        taskThread.setPriority(Thread.NORM_PRIORITY);
        taskThread.start();
    }

    /**
     * Gets the taskgraph factory rendering hint for the specified tool. This method should not be used with taskgraphs
     * (a RuntimeException will be thrown!).
     *
     * @return the taskgraph factory rendering hint, or null if cancelled
     */
    private static RenderingHint getTaskGraphFactoryHint(Tool newtool, TaskGraphPanel panel) throws TaskException {
        TaskGraphFactory factory = TaskGraphManager.getTaskGraphFactory(panel.getTaskGraph());
        Proxy proxy = newtool.getProxy();

        if (newtool instanceof TaskGraphFactory) {
            throw (new RuntimeException("getTaskGraphFactoryHint should not be called with TaskGraph instances"));
        }

        if (proxy == null) {
            throw (new TaskException("No Proxy set in " + newtool.getToolName()));
        }

        TaskFactory[] factories = factory.getRegisteredTaskGraphFactories(proxy.getType());
        String factoryname;

        if (factories.length == 0) {
            throw (new TaskException("No TaskFactory set in for Proxy type: " + proxy.getType()));
        }

        if (factories.length == 1) {
            factoryname = factories[0].getFactoryName();
        } else {
            factoryname = showSelectTaskFactoryDialog(newtool.getToolName(), proxy.getType(), factories);
        }

        if (factoryname == null) {
            return null;
        }

        RenderingHintImp hint = new RenderingHintImp(TaskGraphFactory.TASKGRAPH_FACTORY_RENDENRING_HINT, true);
        hint.setRenderingDetail(TaskGraphFactory.FACTORY_NAME, factoryname);

        return hint;
    }


    /**
     * Connects two nodes, type checking if required
     */
    public static void connect(TaskGraphPanel panel, Node outnode, Node innode) {
        try {

            logger.fine(
                    outnode.getTask().getToolName() + " attempting to connect to " + innode.getTask().getToolName());

            panel.getTaskGraph().connect(outnode, innode);
        } catch (IncompatibleTypeException except) {
            new ErrorDialog(
                    Env.getString("cableError") + ": " + outnode.getTask().getToolName() + "->" + innode.getTask()
                            .getToolName(),
                    "Error connecting " + outnode.getTask().getToolName() + " to " + innode.getTask().getToolName()
                            + ": Incompatible Types");
        } catch (IncompatibleProxyException except) {
            new ErrorDialog(
                    Env.getString("cableError") + ": " + outnode.getTask().getToolName() + "->" + innode.getTask()
                            .getToolName(),
                    "Error connecting " + outnode.getTask().getToolName() + " to " + innode.getTask().getToolName()
                            + ": Incompatible Proxies (" + outnode.getTopLevelTask().getProxy().getType() + "->"
                            + innode.getTopLevelTask().getProxy().getType() + ")");
        } catch (CableException except) {
            new ErrorDialog(
                    Env.getString("cableError") + ": " + outnode.getTask().getToolName() + "->" + innode.getTask()
                            .getToolName(), except.getMessage());
        }
    }


    /**
     * @return the selected factory name or null if cancelled
     */
    private static String showSelectTaskFactoryDialog(String toolname, String proxyname, TaskFactory[] factories) {
        SelectTaskFactoryDialog dialog = new SelectTaskFactoryDialog(toolname, proxyname, factories);
        dialog.show();

        if (dialog.isAccepted()) {
            return dialog.getFactoryName();
        } else {
            return null;
        }
    }


    private static class SelectTaskFactoryDialog extends JDialog implements ActionListener, ListSelectionListener {

        private JList list = new JList(new DefaultListModel());
        private JTextArea description = new JTextArea(10, 20);

        private JButton ok = new JButton(Env.getString("OK"));
        private JButton cancel = new JButton(Env.getString("Cancel"));

        private boolean okclicked = false;


        public SelectTaskFactoryDialog(String toolname, String proxyname, TaskFactory[] factories) {
            super(GUIEnv.getApplicationFrame(), "Create Task: " + toolname, true);

            initLayout(proxyname);
            populateList(factories);
        }


        /**
         * @return true if the dialog was accepted
         */
        public boolean isAccepted() {
            return okclicked;
        }

        /**
         * @return the name of the taskgraph factory
         */
        public String getFactoryName() {
            if (list.getSelectedValue() != null) {
                return ((TaskFactoryItem) list.getSelectedValue()).getTaskFactory().getFactoryName();
            } else {
                return null;
            }
        }


        private void initLayout(String proxyname) {
            JScrollPane scroll = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            list.setPrototypeCellValue("01234567890123456789012345");
            list.setVisibleRowCount(10);
            list.addListSelectionListener(this);

            JPanel listpanel = new JPanel(new BorderLayout(3, 0));
            listpanel.add(scroll, BorderLayout.CENTER);
            listpanel.add(description, BorderLayout.EAST);
            description.setEditable(false);
            description.setLineWrap(true);
            description.setWrapStyleWord(true);
            description.setBackground(getBackground());

            JPanel listcont = new JPanel(new BorderLayout(0, 3));
            listcont.add(new JLabel(proxyname + " Type:"), BorderLayout.NORTH);
            listcont.add(listpanel, BorderLayout.CENTER);

            JPanel buttonpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonpanel.add(ok);
            buttonpanel.add(cancel);
            ok.addActionListener(this);
            cancel.addActionListener(this);

            JPanel mainpanel = new JPanel(new BorderLayout(0, 5));
            mainpanel.add(listcont, BorderLayout.CENTER);
            mainpanel.add(buttonpanel, BorderLayout.SOUTH);
            mainpanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

            getContentPane().add(mainpanel);
            getRootPane().setDefaultButton(ok);
            setLocationRelativeTo(GUIEnv.getApplicationFrame());

            pack();
        }

        private void populateList(TaskFactory[] factories) {
            DefaultListModel model = (DefaultListModel) list.getModel();

            for (int count = 0; count < factories.length; count++) {
                addSorted(new TaskFactoryItem(factories[count]), model);
            }

            if (model.size() > 0) {
                list.setSelectedIndex(0);
            }

            ok.setEnabled(model.size() > 0);
        }

        private void addSorted(TaskFactoryItem item, DefaultListModel model) {
            for (int count = 0; count < model.size(); count++) {
                if (item.toString().compareTo(model.getElementAt(count).toString()) < 0) {
                    model.add(count, item);
                    return;
                }
            }

            model.addElement(item);
        }

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == ok) {
                okclicked = true;
                setVisible(false);
                dispose();
            } else {
                setVisible(false);
                dispose();
            }
        }

        /**
         * Called whenever the value of the selection changes.
         */
        public void valueChanged(ListSelectionEvent event) {
            if (event.getSource() == list) {
                if (list.getSelectedIndex() != -1) {
                    TaskFactory factory = ((TaskFactoryItem) list.getSelectedValue()).getTaskFactory();
                    description.setText(factory.getFactoryDescription());
                    ok.setEnabled(true);
                } else {
                    description.setText(null);
                    ok.setEnabled(false);
                }
            }
        }

    }


    public static class TaskFactoryItem {

        private TaskFactory factory;


        public TaskFactoryItem(TaskFactory factory) {
            this.factory = factory;
        }

        public TaskFactory getTaskFactory() {
            return factory;
        }

        public String toString() {
            return factory.getFactoryName();
        }

    }


}
