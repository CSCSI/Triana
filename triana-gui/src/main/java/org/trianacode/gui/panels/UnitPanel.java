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

import org.trianacode.gui.windows.WindowButtonConstants;

import java.awt.*;
import java.beans.Customizer;

/**
 * UI for Units
 factor out and redesign UI at later point
 *
 * @author      Your Name Here...
 * @created     Todays Date Here...
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class UnitPanel extends ParameterPanel implements Customizer {

    /**
     * @return true so that parameter changes are not committed automatically
     */
    public boolean isAutoCommitByDefault () {
        return true;
    }

    /**
     * Overrides super method to return WindowConstans.OK_BUTTON, as required by legacy panels.
     */
    public byte getPreferredButtons () {
        return WindowButtonConstants.OK_BUTTON;
    }


    /**
     * This method is called when the task is set for this panel. It can be overridden to create the panel layout.
     */
    public void init () {
        setObject (null);
    }


    /**
     * This method should be overridden to reset all components to the values apecified in task.
     */
    public void reset () {
    }

    /**
     * Called when the panel is finished with. This method should be overridden to dispose of all of the panels
     * components.
     */
    public void dispose () {

    }


    /**
     * @deprecated
     */
    public void setObject(Object unit) {
//        this.unit = (OldUnit) unit;

        /*if (unit == null) {
            setName("NULL OldUnit Panel");
            return;
        }

        if (!(unit instanceof OldUnit)) {
            ErrorDialog.show(null, "OldUnit " + ((OldUnit) unit).getName() + " IS NOT A Triana OldUnit\n" +
                                   "A OldUnit Panel cannot be created for NON Triana units");
            return;
        }
        oclUnit = (OldUnit) unit;
        setName(oclUnit.getName());   */
    }

    /**
     * Sets the parameter in the unit identified by <i>parName</i>
     * to the given <i>value</i>.
     */
    public void setParameter(String parName, String value) {
        super.setParameter(parName, value);
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     * @deprecated  As of Triana 2.0, replaced by {@link #updateParameter(java.lang.String, double)}
     */
    public void setParameter(String parName, double value) {
        setParameter(parName, String.valueOf(value));
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     * @deprecated  As of Triana 2.0, replaced by {@link #updateParameter(java.lang.String, boolean)}
     */
    public void setParameter(String parName, boolean value) {
        setParameter(parName, String.valueOf(value));
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     * @deprecated  As of Triana 2.0, replaced by {@link #updateParameter(java.lang.String, float)}
     */
    public void setParameter(String parName, float value) {
        setParameter(parName, String.valueOf(value));
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     * @deprecated  As of Triana 2.0, replaced by {@link #updateParameter(java.lang.String, int)}
     */
    public void setParameter(String parName, int value) {
        setParameter(parName, String.valueOf(value));
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     * @deprecated  As of Triana 2.0, replaced by {@link #updateParameter(java.lang.String, short)}
     */
    public void setParameter(String parName, short value) {
        setParameter(parName, String.valueOf(value));
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     * @deprecated  As of Triana 2.0, replaced by {@link #updateParameter(java.lang.String, long)}
     */
    public void setParameter(String parName, long value) {
        setParameter(parName, String.valueOf(value));
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     * @deprecated  As of Triana 2.0, replaced by {@link #updateParameter(java.lang.String, byte)}
     */
    public void setParameter(String parName, byte value) {
        setParameter(parName, String.valueOf(value));
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     * @deprecated  As of Triana 2.0, replaced by {@link #updateParameter(java.lang.String, java.awt.Color)}
     */
    public void setParameter(String parName, Color value) {
        setParameter(parName, String.valueOf(value));
    }

    /**
     * Sets the parameter in the unit identified by <i>parName</i>
     * to the given <i>value</i>.  This function basically calls the
     * setParameter function within the unit and notifies parameter
     * listeners that there has been a change.
     */
    public void updateParameter(String parName, String value) {
//        oclUnit.updateParameter(parName, value);
        setParameter (parName, value);
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     */
    public void updateParameter(String parName, double value) {
//        oclUnit.updateParameter(parName, value);
        setParameter (parName, value);
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     */
    public void updateParameter(String parName, boolean value) {
//        oclUnit.updateParameter(parName, value);
        setParameter (parName, value);
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     */
    public void updateParameter(String parName, float value) {
//        oclUnit.updateParameter(parName, value);
        setParameter (parName, value);
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     */
    public void updateParameter(String parName, int value) {
//        oclUnit.updateParameter(parName, value);
        setParameter (parName, value);
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     */
    public void updateParameter(String parName, short value) {
//        oclUnit.updateParameter(parName, value);
        setParameter (parName, value);
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     */
    public void updateParameter(String parName, long value) {
//        oclUnit.updateParameter(parName, value);
        setParameter (parName, value);
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     */
    public void updateParameter(String parName, byte value) {
//        oclUnit.updateParameter(parName, value);
        setParameter (parName, value);
    }

    /**
     * Calls the setParameter function with the given parameter converted
     * to a string
     */
    public void updateParameter(String parName, Color value) {
//        oclUnit.updateParameter(parName, value);
        setParameter (parName, value);
    }

    /**
     * This function can be used to layout your panel. Otherwise put
     * your layout code in setObject directly. This function
     * here to make things more readable.
     */
    //public void layoutPanel() {
    //}

    /**
     * @return the reference to the actual OCL unit which this unit
     * window is created for.
     * @see triana.unit.OldUnit
     * @deprecated
     */
    /*public OldUnit getOCLUnit() {
        return unit;
    } */

/*    public void addPropertyChangeListener(PropertyChangeListener l) {
        support.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        support.removePropertyChangeListener(l);
    } */
}










