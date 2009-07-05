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

import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.TaskGraphOrganize;
import org.trianacode.gui.panels.SaveHistoryPanel;
import org.trianacode.gui.panels.TFileChooser;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.clipin.HistoryClipIn;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.service.*;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Action class for "save History" actions
 *
 * @author Matthew Shields
 *         2@created May 12, 2003: 4:47:25 PM
 *         <<<<<<< SaveHistoryListener.java
 * @version $Revision: 4048 $
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 * =======
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 * >>>>>>> 1.2.2.1
 */
public class SaveHistoryListener implements ActionListener, ExecutionListener {

    private ToolSelectionHandler handler;

    private Hashtable infotable = new Hashtable();

    public SaveHistoryListener(ToolSelectionHandler handler) {
        this.handler = handler;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        if (handler.isSingleSelectedTool() && (handler.getSelectedTool() instanceof Task)) {
            if (e.getActionCommand().equals(Env.getString("saveHistory")))
                saveHistory((Task) handler.getSelectedTool());
            else if (e.getActionCommand().equals(Env.getString("autoSaveHistory")))
                autoSaveHistory((Task) handler.getSelectedTool(), ((JRadioButtonMenuItem) e.getSource()).isSelected());
        }
    }


    /**
     * Pulls up a file panel that allows the user to choose the file that the
     * history is saved in
     */
    private void saveHistory(Task task) {
        if (!(task instanceof ClipableTaskInterface)) {
            JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(), "Save Histroy Error: Clipins not implemented for " + task.getToolName(),
                    "Save Error", JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
            return;
        }

        if (!((ClipableTaskInterface) task).isClipInName(HistoryClipIn.HISTORY_CLIPIN_NAME)) {
            JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(), "Save Histroy Error: No History Information Availible",
                    "Save Error", JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
            return;
        }

        TFileChooser chooser = new TFileChooser(Env.DATA_DIRECTORY);
        chooser.setDialogTitle(task.getToolName() + ": " + Env.getString("saveHistory"));
        chooser.setFileFilter(new XMLFileFilter());

        if (chooser.showSaveDialog(GUIEnv.getApplicationFrame()) == JFileChooser.APPROVE_OPTION)
            saveHistory((ClipableTaskInterface) task, chooser.getSelectedFile());
    }

    /**
     * Save the history in the specified file
     */
    private void saveHistory(ClipableTaskInterface task, File file) {
        HistoryClipIn clipin = (HistoryClipIn) ((ClipableTaskInterface) task).getClipIn(HistoryClipIn.HISTORY_CLIPIN_NAME);
        TaskGraph history = clipin.getHistory();

        TaskGraphOrganize.organizeTaskGraph(TaskGraphOrganize.TREE_ORGANIZE, history);

        try {
            String taskName = FileUtils.getFileNameNoSuffix(file.getAbsolutePath());
            history.setToolName(taskName);

            XMLWriter writer = new XMLWriter(new FileWriter(file));
            writer.writeComponent(history);
            writer.close();
        } catch (IOException except) {
            JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(), "Save Histroy Error: " + except.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
        }
    }

    /**
     * Allows the user to select a file/sequence of files that the history
     * for the task is automatically saved in each time the task is run.
     */
    private void autoSaveHistory(Task task, boolean selected) {
        Frame frame = GUIEnv.getApplicationFrame();

        if (!(task instanceof RunnableTask)) {
            JOptionPane.showMessageDialog(frame, "Save History Error: " + task.getToolName() + " not a runnable instance (contact Triana developers)",
                    "Save Error", JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
            return;
        }

        if (!selected) {
            ((RunnableInstance) task).removeExecutionListener(this);

            if (infotable.containsKey(task)) {
                ((SaveHistoryInfo) infotable.get(task)).resetSequenceNumber();
            }
        } else {
            ParameterWindow window = new ParameterWindow(frame, WindowButtonConstants.OK_CANCEL_BUTTONS, true);
            SaveHistoryPanel panel;
            SaveHistoryInfo info;

            if (infotable.containsKey(task))
                info = (SaveHistoryInfo) infotable.get(task);
            else
                info = new SaveHistoryInfo("", false);

            panel = new SaveHistoryPanel(info.getFileName(), info.isAppendSequenceNumber());
            panel.init();

            window.setTitle("Auto Save History: " + task.getToolName());
            window.setLocation(frame.getLocationOnScreen().x + (frame.getSize().width / 2), frame.getLocationOnScreen().y + (frame.getSize().height / 2));
            window.setParameterPanel(panel);
            window.show();

            if (window.isAccepted() && (!panel.getFileName().equals(""))) {
                info.setFileName(panel.getFileName());
                info.setAppendSequenceNumber(panel.isAppendSequenceNumber());

                infotable.put(task, info);
                ((RunnableInstance) task).addExecutionListener(this);
            }
        }
    }

    /**
     * @return a file with the correct filename (including sequence number if
     *         required)
     */
    private File getFile(Task task, SaveHistoryInfo info) {
        if (info.isAppendSequenceNumber()) {
            String body = info.getFileName();
            String end = "";

            if (body.lastIndexOf('.') > -1) {
                end = body.substring(body.lastIndexOf('.'));
                body = body.substring(0, body.lastIndexOf('.'));
            }

            return new File(body + info.getSequenceNumber() + end);
        } else
            return new File(info.getFileName());
    }


    public void executionRequested(ExecutionEvent event) {
    }

    public void executionStarting(ExecutionEvent event) {
    }

    public void executionFinished(ExecutionEvent event) {
        if (infotable.containsKey(event.getRunnableInstance())) {
            RunnableTask task = (RunnableTask) event.getRunnableInstance();
            SaveHistoryInfo info = (SaveHistoryInfo) infotable.get(task);

            if (task.isClipInName(HistoryClipIn.HISTORY_CLIPIN_NAME)) {
                saveHistory(task, getFile(task, info));
                info.increaseSequenceNumber();
            }
        }
    }

    public void executionReset(ExecutionEvent event) {
    }

    public void executionStateChanged(ExecutionStateEvent event) {
    }


    private class SaveHistoryInfo {

        private String filename;
        private boolean append;
        private int seqnum = 0;

        public SaveHistoryInfo(String filename, boolean append) {
            this.filename = filename;
            this.append = append;
        }


        public void setFileName(String filename) {
            this.filename = filename;
        }

        public String getFileName() {
            return filename;
        }


        public void setAppendSequenceNumber(boolean append) {
            this.append = append;
        }

        public boolean isAppendSequenceNumber() {
            return append;
        }


        public void increaseSequenceNumber() {
            seqnum++;
        }

        public void resetSequenceNumber() {
            seqnum = 0;
        }

        public int getSequenceNumber() {
            return seqnum;
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
         * @see FileView#getName
         */
        public String getDescription() {
            return "XML Files";
        }

    }

}
