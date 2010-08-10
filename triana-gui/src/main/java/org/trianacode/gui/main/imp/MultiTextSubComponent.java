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

package org.trianacode.gui.main.imp;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.main.TrianaLayoutConstants;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class MultiTextSubComponent extends JPanel {

    private TextSubComponent main;
    private TextSubComponent sub;

    public MultiTextSubComponent(String main, String sub, TaskComponent comp) {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));
        this.main = new TextSubComponent(main, comp);
        this.sub = new TextSubComponent(sub, comp);
        this.sub.setFont(TrianaLayoutConstants.SMALL_FONT);
        add(this.main, BorderLayout.CENTER);
        add(this.sub, BorderLayout.SOUTH);
    }

    public void updateText(String main, String sub) {
        if (main != null) {
            this.main.setText(main);
        }
        if (sub != null) {
            this.sub.setText(sub);
        }
    }

    public void updateMainText(String main) {
        updateText(main, null);
    }

    public void updateSubText(String sub) {
        updateText(null, sub);

    }
}
