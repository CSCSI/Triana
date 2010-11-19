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

import org.trianacode.gui.util.Env;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class TFileChooser extends JFileChooser implements ActionListener {

    private String dirtype = null;

    /**
     * Create a TFileChooser pointing to the user's default directory
     */
    public TFileChooser() {
        super();
    }

    /**
     * Create a TFileChooser pointing to the current directory for the specified directory type.
     */
    public TFileChooser(String dirtype) {
        super();

        this.dirtype = dirtype;

        File dir = new File(Env.getDirectory(dirtype));

        if (dir.exists()) {
            setCurrentDirectory(dir);
        }

        addActionListener(this);
    }


    public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
        setApproveButtonText(approveButtonText);
        int result = super.showDialog(parent, null);

        return result;
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        if ((dirtype != null) && (e.getActionCommand().equals(APPROVE_SELECTION))) {
            Env.setDirectory(dirtype, getCurrentDirectory().getAbsolutePath());
        }
    }

}
