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
package org.trianacode.gui.action.files;

import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.util.Env;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Specific internal class that implements a listener just to listen to the recent items list.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public class OpenRecentListener implements ActionListener {

    private Hashtable itemTable = new Hashtable();
    private ApplicationFrame applicationFrame;

    public OpenRecentListener(ApplicationFrame app) {
        applicationFrame = app;
    }

    /**
     * Add a recent item to the listeners list of recent items
     *
     * @param fullName the full path to the recent item
     * @return the short name to be displayed in the menu
     */
    public String addRecent(String fullName) {
        StringTokenizer st = new StringTokenizer(Env.separator());
        Vector<String> splitName = new Vector<String>();
        while (st.hasMoreTokens()) {
            splitName.add(st.nextToken());
        }
        String shortName = splitName.get(splitName.size() - 1);
        if ((itemTable.containsKey(shortName)) && (!itemTable.get(shortName).equals(fullName))) {
            int i = splitName.size() - 2;
            boolean containsKey = true;
            while ((i >= 0) && containsKey) {
                shortName = splitName.get(i) + Env.separator() + shortName;
                if ((!itemTable.containsKey(shortName)) || (itemTable.get(shortName).equals(fullName))) {
                    containsKey = false;
                }
                i--;
            }
        }
        itemTable.put(shortName, fullName);
        return shortName;
    }

    public void removeRecent(String fullName) {
        if (itemTable.containsValue(fullName)) {
            for (Enumeration enumeration = itemTable.keys(); enumeration.hasMoreElements();) {
                Object key = enumeration.nextElement();
                if ((itemTable.get(key)).equals(fullName)) {
                    itemTable.remove(key);
                    break;
                }
            }
        }
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        TaskGraphFileHandler taskGraphFileHandler = applicationFrame.getTaskGraphFileHandler();
        taskGraphFileHandler.backgroundOpenTaskgraph(new File((String) itemTable.get(e.getActionCommand())));
    }
}
