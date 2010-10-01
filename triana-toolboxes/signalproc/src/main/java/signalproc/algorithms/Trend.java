package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSampleSet;
import triana.types.Const;
import triana.types.SampleSet;
import triana.types.util.Str;

/**
 * A Trend unit to accumulate single numbers into a SampleSet
 *
 * @author B F Schutz
 * @version 2.0 26 Feb 2001
 */
public class Trend extends Unit {

    String name = "Trend analysis";
    double sampleTime = 1;
    int length = 100;
    int count = 0;
    double[] dataOut = new double[length];
    double[] dataOutImag = new double[length];

    String acqString = "Acquistion time of first sample of data from which output is built = ";
    String labelLabel = "Input label = ";

    /**
     * ********************************************* ** USER CODE of Trend goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Const input = (Const) getInputAtNode(0);
        int timeStringIndex = -1;
        int k, end, label1, label2;
        double time = 0;
        ;
        boolean hasTime = false;
        String labelString = "";

        double dataIn = input.getReal();
        double dataInImag = 0;
        boolean complex = input.isComplex();
        if (complex) {
            dataInImag = input.getImag();
        }

        if (count < length) {
            for (k = count; k > 0; k--) {
                dataOut[k] = dataOut[k - 1];
            }
            dataOut[0] = dataIn;
            if (complex) {
                for (k = count; k > 0; k--) {
                    dataOutImag[k] = dataOutImag[k - 1];
                }
                dataOutImag[0] = dataInImag;
            }
        } else {
            for (k = length - 1; k > 0; k--) {
                dataOut[k] = dataOut[k - 1];
            }
            dataOut[0] = dataIn;
            if (complex) {
                for (k = length - 1; k > 0; k--) {
                    dataOutImag[k] = dataOutImag[k - 1];
                }
                dataOutImag[0] = dataInImag;
            }
        }
        count++;
        if (!complex) {
            SampleSet outputSet = new SampleSet(1 / sampleTime, dataOut, time);
            outputSet.setDependentLabels(0, name + labelString);
            output(outputSet);
        } else {
            ComplexSampleSet outputSet = new ComplexSampleSet(1 / sampleTime, dataOut, dataOutImag, time);
            outputSet.setDependentLabels(0, name + labelString);
            output(outputSet);
        }

    }


    /**
     * Initialses information specific to Trend.
     */
    public void init() {
        super.init();
        count = 0;
//        setUseGUIBuilder(true);
//
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Give the name of the data  $title name TextField Trend analysis\n";
        guilines += "Time between samples (s) $title sampleTime Scroller 0 100 1\n";
        guilines += "Number of samples to accumulate $title length IntScroller 0 1000 100\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Give the name of the data  $title name TextField Trend analysis");
//        addGUILine("Time between samples (s) $title sampleTime Scroller 0 100 1");
//        addGUILine("Number of samples to accumulate $title length IntScroller 0 1000 100");
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
//     * Saves Trend's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("name", name);
//        saveParameter("sampleTime", sampleTime);
//        saveParameter("length", length);
//    }

    /**
     * Used to set each of Trend's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("name")) {
            name = (String) value;
        }
        if (name.equals("sampleTime")) {
            sampleTime = Str.strToDouble((String) value);
        }
        if (name.equals("length")) {
            length = Str.strToInt((String) value);
            dataOut = new double[length];
            dataOutImag = new double[length];
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Trend, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "Const";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Trend, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "SampleSet ComplexSampleSet";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.Const"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.SampleSet", "triana.types.ComplexSampleSet"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Accumulates input numbers into a sequence";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Trend.html";
    }
}




