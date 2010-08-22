package audio.processing.tools;


import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * A Fader Unit which allows user to adjust the level of the incoming signal my multiplying each sample in the signal by
 * a float value determined in the GUI. This base class extends Unit.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see FaderEffect
 */

public class Fader extends Unit {

    // parameter data type definitions
    private double volumeInDB;
    FaderEffect fader = null;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        // Input and Outputs created
        MultipleAudio input = (MultipleAudio) getInputAtNode(0);
        MultipleAudio output = new MultipleAudio(input.getChannels());
        Object in;

        // Instantiates new faderEffect object
        if (fader == null) {
            fader = new FaderEffect(volumeInDB);
        }

        // For each channel
        for (int i = 0; i < input.getChannels(); ++i) {

            in = input.getChannel(i);
            short[] out;

            if (in instanceof short[]) { // 16bit data

                short[] temp = (short[]) in;
                out = fader.process(temp);

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
        setHelpFileLocation("Fader.html");

        // Define initial value and type of parameters
        defineParameter("volumeInDB", "0", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Adjust volume level in Decibels $title volumeInDB Scroller -64 24 0 false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        volumeInDB = new Double((String) getParameter("volumeInDB")).doubleValue();
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
        // Code to update local variables
        //    System.out.println("Fader.parameterUpdate" + paramname + " " + value.toString());
        if (paramname.equals("volumeInDB")) {
            volumeInDB = new Double((String) value).doubleValue();
            if (fader != null) {
                fader.setVolume(volumeInDB);
            }
        }
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



