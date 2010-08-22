package signalproc.algorithms;

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


import java.util.Vector;

import triana.types.OldUnit;
import triana.types.Spectrum;

/**
 * A SpecAver unit to ..
 *
 * @author Ian Taylor
 * @version 1.0 alpha 11 Mar 1997
 */
public class SpecAver extends OldUnit {
    /**
     * A vector storing all of the input spectra.
     */
    Vector spectra;

    /**
     * The number of spectra to average over
     */
    int numberOfSpectra = 10;

    /**
     * The number of input spectra taken so far.
     */
    public int size;

    int current;
    int min = 1, max = 100;
    //int points;
    String parameterName = "spectra";

    /**
     * The UnitWindow for SpecAver
     */
    //IntScrollerWindow myWindow;

    /**
     * Initialses information specific to SpecAver.
     */
    public void init() {
        super.init();

        spectra = new Vector();
        setResizableInputs(false);
        setResizableOutputs(true);

        setUseGUIBuilder(true);

        //myWindow = new IntScrollerWindow(this, "Change Number of Spectra to average ?");
        //myWindow.setParameterName(parameterName);
        reset();
    }

    public void setGUIInformation() {
        addGUILine(
                "Number of Spectra to average $title " + parameterName + " IntScroller " + min + " " + max + " " + 10);
    }

    /**
     * Reset's SpecAver
     */
    public void reset() {
        super.reset();
        //numberOfSpectra = 10;
        //myWindow.setValues(min, max, numberOfSpectra);
        //myWindow.updateWidgets();
        size = 0;
        current = 0;
        spectra = new Vector(numberOfSpectra);
        spectra.setSize(numberOfSpectra);
    }

    /**
     * Saves SpecAver's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter(parameterName, numberOfSpectra);
        saveParameter("minimum", min);
        saveParameter("maximum", max);
    }


    public void setParameter(String name, String value) {
        if (name.equals(parameterName)) {
            numberOfSpectra = strToInt(value);
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
        //myWindow.setValues(min,max,numberOfSpectra);
        //myWindow.updateWidgets();
    }

    /**
     * @return a string containing the names of the types allowed to be input to SpecAver, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "Spectrum";
    }

    /**
     * @return a string containing the names of the types output from SpecAver, each separated by a white space.
     */
    public String outputTypes() {
        return "Spectrum";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Averages the input spectra";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "SpecAver.html";
    }

    /**
     * The main functionality of SpecAver goes here
     */
    public void process() {
        Spectrum s, s1;
        int i, j;

        spectra.setSize(numberOfSpectra);

        if (current >= spectra.size()) {
            current = 0;
        }

        s = (Spectrum) getInputNode(0);

        if (spectra.size() == 0) {
            output(s);
            return;
        }

        //if (firstTimeCalled())
        //points = s.size();

        spectra.setElementAt(s, current);

        ++current;

        if (current > size) {
            ++size;
        }

        if (size > spectra.size()) {
            size = spectra.size();
        }

        double[] data = new double[s.size()];

        for (i = 0; i < s.size(); ++i) {
            data[i] = 0.0;
        }

        for (j = 0; j < size; ++j) {
            s1 = (Spectrum) spectra.elementAt(j);

            for (i = 0; i < s1.size(); ++i) {
                data[i] += s1.data[i];
            }
        }

        for (i = 0; i < s.size(); ++i) {
            data[i] /= (double) size;
        }

        output(new Spectrum(s.samplingFrequency, data));
    }

    /**
     * @return SpecAver's parameter window sp that Triana 
     * can move and display it.
     */
    /*public Window getParameterWIndow() {
        return myWindow;
        }*/
}

















