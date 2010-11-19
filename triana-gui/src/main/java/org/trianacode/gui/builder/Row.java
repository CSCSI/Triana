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
package org.trianacode.gui.builder;

import org.trianacode.gui.panels.TFileChooser;
import org.trianacode.gui.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * This is a class used to represt one row of the interface builder's building screen. A JTextField followed by a
 * JComboBox and then 3 JTextFields.  We include here convenient methods for setting the various fields and gettting to
 * the various fields.
 * <p/>
 * <p> Its more general use though is in GUICreaterPanel where it is again used to store each row of the user
 * interface's information.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public class Row extends JPanel implements ActionListener {

    public static final int INTSCROLLER = 1;
    public static final int SCROLLER = 2;
    public static final int CHECKBOX = 3;
    public static final int TEXTFIELD = 4;
    public static final int CHOICE = 5;
    public static final int FILE = 6;
    public static final int LABEL = 7;
    public static final int TEXTAREA = 8;

    private int type;

    /**
     * the name of the parameter represented by this row
     */
    private String param;

    /**
     * used for scroller parameter input
     */
    private Scroller scroll = null;

    /**
     * used for file parameter input
     */
    private JButton browse;
    private Filter filefilter;


    /**
     * used for textfield, checkbox and choice parameter input
     */
    private JComponent paramcomp = null;


    public Row(Vector<String> line, GUICreaterPanel listener) {
        creater(line, listener);
    }

    /**
     * Each line given to a Row in order to initialise it is given in the following format :-</p>
     * <p/>
     * <pre>this is the title $title paramName GUItype parameters</pre>
     * <p/>
     * This function retruns the title of the parameter
     */
    public static Vector<String> getParTitle(Vector<String> sv) {
        Vector<String> s = new Vector<String>();
        for (int i = 0; i < sv.size(); ++i) {
            if (!sv.get(i).equalsIgnoreCase("$title")) {
                s.addElement(sv.get(i));
            } else {
                return s;
            }
        }

        return new Vector<String>(); // no title yet
    }

    public static String getParTitleAsString(Vector<String> sv) {
        Vector<String> vec = getParTitle(sv);
        if (vec.size() == 0) {
            return "";
        }
        String s = "";
        for (int i = 0; i < vec.size(); ++i) {
            s = s + vec.get(i) + " ";
        }
        s = s + "\n";
        return s;

    }

    /**
     * Each line given to a Row in order to initialise it is given in the following format :-</p>
     * <p/>
     * <pre>this is the title $title paramName GUItype parameters</pre>
     * <p/>
     * This function returns everything but the title of the parameter
     */
    public static Vector<String> getRest(Vector<String> sv) {
        Vector<String> s = new Vector<String>();
        int j = 0;
        while ((j < sv.size()) && (!sv.get(j).equalsIgnoreCase("$title"))) {
            ++j;
        }

        if (j == sv.size()) // no title this time
        {
            return sv;
        }

        for (int i = j + 1; i < sv.size(); ++i) {
            s.addElement(sv.get(i));
        }

        return s;
    }


    /**
     * Tests if these two Row have the same type
     */
    public boolean equals(Object row) {
        if (row instanceof Row) {
            if (((Row) row).type == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the string representation of the type represented by this Row object
     */
    public String typeToString() {
        if (type == SCROLLER) {
            return "Scroller";
        } else if (type == INTSCROLLER) {
            return "IntScroller";
        } else if (type == TEXTFIELD) {
            return "TextField";
        } else if (type == TEXTAREA) {
            return "TextArea";
        } else if (type == CHECKBOX) {
            return "Checkbox";
        } else if (type == CHOICE) {
            return "Choice";
        } else if (type == FILE) {
            return "File";
        } else if (type == LABEL) {
            return "Label";
        } else {
            return "undefined";
        }
    }


    /**
     * @return the type of this row e.g. SCROLLER, CHECKBOX
     */
    public int getType() {
        return type;
    }


    /**
     * Creates a new row for the GUI interface from the various arguments contained within a StringVector.
     */
    public void creater(Vector<String> line, GUICreaterPanel listener) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("  " + getParTitleAsString(line).trim() + "  ", JLabel.CENTER);
        line = getRest(line);
        param = line.get(0);

        if ((line.get(1).equals("Scroller")) || line.get(1).equals("IntScroller")) {
            double min = 0.0;
            double max = 0.0;
            double cur = 0.0;

            if (line.size() > 1)  // got a min
            {
                min = Double.parseDouble(line.get(2));
            }
            if (line.size() > 2)  // got a max
            {
                max = Double.parseDouble(line.get(3));
            }
            if (line.size() > 3)  // got a min
            {
                cur = Double.parseDouble(line.get(4));
            }

            if (line.get(1).equals("Scroller")) {
                scroll = new Scroller(Scroller.FLOAT, min, max, cur);
                type = SCROLLER;
            } else {
                scroll = new Scroller(Scroller.INTEGER, min, max, cur);
                type = INTSCROLLER;
            }

            JPanel tmppanel = new JPanel(new BorderLayout());
            tmppanel.add(title, BorderLayout.NORTH);
            tmppanel.add(scroll.getScrollbar(), BorderLayout.CENTER);

            JPanel displaypanel = new JPanel(new BorderLayout());
            displaypanel.add(scroll.getDisplay(), BorderLayout.NORTH);
            tmppanel.add(displaypanel, BorderLayout.EAST);

            tmppanel.setBorder(new EmptyBorder(0, 0, 3, 0));
            add(tmppanel, BorderLayout.CENTER);

//            scroll.getScrollbar ().addChangeListener(listener);
            scroll.getDisplay().addActionListener(listener);

        } else if (line.get(1).equals("Checkbox")) {
            type = CHECKBOX;

            if (line.size() > 2)  // got a CHECKBOX value
            {
                paramcomp = new JCheckBox("", Boolean.parseBoolean(line.get(2)));
            } else {
                paramcomp = new JCheckBox("", false);
            }

            title.setHorizontalAlignment(JLabel.RIGHT);

            JPanel tmppanel = new JPanel(new BorderLayout());
            tmppanel.add(title, BorderLayout.CENTER);
            tmppanel.add(paramcomp, BorderLayout.EAST);
            tmppanel.setBorder(new EmptyBorder(0, 0, 3, 0));
            add(tmppanel, BorderLayout.WEST);

            ((JCheckBox) paramcomp).addItemListener(listener);
        } else if (line.get(1).equals("TextField")) {
            type = TEXTFIELD;

            if (line.size() > 2) { // got an element in TextField
                String ln = "";
                for (int i = 2; i < line.size(); ++i) {
                    ln += line.get(i) + " ";
                }
                ln = ln.trim();

                paramcomp = new JTextField(ln, 20);
            } else {
                paramcomp = new JTextField(20);
            }

            title.setHorizontalAlignment(JLabel.RIGHT);

            JPanel tmppanel = new JPanel(new BorderLayout());
            tmppanel.add(title, BorderLayout.CENTER);
            tmppanel.add(paramcomp, BorderLayout.EAST);
            tmppanel.setBorder(new EmptyBorder(0, 0, 3, 0));
            add(tmppanel, BorderLayout.WEST);

            ((JTextField) paramcomp).addActionListener(listener);
            paramcomp.addFocusListener(listener);
        } else if (line.get(1).equals("TextArea")) {
            type = TEXTAREA;

            if (line.size() > 2) { // got an element in TextField
                String ln = "";
                for (int i = 2; i < line.size(); ++i) {
                    ln += line.get(i) + " ";
                }
                ln = ln.trim();
                paramcomp = new JTextArea(5, 20);
                ((JTextArea) paramcomp).setText(ln);
            } else {
                paramcomp = new JTextArea(5, 20);
            }
            ((JTextArea) paramcomp).setLineWrap(false);
            ((JTextArea) paramcomp).setWrapStyleWord(true);
            title.setHorizontalAlignment(JLabel.LEFT);

            JPanel tmppanel = new JPanel(new BorderLayout());
            tmppanel.add(title, BorderLayout.NORTH);
            JScrollPane scroll = new JScrollPane(paramcomp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            title.setBorder(new EmptyBorder(0, 0, 3, 0));
            tmppanel.add(scroll, BorderLayout.CENTER);
            tmppanel.setBorder(new EmptyBorder(0, 0, 3, 0));
            add(tmppanel, BorderLayout.CENTER);

            paramcomp.addFocusListener(listener);
        } else if (line.get(1).equals("Choice")) {
            type = CHOICE;

            paramcomp = new JComboBox();

            if (line.size() > 2) {
                if (line.get(2).startsWith("[")) {
                    String choice = "";

                    for (int count = 2; count < line.size(); count++) {
                        if (!(choice.equals(""))) {
                            choice += " ";
                        }

                        choice += line.get(count);

                        if (choice.endsWith("]")) {
                            choice = choice.substring(1, choice.length() - 1);
                            ((JComboBox) paramcomp).addItem(choice);

                            choice = "";
                        }
                    }
                } else {
                    for (int count = 2; count < line.size(); ++count) {
                        ((JComboBox) paramcomp).addItem(line.get(count));
                    }
                }

                ((JComboBox) paramcomp).setSelectedItem(line.get(2));
            }


            title.setHorizontalAlignment(JLabel.RIGHT);

            JPanel tmppanel = new JPanel(new BorderLayout());
            tmppanel.add(title, BorderLayout.CENTER);
            tmppanel.add(paramcomp, BorderLayout.EAST);
            tmppanel.setBorder(new EmptyBorder(0, 0, 3, 0));
            add(tmppanel, BorderLayout.WEST);

            ((JComboBox) paramcomp).addActionListener(listener);
        } else if (line.get(1).equals("File")) {
            type = FILE;

            title.setHorizontalAlignment(JLabel.RIGHT);
            paramcomp = new JTextField(20);

            if ((line.size() > 2) && (!line.get(2).equals("null"))) {
                ((JTextField) paramcomp).setText(line.get(2));
            }

            if (line.size() > 3) {
                filefilter = new Filter();

                for (int count = 3; (count < line.size()) && (filefilter != null); ++count) {
                    if (line.get(count).equals("*.*")) {
                        filefilter = null;
                    } else {
                        String entry = line.get(count);

                        if (entry.lastIndexOf('.') > -1) {
                            filefilter.addEnding(entry.substring(entry.lastIndexOf('.') + 1));
                        }
                    }
                }
            }

            browse = new JButton(Env.getString("Select"));
            browse.addActionListener(this);

            JPanel parampanel = new JPanel(new BorderLayout());
            parampanel.add(paramcomp, BorderLayout.SOUTH);

            JPanel filepanel = new JPanel(new BorderLayout());
            filepanel.add(title, BorderLayout.WEST);
            filepanel.add(parampanel, BorderLayout.CENTER);
            filepanel.add(browse, BorderLayout.EAST);

            //JPanel titlepanel = new JPanel(new BorderLayout());
            //titlepanel.add(title, BorderLayout.SOUTH);

            JPanel tmppanel = new JPanel(new BorderLayout());
            //tmppanel.add(titlepanel, BorderLayout.WEST);
            tmppanel.add(filepanel, BorderLayout.EAST);
            tmppanel.setBorder(new EmptyBorder(0, 0, 3, 0));
            add(tmppanel, BorderLayout.WEST);

            int yshift = (browse.getPreferredSize().height - parampanel.getPreferredSize().height) / 2;
            parampanel.setBorder(new EmptyBorder(0, 0, yshift, 3));
            title.setBorder(new EmptyBorder(0, 0, yshift, 0));

            browse.addActionListener(listener);
            ((JTextField) paramcomp).addActionListener(listener);
            paramcomp.addFocusListener(listener);
        } else if (line.get(1).equals("Label")) {
            type = LABEL;

            if (line.size() > 2) { // got an element in Label
                String ln = "";
                for (int i = 2; i < line.size(); ++i) {
                    ln += line.get(i) + " ";
                }
                ln = ln.trim();

                paramcomp = new JLabel(ln);
            } else {
                paramcomp = new JLabel();
            }

            title.setHorizontalAlignment(JLabel.RIGHT);

            JPanel tmppanel = new JPanel(new BorderLayout());
            tmppanel.add(title, BorderLayout.CENTER);
            tmppanel.add(paramcomp, BorderLayout.EAST);
            tmppanel.setBorder(new EmptyBorder(0, 0, 3, 0));
            add(tmppanel, BorderLayout.WEST);
        }
    }

    /**
     * @return the value of this row as a string.
     */
    public String getValue() {
        if (type == INTSCROLLER) {
            return scroll.getDisplay().getText();
        } else if (type == SCROLLER) {
            return scroll.getDisplay().getText();
        } else if (type == TEXTFIELD) {
            return ((JTextField) paramcomp).getText();
        } else if (type == TEXTAREA) {
            return ((JTextArea) paramcomp).getText();
        } else if (type == CHECKBOX) {
            return String.valueOf(((JCheckBox) paramcomp).isSelected());
        } else if (type == CHOICE) {
            return (String) ((JComboBox) paramcomp).getSelectedItem();
        } else if (type == FILE) {
            return ((JTextField) paramcomp).getText();
        } else if (type == LABEL) {
            return ((JLabel) paramcomp).getText();
        } else {
            return null;
        }
    }

    /**
     * Sets the value for this particular Row.
     */
    public void setValue(String value) {
        if (type == SCROLLER) {
            scroll.setValue(Double.parseDouble(value));
        } else if (type == INTSCROLLER) {
            scroll.setValue(Integer.parseInt(value));
        } else if (type == CHECKBOX) {
            ((JCheckBox) paramcomp).setSelected(Boolean.parseBoolean(value));
        } else if (type == TEXTFIELD) {
            ((JTextField) paramcomp).setText(value);
        } else if (type == TEXTAREA) {
            ((JTextArea) paramcomp).setText(value);
        } else if (type == CHOICE) {
            ((JComboBox) paramcomp).setSelectedItem(value);
        } else if (type == FILE) {
            ((JTextField) paramcomp).setText(value);
        } else if (type == LABEL) {
            ((JLabel) paramcomp).setText(value);
        }
    }

    /**
     * @return the parameter name for this row i.e. what parameter this row is used to edit.
     */
    public String getParameterName() {
        return param;
    }


    /**
     * @return true if this row uses the specified component
     */
    public boolean containsComponent(Component comp) {
        if ((comp != null) && (comp == paramcomp)) {
            return true;
        } else if ((scroll != null) && (comp == scroll.getScrollbar())) {
            return true;
        } else if ((scroll != null) && (comp == scroll.getDisplay())) {
            return true;
        } else if ((browse != null) && (comp == browse)) {
            return true;
        } else {
            return false;
        }
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == browse) {
            TFileChooser chooser = new TFileChooser(Env.DATA_DIRECTORY);
            File current = new File(((JTextField) paramcomp).getText());

            if (current.isDirectory()) {
                chooser.setCurrentDirectory(current);
            } else if (current.exists()) {
                chooser.setSelectedFile(current);
            }

            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setMultiSelectionEnabled(false);

            if (filefilter != null) {
                chooser.setFileFilter(filefilter);
            }

            int retval = chooser.showDialog(this, Env.getString("OK"));

            if (retval == chooser.APPROVE_OPTION) {
                ((JTextField) paramcomp).setText(chooser.getSelectedFile().getAbsolutePath());
                paramcomp.requestFocus();
            }
        }
    }


    private class Filter extends FileFilter {

        private ArrayList endings = new ArrayList();
        private String description = "";


        public void addEnding(String ending) {
            endings.add(ending);

            if (description.equals("")) {
                description = "*." + ending;
            } else {
                description = description + " *." + ending;
            }
        }


        public String getDescription() {
            return description;
        }

        public boolean accept(File file) {
            Iterator iter = endings.iterator();
            boolean accept = file.isDirectory();

            while (iter.hasNext() && (!accept)) {
                accept = file.getName().endsWith((String) iter.next());
            }

            return accept;
        }

    }
}














