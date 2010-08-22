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
import triana.types.Const;
import triana.types.OldUnit;
import triana.types.SampleSet;
import triana.types.Spectrum;
import triana.types.VectorType;


/**
 * A Correlate unit to correlate the two inputs giving a similarity match (between 0 and 1 if used in conjunction with
 * the normalise unit) of the inputs.
 *
 * @author Ian Taylor
 * @version 1.0 alpha 02 Feb 1998
 */
public class Correlate extends OldUnit {

    /**
     * ********************************************* ** USER CODE of Correlate goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Object in = getInputAtNode(0);
        Object in1 = getInputAtNode(1);

        if (in instanceof SampleSet) {
            SampleSet wave = (SampleSet) in;
            SampleSet wave1 = (SampleSet) in1;

            double data = correlate(wave.data, wave1.data);
            output(new Const(data));
            return;
        }


        if (in instanceof Spectrum) {
            Spectrum wave = (Spectrum) in;
            Spectrum wave1 = (Spectrum) in1;
            double data = correlate(wave.data, wave1.data);
            output(new Const(data));
            return;
        }


        if (in instanceof VectorType) {
            VectorType raw = (VectorType) in;
            VectorType raw1 = (VectorType) in1;

            double data = correlate(raw.getData(), raw1.getData());
            output(new Const(data));
            return;
        }

        new ErrorDialog(null, "Invalid Input Data to " + getName());
        stop();
    }


    public double correlate(double[] a1, double a2[]) {
        // correlates two vectors
        int i;
        double result = 0.0;

        if (a1.length != a2.length) {
            new ErrorDialog(null,
                    "Invalid Sizes in Input data " + getName());
            return 0; // no correlation wrong size!
        }

        for (i = 0; i < a1.length; ++i) {
            result += (a1[i] * a2[i]);
        }

        return result;
    }

    /**
     * Initialses information specific to Correlate.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Reset's Correlate
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
     * @return a string containing the names of the types allowed to be input to Correlate, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "Spectrum VectorType SampleSet";
    }

    /**
     * @return a string containing the names of the types output from Correlate, each separated by a white space.
     */
    public String outputTypes() {
        return "Const";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Outputs a similarity rating of the two correlated inputs";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Correlate.html";
    }
}













