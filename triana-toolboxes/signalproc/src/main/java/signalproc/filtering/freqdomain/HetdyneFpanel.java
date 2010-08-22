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
package signalproc.filtering.freqdomain;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.trianacode.gui.panels.ParameterPanel;
import triana.types.util.SigAnalWindows;
import triana.types.util.Str;

/**
 * Custom panel for the updated NewHetdyneF unit.
 *
 * @author Matthew Shields
 * @version $Revision: 2921 $
 */
public class HetdyneFpanel extends ParameterPanel
        implements ActionListener, ChangeListener, FocusListener {

    private double freqMin = 0;
    private double freqMax = 1000;
    private double freq = 500;
    private double bandMin = 0;
    private double bandMax = 200;
    private double band = 100;

    public static int DECIMAL_PLACES = 3;

    // Define GUI components here, e.g.
    private JSlider freqSlider = new JSlider(0, 100, 50);
    private JTextField freqTextField = new JTextField(10);
    private JSlider bandSlider = new JSlider(0, 100, 50);
    private JTextField bandTextField = new JTextField(10);
    private JComboBox windowComboBox = new JComboBox(SigAnalWindows.listOfWindowsAsArray());
    private JCheckBox nyquistCheckBox = new JCheckBox("Reduce output Nyquist frequency to bandwidth?", true);

    /**
     * This method is called before the panel is displayed. It should initialise the panel layout.
     */
    public void init() {
        setLayout(new BorderLayout());

        JPanel main = new JPanel(new GridLayout(4, 1));

        JPanel paramRow = new JPanel(new BorderLayout());
        JPanel contentRow = new JPanel(new BorderLayout());
        contentRow.add(new JLabel("HetdyneF Frequency", JLabel.CENTER), BorderLayout.NORTH);
        paramRow.add(freqSlider, BorderLayout.CENTER);
        paramRow.add(freqTextField, BorderLayout.EAST);
        freqTextField.setHorizontalAlignment(JTextField.RIGHT);
        contentRow.add(paramRow, BorderLayout.CENTER);
        freqSlider.addChangeListener(this);
        freqTextField.addActionListener(this);
        freqTextField.addFocusListener(this);
        main.add(contentRow);

        paramRow = new JPanel(new BorderLayout());
        contentRow = new JPanel(new BorderLayout());
        contentRow.add(new JLabel("Bandwidth", JLabel.CENTER), BorderLayout.NORTH);
        paramRow.add(bandSlider, BorderLayout.CENTER);
        paramRow.add(bandTextField, BorderLayout.EAST);
        bandTextField.setHorizontalAlignment(JTextField.RIGHT);
        contentRow.add(paramRow, BorderLayout.CENTER);
        bandSlider.addChangeListener(this);
        bandTextField.addActionListener(this);
        bandTextField.addFocusListener(this);
        main.add(contentRow);

        paramRow = new JPanel(new BorderLayout());
        paramRow.add(new JLabel("Choose window for smoothing spectrum edges"), BorderLayout.WEST);
        paramRow.add(windowComboBox, BorderLayout.CENTER);
        contentRow = new JPanel(new BorderLayout());
        contentRow.add(paramRow, BorderLayout.NORTH);
        windowComboBox.addActionListener(this);
        main.add(contentRow);

        contentRow = new JPanel(new BorderLayout());
        contentRow.add(nyquistCheckBox, BorderLayout.NORTH);
        main.add(contentRow);
        nyquistCheckBox.addActionListener(this);

        add(main, BorderLayout.NORTH);

    }

    private void setFreqValue(double cur) {
        setFreqValues(freqMin, freqMax, cur);
    }

    private void setFreqValues(double min, double max, double cur) {
        freqSlider.removeChangeListener(this);
        freq = cur;
        if (max < freq) {
            max = freq;
        }
        if (min > freq) {
            min = freq;
        }
        freqMin = min;
        freqMax = max;
        freqSlider.setValue((int) ((freq - min) / (max - min) * 100));
        updateFreq();
        freqSlider.addChangeListener(this);
    }

    private void updateFreq() {
        long pow = Math.round(Math.pow(10, DECIMAL_PLACES));
        long slidertempval = Math.round(freq * pow);
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

            freqTextField.setText(minus + String.valueOf(slidertempval / pow) + '.' + remain);
        } else {
            freqTextField.setText(minus + String.valueOf(slidertempval / pow));
        }

        ActionListener[] actionListeners = freqTextField.getActionListeners();
        ActionEvent event = new ActionEvent(freqTextField, 0, freqTextField.getText());
        for (int i = 0; i < actionListeners.length; i++) {
            actionListeners[i].actionPerformed(event);
        }
    }

    private void updateBand() {
        long pow = Math.round(Math.pow(10, DECIMAL_PLACES));
        long slidertempval = Math.round(band * pow);
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

            bandTextField.setText(minus + String.valueOf(slidertempval / pow) + '.' + remain);
        } else {
            bandTextField.setText(minus + String.valueOf(slidertempval / pow));
        }

        ActionListener[] actionListeners = bandTextField.getActionListeners();
        ActionEvent event = new ActionEvent(bandTextField, 0, bandTextField.getText());
        for (int i = 0; i < actionListeners.length; i++) {
            actionListeners[i].actionPerformed(event);
        }
    }

    private void setBandValue(double cur) {
        setBandValues(bandMin, bandMax, cur);
    }

    private void setBandValues(double min, double max, double cur) {
        bandSlider.removeChangeListener(this);
        band = cur;
        if (max < band) {
            max = band;
        }
        if (min > band) {
            min = band;
        }
        bandMin = min;
        bandMax = max;
        bandSlider.setValue((int) ((band - min) / (max - min) * 100));
        updateBand();
        bandSlider.addChangeListener(this);
    }

    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        setFreqValue(Str.strToDouble((String) getParameter("freq")));
        setBandValue(Str.strToDouble((String) getParameter("bandwidth")));
        windowComboBox.setSelectedItem(getParameter("window"));
        nyquistCheckBox.setSelected(Str.strToBoolean((String) getParameter("nyquist")));

    }

    /**
     * This method is called when a parameter in the task is updated. It should update the GUI in response to the
     * parameter update
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("freq")) {
            setFreqValue(Str.strToDouble((String) value));
        }
        if (paramname.equals("bandwidth")) {
            setBandValue(Str.strToDouble((String) value));
        }
        if (paramname.equals("window")) {
            windowComboBox.setSelectedItem(value);
        }
        if (paramname.equals("nyquist")) {
            nyquistCheckBox.setSelected(Str.strToBoolean((String) value));
        }
    }


    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
        // Insert code to clean-up panel here
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == freqTextField) {
            setParameter("freq", freqTextField.getText());
        }
        if (e.getSource() == bandTextField) {
            setParameter("bandwidth", bandTextField.getText());
        }
        if (e.getSource() == windowComboBox) {
            setParameter("window", windowComboBox.getSelectedItem());
        }
        if (e.getSource() == nyquistCheckBox) {
            setParameter("nyquist", (new Boolean(nyquistCheckBox.isSelected())).toString());
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == freqSlider) {
            setFreqValues(freqMin, freqMax, (((double) freqSlider.getValue()) / 100) * (freqMax - freqMin) + freqMin);
        }
        if (e.getSource() == bandSlider) {
            setBandValues(bandMin, bandMax, (((double) bandSlider.getValue()) / 100) * (bandMax - bandMin) + bandMin);
        }
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
        if (e.getSource() == freqTextField) {
            double value = Str.strToDouble(freqTextField.getText());
            setFreqValues(freqMin, freqMax, value);
        }
        if (e.getSource() == bandTextField) {
            double value = Str.strToDouble(bandTextField.getText());
            setBandValues(bandMin, bandMax, value);
        }
    }

}
