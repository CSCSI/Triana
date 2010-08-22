package common.parameter;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */


import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JColorChooser;
import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.gui.windows.WindowButtonConstants;

/**
 * A ColorGenWindow UnitPanel to allow the editing of a color
 *
 * @author ian
 * @version 1.0 Final 15 Aug 2000
 * @see UnitPanel
 */
public class ColorGenWindow extends UnitPanel {

    JColorChooser colorChoice;
    public Color currentColour;


    /**
     * @return false so that parameter changes are not committed automatically
     */
    public boolean isAutoCommitByDefault() {
        return false;
    }

    /**
     * Overrides UnitPanel method to return WindowConstans.OK_CANCEl_APPLY_BUTTONS.
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS;
    }


    /**
     * Sets the unit to for ColorGen.
     */
    public void setObject(Object unit) {
        super.setObject(unit);

        colorChoice = new JColorChooser();

        layoutPanel();
        reset();
    }

    public void setColor(Color color) {
        colorChoice.setColor(color);
    }

    /**
     * Function to do the layout of the panel.
     */
    public void layoutPanel() {
        setLayout(new BorderLayout());
        add(colorChoice, BorderLayout.CENTER);
    }

    /**
     * Called when the ok button is clicked on the parameter window. Commits any parameter changes.
     */
    public void okClicked() {
        setParameter("color", ColorGen.toString(colorChoice.getColor()));
        super.okClicked();
    }

    /**
     * Called when the apply button is clicked on the parameter window. Commits any parameter changes.
     */
    public void applyClicked() {
        setParameter("color", ColorGen.toString(colorChoice.getColor()));
        super.applyClicked();
    }

    /**
     * Resets the components in the panel to those specified by the task.
     */
    public void reset() {
        super.reset();

        if ((getTask() != null) && (getTask().isParameterName("color"))) {
            setColor(ColorGen.strToColor((String) getTask().getParameter("color")));
        }

    }

}





