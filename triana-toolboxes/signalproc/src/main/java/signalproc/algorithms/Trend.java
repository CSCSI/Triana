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


import triana.types.ComplexSampleSet;
import triana.types.Const;
import triana.types.OldUnit;
import triana.types.SampleSet;


/**
 * A Trend unit to accumulate single numbers into a SampleSet
 *
 * @author B F Schutz
 * @version 2.0 26 Feb 2001
 */
public class Trend extends OldUnit {

    String name = "Trend analysis";
    double sampleTime = 1;
    int length = 100;
    int count = 0;
    double[] dataOut = new double[length];
    double[] dataOutImag = new double[length];

    String acqString = "Acquistion time of first sample of data from which output is built = ";
    String labelLabel = "Input label = ";

    /**
     * ********************************************* ** USER CODE of Trend goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Const input = (Const) getInputAtNode(0);
        int timeStringIndex = -1;
        int k, end, label1, label2;
        double time = 0;
        ;
        boolean hasTime = false;
        String labelString = "";

        double dataIn = input.getReal();
        double dataInImag = 0;
        boolean complex = input.isComplex();
        if (complex) {
            dataInImag = input.getImag();
        }

        if (count < length) {
            for (k = count; k > 0; k--) {
                dataOut[k] = dataOut[k - 1];
            }
            dataOut[0] = dataIn;
            if (complex) {
                for (k = count; k > 0; k--) {
                    dataOutImag[k] = dataOutImag[k - 1];
                }
                dataOutImag[0] = dataInImag;
            }
        } else {
            for (k = length - 1; k > 0; k--) {
                dataOut[k] = dataOut[k - 1];
            }
            dataOut[0] = dataIn;
            if (complex) {
                for (k = length - 1; k > 0; k--) {
                    dataOutImag[k] = dataOutImag[k - 1];
                }
                dataOutImag[0] = dataInImag;
            }
        }
        count++;
        if (!complex) {
            SampleSet outputSet = new SampleSet(1 / sampleTime, dataOut, time);
            outputSet.setDependentLabels(0, name + labelString);
            output(outputSet);
        } else {
            ComplexSampleSet outputSet = new ComplexSampleSet(1 / sampleTime, dataOut, dataOutImag, time);
            outputSet.setDependentLabels(0, name + labelString);
            output(outputSet);
        }

    }


    /**
     * Initialses information specific to Trend.
     */
    public void init() {
        super.init();
        count = 0;
        setUseGUIBuilder(true);

        setRequireDoubleInputs(false);
        setCanProcessDoubleArrays(false);

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Give the name of the data  $title name TextField Trend analysis");
        addGUILine("Time between samples (s) $title sampleTime Scroller 0 100 1");
        addGUILine("Number of samples to accumulate $title length IntScroller 0 1000 100");
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
     * Saves Trend's parameters.
     */
    public void saveParameters() {
        saveParameter("name", name);
        saveParameter("sampleTime", sampleTime);
        saveParameter("length", length);
    }


    /**
     * Used to set each of Trend's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("name")) {
            name = value;
        }
        if (name.equals("sampleTime")) {
            sampleTime = strToDouble(value);
        }
        if (name.equals("length")) {
            length = strToInt(value);
            dataOut = new double[length];
            dataOutImag = new double[length];
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Trend, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "Const";
    }

    /**
     * @return a string containing the names of the types output from Trend, each separated by a white space.
     */
    public String outputTypes() {
        return "SampleSet ComplexSampleSet";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Accumulates input numbers into a sequence";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Trend.html";
    }
}




