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
package org.trianacode.gui.hci;

import org.trianacode.gui.hci.tools.ToolTreeModel;
import org.trianacode.gui.hci.tools.filters.*;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A ToolBar for searching and filtering of units in the unit tree view
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 * @created Nov 15, 2002; 4:43:06 PM
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class SearchToolBar extends JToolBar implements ActionListener, FocusListener {

    private JComboBox searchField;

    /**
     * An array of the filters in the toolbar
     */
    public static ToolFilter[] filters = {new AllPackagesFilter(),
            new SubPackageFilter(),
            new AllToolsFilter(),
            new InputToolsFilter(),
            new OutputToolsFilter(),
            new DataTypeFilter()};


    /**
     * The model that manages the tool tree
     */
    private ToolTreeModel model;

    /**
     * The tree this tool bar is controlling
     */
    private JTree tree;

    /**
     * A string representing the last selected filter (required to stop a
     * second event that is generated refiltering)
     */
    private String filtertxt = "";


    public SearchToolBar(String title, JTree tree, ToolTreeModel model) {
        super(title, HORIZONTAL);

        this.model = model;
        this.tree = tree;

        setFloatable(true);
        setBorderPainted(true);
        setMargin(new Insets(0, 0, 0, 0));
        createWidgets();
    }

    /**
     * Set up the ToolBar contents
     */
    private void createWidgets() {
        JPanel searchpanel = new JPanel(new BorderLayout());
        searchField = new JComboBox(filters);
        searchField.setPrototypeDisplayValue("01234567890123456");
        searchField.setEditable(true);
        searchField.setSelectedIndex(0);
        searchField.addActionListener(this);
        searchField.addFocusListener(this);
        searchField.setToolTipText("Enter search string");
        searchpanel.add(searchField, BorderLayout.SOUTH);
        add(searchpanel);
        initFilter();
    }


    /**
     * Adds a filter to the search toolbar
     */
    public void addFilter(ToolFilter filter) {
        searchField.addItem(filter);
    }

    /**
     * Removes a filter from the search toolbar
     */
    public void removeFilter(ToolFilter filter) {
        searchField.removeItem(filter);
    }

    /**
     * Initialises a new tool filter
     */
    private void initFilter() {
        Object item = searchField.getSelectedItem();

        if (!filtertxt.equals(item)) {
            filtertxt = item.toString();

            if (item instanceof ToolFilter)
                model.setToolFilter((ToolFilter) item);
            else if (item instanceof String)
                model.setToolFilter(new SearchFilter((String) item));

            tree.expandPath(new TreePath(model.getRoot()));
        }
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchField)
            initFilter();
    }


    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        if (event.getSource() == searchField)
            initFilter();
    }


}
