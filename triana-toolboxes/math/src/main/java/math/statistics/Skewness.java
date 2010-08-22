package math.statistics;

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


import triana.types.Const;
import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.util.FlatArray;

/**
 * A Skewness unit to compute the skewness or sample skewness (normalized third moment about the mean) of any data set.
 * Skewness is normalized by the standard deviation cubed. "Sample" values differ from ordinary ones by normalization:
 * divide by n-1 instead of n. For complex data the method returns the complex skewness (skewness computed with cubes of
 * complex numbers).
 *
 * @author B F Schutz
 * @version 1.0 28 Feb 2001
 */
public class Skewness extends OldUnit {

    int dv = 0;
    String type = "Skewness";


    /**
     * ********************************************* ** USER CODE of Skewness goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType input = (GraphType) getInputAtNode(0);

        FlatArray flatR, flatI;
        double[] dataR, dataI;
        double meanR, meanI, yI, yR, t1, t2;
        int k, len;
        double answerR = Double.NaN;
        double answerI = Double.NaN;
        double variance = 0;
        double std = 0;
        boolean complex;

        if (input.isArithmeticArray(dv)) {
            complex = input.isDependentComplex(dv);
            if (complex) {
                flatR = new FlatArray(input.getDataArrayReal(dv));
                flatI = new FlatArray(input.getDataArrayImag(dv));
                meanR = 0;
                meanI = 0;
                dataR = (double[]) flatR.getFlatArray();
                dataI = (double[]) flatI.getFlatArray();
                len = dataR.length;
                for (k = 0; k < len; k++) {
                    meanR += dataR[k];
                    meanI += dataI[k];
                }
                meanR /= len;
                meanI /= len;
                answerR = 0;
                answerI = 0;
                variance = 0;
                for (k = 0; k < len; k++) {
                    yR = dataR[k] - meanR;
                    yI = dataI[k] - meanI;
                    t1 = yR * yR;
                    t2 = yI * yI;
                    variance += t1 + t2;
                    answerR += t1 + yR - 3 * t2 + yR;
                    answerI += 3 * t1 * yI - t2 * yI;
                }
            } else {
                flatR = new FlatArray(input.getDataArrayReal(dv));
                meanR = 0;
                dataR = (double[]) flatR.getFlatArray();
                len = dataR.length;
                for (k = 0; k < len; k++) {
                    meanR += dataR[k];
                }
                meanR /= len;
                answerR = 0;
                variance = 0;
                for (k = 0; k < len; k++) {
                    yR = dataR[k] - meanR;
                    t1 = yR * yR;
                    variance += t1;
                    answerR += t1 * yR;
                }
            }
            if ((len > 1) && type.equals("SampleSkewness")) {
                len--;
            }
            std = Math.sqrt(variance / len);
            std *= (std * std);
            if (complex) {
                answerR /= (len * std);
                answerI /= (len * std);
                output(new Const(answerR, answerI));
            } else {
                answerR /= (len * std);
                output(new Const(answerR));
            }


        }


    }


    /**
     * Initialses information specific to Skewness.
     */
    public void init() {
        super.init();

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
        addGUILine("Which dependent variable do you want the skewness of? $title dv IntScroller 0 5 0");
        addGUILine("Choose value to be computed: $title type Choice Skewness SampleSkewness");
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
     * Saves Skewness's parameters.
     */
    public void saveParameters() {
        saveParameter("dv", dv);
        saveParameter("type", type);
    }


    /**
     * Used to set each of Skewness's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("dv")) {
            dv = strToInt(value);
        }
        if (name.equals("type")) {
            type = value;
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Skewness, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType";
    }

    /**
     * @return a string containing the names of the types output from Skewness, each separated by a white space.
     */
    public String outputTypes() {
        return "Const";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Compute the skewness or sample skewness of any data set";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Skewness.html";
    }
}




