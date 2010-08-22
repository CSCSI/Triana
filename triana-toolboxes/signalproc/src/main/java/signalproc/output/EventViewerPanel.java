/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2003 University of Wales, Cardiff. All rights reserved.
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
 */
package signalproc.output;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;


/**
 * Used by SGTGrapher to plot graphs on the client side.
 *
 * @author david Churches
 * @version $Revision: 2921 $
 */
public class EventViewerPanel extends ParameterPanel
        implements TaskListener, FocusListener, ItemListener, ActionListener, CaretListener {


    private JTextField textField;
    private JTextArea textArea;

    /**
     * Creates a new EventViewerPanel.
     */
    public EventViewerPanel() {
        super();
    }


    /**
     * @return false so that the auto commit box is not shown
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    /**
     * Overridden to return WindowButtonConstants.OK_BUTTON only.
     public byte getPreferredButtons() {
     return WindowButtonConstants.OK_BUTTON;
     }
     */

    /**
     * Overridden to return false, suggesting that the panel prefers to be allowed to be hidden behind the main Triana
     * window.
     */
    public boolean isAlwaysOnTopPreferred() {
        return false;
    }

    /**
     * Initialises the panel.
     */
    public void init() {
        initPanel();
        getTask().addTaskListener(this);
    }

    /**
     * A empty method as the graph is updated through its task listener interface.
     */
    public void run() {
    }

    /**
     * A empty method as the graph is updated through its task listener interface.
     */
    public void reset() {
        if (getParameter("textString") != null) {
            System.out.println(
                    "EventViewerPanel recieved textString= " + (String) getParameter("textString") + " from reset()");
            textField.setText((String) getParameter("textString"));
        }
    }

    /**
     * Disposes of the graph window and removes this panel as a task listener.
     */
    public void dispose() {
        getTask().removeTaskListener(this);
    }


    private void initPanel() {

        JPanel mainPanel = new JPanel(new BorderLayout());

        textArea = new JTextArea(20, 50);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.addCaretListener(this);

        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        textField = new JTextField(20);
        textField.addFocusListener(this);

        JPanel textFieldPanel = new JPanel(new GridLayout(1, 1));
        textFieldPanel.add(new JLabel("text String"));
        textFieldPanel.add(textField);
        textFieldPanel.setBorder(new EmptyBorder(0, 0, 0, 3));

        mainPanel.add(textAreaPanel, BorderLayout.NORTH);
        mainPanel.add(textFieldPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

    }


    public void caretUpdate(CaretEvent e) {
    }

    public void actionPerformed(ActionEvent ev) {
    }

    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        if (event.getSource() == textField) {
            setParameter("textString", textField.getText());
        }
    }


    public void itemStateChanged(ItemEvent ev) {
    }

    /**
     * Updates the graph when the SGTGraphData parameter is changed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
        String paramname = event.getParameterName();
        Task task = event.getTask();

        if (paramname.equals("textAreaString")) {
            String paramstr = (String) task.getParameter(paramname);
            textArea.append(paramstr);
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }


    public void nodeAdded(TaskNodeEvent event) {
    }

    public void nodeRemoved(TaskNodeEvent event) {
    }


}
