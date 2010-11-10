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
package org.trianacode.gui.action.files;

import org.trianacode.gui.Display;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.desktop.TrianaDesktopView;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.util.Env;
import org.trianacode.util.PrintUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Action to handle workflow/taskgraph Exporting.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public class PrintAction extends AbstractAction implements ActionDisplayOptions {

    private ToolSelectionHandler selhandler;

    public PrintAction(ToolSelectionHandler selhander) {
        this.selhandler = selhander;

        putValue(SHORT_DESCRIPTION, Env.getString("PrintTip"));
        putValue(ACTION_COMMAND_KEY, Env.getString("Print"));
        putValue(SMALL_ICON, GUIEnv.getIcon("print.png"));
        putValue(NAME, Env.getString("Print") + "...");
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent event) {
        final TaskGraph taskgraph = selhandler.getSelectedTaskgraph();

        Thread thread = new Thread() {
            public void run() {
                PrintPanel panel = new PrintPanel(taskgraph != null);
                panel.init();

                ParameterWindow window = new ParameterWindow(GUIEnv.getApplicationFrame(),
                        WindowButtonConstants.OK_CANCEL_BUTTONS, true);
                window.setTitle(Env.getString("Print") + "...");
                window.setParameterPanel(panel);
                Display.centralise(window);

                window.setVisible(true);

                if (window.isAccepted()) {
                    Component comp = null;

                    if (panel.isPrintSelectedTaskgraph()) {
                        TrianaDesktopView view = GUIEnv.getDesktopViewFor(taskgraph);
                        if (view != null) {
                            TaskGraphPanel taskgraphpanel = view.getTaskgraphPanel();
                            if (taskgraphpanel != null) {
                                comp = taskgraphpanel.getContainer();
                            } else {
                                JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(),
                                        "Error: Invalid selected taskgraph", "Print Taskgraph", JOptionPane.ERROR_MESSAGE,
                                        GUIEnv.getTrianaIcon());

                            }
                        } else {
                            JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(),
                                    "Error: Invalid selected taskgraph", "Print Taskgraph", JOptionPane.ERROR_MESSAGE,
                                    GUIEnv.getTrianaIcon());
                        }
                    } else {
                        comp = GUIEnv.getApplicationFrame();
                    }

                    if (comp != null) {
                        PrintUtilities
                                .printComponent(comp, panel.isScaleToPageSize(), panel.isPrintSelectedTaskgraph());
                    }
                }
            }
        };

        thread.setName("PrintActionThread");
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }


    private class PrintPanel extends ParameterPanel {

        JRadioButton taskgraphradio = new JRadioButton("Print Selected Taskgraph");
        JRadioButton trianaradio = new JRadioButton("Print Triana Window");

        JCheckBox scalecheck = new JCheckBox("Scale to page size", true);

        public PrintPanel(boolean isselected) {
            taskgraphradio.setSelected(isselected);
            taskgraphradio.setEnabled(isselected);
            trianaradio.setSelected(!isselected);
        }


        public boolean isPrintSelectedTaskgraph() {
            return taskgraphradio.isSelected();
        }

        public boolean isScaleToPageSize() {
            return scalecheck.isSelected();
        }


        public boolean isAutoCommitVisible() {
            return false;
        }

        /**
         * This method is called when the task is set for this panel. It is overridden to create the panel layout.
         */
        public void init() {
            setLayout(new BorderLayout(0, 5));

            JPanel radiopanel = new JPanel(new GridLayout(2, 1));
            radiopanel.add(taskgraphradio);
            radiopanel.add(trianaradio);

            ButtonGroup group = new ButtonGroup();
            group.add(taskgraphradio);
            group.add(trianaradio);

            add(radiopanel, BorderLayout.CENTER);
            add(scalecheck, BorderLayout.SOUTH);
        }

        public void reset() {
        }

        public void dispose() {
        }

    }


}
