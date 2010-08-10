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
package org.trianacode.gui.action;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

/**
 * A class for building InputMap and ActionMap key binding classes.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public class MainTrianaKeyMapFactory implements Actions {

    /**
     * The Dispay options for the actions, name, icon or both
     */
    private int display;

    /**
     * Maps
     */
    private InputMap inmap;
    private ActionMap acmap;

    /**
     * Create a Factory to return the InputMap and ActionMap classes for the selected SelectionManager.
     *
     * @see SelectionManager
     */
    public MainTrianaKeyMapFactory(ToolSelectionHandler sel, int displayOptions) {
        display = displayOptions;
        addStandardBindings();
        addExtraBindings();
    }

    /**
     * @return The InputMap for the selelected component
     */
    public InputMap getInputMap() {
        return inmap;
    }

    /**
     * @return The ActionMap for the selelected component
     */
    public ActionMap getActionMap() {
        return acmap;
    }

    /**
     * Add the standard Cut/Copy/Paste OS specific bindings
     */
    private void addStandardBindings() {
        inmap = new InputMap();
        acmap = new ActionMap();

        Action copy = ActionTable.getAction(Actions.COPY_ACTION);
        inmap.put((KeyStroke) copy.getValue(Action.ACCELERATOR_KEY), copy.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(copy.getValue(Action.ACTION_COMMAND_KEY), copy);

        Action cut = ActionTable.getAction(Actions.CUT_ACTION);
        inmap.put((KeyStroke) cut.getValue(Action.ACCELERATOR_KEY), cut.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(cut.getValue(Action.ACTION_COMMAND_KEY), cut);

        Action paste = ActionTable.getAction(Actions.PASTE_ACTION);
        inmap.put((KeyStroke) paste.getValue(Action.ACCELERATOR_KEY), paste.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(paste.getValue(Action.ACTION_COMMAND_KEY), paste);

        Action help = ActionTable.getAction(Actions.HELP_ACTION);
        inmap.put((KeyStroke) help.getValue(Action.ACCELERATOR_KEY), help.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(help.getValue(Action.ACTION_COMMAND_KEY), help);

        Action open = ActionTable.getAction(OPEN_ACTION);
        inmap.put((KeyStroke) open.getValue(Action.ACCELERATOR_KEY), open.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(open.getValue(Action.ACTION_COMMAND_KEY), open);

        Action save = ActionTable.getAction(SAVE_ACTION);
        inmap.put((KeyStroke) save.getValue(Action.ACCELERATOR_KEY), save.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(save.getValue(Action.ACTION_COMMAND_KEY), save);

        Action newA = ActionTable.getAction(NEW_ACTION);
        inmap.put((KeyStroke) newA.getValue(Action.ACCELERATOR_KEY), newA.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(newA.getValue(Action.ACTION_COMMAND_KEY), newA);

        Action close = ActionTable.getAction(CLOSE_ACTION);
        inmap.put((KeyStroke) close.getValue(Action.ACCELERATOR_KEY), close.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(close.getValue(Action.ACTION_COMMAND_KEY), close);

        Action select = ActionTable.getAction(SELECT_ALL_ACTION);
        inmap.put((KeyStroke) select.getValue(Action.ACCELERATOR_KEY), select.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(select.getValue(Action.ACTION_COMMAND_KEY), select);

        Action find = ActionTable.getAction(FIND_ACTION);
        inmap.put((KeyStroke) find.getValue(Action.ACCELERATOR_KEY), find.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(find.getValue(Action.ACTION_COMMAND_KEY), find);

        Action group = ActionTable.getAction(GROUP_ACTION);
        inmap.put((KeyStroke) group.getValue(Action.ACCELERATOR_KEY), group.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(group.getValue(Action.ACTION_COMMAND_KEY), group);

        Action ungroup = ActionTable.getAction(UNGROUP_ACTION);
        inmap.put((KeyStroke) ungroup.getValue(Action.ACCELERATOR_KEY), ungroup.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(ungroup.getValue(Action.ACTION_COMMAND_KEY), ungroup);

        Action zoomout = ActionTable.getAction(ZOOMOUT_ACTION);
        inmap.put((KeyStroke) zoomout.getValue(Action.ACCELERATOR_KEY), zoomout.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(zoomout.getValue(Action.ACTION_COMMAND_KEY), zoomout);

        Action zoomin = ActionTable.getAction(ZOOMIN_ACTION);
        inmap.put((KeyStroke) zoomin.getValue(Action.ACCELERATOR_KEY), zoomin.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(zoomin.getValue(Action.ACTION_COMMAND_KEY), zoomin);

        Action incIn = ActionTable.getAction(INC_INPUT_NODES_ACTION);
        inmap.put((KeyStroke) incIn.getValue(Action.ACCELERATOR_KEY), incIn.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(incIn.getValue(Action.ACTION_COMMAND_KEY), incIn);

        Action incOut = ActionTable.getAction(INC_OUTPUT_NODES_ACTION);
        inmap.put((KeyStroke) incOut.getValue(Action.ACCELERATOR_KEY), incOut.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(incOut.getValue(Action.ACTION_COMMAND_KEY), incOut);

        Action decIn = ActionTable.getAction(DEC_INPUT_NODES_ACTION);
        inmap.put((KeyStroke) decIn.getValue(Action.ACCELERATOR_KEY), decIn.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(decIn.getValue(Action.ACTION_COMMAND_KEY), decIn);

        Action decOut = ActionTable.getAction(DEC_OUTPUT_NODES_ACTION);
        inmap.put((KeyStroke) decOut.getValue(Action.ACCELERATOR_KEY), decOut.getValue(Action.ACTION_COMMAND_KEY));
        acmap.put(decOut.getValue(Action.ACTION_COMMAND_KEY), decOut);
    }

    /**
     * TODO Add any additional bindings for existing actions
     */
    private void addExtraBindings() {
    }

}
