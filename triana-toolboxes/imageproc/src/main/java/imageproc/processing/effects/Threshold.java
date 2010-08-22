package imageproc.processing.effects;

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


import java.awt.event.ActionEvent;

import triana.types.OldUnit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A Threshold unit to ..
 *
 * @author Melanie Lewis
 * @version 1.0 alpha 20 Aug 1997
 */
public class Threshold extends OldUnit {

    // some examples of parameters

    public int threshold = 128;

    /**
     * The UnitWindow for Threshold
     */
    //IntScrollerWindow myWindow;
    String parameterName = "Threshold";
    int min = 0, max = 255;

    /**
     * ********************************************* ** USER CODE of Threshold goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputNode(0);
        PixelMap pixelMap = trianaPixelMap.getPixelMap();
        PixelMap newPixelMap = new PixelMap(pixelMap);
        int[] newPixels = newPixelMap.getPixels();
        int p, a, r, g, b;

        for (int i = 0; i < newPixels.length; i++) {
            p = newPixels[i];

            a = p & 0xff000000;
            r = (p >> 16) & 0xff;
            g = (p >> 8) & 0xff;
            b = p & 0xff;

            r = (r > threshold) ? 0xff : 0;
            g = (g > threshold) ? 0xff : 0;
            b = (b > threshold) ? 0xff : 0;

            newPixels[i] = a | (r << 16) | (g << 8) | b;
        }

        output(new TrianaPixelMap(newPixelMap));
    }

    /**
     * Initialses information specific to Threshold.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

        setUseGUIBuilder(true);

        /*myWindow = new IntScrollerWindow(this, "Enter Threshold level (0 to 255)");
        myWindow.setParameterName(parameterName);
        myWindow.setValues(min, max, threshold);
        myWindow.updateWidgets();*/
    }

    public void setGUIInformation() {
        addGUILine("Threshold level (" + min + " to " + max + ") $title " + parameterName + " IntScroller " + min + " "
                + max + " " + threshold);
    }

    /**
     * Reset's Threshold
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Brightness's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter(parameterName, threshold);
        saveParameter("minimum", min);
        saveParameter("maximum", max);
    }


    public void setParameter(String name, String value) {
        if (name.equals(parameterName)) {
            threshold = strToInt(value);
        } else if (name.equals("minimum")) {
            min = strToInt(value);
        } else if (name.equals("maximum")) {
            max = strToInt(value);
        }
    }


    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
        //myWindow.setValues(min,max,threshold);
        //myWindow.updateWidgets();
    }

    /**
     * @return a string containing the names of the types allowed to be input to Threshold, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * @return a string containing the names of the types output from Threshold, each separated by a white space.
     */
    public String outputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Thresholds a TrianaPixelMap at the given value.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Threshold.html";
    }

    /**
     * @return Threshold's parameter window sp that Triana 
     * can move and display it.
     */
    /*public Window getParameterWIndow() {
        return myWindow;
        }*/


    /**
     * Captures the events thrown out by ScrollerWindow.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this

        /*if (e.getSource() == myWindow.slider) {
        threshold = myWindow.getValue();
        }*/
    }
}

















