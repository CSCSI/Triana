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
package org.trianacode.gui.toolmaker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.trianacode.gui.builder.GUICreaterPanel;
import org.trianacode.gui.toolmaker.guibuilder.BuilderPanel;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.gui.windows.WizardInterface;
import org.trianacode.gui.windows.WizardPanel;
import org.trianacode.util.Env;

/**
 * A tool wizard panel for specifying the tools graphical interface.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class GUIPanel extends JPanel
        implements WizardPanel, ItemListener, ActionListener, ListSelectionListener {

    /**
     * the panel defining the parameter that the GUI is built for
     */
    protected ParamsPanelInterface parampanel;

    /**
     * the gui type radio buttons
     */
    private JRadioButton guibuild = new JRadioButton(Env.getString("guiBuilderInterface"));
    private JRadioButton custom = new JRadioButton(Env.getString("customInterface"));
    private JRadioButton none = new JRadioButton(Env.getString("noInterface"));

    /**
     * gui parameter list
     */
    private JList paramlist = new JList(new DefaultListModel());

    /**
     * show hidden parameters
     */
    private JCheckBox showhidden = new JCheckBox();

    /**
     * move parameters up/down gui interface buttons
     */
    private JButton moveup = new JButton(Env.getString("moveup"));
    private JButton movedown = new JButton(Env.getString("movedown"));

    /**
     * custom panel class text field
     */
    private JTextField customclass = new JTextField(15);

    /**
     * generate custom panel template checkbox
     */
    private JCheckBox generate = new JCheckBox();

    /**
     * the main gui builder panel
     */
    private BuilderPanel builderpanel;

    /**
     * the builder panel container
     */
    private JPanel buildercont;

    /**
     * the preview GUI button
     */
    private JButton preview = new JButton(Env.getString("previewGUI"));

    /**
     * a flag indicating whether the gui builder is being used
     */
    private boolean guibuilder = false;

    /**
     * the interface to the underlying wizard
     */
    private WizardInterface wizard;


    /**
     * Constructs a GUIPanel that builds an interface for the parameters defined in the specified parameter panel.
     */
    public GUIPanel(ParamsPanelInterface parampanel) {
        this.parampanel = parampanel;
        initLayout();
    }

    /**
     * Constructs a GUI Panel for the GUI Editor
     */
    protected GUIPanel() {
        initLayout();
    }


    /**
     * layout the panel
     */
    protected void initLayout() {
        setLayout(new BorderLayout());

        JPanel selpanel = getSelectionPanel();

        add(selpanel, BorderLayout.WEST);
    }


    public void setWizardInterface(WizardInterface wizard) {
        this.wizard = wizard;
    }

    public WizardInterface getWizardInterface() {
        return wizard;
    }

    public boolean isFinishEnabled() {
        return true;
    }

    public boolean isNextEnabled() {
        return true;
    }

    /**
     * @return true if a gui builder panel is being used
     */
    public boolean isUsingGUIBuilder() {
        return guibuild.isSelected();
    }

    /**
     * @return true if a custom panel is being used
     */
    public boolean isUsingCustomPanel() {
        return custom.isSelected();
    }

    /**
     * @return an array of gui builder lines for each parameter
     */
    public String[] getGUIBuilderLines() {
        int size = paramlist.getModel().getSize();
        ArrayList lines = new ArrayList();
        String line;

        for (int count = 0; count < size; count++) {
            line = builderpanel.getGUILine(getParameterAt(count));

            if ((line != null) && (!line.equals(""))) {
                lines.add(line);
            }
        }

        return (String[]) lines.toArray(new String[lines.size()]);
    }

    /**
     * @return the name of the parameter in the parameter list at the specified index
     */
    private String getParameterAt(int count) {
        String listentry = (String) paramlist.getModel().getElementAt(count);
        return listentry.substring(0, listentry.indexOf(" "));
    }


    /**
     * @return the custom panel name specified (does not include package qualifier)
     */
    public String getCustomPanelName() {
        String custname = customclass.getText();

        if (custname.indexOf('.') == -1) {
            return custname;
        } else {
            return custname.substring(custname.lastIndexOf('.') + 1);
        }
    }

    /**
     * @return the <package>.<package> qualifier for this panel
     */
    public String getCustomPanelPackage() {
        String full = customclass.getText();
        String packageName = full.replaceAll(getCustomPanelName(), "");

        if (packageName.endsWith(".")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }
        return packageName;

    }


    /**
     * Sets the gui builder lines, setting isUsingGUIBuilder yo true
     */
    public void setGUIBuilderLines(String[] lines) {
        for (int count = 0; count < lines.length; count++) {
            if (!lines.equals("")) {
                builderpanel.setGUILine(getParam(lines[count]), lines[count]);
            }
        }

        guibuild.setSelected(true);
    }

    private String getParam(String line) {
        int titleidx = line.indexOf("$title");
        int start = line.indexOf(' ', titleidx) + 1;
        int end = line.indexOf(' ', start);

        return line.substring(start, end);
    }

    /**
     * Sets the parameter package name, setting isUsingCustomPanel to true
     */
    public void setCustomPanel(String panelname) {
        customclass.setText(panelname);
        custom.setSelected(true);
    }

    /**
     * Sets isUsingGUIBuilder and isUsingCustomPanel to false
     */
    public void setNoInterface() {
        custom.setSelected(false);
        guibuild.setSelected(false);
    }


    /**
     * @return the full custom panel class specified including package qualifier
     */
    public String getQualifiedCustomPanelName() {
        return customclass.getText();
    }


    /**
     * @return true if the user wants a template for their custom panel class to be generated
     */
    public boolean isGenerateCustomPanel() {
        return custom.isSelected() && generate.isSelected();
    }


    /**
     * @return the panel for selecting which type of gui is built (either gui-builder, custom or node)
     */
    private JPanel getSelectionPanel() {
        ButtonGroup group = new ButtonGroup();
        group.add(guibuild);
        group.add(custom);
        group.add(none);

        guibuild.addItemListener(this);
        custom.addItemListener(this);
        none.addItemListener(this);
        none.setSelected(true);

        JPanel nonepanel = new JPanel(new BorderLayout());
        nonepanel.add(none, BorderLayout.WEST);

        JPanel nonecont = new JPanel(new BorderLayout());
        nonecont.add(nonepanel, BorderLayout.NORTH);

        JPanel guicont = new JPanel(new BorderLayout());
        guicont.add(getGUIBuilderSelectionPanel(), BorderLayout.SOUTH);
        guicont.add(nonecont, BorderLayout.CENTER);

        JPanel customcont = new JPanel(new BorderLayout());
        customcont.add(guicont, BorderLayout.CENTER);
        customcont.add(getCustomSelectionPanel(), BorderLayout.SOUTH);

        JPanel mainpanel = new JPanel(new BorderLayout());
        mainpanel.add(customcont, BorderLayout.NORTH);

        return mainpanel;
    }

    /**
     * @return the gui builder selection panel
     */
    private JPanel getGUIBuilderSelectionPanel() {
        builderpanel = new BuilderPanel(this);
        builderpanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JPanel previewpanel = new JPanel(new BorderLayout());
        previewpanel.add(preview, BorderLayout.EAST);
        preview.addActionListener(this);

        buildercont = new JPanel(new BorderLayout());
        buildercont.add(builderpanel, BorderLayout.CENTER);
        buildercont.add(previewpanel, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(paramlist, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        paramlist.setPrototypeCellValue("1234567890123456789012345");
        paramlist.setVisibleRowCount(6);
        paramlist.addListSelectionListener(this);

        JPanel buttonpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonpanel.add(moveup);
        buttonpanel.add(movedown);
        moveup.addActionListener(this);
        movedown.addActionListener(this);

        JPanel hiddenpanel = new JPanel(new BorderLayout());
        hiddenpanel.add(new JLabel(Env.getString("showHidden")), BorderLayout.WEST);
        hiddenpanel.add(showhidden, BorderLayout.CENTER);
        showhidden.setSelected(true);
        showhidden.addItemListener(this);

        JPanel parampanel = new JPanel(new BorderLayout());
        parampanel.add(new JLabel(Env.getString("parameters")), BorderLayout.NORTH);
        parampanel.add(scroll, BorderLayout.NORTH);
        parampanel.add(hiddenpanel, BorderLayout.CENTER);
        parampanel.add(buttonpanel, BorderLayout.SOUTH);
        parampanel.setBorder(new EmptyBorder(0, 30, 0, 0));

        paramlist.setEnabled(guibuild.isSelected());
        moveup.setEnabled(guibuild.isSelected());
        movedown.setEnabled(guibuild.isSelected());

        JPanel guicont = new JPanel(new BorderLayout());
        guicont.add(guibuild, BorderLayout.NORTH);
        guicont.add(parampanel, BorderLayout.WEST);

        return guicont;
    }

    /**
     * @return the custom selection panel
     */
    private JPanel getCustomSelectionPanel() {
        JLabel customlabel = new JLabel(Env.getString("panelClass"));
        customlabel.setBorder(new EmptyBorder(0, 0, 0, 3));
        JLabel generatelabel = new JLabel(Env.getString("generateTemplate"));
        generatelabel.setBorder(new EmptyBorder(0, 0, 0, 6));

        JPanel custompanel = new JPanel(new BorderLayout());
        custompanel.add(customlabel, BorderLayout.WEST);
        custompanel.add(customclass, BorderLayout.CENTER);

        JPanel generatepanel = new JPanel(new BorderLayout());
        generatepanel.add(custompanel, BorderLayout.NORTH);
        generatepanel.add(generatelabel, BorderLayout.WEST);
        generatepanel.add(generate, BorderLayout.CENTER);
        generatepanel.setBorder(new EmptyBorder(0, 30, 0, 0));

        customclass.setEnabled(custom.isSelected());
        generate.setEnabled(custom.isSelected());
        generate.setSelected(false);


        JPanel customcont = new JPanel(new BorderLayout());
        customcont.add(custom, BorderLayout.NORTH);
        customcont.add(generatepanel, BorderLayout.WEST);

        return customcont;
    }


    /**
     * @return an array of the GUI parameters
     */
    private String[] getGUIParams() {
        Object[] objs = ((DefaultListModel) paramlist.getModel()).toArray();
        String[] params = new String[objs.length];

        for (int count = 0; count < params.length; count++) {
            params[count] = getParameterName((String) objs[count]);
        }

        return params;
    }

    /**
     * @return the default value for the given parameter (as defined in the parameter panel)
     */
    public String getDefaultValue(String param) {
        return parampanel.getDefaultValue(param);
    }

    /**
     * Sets the default value for the given parameter, updating that defined in the parameter panel
     */
    public void setDefaultValue(String param, String value) {
        parampanel.setDefaultValue(param, value);
    }


    /**
     * updates the currently selected GUI parameter to be associated with the specified component
     */
    public void updateGUIComponent(String component) {
        int index = paramlist.getSelectedIndex();
        DefaultListModel model = (DefaultListModel) paramlist.getModel();

        String paramstr = getParameterStr(getParameterName((String) paramlist.getSelectedValue()), component);

        paramlist.removeListSelectionListener(this);

        model.removeElementAt(index);
        model.insertElementAt(paramstr, index);
        paramlist.setSelectedIndex(index);

        paramlist.addListSelectionListener(this);
    }

    /**
     * @return true if the specified parameter exists in the GUI parameter list
     */
    private boolean containsGUIParam(String param) {
        String[] params = getGUIParams();
        boolean found = false;

        for (int count = 0; (!found) && (count < params.length); count++) {
            found = params[count].equals(param);
        }

        return found;
    }

    /**
     * @return the parameter list string for the specified param/component combination
     */
    private String getParameterStr(String param, String component) {
        return param + " - " + component;
    }

    /**
     * @return the parameter name for a list element in the parameter list
     */
    private String getParameterName(String listelem) {
        return listelem.substring(0, listelem.indexOf(' '));
    }

    /**
     * @return the parameter name for a list element in the parameter list
     */
    private String getComponent(String listelem) {
        return listelem.substring(listelem.indexOf('-') + 2);
    }


    /**
     * Called when the panel is displayed in the wizard
     */
    public void panelDisplayed() {
        updateParameterList();
    }

    protected void updateParameterList() {
        DefaultListModel model = (DefaultListModel) paramlist.getModel();
        String[] params = parampanel.getParameterNames();

        for (int count = 0; count < params.length; count++) {
            if ((!containsGUIParam(params[count])) && (parampanel.getParameterType(params[count]).equals(
                    UnitWizard.USER_ACCESSIBLE))) {
                model.addElement(getParameterStr(params[count], builderpanel.getComponent(params[count])));
            } else if ((containsGUIParam(params[count])) && (!parampanel.getParameterType(params[count])
                    .equals(UnitWizard.USER_ACCESSIBLE))) {
                model.removeElement(getParameterStr(params[count], builderpanel.getComponent(params[count])));
            }
        }

        String[] guiparams = getGUIParams();
        boolean found;
        int delcount = 0;

        for (int count1 = 0; count1 < guiparams.length; count1++) {
            found = false;

            for (int count2 = 0; (count2 < params.length) && (!found); count2++) {
                found = guiparams[count1].equals(params[count2]);
            }

            if (!found) {
                model.removeElementAt(count1 - delcount);
                delcount++;
            }
        }

        if (!showhidden.isSelected()) {
            removeHidden();
        }

        if (model.getSize() > 0) {
            paramlist.setSelectedIndex(0);
            builderpanel.setCurrentParameter(getParameterName((String) paramlist.getSelectedValue()));
        } else {
            builderpanel.setCurrentParameter(null);
        }
    }

    private void removeHidden() {
        DefaultListModel model = (DefaultListModel) paramlist.getModel();

        for (int count = 0; count < model.getSize(); count++) {
            if (getComponent((String) model.get(count)).equals(Env.getString("hidden"))) {
                model.remove(count--);
            }
        }
    }

    /**
     * Called when the panel is hidden in the wizard
     */
    public void panelHidden() {
    }


    public void actionPerformed(ActionEvent event) {
        if ((event.getSource() == moveup) && (paramlist.getSelectedIndex() > 0)) {
            paramlist.removeListSelectionListener(this);

            String entry = (String) paramlist.getSelectedValue();
            int index = paramlist.getSelectedIndex();

            ((DefaultListModel) paramlist.getModel()).removeElementAt(index);
            ((DefaultListModel) paramlist.getModel()).insertElementAt(entry, index - 1);

            paramlist.addListSelectionListener(this);
            paramlist.setSelectedIndex(index - 1);
        } else if ((event.getSource() == movedown) && (paramlist.getSelectedIndex() > -1) && (
                paramlist.getSelectedIndex() < paramlist.getModel().getSize() - 1)) {
            paramlist.removeListSelectionListener(this);

            String entry = (String) paramlist.getSelectedValue();
            int index = paramlist.getSelectedIndex();

            ((DefaultListModel) paramlist.getModel()).removeElementAt(index);
            ((DefaultListModel) paramlist.getModel()).insertElementAt(entry, index + 1);

            paramlist.addListSelectionListener(this);
            paramlist.setSelectedIndex(index + 1);
        } else if (event.getSource() == preview) {
            String[] lines = getGUIBuilderLines();
            String str = "";

            for (int count = 0; count < lines.length; count++) {
                str += lines[count] + '\n';
            }

            Vector<String> strvect = GUICreaterPanel.splitLine(str);
            GUICreaterPanel guicreate = new GUICreaterPanel(strvect);
            ParameterWindow window;
            Window frame = getWindow();

            if (frame instanceof Frame) {
                window = new ParameterWindow((Frame) frame, WindowButtonConstants.OK_BUTTON, true);
            } else {
                window = new ParameterWindow((Dialog) frame, WindowButtonConstants.OK_BUTTON, true);
            }

            window.setLocation(frame.getLocationOnScreen().x + 150, frame.getLocationOnScreen().y + 150);
            window.setTitle(Env.getString("previewGUI"));
            window.setParameterPanel(guicreate);
            window.setVisible(true);
        }
    }


    public void itemStateChanged(ItemEvent event) {
        if ((event.getSource() == guibuild) || (event.getSource() == custom) || (event.getSource() == none)) {
            if (!showhidden.isSelected()) {
                removeHidden();
            }

            paramlist.setEnabled(guibuild.isSelected());
            moveup.setEnabled(guibuild.isSelected());
            movedown.setEnabled(guibuild.isSelected());
            showhidden.setEnabled(guibuild.isSelected());

            customclass.setEnabled(custom.isSelected());
            generate.setEnabled(custom.isSelected());

            if ((!guibuilder) && guibuild.isSelected()) {
                add(buildercont, BorderLayout.CENTER);
                guibuilder = guibuild.isSelected();
                repack();
            } else if (guibuilder && (!guibuild.isSelected())) {
                remove(buildercont);
                guibuilder = guibuild.isSelected();
                repack();
            }
        } else if (event.getSource() == showhidden) {
            if (showhidden.isSelected()) {
                updateParameterList();
            } else {
                removeHidden();
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

        if (comp != null) {
            ((Window) comp).pack();
        }
    }


    /**
     * @return the wizard window
     */
    private Window getWindow() {
        Component comp = getParent();

        while ((comp != null) && (!(comp instanceof Window))) {
            comp = comp.getParent();
        }

        if (comp instanceof Window) {
            return (Window) comp;
        } else {
            return null;
        }
    }


    public void valueChanged(ListSelectionEvent event) {
        if (event.getSource() == paramlist) {
            if (!showhidden.isSelected()) {
                removeHidden();
            }

            if (paramlist.getSelectedValue() != null) {
                builderpanel.setCurrentParameter(getParameterName((String) paramlist.getSelectedValue()));
            } else {
                builderpanel.setCurrentParameter(null);
            }
        }
    }

}
