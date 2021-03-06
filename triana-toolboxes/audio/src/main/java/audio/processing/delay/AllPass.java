package audio.processing.delay;

import javax.sound.sampled.AudioFormat;
import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * Allpass-delay Unit which allows user to add a delayed signal onto the original by combing two Comb Filters. An
 * allpass filter is a building block of a Schroeder reverb device. The user can set the attenuation level of the
 * delayed signal and also adjust the delay time by adjusting the settings using the GUI. AllPass creates an
 * AllPassEffect object which extends AudioEffect16bit in the triana.audio package extends AudioEffect 16bit to allow
 * for chunked data.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see AllPassEffect
 */

public class AllPass extends Unit {

    // parameter data type definitions
    float delayInMs; // In milliseconds
    int feedback;
    boolean chunked;
    float sampleRate;
    AllPassEffect allpass = null;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        MultipleAudio input = (MultipleAudio) getInputAtNode(0);
        MultipleAudio output = new MultipleAudio(input.getChannels());
        Object in;

        AudioFormat af = input.getAudioFormat();
        int noOfChannels = af.getChannels();
        float sampleRate = af.getSampleRate();

        // This line converts the delay in ms to number of samples and calls it delayOffset
        int delayOffset = ((int) (delayInMs * sampleRate) / 1000);

        if (allpass == null) {
            allpass = new AllPassEffect(delayOffset, feedback, chunked, delayInMs, sampleRate);
        }

        //  For each channel
        for (int i = 0; i < input.getChannels(); ++i) {

            in = input.getChannel(i);
            short[] out; // Creates a short array for output data

            // Checks for 16bit data
            if (in instanceof short[]) {

                short[] temp = (short[]) in;
                out = allpass.process(temp);
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
        setHelpFileLocation("AllPass.html");

        // Define initial value and type of parameters
        defineParameter("delayInMs", "100", USER_ACCESSIBLE);
        defineParameter("feedback", "25", USER_ACCESSIBLE);
        defineParameter("chunked", "false", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Delay in Milliseconds $title delayInMs Scroller 0 1500 100 false\n";
        guilines += "Feedback level (%) $title feedback IntScroller 0 100 25 false\n";
        guilines += "Chunked Data? $title chunked Checkbox false\n";
        //System.out.println("guilines = " + guilines);
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        delayInMs = new Float((String) getParameter("delayInMs")).floatValue();
        feedback = new Integer((String) getParameter("feedback")).intValue();
        chunked = new Boolean((String) getParameter("chunked")).booleanValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up AllPassFilter (e.g. close open files)
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("delayInMs")) {
            delayInMs = new Float((String) value).floatValue();
            if (allpass != null) {
                allpass.setDelayOffset(delayInMs);
            }
        }

        if (paramname.equals("feedback")) {
            feedback = new Integer((String) value).intValue();
            if (allpass != null) {
                allpass.setFeedback(feedback);
            }
        }

        if (paramname.equals("chunked")) {
            chunked = new Boolean((String) value).booleanValue();
        }
        {
            if (allpass != null) {
                allpass.setChunked(chunked);
            }
        }
    }

    /**
     * @return an array of the input types for AllPassFilter
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types for AllPassFilter
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

}



