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

import javax.swing.*;
import java.awt.*;

/**
 * The Triana Unit toolbar, placed at the top of the Main application window.
 * This is implemented as a tear-off window.
 *
 * @version $Revision: 4048 $
 * @author Ian Taylor
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $modified by $Author: spxmss $
 */
public class TrianaUnitToolBar extends AbstractToolBar implements Actions {

    JButton run;
    //JButton runhistory;
    JButton stop;
    JButton reset;
    JButton flush;

    JButton properties;
    JButton group;
    JButton ungroup;
    JButton zoomin;
    JButton zoomout;
    JButton help;


    /**
     *  Description of the Field
     */
    JComboBox modeBox = null;

    public TrianaUnitToolBar(String title) {
        super(title, HORIZONTAL);

        setFloatable(true);
        setBorderPainted(true);
        setMargin(new Insets(0, 0, 0, 0));
        createWidgets();
    }

    public void createWidgets() {
        run = createButton(ActionTable.getAction(RUN_ACTION));
        //runhistory = createButton(ActionTable.getAction(RUN_HISTORY_ACTION));
        stop = createButton(ActionTable.getAction(PAUSE_ACTION));
        reset = createButton(ActionTable.getAction(RESET_ACTION));

        properties = createButton(ActionTable.getAction(PROERTIES_ACTION));

        group = createButton(ActionTable.getAction(GROUP_ACTION));
        ungroup = createButton(ActionTable.getAction(UNGROUP_ACTION));
        zoomin = createButton(ActionTable.getAction(ZOOMIN_ACTION));
        zoomout = createButton(ActionTable.getAction(ZOOMOUT_ACTION));

        add(run);
        //add(runhistory);
        add(stop);
        add(reset);
        add(new ToolBarSeparator());
        add(properties);
        add(group);
        add(ungroup);
        add(new ToolBarSeparator());
        add(zoomin);
        add(zoomout);

        MenuUtils.formatToolBar(this, MenuUtils.ICON_ONLY);
    }

}
