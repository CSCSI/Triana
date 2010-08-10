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

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;

/**
 * UI for pasing tasks that have been cut or copied from the scratch pad back to the tool box
 *
 * @author Matthew Shields
 * @version $Revsion:$
 */
public class PastePanel extends ParameterPanel implements ItemListener {

    /**
     * currently loaded tools
     */
    private ToolTable tools = null;

    private ToolPanel toolpanel;
    private JCheckBox createMissingCheckBox;

    private static String CREATE_MISSING_PASTE_DIR = "createMissingPaste";

    private String suggestedPack;


    public PastePanel(ToolTable tools, String suggestedToolPackage) {
        this.tools = tools;
        this.suggestedPack = suggestedToolPackage;

        init();
    }

    /**
     * This method returns true by default. It should be overridden if the panel does not want the user to be able to
     * change the auto commit state
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    /**
     * This method is called when the task is set for this panel. It is overridden to create the panel layout.
     */
    public void init() {
        setLayout(new BorderLayout());

        toolpanel = new ToolPanel(tools, false);
        toolpanel.setPackage(suggestedPack);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolpanel, BorderLayout.CENTER);

        Boolean createSetting = (Boolean) Env.getUserProperty(CREATE_MISSING_PASTE_DIR);
        boolean create;

        if (createSetting != null) {
            create = createSetting.booleanValue();
        } else {
            create = true;
        }

        createMissingCheckBox = new JCheckBox(Env.getString("createMissingDirectories"), create);
        createMissingCheckBox.addItemListener(this);
        panel.add(createMissingCheckBox, BorderLayout.SOUTH);

        add(panel, BorderLayout.NORTH);
    }

    /**
     * This method is called when the panel is reset or cancelled. It should reset all the panels components to the
     * values specified by the associated task, e.g. a component representing a parameter called "noise" should be set
     * to the value returned by a getTool().getParameter("noise") call.
     */
    public void reset() {
    }

    /**
     * This method is called when the panel is finished with. It should dispose of any components (e.g. windows) used by
     * the panel.
     */
    public void dispose() {
    }

    /*
     * @return the toolbox directory
     */

    public String getToolBox() {
        return toolpanel.getToolBox();
    }

    /**
     * @return the package name as an array of package name strings in descending order of hierarchy
     */
    public String[] getUnitPackageNames() {
        String pkg = toolpanel.getPackage();
        String[] names = pkg.split("\\.");
        return names;
    }

    /**
     * @return whether or not to create any missing directories needed for the package
     */
    public boolean createMissingDirs() {
        return createMissingCheckBox.isSelected();
    }


    /**
     * Invoked when an item has been selected or deselected by the user. The code written for this method performs the
     * operations that need to occur when an item is selected (or deselected).
     */
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getSource();
        if (source == createMissingCheckBox) {
            Env.setUserProperty(CREATE_MISSING_PASTE_DIR, new Boolean(createMissingCheckBox.isSelected()));
        }
    }

}
