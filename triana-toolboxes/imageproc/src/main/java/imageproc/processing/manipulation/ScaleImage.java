package imageproc.processing.manipulation;

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


import java.awt.Canvas;
import java.awt.Image;
import java.awt.event.ActionEvent;

import triana.types.OldUnit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A ScaleImage unit to ..
 *
 * @author Melanie Lewis
 * @version 1.0 alpha 20 Aug 1997
 */
public class ScaleImage extends OldUnit {

    // some examples of parameters

    public int scale = 100;

    /**
     * The UnitWindow for Threshold
     */
    //IntScrollerWindow myWindow;
    String parameterName = "scale";
    int min = 10, max = 1000;

    /**
     * ********************************************* ** USER CODE of Threshold goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputNode(0);
        PixelMap newPixelMap = new PixelMap(trianaPixelMap.getPixelMap());

        Canvas canvas = new Canvas(); // Used as an image consumer
        Image image = canvas.createImage(newPixelMap.getImageProducer());

        int x = (newPixelMap.getWidth() * scale) / 100;
        int y = (newPixelMap.getHeight() * scale) / 100;

        output(new TrianaPixelMap(new
                PixelMap(image.getScaledInstance(x, y, Image.SCALE_SMOOTH))));
    }

    /**
     * Initialses information specific to Threshold.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

        setUseGUIBuilder(true);

/*        myWindow = new IntScrollerWindow(this, "Enter the scaling factor in %");
        myWindow.setParameterName(parameterName);
        myWindow.setValues(min, max, scale);
        myWindow.updateWidgets();*/
    }


    public void setGUIInformation() {
        addGUILine("Scaling factor (" + min + "% to " + max + "%) $title " + parameterName + " IntScroller " + min + " "
                + max + " " + scale);
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
        saveParameter(parameterName, scale);
        saveParameter("minimum", min);
        saveParameter("maximum", max);
    }


    public void setParameter(String name, String value) {
        if (name.equals(parameterName)) {
            scale = strToInt(value);
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
        //myWindow.setValues(min,max,scale);
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
        return "Scales a TrianaPixelMap.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ScaleImage.html";
    }

    /**
     * @return Threshold's parameter window sp that Triana 
     * can move and display it.
     */
    /*public Window getParameterWIndow() {
        return myWindow;
        } */


    /**
     * Captures the events thrown out by ScrollerWindow.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this

        /*if (e.getSource() == myWindow.slider) {
       scale = myWindow.getValue();
       } */
    }
}
