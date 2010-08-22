package audio.processing.mir;


import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import triana.types.audio.MultipleAudio;

/**
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2915 $
 */
public class ID3Xtractor extends Unit {

    static boolean bigendian = true;

    // Works out type of operating system in order to establish byte-order

    static {
        try {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                bigendian = false;
            }
        }
        catch (Exception ee) { // Windows by default ...
            bigendian = false;
        }
    }

    // parameter data type definitions
    private String filePath;
    AudioInputStream audioInputStream;
    AudioFileFormat audioFileFormat;

    //AudioFormat baseFormat = null;
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

    List linkedListA = new LinkedList();

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {

        Object inputA = getInputAtNode(0);

        if (inputA instanceof File) {
            File input = (File) inputA;


            AudioFileFormat baseFileFormat = null;
            AudioFileFormat output = null;

            // Insert main algorithm for ID3Xtractor
            if ((input.toString()).endsWith(".mp3") || (input.toString()).endsWith(".Mp3")
                    || (input.toString()).endsWith(".mP3")
                    || (input.toString()).endsWith(".MP3")) {

                baseFileFormat = AudioSystem.getAudioFileFormat(input);

                if (baseFileFormat instanceof TAudioFileFormat) {
                    Map properties = ((TAudioFileFormat) baseFileFormat).properties();

                    //  AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(input);
                    //  Map properties = baseFileFormat.properties();
                    String key_author = "author";
                    String author = (String) properties.get(key_author);
                    String key_album = "album";
                    String album = (String) properties.get(key_album);
                    String key_title = "title";
                    String title = (String) properties.get(key_title);
                    String key_duration = "duration";
                    Long duration = (Long) properties.get(key_duration);

                    System.out.println("Author = " + author);
                    System.out.println("Album = " + album);
                    // System.out.println("Title = " + title);
                    //   System.out.println("Duration = " + duration);

                    //       createAudioInputStream(input);

                    linkedListA.add(author);
                    output = baseFileFormat;
                }
            }

        }
        //output(output);

        System.out.println("THIS IS JUST BEFORE THE IF STATEMENT");
        System.out.println("inputA = " + inputA);

        if (inputA instanceof String) {
            System.out.println("hey guess what the output is null");
            output(linkedListA);
        }
    }

    public void createAudioInputStream(File file) {

        if (file.getAbsolutePath() != null && file.isFile()) {
            try {
                System.out.println("this is file: " + file.getAbsolutePath());
                System.out.println("test 1");
                errStr = null;

                System.out.println("test 2");

//               audioInputStream  = AudioSystem.getAudioInputStream(file);

                System.out.println("test 3");
                audioFileFormat = AudioSystem.getAudioFileFormat(file);

                System.out.println("test 4");

                format = audioInputStream.getFormat();
                AudioFormat pcmFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), 16,
                        format.getChannels(),
                        format.getChannels() * 2, format.getSampleRate(), bigendian);

                audioInputStream = AudioSystem.getAudioInputStream(file);

                format = audioInputStream.getFormat();


                //audioFileFormat = AudioSystem.getAudioFileFormat(file);
                //pcmFormat = new AudioFormat(format.getSampleRate(), 16, format.getChannels(), true, false);

                System.out.println("test 5");

                if (audioFileFormat instanceof TAudioFileFormat) {

                    System.out.println("test 6");

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
            }
            catch (Exception ex) {
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
        setHelpFileLocation("ID3Xtractor.html");

        // Define initial value and type of parameters
        defineParameter("filePath", "", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Choose MP3: $title filePath File null *.mp3\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        filePath = (String) getParameter("filePath");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up ID3Xtractor (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("filePath")) {
            filePath = (String) value;
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
        return new String[]{"java.lang.Object"};
    }

}



