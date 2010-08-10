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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import org.trianacode.gui.Display;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.TaskGraphOrganize;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.panels.TFileChooser;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.clipin.HistoryClipIn;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.service.ClipableTaskInterface;
import org.trianacode.taskgraph.service.HistoryTrackerAutoSave;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.util.Env;

/**
 * The action for showing the node editor
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class HistoryTrackingAction extends AbstractAction implements ActionDisplayOptions {

    private ToolSelectionHandler selhandler;


    public HistoryTrackingAction(ToolSelectionHandler sel) {
        this.selhandler = sel;

        putValue(SHORT_DESCRIPTION, Env.getString("HistoryTrackingTip"));
        putValue(ACTION_COMMAND_KEY, Env.getString("HistoryTracking"));
        putValue(NAME, Env.getString("HistoryTracking") + "...");
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        if (selhandler.isSingleSelectedTool() && (selhandler.getSelectedTool() instanceof Task)) {
            showHistoryTrackingDialog((Task) selhandler.getSelectedTool(), e.getSource());
        }
    }


    /**
     * Show the history tracking dialog for the specified task
     *
     * @param source the source object asking for the node editor to be shown
     */
    protected void showHistoryTrackingDialog(Task task, Object source) {
        ParameterWindow historyWindow = new ParameterWindow(GUIEnv.getApplicationFrame(),
                WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS, true);
        historyWindow.setTitle(Env.getString("HistoryTracking") + ": " + task.getToolName());

        HistoryTrackingPanel historypanel = new HistoryTrackingPanel();
        historypanel.setTask(task);
        historypanel.init();

        historyWindow.setParameterPanel(historypanel);

        Point loc = Display.getAnchorPoint(source, historyWindow);
        loc.translate(140, 40);

        historyWindow.setLocation(loc);
        historyWindow.setVisible(true);
        historyWindow.requestFocus();

        if (historyWindow.isAccepted()) {
            task.setParameter(HistoryTrackerAutoSave.AUTO_SAVE, String.valueOf(historypanel.isAutoSave()));
            task.setParameter(HistoryTrackerAutoSave.AUTO_SAVE_FILENAME, historypanel.getFileName());
            task.setParameter(HistoryTrackerAutoSave.AUTO_SAVE_APPEND,
                    String.valueOf(historypanel.isAppendSequenceNumber()));
        }


    }


    private class HistoryTrackingPanel extends ParameterPanel implements ActionListener, ItemListener {

        private JButton savebutton = new JButton(Env.getString("saveHistoryNow") + "...");

        private JCheckBox autosave = new JCheckBox(Env.getString("autoSaveHistory"));

        private JTextField filename = new JTextField(25);
        private JButton filebrowse = new JButton(GUIEnv.getIcon("dots.png"));

        private JCheckBox seqcheck = new JCheckBox();


        /**
         * Hides the auto commit button
         */
        public boolean isAutoCommitVisible() {
            return false;
        }

        /**
         * This method returns false by default. It should be overridden if the panel wants parameter changes to be
         * commited automatically
         */
        public boolean isAutoCommitByDefault() {
            return false;
        }

        /**
         * This method returns WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS by default. It should be overridden if the
         * panel has different preferred set of buttons.
         *
         * @return the panels preferred button combination (as defined in Windows Constants).
         */
        public byte getPreferredButtons() {
            return WindowButtonConstants.OK_CANCEL_BUTTONS;
        }


        /**
         * This method is called when the task is set for this panel. It is overridden to create the panel layout.
         */
        public void init() {
            setLayout(new BorderLayout(3, 3));

            JPanel savepanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            savepanel.add(savebutton);
            savebutton.addActionListener(this);

            JPanel filepanel = new JPanel(new BorderLayout(3, 0));
            filepanel.add(new JLabel("File Name"), BorderLayout.WEST);
            filepanel.add(filename, BorderLayout.CENTER);
            filepanel.add(filebrowse, BorderLayout.EAST);
            filepanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

            filebrowse.setMargin(new Insets(6, 4, 2, 4));
            filebrowse.addActionListener(this);

            JPanel seqpanel = new JPanel(new BorderLayout(3, 0));
            seqpanel.add(new JLabel("Append Sequence Number"), BorderLayout.WEST);
            seqpanel.add(seqcheck, BorderLayout.CENTER);
            seqpanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

            JPanel autopanel = new JPanel(new BorderLayout(3, 3));
            autopanel.add(autosave, BorderLayout.NORTH);
            autopanel.add(filepanel, BorderLayout.CENTER);
            autopanel.add(seqpanel, BorderLayout.SOUTH);
            autosave.addItemListener(this);

            filename.setEnabled(autosave.isSelected());
            filebrowse.setEnabled(autosave.isSelected());
            seqcheck.setEnabled(autosave.isSelected());

            add(savepanel, BorderLayout.NORTH);
            add(autopanel, BorderLayout.CENTER);
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

        }


        /**
         * @return true if auto save is enabled
         */
        public boolean isAutoSave() {
            return autosave.isSelected();
        }

        /**
         * @return the filename
         */
        public String getFileName() {
            return filename.getText();
        }

        /**
         * @return true if a sequence number is to be appended
         */
        public boolean isAppendSequenceNumber() {
            return seqcheck.isSelected();
        }


        /**
         * Pulls up a file panel that allows the user to choose the file that the history is saved in
         */
        private void saveHistory(Task task) {
            if (!(task instanceof ClipableTaskInterface)) {
                JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(),
                        "Save Histroy Error: Clipins not implemented for " + task.getToolName(),
                        "Save Error", JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
                return;
            }

            if (!((ClipableTaskInterface) task).isClipInName(HistoryClipIn.HISTORY_CLIPIN_NAME)) {
                JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(),
                        "Save Histroy Error: No history information availible, try re-running with history tracking",
                        "Save Error", JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
                return;
            }

            TFileChooser chooser = new TFileChooser(Env.DATA_DIRECTORY);
            chooser.setDialogTitle(task.getToolName() + ": " + Env.getString("saveHistory"));
            chooser.setFileFilter(new XMLFileFilter());

            if (chooser.showSaveDialog(GUIEnv.getApplicationFrame()) == JFileChooser.APPROVE_OPTION) {
                saveHistory((ClipableTaskInterface) task, chooser.getSelectedFile());
            }
        }

        /**
         * Save the history in the specified file
         */
        private void saveHistory(ClipableTaskInterface task, File file) {
            HistoryClipIn clipin = (HistoryClipIn) task.getClipIn(HistoryClipIn.HISTORY_CLIPIN_NAME);
            TaskGraph history = clipin.getHistory();

            TaskGraphOrganize.organizeTaskGraph(TaskGraphOrganize.TREE_ORGANIZE, history);

            try {
                String taskName = FileUtils.getFileNameNoSuffix(file.getAbsolutePath());
                history.setToolName(taskName);

                XMLWriter writer = new XMLWriter(new FileWriter(file));
                writer.writeComponent(history);
                writer.close();
            } catch (IOException except) {
                JOptionPane
                        .showMessageDialog(GUIEnv.getApplicationFrame(), "Save Histroy Error: " + except.getMessage(),
                                "Save Error", JOptionPane.ERROR_MESSAGE,
                                GUIEnv.getTrianaIcon());
            }
        }

        private void fileBrowse() {
            TFileChooser chooser = new TFileChooser(Env.DATA_DIRECTORY);
            chooser.setSelectedFile(new File(getFileName()));
            chooser.setMultiSelectionEnabled(false);

            int result = chooser.showDialog(this, "O.K.");

            if (result == JFileChooser.APPROVE_OPTION) {
                filename.setText(chooser.getSelectedFile().getAbsolutePath());
                filename.setCaretPosition(0);
            }
        }

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == savebutton) {
                saveHistory(getTask());
            } else if (event.getSource() == filebrowse) {
                fileBrowse();
            }
        }

        /**
         * Invoked when an item has been selected or deselected by the user. The code written for this method performs
         * the operations that need to occur when an item is selected (or deselected).
         */
        public void itemStateChanged(ItemEvent event) {
            if (event.getSource() == autosave) {
                filename.setEnabled(autosave.isSelected());
                filebrowse.setEnabled(autosave.isSelected());
                seqcheck.setEnabled(autosave.isSelected());
            }

        }

    }


    private class XMLFileFilter extends FileFilter {

        private static final String XML_EXTENSION = ".xml";

        /**
         * Whether the given file is accepted by this filter.
         */
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().toLowerCase().endsWith(XML_EXTENSION);
        }

        /**
         * The description of this filter. For example: "JPG and GIF Images"
         *
         * @see javax.swing.filechooser.FileView#getName
         */
        public String getDescription() {
            return "XML Files";
        }

    }

}
