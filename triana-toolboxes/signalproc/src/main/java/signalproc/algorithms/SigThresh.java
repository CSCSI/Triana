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


import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.util.FlatArray;

/**
 * A SigThresh unit to route an input GraphType data set to one of two output nodes, depending on whether the data it
 * contains exceed a particular threshold. The user can choose among several types of threshold computations, and the
 * level of the threshold.
 *
 * @author Ian Taylor
 * @author Bernard Schutz
 * @version $Revision: 2921 $
 */
public class SigThresh extends OldUnit {

    // some examples of parameters

    public double threshold = 1000;
    public String type = "Amplitude";

    double passedValue = 0.0;

    /**
     * ********************************************* ** USER CODE of SigThresh goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        boolean passed = false;
        GraphType input = (GraphType) getInputNode(0);
        int dv = 0;
        while (!input.isArithmeticArray(dv)) {
            dv++;
        }

        FlatArray flr = new FlatArray(input.getDataArrayReal(dv));
        double[] dataR = (double[]) flr.getFlatArray();
        FlatArray fli = null;
        double[] dataI = null;
        boolean complex = input.isDependentComplex(dv);
        if (complex) {
            fli = new FlatArray(input.getDataArrayImag(dv));
            dataI = (double[]) fli.getFlatArray();
        }


        if (type.equals("Amplitude")) {
            if (threshold > 0) {
                for (int i = 0; i < dataR.length; ++i) {
                    if (dataR[i] > threshold) {
                        passed = true;
                        passedValue = dataR[i];
                        break;
                    }
                }

            } else {
                for (int i = 0; i < dataR.length; ++i) {
                    if (dataR[i] < threshold) {
                        passed = true;
                        passedValue = dataR[i];
                        break;
                    }
                }
            }
        } else if (type.equals("Mean")) {
            double sumR = 0.0;
            for (int i = 0; i < dataR.length; ++i) {
                sumR += dataR[i];
            }
            double meanR = sumR / dataR.length;
            if (meanR > threshold) {
                passed = true;
            }
        } else if (type.equals("RMS")) {
            double rms = 0.0;
            for (int i = 0; i < dataR.length; ++i) {
                rms += dataR[i] * dataR[i];
            }
            if (complex) {
                for (int i = 0; i < dataR.length; ++i) {
                    rms += dataI[i] * dataI[i];
                }
            }

            rms = Math.sqrt(rms / dataR.length);

            if (rms > threshold) {
                passed = true;
            }
        } else if (type.equals("StdDev")) {
            double sumR = 0.0;
            double x;
            for (int i = 0; i < dataR.length; ++i) {
                sumR += dataR[i];
            }
            sumR /= dataR.length;
            double sigma = 0.0;
            for (int i = 0; i < dataR.length; ++i) {
                x = dataR[i] - sumR;
                sigma += x * x;
            }
            if (complex) {
                double sumI = 0.0;
                for (int i = 0; i < dataR.length; ++i) {
                    sumI += dataI[i];
                }
                sumI /= dataR.length;
                for (int i = 0; i < dataR.length; ++i) {
                    x = dataI[i] - sumI;
                    sigma += x * x;
                }
            }

            sigma = Math.sqrt(sigma / dataR.length);

            if (sigma > threshold) {
                passed = true;
            }
        } else if (type.equals("ABS")) {
            if (!complex) {
                for (int i = 0; i < dataR.length; ++i) {
                    if ((dataR[i] > threshold) || (dataR[i] < -threshold)) {
                        passed = true;
                        passedValue = dataR[i];
                        break;
                    }
                }
            } else {
                double threshold2 = threshold * threshold;
                for (int i = 0; i < dataR.length; ++i) {
                    if (dataR[i] * dataR[i] + dataI[i] * dataI[i] > threshold2) {
                        passed = true;
                        passedValue = dataI[i];
                        break;
                    }
                }

            }
        }

        if (passed) {
            outputAtNode(1, input);
        } else {
            outputAtNode(0, input);
        }

    }

    /**
     * Initialses information specific to SigThresh.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setResizableInputs(false);
        setResizableOutputs(false);
        allowZeroOutputNodes();

        threshold = 1.0;
        type = "Amplitude";
    }

    public void setGUIInformation() {
        addGUILine("Choose Threshold Type $title type Choice Amplitude Mean ABS StdDev RMS");
        addGUILine("Threshold Value $title threshold Scroller 0 10000 1000");
    }

    /**
     * Reset's SigThresh
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves SigThresh's parameters.
     */
    public void saveParameters() {
        saveParameter("threshold", threshold);
        saveParameter("type", type);
    }

    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("type")) {
            type = value;
        }
        if (name.equals("threshold")) {
            threshold = strToDouble(value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to SigThresh, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType";
    }

    /**
     * @return a string containing the names of the types output from SigThresh, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Passes the data to the second output if the signal crosses\n" +
                "the given threshold or to the first output node if not";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "SigThresh.html";
    }
}

















