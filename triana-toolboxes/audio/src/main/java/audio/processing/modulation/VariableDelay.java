package audio.processing.modulation;


import javax.sound.sampled.AudioFormat;
import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * A Variable-delay Effect which allows user to create a chorus or flanger unit by adjusting variables. A single delay
 * is created using a feedforward comb filter. These effects work on the principle of varying the length of the delay.
 * The user can set the attenuation the level of the delayed signal and also adjust the delay time, period length and
 * amplitude by adjusting the settings using the GUI. This base class extends Unit. VariableDelayEffect extends
 * AudioEffect 16bit to allow for chunked data.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see VariableDelayEffect
 */

public class VariableDelay extends Unit {

    // parameter data type definitions
    private float delayInMs; // In milliseconds
    private int feedback;
    private String LFOType;
    private String filterType;
    private String summingType;
    private int oscillationPeriod;
    private int amplitude;
    boolean chunked;
    float sampleRate;
    VariableDelayEffect vari = null;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        MultipleAudio input = (MultipleAudio) getInputAtNode(0);
        MultipleAudio output = new MultipleAudio(input.getChannels());
        Object in;

        AudioFormat af = input.getAudioFormat();
        float sampleRate = af.getSampleRate();
        int noOfChannels = af.getChannels();

        // Calculates to delay size in samples, converts from milliseconds
        int delayOffset = ((int) (delayInMs * sampleRate) / 1000);

        if (vari == null) {
            vari = new VariableDelayEffect(delayOffset, feedback, LFOType, filterType, oscillationPeriod, summingType,
                    amplitude, delayInMs, chunked, sampleRate);
        }

        for (int i = 0; i < input.getChannels(); ++i) {

            in = input.getChannel(i);
            short[] out;

            // If 16bit data
            if (in instanceof short[]) {

                short[] temp = (short[]) in;
                out = vari.process(temp);
                output.setChannel(i, out, input.getChannelFormat(i));
            } // Close if Statement
        } // Close first for loop

        output(output);
    } // Close process() method


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
        setHelpFileLocation("VariableDelay.html");

        // Define initial value and type of parameters
        defineParameter("delayInMs", "20", USER_ACCESSIBLE);
        defineParameter("feedback", "70", USER_ACCESSIBLE);
        defineParameter("LFOType", "sinusoidal", USER_ACCESSIBLE);
        defineParameter("summingType", "Type 1", USER_ACCESSIBLE);
        defineParameter("filterType", "comb", USER_ACCESSIBLE);
        defineParameter("oscillationPeriod", "88200", USER_ACCESSIBLE);
        defineParameter("amplitude", "50", USER_ACCESSIBLE);
        defineParameter("chunked", "false", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Delay In Milliseconds $title delayInMs Scroller 0 50 5 false\n";
        guilines += "Feedback level (%) $title feedback IntScroller 0 100 40 false\n";
        guilines += "Amplitude $title amplitude IntScroller 0 200 50 false\n";
        guilines
                += "Length of Oscillation Period $title oscillationPeriod Choice [11025] [22050] [44100] [66150] [88200] [132300] [176400] \n";
        guilines += "LFO Type $title LFOType Choice [sinusoidal] [triangular]\n";
        guilines += "Oscillator Summing Type $title summingType Choice [Type 1] [Type 2] [Type 3]\n";
        guilines += "Delay Filter Type $title filterType Choice [comb] [allpass] [delay only]\n";
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
        LFOType = (String) getParameter("LFOType");
        summingType = (String) getParameter("summingType");
        filterType = (String) getParameter("filterType");
        oscillationPeriod = new Integer((String) getParameter("oscillationPeriod")).intValue();
        amplitude = new Integer((String) getParameter("amplitude")).intValue();
        chunked = new Boolean((String) getParameter("chunked")).booleanValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up VariableDelayUnit (e.g. close open files)
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("delayInMs")) {
            delayInMs = new Float((String) value).floatValue();
            if (vari != null) {
                vari.setDelayOffset(delayInMs);
            }
        }

        if (paramname.equals("feedback")) {
            feedback = new Integer((String) value).intValue();
            if (vari != null) {
                vari.setFeedback(feedback);
            }
        }

        if (paramname.equals("LFOType")) {
            LFOType = (String) value;
            if (vari != null) {
                vari.setLFOType(LFOType);
            }
        }

        if (paramname.equals("summingType")) {
            summingType = (String) value;
            if (vari != null) {
                vari.setSummingType(summingType);
            }
        }

        if (paramname.equals("filterType")) {
            filterType = (String) value;
            if (vari != null) {
                vari.setFilterType(filterType);
            }
        }

        if (paramname.equals("oscillationPeriod")) {
            oscillationPeriod = new Integer((String) value).intValue();
            if (vari != null) {
                vari.setOscillationPeriod(oscillationPeriod);
            }
        }

        if (paramname.equals("amplitude")) {
            amplitude = new Integer((String) value).intValue();
            if (vari != null) {
                vari.setAmplitude(amplitude);
            }
        }

        if (paramname.equals("chunked")) {
            chunked = new Boolean((String) value).booleanValue();
        }
        {
            if (vari != null) {
                vari.setChunked(chunked);
            }
        }
    }

    /**
     * @return an array of the input types for VariableDelayUnit
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types for VariableDelayUnit
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

}



