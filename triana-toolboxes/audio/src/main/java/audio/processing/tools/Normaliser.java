package audio.processing.tools;

import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * A Normalisation unit which allows the user to control the gain of the audio file's selection to the maximum level
 * without clipping (digital distortion). The normalisation factor should be entered as a percentage from the maximum
 * amplitude that the audio file should be normalised to.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 */

public class Normaliser extends Unit {

    // parameter data type definitions
    private float normal;
    NormaliserEffect normaliser = null;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        // Input and Outputs created
        MultipleAudio input = (MultipleAudio) getInputAtNode(0);
        MultipleAudio output = new MultipleAudio(input.getChannels());
        Object in;

        // Instantiates new faderEffect object
        if (normaliser == null) {
            normaliser = new NormaliserEffect(normal);
        }

        // For each channel
        for (int i = 0; i < input.getChannels(); ++i) {

            in = input.getChannel(i);
            short[] out;

            if (in instanceof short[]) { // 16bit data

                short[] temp = (short[]) in;
                out = normaliser.process(temp);

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
        setPopUpDescription("Normalise (your wave to ");
        setHelpFileLocation("Normaliser.html");

        // Define initial value and type of parameters
        defineParameter("normal", "95", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Enter amount of normalisation $title normal Scroller 0 100 95 false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        normal = new Float((String) getParameter("normal")).floatValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Normaliser (e.g. close open files)
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("normal")) {
            normal = new Float((String) value).floatValue();
            if (normaliser != null) {
                normaliser.setNormalLevel(normal);
            }
        }
    }

    /**
     * @return an array of the input types for Normaliser
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types for Normaliser
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

}



