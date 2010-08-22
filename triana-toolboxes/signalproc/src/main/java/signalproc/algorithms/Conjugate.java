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
import triana.types.ComplexSpectrum;
import triana.types.OldUnit;


/**
 * A Conjugate unit to take the complex conjugate of the input
 *
 * @author ian
 * @version 1.0 alpha 02 Apr 1997
 */
public class Conjugate extends OldUnit {


    /**
     * Conjugate takes a complex input i.e. either a ComplexSpectrum or a ComplexSampleSet and outputs its complex
     * conjugate.
     */
    public void process() {
        Object input;

        input = getInputAtNode(0);

        if (input instanceof ComplexSpectrum) {
            for (int i = 0; i < ((ComplexSpectrum) input).size(); ++i) {
                ((ComplexSpectrum) input).imag[i] = -((ComplexSpectrum) input).imag[i];
            }
        } else {
            for (int i = 0; i < ((ComplexSampleSet) input).size(); ++i) {
                ((ComplexSampleSet) input).imag[i] = -((ComplexSampleSet) input).imag[i];
            }
        }

        output(input);  // change result to incorporate other inputs
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Performs the Complex Conjugate on the complex input series";
    }

    /**
     * Initialses information specific to Conjugate.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * Reset's Conjugate public void reset() { super.reset(); }
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
     * @return a string containing the names of the types allowed to be input to Conjugate, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "ComplexSampleSet ComplexSpectrum";
    }

    /**
     * @return a string containing the names of the types output from Conjugate, each separated by a white space.
     */
    public String outputTypes() {
        return "ComplexSampleSet ComplexSpectrum";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Conjugate.html";
    }
}













