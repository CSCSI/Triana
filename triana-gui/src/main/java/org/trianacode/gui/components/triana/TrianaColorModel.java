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

package org.trianacode.gui.components.triana;

import org.trianacode.gui.hci.color.*;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.tool.Tool;

import java.awt.*;

/**
 * The color model for standard Triana componenets
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class TrianaColorModel implements NodeColorModel, CableColorModel, BackgroundColorModel, TrianaColorConstants {

    private static final Color DEFAULT_COLOR = new Color(190, 190, 255);
    private static final Color GROUP_COLOR = new Color(220, 220, 90);

    private static final Color FORSHOW_CONNECTED_COLOR = new Color(255, 255, 255);
    private static final Color FORSHOW_UNCONNECTED_COLOR = new Color(255, 160, 160);

    public TrianaColorModel() {
        ColorTable.instance().initDefaultColor(this, TOOL_COLOR, DEFAULT_COLOR);
        ColorTable.instance().initDefaultColor(this, GROUP_TOOL_COLOR, GROUP_COLOR);
        ColorTable.instance().initDefaultColor(this, ERROR_TOOL_COLOR, DEFAULT_ERROR_COLOR);
        ColorTable.instance().initDefaultColor(this, NAME_COLOR, Color.black);
        ColorTable.instance().initDefaultColor(this, NODE_COLOR, Color.black);
        ColorTable.instance().initDefaultColor(this, TRIGGER_NODE_COLOR, Color.red);
        ColorTable.instance().initDefaultColor(this, ERROR_NODE_COLOR, Color.blue);
        ColorTable.instance().initDefaultColor(this, PROGRESS_COLOR, Color.green);
        ColorTable.instance().initDefaultColor(this, SHOW_TOOL_CONNECTED_COLOR, FORSHOW_CONNECTED_COLOR);
        ColorTable.instance().initDefaultColor(this, SHOW_TOOL_UNCONNECTED_COLOR, FORSHOW_UNCONNECTED_COLOR);
        ColorTable.instance().initDefaultColor(this, CABLE_COLOR, Color.black);
        ColorTable.instance().initDefaultColor(this, CONTROL_CABLE_COLOR, Color.red);
        ColorTable.instance().initDefaultColor(this, BACKGROUND_COLOR, Color.lightGray);

    }

    /**
     * @return the name of this Color model
     */
    public String getModelName() {
        return "Default Tools";
    }

    /**
     * @return the color names that this model uses. These are linked to actual colors by querying the color table.
     */
    public String[] getColorNames() {
        return new String[]{TOOL_COLOR, GROUP_TOOL_COLOR, ERROR_TOOL_COLOR, NAME_COLOR,
                NODE_COLOR, TRIGGER_NODE_COLOR, ERROR_NODE_COLOR,
                PROGRESS_COLOR, SHOW_TOOL_CONNECTED_COLOR,
                SHOW_TOOL_UNCONNECTED_COLOR, CABLE_COLOR, BACKGROUND_COLOR};
    }

    /**
     * @return the element names this color model links with color names
     */
    public String[] getElementNames() {
        return new String[]{TOOL_ELEMENT, STRIPE_ELEMENT, NAME_ELEMENT, PROGRESS_ELEMENT,
                SHOW_TOOL_CONNECTED_ELEMENT, SHOW_TOOL_UNCONNECTED_ELEMENT};
    }


    /**
     * @return the color for the specified graphical element when representing the specified tool. If the element is
     *         unrecognized this method will return a default color.
     */
    public Color getColor(String element, Tool tool) {
        if (element.equals(TOOL_ELEMENT)) {
            if (tool instanceof TaskGraph) {
                return ColorTable.instance().getColor(this, GROUP_TOOL_COLOR);
            } else if (tool.isParameterName(Task.ERROR_MESSAGE)) {
                return ColorTable.instance().getColor(this, ERROR_TOOL_COLOR);
            } else {
                return ColorTable.instance().getColor(this, TOOL_COLOR);
            }
        } else {
            return getColor(element);
        }
    }

    /**
     * @return the color for the specified graphical element not linked to a specific tool
     */
    public Color getColor(String element) {
        if (element.equals(TOOL_ELEMENT)) {
            return ColorTable.instance().getColor(this, TOOL_COLOR);
        } else if (element.equals(STRIPE_ELEMENT)) {
            return ColorTable.instance().getColor(this, TOOL_COLOR);
        } else if (element.equals(NAME_ELEMENT)) {
            return ColorTable.instance().getColor(this, NAME_COLOR);
        } else if (element.equals(PROGRESS_ELEMENT)) {
            return ColorTable.instance().getColor(this, PROGRESS_COLOR);
        } else if (element.equals(SHOW_TOOL_CONNECTED_ELEMENT)) {
            return ColorTable.instance().getColor(this, SHOW_TOOL_CONNECTED_COLOR);
        } else if (element.equals(SHOW_TOOL_UNCONNECTED_ELEMENT)) {
            return ColorTable.instance().getColor(this, SHOW_TOOL_UNCONNECTED_COLOR);
        } else {
            return Color.black;
        }
    }

    /**
     * @return the color for the specified node
     */
    public Color getColor(Node node) {
        if (node instanceof ParameterNode) {
            if (((ParameterNode) node).isTriggerNode()) {
                return ColorTable.instance().getColor(this, TRIGGER_NODE_COLOR);
            } else if (((ParameterNode) node).isErrorNode()) {
                return ColorTable.instance().getColor(this, ERROR_NODE_COLOR);
            }
        }

        return ColorTable.instance().getColor(this, NODE_COLOR);
    }

    @Override
    public Color getColor(Cable cable) {
        String type = cable.getType();
        if (type.equals(Cable.CONTROL_CABLE_TYPE)) {
            return ColorTable.instance().getColor(this, CONTROL_CABLE_COLOR);
        }
        return ColorTable.instance().getColor(this, CABLE_COLOR);
    }

    @Override
    public Color getBackground() {
        return ColorTable.instance().getColor(this, BACKGROUND_COLOR);
    }
}
