package audio.input;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import org.trianacode.taskgraph.Unit;
import triana.types.audio.AudioChannelFormat;
import triana.types.audio.MultipleAudio;

/**
 * This unit takes a Line In from the computers main input source, and outputs the data as Multiple Audio 'chunks'. The
 * default audio input device is used. The user can select different quality formats/settings for the audio, such as
 * sample rate, bit depth, etc. Created by Eddie Al-Shakarchi - contact e.alshakarchi@cs.cf.ac.uk
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2921 $
 */

public class LineIn extends Unit {

    private float sampleRate;
    private int bitDepth;
    private float chunkSizeInSecs;
    private String channels;
    boolean stopped;
    byte[] bytes;
    int bytesread;
    int sizeInBytes = 0;
    int outputBufferSize;
    TargetDataLine targetDataLine = null;
    AudioFormat audioFormat;
    MultipleAudio ma;

    static boolean bigendian = true;

    static {
        try {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                bigendian = false;
            }
        }
        catch (Exception ee) { // Windows by default...
            bigendian = false;
        }
    }

    public void process() throws Exception {

        setUpCapture();
        targetDataLine.start();

        // If bytes is null, then bytes is a new byte array of size 'chunkSizeInSecs'
        if (bytes == null) {
            bytes = new byte[(int) sizeInBytes];
        }

        while (!stopped) {
            try {
                bytesread = targetDataLine.read(bytes, 0, sizeInBytes);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            int channels = audioFormat.getChannels();
            ma = new MultipleAudio(channels);

            if (audioFormat.getSampleSizeInBits() > 16) { // i.e. for 26 and 32, store as ints

                int i, chan;
                int ptr;
                int tracklength;
                int chan2;
                int channels4 = channels * 4;

                for (chan = 0; chan < channels; ++chan) {
                    chan2 = chan * 2;
                    int[] vals = new int[bytes.length / 4 / channels];
                    tracklength = vals.length;

                    if (audioFormat.isBigEndian()) {
                        for (i = 0; i < tracklength; ++i) {
                            ptr = chan2 + (i * channels4);
                            vals[i] = ((bytes[ptr] & 0xFF) << 24) |
                                    ((bytes[ptr + 1] & 0xFF) << 16) |
                                    ((bytes[ptr + 2] & 0xFF) << 8) |
                                    ((bytes[ptr + 3] & 0xFF));
                        }
                    } else {
                        for (i = 0; i < tracklength; ++i) {
                            ptr = chan2 + (i * channels4);
                            vals[i] = ((bytes[ptr] & 0xFF)) |
                                    ((bytes[ptr + 1] & 0xFF) << 8) |
                                    ((bytes[ptr + 2] & 0xFF) << 16) |
                                    ((bytes[ptr + 3] & 0xFF) << 24);
                        }
                    }
                    ma.setChannel(chan, vals, new AudioChannelFormat(audioFormat));
                }
            } else if (audioFormat.getSampleSizeInBits() == 16) { // 16 bit

                int i, chan;
                int tracklength;
                int chan2;
                int channels2 = channels * 2;

                // For each channel
                for (chan = 0; chan < channels; ++chan) {

                    short[] vals = new short[bytesread / 2 / channels];
                    chan2 = chan * 2;
                    tracklength = vals.length;
                    //System.out.println("Track length = " + tracklength);

                    // If format = big endian, then for each sample
                    if (audioFormat.isBigEndian()) {
                        for (i = 0; i < tracklength; ++i) {
                            vals[i] = ((short) (((bytes[chan2 + (i * channels2)] & 0xFF) << 8) | ((
                                    bytes[chan2 + (i * channels2) + 1] & 0xFF))));
                        }
                    } else {
                        for (i = 0; i < tracklength; ++i) {
                            vals[i] = (short) (((bytes[chan2 + (i * channels2)] & 0xFF)) | (
                                    (bytes[chan2 + (i * channels2) + 1] & 0xFF) << 8));
                        }
                    }
                    ma.setChannel(chan, vals, new AudioChannelFormat(audioFormat));
                }
            } else { // 8-bit
                int i, chan;
                int tracklength;

                if (channels == 1) {
                    byte[] vals = bytes;
                    ma.setChannel(0, vals, new AudioChannelFormat(audioFormat));
                } else { // 2 or more channels
                    int chan2;
                    for (chan = 0; chan < channels; ++chan) {
                        chan2 = chan * 2;
                        byte[] vals = new byte[bytes.length / channels];
                        tracklength = vals.length;
                        for (i = 0; i < tracklength; ++i) {
                            vals[i] = bytes[chan2 + i * channels];
                        }
                        ma.setChannel(chan, vals, new AudioChannelFormat(audioFormat));
                    }
                }
            }
            output(ma); //outputs multiple audio which has been converted from byte array
        }
    }

    /**
     * ********************************************************* This method sets up the audio capturing TargetDataLine.
     * * *********************************************************
     */
    public void setUpCapture() {

        System.out.println("Setting up Caputuring...");

        double chunkSizeInMillisecs = (double) ((double) chunkSizeInSecs / (double) 1000);

        if (channels.equals("Mono")) {
            int sizeInBytes = (int) (chunkSizeInMillisecs * sampleRate * 2);
            System.err.println("The size in Bytes, for MONO is: " + sizeInBytes);

            audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, bitDepth, 1, 2, sampleRate,
                    bigendian);
        } else if (channels.equals("Stereo")) {
            sizeInBytes = (int) (chunkSizeInMillisecs * sampleRate * 4);
            System.err.println("The size in Bytes, for STEREO is: " + sizeInBytes);

            audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, bitDepth, 2, 4, sampleRate,
                    bigendian);
        }

        System.out.println("New outputted Format is gonna be: " + audioFormat);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
        }
        catch (LineUnavailableException e) {
            System.out.println("unable to get a recording line");
            e.printStackTrace();
        }
    }

    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(0);
        setMinimumInputNodes(0);
        setMaximumInputNodes(0);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("LineInTest.html");

        // Define initial value and type of parameters
        defineParameter("sampleRate", "44100.00", USER_ACCESSIBLE);
        defineParameter("bitDepth", "16", USER_ACCESSIBLE);
        defineParameter("channels", "Stereo", USER_ACCESSIBLE);
        defineParameter("chunkSizeInSecs", "1000", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Choose Bit Depth $title bitDepth Choice [16]\n";
        guilines += "Choose Sampling Frequency $title sampleRate Choice [44100.00] [22050.00] [11025.00]\n";
        guilines += "Choose Stereo or Mono $title channels Choice [Mono] [Stereo]\n";
        guilines += "Set chunk size In Milliseconds $title chunkSizeInSecs Scroller 0 5000 500 false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        cleanUpAudio();
        // Set unit variables to the values specified by the parameters
        sampleRate = new Float((String) getParameter("sampleRate")).floatValue();
        bitDepth = new Integer((String) getParameter("bitDepth")).intValue();
        channels = (String) getParameter("channels");
        chunkSizeInSecs = new Float((String) getParameter("chunkSizeInSecs")).floatValue();
        cleanUpAudio();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        cleanUpAudio();
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("sampleRate")) {
            sampleRate = new Float((String) value).floatValue();
        }

        if (paramname.equals("channels")) {
            channels = (String) value;
        }

        if (paramname.equals("bitDepth")) {
            bitDepth = new Integer((String) value).intValue();
        }

        if (paramname.equals("chunkSizeInSecs")) {
            chunkSizeInSecs = new Float((String) value).floatValue();
        }
    }

    public void stopping() {
        cleanUpAudio();
        System.out.println("STOP BUTTON");
    }

    public void cleanUpAudio() {
        if (targetDataLine != null) {
            stopped = true;
            targetDataLine.stop();
            targetDataLine.drain();
            targetDataLine.close();
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
        return new String[]{};
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
