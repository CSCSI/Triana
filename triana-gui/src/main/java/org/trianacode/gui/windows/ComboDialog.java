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
package org.trianacode.gui.windows;

import org.trianacode.gui.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A dialog for selecting an item from a list
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class ComboDialog extends JDialog implements ActionListener {

    /**
     * the main list
     */
    private JComboBox combo = new JComboBox(new DefaultComboBoxModel());

    /**
     * the label prompt
     */
    private JLabel label = new JLabel();

    /**
     * the ok and cancel buttons
     */
    private JButton ok = new JButton(Env.getString("OK"));
    private JButton cancel = new JButton(Env.getString("Cancel"));

    /**
     * a flag indicating whether the ok button was clicked
     */
    private boolean approve = false;

    /**
     * Constructs a modal combo dialog offering the specified item choices
     */
    public ComboDialog(String[] items, Frame parent) {
        super(parent);
        initialise(items, false);
    }

    /**
     * Constructs a modal combo dialog offering the specified item choices
     */
    public ComboDialog(String[] items, Dialog parent) {
        super(parent);
        initialise(items, false);
    }

    /**
     * Constructs a modal combo dialog offering the specified item choices
     *
     * @param title    the dialog title
     * @param editable a flag indicating whether the combo is editable
     */
    public ComboDialog(String[] items, Frame parent, String title, boolean editable) {
        super(parent, title, true);
        initialise(items, editable);
    }

    /**
     * Constructs a modal combo dialog offering the specified item choices
     *
     * @param title    the dialog title
     * @param editable a flag indicating whether the combo is editable
     */
    public ComboDialog(String[] items, Dialog parent, String title, boolean editable) {
        super(parent, title, true);
        initialise(items, editable);
    }


    /**
     * Initialises the dialog
     */
    private void initialise(String[] items, boolean editable) {
        getContentPane().setLayout(new BorderLayout());

        DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();
        combo.setEditable(editable);
        combo.setPrototypeDisplayValue("01234567890123456789");

        for (int count = 0; count < items.length; count++) {
            model.addElement(items[count]);
        }

        JPanel listpanel = new JPanel(new BorderLayout(3, 0));
        listpanel.add(label, BorderLayout.WEST);
        listpanel.add(combo, BorderLayout.CENTER);
        listpanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        getContentPane().add(listpanel, BorderLayout.CENTER);

        JPanel buttonpanel = new JPanel();
        buttonpanel.add(ok);
        buttonpanel.add(cancel);

        ok.addActionListener(this);
        cancel.addActionListener(this);

        getContentPane().add(buttonpanel, BorderLayout.SOUTH);

        pack();
    }


    /**
     * Sets the user prompt
     */
    public void setLabel(String prompt) {
        label.setText(prompt);
        pack();
    }

    /**
     * @return the user prompt
     */
    public String getLabel() {
        return label.getText();
    }


    /**
     * @return true if the ok button was clicked
     */
    public boolean isApproved() {
        return approve;
    }

    /**
     * @return an array of the selected items, or null if the cancel button was clicked
     */
    public String getSelectedItem() {
        if (!approve) {
            return null;
        } else {
            return (String) combo.getSelectedItem();
        }
    }


    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == ok) {
            approve = true;
        }

        setVisible(false);
        dispose();
    }


}
