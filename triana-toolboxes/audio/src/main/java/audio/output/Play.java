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
 * A Unit class to support the playback of (chunked/nonchunked) audio. A seperate thread is used for playback to boost
 * CPU efficiency, which the toByteArray method converts the audio from a short array to a byte array in a seperate
 * thread. Created by Dr. Ian Taylor and modified by Eddie Al-Shakarchi Contact e.alshakarchi@cs.cf.ac.uk
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see AudioPlayer
 */

public class Play extends Unit {

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

    int oldBufferSize = 0;
    int outputBufferSize;
    byte[] bytedata;
    SourceDataLine outputChannel = null; // This is initialised for the DataLine.Info line later
    AudioFormat format = null;
    AudioFormat newformat = null;
    AudioPlayer audioPlayer = null;

    /**
     * **************************************************************************************** This is the main
     * processing method. Called whenever there is data for the unit to process *****************************************************************************************
     */

    public void process() throws Exception {

        Object in = getInputAtNode(0);
        byte[] bytes;
        MultipleAudio input = (MultipleAudio) in;

        // SampleSet
        if (in instanceof SampleSet) {
            SampleSet input2 = (SampleSet) in;
            newformat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float) input2.getSamplingRate(),
                    16, 1, 2, (int) input2.getSamplingRate(), bigendian);

            bytes = AudioUtils.to16BitByteArray(input2, newformat);
        }

        // A MultipleAudio type
        else {

            newformat = input.getAudioFormat();
            AudioFormat au;

            // For each channel
            for (int chan = 0; chan < newformat.getChannels(); ++chan) {
                au = input.getAudioFormat(); // grab the audio format

                if (!newformat.matches(au)) {
                    throw new Exception("Incompatible Format Error in " + getToolName() +
                            "\n : Format at node 0 is : \n" + newformat + "\n and format at node "
                            + String.valueOf(chan) + " is : \n" + au);
                }
            }

            // convert to byte array in correct format
            try {
                bytes = toByteArray(input); // calls toByteArray method on 'input'

                if (newformat.getSampleSizeInBits() == 8) {
                    outputBufferSize = input.getChannelLength(0);
                } else if (newformat.getSampleSizeInBits() == 16) {
                    outputBufferSize = input.getChannelLength(0) * 4;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Incompatible Data Error in " + getToolName());
            }
        }

        if (audioPlayer == null) {
            setUpPlayer(newformat); // can only set one format per output line
            format = newformat;
            audioPlayer = new AudioPlayer(outputChannel);
            audioPlayer.start();
            audioPlayer.addChunk(bytes);
        } else {
            audioPlayer.addChunk(bytes);
        }

        output(in);  // output the input data if there are any output nodes
    }

    /**
     * ************************************************************************************** This method sets up the
     * player, and is only called IF there is a change in the format  * of the incoming audio. The SourceDataLine is
     * opened and then started - it's written to in the AudioPlayer ****************************************************************************************
     */

    public void setUpPlayer(AudioFormat audioFormat) {

        System.out.println("Setting Up Player ..");

        // If source dataline is null, then close the channel
        if (outputChannel != null) {
            outputChannel.close();
        }

        //Creates new audio format object
        AudioFormat outputFormat = new AudioFormat(audioFormat.getEncoding(), audioFormat.getSampleRate(),
                audioFormat.getSampleSizeInBits(), audioFormat.getChannels(),
                audioFormat.getFrameSize(), audioFormat.getFrameRate(), audioFormat.isBigEndian());

        System.out.println("In PLAY : Format.. " + outputFormat);
        System.out.println("Frame size = " + outputFormat.getFrameSize());
        System.out.println("Frame Rate = " + outputFormat.getFrameRate());

        // This uses the DataLine.Info subclass to obtain and open a source data line
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, outputFormat);

        try {
            outputChannel = ((SourceDataLine) AudioSystem.getLine(info));
            outputChannel.open(outputFormat, outputBufferSize);
            outputChannel.start();
        }
        catch (LineUnavailableException e) {
            System.err.println("ERROR!! line not supported : " + audioFormat);
        }
    }

    /**
     * ********************************************************************** This method converts the multiple audio
     * input to a specific byte array,* taking into account the bit depth of the audio file.                   *
     * ***********************************************************************
     */

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

        if (frameSizeInBytes == 2) {
            bytedata = new byte[(frameSizeInBytes * samples)];
        } else {
            bytedata = new byte[(frameSizeInBytes * samples) / 2];
        }

// For each channel... basically this converts from multiple audio to byte array
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
                } else { // swap 'em if little endian
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
        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Plays Audio");
        setHelpFileLocation("Play.html");
    }


    // Stops the music using methods from Java Sound API

    public void stopDeMusicMan() {
        if (audioPlayer == null) {
            return;
        }

        audioPlayer.stopPlayer();

        try {
            outputChannel.flush();
            outputChannel.stop();
            outputChannel.close();
        } catch (Exception ee) {
        }

        outputChannel = null;
        format = null;
        audioPlayer = null;
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        stopDeMusicMan();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        System.out.println("Disposig Audio Player");
        stopDeMusicMan();
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
    }

    /**
     * @return an array of the input types for StringGen
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.SampleSet", "triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types for StringGen
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.SampleSet", "triana.types.audio.MultipleAudio"};
    }

}
