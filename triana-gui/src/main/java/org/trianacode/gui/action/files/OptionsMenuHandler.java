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

import org.trianacode.gui.hci.OptionsHandler;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;
import org.trianacode.util.UserPropertyEvent;
import org.trianacode.util.UserPropertyListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * Class that handles events and actions for the Tools menu, including New Tool, Compile
 * and Edit Tool Box Paths.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 * @created May 13, 2003: 4:45:14 PM
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class OptionsMenuHandler implements ActionListener, ItemListener, UserPropertyListener {

    private ToolTable tools;

    /**
     * an array of the debug window listener abstract buttons
     */
    private ArrayList debuglisteners = new ArrayList();


    public OptionsMenuHandler(ToolTable tools) {
        this.tools = tools;
        Env.addUserPropertyListener(this);
    }


    /**
     * Adds a abstract button to monitor the visibility of the debug window
     */
    public void addMonitorDebugWindow(AbstractButton button) {
        if (!debuglisteners.contains(button)) {
            debuglisteners.add(button);
            button.setSelected(Env.isDebugVisible());
        }
    }

    /**
     * Adds a abstract button to monitor the visibility of the debug window
     */
    public void removeMonitorDebugWindow(AbstractButton button) {
        debuglisteners.remove(button);
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        String label = e.getActionCommand();
        if (label.equals(Env.getString("TrianaOptionTitle")))
            handleOptions();
    }

    /**
     * Invoked when an item has been selected or deselected by the user.
     * The code written for this method performs the operations
     * that need to occur when an item is selected (or deselected).
     */
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() instanceof AbstractButton) {
            String label = ((AbstractButton) e.getItem()).getText();

            if (label.equals(Env.getString("DebugWindow")))
                Env.setDebugVisible(e.getStateChange() == ItemEvent.SELECTED);
        }
    }


    /**
     * display the options dialog
     */
    private void handleOptions() {
        new OptionsHandler(tools);
    }


    /**
     * Notified when a user property changes
     */
    public void userPropertyChanged(UserPropertyEvent event) {
        if (event.getPropertyName().equals(Env.DEBUG_VISIBLE_STR)) {
            AbstractButton[] copy = (AbstractButton[]) debuglisteners.toArray(new AbstractButton[debuglisteners.size()]);

            for (int count = 0; count < copy.length; count++)
                copy[count].setSelected(Env.isDebugVisible());
        }
    }

}
