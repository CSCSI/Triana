package audio.input;

import org.trianacode.gui.util.Env;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.QuestionWindow;
import org.trianacode.taskgraph.Unit;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import javazoom.spi.mpeg.sampled.file.MpegAudioFormat;
import triana.types.audio.AudioChannelFormat;
import triana.types.audio.MultipleAudio;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * A class for a Unit which allows the user to load a sound file into Triana. This unit allows the user to split the
 * audio into a stream of contiguous chunks, and output, or as the whole sound file in its entirety. Created by Dr. Ian
 * Taylor. Modified and updated by Eddie Al-Shakarchi. Contact e.alshakarchi@cs.cf.ac.uk .
 *
 * @author Ian Taylor
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 */

public class LoadMP3 extends Unit {
    static boolean bigendian = true;

    // Works out type of operating system in order to establish byte-order

    static {
        try {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                bigendian = false;
            }
        }
        catch (Exception ee) { // ha ha! windows by default ...
            bigendian = false;
        }
    }

    public static AudioInputStream audioInputStream;
    public static AudioInputStream din;
    public static AudioFormat baseFormat;
    public static AudioFormat decodedFormat;
    public static long duration;

    String lastDir = null;

    public static String fileName;

    public static String errStr;
    public static MultipleAudio ma = null;
    public static boolean gotEntireFile;

    //static double duration;
    public static long bufSize;
    public static long songSizeInSamples;
    public static long outputSizeInSamples;
    public static int numberOfChunks;

    public static byte[] newBytes;
    public SourceDataLine line;


    //public byte[] bytes;
    int by;

    public void process() throws Exception {

        int chunkNo = 0;
        //createAudioInputStream(new File(fileName));


        while (chunkNo < numberOfChunks) {
            System.out.println("Chunk Number = " + chunkNo);

            // If multiple audio is not null and you have the entire file, then output the multiple audio
            if ((ma != null) && (gotEntireFile)) {
                output(ma);
                return;
            }

            System.out.println("bufSize = " + bufSize);

            //if (bytes == null) {
            byte[] bytes = new byte[(int) bufSize];
            //}
            Vector<Byte> bytesVector = new Vector<Byte>();

            int bytesread = 0;
            din = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);

            System.out.println("CHECK 1!");

                try {
                    bytesread = din.read(bytes, 0, (int) bufSize);
                    System.out.println("BYTESREAD = " + bytesread);

                    while (bytesread != -1) {
                        for (int i = 0; i < bytesread; ++i) {
                            bytesVector.add(bytes[i]);
                        }
	                    bytesread = din.read(bytes, 0, (int) bufSize);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    //    System.out.println("QUICKTEST CATCH");
                }

            System.out.println("vector size is = " + bytesVector.size());
//            System.out.println("newBytes.length = " + newBytes.length);

            if (numberOfChunks == 1){
                //newBytes = new byte[bytes.length];
                System.out.println("bytes.length = " + bytes.length);
                for (int i = 0; i < bytesVector.size(); ++i) {
                    bytes[i] = bytesVector.get(i);
                }
           }
            if (numberOfChunks > 1){
                System.out.println("bytes.length = " + bytes.length);
                for (int i = 0; i < bytes.length; ++i) {
                    bytes[i] = bytesVector.get(i);
                }
            }

            //byteToShortArray(bytesVector);

            int channels = decodedFormat.getChannels();
            ma = new MultipleAudio(channels);

        if (decodedFormat.getSampleSizeInBits() > 16) { // i.e. for 24 and 32, store as ints

            int i, j, chan;
                int ptr;
                int tracklength;
                int chan2;
                int channels4 = channels * 4;

                for (chan = 0; chan < channels; ++chan) {
                    chan2 = chan * 2;
                    int[] vals = new int[bytes.length / 4 / channels];
                    tracklength = vals.length;

                    if (decodedFormat.isBigEndian()) {
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

                    ma.setChannel(chan, vals, new AudioChannelFormat(decodedFormat));
                }
            } else if (decodedFormat.getSampleSizeInBits() == 16) { // 16 bit
                System.out.println("converting from byte array...");

                int i, j, chan;
                int tracklength;
                int chan2;
                int channels2 = channels * 2;

                // For each channel
                for (chan = 0; chan < channels; ++chan) {

                    short[] vals = new short[bytes.length / 2 / channels];
                    chan2 = chan * 2;
                    tracklength = vals.length;

                    // If  = big endian, then for each sample
                    if (decodedFormat.isBigEndian()) {
                        for (i = 0; i < tracklength; ++i) {
                            vals[i] = ((short) (((bytes[chan2 + (i * channels2)] & 0xFF) << 8) |
                                    ((bytes[chan2 + (i * channels2) + 1] & 0xFF))));
                        }
                        //System.out.print(" vals is big endian and length = " + vals.length);
                    } else {
                        for (i = 0; i < tracklength; ++i) {
                            vals[i] = (short) (((bytes[chan2 + (i * channels2)] & 0xFF)) |
                                    ((bytes[chan2 + (i * channels2) + 1] & 0xFF) << 8));
                        }
                    }

                    ma.setChannel(chan, vals, new AudioChannelFormat(decodedFormat));
                }
            } else { // 8-bit
                int i, j, chan;
                int tracklength;

                if (channels == 1) {
                    byte[] vals = bytes;
                    ma.setChannel(0, vals, new AudioChannelFormat(decodedFormat));
                } else { // 2 or more channels
                    int chan2;
                    for (chan = 0; chan < channels; ++chan) {
                        chan2 = chan * 2;
                        byte[] vals = new byte[bytes.length / channels];
                        tracklength = vals.length;
                        for (i = 0; i < tracklength; ++i) {
                            vals[i] = bytes[chan2 + i * channels];
                        }
                        ma.setChannel(chan, vals, new AudioChannelFormat(decodedFormat));
                    }
                }
            }

            output(ma); //outputs the multiple audio which has been converted from the byte array
            ++chunkNo;

        }

            if (din != null) {
                try {
                    din.close();
                }
                catch (Exception ee) {
                    ee.printStackTrace();
                }
            }

    }

    public static void createAudioInputStream(File file) {

        System.out.println("file = " + file);

        if (file != null && file.isFile()) {
            try {
                //errStr = null;

                try{
			        audioInputStream = AudioSystem.getAudioInputStream(file);
                    //System.out.println("audioinputstream = " + audioInputStream);
		        } catch (Exception e){
                    e.printStackTrace();
                    //System.exit(1);
		        }

                baseFormat = audioInputStream.getFormat();
                decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
                        baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), baseFormat.isBigEndian());


                AudioFileFormat baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(file);
                Map properties = baseFileFormat.properties();
                String key_author = "author";
                String key_duration = "duration";
                duration = (Long) properties.get(key_duration);

                System.out.println("Author = " + properties.get(key_author));
                System.out.println("Duration in microseconds = " + duration);

                System.out.println("DecodedFormat = " + decodedFormat);
                System.out.println("decodedFormat Frame size = " + decodedFormat.getFrameSize());
                System.out.println("decodedFormat Frame Rate = " + decodedFormat.getFrameRate());
                System.out.println("decodedFormat sample Rate = " + decodedFormat.getSampleRate());
                //System.out.println("din format = " + din.getFormat());

                //rawplay(decodedFormat, din);
                //System.out.println("din = " + din);

                audioInputStream.close();

            } catch (Exception ex) {
                ErrorDialog.show(ex.toString());
            }
        } else {
            ErrorDialog.show("Audio file " + file.getAbsolutePath());
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
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);
        setParameterPanelClass("audio.input.LoadMP3Panel");
        setParameterPanelInstantiate(ON_USER_ACCESS);

        // Initialise pop-up description and help file location
        setPopUpDescription("Load in an audio file...");
        setHelpFileLocation("LoadMP3.html");

        // Define initial value and type of parameters
        defineParameter("fileName", "untitled", USER_ACCESSIBLE);
        defineParameter("bufSize", "16384", USER_ACCESSIBLE);
        defineParameter("songSizeInSamples", "", USER_ACCESSIBLE);
        defineParameter("outputSizeInSamples", "", USER_ACCESSIBLE);
        defineParameter("numberOfChunksInSong", "", USER_ACCESSIBLE);

        // Initialise custom panels interface
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        super.reset();
        // Set unit variables to the values specified by the parameters
        fileName = (String) getParameter("fileName");

    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up LoadSound (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("fileName")) {
           // LoadMP3Panel.fileName = (String) value;
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