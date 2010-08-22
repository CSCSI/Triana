package audio.output;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.trianacode.taskgraph.Unit;
import org.tritonus.share.sampled.AudioSystemShadow;
import org.tritonus.share.sampled.file.AudioOutputStream;
import org.tritonus.share.sampled.file.TDataOutputStream;
import triana.types.SampleSet;
import triana.types.audio.AudioUtils;
import triana.types.audio.MultipleAudio;

/**
 * A Unit class to support the writing/saving of (chunked/nonchunked) audio. A seperate thread is used for the actual
 * writing, to boost CPU efficiency, which the toByteArray method converts the audio from a short array to a byte array
 * in ths current thread/class. This requires the use of Tritonus jars, which are now included as part of the Triana
 * distribution.
 * <p/>
 * Created by Eddie Al-Shakarchi Contact e.alshakarchi@cs.cf.ac.uk
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see WriteAU WriteWAV AudioWriter
 */

public class WriteAiff_old extends Unit {

    public static final long filt = (long) (Math.pow(2.0, 8) - 1);
    static boolean bigendian = true;

    static {

        try {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                bigendian = false;
            }
        } catch (Exception ee) {
            bigendian = false;
        }
    }

    int oldBufferSize = 0;
    int outputBufferSize;
    byte[] bytedata;
    //TargetDataLine outputChannel = null; // This is initialised for the DataLine.Info line later
    AudioFormat format = null;
    AudioFormat newformat = null;
    AudioFormat outputFormat = null;
    AudioWriter audioWriter = null;
    AudioInputStream audioInputStream;
    AudioFileFormat.Type audioFileFormat;
    File outputFile;
    String formatType;
    String fileName = "";
    String fileName2;
    byte[] bytes;

    TDataOutputStream dataOutputStream = null;
    AudioOutputStream audioOutputStream = null;

    /**
     * **************************************************************************************** This is the main
     * processing method. Called whenever there is data for the unit to process *****************************************************************************************
     */

    public void process() throws Exception {

        Object in = getInputAtNode(0);

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

        if (audioWriter == null) {
            setUpWriter(newformat); // can only set one format per output line
            format = newformat;
            audioWriter = new AudioWriter(audioOutputStream);
            audioWriter.addChunk(bytes);
            audioWriter.start();
        } else {
            audioWriter.addChunk(bytes);
        }
        output(in);  // output the input data if there are any output nodes
    }

/*    public void setUpGUI(){

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Audio to File");

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String fn = chooser.getSelectedFile().getAbsolutePath();
            userScreen(chooser.getSelectedFile().getName());
                    updateParameter("fileName", fn);
                    lastDir = chooser.getSelectedFile().getPath();
                }
    }
 */


    /**
     * **************************************************************************************** This method sets up the
     * output stream, and is only called IF there is a change in the  * format of the incoming audio. The
     * dataOutputStream is started - it's written to on the * fly in the AudioWriter class *
     * ****************************************************************************************
     */

    public void setUpWriter(AudioFormat audioFormat) {

        System.out.println("Setting Up Writer...");

        if (fileName.endsWith(".aiff") || fileName.endsWith(".aif")) { //(formatType = WAV)){
            audioFileFormat = AudioFileFormat.Type.AIFF;
            fileName2 = fileName;
        } else {
            audioFileFormat = AudioFileFormat.Type.AIFF;
            fileName2 = fileName + ".aiff";
        }

        outputFile = new File(fileName2);

        //Creates new audio format object
        outputFormat = new AudioFormat(audioFormat.getEncoding(), audioFormat.getSampleRate(),
                audioFormat.getSampleSizeInBits(), audioFormat.getChannels(),
                audioFormat.getFrameSize(), audioFormat.getFrameRate(),
                audioFormat.isBigEndian());

        System.out.println("In WRITE : Format.. " + outputFormat);
        System.out.println("Frame size = " + outputFormat.getFrameSize());
        System.out.println("Frame Rate = " + outputFormat.getFrameRate());

        long lLengthInBytes = AudioSystem.NOT_SPECIFIED;

        try {
            dataOutputStream = AudioSystemShadow.getDataOutputStream(outputFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        audioFileFormat = AudioFileFormat.Type.AIFF;
        audioOutputStream = AudioSystemShadow.getAudioOutputStream(audioFileFormat, outputFormat,
                lLengthInBytes, dataOutputStream);

        // This uses the DataLine.Info subclass to obtain and open a target data line
        //    DataLine.Info info = new DataLine.Info(TargetDataLine.class, outputFormat);

        //     try {
        //       outputChannel = ((TargetDataLine) AudioSystem.getLine(info));
        //        outputChannel.open(outputFormat);
        //        outputChannel.start();
        //    }
        //    catch (LineUnavailableException e) {
        //        System.err.println("ERROR!! line not supported : " + audioFormat);
        //    }
    }

    /**
     * ************************************************************************ This method converts the multiple audio
     * input to a specific byte array,* taking into account the bit depth of the audio file.                   *
     * ************************************************************************
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
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Saves audio as AIFF file");
        setHelpFileLocation("WriteAIFF.html");

        // Define initial value and type of parameters
        defineParameter("fileName", "untitled", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "New File Name: $title fileName TextField untitled\n";
        setGUIBuilderV2Info(guilines);
    }

    public void reset() {
        // Set unit variables to the values specified by the parameters
        fileName = (String) getParameter("fileName");
        stopDeMusicMan();
    }


    // Stops the music using methods from Java Sound API

    public void stopDeMusicMan() {
        if (audioWriter == null) {
            return;
        }

        audioWriter.stopWriter();

        try {
            audioOutputStream.close();
        }
        catch (Exception ee) {
        }

        audioOutputStream = null;
        format = null;
        audioWriter = null;
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        System.out.println("Disposig Audio Writer");
        stopDeMusicMan();
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("fileName")) {
            fileName = (String) value;
        }
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
