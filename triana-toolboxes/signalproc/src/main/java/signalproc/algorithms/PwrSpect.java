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


import triana.types.ComplexSpectrum;
import triana.types.OldUnit;
import triana.types.Spectrum;


/**
 * PwrSpect takes an input ComplexSpectrum and outputs a Spectrum consisting of the squared magnitude of the complex
 * elements of the input.
 *
 * @author Ian Taylor
 * @author B F Schutz
 * @version 2.0 10 April 2000
 */
public class PwrSpect extends OldUnit {

    /**
     * ********************************************* ** USER CODE of PwrSpect goes here    ***
     * *********************************************
     */
    public void process() {
        ComplexSpectrum input;

        input = (ComplexSpectrum) getInputNode(0);

        double[] power = new double[input.size()];
        double[] re = input.getDataReal();
        double[] im = input.getDataImag();
        for (int i = 0; i < input.size(); ++i) {
            power[i] = (re[i] * re[i]) +
                    (im[i] * im[i]);
        }

        Spectrum output = new Spectrum(input.isTwoSided(), input.isNarrow(), input.size(), input.size(),
                input.getFrequencyResolution(), input.getUpperFrequencyBound());
        output.setData(power);
        output(output);  // output the modified input
    }


    /**
     * Initialses information specific to PwrSpect.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * Reset's PwrSpect public void reset() { super.reset(); }
     * <p/>
     * /** Saves parameters
     */
    public void saveParameters() {
    }

    /**
     * Sets the parameters
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to PwrSpect, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "ComplexSpectrum";
    }

    /**
     * @return a string containing the names of the types output from PwrSpect, each separated by a white space.
     */
    public String outputTypes() {
        return "Spectrum";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts a complex frequency sampleset into a power spectrum";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "PwrSpect.html";
    }
}













