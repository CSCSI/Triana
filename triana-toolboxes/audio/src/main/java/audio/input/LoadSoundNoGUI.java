package audio.input;


import org.trianacode.taskgraph.Unit;
import triana.types.audio.AudioChannelFormat;
import triana.types.audio.MultipleAudio;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

/**
 * Headless version of of LoadSound so that it can be used to process a file whose location is hardcoded in this code.
 * Main use is for MIR/DART test protootype.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2915 $
 */
public class LoadSoundNoGUI extends Unit {

    static boolean bigendian = true;

    // Works out type of operating system in order to establish byte-order

    static {
        try {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                bigendian = false;
            }
        }
        catch (Exception ee) { // Windows by default
            bigendian = false;
        }
    }

    AudioInputStream audioInputStream;
    AudioFileFormat audioFileFormat;
    AudioFormat format;
    String lastDir = null;
    String fileName = "/Users/eddie/Desktop/06 Midnight Express copy.aif";
    String errStr;
    MultipleAudio ma = null;
    boolean gotEntireFile = false;
    double duration, seconds;
    long bufSize = 16384;
    long songSizeInSamples;
    long outputSizeInSamples;
    int numberOfChunks = 1;
    byte[] bytes;
    int by;

    File file;

    public void process() throws Exception {

        if(getInputNodeCount() == 1){
            Object object = getInputAtNode(0);
            if(object instanceof String){
                fileName = (String) object;
                createAudioInputStream(new File(fileName));
            }

        }

        if (audioInputStream == null) {
            return;
        }

        int chunkNo = 0;

        System.out.println("File Name stage A = " + fileName);
        createAudioInputStream(new File(fileName));
        //   System.out.println("File Name stage B = " + fileName);

        while (chunkNo < numberOfChunks) {

            // If multiple audio is not null and you have the entire file, then output the multiple audio
            if ((ma != null) && (gotEntireFile)) {
                output(ma);
                System.out.println("mission complete A");
                return;
            }

            // If bytes is null, then bytes is a new byte array of size 16384
            if (bytes == null) {
                bytes = new byte[(int) bufSize];
            }

            int bytesread = 0;

            do {
                try {
                    bytesread = audioInputStream.read(bytes, 0, (int) bufSize);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    //    System.out.println("QUICKTEST CATCH");
                }

                if (bytesread == -1) {
                    //QuestionWindow con = new QuestionWindow(null, Env.getString("StartFromBeginning"));
                    //	if (con.reply == con.YES){
                    //  reset();
                    //		}
                    //      else {
                    //  stop();
                    return;
                    //    }
                }
            } // End of do block

            while (bytesread == -1);

            if (bytesread != bufSize) {
                byte[] newbytes = new byte[(int) bufSize];
                for (int i = 0; i < bytesread; ++i) {
                    newbytes[i] = bytes[i];
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
            System.out.println("mission complete B");
            ++chunkNo;
        }

        if (audioInputStream != null) {
            try {
                audioInputStream.close();
            }
            catch (Exception ee) {
            }
        }
    }

    public void createAudioInputStream(File file) {
        if (file != null && file.isFile()) {
            try {

                //System.out.println("file = " + file);
                errStr = null;
                audioInputStream = AudioSystem.getAudioInputStream(file);
                audioFileFormat = AudioSystem.getAudioFileFormat(file);
                audioInputStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, audioInputStream);

                format = audioInputStream.getFormat();
                //     System.out.println("Format = " + format);
                //     System.out.println("Frame size = " + format.getFrameSize());
                //     System.out.println("Frame Rate = " + format.getFrameRate());

                long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat()
                        .getFrameRate());
                duration = milliseconds / 1000.0;

                long bufferLengthInFrames = audioInputStream.getFrameLength();
                int samples = (int) bufferLengthInFrames;
                final double convertSampsToMSec = 1000.0 / (double) format.getSampleRate();
                int frameSizeInBytes = format.getFrameSize();

                bufSize = bufferLengthInFrames * frameSizeInBytes;

                songSizeInSamples = bufferLengthInFrames;

            } catch (Exception ex) {
                //         ErrorDialog.show(ex.toString());
            }
        } else {
            System.out.println("File isn't there!!");
//            String title = "Error";
//            String text = "Audio file " + file + " not found in correct location!";
//            final JFrame showit = new JFrame(title);
//
//            JButton ok = new JButton("OK");
//            ok.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    showit.dispose();
//                }
//            });
//
//            JPanel buttonpanel = new JPanel(new FlowLayout());
//            buttonpanel.add(ok);
//
//            JTextArea textarea = new JTextArea(text);
//            textarea.setEditable(false);
//            textarea.setBackground(ok.getBackground());
//            textarea.setBorder(new EmptyBorder(3, 3, 3, 3));
//
//            //ImageIcon ima = GUIEnv.getIcon("triana.gif");
//            //JLabel icon = new JLabel(ima);
//            //icon.setBorder(new EmptyBorder(3, 3, 3, 3));
//
//            showit.getContentPane().setLayout(new BorderLayout());
//            showit.getContentPane().add(textarea, BorderLayout.EAST);
//            //showit.getContentPane().add(icon, BorderLayout.WEST);
//            showit.getContentPane().add(buttonpanel, BorderLayout.SOUTH);
//
//            showit.pack();
//            Display.centralise(showit);
//            showit.show();
//            showit.toFront();
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
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("LoadSoundNoGUI.html");

//        // Define initial value and type of parameters
        defineParameter("fileName", "/Users/eddie/Desktop/06 Midnight Express copy.aif", USER_ACCESSIBLE);
//
        File file = new File(fileName);
        if(file.exists()){
            createAudioInputStream(file);
            String fn = "/Users/eddie/Desktop/06 Midnight Express copy.aif";
            parameterUpdate("fileName", fn);
        }

    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        fileName = (String) getParameter("fileName");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up LoadSoundNoGUI (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("fileName")) {
            fileName = (String) value;
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
        return new String[]{"java.lang.Object"};
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



