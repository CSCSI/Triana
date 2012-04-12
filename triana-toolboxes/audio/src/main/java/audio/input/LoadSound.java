package audio.input;

import org.trianacode.gui.util.Env;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.QuestionWindow;
import org.trianacode.taskgraph.Unit;
import triana.types.audio.AudioChannelFormat;
import triana.types.audio.MultipleAudio;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

/**
 * A class for a Unit which allows the user to load a sound file into Triana. This unit allows the user to split the
 * audio into a stream of contiguous chunks, and output, or as the whole sound file in its entirety. Created by Dr. Ian
 * Taylor. Modified and updated by Eddie Al-Shakarchi. Contact e.alshakarchi@cs.cf.ac.uk .
 *
 * @author Ian Taylor
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 */

public class LoadSound extends Unit {
    static boolean bigendian = true;

    // Works out type of operating system in order to establish byte-order

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

    public static AudioInputStream audioInputStream;
    static AudioFileFormat audioFileFormat;
    public static AudioFormat format;
    String lastDir = null;

    public static String fileName;

    static String errStr;
    public static MultipleAudio ma = null;
    public static boolean gotEntireFile = false;
    static double duration;
    double seconds;
    public static long bufSize = 16384;
    public static long songSizeInSamples;
    public static long outputSizeInSamples;
    public static int numberOfChunks;
    public static byte[] bytes;
    int by;

    public void process() throws Exception {

        if (audioInputStream == null) {
            return;
        }

        int chunkNo = 0;
        createAudioInputStream(new File(fileName));
        System.out.println("Chunk Number = " + chunkNo);

        while (chunkNo < numberOfChunks) {

            // If multiple audio is not null and you have the entire file, then output the multiple audio
            if ((ma != null) && (gotEntireFile)) {
                output(ma);
                return;
            }

            // If bytes is null, then bytes is a new byte array of size 16384
            if (bytes == null) {
                bytes = new byte[(int) bufSize];
            }

            int bytesread = 0;

            System.out.println("bufSize = " + bufSize);

            do {
                try {
                    bytesread = audioInputStream.read(bytes, 0, (int) bufSize);
                    System.out.println("bytesread" + bytesread);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (bytesread == -1) {
                    QuestionWindow con = new QuestionWindow(null, Env.getString("StartFromBeginning"));
                    if (con.reply == con.YES) {
                        reset();
                    } else {
                        //  stop();
                        return;
                    }
                }
            } // End of do block

            while (bytesread == -1);

            if (bytesread != bufSize) {
                byte[] newbytes = new byte[(int) bufSize];
                for (int i = 0; i < bytesread; ++i) {
                    newbytes[i] = bytes[i];
                    System.out.println("bytes[i] = " + bytes[i]);
                }
                bytes = newbytes;
            }

            int channels = format.getChannels();
            ma = new MultipleAudio(channels);

            if (format.getSampleSizeInBits() > 16) { // i.e. for 24 and 32, store as ints

                int i, j, chan;
                int ptr;
                int tracklength;
                int chan2;
                int channels4 = channels * 4;

                for (chan = 0; chan < channels; ++chan) {
                    chan2 = chan * 2;
                    int[] vals = new int[bytes.length / 4 / channels];
                    tracklength = vals.length;

                    if (format.isBigEndian()) {
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

                    ma.setChannel(chan, vals, new AudioChannelFormat(format));
                }
            } else if (format.getSampleSizeInBits() == 16) { // 16 bit
                //System.out.println("converting from byte array...");

                int i, j, chan;
                int tracklength;
                int chan2;
                int channels2 = channels * 2;

                // For each channel
                for (chan = 0; chan < channels; ++chan) {

                    short[] vals = new short[bytes.length / 2 / channels];
                    chan2 = chan * 2;
                    tracklength = vals.length;

                    // If format = big endian, then for each sample
                    if (format.isBigEndian()) {
                        for (i = 0; i < tracklength; ++i) {
                            vals[i] = ((short) (((bytes[chan2 + (i * channels2)] & 0xFF) << 8) |
                                    ((bytes[chan2 + (i * channels2) + 1] & 0xFF))));
                        }
                    } else {
                        for (i = 0; i < tracklength; ++i) {
                            vals[i] = (short) (((bytes[chan2 + (i * channels2)] & 0xFF)) |
                                    ((bytes[chan2 + (i * channels2) + 1] & 0xFF) << 8));
                        }
                    }

                    ma.setChannel(chan, vals, new AudioChannelFormat(format));
                }
            } else { // 8-bit
                int i, j, chan;
                int tracklength;

                if (channels == 1) {
                    byte[] vals = bytes;
                    ma.setChannel(0, vals, new AudioChannelFormat(format));
                } else { // 2 or more channels
                    int chan2;
                    for (chan = 0; chan < channels; ++chan) {
                        chan2 = chan * 2;
                        byte[] vals = new byte[bytes.length / channels];
                        tracklength = vals.length;
                        for (i = 0; i < tracklength; ++i) {
                            vals[i] = bytes[chan2 + i * channels];
                        }
                        ma.setChannel(chan, vals, new AudioChannelFormat(format));
                    }
                }
            }

            output(ma); //outputs the multiple audio which has been converted from the byte array
            ++chunkNo;
        }

        if (audioInputStream != null) {
            try {
                audioInputStream.close();
            } catch (Exception ee) {
            }
        }
    }

    public static void createAudioInputStream(File file) {
        if (file != null && file.isFile()) {
            try {
                errStr = null;
                audioInputStream = AudioSystem.getAudioInputStream(file);
                audioInputStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, audioInputStream);

                format = audioInputStream.getFormat();
                System.out.println("Format = " + format);
                System.out.println("Frame size = " + format.getFrameSize());
                System.out.println("Frame Rate = " + format.getFrameRate());

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

        // Initialise pop-up description and help file location
        setPopUpDescription("Load in an audio file...");
        setHelpFileLocation("LoadSound.html");

        // Define initial value and type of parameters
        defineParameter("fileName", "untitled", USER_ACCESSIBLE);
        defineParameter("bufSize", "16384", USER_ACCESSIBLE);
        defineParameter("songSizeInSamples", "", USER_ACCESSIBLE);
        defineParameter("outputSizeInSamples", "", USER_ACCESSIBLE);
        defineParameter("numberOfChunksInSong", "", USER_ACCESSIBLE);

        // Initialise custom panels interface
        setParameterPanelClass("audio.input.LoadSoundPanel");
        setParameterPanelInstantiate(ON_USER_ACCESS);

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
            LoadSoundPanel.fileName = (String) value;
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