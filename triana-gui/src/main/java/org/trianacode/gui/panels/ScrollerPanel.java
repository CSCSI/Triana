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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.trianacode.gui.windows.WindowButtonConstants;


/**
 * This panel allows a scaler or a number to be input. It is used in many parameter editing wqindows within Triana and
 * consists of a Slider, and three textfields, one to display the value of the slider and the other two to display the
 * minimum and maximum values of the slider.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public class ScrollerPanel extends UnitPanel implements ChangeListener,
        ActionListener, FocusListener {

//    static double MAX_INT = 100000000; // Large int for scaling

    public static int DECIMAL_PLACES = 3;

    private double min = -1; // always force a setValues

    private double max = 99999;

//    double doubleScaler;


    /**
     * Represents value of the scroller as a double-precision floating-point number.
     */

    private double sliderValue;


    /**
     * The scrollbar.
     */

    private JSlider slider = new JSlider();


    /**
     * The display used to display the scrollbar's value.
     */

    private JTextField display = new JTextField(10);


    /**
     * The name of the parameter that you wish to change.
     */

    private String title;


    /**
     * Parameter name
     */

    private String paramName = "current";

    /**
     * Title label
     */
    private JLabel titlelabel;


    /**
     * @return false so that parameter changes are not committed automatically
     */
    public boolean isAutoCommitByDefault() {
        return false;
    }


    /**
     * Overrides UnitPanel method to return WindowConstans.OK_CANCEl_APPLY_BUTTONS.
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS;
    }


    public void setObject(Object unit) {
        super.setObject(unit);
        layoutPanel();

        reset();
    }


    public void setTextForUser(String text) {
        title = text;
        titlelabel.setText(text);
    }

    public void addActionListeners() {
        slider.addChangeListener(this);
        display.addActionListener(this);
        display.addFocusListener(this);
    }


    /**
     * @return the title of this scroller panel.
     */
    public String getTitle() {
        return title;
    }


    /**
     * Set the minimum, maximum and current scrollbar's values
     */

    public void setValues(double min, double max, double cur) {
        sliderValue = cur;

        min = Math.min(min, cur);
        max = Math.max(max, cur);

        if ((this.min != min) || (this.max != max)) {
            this.min = min;
            this.max = max;
        }

        updateWidgets();
    }


    public void setValue(double newval) {
        setValues(min, max, newval);
    }


    public void setMin(double newmin) {
        setValues(newmin, max, Double.parseDouble(display.getText()));
    }


    public void setMax(double newmax) {
        setValues(min, newmax, Double.parseDouble(display.getText()));
    }


    /**
     * Sets the new name for the parameter which the slider controls
     */
    public void setParameterName(String newname) {
        paramName = newname;
    }

    /**
     * @return the name of the parameter which the slider controls
     */
    public String getParameterName() {
        return paramName;
    }


    public void updateWidgets() {
//        doubleScaler = MAX_INT / (max - min);

        slider.removeChangeListener(this);
        slider.setValue((int) (((sliderValue - min) / (max - min)) * 100));
        slider.addChangeListener(this);

        updateDisplay();
        setParameter(paramName, Double.parseDouble(display.getText()));

//        slider.setValue((int) (sliderValue * doubleScaler));
//        slider.setMaximum((int) (max * doubleScaler));
//        slider.setMinimum((int) (min * doubleScaler));

/*        int val;

        val = (int) (this.max / 10.0);
            if (val == 0) val = 1;

        slider.setMajorTickSpacing(val);
        val = (int) (this.max / 100.0);

        if (val == 0) val = 1;
            slider.setMajorTickSpacing(val); */

    }

    private void updateDisplay() {
        long pow = Math.round(Math.pow(10, DECIMAL_PLACES));
        long slidertempval = Math.round(sliderValue * pow);
        String minus = "";

        if (slidertempval < 0) {
            slidertempval = Math.abs(slidertempval);
            minus = "-";
        }

        if (DECIMAL_PLACES > 0) {
            String remain = String.valueOf(slidertempval % pow);

            while (remain.length() < DECIMAL_PLACES) {
                remain = "0" + remain;
            }

            display.setText(minus + String.valueOf(slidertempval / pow) + '.' + remain);
        } else {
            display.setText(minus + String.valueOf(slidertempval / pow));
        }
    }


    /**
     * returns the current value of the parameter
     */

    public double getValue() {
        return Double.parseDouble(display.getText());
    }


    /**
     * returns the min value of the slider
     */

    public double getMin() {
        return min;
    }


    /**
     * returns the max value of the slider
     */

    public double getMax() {
        return max;
    }


    /**
     * Does a layout on the Scroller Window.
     */

    protected void layoutPanel() {
//        MAX_INT = 100;

        slider.setMinimum(0);
        slider.setMaximum(100);


        titlelabel = new JLabel(title, JLabel.CENTER);
//        minValue = new JTextField();
//        maxValue = new JTextField();

        addActionListeners();
        setValues(1, 1000, 1);
//        doubleScler = MAX_INT / (max - min);

        // create new panel
        setLayout(new BorderLayout());

        JPanel subPanel = new JPanel(new BorderLayout());
        subPanel.add(titlelabel, BorderLayout.NORTH);
        subPanel.add(slider, BorderLayout.CENTER);
        subPanel.add(display, BorderLayout.EAST);

        add(subPanel, BorderLayout.NORTH);
    }


    /**
     * Called when the ok button is clicked on the parameter window. Commits any parameter changes.
     */
    public void okClicked() {
        setParameter(paramName, String.valueOf(sliderValue));

        super.okClicked();
    }

    /**
     * Called when the apply button is clicked on the parameter window. Commits any parameter changes.
     */
    public void applyClicked() {
        setParameter(paramName, String.valueOf(sliderValue));

        super.applyClicked();
    }


    /**
     * Resets the components in the panel to those specified by the task.
     */
    public void reset() {
        super.reset();

        if ((getTask() != null) && (getTask().isParameterName(paramName))) {
            setValue(Double.parseDouble((String) getTask().getParameter(paramName)));
        }

        updateWidgets();
    }


    /**
     * Invoked when the value of the adjustable has changed.
     */

    public void stateChanged(ChangeEvent e) {
        //   sliderValue = slider.getValue() / doubleScaler;
        if (e.getSource() == slider) {
            double value = (((double) slider.getValue()) / 100) * (max - min) + min;
            setValues(min, max, value);
            setParameter(paramName, display.getText());
        }
    }


    /**
     * Invoked when an action occurs.
     */

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == display) {
            double value = Double.parseDouble(display.getText());
            setValues(min, max, value);
            setParameter(paramName, display.getText());
        }


/*        if (e.getSource() == minValue) {

            setValues(value, max, sliderValue);

            slider.setMinimum((int) (value * doubleScaler));

            setParameter("minimum", str);

        }


        if (e.getSource() == maxValue) {

            setValues(min, value, sliderValue);

            slider.setMaximum((int) (value * doubleScaler));

            setParameter("maximum", str);

        }   */

    }

    public void focusLost(FocusEvent event) {
        if (event.getSource() == display) {
            double value = Double.parseDouble(display.getText());
            setValues(min, max, value);
        }
    }

    public void focusGained(FocusEvent event) {
    }

}













