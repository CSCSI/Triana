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
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Wangy
 * Date: 18-Jun-2004
 * Time: 16:21:37
 * To change this template use File | Settings | File Templates.
 */
public class GroupParameterPanel extends ParameterPanel {

    /**
     * An array of the panels displayed by this group
     */
    private ParameterPanel[] panels;

    /**
     * The tabbed pane that holds the individual parameter panels.
     */
    private JTabbedPane paramTabs = new JTabbedPane();


    public GroupParameterPanel(ParameterPanel[] panels) {
        super();
        this.panels = panels;

        initLayout();
    }

    private void initLayout() {
        setLayout(new BorderLayout());

        for (int count = 0; count < panels.length; count++) {
            ParameterPanel paramPanel = panels[count];
            String tabName = "";

            if (paramPanel.getTask() != null) {
                tabName = paramPanel.getTask().getToolName();
            }

            paramPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
            paramTabs.addTab(tabName, paramPanel);
        }

        add(paramTabs, BorderLayout.CENTER);
    }


    /**
     * Sets the component that determines whether parameter changes are committed
     * automatically
     */
    public void setWindowInterface(ParameterWindowInterface comp) {
        super.setWindowInterface(comp);

        for (int count = 0; count < panels.length; count++)
            panels[count].setWindowInterface(comp);
    }


    /**
     * This method is called when the task is set for this panel. It is overridden
     * to create the panel layout.
     */
    public void init() {
    }

    /**
     * This method is called when the panel is reset or cancelled. It should reset
     * all the panels components to the values specified by the associated task,
     * e.g. a component representing a parameter called "noise" should be set to
     * the value returned by a getTool().getParameter("noise") call.
     */
    public void reset() {
        for (int count = 0; count < panels.length; count++)
            panels[count].reset();
    }


    /**
     * This method is called when the panel is finished with. It should dispose
     * of any components (e.g. windows) used by the panel.
     */
    public void dispose() {
    }


    /**
     * Called when the apply button is clicked on the parameter window. Commits
     * any parameter changes.
     */
    public void applyClicked() {
        super.applyClicked();

        for (int count = 0; count < panels.length; count++)
            panels[count].applyClicked();
    }

    /**
     * Called when the cancel button is clicked on the parameter window.
     * Parameter changes are not commited.
     */
    public void cancelClicked() {
        super.cancelClicked();

        for (int count = 0; count < panels.length; count++)
            panels[count].cancelClicked();
    }

    /**
     * Called when the ok button is clicked on the parameter window. Calls
     * applyClicked by default to commit any parameter changes.
     */
    public void okClicked() {
        super.okClicked();

        for (int count = 0; count < panels.length; count++)
            panels[count].applyClicked();
    }

}
