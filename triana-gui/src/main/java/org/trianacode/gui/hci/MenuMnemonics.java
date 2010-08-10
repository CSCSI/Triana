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


import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.trianacode.taskgraph.util.FileUtils;

/**
 * A utility class that handles setting mnemonics for menu items so that we don't get duplicated items.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public class MenuMnemonics {

    private static MenuMnemonics ourInstance;
    /**
     * keeps a track of all the mnemonics used for each menu so we can automatically pick a unused sensible value
     */
    private Hashtable menuMnemonics = new Hashtable();


    public synchronized static MenuMnemonics getInstance() {
        if (ourInstance == null) {
            ourInstance = new MenuMnemonics();
        }
        return ourInstance;
    }

    private MenuMnemonics() {
    }

    /**
     * Convenience method for creating a menu.  This function creates the menu and sets the Mnemonic for the menu to the
     * first letter of the name for the menu
     */
    public JMenu createMenu(String name) {
        JMenu m = new JMenu(name);

        menuMnemonics.put(name, String.valueOf(name.charAt(0)));

        m.setMnemonic(name.charAt(0));
        return m;
    }

    /**
     * @param menuName The name of the JMenu
     * @return The mnemonic for the JMenu that goes by this name
     */
    public char getNextMnemonic(String menuName) {
        char result = menuName.charAt(0);
        if (menuMnemonics.containsValue(String.valueOf(result))) {
            result = menuName.charAt(1);
        }
        menuMnemonics.put(menuName, String.valueOf(result));
        return result;
    }


    /**
     * Convenience method for creating a menu item.  This function creates the menu and sets the Mnemonic (see below)
     * for the menu . It then adds the menu item to the specified menu and sets the recevier of the actions from this
     * menu item to the specified target.
     */
    public JMenuItem createMenuItem(String name, JMenu menu, ActionListener target) {
        JMenuItem m = new JMenuItem(name);
        assignAMnemonic(m, menu, name);
        m.addActionListener(target);
        menu.add(m);
        return m;
    }

    /**
     * Convenience method for creating a check box menu item.  This function creates the menu and sets the Mnemonic (see
     * below) for the menu . It then adds the menu item to the specified menu and sets the recevier of the actions from
     * this menu item to the specified target.
     */
    public JCheckBoxMenuItem createCheckBoxMenuItem(String name, JMenu menu, ItemListener target) {
        JCheckBoxMenuItem m = new JCheckBoxMenuItem(name);
        assignAMnemonic(m, menu, name);
        m.addItemListener(target);
        menu.add(m);
        return m;
    }

    /*
     * The Mmemonic is calculated in a complex way. It tries to use the first
     * letter of the name of this menu item if it can i.e. if it is not already
     * used within this menu and it isn't the mnemonic for the menu
     * itself. It then performs a search to try to find a nice mnemonic
     * (i.e. a capital letter starting a key word in the menu items name)
     * and the if all else fails it searches all the letter in the string to
     * find one which isn't being used. If it can't use anything at all then
     * it gives up.
     *
     * This function is designed to take care of the new Triana
     * internationalization feature i.e. we can translate the user interface
     * into several languages and Triana automatically reconfigures itself
     * sensibly to cater for the language it is given.
     */

    public void assignAMnemonic(JMenuItem m, JMenu menu, String name) {
        char mnem = getNextMnemonic(menu, name);
        m.setMnemonic(mnem);
    }

    /**
     * @param parentMenu the parent JMenu
     * @param name       the name of the menu item
     * @return the next mnemonic for a menuitem
     */
    public char getNextMnemonic(JMenu parentMenu, String name) {
        String parentMenuName = parentMenu.getText();

        return getNextMnemonic(parentMenuName, name);

    }

    private char getNextMnemonic(String nm, String name) {
        char mnem = ' ';
        String used = (String) menuMnemonics.get(nm);

        String caps = "";
        Vector<String> sv = FileUtils.splitLine(name);

        boolean foundACapital = false;

        for (int j = 0; j < sv.size(); ++j) {
            caps += sv.get(j).charAt(0);
        }

        int i = 0;
        while (i < caps.length()) {
            mnem = caps.charAt(i); // possibility ??, start at the first char
            if (used.indexOf(mnem) == -1) { //  Its free and its a capital!!!!
                foundACapital = true;
                menuMnemonics.remove(nm);
                menuMnemonics.put(nm, used + String.valueOf(mnem));
                break;
            }
            ++i;
        }

        if (!foundACapital) {
            i = 0;
            while (i < name.length()) {
                mnem = name.charAt(i); // possibility ??, start at the first char
                if (used.indexOf(mnem) == -1) { //  Its free!!!!
                    menuMnemonics.remove(nm);
                    menuMnemonics.put(nm, used + String.valueOf(mnem));
                    break;
                }
                ++i;
            }
        }
        return mnem;
    }

}

