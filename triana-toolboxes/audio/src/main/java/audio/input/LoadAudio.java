package audio.input;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import triana.types.OldUnit;
import triana.types.audio.AudioChannelFormat;
import triana.types.audio.MultipleAudio;
import triana.types.util.Str;

/**
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2915 $
 */
public class LoadAudio extends OldUnit {


    private float sampleRate;
    private int bitDepth;
    private float chunkSizeInSecs;
    private String channels;
    boolean stopped;
    int bytesread;
    int sizeInBytes = 0;
    int outputBufferSize;
    TargetDataLine targetDataLine = null;
    AudioFormat audioFormat;

    AudioInputStream audioInputStream;
    AudioFileFormat audioFileFormat;
    AudioFormat format;
    String lastDir = null;
    String fileName = "untitled";
    String errStr;
    MultipleAudio ma = null;
    boolean gotEntireFile = false;
    double duration, seconds;
    long bufSize = 16384;
    long songSizeInSamples;
    long outputSizeInSamples;
    int numberOfChunks;
    byte[] bytes;

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

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        if (audioInputStream == null) {
            return;
        }

        int chunkNo = 0;
        createAudioInputStream(new File(fileName));

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

            do {
                try {

                    //   System.out.println("Bytes:" + bytes);
                    //   System.out.println("Bufsize:" + bufSize);
                    //   System.out.println("Bytesread: " + bytesread);
                    //   System.out.println("Audioinputstream: " + audioInputStream);

                    bytesread = audioInputStream.read(bytes, 0, (int) bufSize);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    //    System.out.println("QUICKTEST CATCH");
                }

                //      if (bytesread == -1) {
                //QuestionWindow con = new QuestionWindow(null, Env.getString("StartFromBeginning"));
                //if (con.reply == con.YES){
                //    reset();
                //	}
                //  else {
                //       stop();
                //     return;

                //   }
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

    public void doubleClick() {
        try {
            File file;
            if (lastDir == null) {
                file = new File(System.getProperty("user.dir"));
            } else {
                file = new File(lastDir);
            }

            JFileChooser fc = new JFileChooser(file);
            fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    String name = f.getName();
                    if (name.endsWith(".au") || name.endsWith(".wav") || name.endsWith(".aiff") || name.endsWith(".aif")
                            ||
                            name.endsWith(".AU") || name.endsWith(".WAV") || name.endsWith(".WAV")
                            || name.endsWith(".AIF") ||
                            name.endsWith(".mp3") || name.endsWith(".MP3")) {
                        return true;
                    }
                    return false;
                }

                public String getDescription() {
                    return ".aif, .au, .mp3, .wav, .AU, .WAV, .AIF, .MP3";
                }
            });

            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                createAudioInputStream(fc.getSelectedFile());
                String fn = fc.getSelectedFile().getAbsolutePath();
                userScreen(fc.getSelectedFile().getName());
                parameterUpdate("fileName", fn);
                lastDir = fc.getSelectedFile().getPath();
            }
        } catch (SecurityException ex) {
            // JavaSound.showInfoDialog();
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createAudioInputStream(File file) {
        if (file != null && file.isFile()) {
            try {
                errStr = null;
                audioInputStream = AudioSystem.getAudioInputStream(file);
                audioFileFormat = AudioSystem.getAudioFileFormat(file);
                audioInputStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, audioInputStream);

                format = audioInputStream.getFormat();
                //  System.out.println("Format = " + format);
                //  System.out.println("Frame size = " + format.getFrameSize());
                //  System.out.println("Frame Rate = " + format.getFrameRate());
            } catch (Exception ex) {
                //  ErrorDialog.show(ex.toString());
            }
        } else {
            // ErrorDialog.show("Audio file " + file.getAbsolutePath());
        }
    }

    public void userScreen(String fileName) {
        long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat()
                .getFrameRate());
        duration = milliseconds / 1000.0;

        long bufferLengthInFrames = audioInputStream.getFrameLength();
        int samples = (int) bufferLengthInFrames;
        final double convertSampsToMSec = 1000.0 / (double) format.getSampleRate();
        int frameSizeInBytes = format.getFrameSize();

        bufSize = bufferLengthInFrames * frameSizeInBytes;

        songSizeInSamples = bufferLengthInFrames;

        /** System.out.println("Format : " + format);
         System.out.println("Audio Encoding : " + format.getEncoding());
         System.out.println("Number Of Samples : " + samples);
         System.out.println("Conversion : " + convertSampsToMSec);
         System.out.println("BUFSIZE =" + bufSize); */

        final JSlider slider = new JSlider();
        final JTextField text = new JTextField(String.valueOf(bufSize));
        final JTextField textSamp = new JTextField(String.valueOf(samples));
        int range = (int) milliseconds;

        slider.setMaximum(range);
        slider.setMinimum(0);
        slider.setValue(range);
        slider.setMinorTickSpacing(100);
        slider.setExtent(100);
        slider.setMajorTickSpacing(1000);
        slider.setEnabled(false);

        text.setEnabled(false);
        textSamp.setEnabled(false);
        final JPanel chunkit = new JPanel();
        chunkit.setLayout(new GridLayout(4, 1));
        chunkit.setBackground(Color.white);
        chunkit.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 10, 0));

        chunkit.setEnabled(false);

        final JCheckBox entireFile = new JCheckBox("Load the entire audio file ?", true);
        entireFile.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (entireFile.isSelected()) {
                    slider.setEnabled(false);
                    text.setEnabled(false);
                    textSamp.setEnabled(false);
                    chunkit.setEnabled(false);
                } else {
                    slider.setEnabled(true);
                    text.setEnabled(true);
                    textSamp.setEnabled(true);
                    chunkit.setEnabled(true);
                }
            }
        });
        JLabel fname = new JLabel(
                fileName + " : " + duration + " seconds in length (" + bufferLengthInFrames + " samples)");
        JLabel formatLab = new JLabel("Format : " + format.toString());

        JLabel stream = new JLabel("Or Stream Audio As Follows :", JLabel.CENTER);
        stream.setForeground(Color.black);
        stream.setBackground(Color.magenta);
        JLabel chunkLab = new JLabel("Specify Output Size (in milliseconds or samples) : ");
        JButton ok = new JButton("OK");
        final JDialog frame = new JDialog(new Frame(), "Configure Output Mode", true);

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                frame.setVisible(false);
            }
        });

        JPanel textRow = new JPanel();
        textRow.setLayout(new GridLayout(1, 4));
        textRow.add(new JLabel("Milliseconds :"));
        textRow.add(text);
        textRow.add(new JLabel("Samples :"));
        textRow.add(textSamp);
        textRow.setBackground(Color.white);

        JPanel desc = new JPanel();
        desc.setLayout(new GridLayout(3, 1));
        desc.setBackground(Color.lightGray);
        desc.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        desc.add(fname);
        desc.add(formatLab);
        desc.add(entireFile);

        chunkit.add(stream);
        chunkit.add(chunkLab);
        chunkit.add(slider);
        chunkit.add(textRow);
        chunkit.setBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        content.add(desc, BorderLayout.NORTH);
        content.add(chunkit, BorderLayout.CENTER);
        content.add(ok, BorderLayout.SOUTH);

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int sampVal = (int) ((double) slider.getValue() / convertSampsToMSec);
                text.setText(String.valueOf(slider.getValue()));
                textSamp.setText(String.valueOf(sampVal));
            }
        });
        text.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                slider.setValue(Str.strToInt(text.getText()));
            }
        });

        textSamp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int secVal = (int) ((double) Str.strToInt(textSamp.getText()) * convertSampsToMSec);
                slider.setValue(secVal);
            }
        });

        frame.pack();
        //Display.centralise(frame);
        frame.setVisible(true);

        if (entireFile.isSelected()) {
            numberOfChunks = 1;
            outputSizeInSamples = songSizeInSamples;
        } else {
            outputSizeInSamples = (long) ((slider.getValue() / 1000.0) * format.getSampleRate());
            numberOfChunks = (int) (songSizeInSamples / outputSizeInSamples);
            if ((songSizeInSamples % outputSizeInSamples) > 0) {
                ++numberOfChunks;
            }
        }

        int by = (int) (frameSizeInBytes * outputSizeInSamples);
        //updateParameter("OutputBufferSizeInBytes", by);
        //updateParameter("OutputBufferSizeInFrames", outputSizeInSamples);
        //updateParameter("SongSizeInFrames", songSizeInSamples);
        //updateParameter("NumberOfAudioChunksInSong", numberOfChunks);
        bytes = null;

        ma = null;
        if (entireFile.isSelected()) {
            gotEntireFile = true;
        } else {
            gotEntireFile = false;
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
        setHelpFileLocation("LoadAudio.html");

        // Define initial value and type of parameters
        defineParameter("fileName", "untitled", USER_ACCESSIBLE);
        defineParameter("bufSize", "16384", USER_ACCESSIBLE);
        defineParameter("songSizeInSamples", "", USER_ACCESSIBLE);
        defineParameter("outputSizeInSamples", "", USER_ACCESSIBLE);
        defineParameter("numberOfChunks", "1", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Choose a file $title fileName File untitled *.*\n";
        guilines += "bufSize $title bufSize TextField 16384\n";
        guilines += "SongSizeInSamples $title songSizeInSamples Label \n";
        guilines += "outputSizeInSamples $title outputSizeInSamples Scroller 0 34 0 true\n";
        guilines += "numberOfChunks $title numberOfChunks Label \n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        fileName = (String) getParameter("fileName");
        bufSize = new Long((String) getParameter("bufSize")).longValue();
        songSizeInSamples = new Long((String) getParameter("songSizeInSamples")).longValue();
        outputSizeInSamples = new Long((String) getParameter("outputSizeInSamples")).longValue();
        numberOfChunks = new Integer((String) getParameter("numberOfChunks")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up LoadAudio (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("fileName")) {
            fileName = (String) value;
        }

        if (paramname.equals("bufSize")) {
            bufSize = new Long((String) value).longValue();
        }

        if (paramname.equals("songSizeInSamples")) {
            songSizeInSamples = new Long((String) value).longValue();
        }

        if (paramname.equals("outputSizeInSamples")) {
            outputSizeInSamples = new Long((String) value).longValue();
        }

        if (paramname.equals("numberOfChunks")) {
            numberOfChunks = new Integer((String) value).intValue();
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



