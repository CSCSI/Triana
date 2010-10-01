package audio.processing.tools;

import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;


/**
 * A HalfSampleRate unit to ..
 *
 * @author ian
 * @version 2.0 01 Sep 2000
 */
public class HalfSampleRate extends Unit {

    /**
     * ********************************************* ** USER CODE of HalfSampleRate goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        SampleSet input = (SampleSet) getInputAtNode(0);

        double dataIn[] = input.data;
        double dataOut[] = new double[dataIn.length];
        for (int i = 0; i < dataIn.length; ++i) {
            dataOut[i] = dataIn[i];
        }

        output(new SampleSet(input.samplingFrequency(), dataOut));
    }


    /**
     * Initialses information specific to HalfSampleRate.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);
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
     * Saves HalfSampleRate's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of HalfSampleRate's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return an array of the input types
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    /**
     * @return an array of the output types
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put HalfSampleRate's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "HalfSampleRate.html";
    }
}



