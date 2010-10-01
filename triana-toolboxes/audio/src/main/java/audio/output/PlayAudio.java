package audio.output;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.audio.AudioUtils;
import triana.types.audio.MultipleAudio;

/**
 * @author
 * @version $Revision: 4052 $
 */
public class PlayAudio extends Unit {

    public static final long filt = (long) (Math.pow(2.0, 8) - 1);
    static boolean bigendian = true;

    static {
        try {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                bigendian = false;
            }
        } catch (Exception ee) { // ha ha! windows by default ...
            bigendian = false;
        }
    }

    int outputBufferSize = 16384;

    SourceDataLine outputChannel = null;
    AudioFormat format = null;
    AudioFormat newformat;


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        Object in = getInputAtNode(0);
        byte[] bytes;

        if (in instanceof SampleSet) {
            SampleSet input = (SampleSet) in;
            newformat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    (float) input.getSamplingRate(), 16, 1, 2, (int) input.getSamplingRate(), bigendian);
            bytes = AudioUtils.to16BitByteArray(input, newformat);
        } else { // a multiplAudio type
            MultipleAudio input = (MultipleAudio) in;
            newformat = input.getAudioFormat();
            AudioFormat au;

            for (int chan = 0; chan < newformat.getChannels(); ++chan) {
                au = input.getAudioFormat();
                if (!newformat.matches(au)) {
                    throw new Exception("Incompatible Format Error in " + getToolName() +
                            "\n : Format at node 0 is : \n" + newformat +
                            "\n and format at node " + String.valueOf(chan) + " is : \n" + au);
                }
            }

            // converet to byte array in correct format
            try {
                bytes = toByteArray(input);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Incompatible Data Error in " + getToolName());
            }
        }

        if ((outputChannel == null) || (!newformat.matches(format))) {
            setUpPlayer(newformat);
            format = newformat;
        }

        outputChannel.write(bytes, 0, bytes.length);
        outputChannel.flush();

        output(in);  // output the input data if there are any output nodes
    }

    public void setUpPlayer(AudioFormat audioFormat) {
        System.out.println("Setting Up Player ..");
        if (outputChannel != null) {
            outputChannel.close();
        }
        AudioFormat outputFormat = new AudioFormat(audioFormat.getEncoding(),
                audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(),
                audioFormat.getChannels(), audioFormat.getFrameSize(),
                audioFormat.getFrameRate(), audioFormat.isBigEndian());
        System.out.println("In PLAY : Format.. " + outputFormat);
        System.out.println("Frame size = " + outputFormat.getFrameSize());
        System.out.println("Frame Rate = " + outputFormat.getFrameRate());
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, outputFormat);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line matching " + info + " not supported.");
        }

        try {
            outputChannel = ((SourceDataLine) AudioSystem.getLine(info));
            outputChannel.open(outputFormat, outputBufferSize);
            outputChannel.start();
        } catch (LineUnavailableException e) {
            System.err.println("ERROR!! line not supported : " + audioFormat);
        }
    }

    public byte[] toByteArray(MultipleAudio input) throws Exception {
        int frameSizeInBytes = newformat.getFrameSize();
        int channels = newformat.getChannels();
        int chan;
        int samples = 0;
        int pos;
        int i, j;
        for (chan = 0; chan < channels; ++chan) {
            if (samples < input.getChannelLength(chan)) {
                samples = input.getChannelLength(chan);
            }
        }

        samples = samples * channels;

        // this could create a n array which is larger that we need if the arrays are
        // different sizes.  They can be different sizes but not sure what the result will be

        byte[] bytedata = new byte[frameSizeInBytes * samples];

        for (chan = 0; chan < channels; ++chan) {
            Object o = input.getChannel(chan);

            if (newformat.getSampleSizeInBits() == 8) {
                byte channel[] = (byte[]) o;
                for (i = 0; i < channel.length; ++i) {
                    bytedata[chan + (i * channels)] = channel[i];
                }
            } else if (newformat.getSampleSizeInBits() == 16) {
                int b = 2;
                short bitVal;
                short vals[] = (short[]) o;
                if (bigendian) {
                    for (j = 0; j < vals.length; ++j) {
                        pos = j * channels;
                        bitVal = (short) vals[j];
                        for (i = 0; i < b; ++i) {
                            bytedata[(chan * b) + (pos * b) + (b - i - 1)] = (byte) (bitVal & filt);
                            bitVal = (short) (bitVal >> 8);
                        }
                    }
                } else { // a windows machine ... need to swap the bytes back for the audio engine
                    for (j = 0; j < vals.length; ++j) {
                        pos = j * channels;
                        bitVal = (short) vals[j];
                        for (i = 0; i < b; ++i) {
                            bytedata[(chan * b) + (pos * b) + i] = (byte) (bitVal & filt);
                            bitVal = (short) (bitVal >> 8);
                        }
                    }
                }
            } else { // assume 32 bit
                int b = 4;
                int bitVal;
                int vals[] = (int[]) o;
                if (bigendian) {
                    for (j = 0; j < vals.length; ++j) {
                        pos = j * channels;
                        bitVal = (int) vals[j];
                        for (i = 0; i < b; ++i) {
                            bytedata[(chan * b) + b - i - 1 + (pos * b)] = (byte) (bitVal & filt);
                            bitVal = (short) (bitVal >> 8);
                        }
                    }
                } else { // swap 'em
                    for (j = 0; j < vals.length; ++j) {
                        pos = j * channels;
                        bitVal = (int) vals[j];
                        for (i = 0; i < b; ++i) {
                            bytedata[(chan * b) + i + (pos * b)] = (byte) (bitVal & filt);
                            bitVal = (short) (bitVal >> 8);
                        }
                    }
                }
            }
        }
        return bytedata;
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
        setHelpFileLocation("PlayAudio.html");
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        stopDeMusicMan();
    }

    public void stopDeMusicMan() {
        try {
            outputChannel.stop();
            outputChannel.close();
        } catch (Exception ee) {
        }
        outputChannel = null;
        format = null;
    }


    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up PlayAudio (e.g. close open files) 
        stopDeMusicMan();
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
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



