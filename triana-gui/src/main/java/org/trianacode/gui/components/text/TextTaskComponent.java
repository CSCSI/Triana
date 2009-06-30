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

package org.trianacode.gui.components.text;

import org.trianacode.gui.components.common.CircleNode;
import org.trianacode.gui.main.NodeComponent;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.main.imp.TextSubComponent;
import org.trianacode.gui.main.imp.TrianaToolLayout;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.event.*;

import javax.swing.*;
import java.awt.*;

/**
 * A task component that displays a number on the main triana workspace
 *
 * @author      Ian Wang
 * @created     18th July 2004
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $

 */

public class TextTaskComponent extends JPanel implements TaskComponent, TaskListener, SwingConstants {

    // the task this component represents
    private Task task;

    // the parameter containing the text to be displayed
    private String param;

    // a flag indicating whether the component is selected
    private boolean selected;

    // the main text component
    private TextSubComponent textcomp;


    /**
     * Constructs a text task component
     * @param task the task being represented
     * @param param the param name containing the text to be displayed
     */
    public TextTaskComponent(Task task, String param) {
        this(task, param, LEFT);
    }

    /**
     * Constructs a text task component
     * @param task the task being represented
     * @param param the param name containing the text to be displayed
     * @param align the text alignment (LEFT, RIGHT, CENTER)
     */
    public TextTaskComponent(Task task, String param, int align) {
        this.task = task;
        this.param = param;

        task.addTaskListener(this);
        initComponent(align);
    }

    /**
     * Add the node components to the layout
     */
    private void initComponent(int align) {
        setOpaque(false);
        setLayout(new TrianaToolLayout(0, 0, 0, 5));

        textcomp = new TextSubComponent(this);
        textcomp.setHorizontalAlignment(align);
        add(textcomp, TrianaToolLayout.MAIN);

        Node[] nodes = task.getInputNodes();
        for(int count = 0; count < nodes.length; count++)
            add(new CircleNode(nodes[count]), TrianaToolLayout.INPUT_NODE);

        nodes = task.getOutputNodes();
        for (int count = 0; count < nodes.length; count++)
            add(new CircleNode(nodes[count]), TrianaToolLayout.OUTPUT_NODE);

        if (task.isParameterName(param))
            textcomp.setText(task.getParameter(param).toString());
    }

    /**
     * @return this main task component
     */
    public Component getComponent() {
        return this;
    }

    /**
     * @return the node component for ths specified node
     */
    public NodeComponent getNodeComponent(Node node) {
        Component[] comps = getComponents();
        for (int count = 0; count < comps.length; count++)
            if ((comps[count] instanceof NodeComponent) && (((NodeComponent) comps[count]).getNode() == node))
                return (NodeComponent) comps[count];

        return null;
    }

    /**
     * @return the task for this component
     */
    public Task getTaskInterface() {
        return task;
    }

    /**
     * Returns true if the particular component is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the component as selected
     */
    public void setSelected(boolean state) {
        if (selected != state) {
            selected = state;
            repaint();
        }
    }


    private void invalidateAndRepaint() {
        invalidate();

        if (getParent() != null) {
            getParent().validate();
            getParent().repaint();
        }
    }


    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
        if (event.getParameterName().equals(param)) {
            if (event.getNewValue() != null)
                textcomp.setText(event.getNewValue().toString());
            else
                textcomp.setText("");

            invalidateAndRepaint();
        }
    }

    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
        if (event.getNode().isInputNode())
            add(new CircleNode(event.getNode()), TrianaToolLayout.INPUT_NODE);
        else
            add(new CircleNode(event.getNode()), TrianaToolLayout.OUTPUT_NODE);

        invalidateAndRepaint();
    }

    /**
     * Called before a data input node is removed.
     */
    public void nodeRemoved(TaskNodeEvent event) {
        Component[] comps = getComponents();
        for (int count = 0; count < comps.length; count++)
            if ((comps[count] instanceof NodeComponent) && (((NodeComponent) comps[count]).getNode() == event.getNode()))
                remove(comps[count]);

        invalidateAndRepaint();
    }

    /**
     * Called when the core properties of a task change i.e. its name, whether it is running continuously etc.
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
    }

    /**
     * Dispose of this task component
     */
    public void dispose() {
        removeAll();
        task.removeTaskListener(this);
    }

}
