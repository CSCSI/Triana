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
package common.input;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.util.FileUtils;

/**
 * User interface panel for the sequence buffer component, allows normal play/stop fwd/rev functions plus a slider
 * indicating the number of data items in the sequence and current item.
 *
 * @author Matthew Shields
 * @version $Revision: 2921 $
 */
public class SequenceBufferPanel extends ParameterPanel
        implements ActionListener, ChangeListener, PropertyChangeListener {

    public static final String PLAY = "play";
    public static final String STOP = "stop";
    public static final String REWIND = "rewind";
    public static final String FORWARD = "fast forward";
    public static final String PAUSE = "pause";
    public static final String STEPFWD = "step forward";
    public static final String STEPBACK = "step back";
    public static final String RESET = "reset";

    public static final String CURRENT = "sequenceCurrent";
    public static final String STATE = "executionState";

    private DefaultBoundedRangeModel model = new DefaultBoundedRangeModel(0, 0, 0, 0);

    private JToolBar controlBar = null;
    private JSlider sequenceSlider = null;
    private JFormattedTextField textField;

    public SequenceBufferPanel() {
        super();
    }

    /**
     * This method is called when the task is set for this panel. It is overridden to create the panel layout.
     */
    public void init() {
        this.setLayout(new BorderLayout());
        initPanel();
        getTask().addTaskListener(this);
    }

    private void initPanel() {
        controlBar = new JToolBar("Sequence Buffer Control", JToolBar.HORIZONTAL);
        controlBar.setFloatable(false);
        controlBar.setBorderPainted(true);
        controlBar.setMargin(new Insets(0, 0, 0, 0));

        JButton button;
        button = new JButton(getIcon("Rewind24.gif"));
        button.setToolTipText(REWIND);
        button.setActionCommand(REWIND);
        controlBar.add(button);
        button.addActionListener(this);

        button = new JButton(getIcon("StepBack24.gif"));
        button.setToolTipText(STEPBACK);
        button.setActionCommand(STEPBACK);
        controlBar.add(button);
        button.addActionListener(this);

        button = new JButton(getIcon("Play24.gif"));
        button.setToolTipText(PLAY);
        button.setActionCommand(PLAY);
        controlBar.add(button);
        button.addActionListener(this);

        button = new JButton(getIcon("Pause24.gif"));
        button.setToolTipText(PAUSE);
        button.setActionCommand(PAUSE);
        controlBar.add(button);
        button.addActionListener(this);

        button = new JButton(getIcon("Stop24.gif"));
        button.setToolTipText(STOP);
        button.setActionCommand(STOP);
        controlBar.add(button);
        button.addActionListener(this);

        button = new JButton(getIcon("StepForward24.gif"));
        button.setToolTipText(STEPFWD);
        button.setActionCommand(STEPFWD);
        controlBar.add(button);
        button.addActionListener(this);

        button = new JButton(getIcon("FastForward24.gif"));
        button.setToolTipText(FORWARD);
        button.setActionCommand(FORWARD);
        controlBar.add(button);
        button.addActionListener(this);
        this.add(controlBar, BorderLayout.SOUTH);

        sequenceSlider = new JSlider(model);
        sequenceSlider.addChangeListener(this);
        sequenceSlider.setMinorTickSpacing(1);
        sequenceSlider.setPaintTicks(true);
        this.add(sequenceSlider, BorderLayout.CENTER);

        JPanel inner = new JPanel();
        inner.add(new JLabel("Current Frame in Sequence"));
        //Create the formatted text field and its formatter.
        java.text.NumberFormat numberFormat =
                java.text.NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(numberFormat);
        formatter.setMinimum(new Integer(Integer.MIN_VALUE));
        formatter.setMaximum(new Integer(Integer.MAX_VALUE));
        textField = new JFormattedTextField(formatter);
        textField.setValue(new Integer(0));
        textField.setColumns(5); //get some space
        textField.addPropertyChangeListener(this);

        //React when the user presses Enter.
        textField.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER, 0),
                "check");
        textField.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!textField.isEditValid()) { //The text is invalid.
                    Toolkit.getDefaultToolkit().beep();
                    textField.selectAll();
                } else {
                    try {                    //The text is valid,
                        textField.commitEdit();     //so use it.
                    }
                    catch (java.text.ParseException exc) {
                    }
                }
            }
        });
        inner.add(textField);
        add(inner, BorderLayout.NORTH);

    }

    /**
     * This method is called when the panel is reset or cancelled. It should reset all the panels components to the
     * values specified by the associated task, e.g. a component representing a parameter called "noise" should be set
     * to the value returned by a getTask().getParameter("noise") call.
     */
    public void reset() {
    }

    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals(CURRENT)) {
            model.setValueIsAdjusting(true);
            int newVal = Integer.parseInt((String) value);
            if (newVal > model.getMaximum()) {
                model.setMaximum(newVal);
            }
            model.setValue(newVal);
            model.setValueIsAdjusting(false);
        } else if (paramname.equals(RESET)) {
            model.setValueIsAdjusting(true);
            model.setMaximum(0);
            model.setValueIsAdjusting(false);
        }
    }

    /**
     * This method is called when the panel is finished with. It should dispose of any components (e.g. windows) used by
     * the panel.
     */
    public void dispose() {
        getTask().removeTaskListener(this);
    }

    /**
     * This method returns false by default. It should be overridden if the panel wants parameter changes to be commited
     * automatically
     */
    public boolean isAutoCommitByDefault() {
        return true;
    }

    /**
     * This method returns WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS by default. It should be overridden if the
     * panel has different preferred set of buttons.
     *
     * @return the panels preferred button combination (as defined in Windows Constants).
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_BUTTON;
    }

    /**
     * This method returns true by default. It should be overridden if the panel does not want the user to be able to
     * change the auto commit state
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    private ImageIcon getIcon(String file) {
        return FileUtils.getSystemImageIcon("media" +
                File.separator + file);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if (action.equals("")) {
            // do nothing catch
        } else if (action.equals(REWIND)) {
            setParameter(STATE, STOP);
            setParameter(CURRENT, "0");
        } else if (action.equals(FORWARD)) {
            setParameter(STATE, STOP);
            setParameter(CURRENT, Integer.toString(model.getMaximum()));
        } else if (action.equals(STEPBACK)) {
            setParameter(STATE, STOP);
            if (model.getValue() > model.getMinimum()) {
                setParameter(CURRENT, Integer.toString(model.getValue() - 1));
            }
        } else if (action.equals(STEPFWD)) {
            getTask().setParameter(STATE, STOP);
            if (model.getValue() < model.getMaximum()) {
                setParameter(CURRENT, Integer.toString(model.getValue() + 1));
            }
        } else {
            setParameter(STATE, action);
        }

    }


    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        int current = source.getValue();
        if (!sequenceSlider.getValueIsAdjusting()) {
            textField.setValue(new Integer(current));
        } else {
            textField.setText(String.valueOf(current));
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("value".equals(evt.getPropertyName())) {
            Number value = (Number) evt.getNewValue();
            if (value != null) {
                setParameter(CURRENT, value.toString());
            }
        }
    }

}
