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
package org.trianacode.gui.main.imp;

import org.trianacode.gui.hci.tools.TaskGraphViewManager;
import org.trianacode.gui.hci.tools.UpdateActionConstants;
import org.trianacode.taskgraph.Task;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * MainTrianaTask is a class which creates the a Triana
 * Icon consisting of a set of input nodes and output nodes.
 * <p/>
 * A MainTrianaTask is TrianaTool which is a JPanel, which is a Container,
 * which is a JComponent and therefore has the ability to ommit event to
 * the relevant listeners. MainTrianaTask contains two Vectors (in java.util) to
 * store the pointers to the TrianaNode Objects.  Vector can be increased
 * and decreased in size.
 * <p/>
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @created April 2, 1997
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class MainTrianaTask extends TrianaTask implements MouseListener, ActionListener {

    /**
     * the plus/minus icons
     */
    private PlusMinusIcon addinput = new PlusMinusIcon(true, true);
    private PlusMinusIcon addoutput = new PlusMinusIcon(true, false);
    private PlusMinusIcon removeinput = new PlusMinusIcon(false, true);
    private PlusMinusIcon removeoutput = new PlusMinusIcon(false, false);

    /**
     * a flag indicating whether the tool has been initialised
     */
    private boolean init = false;


    private int id = 0;
    private static int idcount = 0;

    /**
     * Constructs a new MainTrianaTask for viewing the specified task
     */
    public MainTrianaTask(Task task) {
        super(task);

        id = idcount++;

        addMouseListener(this);
    }


    /**
     * Notifies this component that it now has a parent component.
     * When this method is invoked, the chain of parent components is
     * set up with <code>KeyboardAction</code> event listeners.
     *
     * @see #registerKeyboardAction
     */
    public void addNotify() {
        super.addNotify();

        if (!init) {
            initPlusMinusIcons();
            invalidateSize();

            if (getMainComponent() != null)
                getMainComponent().addMouseListener(this);

            init = true;
        }
    }

    /**
     * Adds the little plus/minus node icons to the tool
     */
    private void initPlusMinusIcons() {
        addinput.setVisible(false);
        addoutput.setVisible(false);
        removeinput.setVisible(false);
        removeoutput.setVisible(false);

        addinput.addMouseListener(this);
        addoutput.addMouseListener(this);
        removeinput.addMouseListener(this);
        removeoutput.addMouseListener(this);

        addinput.addActionListener(this);
        addoutput.addActionListener(this);
        removeinput.addActionListener(this);
        removeoutput.addActionListener(this);

        add(addinput, TrianaToolLayout.ADD_INPUT);
        add(addoutput, TrianaToolLayout.ADD_OUTPUT);
        add(removeinput, TrianaToolLayout.REMOVE_INPUT);
        add(removeoutput, TrianaToolLayout.REMOVE_OUTPUT);
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent event) {
        Task task = getTaskInterface();
        Action action = null;

        if (event.getSource() == addinput)
            action = TaskGraphViewManager.getUpdateAction(task, UpdateActionConstants.INCREASE_INPUT_NODES_ACTION);
        else if (event.getSource() == removeinput)
            action = TaskGraphViewManager.getUpdateAction(task, UpdateActionConstants.DECREASE_INPUT_NODES_ACTION);
        else if (event.getSource() == addoutput)
            action = TaskGraphViewManager.getUpdateAction(task, UpdateActionConstants.INCREASE_OUTPUT_NODES_ACTION);
        else if (event.getSource() == removeoutput)
            action = TaskGraphViewManager.getUpdateAction(task, UpdateActionConstants.DECREASE_OUTPUT_NODES_ACTION);

        if (action != null) {
            ActionEvent evt = new ActionEvent(getTaskInterface(), ActionEvent.ACTION_PERFORMED, (String) action.getValue(Action.ACTION_COMMAND_KEY), event.getWhen(), event.getModifiers());
            action.actionPerformed(evt);

            updatePlusMinusVisible();
        }
    }

    public void updatePlusMinusVisible() {
        Task task = getTaskInterface();

        addinput.setVisible(TaskGraphViewManager.isUpdateIcon(task, UpdateActionConstants.INCREASE_INPUT_NODES_ACTION));
        addoutput.setVisible(TaskGraphViewManager.isUpdateIcon(task, UpdateActionConstants.INCREASE_OUTPUT_NODES_ACTION));
        removeinput.setVisible(TaskGraphViewManager.isUpdateIcon(task, UpdateActionConstants.DECREASE_INPUT_NODES_ACTION));
        removeoutput.setVisible(TaskGraphViewManager.isUpdateIcon(task, UpdateActionConstants.DECREASE_OUTPUT_NODES_ACTION));
    }


    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
        updatePlusMinusVisible();
    }

    public void mouseExited(MouseEvent event) {
        addinput.setVisible(false);
        addoutput.setVisible(false);
        removeinput.setVisible(false);
        removeoutput.setVisible(false);
    }

    public void mousePressed(MouseEvent event) {
    }

    public void mouseReleased(MouseEvent event) {
    }


    /**
     * Cleans up the node editor window and parameter componenet if there is one for this unit.
     */
    public void dispose() {
        removeMouseListener(this);

        addinput.removeActionListener(this);
        addoutput.removeActionListener(this);
        removeinput.removeActionListener(this);
        removeoutput.removeActionListener(this);

        if (getTaskInterface() != null)
            getTaskInterface().removeTaskListener(this);

        super.dispose();
    }


    /**
     * Returns a string representation of this component and its values.
     *
     * @return a string representation of this component
     * @since JDK1.0
     */
    public String toString() {
        return String.valueOf(id);
    }


}
