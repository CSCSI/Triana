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


import java.awt.Color;
import java.awt.event.ActionEvent;

import triana.types.OldUnit;
import triana.types.Parameter;
import triana.types.util.StringSplitter;

/**
 * A ColorGen unit to generate a color parameter type!!
 *
 * @author ian
 * @version 2 Final 15 Aug 2000
 */
public class ColorGen extends OldUnit {
    /**
     * The UnitPanel for ColorGen
     */
    //ColorGenWindow myPanel;
    Color color = Color.white;

    /**
     * ********************************************* ** USER CODE of ColorGen goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        output(new Parameter(color));
    }

    /**
     * Initialses information specific to ColorGen.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

        //myPanel = new ColorGenWindow();
        //myPanel.setObject(this);
        //myPanel.setConnectedColor(color);
    }

    /**
     * Reset's ColorGen
     */
    public void reset() {
        super.reset();
    }

    /**
     * Called when the stop button is pressed within the MainTriana Window
     */
    public void stopping() {
        super.stopping();
    }

    /**
     * Called when the start button is pressed within the MainTriana Window
     */
    public void starting() {
        super.starting();
    }

    /**
     * Saves ColorGen's parameters.
     */
    public void saveParameters() {
        saveParameter("color", toString(color));
    }

    /**
     * Used to set each of ColorGen's parameters.
     */
    public void setParameter(String name, String value) {
        if (name.equals("color")) {
            color = strToColor(value);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
        //myPanel.setConnectedColor(color);
    }


    /**
     * @return a string containing the names of the types allowed to be input to ColorGen, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "none";
    }

    /**
     * @return a string containing the names of the types output from ColorGen, each separated by a white space.
     */
    public String outputTypes() {
        return "Parameter";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put ColorGen's brief description here";
    }

    /**
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ColorGen.html";
    }

    /**
     * @return ColorGen's parameter panel 
     */
/*    public UnitPanel getParameterPanel() {
        return myPanel;
        }*/

    /**
     * Captures the events thrown out by ColorGenWindow.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this
    }

    public final static String toString(Color col) {
        return toString(col.getRed()) + ' ' + toString(col.getGreen()) + ' ' + toString(col.getBlue());
    }

    public final static Color strToColor(String value) {
        StringSplitter str = new StringSplitter(value);
        return new Color((int) strToDouble(str.at(0)),
                (int) strToDouble(str.at(1)),
                (int) strToDouble(str.at(2)));
    }

}




