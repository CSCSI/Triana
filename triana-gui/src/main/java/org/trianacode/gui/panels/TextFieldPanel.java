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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This panel allows the user to input information via as many text fields as you want. You simply specify the number of
 * textfields you want and pass the names for each of these JTextFields as a parameter when initialising this class. For
 * example, to create a window with 2 text fields and two names you would use the following :-
 * <pre>
 * <p/>
 * JTextFieldPanel panel;
 * <p/>
 * String[] names = new String[2];
 * <p/>
 * names[0] = "Enter Your Name";
 * names[1] = "Enter Your Address";
 * <p/>
 * panel = new JTextFieldPanel();
 * panel.setObject(this, 2, names);
 * <p/>
 * </pre>
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @see ParameterPanel
 */
public class TextFieldPanel extends UnitPanel implements ActionListener {
    /**
     * A reference to the vector which stores all of the Textfields
     */
    Vector textFields;

    String c[];

    int oldTextFields = -1;
    String[] textFieldNames;

    /**
     * Creates a new JTextFieldPanel for the particular task.
     *
     * @see ParameterPanel#ParameterPanel
     */
    public TextFieldPanel() {
    }


    public void setObject(Object object, int txtfields, int minSize, String names[]) {
        super.setObject(object);

        JTextField t;

        textFields = new Vector(txtfields);

        if (names != null) {
            textFieldNames = names;
        } else {
            textFieldNames = new String[txtfields];
        }

        for (int i = 0; i < txtfields; ++i) {
            t = new JTextField(minSize);
            textFields.addElement(t);
            t.addActionListener(this);
        }
        layoutPanel();
    }

    /**
     * Creates a new JTextFieldPanel for the particular task.
     *
     * @param txtfields number of JTextFields
     * @see ParameterPanel#ParameterPanel
     */
    public void setObject(Object object, int txtfields) {
        setObject(object, txtfields, 50, null);
    }

    /**
     * Creates a new JTextFieldPanel for the particular task.
     *
     * @param txtfields number of JTextFields
     * @param names     an array containing the names of each textfield
     * @see ParameterPanel#ParameterPanel
     */
    public void setObject(Object object, int txtfields, String names[]) {
        setObject(object, txtfields, 50, names);
    }

    /**
     * The layout of the Text Field Window.
     */
    public void layoutPanel() {
        if (oldTextFields == textFields.size()) {
            return;
        }

        removeAll();

        JPanel aPanel = new JPanel();
        aPanel.setLayout(new GridLayout(2 * textFields.size(), 1, 5, 5));

        setLayout(new BorderLayout());

        for (int i = 0; i < textFields.size(); ++i) {
            aPanel.add(new JLabel(textFieldNames[i], JLabel.CENTER));
            aPanel.add((JTextField) textFields.elementAt(i));
        }

        add(aPanel, BorderLayout.NORTH);

        oldTextFields = textFields.size();
    }

    public int getTextFieldNumberFor(JTextField tf) {
        for (int i = 0; i < textFields.size(); ++i) {
            if (tf == textFields.elementAt(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return the string which is contained within the specified text field .
     */
    public String getContents(int textFieldNumber) {
        return ((JTextField) textFields.elementAt(textFieldNumber)).getText();
    }

    /**
     * @return the value which is contained within the specified text field .
     */
    public double getDouble(int textFieldNumber) {
        return Double.parseDouble(((JTextField) textFields.elementAt(textFieldNumber)).getText());
    }

    /**
     * @return the value which is contained within the specified text field .
     */
    public float getFloat(int textFieldNumber) {
        return Float.parseFloat(((JTextField) textFields.elementAt(textFieldNumber)).getText());
    }

    /**
     * @return the value which is contained within the specified text field .
     */
    public int getInt(int textFieldNumber) {
        return Integer.parseInt(((JTextField) textFields.elementAt(textFieldNumber)).getText());
    }

    /**
     * @return the value which is contained within the specified text field .
     */
    public long getLong(int textFieldNumber) {
        return Long.parseLong(((JTextField) textFields.elementAt(textFieldNumber)).getText());
    }

    /**
     * @return the value which is contained within the specified text field .
     */
    public byte getByte(int textFieldNumber) {
        return Byte.parseByte(((JTextField) textFields.elementAt(textFieldNumber)).getText());
    }

    /**
     * Sets the name of the num'th textfield.  You must do a layoutPanel to change the appearance.
     */
    public void setName(int num, String name) {
        textFieldNames[num] = name;
    }


    /**
     * Sets the specified text field to the given text.
     */
    public void setContents(int textFieldNumber, String text) {
        JTextField t = ((JTextField) textFields.elementAt(textFieldNumber));
        t.removeActionListener(this);
        t.setText(text);
        t.addActionListener(this);
    }

    /**
     * Sets the specified text field to the given value.
     */
    public void setContents(int textFieldNumber, double val) {
        ((JTextField) textFields.elementAt(textFieldNumber)).setText(String.valueOf(val));
    }

    /**
     * Sets the specified text field to the given value.
     */
    public void setContents(int textFieldNumber, float val) {
        ((JTextField) textFields.elementAt(textFieldNumber)).setText(String.valueOf(val));
    }

    /**
     * Sets the specified text field to the given value.
     */
    public void setContents(int textFieldNumber, int val) {
        ((JTextField) textFields.elementAt(textFieldNumber)).setText(String.valueOf(val));
    }

    /**
     * Sets the specified text field to the given value.
     */
    public void setContents(int textFieldNumber, long val) {
        ((JTextField) textFields.elementAt(textFieldNumber)).setText(String.valueOf(val));
    }

    /**
     * Sets the specified text field to the given value.
     */
    public void setContents(int textFieldNumber, byte val) {
        ((JTextField) textFields.elementAt(textFieldNumber)).setText(String.valueOf(val));
    }

    /**
     * Sets the specified text field to the given value.
     */
    public void setContents(int textFieldNumber, char val) {
        ((JTextField) textFields.elementAt(textFieldNumber)).setText(String.valueOf(val));
    }

    /**
     * Sets the specified text field to the given object's toString().
     */
    public void setContents(int textFieldNumber, Object o) {
        ((JTextField) textFields.elementAt(textFieldNumber)).setText(o.toString());
    }

    /**
     * returns the name of the specified text field .
     */
    public String getName(int textFieldNumber) {
        return textFieldNames[textFieldNumber];
    }


    public void actionPerformed(ActionEvent evt) {
        if (!(evt.getSource() instanceof JTextField)) {
            return;
        }

        JTextField jtf = (JTextField) evt.getSource();

        int val = getTextFieldNumberFor(jtf);

        setParameter("content" + String.valueOf(val), jtf.getText());
    }

}


