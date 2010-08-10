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

package org.trianacode.gui.hci;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

/**
 * A set of utilities for customizing menus and toolbars.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class MenuUtils {

    public static final int ICON_ONLY = 0;
    public static final int TEXT_ONLY = 1;

    public static void formatToolBar(JToolBar toolbar, int style) {
        Component[] comps = toolbar.getComponents();

        for (int count = 0; count < comps.length; count++) {
            if (comps[count] instanceof AbstractButton) {
                if (style == ICON_ONLY) {
                    ((AbstractButton) comps[count]).setText(null);
                } else if (style == TEXT_ONLY) {
                    ((AbstractButton) comps[count]).setIcon(null);
                }
            }
        }
    }


    public static void assignMnemonics(JMenu menu) {
        Component[] comps = menu.getMenuComponents();
        ArrayList list = new ArrayList();
        char mne;

        for (int count = 0; count < comps.length; count++) {
            if (comps[count] instanceof JMenuItem) {
                mne = getMnemonic(((JMenuItem) comps[count]).getText(), list);
                ((JMenuItem) comps[count]).setMnemonic(mne);
            }
        }
    }

    private static char getMnemonic(String label, ArrayList list) {
        int idx = 0;

        while ((idx > -1) && (list.contains(new Character(Character.toUpperCase(label.charAt(idx)))))) {
            idx = getUpperCaseIdx(idx + 1, label);
        }

        if (idx > -1) {
            list.add(new Character(Character.toUpperCase(label.charAt(idx))));
            return label.charAt(idx);
        } else {
            idx = 0;

            while ((idx > -1) && (list.contains(new Character(Character.toUpperCase(label.charAt(idx)))))) {
                idx = getLowerCaseIdx(idx + 1, label);
            }

            if (idx > -1) {
                list.add(new Character(Character.toUpperCase(label.charAt(idx))));
                return label.charAt(idx);
            } else {
                return '.';
            }
        }
    }

    private static int getUpperCaseIdx(int index, String text) {
        int idx = -1;

        for (int count = index; (count < text.length()) && (idx == -1); count++) {
            if (Character.isUpperCase(text.charAt(count))) {
                idx = count;
            }
        }

        return idx;
    }

    private static int getLowerCaseIdx(int index, String text) {
        int idx = -1;

        for (int count = index; (count < text.length()) && (idx == -1); count++) {
            if (Character.isLowerCase(text.charAt(count))) {
                idx = count;
            }
        }

        return idx;
    }

}
