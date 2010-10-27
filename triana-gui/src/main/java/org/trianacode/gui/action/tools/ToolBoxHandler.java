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
package org.trianacode.gui.action.tools;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.panels.ToolBoxPanel;
import org.trianacode.gui.panels.UnknownToolBoxTypePanel;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.tool.FileToolboxLoader;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Handler class for displaying the toolbox path panel
 *
 * @author Matthew Shields
 * @version $Revsion$
 */
public class ToolBoxHandler implements WindowListener {

    private ParameterWindow paramwin;
    private String type;


    /**
     * Invoked the first time a window is made visible.
     */
    public void windowOpened(WindowEvent e) {
    }

    /**
     * Invoked when the user attempts to close the window from the window's system menu.  If the program does not
     * explicitly hide or dispose the window while processing this event, the window close operation will be cancelled.
     */
    public void windowClosing(WindowEvent e) {
    }

    /**
     * Invoked when a window has been closed as the result of calling dispose on the window.
     */
    public void windowClosed(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a normal to a minimized state. For many platforms, a minimized window is
     * displayed as the icon specified in the window's iconImage property.
     *
     * @see Frame#setIconImage
     */
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * Invoked when the Window is set to be the active Window. Only a Frame or a Dialog can be the active Window. The
     * native windowing system may denote the active Window or its children with special decorations, such as a
     * highlighted title bar. The active Window is always either the focused Window, or the first Frame or Dialog that
     * is an owner of the focused Window.
     */
    public void windowActivated(WindowEvent e) {
    }

    /**
     * Invoked when a Window is no longer the active Window. Only a Frame or a Dialog can be the active Window. The
     * native windowing system may denote the active Window or its children with special decorations, such as a
     * highlighted title bar. The active Window is always either the focused Window, or the first Frame or Dialog that
     * is an owner of the focused Window.
     */
    public void windowDeactivated(WindowEvent e) {
    }

    public ToolBoxHandler(ToolTable tools, String type) {
        ParameterPanel panel;
        if (type.equals(FileToolboxLoader.LOCAL_TYPE)) {
            panel = new ToolBoxPanel(tools);
        } else {
            panel = new UnknownToolBoxTypePanel(tools, type);
        }
        panel.init();

        paramwin = new ParameterWindow(GUIEnv.getApplicationFrame(), WindowButtonConstants.OK_BUTTON, false);
        paramwin.setTitle(Env.getString("editToolBoxPaths"));
        paramwin.setParameterPanel(panel);
        paramwin.addWindowListener(this);


        paramwin.setLocation((paramwin.getToolkit().getScreenSize().width / 2) - (paramwin.getSize().width / 2),
                (paramwin.getToolkit().getScreenSize().height / 2) - (paramwin.getSize().height / 2));

        paramwin.setVisible(true);

    }

}
