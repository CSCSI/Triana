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


import triana.types.OldUnit;
import triana.types.SampleSet;


/**
 * A AutoCorel unit to ..
 *
 * @author Ian
 * @version 1.0 alpha 05 Feb 1998
 */
public class AutoCorel extends OldUnit {

    /**
     * ********************************************* ** USER CODE of AutoCorel goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        SampleSet w1 = (SampleSet) getInputAtNode(0);

        double[] cross = new double[w1.size()];
        for (int i = 0; i < w1.size(); ++i) {
            cross[i] = 0.0;
        }

        int j = 0;

        do {
            for (int i = 0; i < w1.size(); ++i) {
                if ((i + j) < w1.size()) {
                    cross[j] += (w1.data[i] * w1.data[i + j]);
                }
            }
            ++j;
        }
        while (j < w1.size());

        output(new SampleSet(w1.samplingFrequency, cross));
    }


    /**
     * Initialses information specific to AutoCorel.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Reset's AutoCorel
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves AutoCorel's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads AutoCorel's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to AutoCorel, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "SampleSet";
    }

    /**
     * @return a string containing the names of the types output from AutoCorel, each separated by a white space.
     */
    public String outputTypes() {
        return "SampleSet";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Outputs the auto corelation of the input Sampleset";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "AutoCorel.html";
    }
}













