package signalproc.time;

import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.util.Str;


/**
 * Returns down-sampled data set with or without low-pass filtering
 *
 * @author ian
 * @version 2.0 01 Mar 2001
 */
public class Decimate extends Unit {

    int decimationFactor = 1;
    boolean ApplyFilter = false;
    double lcoeffs[];

    /**
     * ********************************************* ** USER CODE of Decimate goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        SampleSet input = (SampleSet) getInputAtNode(0);

        if ((lcoeffs == null) || (lcoeffs.length != (decimationFactor * 2))) {
            lcoeffs = new double[decimationFactor * 2];
        }

        double dataIn[] = input.getData();
        double dataOut[] = new double[dataIn.length / decimationFactor];

        decimate(dataIn, dataOut, ApplyFilter,
                dataIn.length, decimationFactor);
        output(new SampleSet(input.samplingFrequency() / decimationFactor, dataOut));
    }

    public int decimate(double dataIn[], double dataOut[], boolean filter, int srate, int dec) {
        double av;
        int fnSize;
        int i, j, k;
        double fnSizeRecpip;

        /* printf("filtering....");  */

        if (filter) {
            fnSize = dec * 2;
        } else {
            fnSize = 1;
        }

        fnSizeRecpip = 1.0 / (double) fnSize;

        k = 0;

        for (i = 0; i < fnSize; ++i) {
            lcoeffs[i] = 1;
        }    /*  can tune this if we want to  */

        if (!filter) { /* No low pass filter required so just decimate */
            for (i = 0; i < srate; i += dec) {
                dataOut[k] = dataIn[i];
                ++k;
            }
            return k;
        }

        /* does low pas filtering and decimation  */
        k = 0;

        for (i = 0; i < srate - fnSize; i += dec) {
            av = 0.0;
            for (j = 0; j < fnSize; ++j) {
                av += dataIn[i + j] * lcoeffs[j];
            }
            dataOut[k] = av * fnSizeRecpip;
            ++k;
        }

        for (i = srate - fnSize; i < srate; i += dec) {
            av = 0.0;
            fnSize = srate - i;
            fnSizeRecpip = 1.0 / (double) fnSize;

            for (j = 0; j < fnSize; ++j) {
                av += dataIn[i + j] * lcoeffs[j];
            }
            dataOut[k] = av * fnSizeRecpip;
            ++k;
        }
        return k;    /* number of new elements in this part of dataOut */
    }

    /**
     * Initialses information specific to Decimate.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

//        setUseGUIBuilder(true);
//
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);
        String guilines = "";
        guilines += "Enter the decimation factor (e.g. 2 = half sample rate) $title decimationFactor IntScroller 1 100 1\n";
        guilines += "Apply Low-Pass Averaging Filter ? $title ApplyFilter Checkbox false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine(
//                "Enter the decimation factor (e.g. 2 = half sample rate) $title decimationFactor IntScroller 1 100 1");
//        addGUILine("Apply Low-Pass Averaging Filter ? $title ApplyFilter Checkbox false");
//    }

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
//    public void starting() {
//        super.starting();
//    }
//
//    /**
//     * Saves Decimate's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("decimationFactor", decimationFactor);
//        saveParameter("ApplyFilter", ApplyFilter);
//    }

    /**
     * Used to set each of Decimate's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("decimationFactor")) {
            decimationFactor = Str.strToInt((String) value);
        }
        if (name.equals("ApplyFilter")) {
            ApplyFilter = Str.strToBoolean((String) value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Decimate, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "SampleSet";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Decimate, each separated by a white space.
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
        return "Returns down-sampled data set with or without low-pass filtering";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Decimate.html";
    }
}




