/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.gui.panels;

import org.trianacode.gui.Display;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class OptionPane {


    public static void showInformation(String msg, String title, Component parent) {
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE, GUIEnv.getTrianaIcon());
    }

    public static void showError(String msg, String title, Component parent) {
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
    }

    public static void showErrorLater(final String msg, final String title, final Component parent) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
            }
        });
    }


    public static boolean showOkCancel(String msg, String title, Component parent) {
        int reply = JOptionPane
                .showConfirmDialog(parent, msg, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        GUIEnv.getTrianaIcon());
        if (reply == JOptionPane.OK_OPTION) {
            return true;
        }
        return false;
    }

    public static void showException(Exception e) {
        final JDialog showit = new JDialog(GUIEnv.getApplicationFrame(), e.getClass().getName(), true);

        JButton ok = new JButton("OK");
        ok.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showit.dispose();
                    }
                });
        String text = e.getMessage();
        StringWriter ss = new StringWriter();
        PrintWriter sr = new PrintWriter(ss);
        e.printStackTrace(sr);
        text += "\n";
        text += Env.getString("FullTrace") +
                "\n\n" + ss.toString();

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
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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

    public static void showInformation(String msg, String title) {
        showInformation(msg, title, GUIEnv.getApplicationFrame());
    }

    public static void showError(String msg, String title) {
        showError(msg, title, GUIEnv.getApplicationFrame());
    }

    public static boolean showOkCancel(String msg, String title) {
        return showOkCancel(msg, title, GUIEnv.getApplicationFrame());
    }

}
