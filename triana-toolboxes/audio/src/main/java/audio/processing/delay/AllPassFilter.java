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
 * @version $Revision: 2921 $
 * @see AllPassFilterEffect
 */

public class AllPassFilter extends Unit {

    // parameter data type definitions
    float delayInMs; // In milliseconds
    int feedback;
    boolean chunked;
    float sampleRate;
    AllPassFilterEffect allpass = null;
    int oldSize = 0;
    int tempLength = 0;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        MultipleAudio input = (MultipleAudio) getInputAtNode(0);
        MultipleAudio output = new MultipleAudio(input.getChannels());
        Object in;

        AudioFormat af = input.getAudioFormat();
        float sampleRate = af.getSampleRate();

        // This line converts the delay in ms to number of samples and calls it delayOffset

        double loopGain = (double) (20 * (Math.log(((double) feedback / 100)) / (Math.log(10.0))));
        System.out.println("loopGain = " + loopGain);
        double decay60dbTime = (double) ((60 / -loopGain) * delayInMs);
        System.out.println("decay60dbTime = " + decay60dbTime);
        int delayOffset = ((int) (decay60dbTime * sampleRate) / 1000);
        System.out.println("delayOffset = " + delayOffset);

        if (allpass == null) {
            allpass = new AllPassFilterEffect(delayOffset, feedback, chunked, delayInMs, sampleRate);
        }

        //  For each channel
        for (int i = 0; i < input.getChannels(); ++i) {

            in = input.getChannel(i);
            short[] out; // Creates a short array for output data

            // If 16bit data
            if (in instanceof short[]) {

                short[] temp = (short[]) in;
                tempLength = temp.length;

                if (chunked == true) {

                    if (tempLength == oldSize || oldSize == 0) {
                        out = allpass.process(temp);
                        oldSize = temp.length;
                    } else {
                        short[] blankArray = new short[delayOffset];
                        for (int n = 0; n < delayOffset; ++n) {
                            blankArray[n] = 0;
                        }
                        out = allpass.process(blankArray);
                        System.out.println("*********End of file reached here*******");
                    }
                } else {
                    out = allpass.process(temp);
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



