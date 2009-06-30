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

import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.util.Env;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Action class to handle all "open" actions.
 *
 * @author  Matthew Shields
 * @created May 2, 2003: 3:49:12 PM
 * @version $Revision: 4048 $
 * @date    $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class OpenAction extends AbstractAction implements ActionDisplayOptions {

    public static final int FILE_OR_GROUP_MODE = 0;
    public static final int FILE_ONLY_MODE = 1;

    private ToolSelectionHandler selectionHandler;
    private int mode;

    public OpenAction(ToolSelectionHandler selhandler) {
        this(selhandler, FILE_OR_GROUP_MODE);
    }

    public OpenAction(ToolSelectionHandler selhandler, int mode) {
        this.selectionHandler = selhandler;
        this.mode = mode;
        putValue(SHORT_DESCRIPTION, Env.getString("Open"));
        putValue(ACTION_COMMAND_KEY, Env.getString("Open"));
        putValue(SMALL_ICON, GUIEnv.getIcon("open.png"));
        putValue(NAME, Env.getString("Open"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        if ((mode == FILE_OR_GROUP_MODE) && selectionHandler.isSingleSelectedTool() && (selectionHandler.getSelectedTool() instanceof TaskGraph)) {
            TaskGraph taskgraph = (TaskGraph) selectionHandler.getSelectedTool();

            if (GUIEnv.getTaskGraphPanelFor(taskgraph) != null) {
                TaskGraphPanel panel = GUIEnv.getTaskGraphPanelFor(taskgraph);
                GUIEnv.getApplicationFrame().getInternalFrameFor(panel).toFront();
                GUIEnv.getApplicationFrame().getInternalFrameFor(panel).requestFocus();
            } else if (taskgraph.getParent() == null)
                GUIEnv.getApplicationFrame().addParentTaskGraphPanel(taskgraph);
            else
                GUIEnv.getApplicationFrame().addChildTaskGraphPanel(taskgraph, GUIEnv.getTrianaClientFor(taskgraph));
        } else
            TaskGraphFileHandler.open();
    }

}
