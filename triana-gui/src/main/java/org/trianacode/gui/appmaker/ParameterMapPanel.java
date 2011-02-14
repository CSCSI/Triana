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

package org.trianacode.gui.appmaker;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.util.Env;
import org.trianacode.gui.windows.WizardInterface;
import org.trianacode.gui.windows.WizardPanel;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class ParameterMapPanel extends JPanel
        implements WizardPanel, CommandFileListener, ActionListener, FocusListener {

    private Tool tool;
    private String filename;

    private JTextField argfield = new JTextField(3);

    private ArrayList maplist = new ArrayList();
    private ArrayList paramlist = new ArrayList();
    private ArrayList valuelist = new ArrayList();
    private ArrayList desclist = new ArrayList();

    private JPanel mappanel = new JPanel();
    private JPanel equalpanel1 = new JPanel();
    private JPanel parampanel = new JPanel();
    private JPanel equalpanel2 = new JPanel();
    private JPanel valpanel = new JPanel();
    private JPanel descpanel = new JPanel();

    private JButton additem = new JButton("Add");

    /**
     * an interface to the main wizard window
     */
    private WizardInterface wizard;


    public ParameterMapPanel(CommandFilePanel filepanel) {
        filepanel.addCommandFileListener(this);

        initLayout();
    }


    private void initLayout() {
        setLayout(new BorderLayout());

        add(getParamPanel(), BorderLayout.NORTH);
    }

    private JPanel getParamPanel() {
        JPanel mainpanel = new JPanel(new BorderLayout());

        JPanel argpanel = new JPanel(new BorderLayout(3, 0));
        argpanel.add(new JLabel(Env.getString("numberOfRequiredArgs")), BorderLayout.WEST);
        argpanel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JPanel argpanel2 = new JPanel(new BorderLayout());
        argpanel2.add(argfield, BorderLayout.WEST);
        argpanel.add(argpanel2, BorderLayout.CENTER);
        argfield.addFocusListener(this);
        argfield.setText("1");

        mainpanel.add(argpanel, BorderLayout.NORTH);

        mappanel.setLayout(new GridLayout(1, 1, 0, 3));
        equalpanel1.setLayout(new GridLayout(1, 1, 0, 3));
        parampanel.setLayout(new GridLayout(1, 1, 0, 3));
        equalpanel2.setLayout(new GridLayout(1, 1, 0, 3));
        valpanel.setLayout(new GridLayout(1, 1, 0, 3));
        descpanel.setLayout(new GridLayout(1, 1, 0, 3));

        equalpanel1.setBorder(new EmptyBorder(0, 3, 0, 3));
        equalpanel2.setBorder(new EmptyBorder(0, 3, 0, 3));

        JLabel maplabel = new JLabel(Env.getString("map"));
        JLabel paramlabel = new JLabel(Env.getString("parameter"));
        JLabel vallabel = new JLabel(Env.getString("value"));
        JLabel desclabel = new JLabel(Env.getString("description"));

//        maplabel.setVerticalAlignment(JLabel.BOTTOM);
//        paramlabel.setVerticalAlignment(JLabel.BOTTOM);
//        vallabel.setVerticalAlignment(JLabel.BOTTOM);
//        desclabel.setVerticalAlignment(JLabel.BOTTOM);

        mappanel.add(maplabel);
        equalpanel1.add(new JLabel());
        parampanel.add(paramlabel);
        equalpanel2.add(new JLabel());
        valpanel.add(vallabel);
        descpanel.add(desclabel);

        for (int count = 0; count < 5; count++) {
            addParamItemPanel();
        }

        JPanel itempanel = new JPanel(new BorderLayout());
        itempanel.add(mappanel, BorderLayout.WEST);
        itempanel.add(equalpanel1, BorderLayout.CENTER);
        itempanel.add(parampanel, BorderLayout.EAST);

        JPanel itempanel2 = new JPanel(new BorderLayout());
        itempanel2.add(itempanel, BorderLayout.WEST);
        itempanel2.add(equalpanel2, BorderLayout.CENTER);
        itempanel2.add(valpanel, BorderLayout.EAST);

        JPanel itempanel3 = new JPanel(new BorderLayout());
        itempanel3.add(itempanel2, BorderLayout.WEST);
        itempanel3.add(descpanel, BorderLayout.CENTER);

        mainpanel.add(itempanel3, BorderLayout.WEST);

        JPanel addpanel = new JPanel(new BorderLayout());
        addpanel.add(additem, BorderLayout.EAST);
        addpanel.setBorder(new EmptyBorder(3, 0, 0, 0));
        additem.addActionListener(this);

        mainpanel.add(addpanel, BorderLayout.SOUTH);

        resetParams();

        return mainpanel;
    }

    private void addParamItemPanel() {
        JTextField mapfield = new JTextField(5);
        JComboBox paramfield = new JComboBox(new DefaultComboBoxModel());
        JTextField valfield = new JTextField(7);
        JTextField descfield = new JTextField(12);
        mapfield.addFocusListener(this);

        paramfield.setPrototypeDisplayValue("0123456789123456789");

        maplist.add(mapfield);
        paramlist.add(paramfield);
        valuelist.add(valfield);
        desclist.add(descfield);

        mappanel.setLayout(new GridLayout(maplist.size() + 1, 1, 0, 3));
        equalpanel1.setLayout(new GridLayout(maplist.size() + 1, 1, 0, 3));
        parampanel.setLayout(new GridLayout(maplist.size() + 1, 1, 0, 3));
        equalpanel2.setLayout(new GridLayout(maplist.size() + 1, 1, 0, 3));
        valpanel.setLayout(new GridLayout(maplist.size() + 1, 1, 0, 3));
        descpanel.setLayout(new GridLayout(maplist.size() + 1, 1, 0, 3));

        JPanel mapcont = new JPanel(new BorderLayout());
        mapcont.add(mapfield, BorderLayout.SOUTH);

        JPanel valcont = new JPanel(new BorderLayout());
        valcont.add(valfield, BorderLayout.SOUTH);

        JPanel desccont = new JPanel(new BorderLayout());
        desccont.add(descfield, BorderLayout.SOUTH);
        desccont.setBorder(new EmptyBorder(0, 3, 0, 0));

        mappanel.add(mapcont);
        equalpanel1.add(new JLabel("=", JLabel.CENTER));
        parampanel.add(paramfield);
        equalpanel2.add(new JLabel("=", JLabel.CENTER));
        valpanel.add(valcont);
        descpanel.add(desccont);
    }


    public void setWizardInterface(WizardInterface wizard) {
        this.wizard = wizard;
    }

    public WizardInterface getWizardInterface() {
        return wizard;
    }

    public boolean isFinishEnabled() {
        return (tool != null);
    }

    public boolean isNextEnabled() {
        return (tool != null);
    }


    /**
     * @return the number of required arguments
     */
    public int getNumberOfRequiredArguments() {
        try {
            return Integer.parseInt(argfield.getText());
        } catch (NumberFormatException except) {
            return 0;
        }
    }

    /**
     * @return an array of strings that have mapped parameters
     */
    public String[] getMaps() {
        Iterator iter = maplist.iterator();
        Iterator paramiter = paramlist.iterator();
        ArrayList maps = new ArrayList();
        JComboBox paramcombo;
        JTextField mapfield;

        while (iter.hasNext()) {
            mapfield = (JTextField) iter.next();
            paramcombo = (JComboBox) paramiter.next();

            if ((!mapfield.getText().equals("")) && (!maps.contains(mapfield.getText())) &&
                    (paramcombo.getSelectedItem() != null)) {
                maps.add(mapfield.getText());
            }
        }

        return (String[]) maps.toArray(new String[maps.size()]);
    }

    /**
     * @return the parameters that are mapped to the specified map string (in the form groupname.taskname.paramname)
     */
    public String[] getMappedParameters(String map) {
        Iterator mapiter = maplist.iterator();
        Iterator paramiter = paramlist.iterator();
        ArrayList params = new ArrayList();
        JTextField mapfield;
        JComboBox paramcombo;

        while (mapiter.hasNext()) {
            mapfield = (JTextField) mapiter.next();
            paramcombo = (JComboBox) paramiter.next();

            if (mapfield.getText().equals(map)) {
                params.add(paramcombo.getSelectedItem());
            }
        }

        return (String[]) params.toArray(new String[params.size()]);
    }

    /**
     * @return the value mapped to the specified map/parameter combination, or null if no value is mapped
     */
    public String getMappedValue(String map, String parameter) {
        Iterator mapiter = maplist.iterator();
        Iterator paramiter = paramlist.iterator();
        Iterator valiter = valuelist.iterator();
        JTextField mapfield;
        JComboBox paramcombo;
        JTextField valfield;

        while (mapiter.hasNext()) {
            mapfield = (JTextField) mapiter.next();
            paramcombo = (JComboBox) paramiter.next();
            valfield = (JTextField) valiter.next();

            if ((mapfield.getText().equals(map)) && (paramcombo.getSelectedItem().equals(parameter))) {
                if (valfield.getText().equals("")) {
                    return null;
                } else {
                    return valfield.getText();
                }
            }
        }

        return null;
    }

    /**
     * @return the description for the specified map
     */
    public String getDescription(String map) {
        Iterator mapiter = maplist.iterator();
        Iterator desciter = desclist.iterator();
        JTextField mapfield;
        JTextField descfield;
        String desc = "";

        while (mapiter.hasNext()) {
            mapfield = (JTextField) mapiter.next();
            descfield = (JTextField) desciter.next();

            if (mapfield.getText().equals(map)) {
                if (!desc.equals("")) {
                    desc += '\n';
                }

                desc += descfield.getText();
            }
        }

        return desc;
    }


    private void handleXMLFile(String fname) {
        this.tool = null;
        this.filename = fname;
        wizard.notifyButtonStateChange();

        Thread thread = new Thread() {
            public void run() {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));

                try {
                    XMLReader reader = new XMLReader(new FileReader(filename));
                    tool = reader.readComponent(GUIEnv.getApplicationFrame().getEngine().getProperties());
                } catch (IOException except) {
                    JOptionPane
                            .showMessageDialog(null, "Error reading " + filename, "IO Error", JOptionPane.ERROR_MESSAGE,
                                    GUIEnv.getTrianaIcon());
                } catch (TaskGraphException except) {
                    JOptionPane.showMessageDialog(null, "Invalid taskgraph file: " + filename, "Taskgraph Error",
                            JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
                }

                if (tool != null) {
                    ArrayList list = new ArrayList();
                    makeParameterList(tool, list, "");

                    Iterator iter = paramlist.iterator();

                    while (iter.hasNext()) {
                        JComboBox comboBox = (JComboBox) iter.next();

                        comboBox.removeAllItems();

                        Iterator listiter = list.iterator();

                        while (listiter.hasNext()) {
                            ((DefaultComboBoxModel) comboBox.getModel()).addElement(listiter.next());
                        }

                        comboBox.setSelectedItem(null);
                    }
                }

                resetParams();
                wizard.notifyButtonStateChange();
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        };

        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    private void makeParameterList(Tool tool, ArrayList list, String base) {
        String[] paramnames = tool.getParameterNames();

        for (int count = 0; count < paramnames.length; count++) {
            if (tool.getParameterType(paramnames[count]).equals(Tool.USER_ACCESSIBLE)) {
                list.add(base + paramnames[count]);
            }
        }

        if (tool instanceof TaskGraph) {
            Task[] tasks = ((TaskGraph) tool).getTasks(true);

            for (int count = 0; count < tasks.length; count++) {
                makeParameterList(tasks[count], list, base + tasks[count] + '.');
            }
        }
    }

    private void resetParams() {
        Iterator iter = maplist.iterator();
        JTextField field;
        JComboBox combo;

        while (iter.hasNext()) {
            field = ((JTextField) iter.next());
            field.setEnabled(tool != null);
            field.setText("");
        }

        iter = paramlist.iterator();
        while (iter.hasNext()) {
            combo = ((JComboBox) iter.next());
            combo.setEnabled(tool != null);
            combo.setSelectedItem(null);
        }

        iter = valuelist.iterator();
        while (iter.hasNext()) {
            field = ((JTextField) iter.next());
            field.setEnabled(tool != null);
            field.setText("");
        }

        iter = desclist.iterator();
        while (iter.hasNext()) {
            field = ((JTextField) iter.next());
            field.setEnabled(tool != null);
            field.setText("");
        }

        additem.setEnabled(tool != null);
        initArgumentOptions();
    }

    private void initArgumentOptions() {
        int argnum = getNumberOfRequiredArguments();
        Iterator iter;
        boolean exist;
        JTextField mapfield;
        JTextField empty;

        for (int count = 1; count <= argnum; count++) {
            iter = maplist.iterator();
            empty = null;
            exist = false;

            while (iter.hasNext() && (!exist)) {
                mapfield = (JTextField) iter.next();
                exist = mapfield.getText().equals("#" + count);

                if ((empty == null) && mapfield.getText().equals("")) {
                    empty = mapfield;
                }
            }

            if (!exist) {
                if (empty == null) {
                    addParamItemPanel();
                    empty = (JTextField) maplist.get(maplist.size() - 1);
                }

                empty.setText("#" + count);
            }
        }
    }


    /**
     * repacks the builder window to preferred size;
     */
    private void repack() {
        Component comp = getParent();

        while ((comp != null) && (!(comp instanceof Window))) {
            comp = comp.getParent();
        }

        ((Window) comp).pack();
    }


    public void commandFileChanged(String filename) {
        handleXMLFile(filename);
    }


    public void panelDisplayed() {
    }

    public void panelHidden() {
    }


    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == additem) {
            addParamItemPanel();
            repack();
        }
    }

    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(FocusEvent event) {
    }

    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(FocusEvent event) {
        if (event.getSource() == argfield) {
            initArgumentOptions();
        } else if (maplist.contains(event.getSource())) {
            JTextField source = (JTextField) event.getSource();

            while (source.getText().startsWith("-")) {
                source.setText(source.getText().substring(1));
            }
        }
    }

}
