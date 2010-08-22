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


import triana.types.OldUnit;
import triana.types.SampleSet;
import triana.types.util.SigAnalWindows;


/**
 * An FIR_LowPass unit to apply an FIR low-pass filter smoothed by a windowing function chosen by the user. The order
 * (number of points in the filter) and cutoff frequency can also be chosen by the user. The unit maintains continuity
 * across successive input data sets if the user requests it. The computation starts off as if the first data set had
 * been preceded by a sequence of zeros.
 *
 * @author P Boyle
 * @author B F Schutz
 * @version 2.0 23 February 2002
 */
public class FIR_LowPass extends OldUnit {

    String filterType;
    static int defaultOrder = 9;
    double[] coefficients;
    int order;
    double freq, freqToNyquist;
    static double pi = 3.14159265358979323846;
    String filterName = "none";
    boolean continuity = true;
    double sum = 0.0;

    boolean firstData = true;
    double[] previousData = {0};

    public void process() throws Exception {

        SampleSet wave = (SampleSet) getInputNode(0);
        int length = wave.size();
        freqToNyquist = freq / wave.getSamplingRate() * 2;

        double out[] = new double[length];
        double in[] = new double[order + length];
        System.arraycopy(wave.getData(), 0, in, order, length);

        if (continuity) {
            if (firstData) {
                firstData = false;
            } else {
                System.arraycopy(previousData, 0, in, 0, order);
            }
        }

        String currentFilterName = filterType + "(" + String.valueOf(freqToNyquist) + ")(" + String.valueOf(order)
                + ")";
        if (!currentFilterName.equals(filterName)) {
            filterName = currentFilterName;
            setCoefficients();
        }


        int j, k;
        double av;
        for (j = order; j < order + length; ++j) {
            av = 0.0;
            for (k = 0; k < order; ++k) {
                av += coefficients[k] * in[j - k];
            }
            out[j - order] = av;
        }

        wave.setData(out);
        if (order > 0) {
            System.arraycopy(in, length - order, previousData, 0, order);
        }

        output(wave);


    }

    private void setCoefficients() {
        int j;
        double omega = pi * freqToNyquist;
        double arg = (order - 1.0) / 2.0;
        System.out.println("arg= " + (int) arg);

        for (j = 0; j < order; j++) {
            if (j == arg) {
                coefficients[j] = omega / pi;
            } else {
                coefficients[j] = Math.sin(omega * (j - arg)) / (pi * (j - arg));
            }
        }

        if (!filterType.equals("(none)")) {
            SigAnalWindows.doFullWindow(filterType, coefficients, 0, order - 1);
        }

        sum = 0.0;
        for (j = 0; j < order; j++) {
            sum += coefficients[j];
        }

        for (j = 0; j < order; j++) {
            coefficients[j] *= (1.0 / sum);
        }
    }


    /*
    * Initialses information specific to FIR_LowPass.
    */

    public void init() {
        int i;
        super.init();

        order = defaultOrder;
        coefficients = new double[order];
        previousData = new double[order];
        filterType = "Rectangle";
        freq = 250;

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
        addGUILine("Cutoff Frequency (Hz) $title freq Scroller 0.0 4000 250");
        addGUILine("Filter Order (number of points) $title order IntScroller 3 255 9");
        addGUILine("Window $title filterType Choice " + SigAnalWindows.listOfWindows());
        addGUILine(
                "Check here if you want filter continued across successive input sets $title continuity Checkbox true");
    }

    /**
     * Reset's FIR_LowPass
     */
    public void reset() {
        firstData = true;
        super.reset();
    }


    /**
     * Saves FIR_LowPass's parameters.
     */
    public void saveParameters() {
        saveParameter("filterType", filterType);
        saveParameter("order", order);
        saveParameter("freq", freq);
        saveParameter("continuity", continuity);
    }

    /**
     * Used to set each of FIR_LowPass's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("filterType")) {
            filterType = value;
        }
        if (name.equals("order")) {
            order = strToInt(value);
            previousData = new double[order];
            coefficients = new double[order];
        }
        if (name.equals("freq")) {
            freq = strToDouble(value);
        }
        if (name.equals("continuity")) {
            continuity = strToBoolean(value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to FIR_LowPass, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "SampleSet";
    }

    /**
     * @return a string containing the names of the types output from FIR_LowPass, each separated by a white space.
     */
    public String outputTypes() {
        return "SampleSet";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Low Pass FIR time-domain filter smoothed with chosen window function";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "FIR_LowPass.html";
    }
}




