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

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.util.Env;

/**
 * A panel for specifying the auto save history file and sequence number policy
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class SaveHistoryPanel extends ParameterPanel implements ActionListener {

    private JTextField filename = new JTextField(25);
    private JButton filebrowse = new JButton(GUIEnv.getIcon("dots.png"));

    private JCheckBox seqcheck = new JCheckBox();


    public SaveHistoryPanel() {
        super();
    }

    public SaveHistoryPanel(String filename, boolean appendseq) {
        super();

        this.filename.setText(filename);
        this.seqcheck.setSelected(appendseq);
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
     * This method returns true by default. It should be overridden if the panel prefers to be allowed to be hidden
     * behind the main triana window.
     *
     * @return true by default
     */
    public boolean isAlwaysOnTopPreferred() {
        return true;
    }

    /**
     * This method returns true by default. It should be overridden if the panel does not want the user to be able to
     * change the auto commit state
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    /**
     * This method returns false by default. It should be overridden if the panel wants parameter changes to be commited
     * automatically
     */
    public boolean isAutoCommitByDefault() {
        return false;
    }


    /**
     * This method is called when the task is set for this panel. It is overridden to create the panel layout.
     */
    public void init() {
        setLayout(new BorderLayout(0, 3));

        JPanel filepanel = new JPanel(new BorderLayout(3, 0));
        filepanel.add(new JLabel("File Name"), BorderLayout.WEST);
        filepanel.add(filename, BorderLayout.CENTER);
        filepanel.add(filebrowse, BorderLayout.EAST);

        filebrowse.setMargin(new Insets(6, 4, 2, 4));
        filebrowse.addActionListener(this);

        add(filepanel, BorderLayout.CENTER);

        JPanel seqpanel = new JPanel(new BorderLayout(3, 0));
        seqpanel.add(new JLabel("Append Sequence Number"), BorderLayout.WEST);
        seqpanel.add(seqcheck, BorderLayout.CENTER);

        add(seqpanel, BorderLayout.SOUTH);
    }

    /**
     * This method is called when the panel is reset or cancelled. It should reset all the panels components to the
     * values specified by the associated task, e.g. a component representing a parameter called "noise" should be set
     * to the value returned by a getTool().getParameter("noise") call.
     */
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * This method is called when the panel is finished with. It should dispose of any components (e.g. windows) used by
     * the panel.
     */
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        TFileChooser chooser = new TFileChooser(Env.DATA_DIRECTORY);
        chooser.setSelectedFile(new File(getFileName()));
        chooser.setMultiSelectionEnabled(false);

        int result = chooser.showDialog(this, "O.K.");

        if (result == JFileChooser.APPROVE_OPTION) {
            filename.setText(chooser.getSelectedFile().getAbsolutePath());
            filename.setCaretPosition(0);
        }
    }

}
