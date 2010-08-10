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

package com.tomtessier.scrollabledesktop;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;


/**
 * This code is from a JavaWorld <a href="http://www.javaworld.com/javaworld/jw-11-2001/jw-1130-jscroll.html">
 * article</a> by Tom Tessier
 * <p/>
 * Generic self-contained resizable toolbar class. When a button addition exceeds the width of the toolbar container,
 * all buttons within the container are automatically resized to compensate, down to the minimum button width defined
 * upon creation of the ResizableToolbar instance.
 *
 * @author <a href="mailto:tessier@gabinternet.com">Tom Tessier</a>
 * @version 1.0  03-Mar-2001
 */


public class ResizableToolBar extends JToolBar
        implements ComponentListener {

    // ButtonGroups for toolbar buttons
    private ButtonGroup buttonGroup;

    private int minButtonWidth;
    private int maxButtonWidth;


    /**
     * creates the ResizableToolbar object
     *
     * @param minButtonWidth the minimum button width allowed
     * @param maxButtonWidth the maximum button width allowed
     */
    public ResizableToolBar(int minButtonWidth, int maxButtonWidth) {

        buttonGroup = new ButtonGroup();
        setFloatable(false);
        this.minButtonWidth = minButtonWidth;
        this.maxButtonWidth = maxButtonWidth;

        addComponentListener(this);

    }

    /**
     * adds a button to the ResizableToolbar
     *
     * @param button the button to add
     */
    public void add(AbstractButton button) {
        buttonGroup.add(button);
        super.add(button);
        button.setSelected(true);
        resizeButtons();

    }


    /**
     * removes a button from the ResizableToolbar
     *
     * @param button the button to remove
     */
    public void remove(AbstractButton button) {
        super.remove(button);
        buttonGroup.remove(button);
        resizeButtons();
        repaint();
    }

    /**
     * returns the ResizableToolbar elements
     *
     * @return an Enumeration of the ResizableToolbar elements
     */
    public Enumeration getElements() {
        return buttonGroup.getElements();
    }

    /**
     * returns the number of buttons stored within the ResizableToolbar
     *
     * @return the number of buttons
     */
    public int getButtonCount() {
        // note: getButtonCount() will not work with JDK 1.2
        return buttonGroup.getButtonCount();
    }


    /**
     * resizes the buttons of the toolbar, depending upon the total number of components stored therein. Executes as an
     * "invoked later" thread for a slight perceived performance boost.
     */
    private void resizeButtons() {

        final float exactButtonWidth = getCurrentButtonWidth();

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                JToggleButton b = null;
                Enumeration e = getElements();

                float currentButtonXLocation = 0.0f;

                // resize the buttons
                while (e.hasMoreElements()) {
                    b = (JToggleButton) e.nextElement();
                    int buttonWidth =
                            Math.round(currentButtonXLocation +
                                    exactButtonWidth) -
                                    Math.round(currentButtonXLocation);
                    assignWidth(b, buttonWidth);

                    currentButtonXLocation += exactButtonWidth;
                }

                revalidate();

            }
        });
    }


    /**
     * returns the current button width, defined as the width of the ResizableToolbar divided by the number of buttons.
     * The value returned ranges from minButtonWidth to maxButtonWidth (two variables defined upon creation of the
     * ResizableToolbar instance).
     *
     * @return the current button width as a float.
     */
    private float getCurrentButtonWidth() {

        int width = getWidth() - getInsets().left - getInsets().right;

        // if width <= 0, means JToolbar hasn't been displayed yet, so use
        // the maximum button width
        float buttonWidth =
                ((width <= 0) ? maxButtonWidth : width);

        int numButtons = getButtonCount();

        // have at least one button? then divide the width by the # of buttons
        // (ie: resultant buttonWidth = viewport width / # of buttons)
        if (numButtons > 0) {
            buttonWidth /= numButtons;
        }

        if (buttonWidth < minButtonWidth) {
            buttonWidth = minButtonWidth;
        } else if (buttonWidth > maxButtonWidth) {
            buttonWidth = maxButtonWidth;
        }

        return buttonWidth;
    }


    /**
     * assigns a new width to the specified button
     *
     * @param b           the button whose width is to be adjusted
     * @param buttonWidth the new width
     */
    private void assignWidth(JToggleButton b, int buttonWidth) {

        b.setMinimumSize(
                new Dimension(buttonWidth - 2, b.getPreferredSize().height));
        b.setPreferredSize(
                new Dimension(buttonWidth, b.getPreferredSize().height));
        Dimension newSize = b.getPreferredSize();
        b.setMaximumSize(newSize);
        b.setSize(newSize);

    }


    /////
    // respond to resize events...
    /////

    /**
     * resize the buttons when the ResizableToolbar itself is resized
     *
     * @param e the ComponentEvent
     */
    public void componentResized(ComponentEvent e) {
        resizeButtons();
    }


    /**
     * interface placeholder
     *
     * @param e the ComponentEvent
     */
    public void componentShown(ComponentEvent e) {
    }

    /**
     * interface placeholder
     *
     * @param e the ComponentEvent
     */
    public void componentMoved(ComponentEvent e) {
    }

    /**
     * interface placeholder
     *
     * @param e the ComponentEvent
     */
    public void componentHidden(ComponentEvent e) {
    }


}