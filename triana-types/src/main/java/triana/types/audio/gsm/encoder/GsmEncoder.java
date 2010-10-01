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

package triana.types.audio.gsm.encoder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.MenuShortcut;
import java.awt.Point;


public class GsmEncoder extends Frame {

    // The main Encoder object.
    Encoder coder = null;
    // The name of the sound file loaded by file chooser.
    String auFile = null;
    // The name of the encoded output file.
    String outputFile = null;

    public GsmEncoder() {
        setLayout(null);
        setBackground(java.awt.Color.lightGray);
        setSize(399, 229);
        setVisible(false);
        openFileDialog1.setMode(FileDialog.LOAD);
        openFileDialog1.setTitle("Open a sound file");
        encodeButton.setLabel("Encode File");
        add(encodeButton);
        encodeButton.setBackground(java.awt.Color.lightGray);
        encodeButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        encodeButton.setBounds(120, 156, 164, 37);
        textFieldSelectedFile.setEnabled(false);
        add(textFieldSelectedFile);
        textFieldSelectedFile.setBackground(java.awt.Color.white);
        textFieldSelectedFile.setForeground(java.awt.Color.black);
        textFieldSelectedFile.setFont(new Font("Dialog", Font.BOLD, 12));
        textFieldSelectedFile.setBounds(24, 36, 350, 26);
        label1.setText("Selected Sound / Au File");
        add(label1);
        label1.setBounds(24, 12, 180, 24);
        textFieldOutputFile.setEnabled(false);
        add(textFieldOutputFile);
        textFieldOutputFile.setBackground(java.awt.Color.white);
        textFieldOutputFile.setForeground(java.awt.Color.black);
        textFieldOutputFile.setFont(new Font("Dialog", Font.BOLD, 12));
        textFieldOutputFile.setBounds(24, 96, 350, 26);
        label2.setText("Output File");
        add(label2);
        label2.setBounds(24, 72, 180, 24);
        SaveFileDialog.setMode(FileDialog.SAVE);
        SaveFileDialog.setTitle("Save GSM file");
        setTitle("AWT Application");

        menu1.setLabel("File");
        menu1.add(newMenuItem);
        newMenuItem.setEnabled(false);
        newMenuItem.setLabel("New");
        newMenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_N, false));
        menu1.add(openMenuItem);
        openMenuItem.setLabel("Open...");
        openMenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_O, false));
        menu1.add(saveMenuItem);
        saveMenuItem.setLabel("Save");
        saveMenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_S, false));
        menu1.add(saveAsMenuItem);
        saveAsMenuItem.setEnabled(false);
        saveAsMenuItem.setLabel("Save As...");
        menu1.add(separatorMenuItem);
        separatorMenuItem.setLabel("-");
        menu1.add(exitMenuItem);
        exitMenuItem.setLabel("Exit");
        mainMenuBar.add(menu1);
        menu2.setLabel("Edit");
        menu2.add(cutMenuItem);
        cutMenuItem.setEnabled(false);
        cutMenuItem.setLabel("Cut");
        cutMenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_X, false));
        menu2.add(copyMenuItem);
        copyMenuItem.setEnabled(false);
        copyMenuItem.setLabel("Copy");
        copyMenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_C, false));
        menu2.add(pasteMenuItem);
        pasteMenuItem.setEnabled(false);
        pasteMenuItem.setLabel("Paste");
        pasteMenuItem.setShortcut(new MenuShortcut(java.awt.event.KeyEvent.VK_V, false));
        mainMenuBar.add(menu2);
        menu3.setLabel("Help");
        menu3.add(aboutMenuItem);
        aboutMenuItem.setLabel("About...");
        mainMenuBar.add(menu3);
        setMenuBar(mainMenuBar);

        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);
        SymAction lSymAction = new SymAction();
        openMenuItem.addActionListener(lSymAction);
        exitMenuItem.addActionListener(lSymAction);
        aboutMenuItem.addActionListener(lSymAction);
        SymMouse aSymMouse = new SymMouse();
        encodeButton.addMouseListener(aSymMouse);
        this.addMouseListener(aSymMouse);
        saveMenuItem.addActionListener(lSymAction);
    }

    public GsmEncoder(String title) {
        this();
        setTitle(title);
    }

    /**
     * Shows or hides the component depending on the boolean flag b.
     *
     * @param b if true, show the component; otherwise, hide the component.
     * @see java.awt.Component#isVisible
     */
    public void setVisible(boolean b) {
        if (b) {
            setLocation(50, 50);
        }
        super.setVisible(b);
    }

    static public void main(String args[]) {
        try {
            //Create a new instance of our application's frame, and make it visible.
            (new GsmEncoder("GSM Encoder")).setVisible(true);
        }
        catch (Throwable t) {
            System.err.println(t);
            t.printStackTrace();
            //Ensure the application exits with an error condition.
            System.exit(1);
        }
    }

    public void addNotify() {
        // Record the size of the window prior to calling parents addNotify.
        Dimension d = getSize();

        super.addNotify();

        if (fComponentsAdjusted) {
            return;
        }

        // Adjust components according to the insets
        setSize(getInsets().left + getInsets().right + d.width, getInsets().top + getInsets().bottom + d.height);
        Component components[] = getComponents();
        for (int i = 0; i < components.length; i++) {
            Point p = components[i].getLocation();
            p.translate(getInsets().left, getInsets().top);
            components[i].setLocation(p);
        }
        fComponentsAdjusted = true;
    }

    // Used for addNotify check.
    private boolean fComponentsAdjusted = false;

    java.awt.FileDialog openFileDialog1 = new java.awt.FileDialog(this);
    java.awt.Button encodeButton = new java.awt.Button();
    java.awt.TextField textFieldSelectedFile = new java.awt.TextField();
    java.awt.Label label1 = new java.awt.Label();
    java.awt.TextField textFieldOutputFile = new java.awt.TextField();
    java.awt.Label label2 = new java.awt.Label();
    java.awt.FileDialog SaveFileDialog = new java.awt.FileDialog(this);

    java.awt.MenuBar mainMenuBar = new java.awt.MenuBar();
    java.awt.Menu menu1 = new java.awt.Menu();
    java.awt.MenuItem newMenuItem = new java.awt.MenuItem();
    java.awt.MenuItem openMenuItem = new java.awt.MenuItem();
    java.awt.MenuItem saveMenuItem = new java.awt.MenuItem();
    java.awt.MenuItem saveAsMenuItem = new java.awt.MenuItem();
    java.awt.MenuItem separatorMenuItem = new java.awt.MenuItem();
    java.awt.MenuItem exitMenuItem = new java.awt.MenuItem();
    java.awt.Menu menu2 = new java.awt.Menu();
    java.awt.MenuItem cutMenuItem = new java.awt.MenuItem();
    java.awt.MenuItem copyMenuItem = new java.awt.MenuItem();
    java.awt.MenuItem pasteMenuItem = new java.awt.MenuItem();
    java.awt.Menu menu3 = new java.awt.Menu();
    java.awt.MenuItem aboutMenuItem = new java.awt.MenuItem();

    class SymWindow extends java.awt.event.WindowAdapter {
        public void windowClosing(java.awt.event.WindowEvent event) {
            Object object = event.getSource();
            if (object == GsmEncoder.this) {
                GsmEncoder_WindowClosing(event);
            }
        }
    }

    private void GsmEncoder_WindowClosing(java.awt.event.WindowEvent event) {
        System.exit(0);
    }

    class SymAction implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent event) {
            Object object = event.getSource();
            if (object == openMenuItem) {
                openMenuItem_ActionPerformed(event);
            } else if (object == aboutMenuItem) {
                aboutMenuItem_ActionPerformed(event);
            } else if (object == exitMenuItem) {
                exitMenuItem_ActionPerformed(event);
            } else if (object == saveMenuItem) {
                saveMenuItem_ActionPerformed(event);
            }
        }
    }


    private void openMenuItem_ActionPerformed(java.awt.event.ActionEvent event) {
        try {
            int defMode = openFileDialog1.getMode();
            String defTitle = openFileDialog1.getTitle();
            String defDirectory = openFileDialog1.getDirectory();
            String defFile = openFileDialog1.getFile();

            openFileDialog1 = new java.awt.FileDialog(this, defTitle, defMode);
            openFileDialog1.setDirectory(defDirectory);
            openFileDialog1.setFile(defFile);
            openFileDialog1.setVisible(true);

            setLoadFileName();

        }
        catch (Exception e) {
        }
    }

    /**
     * Set the input file name to be passed to the encoder.
     */
    private void setLoadFileName() {
        int defMode = openFileDialog1.getMode();
        String defTitle = openFileDialog1.getTitle();
        String defDirectory = openFileDialog1.getDirectory();
        String defFile = openFileDialog1.getFile();

        auFile = defDirectory + defFile;

        // Make sure we have the correct file type. Need to add file checking function here.
        while (!auFile.endsWith(".au") && defFile != null) {

//                    JOptionPane.showMessageDialog(this,
            //                                  "Incorrect file type!",
            //                                "Error",
            //                              JOptionPane.ERROR_MESSAGE);

            openFileDialog1 = new java.awt.FileDialog(this, defTitle, defMode);
            openFileDialog1.setDirectory(defDirectory);
            openFileDialog1.setFile(defFile);
            openFileDialog1.setVisible(true);
            defDirectory = openFileDialog1.getDirectory();
            defFile = openFileDialog1.getFile();

            auFile = defDirectory + defFile;
        }
        // Set the encode file text box.
        if (defDirectory != null && defFile != null) {
            textFieldSelectedFile.setText(auFile);
        }
    }

    private void aboutMenuItem_ActionPerformed(java.awt.event.ActionEvent event) {
//                JOptionPane.showMessageDialog(this,
//                   "\nGSM Encoder \n\n" +
//     "This is a Java port of the encoding side of the GSM library provided by Jutta \n" +
//   "Degener (jutta@cs.tu-berlin.de) and Carsten Bormann (cabo@cs.tu-berlin.de),\n" +
// "of Technische Universitaet Berlin. It is freely available for redistribution and/or \n" +
//         "modification under the terms of the GNU Library General Public License as \n" +
//       "published by the Free Software Foundation. Please refer to the included Readme \n" +
//      "file for more information. \n\n" +
//    "By Christopher Edwards Copyright (C) 1999 \n" +
//  " ", "Gsm Encoder - About", JOptionPane.PLAIN_MESSAGE);
    }

    // Close the main frame.

    private void exitMenuItem_ActionPerformed(java.awt.event.ActionEvent event) {
        System.exit(0);
    }

    class SymMouse extends java.awt.event.MouseAdapter {
        public void mouseReleased(java.awt.event.MouseEvent event) {
            Object object = event.getSource();
            if (object == encodeButton) {
                encodeButton_MouseReleased(event);
            }
        }
    }

    private void encodeButton_MouseReleased(java.awt.event.MouseEvent event) {
        try {
            startEncoder();
        }
        catch (Exception e) {
        }
    }

    /**
     * Check the file names and start the encoder. The Encoder itself will check file types and existance, etc.
     */
    private void startEncoder() {

        if (coder == null) {
            coder = new Encoder();
        } else {
            coder = null;
            coder = new Encoder();
        }

        // If we forgot to set the output file
        if (outputFile == null && auFile != null) {
            outputFile = auFile + ".gsm";

            try {
                coder.encode(auFile, outputFile);
            }
            catch (Exception e) { //JOptionPane.showMessageDialog(this,
//       e + "\n" ,
//     "Error",
//  JOptionPane.ERROR_MESSAGE);
            }
            textFieldOutputFile.setText(outputFile);

            // You forgot to choose a file to encode.
        } else if (auFile == null) {
//                        JOptionPane.showMessageDialog(this,
///                                  "Please choose a file to encode.\n" +
//                               "Click File then Open or Ctrl+O.",
//                             "Error",
//                           JOptionPane.ERROR_MESSAGE);
// Everything is OK.
        } else {
            try {
                coder.encode(auFile, outputFile);
            }
            catch (Exception e) {
            }
        }
    }


    private void saveMenuItem_ActionPerformed(java.awt.event.ActionEvent event) {
        try {
            // SaveFileDialog Show the FileDialog
            SaveFileDialog.setVisible(true);
        }
        catch (Exception e) {
        }

        setOutputFileName();
    }

    /**
     * Set the Output file name to be passed to the encoder.
     */
    private void setOutputFileName() {
        int defMode = SaveFileDialog.getMode();
        String defTitle = SaveFileDialog.getTitle();
        String defDirectory = SaveFileDialog.getDirectory();
        String defFile = SaveFileDialog.getFile();

        if (defFile == null && auFile != null) {
            outputFile = auFile + ".gsm";
            // Set the Output file name in the text box.
            textFieldOutputFile.setText(outputFile);
        } else if (defDirectory != null && defFile != null) {
            outputFile = defDirectory + defFile + ".gsm";
            // Set the Output file name in the text box.
            textFieldOutputFile.setText(outputFile);
        } else {
//                        JOptionPane.showMessageDialog(this,
//                                  "Please choose a file to encode.\n" +
//                                "Click File then Open or Ctrl+O.\n" +
//                              "or you can Choose a file to save first\n" +
//                            "by Clicking File the Save or Ctrl+S" ,
//                          "Information",
//                        JOptionPane.INFORMATION_MESSAGE);
        }


    }


}
