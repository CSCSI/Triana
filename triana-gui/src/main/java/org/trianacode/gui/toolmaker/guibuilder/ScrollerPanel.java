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
package org.trianacode.gui.toolmaker.guibuilder;


import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The panel for defining a gui builder scroller/intscroller component
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 2002
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class ScrollerPanel extends JPanel implements ComponentPanelInterface {

    public static final int SCROLLER = 0;
    public static final int INT_SCROLLER = 1;

    public static final int DEFAULT_VALUE = 0;

    /**
     * the panel mode (scroller/int_scroller)
     */
    private int mode;

    /**
     * input fields
     */
    private JTextField title = new JTextField(15);
    private JTextField min = new JTextField("0", 7);
    private JTextField max = new JTextField("100", 7);
    private JCheckBox resize = new JCheckBox();

    /**
     * the default value
     */
    private String defval = String.valueOf(DEFAULT_VALUE);


    public ScrollerPanel(int mode) {
        this.mode = mode;
        initLayout();
    }


    /**
     * intialise the layout
     */
    private void initLayout() {
        setLayout(new BorderLayout());

        JPanel labelpanel = new JPanel(new GridLayout(4, 1));
        labelpanel.add(new JLabel(Env.getString("title")));
        labelpanel.add(new JLabel(Env.getString("min")));
        labelpanel.add(new JLabel(Env.getString("max")));
        labelpanel.add(new JLabel(Env.getString("resizeMinMax")));
        labelpanel.setBorder(new EmptyBorder(0, 0, 0, 3));

        JPanel titlepanel = new JPanel(new BorderLayout());
        titlepanel.add(title, BorderLayout.WEST);

        JPanel minpanel = new JPanel(new BorderLayout());
        minpanel.add(min, BorderLayout.WEST);

        JPanel maxpanel = new JPanel(new BorderLayout());
        maxpanel.add(max, BorderLayout.WEST);

        JPanel resizepanel = new JPanel(new BorderLayout());
        resizepanel.add(resize, BorderLayout.WEST);

        JPanel fieldpanel = new JPanel(new GridLayout(4, 1));
        fieldpanel.add(titlepanel);
        fieldpanel.add(minpanel);
        fieldpanel.add(maxpanel);
        fieldpanel.add(resizepanel);

        JPanel contain = new JPanel(new BorderLayout());
        contain.add(fieldpanel, BorderLayout.CENTER);
        contain.add(labelpanel, BorderLayout.WEST);

        add(contain, BorderLayout.NORTH);
    }

    /**
     * @return the gui builder string for the defined component
     */
    public String getGUIBuilderStr(String param) {
        try {
            if (mode == SCROLLER)
                Double.parseDouble(defval);
            else
                Integer.parseInt(defval);
        }
        catch (NumberFormatException except) {
            defval = String.valueOf(DEFAULT_VALUE);
        }

        if (mode == SCROLLER)
            return title.getText() + " $title " + param + " Scroller " + min.getText() +
                    " " + max.getText() + " " + defval + " " + String.valueOf(resize.isSelected());
        else
            return title.getText() + " $title " + param + " IntScroller " + min.getText() +
                    " " + max.getText() + " " + defval + " " + String.valueOf(resize.isSelected());
    }

    /**
     * Sets the defined component given the specified gui builder string
     */
    public void setGUIBuilderStr(String line) {
        String[] strs = BuilderPanel.splitString(line);

        title.setText(strs[0]);

        if (strs.length > 3)
            min.setText(strs[3]);

        if (strs.length > 4)
            max.setText(strs[4]);

        if (strs.length > 6)
            resize.setSelected(new Boolean(strs[6]).booleanValue());
    }


    /**
     * Resets the defined component to default values
     */
    public void reset(String param) {
        title.setText(param);
        min.setText("0");
        max.setText("0");
        resize.setSelected(false);
    }


    /**
     * notifies the panel of the default parameter value
     */
    public void notifyDefaultValue(String value) {
        if (value == null)
            defval = "0";
        else
            defval = value;

        try {
            double val = new Double(defval).doubleValue();
            double minval = new Double(min.getText()).doubleValue();
            double maxval = new Double(max.getText()).doubleValue();

            if (val < minval)
                min.setText(value);

            if (val > maxval)
                max.setText(value);
        }
        catch (NumberFormatException except) {
        }
    }


}
