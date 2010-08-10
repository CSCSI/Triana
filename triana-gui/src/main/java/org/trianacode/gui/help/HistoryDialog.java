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
package org.trianacode.gui.help;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @version $Revision: 4048 $
 */
public class HistoryDialog extends JDialog {
    UrlHistory urlHistory;
    JButton closeButton, goButton;
    JList list;

    private URL selectedURL;

    public HistoryDialog(Frame parent, String title, boolean modal,
                         UrlHistory urlHistory) {
        super(parent, title, modal);
        this.urlHistory = urlHistory;

        layoutGUI();
        centerOnScreen();
    }

    public HistoryDialog(Frame parent, String title, UrlHistory urlHistory) {
        this(parent, title, false, urlHistory);
    }

    public HistoryDialog(Frame parent, String title) {
        this(parent, title, false, new UrlHistory());
    }

    public HistoryDialog(Frame parent, UrlHistory urlHistory) {
        this(parent, "", false, urlHistory);
    }

    public HistoryDialog(Frame parent) {
        this(parent, "", false, new UrlHistory());
    }

    public HistoryDialog(UrlHistory urlHistory) {
        this(null, "", false, urlHistory);
    }

    public HistoryDialog() {
        this(null, "", false, new UrlHistory());
    }

    public void layoutGUI() {
        // Create the buttons
        closeButton = new JButton("Close");
        closeButton.addActionListener(new CloseButtonAction());
        closeButton.setMnemonic('c');

        goButton = new JButton("Go");
        goButton.addActionListener(new GoButtonAction());
        goButton.setMnemonic('g');

        // Create the list
        list = new JList();

        // Create a scroller to contain the list object
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(400, 200));
        listScroller.setMinimumSize(new Dimension(400, 200));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        //Lay out the label and scroll pane from top to bottom.
        JPanel listPane = new JPanel(new BorderLayout(2, 2));
        listPane.add(new Label("Pages visited..."), BorderLayout.NORTH);
        listPane.add(listScroller, BorderLayout.CENTER);
        listPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout());
        buttonPane.add(closeButton);
        buttonPane.add(goButton);

        // Layout the dialog
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);
        setContentPane(contentPane);

        // Set the default button
        getRootPane().setDefaultButton(goButton);

        pack();
    }

    public void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();

        setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
    }

    public void setUrlHistory(UrlHistory urlHistory) {
        this.urlHistory = urlHistory;
    }

    public URL showDialog() {
        // We don't just set the list content to be urlHistory (ie a vector)
        // as the size of it may be larger than the actual history we wish
        // to include.

        int count = urlHistory.countUrls();
        URL[] urls = new URL[count];
        for (int i = 0; i < count; i++) {
            urls[i] = (URL) urlHistory.elementAt(i);
        }

        list.setListData(urls);
        setVisible(true);

        return selectedURL;
    }

    private class CloseButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            selectedURL = null;
        }
    }

    private class GoButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            selectedURL = (URL) list.getSelectedValue();
        }
    }
}



