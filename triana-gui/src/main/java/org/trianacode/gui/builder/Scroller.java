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
package org.trianacode.gui.builder;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This implements a scroller which works on floating point values
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public class Scroller implements ChangeListener, FocusListener {

    public static int DECIMAL_PLACES = 3;
//    public static double MAX_INT = 10000; // Large int for scaling

    double min = -1; // always force a setValues
    double max = 99999;
//    double doubleScaler;

    JSlider slider;
    int type;

    /**
     * The display used to display the scrollbar's value.
     */
    public JTextFieldHack display;
    public JTextField minValue;
    public JTextField maxValue;
    public JButton set;

    public static int INTEGER = 0;
    public static int FLOAT = 1;

    /**
     * Represents value of the scroller as a double-precision floating-point number.
     */
    public double value;

    /**
     * Creates a Row of widgets
     */
    public Scroller(int type, double min, double max, double cur) {
        this.type = type;
        display = new JTextFieldHack(10);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.addFocusListener(this);

        minValue = new JTextField(10);
//        minValue.addActionListener(this);

        maxValue = new JTextField(10);
//        maxValue.addActionListener(this);

        set = new JButton("Set");
//        set.addActionListener(this);

        slider = new JSlider();
        slider.setMinimum(0);
        slider.setMaximum(100);
        slider.addChangeListener(this);

        setValues(min, max, cur);
    }

    /**
     * Set the scrollbar's current value
     */
    public void setValue(double cur) {
        setValues(min, max, cur);
    }

    /**
     * Set the scrollbar's minimum, maximum and current values
     */
    public void setValues(double min, double max, double cur) {
        slider.removeChangeListener(this);

        value = cur;

        if (max < value) {
            max = value;
        }


        if (min > value) {
            min = value;
        }


        this.min = min;
        this.max = max;

        slider.setValue((int) ((value - min) / (max - min) * 100));

        updateDisplay();

        /*  int val;
          val = (int)(this.max / 10.0);
          if (val==0) val = 1;
          slider.setMajorTickSpacing( val );
          val = (int)(this.max / 100.0);
          if (val==0) val = 1;
          slider.setMinorTickSpacing( val );
          }          */
        /*else {
            slider.setValue((int)sliderValue);

            if (type==INTEGER)
                display.setText(String.valueOf((int)sliderValue));
            else
                display.setText(String.valueOf(sliderValue));

            display.setText(String.valueOf(sliderValue));
            } */
        slider.addChangeListener(this);
    }

    private void updateDisplay() {
        if (type == INTEGER) {
            display.setText(String.valueOf((int) value));
        } else {
            long pow = Math.round(Math.pow(10, DECIMAL_PLACES));
            long slidertempval = Math.round(value * pow);
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

        display.fireActionEvent();
    }


    /**
     * @return the textfield used to display the value of the textfield.
     */
    public JTextField getDisplay() {
        return display;
    }

    /**
     * @return the scrollbar
     */
    public JSlider getScrollbar() {
        return slider;
    }

    /**
     * @return the Set Button
     */
    public JButton getButton() {
        return set;
    }

    /**
     * @return the value of the scrollbar
     */
    public double getValue() {
        return value;
    }


    /**
     * Invoked when the value of the adjustable has changed.
     */
    public void stateChanged(ChangeEvent e) {
        setValues(min, max, (((double) slider.getValue()) / 100) * (max - min) + min);
    }

    public void focusGained(FocusEvent event) {
    }


    public void focusLost(FocusEvent event) {
        if (event.getSource() == display) {
            double value = Double.parseDouble(display.getText());
            setValues(min, max, value);
        }
    }


    private class JTextFieldHack extends JTextField {

        public JTextFieldHack(int length) {
            super(length);
        }

        /**
         * this class is a hack because we need to fire an action event from the display when its text is updates - in
         * Java V1.4 we would not need this because there is a getActionListeners() method in JTextfield.
         */

        private ArrayList listeners = new ArrayList();


        public synchronized void addActionListener(ActionListener listener) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }

            super.addActionListener(listener);
        }

        public synchronized void removeActionListener(ActionListener listener) {
            if (listeners.contains(listener)) {
                listeners.remove(listener);
            }

            super.removeActionListener(listener);
        }


        public void fireActionEvent() {
            ActionEvent event = new ActionEvent(this, 0, getText());
            Iterator iter = listeners.iterator();

            while (iter.hasNext()) {
                ((ActionListener) iter.next()).actionPerformed(event);
            }
        }

    }

}
