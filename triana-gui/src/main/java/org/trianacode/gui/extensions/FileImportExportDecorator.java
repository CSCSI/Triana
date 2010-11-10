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
package org.trianacode.gui.extensions;

import org.trianacode.gui.Display;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.TFileChooser;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicFileChooserUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Decorator design pattern implementation that adds import and export specific plugin functionality to file dialogs in
 * Triana. The import and export filters are dynamically configured.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public class FileImportExportDecorator implements ActionListener {

    private JFileChooser fc;
    private JDialog outer;

    private JComboBox filterList;
    private JButton approveBtn;
    private JCheckBox appendExtChk;
    private JButton cancelBtn;
    private int chooserResult;
    private JButton optionsBtn;
    private JPanel filterPanel;

    public FileImportExportDecorator(JFileChooser filechooser) {
        this.fc = filechooser;
    }

    /**
     * Pops up a "Import Taskgraph" file chooser dialog.
     *
     * @param parent the parent component of the dialog, can be <code>null</code>; see <code>showDialog</code> for
     *               details
     * @return the return state of the file chooser on popdown: <ul> <li>JFileChooser.CANCEL_OPTION
     *         <li>JFileChooser.APPROVE_OPTION <li>JFileCHooser.ERROR_OPTION if an error occurs or the dialog is
     *         dismissed </ul>
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     * @see GraphicsEnvironment#isHeadless
     */
    public int showImportDialog(Component parent) {
        System.out.println("FileImportExportDecorator.showImportDialog");
        init();
        List<TaskGraphImporterInterface> importers = ImportExportRegistry.getImporters();
        Object[] plugins = importers.toArray(new Object[importers.size()]);
        if (plugins.length == 0) {
            JOptionPane.showMessageDialog(parent, "No Taskgraph Importers currently available", "Import",
                    JOptionPane.INFORMATION_MESSAGE,
                    GUIEnv.getTrianaIcon());
            return TFileChooser.CANCEL_OPTION;
        }
        addFilters(plugins);
        approveBtn.setText(Env.getString("ImportBtn"));
        appendExtChk.setEnabled(false);
        setOptionsForSelectedFilter();

        fc.setDialogType(JFileChooser.OPEN_DIALOG);

        Display.centralise(outer);
        outer.setVisible(true);

        return chooserResult;
    }

    /**
     * Pops up a "Import Tool" file chooser dialog.
     *
     * @param parent the parent component of the dialog, can be <code>null</code>; see <code>showDialog</code> for
     *               details
     * @return the return state of the file chooser on popdown: <ul> <li>JFileChooser.CANCEL_OPTION
     *         <li>JFileChooser.APPROVE_OPTION <li>JFileCHooser.ERROR_OPTION if an error occurs or the dialog is
     *         dismissed </ul>
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     * @see GraphicsEnvironment#isHeadless
     */
    public int showImportToolDialog(Component parent) {
        init();
        List<ToolImporterInterface> importers = ImportExportRegistry.getToolImporters();
        Object[] plugins = importers.toArray(new Object[importers.size()]);
        if (plugins.length == 0) {
            JOptionPane.showMessageDialog(parent, "No Tool Importers currently available", "Import",
                    JOptionPane.INFORMATION_MESSAGE,
                    GUIEnv.getTrianaIcon());
            return TFileChooser.CANCEL_OPTION;
        }
        addFilters(plugins);
        approveBtn.setText(Env.getString("ImportBtn"));
        appendExtChk.setEnabled(false);
        setOptionsForSelectedFilter();

        fc.setDialogType(JFileChooser.OPEN_DIALOG);

        Display.centralise(outer);
        outer.setVisible(true);

        return chooserResult;
    }

    /**
     * Pops up a "Export Taskgraph" file chooser dialog.
     *
     * @param parent the parent component of the dialog, can be <code>null</code>; see <code>showDialog</code> for
     *               details
     * @return the return state of the file chooser on popdown: <ul> <li>JFileChooser.CANCEL_OPTION
     *         <li>JFileChooser.APPROVE_OPTION <li>JFileCHooser.ERROR_OPTION if an error occurs or the dialog is
     *         dismissed </ul>
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     * @see GraphicsEnvironment#isHeadless
     */
    public int showExportDialog(Component parent) {
        init();
        List<TaskGraphExporterInterface> importers = ImportExportRegistry.getExporters();
        Object[] plugins = importers.toArray(new Object[importers.size()]);
        if (plugins.length == 0) {
            JOptionPane.showMessageDialog(parent, "No Taskgraph Exporters currently available", "Export",
                    JOptionPane.INFORMATION_MESSAGE,
                    GUIEnv.getTrianaIcon());
            return TFileChooser.CANCEL_OPTION;
        }
        addFilters(plugins);
        approveBtn.setText(Env.getString("ExportBtn"));
        appendExtChk.setEnabled(true);
        setOptionsForSelectedFilter();

        fc.setDialogType(JFileChooser.CUSTOM_DIALOG);
        if ((fc.getSelectedFile() == null) && (GUIEnv.getSelectedDesktopView() != null)) {
            fc.setSelectedFile(new File(
                    fc.getCurrentDirectory().getPath() + File.separator + GUIEnv.getSelectedDesktopView()
                            .getTaskgraphPanel().getTaskGraph().getToolName()));
        }

        Display.centralise(outer);
        outer.setVisible(true);

        return chooserResult;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == approveBtn) {
            // bug fix for not picking up typed file name correctly
            if (fc.getUI() instanceof BasicFileChooserUI) {
                String filename = ((BasicFileChooserUI) fc.getUI()).getFileName();

                if ((filename != null) && (!filename.equals(""))) {
                    fc.setSelectedFile(new File(fc.getCurrentDirectory(), filename));
                }
            }

            fc.approveSelection();
            chooserResult = JFileChooser.APPROVE_OPTION;
            outer.setVisible(false);
        } else if (e.getSource() == cancelBtn) {
            fc.cancelSelection();
            chooserResult = JFileChooser.CANCEL_OPTION;
            outer.setVisible(false);
        } else if (e.getSource() == filterList) {
            setOptionsForSelectedFilter();
        } else if (e.getSource() == optionsBtn) {
            getSelectedFilter().showOptionsDialog(fc);
        }
    }

    /**
     * Translates and returns the <code>TaskGraph</code> from the file choosen by the user.
     *
     * @return the imported taskgraph thrown if the taskgraph or workflow format is invalid or unparsable, non matching
     *         brackets for example.
     * @throws java.io.IOException thrown if there is a file IO problem.
     */
    public TaskGraph importWorkflow() throws TaskGraphException, IOException {
        if (!(getSelectedFilter() instanceof TaskGraphImporterInterface)) {
            throw (new RuntimeException("Attempting to import workflow before Import Dialog shown"));
        }

        return ((TaskGraphImporterInterface) getSelectedFilter()).importWorkflow(fc.getSelectedFile());
    }

    /**
     * Converts the taskgraph to the appropriate format and writes it to the user choosen file.
     *
     * @param taskgraph the taskgraph to export
     * @throws java.io.IOException thrown if there is an IO problem writing the file.
     */
    public void exportWorkflow(TaskGraph taskgraph) throws IOException, TaskGraphException {
        if (!(getSelectedFilter() instanceof TaskGraphExporterInterface)) {
            throw (new RuntimeException("Attempting to export workflow before Export Dialog shown"));
        }

        // Bug fix for not picking up file name text.
        ((TaskGraphExporterInterface) getSelectedFilter()).exportWorkflow(taskgraph,
                fc.getSelectedFile(), appendExtChk.isSelected());
    }


    /**
     * @return the current default package for imported tools.
     */
    public String getDefaultToolPackage() {
        AbstractFormatFilter sel = getSelectedFilter();
        if (!(sel instanceof ToolImporterInterface)) {
            throw (new RuntimeException("Attempting to import tools before Import Dialog shown"));
        }
        return ((ToolImporterInterface) getSelectedFilter()).getDefaultToolPackage();
    }

    /**
     * Translates and returns the an array of <code>Tool</code> objects from the file choosen by the user.
     *
     * @param pack the suggested package for imported tools
     * @param pack
     * @return the imported tool thrown if the tool format is invalid or unparsable, non matching brackets for example.
     * @throws java.io.IOException thrown if there is a file IO problem.
     */
    public Tool[] importTools(String pack) throws TaskGraphException, IOException {
        if (!(getSelectedFilter() instanceof ToolImporterInterface)) {
            throw (new RuntimeException("Attempting to import tools before Import Dialog shown"));
        }

        return ((ToolImporterInterface) getSelectedFilter()).importTools(fc.getSelectedFile(), pack);
    }

    /**
     * Set the options based on choosen filter. Options button enabled, <code>FileFilter</code> filters.
     */

    private void setOptionsForSelectedFilter() {
        AbstractFormatFilter selectedFilter = getSelectedFilter();
        if (selectedFilter == null) {
            if (filterList.getItemCount() > 0) {
                filterList.setSelectedIndex(0);
                selectedFilter = (AbstractFormatFilter) filterList.getItemAt(0);
            }
        }
        if (selectedFilter != null) {
            optionsBtn.setEnabled(selectedFilter.hasOptions());
            fc.resetChoosableFileFilters();
            FileFilter[] choosableFileFilters = selectedFilter.getChoosableFileFilters();
            for (int i = 0; i < choosableFileFilters.length; i++) {
                fc.addChoosableFileFilter(choosableFileFilters[i]);
            }
            fc.setFileFilter(selectedFilter.getDefaultFileFilter());
        }
    }

    /**
     * Casts and return the selected filter from the combo box.
     *
     * @return the selected filter
     */
    private AbstractFormatFilter getSelectedFilter() {
        return (AbstractFormatFilter) filterList.getSelectedItem();
    }

    private void addFilters(Object[] plugins) {
        filterList.removeAllItems();
        for (int i = 0; i < plugins.length; i++) {
            filterList.addItem(plugins[i]);
        }
    }

    /**
     * Initialise the dialog
     */
    private void init() {
        fc.setControlButtonsAreShown(false);
        outer = new JDialog();
        outer.setModal(true);
        outer.getContentPane().setLayout(new BorderLayout());
        outer.getContentPane().add(fc, BorderLayout.CENTER);
        JPanel internal = new JPanel(new BorderLayout());
        outer.getContentPane().add(internal, BorderLayout.SOUTH);

        filterPanel = new JPanel();
        filterPanel.add(new JLabel(Env.getString("Format")));
        filterList = new JComboBox();
        filterList.setPrototypeDisplayValue("01234567890123456789012345");
        filterList.addActionListener(this);
        filterPanel.add(filterList);
        internal.add(filterPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        appendExtChk = new JCheckBox(Env.getString("AppendFileExt"), true);
        buttonPanel.add(appendExtChk);
        optionsBtn = new JButton(Env.getString("OptionsWindow"));
        optionsBtn.addActionListener(this);
        buttonPanel.add(optionsBtn);
        internal.add(buttonPanel, BorderLayout.CENTER);

        JPanel buttonPanel2 = new JPanel();
        approveBtn = new JButton();
        approveBtn.addActionListener(this);
        buttonPanel2.add(approveBtn);
        cancelBtn = new JButton(Env.getString("Cancel"));
        cancelBtn.addActionListener(this);
        buttonPanel2.add(cancelBtn);
        JPanel buttonPanel3 = new JPanel(new BorderLayout());
        buttonPanel3.add(buttonPanel2, BorderLayout.EAST);
        internal.add(buttonPanel3, BorderLayout.SOUTH);

        outer.getRootPane().setDefaultButton(approveBtn);
        outer.pack();
    }


}
