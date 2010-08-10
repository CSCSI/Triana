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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;


/**
 * This code is from a JavaWorld <a href="http://www.javaworld.com/javaworld/jw-11-2001/jw-1130-jscroll.html">
 * article</a> by Tom Tessier
 * <p/>
 * This class provides the optional "Window" menu for the scrollable desktop.
 *
 * @author <a href="mailto:tessier@gabinternet.com">Tom Tessier</a>
 * @version 1.0  11-Aug-2001
 */


public class DesktopMenu extends JMenu implements ActionListener {

    private DesktopMediator desktopMediator;

    private boolean tileMode;
    private int baseItemsEndIndex;
    private ButtonGroup frameRadioButtonMenuItemGroup;


    /**
     * creates the DesktopMenu object
     *
     * @param desktopMediator a reference to the DesktopMediator object
     */
    public DesktopMenu(DesktopMediator desktopMediator) {
        this(desktopMediator, false);
    }

    /**
     * creates the DesktopMenu object with the specified tileMode
     *
     * @param desktopMediator a reference to the DesktopMediator object
     * @param tileMode        the tile mode to use (<code>true</code> = tile internal frames, <code>false</code> =
     *                        cascade internal frames)
     */
    public DesktopMenu(DesktopMediator desktopMediator, boolean tileMode) {

        super("Window");
        setMnemonic(KeyEvent.VK_W);

        this.desktopMediator = desktopMediator;
        this.tileMode = tileMode;

        frameRadioButtonMenuItemGroup = new ButtonGroup();

        new ConstructWindowMenu(this, desktopMediator, tileMode);

        // set the default item count (ie: number of items comprising
        // current menu contents)
        baseItemsEndIndex = getItemCount();

    }


    /**
     * adds a {@link com.tomtessier.scrollabledesktop.BaseRadioButtonMenuItem BaseRadioButtonMenuItem} to the menu and
     * associates it with an internal frame
     *
     * @param associatedFrame the internal frame to associate with the menu item
     */
    public void add(BaseInternalFrame associatedFrame) {

        int displayedCount = getItemCount() - baseItemsEndIndex + 1;
        int currentMenuCount = displayedCount;

        // compute the key mnemonic based upon the currentMenuCount
        if (currentMenuCount > 9) {
            currentMenuCount /= 10;
        }

        BaseRadioButtonMenuItem menuButton =
                new BaseRadioButtonMenuItem(this,
                        displayedCount + " " + associatedFrame.getTitle(),
                        KeyEvent.VK_0 + currentMenuCount, -1, true, associatedFrame);

        associatedFrame.setAssociatedMenuButton(menuButton);

        add(menuButton);
        frameRadioButtonMenuItemGroup.add(menuButton);

        menuButton.setSelected(true); // and reselect here, so that the
        // buttongroup recognizes the change

    }

    /**
     * removes the specified radio menu button from the menu
     *
     * @param menuButton the JRadioButtonMenuItem to remove
     */
    public void remove(JRadioButtonMenuItem menuButton) {
        frameRadioButtonMenuItemGroup.remove(menuButton);
        super.remove(menuButton);

        // cannot simply remove the radio menu button, as need to renumber the
        // keyboard shortcut keys as well. Hence, a call to refreshMenu is in order...

        refreshMenu(); // refresh the mnemonics associated with the other items
    }


    private void refreshMenu() {

        // refresh the associated mnemonics, so that the keyboard shortcut
        // keys are properly renumbered...

        // get an enumeration to the elements of the current button group
        Enumeration e = frameRadioButtonMenuItemGroup.getElements();

        int displayedCount = 1;
        int currentMenuCount = 0;

        while (e.hasMoreElements()) {
            BaseRadioButtonMenuItem b =
                    (BaseRadioButtonMenuItem) e.nextElement();

            // compute the key mnemonic based upon the currentMenuCount
            currentMenuCount = displayedCount;
            if (currentMenuCount > 9) {
                currentMenuCount /= 10;
            }
            b.setMnemonic(KeyEvent.VK_0 + currentMenuCount);
            b.setText(displayedCount +
                    " " + b.getAssociatedFrame().getTitle());
            displayedCount++;
        }

    }

    /**
     * propogates the actionPerformed menu event to DesktopMediator
     *
     * @param e the ActionEvent to propogate
     */
    public void actionPerformed(ActionEvent e) {
        desktopMediator.actionPerformed(e);
    }


}