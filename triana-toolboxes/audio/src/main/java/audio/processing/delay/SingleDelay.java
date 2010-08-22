package audio.processing.delay;


import javax.sound.sampled.AudioFormat;
import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * A Single-delay Effect which allows user to add a delayed signal onto the original by creating a Feedforward Comb
 * Filter. The user can set the attenuate the level of the delayed signal and also adjust the delay time by adjusting
 * the settings using the GUI. This base class extends Unit. SingleDelayEffect exends AudioEffect 16bit to allow for
 * chunked data.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see SingleDelayEffect
 * @see triana.audio.AudioEffect16bit
 */

public class SingleDelay extends Unit {

    // parameter data type definitions
    private float delayInMs; // In milliseconds
    private int feedback;
    private String filterType;
    private boolean chunked;
    float sampleRate;
    SingleDelayEffect single = null;

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

        // This line converts the delay in ms to number of samples and
        // calls it delayOffset
        int delayOffset = ((int) (delayInMs * sampleRate) / 1000);

        if (single == null) {
            single = new SingleDelayEffect(delayOffset, feedback, filterType, chunked, delayInMs, sampleRate);
        }

        // For each channel
        for (int i = 0; i < input.getChannels(); ++i) {

            in = input.getChannel(i);
            short[] out;

            if (in instanceof short[]) { // 16 bit data

                short[] temp = (short[]) in;
                out = single.process(temp);
                output.setChannel(i, out, input.getChannelFormat(i));
            }// Close if statement
        } // Close first statement

        output(output);
    } // Close process () method


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
        setHelpFileLocation("SingleDelay.html");

        // Define initial value and type of parameters
        defineParameter("delayInMs", "500", USER_ACCESSIBLE);
        defineParameter("feedback", "40", USER_ACCESSIBLE);
        defineParameter("filterType", "Standard Feedforward Filter", USER_ACCESSIBLE);
        defineParameter("chunked", "false", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Delay In Milliseconds $title delayInMs Scroller 0 1500 500 false\n";
        guilines += "Feedback Level $title feedback IntScroller 0 100 40 false\n";
        guilines += "Delay Filter Type $title filterType Choice [Standard Feedforward Filter] [Delayed Line Only]\n";
        guilines += "chunked $title chunked Checkbox false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        delayInMs = new Float((String) getParameter("delayInMs")).floatValue();
        feedback = new Integer((String) getParameter("feedback")).intValue();
        filterType = (String) getParameter("filterType");
        chunked = new Boolean((String) getParameter("chunked")).booleanValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up SingleDelay (e.g. close open files)
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("delayInMs")) {
            delayInMs = new Float((String) value).floatValue();
            if (single != null) {
                single.setDelayOffset(delayInMs);
            }
        }

        if (paramname.equals("feedback")) {
            feedback = new Integer((String) value).intValue();
            if (single != null) {
                single.setFeedback(feedback);
            }
        }

        if (paramname.equals("filterType")) {
            filterType = (String) value;
            if (single != null) {
                single.setFilterType(filterType);
            }
        }

        if (paramname.equals("chunked")) {
            chunked = new Boolean((String) value).booleanValue();
        }
        {
            if (single != null) {
                single.setChunked(chunked);
            }
        }
    }

    /**
     * @return an array of the input types for SingleDelay
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types for SingleDelay
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

}



