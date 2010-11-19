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

import org.trianacode.gui.Display;
import org.trianacode.gui.util.Env;
import org.trianacode.taskgraph.util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * What it says on the tin... About Dialog 'nuff said.
 *
 * @author unknown probably Melanie
 * @version $Revision: 4048 $
 */
public class AboutDialog extends JDialog {
    protected JPanel image = null;
    protected boolean viewlicense = false;
    protected JButton viewButton;
    protected JButton closeButton;
    protected CardLayout cardLayout;
    protected JPanel dataPane;
    protected JTextArea textArea;

    public AboutDialog(Frame frame, boolean viewlicense) {
        super(frame, "About Triana", true);

        viewButton = new JButton("View license");
        viewButton.addActionListener(new ViewButtonAction());
        closeButton = new JButton("Close");
        closeButton.addActionListener(new CloseButtonAction());
        JPanel buttonPane = new JPanel(new FlowLayout());
        buttonPane.add(closeButton);
        buttonPane.add(viewButton);

        image = Display.getTrianaLogo();

        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        setVisible(false);
                    }
                });

        dataPane = new JPanel(cardLayout = new CardLayout());
        dataPane.add(image, "about");
        dataPane.add(new JScrollPane(textArea = new JTextArea(5, 5)), "license");
        textArea.setText(FileUtils.readFile(Env.home() + "triana_license.txt"));
        textArea.setFont(new Font("MonoSpaced", Font.PLAIN, 11));
        cardLayout.show(dataPane, "about");

        if (viewlicense) {
            cardLayout.show(dataPane, "license");
            viewButton.setText("About");
        }

        this.viewlicense = viewlicense;

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.add(dataPane, BorderLayout.CENTER);
        JPanel inner = new JPanel(new BorderLayout());
        inner.add(buttonPane, BorderLayout.NORTH);
        contentPane.add(inner, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(closeButton);

        centerOnScreen();
        pack();
        setVisible(true);
    }

    public AboutDialog(Frame frame) {
        this(frame, false);
    }

    public void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();

        setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
    }

    private class ViewButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (viewlicense = !viewlicense) {
                cardLayout.show(dataPane, "license");
                viewButton.setText("About");
                setTitle("Triana Software license");
            } else {
                cardLayout.show(dataPane, "about");
                viewButton.setText("View license");
                setTitle("About Triana");
            }
        }
    }

    private class CloseButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    }
}
