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
package org.trianacode.gui.windows;

import org.trianacode.gui.panels.ScrollerPanel;

/**
 *
 * This window allows a scaler or a number to be input into a Triana unit.
 * If there is an unit which requires one parameter to be passed to it
 * in the form of a double-precision floating point value,
 * programmers should consider using this class.
 * The original scroller's provided by java deal with
 * integers.  Here, we scale the integers to
 * produce a scroller which allows floating point values.</p>
 *
 * <p>Also, see the parameter windows for the Wave, Adder, Subtracter,
 * Divider and Multiplier units for examples of what this class looks
 * like.  Also other units e.g. Wave are created by inheriting from
 * ScrollerWindow and then adding extra functionality to the basic
 * look of the window.
 *
 * @author      Ian Taylor
 * @created     1 Dec 1999
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class ScrollerWindow extends ParameterWindow {

    ScrollerPanel scrollerPanel = null;

    //public JSlider slider=null;

    public ScrollerWindow() {
        super();
    }

    /**
     * Creates a new Scroller Window for a particular unit with a specified
     * title and parameter name.  It also sets the minimum, maximum and current
     * scrollbar values to 1, 100 and 1 repectively.  Call setValues to change
     * these defaults.
     */
    public ScrollerWindow(Object unit, String textForUser) {
        setObject(unit, textForUser);
    }


    public void setObject(Object unit, String textForUser) {
        ScrollerPanel scrollerPanel = new ScrollerPanel();
        //scrollerPanel.setObject(unit, textForUser);
        //slider = scrollerPanel.slider;

        this.scrollerPanel = scrollerPanel;
        //   super.setTask(task, scrollerPanel);
    }

    /**
     * Set the minimum, maximum and current scrollbar's values
     */
    public void setValues(double min, double max, double cur) {
        scrollerPanel.setValues(min, max, cur);
    }

    public void setValue(double newval) {
        scrollerPanel.setValue(newval);
    }

    public void setMin(double newmin) {
        scrollerPanel.setMin(newmin);
    }

    public void setMax(double newmax) {
        scrollerPanel.setMax(newmax);
    }

    /**
     * Updates the widgets within the scroll window so
     * to reflect changes in parameters
     */
    public void updateWidgets() {
        scrollerPanel.updateWidgets();
    }

    /**
     * returns the min value of the slider
     */
    public double getMin() {
        return scrollerPanel.getMin();
    }

    /**
     * Sets the new name for the parameter which the slider controls
     */
    public void setParameterName(String newname) {
        scrollerPanel.setParameterName(newname);
    }

    /**
     * returns the max value of the slider
     */
    public double getMax() {
        return scrollerPanel.getMax();
    }

    /**
     * returns the current value of the parameter
     */
    public double getValue() {
        return scrollerPanel.getValue();
    }

    /**
     * Does a layout on the Scroller Window, i.e. does a layout on the
     * scrollerPanel.
     */
    protected void layoutWindow() {
        //scrollerPanel.layoutPanel();
    }
}













