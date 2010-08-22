package audio.processing.tools;


import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * A Reverse Effect which reverses the input wave.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see Reverse
 */

public class Reverse extends Unit {

    ReverseEffect reverse = null;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        // Input and Outputs created
        MultipleAudio input = (MultipleAudio) getInputAtNode(0);
        MultipleAudio output = new MultipleAudio(input.getChannels());
        Object in;

        // Instantiates new reverseEffect object
        if (reverse == null) {
            reverse = new ReverseEffect();
        }

        // For each channel
        for (int i = 0; i < input.getChannels(); ++i) {

            in = input.getChannel(i);
            short[] out;

            if (in instanceof short[]) { // 16bit data

                short[] temp = (short[]) in;
                out = reverse.process(temp);

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
        setMaximumInputNodes(Integer.MAX_VALUE); // Resizeable number of inputs

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE); // Resizeable number of outputs

        // Initialise parameter update policy
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("Reverse.html");

        // Define initial value and type of parameters

        // Initialise GUI builder interface
        String guilines = "";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Fader (e.g. close open files)
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {

    }

    /**
     * @return an array of the input types for Fader
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types for Fader
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

}



