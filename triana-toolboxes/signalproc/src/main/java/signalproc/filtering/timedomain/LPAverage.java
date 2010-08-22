package signalproc.filtering.timedomain;

/*
 * Copyright (c) 1995 - 1998 University of Wales College of Cardiff
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
 * A LPAverage unit to implement a convolution in the time domain, working forward from the first data element, ie for
 * input data {x(k)}, the filter coefficients {a(k)} lead to output {y{k}):<\p><p> y(n) = a(0)x(n) + a(1)x(n+1) +
 * a(2)x(n+2) + ... <\p><p> The user gives an arbitrary number of coefficients and the unit applies them to the input
 * data.
 *
 * @author Ian Taylor, Bernard Schutz
 * @version 1.1 21 February 2002
 */
public class LPAverage extends OldUnit {

    String Coeffs;


    /**
     * ********************************************* ** USER CODE of LPAverage goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        int i, j;
        VectorType wave = (VectorType) getInputNode(0);

        StringTokenizer st = new StringTokenizer(Coeffs);
        StringVector sv = new StringVector();

        while (st.hasMoreTokens()) {
            sv.addElement(st.nextToken());
        }

        double[] coef = new double[sv.size()];
        for (i = 0; i < sv.size(); ++i) {
            coef[i] = Str.strToDouble(sv.at(i));
        }

        double in[] = wave.getData();
        int length = wave.size();
        double out[] = new double[length];

        double av;
        int fnSize = sv.size();

        for (i = 0; i < length; ++i) {
            av = 0.0;
            if ((fnSize + i) == (length - 1)) {
                --fnSize;
            }
            for (j = 0; j < fnSize; ++j) {
                av += in[i + j] * coef[j];
            }
            out[i] = av;
        }

        wave.setData(out);

        output(wave);
    }


    /**
     * Initialses information specific to LPAverage.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
    public void setGUIInformation() {
        addGUILine(
                "Enter the coefficients for the convolution below (any number of them, separated by spaces)  $title Coeffs TextField 1 1");
    }

    /**
     * Reset's LPAverage
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves LPAverage's parameters.
     */
    public void saveParameters() {
        saveParameter("Coeffs", Coeffs);
    }

    /**
     * Used to set each of LPAverage's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("Coeffs")) {
            Coeffs = value;
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to LPAverage, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "VectorType";
    }

    /**
     * @return a string containing the names of the types output from LPAverage, each separated by a white space.
     */
    public String outputTypes() {
        return "VectorType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Time domain forward convolution: give coefficients for convolution";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "LPAverage.html";
    }
}













