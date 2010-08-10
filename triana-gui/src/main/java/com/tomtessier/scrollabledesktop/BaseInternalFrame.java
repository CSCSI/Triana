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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;

/**
 * This code is from a JavaWorld <a href="http://www.javaworld.com/javaworld/jw-11-2001/jw-1130-jscroll.html">
 * article</a> by Tom Tessier
 * <p/>
 * This class provides a custom internal frame. Each internal frame is assigned an associated toggle button and an
 * optional radio button menu item. These buttons reside in the {@link com.tomtessier.scrollabledesktop.DesktopResizableToolBar
 * DesktopResizableToolBar} and {@link com.tomtessier.scrollabledesktop.DesktopMenu DesktopMenu}. classes respectively.
 *
 * @author <a href="mailto:tessier@gabinternet.com">Tom Tessier</a>
 * @version 1.0  9-Aug-2001
 */

public class BaseInternalFrame extends JInternalFrame {

    private JToggleButton associatedButton;
    private JRadioButtonMenuItem associatedMenuButton;

    private boolean isClosable;
    private int initialWidth;
    private int initialHeight;

    /**
     * creates the BaseInternalFrame
     *
     * @param title         the string displayed in the title bar of the internal frame
     * @param icon          the ImageIcon displayed in the title bar of the internal frame
     * @param frameContents the contents of the internal frame
     * @param isClosable    determines whether the frame is closable
     */
    public BaseInternalFrame(String title,
                             ImageIcon icon, JPanel frameContents,
                             boolean isClosable) {

        super(title, // title
                true, //resizable
                isClosable, //closable
                true, //maximizable
                true);//iconifiable

        this.isClosable = isClosable;

        setBackground(Color.white);
        setForeground(Color.blue);

        if (icon != null) {
            setFrameIcon(icon);
        }

        // add the window contents
        getContentPane().add(frameContents);
        pack();

        saveSize();

        setVisible(true); // turn the frame on
    }

    private void saveSize() {
        initialWidth = getWidth();
        initialHeight = getHeight();
    }

    /**
     * constructor provided for compatibility with JInternalFrame
     */
    public BaseInternalFrame() {
        super();
        saveSize();
    }

    /**
     * constructor provided for compatibility with JInternalFrame
     */
    public BaseInternalFrame(String title) {
        super(title);
        saveSize();
    }

    /**
     * constructor provided for compatibility with JInternalFrame
     */
    public BaseInternalFrame(String title, boolean resizable) {
        super(title, resizable);
        saveSize();
    }

    /**
     * constructor provided for compatibility with JInternalFrame
     */
    public BaseInternalFrame(String title, boolean resizable, boolean closable) {
        super(title, resizable, closable);
        this.isClosable = isClosable;
        saveSize();
    }

    /**
     * constructor provided for compatibility with JInternalFrame
     */
    public BaseInternalFrame(String title, boolean resizable, boolean closable,
                             boolean maximizable) {
        super(title, resizable, closable, maximizable);
        this.isClosable = isClosable;
        saveSize();
    }

    /**
     * constructor provided for compatibility with JInternalFrame
     */
    public BaseInternalFrame(String title, boolean resizable, boolean closable,
                             boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);
        this.isClosable = isClosable;
        saveSize();
    }


    /**
     * sets the associated menu button
     *
     * @param associatedMenuButton the menu button to associate with the internal frame
     */
    public void setAssociatedMenuButton(JRadioButtonMenuItem associatedMenuButton) {
        this.associatedMenuButton = associatedMenuButton;
    }

    /**
     * returns the associated menu button
     *
     * @return the JRadioButtonMenuItem object associated with this internal frame
     */
    public JRadioButtonMenuItem getAssociatedMenuButton() {
        return associatedMenuButton;
    }

    /**
     * sets the associated toggle button
     *
     * @param associatedButton the toggle button to associate with the internal frame
     */
    public void setAssociatedButton(JToggleButton associatedButton) {
        this.associatedButton = associatedButton;
    }

    /**
     * returns the associated toggle button
     *
     * @return the JToggleButton object associated with this internal frame
     */
    public JToggleButton getAssociatedButton() {
        return associatedButton;
    }

    /**
     * returns the initial dimensions of this internal frame. Necessary so that internal frames can be restored to their
     * default sizes when the cascade frame positioning mode is chosen in {@link com.tomtessier.scrollabledesktop.FramePositioning
     * FramePositioning}.
     *
     * @return the Dimension object representing the initial dimensions of this internal frame
     */
    public Dimension getInitialDimensions() {
        return new Dimension(initialWidth, initialHeight);
    }

    /**
     * returns the toString() representation of this object. Useful for debugging purposes.
     *
     * @return the toString() representation of this object
     */
    public String toString() {
        return "BaseInternalFrame: " + getTitle();
    }


    /**
     * selects the current frame, along with any toggle and menu buttons that may be associated with it
     */
    public void selectFrameAndAssociatedButtons() {

        // select associated toolbar button
        if (associatedButton != null) {
            associatedButton.setSelected(true);
            ((BaseToggleButton) associatedButton).
                    flagContentsChanged(false);
        }

        // select menu button
        if (associatedMenuButton != null) {
            associatedMenuButton.setSelected(true);
        }

        try {
            setSelected(true);
            setIcon(false);  // select and de-iconify the frame
        }
        catch (java.beans.PropertyVetoException pve) {
            System.out.println(pve.getMessage());
        }

        setVisible(true); // and make sure the frame is turned on

    }


}