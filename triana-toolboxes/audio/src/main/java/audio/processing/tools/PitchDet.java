package audio.processing.tools;

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


import triana.types.OldUnit;
import triana.types.SampleSet;


/**
 * A PitchDet unit to ..
 *
 * @author ian
 * @version 2.0 09 Nov 2000
 */
public class PitchDet extends OldUnit {
    int filterSize = 100;
    int filterDet = 200;
    int thresh = 50;
    boolean mute = false;

    /**
     * ********************************************* ** USER CODE of PitchDet goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        SampleSet input = (SampleSet) getInputNode(0);

        double dataIn[] = input.data;
        double dataOut[] = new double[dataIn.length];
        double filter[] = new double[filterSize];
        int i = 0, j, k;
        double sum;

        while (i < (dataIn.length - filterSize - filterDet)) {
            for (j = 0; j < filterSize; ++j) {
                filter[j] = dataIn[i + j];
            }

            for (k = 20; k < filterDet; ++k) {
                sum = 0.0;
                for (j = 0; j < filterSize; ++j) {
                    sum += (filter[j] - dataIn[i + j + k]);
                }
                if (Math.abs(sum) < thresh) {
                    System.out.println("Pitch = " + k + " val = " + sum);
                    break;
                }
            }

            if (k < filterSize - 1) // found pitch
            {
                for (j = 0; j < filterSize; ++j) {
                    dataOut[i + j] = dataIn[i + j];
                }
            } else {
                for (j = 0; j < filterSize; ++j) {
                    dataOut[i + j] = 0.0;
                }
            }   // no pitch cancel waveform

            i += k; // go to next point where pitch is
        }

        output(new SampleSet(input.samplingFrequency(), dataOut));
    }


    /**
     * Initialses information specific to PitchDet.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Enter Size Of Pitch Template (samples) $title filterSize IntScroller 0 200 100");
        addGUILine("Enter Number of Samples to apply pitch template to $title filterDet IntScroller 0 500 200");
        addGUILine("Enter Threshold $title thresh IntScroller 0 200 50");
        addGUILine("Mute $title mute Checkbox false");
    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
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
     * Saves PitchDet's parameters.
     */
    public void saveParameters() {
        saveParameter("filterSize", filterSize);
        saveParameter("filterDet", filterSize);
        saveParameter("thresh", thresh);
        saveParameter("mute", mute);
    }


    /**
     * Used to set each of PitchDet's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("filterSize")) {
            filterSize = strToInt(value);
        }
        if (name.equals("filterDet")) {
            filterSize = strToInt(value);
        }
        if (name.equals("thresh")) {
            thresh = strToInt(value);
        }
        if (name.equals("mute")) {
            mute = strToBoolean(value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to PitchDet, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "SampleSet";
    }

    /**
     * @return a string containing the names of the types output from PitchDet, each separated by a white space.
     */
    public String outputTypes() {
        return "SampleSet";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put PitchDet's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "PitchDet.html";
    }
}




