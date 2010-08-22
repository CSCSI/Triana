package audio.processing.eq;


import javax.sound.sampled.AudioFormat;
import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * Comb-filter delay Unit which allows user to add multiple delayed signals onto the original by creating a Comb Filter
 * with both a feedforward and a feedback loop. The user can set the level of attenuation of the delayed signal and also
 * adjust the delay time by adjusting the settings using the GUI. CombDelay creates an CombDelayEffect object which
 * extends AudioEffect16bit in the triana.audio package extends AudioEffect 16bit to allow for chunked data.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2921 $
 * @see CombDelayEffect
 * @see triana.audio.AudioEffect16Bit;
 */

public class PeakEQ extends Unit {

    // parameter data type definitions
    private float delayInMs; // In milliseconds
    private float frequency;
    private boolean chunked;
    private float dbGain;
    float sampleRate;
    private float Q;
    PeakEQEffect peak = null;

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

        if (peak == null) {
            peak = new PeakEQEffect(delayOffset, frequency, chunked, delayInMs, sampleRate, Q, dbGain);
        }

        // For each channel
        for (int i = 0; i < input.getChannels(); ++i) {

            in = input.getChannel(i);
            short[] out; // Creates a short array for output data

            // If 16bit data
            if (in instanceof short[]) {

                short[] temp = (short[]) in;
                out = peak.process(temp);
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
        setHelpFileLocation("PeakEQ.html");

        // Define initial value and type of parameters
        defineParameter("frequency", "200", USER_ACCESSIBLE);
        defineParameter("dbGain", "0.0", USER_ACCESSIBLE);
        defineParameter("Q", "0.7", USER_ACCESSIBLE);
        defineParameter("chunked", "false", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Frequency Cutoff Point (%) $title frequency Scroller 0 16000 200 false\n";
        guilines += "Peak Gain (dB) (%) $title dbGain Scroller -20.0 20.0 0.0 false\n";
        guilines += "Q Value $title Q Scroller 0 1 0.7 false\n";
        guilines += "Chunked Data? $title chunked Checkbox false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        frequency = new Float((String) getParameter("frequency")).floatValue();
        dbGain = new Float((String) getParameter("dbGain")).floatValue();
        Q = new Float((String) getParameter("Q")).floatValue();
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
        if (paramname.equals("frequency")) {
            frequency = new Float((String) value).floatValue();
            if (peak != null) {
                peak.setFrequency(frequency);
            }
        }

        if (paramname.equals("Q")) {
            Q = new Float((String) value).floatValue();
            if (peak != null) {
                peak.setQ(Q);
            }
        }

        if (paramname.equals("dbGain")) {
            dbGain = new Float((String) value).floatValue();
            if (peak != null) {
                peak.setDBGain(dbGain);
            }
        }

        if (paramname.equals("chunked")) {
            chunked = new Boolean((String) value).booleanValue();
        }
        {
            if (peak != null) {
                peak.setChunked(chunked);
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



