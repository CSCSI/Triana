package signalproc.time;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.util.Str;

/**
 * A PackData unit to repackage a data stream into SampleSets of a specified length with an optional overlap between
 * successive sets. Since the input and output are generally asynchronous, it only makes sense to use this Unit in
 * Continuous mode.
 *
 * @author Bernard Schutz
 * @version 1.1 25 July 2000
 */
public class PackData extends Unit {

    int samples = 1024;
    int overlap = 0;
    int startOfNewSet = 0;
    int oldElementsRemaining, newElementsVacant, oldCopyStart, newCopyStart;
    SampleSet input, output;
    double[] newdata, prevdata;
    int length, copylength, overlapStart;
    double inputAcqTime, outputAcqTime, nextAcqTime, samplingRate;
    boolean firstTimeCalled = true;


    /**
     * ********************************************* ** USER CODE of PackData goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        overlapStart = samples - overlap;

        input = (SampleSet) getInputAtNode(0);
        length = input.size();
        oldElementsRemaining = length;
        inputAcqTime = input.getAcquisitionTime();
        oldCopyStart = 0;
        if (firstTimeCalled) {
            samplingRate = input.getSamplingRate();
            firstTimeCalled = false;
            newdata = new double[samples];
            newElementsVacant = samples;
            nextAcqTime = inputAcqTime;
            copylength = Math.min(samples, length);
            newCopyStart = 0;
        } else {
            copylength = Math.min(length, newElementsVacant);
        }
        if (samplingRate != input.getSamplingRate()) {
            new ErrorDialog(null,
                    "Error! The sampling rate of the input data sets has changed during the sequence. Repackaging cannot continue.");
            //stop();
            return;
        }

        while (oldElementsRemaining > 0) {
            System.arraycopy(input.getData(), oldCopyStart, newdata, newCopyStart, copylength);
            oldElementsRemaining -= copylength;
            oldCopyStart = length - oldElementsRemaining;
            newElementsVacant -= copylength;
            newCopyStart = samples - newElementsVacant;
            if (newElementsVacant == 0) {
                outputAcqTime = nextAcqTime;
                nextAcqTime = inputAcqTime + newCopyStart / samplingRate;
                output(new SampleSet(samplingRate, newdata, outputAcqTime));
                prevdata = newdata;
                newdata = new double[samples];
                if (overlap > 0) {
                    System.arraycopy(prevdata, overlapStart, newdata, 0, overlap);
                }
                newElementsVacant = overlapStart;
                newCopyStart = samples - overlapStart;
            }
            copylength = Math.min(oldElementsRemaining, newElementsVacant);
        }
    }


    /**
     * Initialses information specific to PackData.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Number of samples in new data sets $title samples IntScroller 0 8192 1024\n";
        guilines += "Overlapping samples with next data set $title overlap IntScroller 0 2048 0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Number of samples in new data sets $title samples IntScroller 0 8192 1024");
//        addGUILine("Overlapping samples with next data set $title overlap IntScroller 0 2048 0");
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
//     * Saves PackData's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("samples", samples);
//        saveParameter("overlap", overlap);
//    }

    /**
     * Used to set each of PackData's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("samples")) {
            samples = Str.strToInt((String) value);
        }
        if (name.equals("overlap")) {
            overlap = Str.strToInt((String) value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to PackData, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "SampleSet";
//    }
//
//    /**
//     * @return a string containing the names of the types output from PackData, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "SampleSet";
//    }
//
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
        return "Readjust lengths of  SampleSets and allow overlaps";
    }

    /**
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "PackData.html";
    }
}




