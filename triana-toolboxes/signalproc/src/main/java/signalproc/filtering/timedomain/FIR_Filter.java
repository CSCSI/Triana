package signalproc.filtering.timedomain;

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


import java.util.StringTokenizer;

import triana.types.OldUnit;
import triana.types.VectorType;
import triana.types.util.Str;
import triana.types.util.StringVector;

/**
 * An FIR_Filter unit to implement an FIR filter in the time domain, ie for input data {x(k)}, the filter coefficients
 * {a(k)} lead to output {y{k}):<\p><p> y(n) = a(0)x(n) + a(1)x(n-1) + a(2)x(n-2) + ... <\p><p> The user gives an
 * arbitrary number of coefficients and the unit applies them to the input data. The unit maintains continuity across
 * successive input data sets if the user requests it. The computation starts off as if the first data set had been
 * preceded by a sequence of zeros.
 *
 * @author B F Schutz
 * @version 1.0 21 Feb 2002
 */
public class FIR_Filter extends OldUnit {

    String Coeffs = "0.5 0.5";
    boolean continuity = true;

    boolean firstData = true;
    double[] Coefficients;
    double[] previousData = {0};
    int filterSize = 0;


    /**
     * ********************************************* ** USER CODE of FIR_Filter goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        VectorType wave = (VectorType) getInputNode(0);

        int length = wave.size();
        double out[] = new double[length];
        double in[] = new double[filterSize + length];
        System.arraycopy(wave.getData(), 0, in, filterSize, length);

        if (continuity) {
            if (firstData) {
                firstData = false;
            } else {
                System.arraycopy(previousData, 0, in, 0, filterSize);
            }
        }

        double av;
        int i, j, k, m;
        for (i = filterSize, m = 0; i < filterSize + length; ++i, m++) {
            av = 0.0;
            for (j = 0, k = i; (j < filterSize) && (k >= 0); ++j, --k) {
                av += in[k] * Coefficients[j];
            }
            out[m] = av;
        }

        wave.setData(out);
        if (filterSize > 0) {
            System.arraycopy(in, length - filterSize, previousData, 0, filterSize);
        }

        output(wave);
    }


    private void parseCoeffs() {

        StringTokenizer st = new StringTokenizer(Coeffs);
        StringVector sv = new StringVector();

        while (st.hasMoreTokens()) {
            sv.addElement(st.nextToken());
        }
        filterSize = sv.size();

        Coefficients = new double[filterSize];
        previousData = new double[filterSize];

        for (int i = 0; i < filterSize; ++i) {
            Coefficients[i] = Str.strToDouble(sv.at(i));
        }
    }


    /**
     * Initialses information specific to FIR_Filter.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setRequireDoubleInputs(false);
        setCanProcessDoubleArrays(false);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        firstData = true;
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine(
                "Enter the coefficients of the FIR filter below (any number of them, separated by spaces)  $title Coeffs TextField 0.5 0.5");
        addGUILine(
                "Check here if you want filter continued across successive input sets $title continuity Checkbox true");
    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        firstData = true;
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
     * Saves FIR_Filter's parameters.
     */
    public void saveParameters() {
        saveParameter("Coeffs", Coeffs);
        saveParameter("continuity", continuity);
    }


    /**
     * Used to set each of FIR_Filter's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("Coeffs")) {
            Coeffs = value;
            parseCoeffs();
        }
        if (name.equals("continuity")) {
            continuity = strToBoolean(value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to FIR_Filter, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "VectorType";
    }

    /**
     * @return a string containing the names of the types output from FIR_Filter, each separated by a white space.
     */
    public String outputTypes() {
        return "VectorType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Make FIR filter out of coeffs {a(k)}: y(n) = a(0)x(n) + a(1)x(n-1) + ...";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "FIR_Filter.html";
    }
}




