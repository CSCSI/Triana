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


import org.trianacode.gui.windows.ErrorDialog;
import triana.types.OldUnit;
import triana.types.SampleSet;
import triana.types.Spectrum;
import triana.types.VectorType;


/**
 * A Normalise unit to normalise the input by dividing each element by the sqrt of the sum of the squares
 *
 * @author Ian Taylor
 * @version 1.0 alpha 02 Feb 1998
 */
public class Normalise extends OldUnit {

    /**
     * ********************************************* ** USER CODE of Normalise goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        Object in = getInputAtNode(0);

        if (in instanceof SampleSet) {
            SampleSet wave = (SampleSet) in;
            double[] data = normalise(wave.data);
            output(new SampleSet(wave.samplingFrequency, data));
            return;
        }

        if (in instanceof Spectrum) {
            Spectrum wave = (Spectrum) in;
            double[] data = normalise(wave.data);
            output(new Spectrum(wave.samplingFrequency, data));
            return;
        }

        if (in instanceof VectorType) {
            VectorType raw = (VectorType) in;

            double[] data = normalise(raw.getData());
            output(new VectorType(data));
            return;
        }

        new ErrorDialog(null, "Invalid Input Data to " + getName());
        stop();
    }


    public double vectlength(double[] arr) {
        int i;
        double accum;

        accum = 0.0;
        for (i = 0; i < arr.length; ++i) {
            accum = accum + Math.pow(arr[i], 2.0);
        }
        return (Math.sqrt(accum));
    }

    public double[] normalise(double[] arr) { // normalizes a vector
        int i;
        double length;

        double[] arr1 = new double[arr.length];

        length = vectlength(arr);
        if (length > 0)                  /* Watch out division by 0 */ {
            for (i = 0; i < arr.length; ++i) {
                arr1[i] = (arr[i] / length);
            }
        } else {
            for (i = 0; i < arr.length; ++i) {
                arr1[i] = 0;
            }
        }
        return arr1;
    }

    /**
     * Initialses information specific to Normalise.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Reset's Normalise
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves parameters
     */
    public void saveParameters() {
    }

    /**
     * Sets the parameters
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Normalise, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "Spectrum VectorType SampleSet";
    }

    /**
     * @return a string containing the names of the types output from Normalise, each separated by a white space.
     */
    public String outputTypes() {
        return "Spectrum VectorType SampleSet";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Normalises by dividing each element by the sqrt of the sum of the squares";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Normalise.html";
    }
}













