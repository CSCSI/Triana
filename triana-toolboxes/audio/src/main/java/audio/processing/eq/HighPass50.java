package audio.processing.eq;

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
 * A HighPass50 unit to ..
 *
 * @author ian
 * @version 2.0 09 Nov 2000
 */
public class HighPass50 extends OldUnit {
    static int NZEROS = 10;
    static int NPOLES = 10;
    static double GAIN = 1.105632792e+00;

    double xv[] = new double[NZEROS + 1];
    double yv[] = new double[NPOLES + 1];

    public void process(double data[], double dataOut[]) {
        for (int i = 0; i < data.length; ++i) {
            xv[0] = xv[1];
            xv[1] = xv[2];
            xv[2] = xv[3];
            xv[3] = xv[4];
            xv[4] = xv[5];
            xv[5] = xv[6];
            xv[6] = xv[7];
            xv[7] = xv[8];
            xv[8] = xv[9];
            xv[9] = xv[10];
            xv[10] = data[i] / GAIN;
            yv[0] = yv[1];
            yv[1] = yv[2];
            yv[2] = yv[3];
            yv[3] = yv[4];
            yv[4] = yv[5];
            yv[5] = yv[6];
            yv[6] = yv[7];
            yv[7] = yv[8];
            yv[8] = yv[9];
            yv[9] = yv[10];
            yv[10] = (xv[0] + xv[10]) - 10 * (xv[1] + xv[9]) + 45 * (xv[2] + xv[8]) - 120 * (xv[3] + xv[7]) + 210 * (
                    xv[4] + xv[6]) - 252 * xv[5] + (-0.8180468523 * yv[0]) + (8.3447521968 * yv[1])
                    + (-38.3071854240 * yv[2]) + (104.2131178600 * yv[3]) + (-186.0600309300 * yv[4])
                    + (227.7961261700 * yv[5]) + (-193.6852501500 * yv[6]) + (112.9300539100 * yv[7])
                    + (-43.2127124960 * yv[8]) + (9.7991757162 * yv[9]);
            dataOut[i] = yv[10];
        }
    }

    public void process5(double data[], double dataOut[]) {
        for (int i = 0; i < data.length; ++i) {
            xv[0] = xv[1];
            xv[1] = xv[2];
            xv[2] = xv[3];
            xv[3] = xv[4];
            xv[4] = xv[5];
            xv[5] = data[i] / GAIN;
            yv[0] = yv[1];
            yv[1] = yv[2];
            yv[2] = yv[3];
            yv[3] = yv[4];
            yv[4] = yv[5];
            yv[5] = (xv[5] - xv[0]) + 5 * (xv[1] - xv[4]) + 10 * (xv[3] - xv[2])
                    + (0.8304354064 * yv[0]) + (-4.3109482074 * yv[1])
                    + (8.9494880120 * yv[2]) + (-9.2878575104 * yv[3])
                    + (4.8188816051 * yv[4]);
            dataOut[i] = yv[5];
        }
    }

    /**
     * ********************************************* ** USER CODE of HighPass50 goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        SampleSet input = (SampleSet) getInputNode(0);

        double dataIn[] = input.data;
        double dataOut[] = new double[dataIn.length];

        process(dataIn, dataOut);

        output(new SampleSet(input.samplingFrequency(), dataOut));
    }


    /**
     * Initialses information specific to HighPass50.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
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
     * Saves HighPass50's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of HighPass50's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to HighPass50, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "SampleSet";
    }

    /**
     * @return a string containing the names of the types output from HighPass50, each separated by a white space.
     */
    public String outputTypes() {
        return "SampleSet";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put HighPass50's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "HighPass50.html";
    }
}



