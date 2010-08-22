package audio.processing.converters;


import javax.sound.sampled.AudioFormat;
import org.trianacode.taskgraph.Unit;
import audio.processing.tools.NormaliserEffect;
import triana.types.SampleSet;
import triana.types.audio.AudioChannelFormat;
import triana.types.audio.MultipleAudio;

/**
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 */
public class SSetToMAudio extends Unit {

    public static final long filt = (long) (Math.pow(2.0, 8) - 1);
    static boolean bigendian = true;

    static {

        try {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                bigendian = false;
            }
        }
        catch (Exception ee) { // windows by default ...
            bigendian = false;
        }
    }

    private int bitRate;
    private float samplingFrequency;
    private boolean normalCheck;
    private String noOfChannels;
    private float normal;
    AudioFormat newformat = null;
    NormaliserEffect normalise = null;


    /**
     * **************************************************************************************** This is the main
     * processing method. Called whenever there is data for the unit to process *****************************************************************************************
     */

    public void process() throws Exception {

        Object in = getInputAtNode(0);

        double[] doubleArray;
        double[] inputData;

        SampleSet input = (SampleSet) in;
        inputData = input.data;
        short[] finalOutput = new short[inputData.length];

        // Checks to see if user wants data normalised
        if (normalCheck == true) {
            normalise = new NormaliserEffect(normal);

            if (bitRate == 8) {
                finalOutput = normalise.process8Bit(input);
            } else {
                finalOutput = normalise.process(input);
            }
        } else {
            int roundedSample = 0;
            for (int n = 0; n < inputData.length; n++) {
                roundedSample = (int) (Math.rint(inputData[n]));
                finalOutput[n] = (short) roundedSample;
            }
        }

        // Checks Number of Channels
        if (noOfChannels.equals("Mono")) {

            newformat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float) samplingFrequency, bitRate, 1, 2,
                    (int) samplingFrequency, bigendian);

            MultipleAudio ma = new MultipleAudio(1); //creates a MultipleAudio with 1 channel (mono)
            ma.setChannel(0, finalOutput, new AudioChannelFormat(newformat));
            output(ma);
        } else if (noOfChannels.equals("Stereo")) {

            newformat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float) samplingFrequency, bitRate, 2, 4,
                    (int) samplingFrequency, bigendian);

            MultipleAudio ma = new MultipleAudio(2); //creates a MultipleAudio with 1 channel (mono)

            for (int i = 0; i < 2; ++i) {
                ma.setChannel(i, finalOutput, new AudioChannelFormat(newformat));
            }
            output(ma);
        }
    }

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
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("SSetToMAudo.html");

        // Define initial value and type of parameters
        defineParameter("bitRate", "16", USER_ACCESSIBLE);
        defineParameter("samplingFrequency", "44100.0", USER_ACCESSIBLE);
        defineParameter("noOfChannels", "Mono", USER_ACCESSIBLE);
        //defineParameter("endianness", "littleendian", USER_ACCESSIBLE);
        defineParameter("normalCheck", "false", USER_ACCESSIBLE);
        defineParameter("normal", "95", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Select Output Bit Rate $title bitRate Choice [16] [8]\n";
        guilines += "Select SamplingFrequency $title samplingFrequency Choice [44100.0] [22050.0] [11025.0]\n";
        guilines += "Select Mono/Stereo $title noOfChannels Choice [Mono] [Stereo]\n";
        //guilines += "Select Endianness$title endianness Choice [Little Endian] [Big Endian]\n";
        guilines += "Normalise Incoming Data? $title normalCheck Checkbox false\n";
        guilines += "Enter amount of normalisation $title normal Scroller 0 100 95 false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        bitRate = new Integer((String) getParameter("bitRate")).intValue();
        samplingFrequency = new Float((String) getParameter("samplingFrequency")).floatValue();
        noOfChannels = (String) getParameter("noOfChannels");
        normalCheck = new Boolean((String) getParameter("normalCheck")).booleanValue();
        normal = new Float((String) getParameter("normal")).floatValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up SSetToMAudo (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("bitRate")) {
            bitRate = new Integer((String) value).intValue();
        }

        if (paramname.equals("samplingFrequency")) {
            samplingFrequency = new Float((String) value).floatValue();
        }

        if (paramname.equals("noOfChannels")) {
            noOfChannels = (String) value;
        }

        if (paramname.equals("normalCheck")) {
            normalCheck = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("normal")) {
            normal = new Float((String) value).floatValue();
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
        return new String[]{"SampleSet"};
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



