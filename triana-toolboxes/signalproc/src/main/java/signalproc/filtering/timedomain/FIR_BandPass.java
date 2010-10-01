package signalproc.filtering.timedomain;

import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.util.SigAnalWindows;
import triana.types.util.Str;


/**
 * An FIR_BandPass unit to apply an FIR band-pass filter smoothed by a windowing function chosen by the user. The order
 * (number of points in the filter) and lower and upper frequency limis on the band can also be chosen by the user. The
 * unit maintains continuity across successive input data sets if the user requests it. The computation starts off as if
 * the first data set had been preceded by a sequence of zeros.
 *
 * @author P Boyle
 * @author B F Schutz
 * @version 2.0 23 February 2002
 */
public class FIR_BandPass extends Unit {

    String filterType;
    static int defaultOrder = 9;
    double[] coefficients;
    int order;
    double freqLow, freqHigh, freqLowToNyquist, freqHighToNyquist;
    static double pi = 3.14159265358979323846;
    String filterName = "";
    boolean continuity = true;

    boolean firstData = true;
    double[] previousData = {0};

    public void process() throws Exception {

        SampleSet wave = (SampleSet) getInputAtNode(0);
        int length = wave.size();
        freqLowToNyquist = freqLow / wave.getSamplingRate() * 2;
        freqHighToNyquist = freqHigh / wave.getSamplingRate() * 2;

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

        String currentFilterName = filterType + "(" + String.valueOf(freqLowToNyquist) + ")("
                + String.valueOf(freqHighToNyquist) + ")(" + String.valueOf(order) + ")";
        if (!currentFilterName.equals(filterName)) {
            filterName = currentFilterName;
            setCoefficients();
        }

        double av;
        int i, j, k, m;
        for (i = order, m = 0; i < order + length; ++i, m++) {
            av = 0.0;
            for (j = 0, k = i; (j < order) && (k >= 0); ++j, --k) {
                av += in[k] * coefficients[j];
            }
            out[m] = av;
        }

        wave.setData(out);
        if (order > 0) {
            System.arraycopy(in, length - order, previousData, 0, order);
        }

        output(wave);
    }

    private void setCoefficients() {
        int j;
        double omegaLow = pi * freqLowToNyquist;
        double omegaHigh = pi * freqHighToNyquist;
        coefficients[0] = freqHighToNyquist - freqLowToNyquist;
        if (order > 1) {
            for (j = 1; j < order; j++) {
                coefficients[j] = (Math.sin(omegaHigh * j) - Math.sin(omegaLow * j)) / (pi * j);
            }
        }
        if (!filterType.equals("(none)")) {
            SigAnalWindows.doHalfWindow(filterType, coefficients, 0, order - 1, true);
        }
    }


    /*
    * Initialses information specific to FIR_BandPass.
    */

    public void init() {
        int i;
        super.init();

        order = defaultOrder;
        coefficients = new double[order];
        previousData = new double[order];
        filterType = "Rectangle";
        freqLow = 250;
        freqHigh = 500;

//        setUseGUIBuilder(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Lower Frequency (Hz) $title freqLow Scroller 0.0 4000 250\n";
        guilines += "Upper Frequency (Hz) $title freqHigh Scroller 0.0 4000 500\n";
        guilines += "Filter Order (number of points) $title order IntScroller 3 255 9\n";
        guilines += "Window $title filterType Choice " + SigAnalWindows.listOfWindows() + " \n";
        guilines += "Check here if you want filter continued across successive input sets $title continuity Checkbox true\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
//    public void setGUIInformation() {
//        addGUILine("Lower Frequency (Hz) $title freqLow Scroller 0.0 4000 250");
//        addGUILine("Upper Frequency (Hz) $title freqHigh Scroller 0.0 4000 500");
//        addGUILine("Filter Order (number of points) $title order IntScroller 3 255 9");
//        addGUILine("Window $title filterType Choice " + SigAnalWindows.listOfWindows());
//        addGUILine(
//                "Check here if you want filter continued across successive input sets $title continuity Checkbox true");
//    }

    /**
     * Resets FIR_BandPass
     */
    public void reset() {
        firstData = true;
        super.reset();
    }


    /**
     * Saves FIR_BandPass's parameters.
     */
//    public void saveParameters() {
//        saveParameter("filterType", filterType);
//        saveParameter("order", order);
//        saveParameter("freqLow", freqLow);
//        saveParameter("freqHigh", freqHigh);
//        saveParameter("continuity", continuity);
//    }

    /**
     * Used to set each of FIR_BandPass's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("filterType")) {
            filterType = (String) value;
        }
        if (name.equals("order")) {
            order = Str.strToInt((String) value);
            previousData = new double[order];
            coefficients = new double[order];
        }
        if (name.equals("freqLow")) {
            freqLow = Str.strToDouble((String) value);
        }
        if (name.equals("freqHigh")) {
            freqHigh = Str.strToDouble((String) value);
        }
        if (name.equals("continuity")) {
            continuity = Str.strToBoolean((String) value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to FIR_BandPass, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "SampleSet";
//    }
//
//    /**
//     * @return a string containing the names of the types output from FIR_BandPass, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "SampleSet";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Band Pass FIR time-domain filter smoothed with chosen window function";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "FIR_BandPass.html";
    }
}





