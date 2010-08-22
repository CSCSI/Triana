package audio.processing.dynamic;


import javax.sound.sampled.AudioFormat;
import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * A Hard-Knee Compressor Effect which allows the user to attenuate the level of signal which is over a threshold set by
 * the user. The attenuation is decided by the 'compression ratio', and the speed at which the signal is attenuated is
 * decided by the attack and release times.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see Compressor
 */

public class Compressor extends Unit {

    // parameter data type definitions
    private float threshold; // In Decibels
    private double ratio;
    private double attack; // In milliseconds
    private double release; // In milliseconds
    private double gain; // In decibels
    private boolean autoGain;
    private float sampleRate;
    private String detectionType;
    private int thresholdWidth;
    CompressorEffect compress = null;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        MultipleAudio input = (MultipleAudio) getInputAtNode(0);
        MultipleAudio output = new MultipleAudio(input.getChannels());
        Object in;

        AudioFormat af = input.getAudioFormat();
        sampleRate = af.getSampleRate();

        if (compress == null) {
            compress = new CompressorEffect(threshold, ratio, attack, release, gain, autoGain,
                    detectionType, thresholdWidth, sampleRate);
        }

        // For each channel
        for (int i = 0; i < input.getChannels(); ++i) {
            in = input.getChannel(i);
            short[] out; // Creates a short array for output data

            // If 16bit data
            if (in instanceof short[]) {

                short[] temp = (short[]) in;
                out = compress.process(temp);
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

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("Hard Knee Compressor");
        setHelpFileLocation("Compressor.html");

        // Define initial value and type of parameters
        defineParameter("ratio", "2", USER_ACCESSIBLE);
        defineParameter("threshold", "-19.9", USER_ACCESSIBLE);
        defineParameter("attack", "4.0", USER_ACCESSIBLE);
        defineParameter("release", "60.0", USER_ACCESSIBLE);
        defineParameter("gain", "0", USER_ACCESSIBLE);
        defineParameter("autoGain", "false", USER_ACCESSIBLE);
        defineParameter("detectionType", "Peak", USER_ACCESSIBLE);
        defineParameter("thresholdWidth", "10", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Compression Ratio (eg: 2:1) $title ratio Scroller 1 10 2 false\n";
        guilines += "Compression Threshold (Decibels) $title threshold Scroller -50 0 -19.9 false\n";
        guilines += "Attack Speed (ms) $title attack Scroller 0 100 4 false\n";
        guilines += "Release Speed (ms) $title release Scroller 0 100 60 false\n";
        guilines += "Gain Adjust (Decibels) $title gain Scroller -32 12 0 false\n";
        guilines += "Automatic Gain Compensation? $title autoGain Checkbox false\n";
        guilines += "Amplitude Detection Type $title detectionType Choice [Peak] [RMS] [Region Max] [Region Average]\n";
        guilines += "Threshold Width (ms) $title thresholdWidth IntScroller 0 100 0 false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        threshold = new Float((String) getParameter("threshold")).floatValue();
        ratio = new Double((String) getParameter("ratio")).doubleValue();
        attack = new Double((String) getParameter("attack")).doubleValue();
        release = new Double((String) getParameter("release")).doubleValue();
        gain = new Double((String) getParameter("gain")).doubleValue();
        autoGain = new Boolean((String) getParameter("autoGain")).booleanValue();
        detectionType = (String) getParameter("detectionType");
        thresholdWidth = new Integer((String) getParameter("thresholdWidth")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Compressor (e.g. close open files)
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("threshold")) {
            threshold = new Float((String) value).floatValue();
            if (compress != null) {
                compress.setThreshold(threshold);
            }
        }

        if (paramname.equals("ratio")) {
            ratio = new Double((String) value).doubleValue();
            if (compress != null) {
                compress.setRatio(ratio);
            }
        }

        if (paramname.equals("attack")) {
            attack = new Double((String) value).doubleValue();
            if (compress != null) {
                compress.setAttackTime(attack);
            }
        }

        if (paramname.equals("release")) {
            release = new Double((String) value).doubleValue();
            if (compress != null) {
                compress.setReleaseTime(release);
            }
        }

        if (paramname.equals("gain")) {
            gain = new Double((String) value).doubleValue();
            if (compress != null) {
                compress.setGain(gain);
            }
        }

        if (paramname.equals("detectionType")) {
            detectionType = (String) value;
            if (compress != null) {
                compress.setDetectionType(detectionType);
            }
        }

        if (paramname.equals("thresholdWidth")) {
            thresholdWidth = new Integer((String) value).intValue();
            if (compress != null) {
                compress.setThresholdWidth(thresholdWidth);
            }
        }

        if (paramname.equals("autoGain")) {
            autoGain = new Boolean((String) value).booleanValue();
            if (compress != null) {
                compress.setAutoGain(autoGain);
            }
        }
    }

    /**
     * @return an array of the types accepted by each input node. For node indexes not covered the types specified by
     *         getInputTypes() are assumed.
     */
    public String[][] getNodeInputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types accepted by nodes not covered by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }


    /**
     * @return an array of the types output by each output node. For node indexes not covered the types specified by
     *         getOutputTypes() are assumed.
     */
    public String[][] getNodeOutputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types output by nodes not covered by getNodeOutputTypes().
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

}



