package audio.processing.mir;

import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import org.trianacode.taskgraph.Unit;
import org.tritonus.share.sampled.file.TAudioFileFormat;


/**
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2915 $
 */
public class Tester extends Unit {

    // parameter data type definitions
    private String testParameter;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        // Insert main algorithm for Tester
        AudioFileFormat inputBaseFileFormat = (AudioFileFormat) getInputAtNode(0);

        if (inputBaseFileFormat instanceof TAudioFileFormat) {
            Map properties = ((TAudioFileFormat) inputBaseFileFormat).properties();

            //  AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(input);
            //  Map properties = baseFileFormat.properties();
            String key_author = "author";
            String author = (String) properties.get(key_author);
            String key_album = "album";
            Long album = (Long) properties.get(key_album);
            String key_title = "title";
            String title = (String) properties.get(key_title);
            String key_duration = "duration";
            Long duration = (Long) properties.get(key_duration);

            System.out.println("Author = " + author);
            System.out.println("Album = " + album);
            System.out.println("Title = " + title);
            System.out.println("Duration = " + duration);

            //       createAudioInputStream(input);
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
        setMaximumInputNodes(0);

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("Tester.html");

        // Define initial value and type of parameters
        defineParameter("testParameter", "hello", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Type in your crap here: $title testParameter TextField hello\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        testParameter = (String) getParameter("testParameter");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Tester (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("testParameter")) {
            testParameter = (String) value;
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
        return new String[]{"java.lang.String"};
    }

}



