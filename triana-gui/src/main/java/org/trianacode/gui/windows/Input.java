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


import org.trianacode.gui.hci.GUIEnv;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Just calls the JOptionPane with the appropriate values for the particular
 * input you want to get from the user. You use it like this :-
 * <p/>
 * <pre>     double inp = Input.aNumber("Enter the value for beta");
 * String s = Input.aString("Enter your name");   </pre>
 * etc....
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @created 16 Jan 1997
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class Input implements Serializable {

    /**
     * Prompts the user to enter an integer
     */
    public static int anInt(String text) {
        return anInt(null, text);
    }

    /**
     * Prompts the user to enter an integer
     */
    public static int anInt(Component parent, String text) {
        String in = aString(parent, text);
        try {
            return Integer.parseInt(in);
        }
        catch (Exception ee) {
            ErrorDialog.show("Wrong Format!! Please Enter an Integer value");
            return anInt(parent, text);
        }
    }

    /**
     * Prompts the user to enter a double
     */
    public static double aDouble(String text) {
        return aDouble(null, text);
    }

    /**
     * Prompts the user to enter a double
     */
    public static double aDouble(Component parent, String text) {
        String in = aString(parent, text);
        try {
            return Double.parseDouble(in);
        }
        catch (Exception ee) {
            ErrorDialog.show("Wrong Format!! Please Enter an Double value");
            return aDouble(parent, text);
        }
    }

    /**
     * Prompts the user to enter a String
     */
    public static String aString(String text) {
        return aString(null, text);
    }

    /**
     * Prompts the user to enter a String
     */
    public static String aString(Component parent, String text) {
        return (String) JOptionPane.showInputDialog(parent, text, "Triana Input Dialog",
                JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaImageIcon(), null, "");
    }


}











