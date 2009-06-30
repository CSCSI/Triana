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
package org.trianacode.gui.help.search;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class FindWordDialog extends JDialog {

    public static int TOOLS = 0;
    public static int MAIN = 1;
    public final int mode;

    private Vector listeners;

    private HTMLSearchResults results;
    private JButton goButton;
    private JButton closeButton;
    private JTextField wordField;
    private JTextField wordFieldToolbox;

    private JList wordList, documentList;
    private String[] wordArray;
    private HTMLDocumentInfo[] infoArray;

    public FindWordDialog(JFrame frame, HTMLSearchResults results, final int mode) {
        super(frame, "Find word");
        this.mode = mode;

        this.results = results;
        wordArray = results.getWordArray();
        // sort(wordArray);

        goButton = new JButton("Show");
        goButton.addActionListener(new GoButtonAction());
        goButton.setEnabled(false);

        closeButton = new JButton("Close");
        closeButton.addActionListener(new CloseButtonAction());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(goButton);
        buttonPanel.add(closeButton);

        wordField = new JTextField(20);
        wordField.addKeyListener(new WordFieldKeyListener());

        wordFieldToolbox = new JTextField(20);

        JPanel wordPanel = new JPanel(new BorderLayout(2, 2));
        wordPanel.add(new JLabel("Word"), BorderLayout.NORTH);
        wordPanel.add(wordField, BorderLayout.CENTER);

        wordPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        wordList = new JList(wordArray);
        wordList.addListSelectionListener(new WordListListener());

        JScrollPane wordScroller = new JScrollPane(wordList);
        wordScroller.setPreferredSize(new Dimension(400, 150));
        wordScroller.setMinimumSize(new Dimension(400, 150));
        wordScroller.setAlignmentX(LEFT_ALIGNMENT);

        JPanel wordListPanel = new JPanel(new BorderLayout(2, 2));
        wordListPanel.add(new JLabel("Word list"), BorderLayout.NORTH);
        wordListPanel.add(wordScroller, BorderLayout.CENTER);

        documentList = new JList();
        documentList.addListSelectionListener(new DocumentListListener());
        documentList.setEnabled(false);

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    HTMLDocumentInfo info = infoArray[documentList.getSelectedIndex()];
                    despatchFindWordEvent((String) wordList.getSelectedValue(),
                                          info.getTitle(), info.getFile());
                }
                else if (mode == TOOLS) {
                    HTMLDocumentInfo info = infoArray[documentList.getSelectedIndex()];
                    String file = info.getFile().getAbsolutePath();
                    String toolbox = file.substring(0, file.lastIndexOf(File.separator) - 1);
                    toolbox = toolbox.substring(toolbox.indexOf("toolbox") + 10, toolbox.lastIndexOf(File.separator));
                    // toolbox = toolbox.substring(0, toolbox.lastIndexOf(File.separator)+1);
                    wordFieldToolbox.setText(toolbox);
                }

            }
        };

        documentList.addMouseListener(mouseListener);

        JScrollPane documentScroller = new JScrollPane(documentList);
        documentScroller.setPreferredSize(new Dimension(400, 150));
        documentScroller.setMinimumSize(new Dimension(400, 150));
        documentScroller.setAlignmentX(LEFT_ALIGNMENT);

        JPanel documentPanel = new JPanel(new BorderLayout(2, 2));
        documentPanel.add(new JLabel("Found documents"), BorderLayout.NORTH);
        documentPanel.add(documentScroller, BorderLayout.CENTER);

        if (mode == TOOLS) {
            JPanel bottombit = new JPanel();

            bottombit.add(new JLabel("Contained in Toolbox :"));
            bottombit.add((wordFieldToolbox));
            documentPanel.add(bottombit, BorderLayout.SOUTH);
        }

        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        mainPanel.add(wordListPanel);
        mainPanel.add(documentPanel);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(wordPanel, BorderLayout.NORTH);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        contentPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setContentPane(contentPane);

        getRootPane().setDefaultButton(goButton);
        pack();
        centerOnScreen();

        listeners = new Vector();
    }

    public void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();

        setLocation((screenSize.width - frameSize.width) / 2,
                    (screenSize.height - frameSize.height) / 2);
    }

    public void addFindWordListener(FindWordListener listener) {
        listeners.addElement(listener);
    }

    public void removeFindWordListener(FindWordListener listener) {
        listeners.removeElement(listener);
    }

    protected void despatchFindWordEvent(FindWordEvent event) {
        FindWordListener listener;

        for (Enumeration enumeration = listeners.elements(); enumeration.hasMoreElements();) {
            listener = (FindWordListener) enumeration.nextElement();
            listener.wordFound(event);
        }
    }

    protected void despatchFindWordEvent(String word, String title, File file) {
        despatchFindWordEvent(new FindWordEvent(this, word, title, file));
    }

    /*
    private static void quickSort(String[] array, int min, int max, int depth) {
      Collator collator = Collator.getInstance();
      int left = min;
      int right = max;
      String midValue, temp;

      System.out.println("Depth=" + depth);

      if (max > min) {
        midValue = array[(min + max) / 2];

        while (left <= right) {

          while((left < max) && (collator.compare(array[left], midValue) < 0))
            left ++;

          while((right > min) && (collator.compare(array[left], midValue) > 0))
            right --;

          if(left <= right) {
            temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left ++;
            right --;
          }
        }

        if(min < right) quickSort(array, min, right, depth + 1);

        if(left < max) quickSort(array, left, max, depth + 1);
      }
    }

    public static void sort(String[] array) {
      quickSort(array, 0, array.length - 1, 0);
    }
    */

    private class CloseButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    }

    private class GoButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            HTMLDocumentInfo info = infoArray[documentList.getSelectedIndex()];
            despatchFindWordEvent((String) wordList.getSelectedValue(),
                                  info.getTitle(), info.getFile());
        }
    }

    private class WordListListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            String word = wordArray[e.getLastIndex()];
            Vector vector = results.get(word);

            if (vector != null) {
                String[] titleArray = new String[vector.size()];
                int index = 0;

                infoArray = new HTMLDocumentInfo[vector.size()];

                for (Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();) {
                    infoArray[index] = (HTMLDocumentInfo) enumeration.nextElement();
                    titleArray[index] = infoArray[index].getTitle();
                    index++;
                }

                documentList.setListData(titleArray);
                documentList.setEnabled(true);
                goButton.setEnabled(false);
            }
        }
    }

    private class DocumentListListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            goButton.setEnabled(true);
        }
    }

    private class WordFieldKeyListener implements KeyListener {
        public void keyPressed(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
            String text = wordField.getText().toLowerCase();

            for (int i = 0; i < wordArray.length; i++) {
                if (wordArray[i].startsWith(text)) {
                    wordList.setSelectedIndex(i);
                    wordList.ensureIndexIsVisible(i);
                    return;
                }
            }
        }

        public void keyTyped(KeyEvent e) {
        }
    }
}
