package audio.processing.delay;


import javax.sound.sampled.AudioFormat;
import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * Comb-filter delay Unit which allows user to add multiple delayed signals onto the original by creating a Comb Filter
 * with both a feedforward and a feedback loop. The user can set the level of attenuation of the delayed signal and also
 * adjust the delay time by adjusting the settings using the GUI.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2921 $
 * @see CombFilterEffect
 */

public class CombFilter extends Unit {

    // parameter data type definitions
    private float delayInMs; // In milliseconds
    private int feedback;
    private String filterType;
    private boolean chunked;
    float sampleRate;
    CombFilterEffect comb = null;
    int oldSize = 0;
    int tempLength = 0;
    int noOfChannels;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        MultipleAudio input = (MultipleAudio) getInputAtNode(0);
        MultipleAudio output = new MultipleAudio(input.getChannels());
        Object in;

        AudioFormat af = input.getAudioFormat();
        float sampleRate = af.getSampleRate();
        noOfChannels = af.getChannels();
        System.out.println("noOfChannels = " + noOfChannels);

        // This line converts the delay in ms to number of samples and calls it delayOffset

        double loopGain = (double) (20 * (Math.log(((double) feedback / 100)) / (Math.log(10.0))));
        System.out.println("loopGain = " + loopGain);
        double decay60dbTime = (double) ((60 / -loopGain) * delayInMs);
        System.out.println("decay60dbTime = " + decay60dbTime);
        int delayOffset = ((int) (decay60dbTime * sampleRate) / 1000);
        System.out.println("delayOffset = " + delayOffset);

        if (comb == null) {
            comb = new CombFilterEffect(feedback, filterType, chunked, delayInMs, sampleRate, noOfChannels);
        }

        // For each channel
        for (int i = 0; i < input.getChannels(); ++i) {

            in = input.getChannel(i);
            short[] out; // Creates a short array for output data

            // If 16bit data
            if (in instanceof short[]) {

                short[] temp = (short[]) in;
                tempLength = temp.length;

                if (chunked == true) {

                    if (tempLength == oldSize || oldSize == 0) {
                        out = comb.process(temp);
                        oldSize = temp.length;
                    } else {
                        short[] blankArray = new short[delayOffset];
                        for (int n = 0; n < delayOffset; ++n) {
                            blankArray[n] = 0;
                        }
                        out = comb.process(blankArray);
                        System.out.println("*********End of file reached here*******");
                    }
                } else {
                    out = comb.process(temp);
                }
                output.setChannel(i, out, input.getChannelFormat(i));
            }// Close if statement
        } // Close for loop

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
        setHelpFileLocation("CombDelay.html");

        // Define initial value and type of parameters
        defineParameter("delayInMs", "100", USER_ACCESSIBLE);
        defineParameter("feedback", "25", USER_ACCESSIBLE);
        defineParameter("filterType", "Standard Feedback Filter", USER_ACCESSIBLE);
        defineParameter("chunked", "false", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Delay In Milliseconds $title delayInMs Scroller 0 2000 100 false\n";
        guilines += "Feedback Level (%) $title feedback IntScroller 0 100 25 false\n";
        guilines += "Delay Filter Type $title filterType Choice [Standard Feedback Filter] [Feedback Loop Only]\n";
        guilines += "Chunked Data? $title chunked Checkbox false\n";
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
        // Insert code to clean-up CombFilter (e.g. close open files)
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("delayInMs")) {
            delayInMs = new Float((String) value).floatValue();
            if (comb != null) {
                comb.setDelayOffset(delayInMs);
                System.out.println("delayInMS - is it changing? -> " + delayInMs);
            }
        }

        if (paramname.equals("feedback")) {
            feedback = new Integer((String) value).intValue();
            if (comb != null) {
                comb.setFeedback(feedback);
            }
        }

        if (paramname.equals("filterType")) {
            filterType = (String) value;
            if (comb != null) {
                comb.setFilterType(filterType);
            }
        }

        if (paramname.equals("chunked")) {
            chunked = new Boolean((String) value).booleanValue();
        }
        {
            if (comb != null) {
                comb.setChunked(chunked);
            }
        }
    }

    /**
     * @return an array of the input types for CombFilter
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types for CombFilter
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }
}
