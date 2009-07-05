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

package org.trianacode.gui.panels;

import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.Toolbox;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;

/**
 * Parameter panel class for editing the classpath
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 * @created Jun 6, 2003: 6:02:57 PM
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class ClassPathPanel extends ParameterPanel implements ActionListener, PropertyChangeListener, ListSelectionListener {

    private JList classpathList = new JList();
    private final String pathSeparator;
    private JButton addBtn = new JButton(Env.getString("add"));
    private JButton removeBtn = new JButton(Env.getString("remove"));
    private JButton moveUpBtn = new JButton(Env.getString("moveup"));
    private JButton moveDownBtn = new JButton(Env.getString("movedown"));
    private JButton resetBtn = new JButton(Env.getString("Reset"));
    private JCheckBox addAllToolboxChk = new JCheckBox(Env.getString("addAllToolboxPaths"), false);
    private JCheckBox retainCPChk = new JCheckBox(Env.getString("retainPath"), true);
    private static int MOVE_UP = 0;
    private static int MOVE_DOWN = 1;
    private ToolTable tools;

    private static String ADD_ALL_TOOLS_CHECK = "addAllToolsCheck";
    private static String RETAIN_CLASSPATH = "retainClasspath";

    public ClassPathPanel(ToolTable tools) {
        pathSeparator = Env.getPathSeparator();
        this.tools = tools;
    }

    /**
     * Auto commit is turned off
     */
    public boolean isAutoCommitByDefault() {
        return true;
    }

    /**
     * Auto commit is hidden
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    /**
     * This method is called when the task is set for this panel. It is overridden to create the panel layout.
     */
    public void init() {
        setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(classpathList);
        add(scroll, BorderLayout.CENTER);
        classpathList.setPrototypeCellValue("012345678901234567890123456789012345678901234567890123456789");
        classpathList.setVisibleRowCount(20);
        classpathList.addPropertyChangeListener(this);
        classpathList.addListSelectionListener(this);

        JPanel panel = new JPanel(new GridLayout(5, 1));
        panel.add(addBtn);
        addBtn.addActionListener(this);
        panel.add(removeBtn);
        removeBtn.addActionListener(this);
        panel.add(moveUpBtn);
        moveUpBtn.addActionListener(this);
        panel.add(moveDownBtn);
        moveDownBtn.addActionListener(this);
        panel.add(resetBtn);
        resetBtn.addActionListener(this);
        JPanel panelOuter = new JPanel(new BorderLayout());
        panelOuter.add(panel, BorderLayout.NORTH);
        add(panelOuter, BorderLayout.EAST);

        panel = new JPanel(new GridLayout(2, 1));
        panel.add(addAllToolboxChk);
        addAllToolboxChk.addActionListener(this);
        addAllToolboxChk.setSelected(Env.getBooleanUserProperty(ADD_ALL_TOOLS_CHECK, false));
        panel.add(retainCPChk);
        retainCPChk.addActionListener(this);
        retainCPChk.setSelected(Env.getBooleanUserProperty(RETAIN_CLASSPATH, true));
        add(panel, BorderLayout.SOUTH);
    }

    /**
     * This method is called when the panel is reset or cancelled. It should reset all the panels components to
     * the values specified by the associated task, e.g. a component representing a parameter called "noise"
     * should be set to the value returned by a getTool().getParameter("noise") call.
     */
    public void reset() {
    }

    /**
     * @return <strong>true</strong> if the classpath is to be saved
     */
    public boolean isRetainCPCheck() {
        return retainCPChk.isSelected();
    }

    /**
     * This method is called when the panel is finished with. It should dispose of any components (e.g. windows)
     * used by the panel.
     */
    public void dispose() {
    }

    public void okClicked() {
        Env.setUserProperty(RETAIN_CLASSPATH, String.valueOf(retainCPChk.isSelected()));
        if (retainCPChk.isSelected()) {
            Env.setClasspath(getClasspath());
            Env.setUserProperty(ADD_ALL_TOOLS_CHECK, String.valueOf(addAllToolboxChk.isSelected()));
        } else {
            Env.setClasspath("");
            Env.setUserProperty(ADD_ALL_TOOLS_CHECK, "false");
        }
        super.okClicked();
    }

    public String getClasspath() {
        StringBuffer classpathBuf = new StringBuffer();
        for (int i = 0; i < classpathList.getModel().getSize(); i++) {
            classpathBuf.append((String) classpathList.getModel().getElementAt(i));
            if (i < classpathList.getModel().getSize() - 1)
                classpathBuf.append(pathSeparator);
        }
        return classpathBuf.toString();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) {
            handleAddSelection();
        }
        if (e.getSource() == removeBtn) {
            ((PathListModel) classpathList.getModel()).removeElements(classpathList.getSelectedValues());
        }
        if (e.getSource() == moveUpBtn) {
            ((PathListModel) classpathList.getModel()).handleMove(classpathList.getSelectedIndices(), MOVE_UP);
        }
        if (e.getSource() == moveDownBtn) {
            ((PathListModel) classpathList.getModel()).handleMove(classpathList.getSelectedIndices(), MOVE_DOWN);
        }
        if (e.getSource() == resetBtn) {
            setClasspath(Env.getSystemClasspath());
        }
        if (e.getSource() == addAllToolboxChk) {
            handleAddAllToolBoxes();
        }
    }

    private void handleAddAllToolBoxes() {
        ArrayList allDirs = new ArrayList();
        Toolbox[] toolBoxes = tools.getToolBoxes();
        PathListModel pathListModel = ((PathListModel) classpathList.getModel());

        for (int i = 0; i < toolBoxes.length; i++) {
            if (!toolBoxes[i].isVirtual()) {
                File[] classDirs = FileUtils.listEndsWith(toolBoxes[i].getPath(), "classes");

                for (int count = 0; count < classDirs.length; count++)
                    allDirs.add(classDirs[count].getAbsolutePath());
            }
        }

        if (addAllToolboxChk.isSelected())
            pathListModel.addElements(allDirs);
        else
            pathListModel.removeElements(allDirs);
    }

    private void handleAddSelection() {
        TFileChooser chooser = new TFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle(Env.getString("selectPath"));
        chooser.setApproveButtonText(Env.getString("OK"));

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = chooser.getSelectedFiles();
            String[] paths = new String[selectedFiles.length];
            for (int i = 0; i < selectedFiles.length; i++) {
                paths[i] = selectedFiles[i].getAbsolutePath();
            }
            ((PathListModel) classpathList.getModel()).addElements(paths);
        }
    }

    public void setClasspath(String cpStr) {
        classpathList.setModel(new PathListModel(cpStr));
    }

    public void propertyChange(PropertyChangeEvent evt) {
        checkMoveBtnsEnabled();
    }

    private void checkMoveBtnsEnabled() {
        moveDownBtn.setEnabled(classpathList.getMaxSelectionIndex() != classpathList.getModel().getSize() - 1);
        moveUpBtn.setEnabled(classpathList.getMinSelectionIndex() != 0);
    }

    public void valueChanged(ListSelectionEvent e) {
        checkMoveBtnsEnabled();
    }

    private class PathListModel implements ListModel {

        private ArrayList elements = new ArrayList();
        private ArrayList listeners = new ArrayList();

        public PathListModel(String cpStr) {
            StringTokenizer tokenizer = new StringTokenizer(cpStr, pathSeparator);
            String[] items = new String[tokenizer.countTokens()];
            int index = 0;
            while (tokenizer.hasMoreTokens()) {
                items[index] = tokenizer.nextToken();
                index++;
            }
            addElements(items);
        }

        public PathListModel(String[] cpArray) {
            addElements(cpArray);
        }

        public int getSize() {
            return elements.size();
        }

        public Object getElementAt(int index) {
            return elements.get(index);
        }

        public void addElements(Object[] add) {
            for (int i = 0; i < add.length; i++) {
                Object o = add[i];
                if (!elements.contains(o))
                    elements.add(o);
            }
            notifyListeners();
        }

        public void addElements(Collection add) {
            elements.addAll(add);
            notifyListeners();
        }

        public void removeElements(Object[] remove) {
            elements.removeAll(Arrays.asList(remove));
            notifyListeners();
        }

        public void removeElements(Collection remove) {
            elements.removeAll(remove);
            notifyListeners();
        }

        public void handleMove(int[] selectedIndicies, int direction) {
            Arrays.sort(selectedIndicies);
            ListSelectionModel selectionModel = classpathList.getSelectionModel();
            selectionModel.clearSelection();
            if (direction == MOVE_DOWN) {
                if (selectedIndicies[selectedIndicies.length - 1] < elements.size() - 1) {
                    for (int i = selectedIndicies.length - 1; i >= 0; i--) {
                        int indexA = selectedIndicies[i];
                        int indexB = (indexA + 1) % elements.size();
                        swapElements(indexA, indexB);
                        selectionModel.addSelectionInterval(indexB, indexB);
                    }
                }
            } else {
                if (selectedIndicies[0] > 0) {
                    for (int i = 0; i < selectedIndicies.length; i++) {
                        int indexA = selectedIndicies[i];
                        int indexB = (indexA - 1) % elements.size();
                        swapElements(indexA, indexB);
                        selectionModel.addSelectionInterval(indexB, indexB);
                    }
                }
            }
            notifyListeners();
        }

        private void swapElements(int indexA, int indexB) {
            Object temp = elements.get(indexB);
            elements.set(indexB, elements.get(indexA));
            elements.set(indexA, temp);
        }

        private void notifyListeners() {
            ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 0);
            for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
                ((ListDataListener) iterator.next()).contentsChanged(event);
            }
        }

        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }
    }
}
