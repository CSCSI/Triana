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
package org.trianacode.gui.panels;

import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

/**
 * A parameter panel for specifying input files
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 6th August
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class FilePanel extends ParameterPanel implements ActionListener, FocusListener {

    public static final String FILE_FILTER = "fileFilter";

    public static final String FILE_NAME = "fileName";

    public static final String FILE_NAME_TEXT = "fileNameText";
    public static final String PATH_TEXT = "pathText";


    private String filter = null;
    private String filenametxt = null;
    private String pathtxt = null;
    private String filenameparam = FILE_NAME;


    private JTextField file = new JTextField(25);
    private JTextField path = new JTextField(25);

    private JButton browse = new JButton("Browse...");
    private byte prefbuttons = -1;
    private boolean autocommitset = false;
    private boolean autocommitvis;


    public String getFilter() {
        if (filter != null)
            return filter;
        else if ((getTask() != null) && (getTask().isParameterName(FILE_FILTER)))
            return (String) getTask().getParameter(FILE_FILTER);
        else
            return null;
    }

    /**
     * Sets the filename filter
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilenameText() {
        if (filenametxt != null)
            return filenametxt;
        else if ((getTask() != null) && (getTask().isParameterName(FILE_NAME_TEXT)))
            return (String) getTask().getParameter(FILE_NAME_TEXT);
        else
            return "Filename";
    }

    /**
     * Sets the filename text
     */
    public void setFilenameText(String filenametxt) {
        this.filenametxt = filenametxt;
    }

    public String getPathText() {
        if (pathtxt != null)
            return pathtxt;
        else if ((getTask() != null) && (getTask().isParameterName(PATH_TEXT)))
            return (String) getTask().getParameter(PATH_TEXT);
        else
            return "Path";
    }

    /**
     * Sets the path text
     */
    public void setPathText(String pathtxt) {
        this.pathtxt = pathtxt;
    }

    /**
     * Sets the name of the parameter that the filename is stored in
     */
    public String getFilenameParam() {
        if (filenameparam != null)
            return filenameparam;
        else
            return FILE_NAME;
    }

    public void setFilenameParam(String filenameparam) {
        this.filenameparam = filenameparam;
    }


    /**
     * Sets the preferred buttons for this panel (as defined in WindowButtonConstants)
     */
    public void setPreferredButtons(byte buttons) {
        prefbuttons = buttons;
    }

    /**
     * @return the preferred set of buttons for this panel
     */
    public byte getPreferredButtons() {
        if (prefbuttons == -1)
            return super.getPreferredButtons();
        else
            return prefbuttons;
    }

    /**
     * Sets whether the autocommit checkbox is visible
     */
    public void setAutoCommitVisible(boolean state) {
        autocommitvis = state;
        autocommitset = true;
    }

    /**
     * This method returns true by default. It should be overridden if the panel does not want the
     * user to be able to change the auto commit state
     */
    public boolean isAutoCommitVisible() {
        if (!autocommitset)
            return super.isAutoCommitVisible();
        else
            return autocommitvis;
    }


    /**
     * Initialise user interface
     */
    public void init() {
        setLayout(new BorderLayout());

        JPanel labels = new JPanel(new GridLayout(2, 1));
        labels.setBorder(new EmptyBorder(3, 3, 3, 3));

        labels.add(new JLabel(getFilenameText()));
        labels.add(new JLabel(getPathText()));

        JPanel fields = new JPanel(new GridLayout(2, 1));
        fields.setBorder(new EmptyBorder(3, 0, 3, 3));
        fields.add(file);
        fields.add(path);

        file.addFocusListener(this);
        path.addFocusListener(this);

        JPanel browsepanel = new JPanel(new BorderLayout());
        browsepanel.setBorder(new EmptyBorder(3, 0, 3, 3));
        browsepanel.add(browse, BorderLayout.SOUTH);
        browse.addActionListener(this);

        JPanel contain = new JPanel(new BorderLayout());
        contain.add(labels, BorderLayout.WEST);
        contain.add(fields, BorderLayout.CENTER);
        contain.add(browsepanel, BorderLayout.EAST);

        add(contain, BorderLayout.NORTH);

        reset();
    }

    /**
     * Resets the user interface to the parameter values stored in the task
     */
    public void reset() {
        if (!getTask().isParameterName(getFilenameParam()))
            getTask().setParameter(getFilenameParam(), Env.userHome());

        File current = new File((String) getTask().getParameter(getFilenameParam()));

        if (current.isDirectory()) {
            file.setText("");
            path.setText(current.getAbsolutePath());
        }
        else {
            file.setText(current.getName());
            path.setText(current.getAbsolutePath().substring(0, current.getAbsolutePath().lastIndexOf(Env.separator())));
        }
    }


    public void dispose() {
    }


    /**
     * Called when the ok button is clicked on the parameter window. Commits any parameter changes.
     */
    public void okClicked() {
        setParameter(getFilenameParam(), path.getText() + Env.separator() + file.getText());

        super.okClicked();
    }

    /**
     * Called when the apply button is clicked on the parameter window. Commits any parameter
     * changes.
     */
    public void applyClicked() {
        setParameter(getFilenameParam(), path.getText() + Env.separator() + file.getText());

        super.applyClicked();
    }


    /**
     * Browse using a file dialog
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == browse) {
            File currentdir = new File(path.getText() + Env.separator());
            File currentfile = new File(file.getText());
            TFileChooser chooser = new TFileChooser();

            if (!currentdir.getPath().equals(""))
                chooser.setCurrentDirectory(currentdir);
            if (!currentfile.getPath().equals(""))
                chooser.setSelectedFile(currentfile);

            if (getFilter() != null)
                chooser.setFileFilter(new Filter());

            int choice = chooser.showDialog(this, "O.K.");

            if (choice == JFileChooser.APPROVE_OPTION) {
                File select = chooser.getSelectedFile();

                if (select.isDirectory()) {
                    file.setText("");
                    path.setText(select.getAbsolutePath());
                }
                else {
                    file.setText(select.getName());
                    path.setText(select.getAbsolutePath().substring(0, select.getAbsolutePath().lastIndexOf(Env.separator())));
                }

                file.setCaretPosition(0);
                path.setCaretPosition(0);

                setParameter(getFilenameParam(), path.getText() + Env.separator() + file.getText());
            }
        }
    }


    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        setParameter(getFilenameParam(), path.getText() + Env.separator() + file.getText());
    }


    private class Filter extends FileFilter {

        public boolean accept(File file) {
            if (getFilter() == null)
                return true;

            if (file.isDirectory())
                return true;

            String filterstr = getFilter();
            String filter;

            while (filterstr.indexOf(' ') != -1) {
                filter = filterstr.substring(0, filterstr.indexOf(' '));

                if (isAccept(file, filter))
                    return true;

                filterstr = filterstr.substring(filterstr.indexOf(' ') + 1);
            }

            return isAccept(file, filterstr);
        }

        private boolean isAccept(File file, String filter) {
            if (filter.lastIndexOf('.') == -1)
                return true;

            filter = filter.substring(filter.lastIndexOf('.'));

            if (filter.equals("*"))
                return true;
            else if (file.getName().endsWith(filter))
                return true;
            else
                return false;
        }


        public String getDescription() {
            if (getFilter() != null)
                return getFilter();
            else
                return "*.*";
        }
    }

}
