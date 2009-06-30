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
package org.trianacode.gui.builder;

import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;
import org.trianacode.taskgraph.util.FileUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Vector;


/**
 * This is the screen in which users sets the GUI parameters to
 * build their user-defined interface for their unit.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @created 1 Decmeber 1999
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class GUICreaterPanel extends ParameterPanel
        implements ActionListener, ChangeListener, ItemListener, FocusListener {

    private Vector allRows;

    private Vector<String> allParams;

    /**
     * a list of all the parameter names represented in this panel
     */
    private Vector paramnames = new Vector();


    /**
     * a boolean used to determine whether the setParameter function
     * should work or not. When events are sent from here to the unit
     * this is set to false because the widgets which sent the events
     * dont need to be update
     */
    boolean update = true;


    /**
     * Creates a new GUI creater panel.
     *
     * @param guilines the widget set
     */
    public GUICreaterPanel(Vector<String> guilines) {
        super();

        setParameters(guilines);
        layoutPanel();
    }


    /**
     * Installs this panel as a task listener
     */
    public void setTask(Task task) {
        super.setTask(task);
    }


    /**
     * sets the parameters to the new StringVector which contains a vector
     * of Strings representing each line of the interface.  This also
     * does a display since once new parameters have been set then
     * it makes sense to show them!
     */
    public void setParameters(Vector<String> sv) {
        allParams = sv;
        Vector<String> sv2;

        if (allRows != null)
            allRows.removeAllElements();
        else
            allRows = new Vector();

        Row temprow;

        for (int i = 0; i < sv.size(); ++i) { // create all the rows
            sv2 = FileUtils.splitLine(sv.get(i)); // split each line
            temprow = new Row(sv2, this);

            allRows.addElement(temprow);
            paramnames.addElement(temprow.getParameterName());
        }
    }

    /**
     * Called when the panel is contructed.
     */
    public void init() {
    }

    /**
     * Resets the panel to the values specified in task.
     */
    public void reset() {
        Enumeration enumeration = paramnames.elements();
        String paramname;
        Row row;

        while (enumeration.hasMoreElements()) {
            paramname = (String) enumeration.nextElement();
            row = getRowForParameter(paramname);

            if ((row != null) && (getTask().getParameter(paramname) != null))
                row.setValue((String) getTask().getParameter(paramname));
        }
    }

    /**
     * Called when the panel is finished with.
     */
    public void dispose() {
    }


    /**
     * Gets the row for the specified variable name.
     */
    public Row getRowForParameter(String name) {
        Row r;

        for (int i = 0; i < allRows.size(); ++i) {
            r = (Row) allRows.elementAt(i);

            if (r.getParameterName().equals(name))
                return r;
        }

        return null;
    }


    /**
     * @return a StringVector containing all of the parameters of
     *         each row in a text format.  Each String in the StringVector
     *         represents a row of the GUI interface
     */
    public Vector<String> toStringVector() {
        return allParams;
    }


    /**
     * Gets the row which contains the specified widget.
     */
    public Row getRowForWidget(Component wid) {
        Row r;

        for (int i = 0; i < allRows.size(); ++i) {
            r = (Row) allRows.elementAt(i);

            if (r.containsComponent(wid))
                return r;
        }

        return null;
    }


    /**
     * Gets the row i.
     */
    public Row getRow(int i) {
        return (Row) allRows.elementAt(i);
    }


    /**
     * Sets the parameter with the given name to the given value.
     * This is worked out for the particular widget which the name
     * represents. This is needed by OCL when users may want to
     * set the parameters individually and therefore the OCL
     * OldUnit class must update the user interface also. This
     * function is therefore called everytime the user calls
     * the setParameter function within their unit.
     */
    public void setParameter(String name, String value) {
        if (update) {
            Row r = getRowForParameter(name);

            if ((r != null) && (value != null))
                r.setValue(value);
        }

        update = true; // always reset for next call
    }


    /**
     * For the textfield :-
     */
    public void actionPerformed(ActionEvent e) {
        if (getTask() == null)
            return;

        if (e.getSource() instanceof JComboBox) {
            JComboBox c = (JComboBox) e.getSource();
            update = false;
            Row r = getRowForWidget(c);

            super.setParameter(r.getParameterName(), c.getSelectedItem());
        } else if (e.getSource() instanceof JTextField) {
            JTextField t = (JTextField) e.getSource();
            update = false;
            Row r = getRowForWidget(t);

            super.setParameter(r.getParameterName(), t.getText());
        }
    }


    /**
     * For the Scrolbars :-
     */
    public void stateChanged(ChangeEvent e) {
        if (getTask() == null)
            return;

        Row r = getRowForWidget((Component) e.getSource());
        update = false;

        if (r.getType() == Row.INTSCROLLER)
            super.setParameter(r.getParameterName(), r.getValue());
        else
            super.setParameter(r.getParameterName(), r.getValue());
    }


    /**
     * For the JCheckBoxes :-
     */
    public void itemStateChanged(ItemEvent e) {
        if (getTask() == null)
            return;

        if (e.getItemSelectable() instanceof JCheckBox) {
            JCheckBox c = (JCheckBox) e.getItemSelectable();
            Row r = getRowForWidget(c);
            update = false;

            super.setParameter(r.getParameterName(), String.valueOf(c.isSelected()));
        }

    }

    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        Row r = getRowForWidget((Component) event.getSource());
        update = false;

        super.setParameter(r.getParameterName(), r.getValue());
    }

    /**
     * @return the number of lines of parameters.
     */
    public int getRows() {
        return allRows.size();
    }


    /**
     * Lays out the panel.
     */

    public void layoutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel tmppanel = new JPanel();
        Row r;

        setLayout(new BorderLayout());
        removeAll();

        for (int i = allRows.size() - 1; i >= 0; --i) {
            r = (Row) allRows.elementAt(i);

            panel.add(r, BorderLayout.NORTH);

            if (tmppanel != null)
                panel.add(tmppanel, BorderLayout.CENTER);

            tmppanel = panel;
            panel = new JPanel(new BorderLayout());
        }

        add(tmppanel, BorderLayout.NORTH);
    }


    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    public void parameterUpdated(ParameterUpdateEvent event) {
        Row row = getRowForParameter(event.getParameterName());

        if (row != null)
            row.setValue((String) event.getTask().getParameter(event.getParameterName()));
    }

    public void nodeAdded(TaskNodeEvent event) {
    }

    public void nodeRemoved(TaskNodeEvent event) {
    }


    public static Vector<String> splitLine(String fullline) {
        Vector<String> guilines = new Vector<String>();

        if ((fullline.startsWith("null")) || (fullline.startsWith("["))) {
            int startidx = fullline.indexOf('[');

            while (startidx > -1) {
                guilines.addElement(fullline.substring(startidx + 1, fullline.indexOf(']', startidx)));
                startidx = fullline.indexOf('[', startidx + 1);
            }
        }
        else {
            String[] split = fullline.split("\n");

            for (int count = 0; count < split.length; count++)
                guilines.addElement(split[count]);
        }

        return guilines;
    }

}
