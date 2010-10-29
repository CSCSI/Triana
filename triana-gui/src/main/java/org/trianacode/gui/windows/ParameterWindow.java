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
package org.trianacode.gui.windows;

import org.trianacode.gui.panels.OptionPane;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.panels.ParameterWindowInterface;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * ParamaterWindow in Version 3 of Triana is a simple window for containing ParameterPanels. It inherits from JDialog
 * and provides default buttons for OK, CANCEL & APPLY.
 *
 * @author 1st December 1999
 * @version $Revision: 4048 $
 */
public class ParameterWindow extends JDialog
        implements ActionListener, ItemListener, ContainerListener,
        ParameterWindowInterface {

    /**
     * The reference to the parameter panel i.e. the customizer for the unit.
     */
    private ParameterPanel parameterPanel;

    /**
     * The buttons present on this window.
     */
    private byte buttons = WindowButtonConstants.OK_BUTTON;

    /**
     * A flag indicating whether the ok button has been pressed.
     */
    private boolean accepted = false;

    /**
     * A flag indicating whether the parameter is automatically disposed when ok/cancel are choosen (true by default)
     */
    private boolean autodispose = true;

    /**
     * A checkbox that indicates whether parameter changes are automatically commited, or it is waited until apply/ok is
     * clicked
     */
    private JCheckBox autocommit = new JCheckBox("Auto commit", false);

    /**
     * An array list of all the parameter window listeners to be notified when the window is hidden
     */
    private ArrayList listeners = new ArrayList();


    /**
     * Default constructor
     *
     * @deprecated
     */
    public ParameterWindow() {
        super();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Constructs a window with the specified button set
     */
    public ParameterWindow(byte buttons) {
        super();
        this.buttons = buttons;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Constructs a window with the specified button set and the specified modal status
     */
    public ParameterWindow(Component parent, byte buttons, boolean modal) {
        super(getFrame(parent), modal);
        this.buttons = buttons;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public static Frame getFrame(Component comp) {
        while ((comp != null) && (!(comp instanceof Frame))) {
            comp = comp.getParent();
        }

        if (comp instanceof Frame) {
            return (Frame) comp;
        } else {
            return null;
        }
    }


    /**
     * Adds a listener to be notified when the window is hidden
     */
    public void addParameterWindowListener(ParameterWindowListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener from this window
     */
    public void removeParameterWindowListener(ParameterWindowListener listener) {
        listeners.remove(listener);
    }


    /**
     * <p>Sets the targets for this ParameterWindow to the particular task which has the following parameter panel (i.e.
     * the Customizer) and which displays a default title on the top of the window. This function also creates the
     * relevant menu items relevant to UnitPanelWindows and lays out the window by putting the customizing panel in the
     * window with an OK on the bottom.
     */
    public void setParameterPanel(ParameterPanel paramPanel) {
        parameterPanel = paramPanel;
        parameterPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        if ((getTitle() == null) && (parameterPanel.getTask() != null)) {
            setTitle(parameterPanel.getTask().getToolName());
        }

        getContentPane().removeAll();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(parameterPanel, BorderLayout.CENTER);
        parameterPanel.addContainerListener(this);

        if (buttons != WindowButtonConstants.NO_BUTTONS) {
            JPanel buttoncontainer = new JPanel(new BorderLayout());
            JPanel buttonpanel = new JPanel();

            JButton ok = new JButton(Env.getString("OK"));
            buttonpanel.add(ok);
            ok.addActionListener(this);

            getRootPane().setDefaultButton(ok);

            if ((buttons == WindowButtonConstants.OK_CANCEL_BUTTONS) || (buttons
                    == WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS)) {
                JButton cancel = new JButton(Env.getString("Cancel"));
                buttonpanel.add(cancel);
                cancel.addActionListener(this);
            }

            if (buttons == WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS) {
                JButton apply = new JButton(Env.getString("Apply"));
                buttonpanel.add(apply);
                apply.addActionListener(this);
            }

            autocommit.setVisible(paramPanel.isAutoCommitVisible());

            buttoncontainer.add(buttonpanel, BorderLayout.EAST);
            buttoncontainer.add(autocommit, BorderLayout.WEST);
            buttoncontainer.setBorder(new EmptyBorder(0, 5, 0, 0));

            getContentPane().add(buttoncontainer, BorderLayout.SOUTH);
        }

        autocommit.addItemListener(this);
        autocommit.setSelected(paramPanel.isAutoCommitByDefault());

        if (paramPanel.getMenuBar() != null) {
            setJMenuBar(paramPanel.getMenuBar());
        }

        paramPanel.setWindowInterface(this);

        pack();
    }

    /**
     * @return the parameter panel contained in this window
     */
    public ParameterPanel getParameterPanel() {
        return parameterPanel;
    }

    /**
     * @return true if the parameter panel is to automatically commit parameter changes
     */
    public boolean isAutoCommit() {
        return autocommit.isSelected();
    }

    /**
     * Sets whether the panel is auto commit
     */
    public void setAutoCommit(boolean state) {
        autocommit.setSelected(state);
    }

    /**
     * @return true if auto commit is visible
     */
    public boolean isAutoCommitVisible() {
        return autocommit.isVisible();
    }

    /**
     * Sets whether auto commit is visible
     */
    public void setAutoCommitVisible(boolean state) {
        autocommit.setVisible(state);
    }


    /**
     * @return true if the panel is automatically disposed when ok/canel are chosen (true by default)
     */
    public boolean isAutoDispose() {
        return autodispose;
    }

    /**
     * Sets whether the panel is automatically disposed when ok/canel are chosen (true by default)
     */
    public void setAutoDispose(boolean state) {
        autodispose = state;
    }


    /**
     * @return the window (Frame/Dialog) for the panel
     */
    public Window getWindow() {
        return this;
    }

    /**
     * Close the window
     */
    public void closeWindow() {
        setVisible(false);
    }

    /**
     * @return true if the ok button was pressed
     */
    public boolean isAccepted() {
        return accepted;
    }


    /*
     * Invoked when help is requested and pulls up a help window
     * containing the specific help.
     */

    public void showHelp() {
        Help.setTitle(
                Env.getString("Help") + " " + Env.getString("for") + " " + parameterPanel.getTask().getToolName());
        Help.setFile(parameterPanel.getTask().getHelpFile());
    }

    /**
     * Resets the accepted flag.
     */
    public void setVisible(boolean state) {
        if (state == true) {
            accepted = false;
        }

        super.setVisible(state);
    }


    /**
     * Invoked when an action occurs i.e. either a help or a OldUnit Properties menu option may have been chosen.
     */
    public void actionPerformed(ActionEvent e) {
        try {
            String label = e.getActionCommand();

            if (label.equals(Env.getString("Help"))) {
                showHelp();
            } else if (label.equals(Env.getString("OK"))) {
                accepted = true;
                parameterPanel.okClicked();

                setVisible(false);
                notifyWindowHidden();

                if (getDefaultCloseOperation() == DISPOSE_ON_CLOSE) {
                    dispose();
                }
            } else if (label.equals(Env.getString("Cancel"))) {
                parameterPanel.cancelClicked();

                setVisible(false);
                notifyWindowHidden();

                if (getDefaultCloseOperation() == DISPOSE_ON_CLOSE) {
                    dispose();
                }
            } else if (label.equals(Env.getString("Apply"))) {
                parameterPanel.applyClicked();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            OptionPane.showError(e1.getMessage(), "Error", this);
        }
    }

    private void notifyWindowHidden() {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            ((ParameterWindowListener) iter.next()).parameterWindowHidden(this);
        }
    }


    /**
     * Calls apply clicked on the parameter panel when auto commit is selected
     */
    public void itemStateChanged(ItemEvent event) {
        if ((event.getSource() == autocommit) && (autocommit.isSelected())) {
            parameterPanel.applyClicked();
        }
    }


    public void componentAdded(ContainerEvent e) {
        pack();
    }

    public void componentRemoved(ContainerEvent e) {
    }


    public void dispose() {
        if (parameterPanel != null) {
            if (autodispose) {
                parameterPanel.disposePanel();
            }

            remove(parameterPanel);
        }

        super.dispose();
    }

}












