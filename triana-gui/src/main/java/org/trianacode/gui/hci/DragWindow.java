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

import org.trianacode.gui.hci.tools.TaskGraphViewManager;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.imp.TaskFactoryImp;
import org.trianacode.taskgraph.imp.TaskImp;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;

import javax.swing.*;
import java.awt.*;

/**
 * The window which is dragged when a tool is moved
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 11th April 2003
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class DragWindow extends JWindow {

    public static final DragWindow DRAG_WINDOW = new DragWindow();


    private TaskComponent comp;
    private Tool tool;

    public DragWindow() {
        getContentPane().setLayout(new GridLayout(1, 1));
    }


    public void setTool(Tool tool, ToolTable tools) {
        try {
            this.tool = tool;

            if (comp != null) {
                getContentPane().remove(comp.getComponent());
                TaskGraphUtils.disposeTool(comp.getTaskInterface());
            }

            Tool dummy = TaskGraphUtils.createPlaceHolderTool(tool);
            Task task;

            if (dummy instanceof TaskGraph)
                task = ((TaskGraph) dummy);
            else
                task = new TaskImp(dummy, new TaskFactoryImp(), false);

            comp = TaskGraphViewManager.getTaskComponent(task);
            getContentPane().add(comp.getComponent());

            pack();
            validate();
            repaint();
        } catch (TaskException except) {
            except.printStackTrace();
        }
    }


    public Tool getTool() {
        return tool;
    }

}
