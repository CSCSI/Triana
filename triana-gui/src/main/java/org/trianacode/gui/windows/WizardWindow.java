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


import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A window for creating wizards. This window displays a series of panels that can be navigated through using next
 * and previous buttons.
 *
 * @author      Ian Wang
 * @created     8th August
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class WizardWindow extends JDialog implements ActionListener, WizardInterface {

    /**
     * the buttons for navigating through the wizard
     */
    private JButton next = new JButton(Env.getString("Next"));
    private JButton back = new JButton(Env.getString("Back"));
    private JButton finish = new JButton(Env.getString("Finish"));
    private JButton cancel = new JButton(Env.getString("Cancel"));

    /**
     * the main panel that contains the wizard panels
     */
    private JPanel mainpanel;

    /**
     * the wizard panels
     */
    private JPanel[] panels;

    /**
     * the number of the currently displayed panel
     */
    private int curpanel = 0;

    /**
     * the component that receives notification when finish or canel is pressed
     */
    private ArrayList listeners = new ArrayList();


    /**
     * Constructs an empty wizard window
     */
    public WizardWindow(Frame owner, String title) {
        super(owner);
        initLayout();

        setTitle(title);
        setLocation((getToolkit().getScreenSize().width / 2) - (getSize().width / 2),
                    (getToolkit().getScreenSize().height / 2) - (getSize().height / 2));
    }

    /**
     * Constructs and displays a wizard window to display the specified panels.
     */
    public WizardWindow(Frame owner, JPanel[] panels, String title) {
        this(owner, title);

        this.panels = panels;

        for (int count = 0; count < panels.length; count++)
            if (panels[count] instanceof WizardPanel)
                ((WizardPanel) panels[count]).setWizardInterface(this);

        setPanelIndex(0);
        notifyButtonStateChange();

        setVisible(true);
    }

    /**
     * Layout the main wizard window
     */
    private void initLayout() {
        getContentPane().setLayout(new BorderLayout());

        JPanel buttonpanel = new JPanel();
        buttonpanel.add(cancel);
        buttonpanel.add(back);
        buttonpanel.add(next);
        buttonpanel.add(finish);

        cancel.addActionListener(this);
        back.addActionListener(this);
        next.addActionListener(this);
        finish.addActionListener(this);

        JPanel buttoncont = new JPanel(new BorderLayout());
        buttoncont.add(buttonpanel, BorderLayout.EAST);

        mainpanel = new JPanel(new BorderLayout());
        mainpanel.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new EmptyBorder(3, 3, 8, 3)));

        getContentPane().add(buttoncont, BorderLayout.SOUTH);
        getContentPane().add(mainpanel, BorderLayout.CENTER);

        pack();
    }


    /**
     * Adds a wizard listener to this wizaed
     */
    public void addWizardListener(WizardListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    /**
     * Removes a wizard listener from this wizaed
     */
    public void removeWizardListener(WizardListener listener) {
        listeners.remove(listener);
    }


    /**
     * @return an array of the panels in this wizard
     */
    public JPanel[] getPanels() {
        return panels;
    }

    /**
     * Sets the panels displayed by this wizard
     */
    public void setPanels(JPanel[] panels) {
        if ((panels.length > curpanel) && (panels[curpanel] instanceof WizardPanel))
            ((WizardPanel) panels[curpanel]).panelHidden();

        this.panels = panels;

        for (int count = 0; count < panels.length; count++)
            if (panels[count] instanceof WizardPanel)
                ((WizardPanel) panels[count]).setWizardInterface(this);

        setPanelIndex(Math.min(panels.length, curpanel));
        notifyButtonStateChange();
    }


    /**
     * @return the currently displayed panels index
     */
    public int getPanelIndex() {
        return curpanel;
    }

    /**
     * Sets the currently displayed panel to the specified index
     */
    public void setPanelIndex(int index) {
        if ((index >= 0) && (index <= panels.length)) {
            if ((index != curpanel) && (panels[curpanel] instanceof WizardPanel))
                ((WizardPanel) panels[curpanel]).panelHidden();

            Dimension oldsize = mainpanel.getSize();
            mainpanel.removeAll();

            mainpanel.add(panels[index], BorderLayout.CENTER);
            curpanel = index;

            pack();
            setLocation(getLocation().x + (oldsize.width - mainpanel.getSize().width) / 2,
                        getLocation().y + (oldsize.height - mainpanel.getSize().height) / 2);

            back.setEnabled(index > 0);
            next.setEnabled(index < panels.length - 1);

            if (panels[index] instanceof WizardPanel)
                ((WizardPanel) panels[index]).panelDisplayed();

            Iterator iter = listeners.iterator();
            while (iter.hasNext())
                ((WizardListener) iter.next()).panelSelected(this, index);
        }
    }


    /**
     * sets the finish button to enabled/disabled
     */
    public void notifyButtonStateChange() {
        boolean finishstate = true;

        for (int count = 0; (count < panels.length) && (finishstate); count++)
            if ((panels[count] instanceof WizardPanel) && (!((WizardPanel) panels[count]).isFinishEnabled()))
                finishstate = false;

        finish.setEnabled(finishstate);

        boolean nextstate = getPanelIndex() < (panels.length - 1);

        if ((panels[getPanelIndex()] instanceof WizardPanel) && (!((WizardPanel) panels[getPanelIndex()]).isNextEnabled()))
            nextstate = false;

        next.setEnabled(nextstate);
    }


    /**
     * Cycles through the panels
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == back)
            setPanelIndex(curpanel - 1);
        else if (event.getSource() == next)
            setPanelIndex(curpanel + 1);

        if (event.getSource() == cancel) {
            setVisible(false);

            Iterator iter = listeners.iterator();
            while (iter.hasNext())
                ((WizardListener) iter.next()).cancelSelected(this);
        }

        if (event.getSource() == finish) {
            setVisible(false);

            Iterator iter = listeners.iterator();
            while (iter.hasNext())
                ((WizardListener) iter.next()).finishSelected(this);
        }
    }

}



