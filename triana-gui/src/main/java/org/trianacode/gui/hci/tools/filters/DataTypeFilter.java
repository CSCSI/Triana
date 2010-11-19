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
package org.trianacode.gui.hci.tools.filters;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.hci.ToolFilter;
import org.trianacode.gui.util.Env;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.TypeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

/**
 * A filter that sorts tools by sub-package first, e.g. SignalProc.Input becomes Input.SignalProc
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class DataTypeFilter implements ToolFilter {

    public static final String INPUT_EXPLICIT_PACKAGE = "Input (explicit)";
    public static final String INPUT_ASSIGN_PACKAGE = "Input";
    public static final String OUTPUT_EXPLICIT_PACKAGE = "Output (explicit)";
    public static final String OUTPUT_ASSIGN_PACKAGE = "Output";
    public static final String IN_OUT_EXPLICIT_PACKAGE = "Input&Output (explicit)";
    public static final String IN_OUT_ASSIGN_PACKAGE = "Input&Output";


    /**
     * The types selected when the filter was initialised
     */
    private String[] types = null;


    /**
     * @return the name of this filter
     */
    public String getName() {
        return "Data Type Tools";
    }

    /**
     * @return the root for the tool tree
     */
    public String getRoot() {
        if (types == null) {
            return DEFAULT_ROOT;
        } else {
            String rootstr = "";

            for (int count = 0; (count < 2) && (count < types.length); count++) {
                if (count > 0) {
                    rootstr += ", ";
                }

                if (types[count].lastIndexOf(".") == -1) {
                    rootstr += types[count];
                } else {
                    rootstr += types[count].substring(types[count].lastIndexOf('.') + 1);
                }
            }

            if (types.length > 2) {
                rootstr += ", ...";
            }

            return rootstr;
        }
    }

    /**
     * @return the name of this filter
     */
    public String toString() {
        return getName();
    }


    /**
     * @return the filtered package for the tool, null if the tool is to be ignored. (e.g. a tool in SignalPro.Input
     *         could become Input.SignalProc)
     */
    public String[] getFilteredPackage(Tool tool) {
        if (types == null) {
            return new String[]{tool.getToolPackage()};
        }

        Class[] userClasses = TypeUtils.classForTrianaType(types);
        ArrayList packages = new ArrayList();
        Class[] toolTypes;
        String subpackage = "";

        if (tool.getToolPackage().indexOf('.') > -1) {
            subpackage = "." + tool.getToolPackage().substring(0, tool.getToolPackage().indexOf('.'));
        } else if (!tool.getToolPackage().equals("")) {
            subpackage = "." + tool.getToolPackage();
        }

        toolTypes = TypeUtils.classForTrianaType(TaskGraphUtils.getAllDataInputTypes(tool));
        boolean assignin = false;
        boolean explicitin = false;

        for (int i = 0; i < userClasses.length && (!explicitin); i++) {
            Class type = userClasses[i];
            Object obj = null;
            try {
                obj = type.newInstance();
            }
            catch (Exception e) {
            }

            for (int j = 0; j < toolTypes.length && (!explicitin); j++) {
                Class tooltype = toolTypes[j];

                if (tooltype == type) {
                    packages.add(INPUT_EXPLICIT_PACKAGE);
                    explicitin = true;
                }

                if (!assignin) {
                    if (tooltype.isAssignableFrom(type)) {
                        packages.add(INPUT_ASSIGN_PACKAGE + subpackage);
                        assignin = true;
                    } else if ((obj != null) && tooltype.isInstance(obj)) {
                        packages.add(INPUT_ASSIGN_PACKAGE + subpackage);
                        assignin = true;
                    }
                }
            }
        }

        toolTypes = TypeUtils.classForTrianaType(TaskGraphUtils.getAllDataOutputTypes(tool));
        boolean assignout = false;
        boolean explicitout = false;

        for (int i = 0; i < userClasses.length && (!explicitout); i++) {
            Class type = userClasses[i];
            Object obj = null;
            try {
                obj = type.newInstance();
            }
            catch (Exception e) {
            }

            for (int j = 0; j < toolTypes.length && (!explicitout); j++) {
                Class tooltype = toolTypes[j];

                if (tooltype == type) {
                    packages.add(OUTPUT_EXPLICIT_PACKAGE);
                    explicitout = true;
                }

                if (!assignout) {
                    if (tooltype.isAssignableFrom(type)) {
                        packages.add(OUTPUT_ASSIGN_PACKAGE + subpackage);
                        assignout = true;
                    } else if ((obj != null) && tooltype.isInstance(obj)) {
                        packages.add(OUTPUT_ASSIGN_PACKAGE + subpackage);
                        assignout = true;
                    }
                }
            }
        }

        if (explicitout && explicitin) {
            packages.add(IN_OUT_EXPLICIT_PACKAGE);
        }

        if (assignout && assignin) {
            packages.add(IN_OUT_ASSIGN_PACKAGE + subpackage);
        }

        return (String[]) packages.toArray(new String[packages.size()]);
    }

    /**
     * This method is called when the filter is choosen. The initialisation of the filter should be implemented here
     */
    public void init() {
        TypeSelecter selector = new TypeSelecter();

        if (selector.isOKSelected()) {
            types = selector.getSelectedTypes();
        }
    }

    /**
     * This method is called when the filter is unchoosen. Any disposal related to the filter should be implemented
     * here
     */
    public void dispose() {
    }


    /**
     * Dialog class that allows the user to select triana types and whether they are input, output or both.
     */
    private static class TypeSelecter extends JDialog implements ActionListener {

        private JButton ok;
        private JButton cancel;
        private JList typePanel;
        static boolean okSelected = false;


        private TypeSelecter() {
            super(GUIEnv.getApplicationFrame(), true);
            setTitle("Select Data Types");
            initGUI();
            this.setResizable(false);
            setLocation((getToolkit().getScreenSize().width / 2) - (getSize().width / 2),
                    (getToolkit().getScreenSize().height / 2) - (getSize().height / 2));
            loadTrianaTypes();

            setVisible(true);
        }

        /**
         * layout the GUI for this panel.
         */
        private void initGUI() {
            this.getContentPane().setLayout(new BorderLayout());

            typePanel = new JList(new DefaultListModel());
            typePanel.setVisibleRowCount(8);
            JScrollPane scroll = new JScrollPane(typePanel);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            ok = new JButton("OK");
            ok.addActionListener(this);
            buttonPanel.add(ok);
            cancel = new JButton("Cancel");
            cancel.addActionListener(this);
            buttonPanel.add(cancel);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(scroll, BorderLayout.CENTER);
            mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

            this.getContentPane().add(mainPanel, BorderLayout.NORTH);
            this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            pack();
        }

        /**
         * get a list of Triana types and populate the list selecter
         */
        private void loadTrianaTypes() {
            Vector<String> typeVector = Env.getAllTrianaTypes();
            String[] types = (String[]) typeVector.toArray(new String[typeVector.size()]);
            typePanel.setListData(types);
            pack();
        }

        /**
         * @return true if ok was choosen by the user
         */
        public boolean isOKSelected() {
            return okSelected;
        }

        /**
         * return an array of the user select types
         */
        public String[] getSelectedTypes() {
            Object[] types = typePanel.getSelectedValues();
            String[] copy = new String[types.length];

            System.arraycopy(types, 0, copy, 0, types.length);

            return copy;
        }

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            okSelected = (e.getSource() == ok);
            this.setVisible(false);
        }
    }
}
