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
package org.trianacode.gui.components.script;

import java.awt.Color;

import org.trianacode.gui.hci.color.ColorModel;
import org.trianacode.gui.hci.color.ColorTable;
import org.trianacode.gui.hci.color.TrianaColorConstants;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

/**
 * The color model for web service tools
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class ScriptColorModel implements ColorModel, TrianaColorConstants {

    public static final Color SCRIPT_COLOR = new Color(88, 230, 109);
    public static final Color ERROR_COLOR = new Color(180, 180, 180);


    public ScriptColorModel() {
        ColorTable.instance().initDefaultColor(this, TOOL_COLOR, SCRIPT_COLOR);
        ColorTable.instance().initDefaultColor(this, ERROR_TOOL_COLOR, DEFAULT_ERROR_COLOR);
    }

    /**
     * @return the name of this Color model
     */
    public String getModelName() {
        return "Triana Scripts";
    }

    /**
     * @return the color names that this model uses. These are linked to actual colors by querying the color table.
     */
    public String[] getColorNames() {
        return new String[]{TOOL_COLOR, ERROR_TOOL_COLOR};
    }

    /**
     * @return the element names this color model links with color names
     */
    public String[] getElementNames() {
        return new String[]{TOOL_ELEMENT};
    }

    /**
     * @return the color for the specified graphical element when representing the specified tool. If the element is
     *         unrecognized this method will return a default color.
     */
    public Color getColor(String element, Tool tool) {
        if (element.equals(TOOL_ELEMENT)) {
            if (tool.isParameterName(Task.ERROR_MESSAGE)) {
                return ColorTable.instance().getColor(this, ERROR_TOOL_COLOR);
            } else {
                return getColor(element);
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
        } else {
            return Color.black;
        }
    }


}
