package audio.processing.distortion;


import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * A Fuzz-box distortion Effect which allows user to apply a distortion onto the audio by lowering the 'acceptable'
 * threshold level, forcing the signal to clip. The user can compensate for the loss in volume caused by limiting the
 * threshhold by adjusting the gain level using the GUI. This base class extends Unit.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see FuzzboxEffect
 */

public class FuzzBox extends Unit {

    // parameter data type definitions
    private float gain;
    private int threshold;
    FuzzboxEffect fuzz = null;

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {

        MultipleAudio input = (MultipleAudio) getInputAtNode(0);
        MultipleAudio output = new MultipleAudio(input.getChannels());
        Object in;

        // Instantiates new FuzzboxEffect object
        if (fuzz == null) {
            fuzz = new FuzzboxEffect(gain, threshold);
        }

        // For each channel
        for (int i = 0; i < input.getChannels(); ++i) {

            in = input.getChannel(i);
            short[] out;

            // If 16bit data
            if (in instanceof short[]) {

                short[] temp = (short[]) in;
                out = fuzz.process(temp);
                output.setChannel(i, out, input.getChannelFormat(i));

            } // Close if statement
        } // Close first for loop
        output(output);
    } // Close the process() method


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
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

        // Initialise parameter update policy
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("FuzzyBox.html");

        // Define initial value and type of parameters
        defineParameter("gain", "1", USER_ACCESSIBLE);
        defineParameter("threshold", "5000", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Gain Compensation $title gain Scroller 0 10 1 false\n";
        guilines += "Threshold (Lower = More Distortion) $title threshold IntScroller 0 32767 5000 false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        gain = new Float((String) getParameter("gain")).floatValue();
        threshold = new Integer((String) getParameter("threshold")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up FuzzyBox (e.g. close open files)
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("gain")) {
            gain = new Float((String) value).floatValue();
            if (fuzz != null) {
                fuzz.setGain(gain);
            }
        }

        if (paramname.equals("threshold")) {
            threshold = new Integer((String) value).intValue();
            if (fuzz != null) {
                fuzz.setThreshold(threshold);
            }
        }
    }

    /**
     * @return an array of the input types for FuzzyBox
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types for FuzzyBox
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

}



