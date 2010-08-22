package common.sync;

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


import java.awt.Window;
import java.awt.event.ActionEvent;

import org.trianacode.gui.windows.ScrollerWindow;
import triana.types.OldUnit;


/**
 * A Pause unit to enter a delay in a triana network
 *
 * @author Ian Taylor
 * @version 1.0 alpha 21 Aug 2000
 */
public class Pause extends OldUnit {

    // some examples of parameters

    public double pause;

    /**
     * The UnitWindow for Pause
     */
    ScrollerWindow myWindow;
    String parameterName = "pause";
    double min = 0.0, max = 10.0;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "A unit to wait a specified time before continuing";
    }

    /**
     * ********************************************* ** USER CODE of Pause goes here    ***
     * *********************************************
     */
    public void process() {
        Object input;
        input = getInputAtNode(0);

        try {
            Thread.sleep((long) (pause * 1000.0));
        }
        catch (InterruptedException e) {
        }

        output(input);
    }

    /**
     * Initialses information specific to Pause.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

        setUseGUIBuilder(true);

/*        pause = 0.1;

        myWindow = new ScrollerWindow(this, "Pause (in seconds)  ?");
        myWindow.setParameterName(parameterName);        
        myWindow.setValues(0.0, 1.0, pause);
        myWindow.updateWidgets();*/
    }


    public void setGUIInformation() {
        addGUILine("Pause (secs) $title " + parameterName + " Scroller 0 1 0.1");
    }


    /**
     * Reset's Pause
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Pause's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter(parameterName, pause);
        saveParameter("minimum", min);
        saveParameter("maximum", max);
    }

    /**
     * Loads Pause's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
        if (name.equals(parameterName)) {
            pause = strToDouble(value);
        } else if (name.equals("minimum")) {
            min = strToDouble(value);
        } else if (name.equals("maximum")) {
            max = strToDouble(value);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
        myWindow.setValues(min, max, pause);
        myWindow.updateWidgets();
    }

    /**
     * over-rides the basic clean-up routine and destroys all windows relevant to Pause
     */
    public void cleanUp() {
        super.cleanUp();
        if (myWindow != null) {
            myWindow.dispose();
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to Pause, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "java.lang.Object";
    }

    /**
     * @return a string containing the names of the types output from Pause, each separated by a white space.
     */
    public String outputTypes() {
        return "java.lang.Object";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Pause.html";
    }

    /**
     * @return Pause's parameter window sp that Triana can move and display it.
     */
    public Window getParameterWindow() {
        return myWindow;
    }


    /**
     * Captures the events thrown out by ScrollerWindow.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this

        //if (e.getSource() == myWindow.slider) {
        //    pause = myWindow.getValue();
        //    }
    }
}


















