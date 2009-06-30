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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * UI Panel that provides a mechanism for displaying lists and allowing the user to
 * select multiple items. There are two list panels, one that contains the entire set of
 * items and the other that contains the selected items. The user selects items and then
 * hits a button to move them from one list to the other.
 *
 * @author      Matthew Shields
 * @created     Dec 5, 2002; 11:50:20 AM
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class ListSelecterPanel extends JPanel implements ActionListener {

    public static String NONE_STRING = "<-- None -->";

    private boolean sorted = false;
    private JList allItems;
    private JList includedItems;
    private JButton include;
    private JButton exclude;

    public ListSelecterPanel() {
        super();
        initGUI();
    }

    public ListSelecterPanel(boolean keepSorted) {
        this();
        sorted = keepSorted;
    }

    private void initGUI() {
        this.setLayout(new BorderLayout());

        allItems = new JList();
        allItems.setPrototypeCellValue("12345678901234567890");
        allItems.setVisibleRowCount(8);
        JScrollPane scroller = new JScrollPane(allItems);
        add(scroller, BorderLayout.WEST);

        includedItems = new JList(new String[]{NONE_STRING});
        includedItems.setPrototypeCellValue("12345678901234567890");
        includedItems.setVisibleRowCount(8);
        scroller = new JScrollPane(includedItems);
        add(scroller, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        JPanel incPanel = new JPanel();
        JPanel incCont = new JPanel(new BorderLayout());
        JPanel excPanel = new JPanel();
        JPanel excCont = new JPanel(new BorderLayout());
        include = new JButton(">>");
        include.addActionListener(this);
        exclude = new JButton("<<");
        exclude.addActionListener(this);
        incPanel.add(include);
        incCont.add(incPanel, BorderLayout.SOUTH);
        excPanel.add(exclude);
        excCont.add(excPanel, BorderLayout.NORTH);
        buttonPanel.add(incCont);
        buttonPanel.add(excCont);
        add(buttonPanel, BorderLayout.CENTER);
        setToolTipText("Select items in the LHS or move them to the RHS");
    }


    /**
     * Set the list data for the left hand side set list.
     */
    public void setListData(Object[] items) {
        allItems.setListData(items);
        if (sorted)
            sortLHS();
    }

    /**
     * Returns an array of the items in the included right hand set.
     */
    public Object[] getSelectedItems() {
        Object[] result = new Object[includedItems.getModel().getSize()];
        for (int i = 0; i < includedItems.getModel().getSize(); i++) {
            result[i] = includedItems.getModel().getElementAt(i);
        }
        if ((result.length == 1) && (result[0].equals(NONE_STRING))) {
            result = new String[0];
        }
        return result;
    }

    /**
     * Returns an array of the items in the included right hand set. If
     * no items are the included set and default to selected is specified
     * then the selected items in the excluded left hand set are returned
     * instead.
     *
     * @param defaultselected if true and no items are in the included list
     * then the selected items in the excluded set are returned
     *
     */
    public Object[] getSelectedItems(boolean defaultselected) {
        Object[] result = getSelectedItems();

        if ((result.length == 0) && defaultselected)
            result = allItems.getSelectedValues();

        if ((result.length == 1) && (result[0].equals(NONE_STRING))) {
            result = new String[0];
        }

        return result;
    }

    /**
     * Returns an array of the items in the excluded left hand set.
     */
    public Object[] getExcludedItems() {
        Object[] result = new Object[allItems.getModel().getSize()];
        for (int i = 0; i < allItems.getModel().getSize(); i++)
            result[i] = allItems.getModel().getElementAt(i);

        if ((result.length == 1) && (result[0].equals(NONE_STRING))) {
            result = new String[0];
        }

        return result;
    }


    /**
     * Sort the lists.
     */
    public void sort() {
        sortLHS();
        sortRHS();
    }

    private void sortLHS() {
        Object[] exc = getExcludedItems();
        Arrays.sort(exc);
        allItems.setListData(exc);
    }

    private void sortRHS() {
        Object[] inc = getSelectedItems();
        Arrays.sort(inc);
        includedItems.setListData(inc);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        Object[] selected;
        if (e.getSource() == include) {
            selected = allItems.getSelectedValues();
            includedItems.setListData(addItems(selected, getSelectedItems()));
            allItems.setListData(removeItems(getExcludedItems(), selected));
        }
        else if (e.getSource() == exclude) {
            selected = includedItems.getSelectedValues();
            allItems.setListData(addItems(selected, getExcludedItems()));
            includedItems.setListData(removeItems(getSelectedItems(), selected));
        }

        if (sorted) {
            sort();
        }

        if (allItems.getModel().getSize() == 0)
            allItems.setListData(new String[]{NONE_STRING});

        if (includedItems.getModel().getSize() == 0)
            includedItems.setListData(new String[]{NONE_STRING});
    }

    /**
     * Return an new array which is the union of the two inputs
     */
    static Object[] addItems(Object[] set, Object[] add) {
        Object[] result = new Object[set.length + add.length];
        System.arraycopy(set, 0, result, 0, set.length);
        System.arraycopy(add, 0, result, set.length, add.length);
        return result;
    }

    /**
     * return a new array of items in set that are not in remove.
     */
    static Object[] removeItems(Object[] set, Object[] remove) {
        ArrayList result = new ArrayList();
        for (int setInd = 0; setInd < set.length; setInd++) {
            boolean found = false;
            for (int remInd = 0; remInd < remove.length; remInd++) {
                if (set[setInd].equals(remove[remInd])) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(set[setInd]);
            }
        }
        return result.toArray(new Object[result.size()]);
    }
}
