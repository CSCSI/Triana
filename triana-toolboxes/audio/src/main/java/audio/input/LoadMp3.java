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
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
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
import org.trianacode.gui.Display;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.QuestionWindow;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.util.Env;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import triana.types.OldUnit;
import triana.types.audio.AudioChannelFormat;
import triana.types.audio.MultipleAudio;
import triana.types.util.Str;

/**
 * A class for a Unit which allows the user to load a sound file into Triana. This unit allows the user to split the
 * audio into a stream of contiguous chunks, and output, or as the whole sound file in its entirity. Created by Dr. Ian
 * Taylor. Modified and updated by Eddie Al-Shakarchi. Contact e.alshakarchi@cs.cf.ac.uk .
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2921 $
 */

public class LoadMp3 extends OldUnit {

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
                bytes = new byte[(int) (bufSize)];
            }
            int bytesread = 0;

            do {
                try {
                    System.out.println("QUICKTEST HELLO!");

                    System.out.println("Bytes:" + bytes);
                    System.out.println("Bytes Length:" + bytes.length);
                    System.out.println("Bufsize:" + bufSize);
                    System.out.println("Bytesread: " + bytesread);
                    System.out.println("Audioinputstream: " + audioInputStream);

                    bytesread = audioInputStream.read(bytes, 0, (int) bufSize);

                    System.out.println("Bytesread: " + bytesread);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("QUICKTEST CATCH");
                }

                if (bytesread == -1) {
                    QuestionWindow con = new QuestionWindow(null, Env.getString("StartFromBeginning"));
                    if (con.reply == con.YES) {
                        reset();
                    } else {
                        stop();
                        return;
                    }
                }
            } // End of do block


            while (bytesread == -1);

            System.out.println("QUICKTEST FINAL");

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

                        System.out.println("Chan = " + chan);
                        System.out.println("Chan2 = " + chan2);
                        System.out.println("bytes = " + bytes);
                        System.out.println("byte.length = " + bytes.length);
                        System.out.println("tracklength = " + tracklength);
                        System.out.println("vals.length = " + vals.length);
                        System.out.println("channels = " + channels);

                        for (i = 0; i < tracklength; ++i)
                        // vals[i] = bytes[chan2 + i * channels];
                        {
                            vals[i] = bytes[i * channels];
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
                ee.printStackTrace();
            }
        }
    }

    /**
     * Initialses information specific to LoadSound.
     */
    public void init() {
        super.init();
        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
    }

    /**
     * Called when the stop button is pressed within the MainTriana Window
     */
    public void stopping() {
        super.stopping();
    }

    /**
     * Called when the start button is pressed within the MainTriana Window
     */
    public void starting() {
        super.starting();
    }

    /**
     * Saves LoadSound's parameters.
     */
    public void saveParameters() {
        saveParameter("fileName", FileUtils.convertToVirtualName(fileName));
        saveParameter("OutputBufferSizeInBytes", bufSize);
        saveParameter("SongSizeInFrames", songSizeInSamples);
        saveParameter("OutputBufferSizeInFrames", outputSizeInSamples);
        saveParameter("NumberOfAudioChunksInSong", numberOfChunks);
    }

    /**
     * Used to set each of LoadSound's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
        if (name.equals("fileName")) {
            fileName = FileUtils.convertFromVirtualName(value);
            System.out.println("Updated fileName to " + fileName);
        } else if (name.equals("OutputBufferSizeInBytes")) {
            bufSize = strToLong(value);
            System.out.println("Output Buffer file " + bufSize);
        } else if (name.equals("SongSizeInFrames")) {
            songSizeInSamples = strToLong(value);
            System.out.println("Song Size in samples = " + songSizeInSamples);
        } else if (name.equals("OutputBufferSizeInFrames")) {
            outputSizeInSamples = strToLong(value);
            System.out.println("Ouput buffer Size in frames = " + outputSizeInSamples);
        } else if (name.equals("NumberOfAudioChunksInSong")) {
            numberOfChunks = strToInt(value);
            System.out.println("number of chunks " + numberOfChunks);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
        if (name.equals("fileName")) {
            createAudioInputStream(new File(fileName));
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to LoadSound, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "none";
    }

    /**
     * @return a string containing the names of the types output from LoadSound, each separated by a white space.
     */
    public String outputTypes() {
        return "triana.types.audio.MultipleAudio";
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
                updateParameter("fileName", fn);
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
//                errStr = null;
//                audioInputStream = AudioSystem.getAudioInputStream(file);
//                audioFileFormat = AudioSystem.getAudioFileFormat(file);
                audioInputStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, audioInputStream);
//
//                format = audioInputStream.getFormat();
//                System.out.println("Format = " + format);
//                System.out.println("audioFileFormat = " + audioFileFormat );
//                System.out.println("Frame size = " + format.getFrameSize());
//                System.out.println("Frame Rate = " + format.getFrameRate());

                errStr = null;
                audioInputStream = AudioSystem.getAudioInputStream(file);
                audioFileFormat = AudioSystem.getAudioFileFormat(file);

                format = audioInputStream.getFormat();
                AudioFormat pcmFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        16,
                        format.getChannels(),
                        format.getChannels() * 2,
                        format.getSampleRate(),
                        bigendian);

                audioInputStream = AudioSystem.getAudioInputStream(file);

                format = audioInputStream.getFormat();

                //pcmFormat = new AudioFormat(format.getSampleRate(), 16, format.getChannels(), true, false);

                if (audioFileFormat instanceof TAudioFileFormat) {
                    Map properties = ((TAudioFileFormat) audioFileFormat).properties();
                    String key = "author";
                    String val = (String) properties.get(key);
                    System.out.println("key1: " + key);
                    key = "mp3.framerate.fps";
                    System.out.println("key2: " + key);
                    //float test = mp3.framerate.fps;
                    Float tag;
                    tag = (Float) properties.get(key);

                    //InputStream test2 = (InputStream) properties.get(test);

                    System.out.println("OKAY THIS IS A TAUDIOFILEFORMAT ETC");

                    System.out.println("tag: " + tag);
                    System.out.println("val: " + val);
                    //System.out.println("test: " + test);

                } else {
                    System.out.println("THIS IS NOT TAUDIOFILEFORMAT ETC");
                }

            } catch (Exception ex) {
                ErrorDialog.show(ex.toString());
            }
        } else {
            ErrorDialog.show("Audio file " + file.getAbsolutePath());
        }
    }

    /**
     * Prompts the user for the chunk size to be output from this unit.
     */
    public void userScreen(String fileName) {

        try {

            System.out.println("Format = " + format);
            System.out.println("audioFileFormat = " + audioFileFormat);
            // System.out.println("Frame size = " + format.getFrameSize());
            System.out.println("Frame Rate = " + format.getFrameRate());

            Map properties = ((TAudioFileFormat) audioFileFormat).properties();

            String fps_string = "mp3.framerate.fps";
            Float tag;
            tag = (Float) properties.get(fps_string);

            String framelength_string = "mp3.length.frames";
            Integer framelength;
            framelength = (Integer) properties.get(framelength_string);
            String key2 = "mp3.framerate.fps";

            Float samplerate_mp3;
            samplerate_mp3 = (Float) properties.get(key2);

            String key3 = "mp3.framesize.bytes";
            System.out.println("key3: " + key3);
            Integer framesize_mp3;
            framesize_mp3 = (Integer) properties.get(key3);


            //long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameRate());
            long milliseconds = (long) ((framelength * 1000) / tag);

            duration = milliseconds / 1000.0;

            //long bufferLengthInFrames = audioInputStream.getFrameLength();
            long bufferLengthInFrames = (long) framelength;

            //int samples = (int) bufferLengthInFrames;
            int samples = framelength;

            final double convertSampsToMSec = 1000.0 / (double) samplerate_mp3;
            int frameSizeInBytes = framesize_mp3;

            //bufSize = bufferLengthInFrames * frameSizeInBytes;
            bufSize = framelength * frameSizeInBytes;

            // songSizeInSamples = bufferLengthInFrames;
            songSizeInSamples = framelength;

            // System.out.println("Format : " + format);
            //System.out.println("Audio Encoding : " + format.getEncoding());
            System.out.println("Number Of Samples : " + samples);
            System.out.println("Conversion : " + convertSampsToMSec);
            System.out.println("BUFSIZE =" + bufSize);

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
            chunkit.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

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
            JLabel fname = new JLabel(fileName + " : " + duration + " seconds in length (" + framelength + " samples)");
            JLabel formatLab = new JLabel("Real Format : " + audioFileFormat.toString());

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
            Display.centralise(frame);
            frame.setVisible(true);


            if (entireFile.isSelected()) {
                numberOfChunks = 1;
                outputSizeInSamples = songSizeInSamples;
            } else {
                outputSizeInSamples = (long) ((slider.getValue() / 1000.0) * samplerate_mp3);
                numberOfChunks = (int) (songSizeInSamples / outputSizeInSamples);
                if ((songSizeInSamples % outputSizeInSamples) > 0) {
                    ++numberOfChunks;
                }
            }

            int by = (int) (frameSizeInBytes * outputSizeInSamples);
            updateParameter("OutputBufferSizeInBytes", by);
            updateParameter("OutputBufferSizeInFrames", outputSizeInSamples);
            updateParameter("SongSizeInFrames", songSizeInSamples);
            updateParameter("NumberOfAudioChunksInSong", numberOfChunks);
            bytes = null;

            ma = null;
            if (entireFile.isSelected()) {
                gotEntireFile = true;
            } else {
                gotEntireFile = false;
            }
        }


        catch (Exception e) {
            e.printStackTrace();
            System.out.println("QUICKTEST CATCH");
        }
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "LoadMp3 loads an MP3 and streams the output";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "LoadMp3.html";
    }
}
