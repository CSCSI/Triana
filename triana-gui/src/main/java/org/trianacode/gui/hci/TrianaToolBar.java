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

import org.trianacode.gui.action.ActionTable;
import org.trianacode.gui.action.Actions;
import org.trianacode.util.Env;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Triana toolbar, placed at the top of the Main application window. This is implemented as a tear-off window.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public class TrianaToolBar extends AbstractToolBar implements Actions {


    ApplicationFrame mainapp;

    JButton copy;
    JButton cut;
    JButton paste;
    JButton delete;
    JButton open;
    JButton save;
    JButton saveas;
    JButton newfile;
    //JButton find;
    //JButton help;
    JButton print;
    JButton selectall;
    JButton options;

    /**
     * Description of the Field
     */

    public TrianaToolBar(String title, ApplicationFrame app) {
        super(title, HORIZONTAL);
        mainapp = app;
        setFloatable(false);
        setBorderPainted(true);
        setMargin(new Insets(0, 0, 0, 0));
        createWidgets();
    }

    public void createWidgets() {
        copy = createButton(ActionTable.getAction(COPY_ACTION));
        cut = createButton(ActionTable.getAction(CUT_ACTION));
        paste = createButton(ActionTable.getAction(PASTE_ACTION));
        delete = createButton(ActionTable.getAction(DELETE_ACTION));
        open = createButton(ActionTable.getAction(OPEN_ACTION));
        newfile = createButton(ActionTable.getAction(NEW_ACTION));
        save = createButton(ActionTable.getAction(SAVE_ACTION));
        saveas = createButton(ActionTable.getAction(SAVE_AS_ACTION));
        print = createButton(ActionTable.getAction(PRINT_ACTION));
        /*help = createButton(ActionTable.getAction(HELP_ACTION));

        find = createButton(ActionTable.getAction(FIND_ACTION));*/


        selectall = createButton(ActionTable.getAction(SELECT_ALL_ACTION));

        options = createButton(GUIEnv.getIcon("properties.png"));
        options.setToolTipText(Env.getString("TrianaOptionTitle"));
        //options.setActionCommand(Env.getString("TrianaOptionTitle"));
        options.setActionCommand("TrianaOptionTitle");

        add(newfile);
        add(open);
        add(save);
        add(saveas);
        add(new ToolBarSeparator());
        add(copy);
        add(cut);
        add(paste);
        add(selectall);
        add(delete);
        add(new ToolBarSeparator());
        //add(find);
        add(print);
        add(options);
        //add(help);

        MenuUtils.formatToolBar(this, MenuUtils.ICON_ONLY);

        options.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new OptionsHandler(mainapp.getTools());
            }
        });
    }


}
