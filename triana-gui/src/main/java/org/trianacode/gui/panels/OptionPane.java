/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.gui.panels;

import org.trianacode.gui.hci.GUIEnv;

import javax.swing.*;
import java.awt.*;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jul 4, 2009: 12:39:31 PM
 * @date $Date:$ modified by $Author:$
 */

public class OptionPane {


    public static void showInformation(String msg, String title, Component parent) {
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE, GUIEnv.getTrianaIcon());
    }

    public static void showError(String msg, String title, Component parent) {
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
    }

    public static boolean showOkCancel(String msg, String title, Component parent) {
        int reply = JOptionPane.showConfirmDialog(parent, msg, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon());
        if (reply == JOptionPane.OK_OPTION) {
            return true;
        }
        return false;
    }

}
