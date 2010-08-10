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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.FormLayout;
import org.trianacode.gui.windows.WizardInterface;
import org.trianacode.gui.windows.WizardPanel;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolListener;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.Toolbox;
import org.trianacode.util.Env;

/**
 * The tool wizard panel for editing the general properties of a tool, including the number of input/outpur nodes
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class UnitPanel extends JPanel
        implements ItemListener, ActionListener, FocusListener, WizardPanel, ToolListener {

    public static int DEFAULT_INPUT_NODES = 1;
    public static int DEFAULT_OUTPUT_NODES = 1;

    public static String UNLIMITED = "Unlimited";


    /**
     * The main wizard interface
     */
    private WizardInterface wizard;

    /**
     * the inputs to this panel
     */
    private JTextField unitName = new JTextField(20);
    private JComboBox toolboxpath = null;
    private JTextField unitPackage = new JTextField(20);
    private JTextField author = new JTextField(15);
    private JTextField date = new JTextField(10);
    private JTextField popup = new JTextField(25);
    private JTextField help = new JTextField(15);
    private JCheckBox copyright = new JCheckBox();

    private JComboBox innodes = new JComboBox();
    private JComboBox outnodes = new JComboBox();

    private JCheckBox inresize = new JCheckBox();
    private JComboBox minin = new JComboBox();
    private JComboBox maxin = new JComboBox();

    private JCheckBox outresize = new JCheckBox();
    private JComboBox minout = new JComboBox();
    private JComboBox maxout = new JComboBox();

    /**
     * a button for browsing tool box locations
     */
    private JButton browse = new JButton(GUIEnv.getIcon("dots.png"));

    private ToolTable tools = null;


    /**
     * Constructs a panel for editing general properties of a tool.
     */
    public UnitPanel(ToolTable tools) {
        this.tools = tools;
        tools.addToolTableListener(this);
        initLayout();
        setDefaults();
    }


    /**
     * layout the panel
     */
    private void initLayout() {
        setLayout(new FlowLayout());

        JPanel main = new JPanel(new BorderLayout());
        JPanel maincont = new JPanel(new BorderLayout());
        JPanel subcont;


        subcont = new JPanel(new BorderLayout());
        JPanel infopanel = getInfoPanel();
        subcont.add(infopanel, BorderLayout.WEST);
        maincont.add(subcont, BorderLayout.NORTH);

        subcont = new JPanel(new BorderLayout());
        JPanel inpanel = getInputPanel();
        inpanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        subcont.add(inpanel, BorderLayout.WEST);
        maincont.add(subcont, BorderLayout.CENTER);

        subcont = new JPanel(new BorderLayout());
        JPanel outpanel = getOutputPanel();
        subcont.add(outpanel, BorderLayout.WEST);
        maincont.add(subcont, BorderLayout.SOUTH);

        main.add(maincont, BorderLayout.NORTH);
        add(main);
    }


    /**
     * @return the panel for inputting general tool properties
     */
    private JPanel getInfoPanel() {
        JPanel infopanel = new JPanel(new BorderLayout());

        JPanel formpanel = new JPanel(new FormLayout(3, 3));

        formpanel.add(new JLabel(Env.getString("unitname"), JLabel.LEFT));
        JPanel itempanel = new JPanel(new BorderLayout());
        itempanel.add(unitName, BorderLayout.WEST);
        formpanel.add(itempanel);
        unitName.addFocusListener(this);

        formpanel.add(new JLabel(Env.getString("unitPackage"), JLabel.LEFT));
        itempanel = new JPanel(new BorderLayout());
        itempanel.add(unitPackage, BorderLayout.CENTER);
        itempanel.add(browse, BorderLayout.EAST);
        JPanel itemcont = new JPanel(new BorderLayout());
        itemcont.add(itempanel, BorderLayout.WEST);
        formpanel.add(itemcont);
        browse.setMargin(new Insets(6, 4, 2, 4));
        browse.addActionListener(this);

        formpanel.add(new JLabel(Env.getString("toolboxpath"), JLabel.LEFT));
        itempanel = new JPanel(new BorderLayout());
        toolboxpath = new JComboBox(tools.getToolBoxes());
        toolboxpath.addItemListener(this);
        itempanel.add(toolboxpath, BorderLayout.WEST);
        formpanel.add(itempanel);
        toolboxpath.setSelectedItem(Env.getLastWorkingToolbox());

        formpanel.add(new JLabel(Env.getString("author"), JLabel.LEFT));
        itempanel = new JPanel(new BorderLayout());
        itempanel.add(author, BorderLayout.WEST);
        formpanel.add(itempanel);

        formpanel.add(new JLabel(Env.getString("popup"), JLabel.LEFT));
        itempanel = new JPanel(new BorderLayout());
        itempanel.add(popup, BorderLayout.WEST);
        formpanel.add(itempanel);

        formpanel.add(new JLabel(Env.getString("date"), JLabel.LEFT));
        itempanel = new JPanel(new BorderLayout());
        itempanel.add(date, BorderLayout.WEST);
        formpanel.add(itempanel);

        formpanel.add(new JLabel(Env.getString("helpfile"), JLabel.LEFT));
        itempanel = new JPanel(new BorderLayout());
        itempanel.add(help, BorderLayout.WEST);
        formpanel.add(itempanel);

        formpanel.add(new JLabel(Env.getString("copyright"), JLabel.LEFT));
        itempanel = new JPanel(new BorderLayout());
        itempanel.add(copyright, BorderLayout.WEST);
        formpanel.add(itempanel);

        JPanel contain = new JPanel(new BorderLayout());
        contain.add(formpanel, BorderLayout.CENTER);

        infopanel.add(contain, BorderLayout.NORTH);

        return infopanel;
    }

    /**
     * @return the panel for specifying the number of input nodes
     */
    private JPanel getInputPanel() {
        JPanel itempanel = new JPanel(new FlowLayout());
        itempanel.add(new JLabel(Env.getString("defaultIn"), JLabel.LEFT));
        itempanel.add(innodes);
        populate(innodes, 0, 20, false);

        JPanel resizepanel = new JPanel(new FlowLayout());
        resizepanel.add(new JLabel(Env.getString("enableResize"), JLabel.LEFT));
        resizepanel.add(inresize);

        JPanel itemcont = new JPanel(new BorderLayout());
        itemcont.add(itempanel, BorderLayout.WEST);
        itemcont.add(resizepanel, BorderLayout.CENTER);

        JPanel labelpanel = new JPanel(new GridLayout(2, 1));
        labelpanel.add(new JLabel(Env.getString("minIn"), JLabel.LEFT));
        labelpanel.add(new JLabel(Env.getString("maxIn"), JLabel.LEFT));
        labelpanel.setBorder(new EmptyBorder(0, 30, 0, 3));

        JPanel minpanel = new JPanel(new BorderLayout());
        minpanel.add(minin, BorderLayout.WEST);

        JPanel maxpanel = new JPanel(new BorderLayout());
        maxpanel.add(maxin, BorderLayout.WEST);

        JPanel minmaxpanel = new JPanel(new GridLayout(2, 1));
        minmaxpanel.add(minpanel);
        minmaxpanel.add(maxpanel);
        populate(minin, 0, 20, false);
        populate(maxin, 0, 20, true);

        minin.setEnabled(inresize.isSelected());
        maxin.setEnabled(inresize.isSelected());

        innodes.addItemListener(this);
        minin.addItemListener(this);
        maxin.addItemListener(this);
        inresize.addItemListener(this);

        innodes.setSelectedItem(String.valueOf(DEFAULT_INPUT_NODES));

        JPanel subcontain = new JPanel(new BorderLayout());
        subcontain.add(itemcont, BorderLayout.NORTH);
        subcontain.add(labelpanel, BorderLayout.WEST);
        subcontain.add(minmaxpanel, BorderLayout.CENTER);

        JPanel contain = new JPanel(new BorderLayout());
        contain.add(subcontain, BorderLayout.SOUTH);

        JPanel inputpanel = new JPanel(new BorderLayout());
        inputpanel.add(contain, BorderLayout.WEST);

        return inputpanel;
    }

    /**
     * @return the panel for specifying the number of output nodes
     */
    private JPanel getOutputPanel() {
        JPanel itempanel = new JPanel(new FlowLayout());
        itempanel.add(new JLabel(Env.getString("defaultOut"), JLabel.LEFT));
        itempanel.add(outnodes);
        populate(outnodes, 0, 20, false);

        JPanel resizepanel = new JPanel(new FlowLayout());
        resizepanel.add(new JLabel(Env.getString("enableResize"), JLabel.LEFT));
        resizepanel.add(outresize);

        JPanel itemcont = new JPanel(new BorderLayout());
        itemcont.add(itempanel, BorderLayout.WEST);
        itemcont.add(resizepanel, BorderLayout.CENTER);

        JPanel labelpanel = new JPanel(new GridLayout(2, 1));
        labelpanel.add(new JLabel(Env.getString("minOut"), JLabel.LEFT));
        labelpanel.add(new JLabel(Env.getString("maxOut"), JLabel.LEFT));
        labelpanel.setBorder(new EmptyBorder(0, 30, 0, 3));

        JPanel minpanel = new JPanel(new BorderLayout());
        minpanel.add(minout, BorderLayout.WEST);

        JPanel maxpanel = new JPanel(new BorderLayout());
        maxpanel.add(maxout, BorderLayout.WEST);

        JPanel minmaxpanel = new JPanel(new GridLayout(2, 1));
        minmaxpanel.add(minpanel);
        minmaxpanel.add(maxpanel);
        populate(minout, 0, 20, false);
        populate(maxout, 0, 20, true);

        minout.setEnabled(outresize.isSelected());
        maxout.setEnabled(outresize.isSelected());

        outnodes.addItemListener(this);
        minout.addItemListener(this);
        maxout.addItemListener(this);
        outresize.addItemListener(this);

        outnodes.setSelectedItem(String.valueOf(DEFAULT_OUTPUT_NODES));
        outresize.setSelected(true);

        JPanel subcontain = new JPanel(new BorderLayout());
        subcontain.add(itemcont, BorderLayout.NORTH);
        subcontain.add(labelpanel, BorderLayout.WEST);
        subcontain.add(minmaxpanel, BorderLayout.CENTER);

        JPanel contain = new JPanel(new BorderLayout());
        contain.add(subcontain, BorderLayout.SOUTH);

        JPanel outputpanel = new JPanel(new BorderLayout());
        outputpanel.add(contain, BorderLayout.WEST);

        return outputpanel;
    }

    /**
     * Sets the default values for the items
     */
    private void setDefaults() {
        DateFormat format = new SimpleDateFormat();
        date.setText(format.format(new Date()));
    }


    public void setWizardInterface(WizardInterface wizard) {
        this.wizard = wizard;
    }

    public WizardInterface getWizardInterface() {
        return wizard;
    }


    public boolean isFinishEnabled() {
        return !unitName.getText().equals("");
    }

    public boolean isNextEnabled() {
        return isFinishEnabled();
    }


    /**
     * @return the class name for the java source
     */
    public String getSourceClass() {
        //TODO check!
        if (unitName.getText().indexOf(".") > -1) {
            return unitName.getText().substring(unitName.getText().lastIndexOf(".") + 1);
        }
        return unitName.getText();
    }

    /**
     * @return the <package>.<package> qualifier for this class
     */
    public String getSourcePackage() {
        String result = "";
        if ((unitName.getText().lastIndexOf('.')) == -1) {
            result = unitPackage.getText();
        } else {
            result = unitName.getText().substring(0, unitName.getText().lastIndexOf("."));
        }
        return result;
    }

    /**
     * @return the full source class specified including package qualifier
     */
    public String getUnitName() {
        return unitName.getText();
    }


    /**
     * @return the package of the tool
     */
    public String getUnitPackage() {
        return unitPackage.getText();
    }

    /**
     * @return the toolbox (root path) for this tool
     */
    public String getToolBox() {
        return (String) toolboxpath.getSelectedItem();
    }

    /**
     * @return the author of the tool
     */
    public String getAuthor() {
        return author.getText();
    }

    /**
     * @return the version of the tool
     */
    public String getPopUpDescription() {
        return popup.getText();
    }

    /**
     * @return the data of the tool
     */
    public String getDate() {
        return date.getText();
    }

    /**
     * @return the unitName of the help file for the tool
     */
    public String getHelpFile() {
        return help.getText();
    }

    /**
     * @return true if the copyright should be included
     */
    public boolean isIncludeCopyright() {
        return copyright.isSelected();
    }

    /**
     * @return the default input nodes
     */
    public int getDefaultInputNodes() {
        return Integer.parseInt((String) innodes.getSelectedItem());
    }

    /**
     * @return the default output nodes
     */
    public int getDefaultOutputNodes() {
        return Integer.parseInt((String) outnodes.getSelectedItem());
    }

    /**
     * @return the minimum input nodes
     */
    public int getMinimumInputNodes() {
        return Integer.parseInt((String) minin.getSelectedItem());
    }

    /**
     * @return the maximum input nodes
     */
    public int getMaximumInputNodes() {
        if (maxin.getSelectedItem().equals(UNLIMITED)) {
            return Integer.MAX_VALUE;
        } else {
            return Integer.parseInt((String) maxin.getSelectedItem());
        }
    }

    /**
     * @return the minimum output nodes
     */
    public int getMinimumOutputNodes() {
        return Integer.parseInt((String) minout.getSelectedItem());
    }

    /**
     * @return the maximum output nodes
     */
    public int getMaximumOutputNodes() {
        if (maxout.getSelectedItem().equals(UNLIMITED)) {
            return Integer.MAX_VALUE;
        } else {
            return Integer.parseInt((String) maxout.getSelectedItem());
        }
    }


    /**
     * fills the specified combo box with numbers between min and max, adds and unlimited option if unlimited = true;
     */
    private void populate(JComboBox combo, int min, int max, boolean unlimited) {
        if (unlimited) {
            combo.addItem(UNLIMITED);
        }

        for (int count = min; count <= max; count++) {
            combo.addItem(String.valueOf(count));
        }
    }


    /**
     * Called when the panel is displayed in the wizard
     */
    public void panelDisplayed() {
    }

    /**
     * Called when the panel is hidden in the wizard
     */
    public void panelHidden() {
    }


    /**
     * Enable/disable resizable input/output nodes
     */
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == inresize) {
            minin.setEnabled(inresize.isSelected());
            maxin.setEnabled(inresize.isSelected());

            if (inresize.isSelected()) {
                minin.setSelectedItem(String.valueOf("0"));
                maxin.setSelectedItem(UNLIMITED);
            } else {
                minin.setSelectedItem(innodes.getSelectedItem());
                maxin.setSelectedItem(innodes.getSelectedItem());
            }
        }

        if (event.getSource() == outresize) {
            minout.setEnabled(outresize.isSelected());
            maxout.setEnabled(outresize.isSelected());

            if (outresize.isSelected()) {
                minout.setSelectedItem(String.valueOf("0"));
                maxout.setSelectedItem(UNLIMITED);
            } else {
                minout.setSelectedItem(outnodes.getSelectedItem());
                maxout.setSelectedItem(outnodes.getSelectedItem());
            }
        }

        if (event.getSource() == innodes) {
            if (inresize.isSelected()) {
                if (getDefaultInputNodes() > getMaximumInputNodes()) {
                    maxin.setSelectedItem(innodes.getSelectedItem());
                }

                if (getDefaultInputNodes() < getMinimumInputNodes()) {
                    minin.setSelectedItem(innodes.getSelectedItem());
                }
            } else {
                maxin.setSelectedItem(innodes.getSelectedItem());
                minin.setSelectedItem(innodes.getSelectedItem());
            }
        }

        if (event.getSource() == outnodes) {
            if (outresize.isSelected()) {
                if (getDefaultOutputNodes() > getMaximumOutputNodes()) {
                    maxout.setSelectedItem(outnodes.getSelectedItem());
                }

                if (getDefaultOutputNodes() < getMinimumOutputNodes()) {
                    minout.setSelectedItem(outnodes.getSelectedItem());
                }
            } else {
                maxout.setSelectedItem(outnodes.getSelectedItem());
                minout.setSelectedItem(outnodes.getSelectedItem());
            }
        }

        if ((event.getSource() == minin) && (getMinimumInputNodes() > getDefaultInputNodes())) {
            minin.setSelectedItem(innodes.getSelectedItem());
        }

        if ((event.getSource() == maxin) && (getMaximumInputNodes() < getDefaultInputNodes())) {
            maxin.setSelectedItem(innodes.getSelectedItem());
        }

        if ((event.getSource() == minout) && (getMinimumOutputNodes() > getDefaultOutputNodes())) {
            minout.setSelectedItem(outnodes.getSelectedItem());
        }

        if ((event.getSource() == maxout) && (getMaximumOutputNodes() < getDefaultOutputNodes())) {
            maxout.setSelectedItem(outnodes.getSelectedItem());
        }

        if (event.getSource() == toolboxpath) {
            Env.setLastWorkingToolbox((String) toolboxpath.getSelectedItem());
        }
    }


    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == browse) {
            JFileChooser chooser = new JFileChooser((String) toolboxpath.getSelectedItem());
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setFileFilter(new ToolBoxFilter());
            chooser.setDialogTitle(Env.getString("selectUnitPackage"));
            chooser.setFileHidingEnabled(false);

            int choice = chooser.showDialog(this, Env.getString("OK"));

            if (choice == JFileChooser.APPROVE_OPTION) {
                String srcPackageName = stripPackageName(chooser.getSelectedFile().getPath());
                if (srcPackageName == null) {
                    srcPackageName = "";
                    JOptionPane.showMessageDialog(this, "The selected unit package is not in your tool box path",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }

                srcPackageName = srcPackageName.replace(File.separatorChar, '.');
                if (srcPackageName.startsWith(".")) {
                    srcPackageName = srcPackageName.substring(1);
                }

                unitPackage.setText(srcPackageName);
                wizard.notifyButtonStateChange();
            }
        }
    }

    /**
     * Attempt to return the package unitName minus a unitPackage path, null if the unitPackage path doesn't exist
     *
     * @param fullPath the absolute path
     * @return The package unitName or null
     */
    private String stripPackageName(String fullPath) {
        String result = null;
        for (int i = 0; i < toolboxpath.getItemCount(); i++) {
            String toolbox = (String) toolboxpath.getItemAt(i);
            if (fullPath.startsWith(toolbox)) {
                result = fullPath.substring(toolbox.length(), fullPath.length());
                break;
            }
        }
        return result;
    }

    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        if (event.getSource() == unitName) {
            wizard.notifyButtonStateChange();
        }

        if ((event.getSource() == unitName) && (!unitName.getText().equals(""))) {
            help.setText(getUnitName() + ".html");
        }
    }

    @Override
    public void toolsAdded(java.util.List<Tool> tools) {
    }

    @Override
    public void toolsRemoved(List<Tool> tools) {
    }

    /**
     * Called when a new tool is added
     */
    public void toolAdded(Tool tool) {
        // no-op
    }

    /**
     * Called when a tool is removed
     */
    public void toolRemoved(Tool tool) {
        // no-op
    }

    /**
     * Called when a Tool Box is added
     */
    public void toolBoxAdded(Toolbox toolbox) {
        toolboxpath.addItem(toolbox.getPath());
    }

    /**
     * Called when a Tool Box is Removed
     */
    public void toolBoxRemoved(Toolbox toolbox) {
        toolboxpath.removeItem(toolbox.getPath());
    }


    private class ToolBoxFilter extends javax.swing.filechooser.FileFilter {

        public boolean accept(File file) {
            return (file.isDirectory() &&
                    (!file.getPath().endsWith("CVS")) &&
                    (!file.getPath().endsWith("src")) &&
                    (!file.getPath().endsWith("help")) &&
                    (!file.getPath().endsWith("classes")));
        }

        public String getDescription() {
            return ("Tool Box Directories");
        }

    }

}
