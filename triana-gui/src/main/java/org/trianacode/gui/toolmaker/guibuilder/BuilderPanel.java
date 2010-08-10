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
package org.trianacode.gui.toolmaker.guibuilder;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.trianacode.gui.toolmaker.GUIPanel;
import org.trianacode.util.Env;

/**
 * A panel for defining gui builder interface components
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class BuilderPanel extends JPanel implements ItemListener, FocusListener {

    /**
     * current componenet types available
     */
    private static final String[] COMPONENT_TYPES = {Env.getString("textfield"), Env.getString("label"),
            Env.getString("choice"), Env.getString("checkbox"),
            Env.getString("scroller"), Env.getString("intscroller"),
            Env.getString("filechooser"), Env.getString("hidden")};

    /**
     * combo box for selecting component type
     */
    private JComboBox comptype = new JComboBox(new DefaultComboBoxModel());

    /**
     * the main panel for editing the current component
     */
    private JPanel comppanel;

    /**
     * the card layout that manages the comppanel
     */
    private CardLayout complayout;

    /**
     * the panels for each of the available components
     */
    private TextFieldPanel textfield;
    private LabelPanel label;
    private ChoicePanel choice;
    private CheckBoxPanel checkbox;
    private ScrollerPanel scroller;
    private ScrollerPanel intscroller;
    private FileChooserPanel filechooser;
    private HiddenPanel hidden;

    /**
     * the text field for setting the current default value
     */
    private JTextField defval = new JTextField(15);

    /**
     * a hashtable of component panels keyed by type
     */
    private Hashtable comppanels = new Hashtable();

    /**
     * a hashtable of the components for each parameter
     */
    private Hashtable params = new Hashtable();

    /**
     * a hashtable of the gui builder strings for each parameter
     */
    private Hashtable guilines = new Hashtable();

    /**
     * the current parameter
     */
    private String curparam;

    /**
     * the main gui panel
     */
    private GUIPanel guipanel;

    /**
     * a flag indicating whether a change in comp type is relayed back to the main gui panel
     */
    private boolean guicallback = true;


    public BuilderPanel(GUIPanel guipanel) {
        this.guipanel = guipanel;
        initLayout();
    }

    /**
     * initialises the layout
     */
    private void initLayout() {
        setLayout(new BorderLayout());

        populateCompType();
        initCompPanel();

        JLabel complabel = new JLabel(Env.getString("component"));
        complabel.setBorder(new EmptyBorder(0, 0, 0, 3));

        JPanel typepanel = new JPanel(new BorderLayout());
        typepanel.add(comptype, BorderLayout.WEST);

        JPanel compcont = new JPanel(new BorderLayout());
        compcont.add(complabel, BorderLayout.WEST);
        compcont.add(typepanel, BorderLayout.CENTER);
        compcont.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel deflabel = new JLabel(Env.getString("defaultValue"));
        deflabel.setBorder(new EmptyBorder(0, 0, 0, 3));

        JPanel defpanel = new JPanel(new BorderLayout());
        defpanel.add(defval, BorderLayout.WEST);
        defval.addFocusListener(this);

        JPanel defcont = new JPanel(new BorderLayout());
        defcont.add(compcont, BorderLayout.NORTH);
        defcont.add(deflabel, BorderLayout.WEST);
        defcont.add(defpanel, BorderLayout.CENTER);
        defcont.setBorder(new EmptyBorder(0, 0, 3, 0));

        JPanel cont = new JPanel(new BorderLayout());
        cont.add(defcont, BorderLayout.NORTH);
        cont.add(comppanel, BorderLayout.CENTER);

        JPanel maincont = new JPanel(new BorderLayout());
        maincont.add(cont, BorderLayout.NORTH);

        add(maincont, BorderLayout.WEST);
    }

    /**
     * populates the comptype combo box with the COMPONENT_TYPES
     */
    private void populateCompType() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) comptype.getModel();

        for (int count = 0; count < COMPONENT_TYPES.length; count++) {
            model.addElement(COMPONENT_TYPES[count]);
        }

        comptype.addItemListener(this);
    }

    /**
     * adds panels for editing the various components to comppanel
     */
    private void initCompPanel() {
        complayout = new CardLayout();
        comppanel = new JPanel(complayout);

        textfield = new TextFieldPanel();
        label = new LabelPanel();
        choice = new ChoicePanel();
        checkbox = new CheckBoxPanel();
        scroller = new ScrollerPanel(ScrollerPanel.SCROLLER);
        intscroller = new ScrollerPanel(ScrollerPanel.INT_SCROLLER);
        filechooser = new FileChooserPanel();
        hidden = new HiddenPanel();

        comppanel.add(textfield, Env.getString("textfield"));
        comppanel.add(label, Env.getString("label"));
        comppanel.add(choice, Env.getString("choice"));
        comppanel.add(checkbox, Env.getString("checkbox"));
        comppanel.add(scroller, Env.getString("scroller"));
        comppanel.add(intscroller, Env.getString("intscroller"));
        comppanel.add(filechooser, Env.getString("filechooser"));
        comppanel.add(hidden, Env.getString("hidden"));

        comppanels.put(Env.getString("textfield"), textfield);
        comppanels.put(Env.getString("label"), label);
        comppanels.put(Env.getString("choice"), choice);
        comppanels.put(Env.getString("checkbox"), checkbox);
        comppanels.put(Env.getString("scroller"), scroller);
        comppanels.put(Env.getString("intscroller"), intscroller);
        comppanels.put(Env.getString("filechooser"), filechooser);
        comppanels.put(Env.getString("hidden"), hidden);
    }


    /**
     * Sets the current parameter
     */
    public void setCurrentParameter(String param) {
        comptype.setEnabled(param != null);
        defval.setEnabled(param != null);
        defval.setText("");

        if (curparam != null) {
            guilines.put(curparam, getGUILine(curparam));
        }

        curparam = param;

        if (param != null) {
            guicallback = false;

            if (!params.containsKey(param)) {
                initParam(param);
            }

            Enumeration enumeration = comppanels.elements();
            ComponentPanelInterface comp;

            while (enumeration.hasMoreElements()) {
                comp = (ComponentPanelInterface) enumeration.nextElement();
                comp.reset(param);
            }

            defval.setText(guipanel.getDefaultValue(param));
            comptype.setSelectedItem(params.get(param));

            if (guilines.containsKey(param)) {
                ((ComponentPanelInterface) comppanels.get(params.get(param)))
                        .setGUIBuilderStr((String) guilines.get(param));
            }

            ((ComponentPanelInterface) comppanels.get(params.get(param)))
                    .notifyDefaultValue(guipanel.getDefaultValue(param));

            guicallback = true;
        }
    }

    /**
     * @return the current parameter
     */
    public String getCurrentParameter() {
        return curparam;
    }


    /**
     * @return the component type for the specified parameter
     */
    public String getComponent(String param) {
        if (!params.containsKey(param)) {
            initParam(param);
        }

        return (String) params.get(param);
    }

    /**
     * @return the gui line for the specified parameter
     */
    public String getGUILine(String param) {
        if (param.equals(curparam)) {
            return ((ComponentPanelInterface) comppanels.get(comptype.getSelectedItem())).getGUIBuilderStr(param);
        } else if (guilines.containsKey(param)) {
            return (String) guilines.get(param);
        } else {
            String temp = curparam;

            setCurrentParameter(param);
            String line = getGUILine(param);
            setCurrentParameter(temp);

            return line;
        }
    }

    /**
     * Sets the gui line for the specified parameter
     */
    public void setGUILine(String param, String guiline) {
        guilines.put(param, guiline);

        if (param.equals(curparam)) {
            ((ComponentPanelInterface) comppanels.get(comptype.getSelectedItem())).setGUIBuilderStr(guiline);
        }
    }


    /**
     * initialises a parmeter to the default component
     */
    private void initParam(String param) {
        params.put(param, comptype.getModel().getElementAt(0));
    }


    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == comptype) {
            String comp = (String) comptype.getSelectedItem();

            complayout.show(comppanel, comp);

            if (guicallback) {
                params.put(curparam, comp);
                guipanel.updateGUIComponent(comp);
                ((ComponentPanelInterface) comppanels.get(comp)).notifyDefaultValue(defval.getText());
            }
        }
    }


    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        if ((curparam != null) && (event.getSource() == defval)) {
            guipanel.setDefaultValue(curparam, defval.getText());
            ((ComponentPanelInterface) comppanels.get(params.get(curparam))).notifyDefaultValue(defval.getText());
        }
    }


    public static final String[] splitString(String line) {
        ArrayList list = new ArrayList();
        list.add(line.substring(0, line.indexOf(" $title ")).trim());

        String rest = line.substring(line.indexOf(" $title ") + 8).trim();
        ;

        while (rest.indexOf(' ') > -1) {
            list.add(rest.substring(0, rest.indexOf(' ')));
            rest = rest.substring(rest.indexOf(' ') + 1).trim();
        }

        list.add(rest.trim());

        String[] str = new String[list.size()];
        Iterator iter = list.iterator();

        for (int count = 0; count < str.length; count++) {
            str[count] = (String) iter.next();
        }

        return str;
    }

}
