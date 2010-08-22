package math.functions;

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
 * A SumElements unit to compute sum or average of the elements of any data set.
 *
 * @author B F Schutz
 * @version 1.1 28 Feb 2001
 */
public class SumElements extends OldUnit {

    int dv = 0;
    String type = "Sum";


    /**
     * ********************************************* ** USER CODE of SumElements goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType input = (GraphType) getInputAtNode(0);

        FlatArray flatR, flatI;
        double[] dataR, dataI;
        int k, len;
        double answerR = Double.NaN;
        double answerI = Double.NaN;
        boolean complex = input.isDependentComplex(dv);

        if (input.isArithmeticArray(dv)) {
            if (complex) {
                flatR = new FlatArray(input.getDataArrayReal(dv));
                flatI = new FlatArray(input.getDataArrayImag(dv));
                dataR = (double[]) flatR.getFlatArray();
                dataI = (double[]) flatI.getFlatArray();
                len = dataR.length;
                answerR = 0;
                answerI = 0;
                for (k = 0; k < len; k++) {
                    answerR += dataR[k];
                    answerI += dataI[k];
                }
                if (type.equals("Average")) {
                    answerR /= len;
                    answerI /= len;
                }
            } else {
                flatR = new FlatArray(input.getDataArrayReal(dv));
                dataR = (double[]) flatR.getFlatArray();
                len = dataR.length;
                answerR = 0;
                for (k = 0; k < len; k++) {
                    answerR += dataR[k];
                }
                if (type.equals("Average")) {
                    answerR /= len;
                }
            }
            if (complex) {
                output(new Const(answerR, answerI));
            } else {
                output(new Const(answerR));
            }

        }


    }


    /**
     * Initialses information specific to SumElements.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setRequireDoubleInputs(false);
        setCanProcessDoubleArrays(true);

        setResizableInputs(false);
        setResizableOutputs(true);
        setDefaultInputNodes(1);
        setDefaultOutputNodes(1);
        setMinimumInputNodes(1);
        setMinimumOutputNodes(1);

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine(
                "Which dependent variable do you want to compute the sum of squares of? $title dv IntScroller 0 5 0");
        addGUILine("Choose value to be computed: $title type Choice Sum Average");
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
     * Saves SumElements's parameters.
     */
    public void saveParameters() {
        saveParameter("dv", dv);
        saveParameter("type", type);
    }


    /**
     * Used to set each of SumElements's parameters.
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
     * @return a string containing the names of the types allowed to be input to SumElements, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType";
    }

    /**
     * @return a string containing the names of the types output from SumElements, each separated by a white space.
     */
    public String outputTypes() {
        return "Const";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Compute sum or average of the elements of the data";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "SumElements.html";
    }
}




