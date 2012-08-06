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
package org.trianacode.gui.windows;

import org.trianacode.gui.Display;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * Just calls the JOptionPane with the appropriate values for an ErrorDialog
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public class ErrorDialog implements Serializable {

    /**
     * Constructs an ErrorDialog displaying the given text
     */
    public ErrorDialog(String text) {
        this(Env.getString("RunTimeError"), text);
    }

    /**
     * This class creates a basic modal window for Errors mainly. It displays an error message along with a specified
     * title.
     */
    public ErrorDialog(String title, String text) {
        show(title, text);
    }

    /**
     * Constructs an ErrorDialog displaying the given text
     */
    public static void show(String text) {
        show(Env.getString("RunTimeError"), text);
    }

    /**
     * This static function creates a basic modal window for Errors. It displays an error message along with a specified
     * title.
     */
    public static void show(final String title, final String text) {
        final JFrame showit = new JFrame(title);
        showit.setSize(400,600);

        JButton ok = new JButton("OK");
        ok.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showit.dispose();
                    }
                });

        JPanel buttonpanel = new JPanel(new FlowLayout());
        buttonpanel.add(ok);

        JTextArea textarea = new JTextArea(text);
        textarea.setEditable(false);
        textarea.setBackground(ok.getBackground());
        textarea.setBorder(new EmptyBorder(3, 3, 3, 3));

        ImageIcon ima = GUIEnv.getTrianaIcon();
        JLabel icon = new JLabel(ima);
        icon.setBorder(new EmptyBorder(3, 3, 3, 3));
        JScrollPane scroll = new JScrollPane(textarea);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        showit.getContentPane().setLayout(new BorderLayout());
        showit.getContentPane().add(scroll, BorderLayout.EAST);
        showit.getContentPane().add(icon, BorderLayout.WEST);
        showit.getContentPane().add(buttonpanel, BorderLayout.SOUTH);

        showit.pack();
        Display.centralise(showit);
        showit.setVisible(true);
        showit.toFront();
    }

    /**
     * Constructs an ErrorDialog displaying the given text, a new line, followed by brief "Full Stack Trace" message
     * with the full stack trace of the given exception. If the String text is null the just the Exception's trace is
     * output with no preceeding message
     */
    public static void show(JFrame parent, String text, Throwable e) {
        StringWriter ss = new StringWriter();
        PrintWriter sr = new PrintWriter(ss);
        e.printStackTrace(sr);
        if (text == null) {
            text = "";
        } else {
            text += "\n";
        }

        boolean disp = false;

        if (parent == null) {
            disp = true;
            parent = new JFrame();
        }

        show(text + Env.getString("FullTrace") +
                "\n\n" + ss.toString());
        try {
            sr.close();
            ss.close();
        } catch (Exception ee) {
        }

        if (disp) {
            parent.dispose();
        }
    }

    /**
     * Constructs an ErrorDialog displaying the given Exception
     */
    public static void show(String text, Exception e) {
        show(null, text, e);
    }

    /**
     * Constructs an ErrorDialog displaying the given Exception
     */
    public static void show(JFrame parent, Exception e) {
        show(parent, null, e);
    }

    /**
     * Constructs an ErrorDialog displaying the given Exception
     */
    public static void show(Exception e) {
        show(null, null, e);
    }
}


/** OLD CODE FOR JDIALOG
 // FileUtils.playSystemAudio("trianaError.wav");

 boolean disp=false;

 if (parent==null) {
 disp=true;
 parent=new JFrame();
 }

 final JFrame par = parent;

 final Runnable r = new Runnable() {
 public void run() {
 JOptionPane.showMessageDialog(par,
 text, title, JOptionPane.ERROR_MESSAGE);
 }
 };

 Thread t = new Thread (r) {
 public void run() {
 try {
 SwingUtilities.invokeAndWait(r);
 } catch (Exception e) {
 e.printStackTrace();
 }
 }
 };

 t.start();

 if (disp)
 parent.dispose();

 **/




