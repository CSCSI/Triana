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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * This code is from a JavaWorld <a href="http://www.javaworld.com/javaworld/jw-11-2001/jw-1130-jscroll.html">
 * article</a> by Tom Tessier
 *
 * This class constructs the "Window" menu items for use by
 * {@link com.tomtessier.scrollabledesktop.DesktopMenu DesktopMenu}.
 *
 * @author <a href="mailto:tessier@gabinternet.com">Tom Tessier</a>
 * @version 1.0  11-Aug-2001
 */


public class ConstructWindowMenu implements ActionListener {

    private DesktopMediator desktopMediator;


    /**
     * creates the ConstructWindowMenu object.
     *
     * @param sourceMenu the source menu to apply the menu items
     * @param desktopMediator a reference to the DesktopMediator
     * @param tileMode the current tile mode (tile or cascade)
     */
    public ConstructWindowMenu(JMenu sourceMenu,
                               DesktopMediator desktopMediator,
                               boolean tileMode) {
        this.desktopMediator = desktopMediator;
        constructMenuItems(sourceMenu, tileMode);
    }

    /**
     * constructs the actual menu items.
     *
     * @param sourceMenu the source menu to apply the menu items
     * @param tileMode the current tile mode
     */
    private void constructMenuItems(JMenu sourceMenu, boolean tileMode) {

        sourceMenu.add(new BaseMenuItem(this, "Tile", KeyEvent.VK_T, -1));
        sourceMenu.add(new BaseMenuItem(this, "Cascade", KeyEvent.VK_C, -1));
        sourceMenu.addSeparator();

        JMenu autoMenu = new JMenu("Auto");
        autoMenu.setMnemonic(KeyEvent.VK_U);
        ButtonGroup autoMenuGroup = new ButtonGroup();
        JRadioButtonMenuItem radioItem =
                new BaseRadioButtonMenuItem(this,
                                            "Tile", KeyEvent.VK_T, -1, tileMode);
        autoMenu.add(radioItem);
        autoMenuGroup.add(radioItem);

        radioItem =
                new BaseRadioButtonMenuItem(this,
                                            "Cascade", KeyEvent.VK_C, -1, !tileMode);
        autoMenu.add(radioItem);
        autoMenuGroup.add(radioItem);

        sourceMenu.add(autoMenu);
        sourceMenu.addSeparator();

        sourceMenu.add(new BaseMenuItem(this,
                                        "Close", KeyEvent.VK_S, KeyEvent.VK_Z));
        sourceMenu.addSeparator();

    }


    /**
     * propogates actionPerformed menu event to the DesktopMediator reference
     *
     * @param e the ActionEvent to propogate
     */
    public void actionPerformed(ActionEvent e) {
        desktopMediator.actionPerformed(e);
    }

}