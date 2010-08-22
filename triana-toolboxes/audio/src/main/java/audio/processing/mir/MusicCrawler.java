package audio.processing.mir;


import java.io.File;

import org.trianacode.taskgraph.Unit;


/**
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2915 $
 */
public class MusicCrawler extends Unit {

    // parameter data type definitions
    private String folderDir;
    String EOF = "EndOfFile";

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        // Insert main algorithm for MusicCrawler

        File dir = new File(folderDir);
        File[] fileNames = dir.listFiles();
        System.out.println("No. of MP3 files in this directory: " + fileNames.length);

        for (int i = 0; i < fileNames.length; i++) {
            File file = fileNames[i];

            //System.out.println("i+1 = " + (i+1));
            //System.out.println("fileNames.length = " + fileNames.length);

            if ((i + 1) == fileNames.length) {
                System.out.println("wuahahahahaha");

                if ((file.toString()).endsWith(".mp3") || (file.toString()).endsWith(".Mp3")
                        || (file.toString()).endsWith(".MP3")) {
                    //               System.out.println(file);
                    output(file);
                }
                output(EOF);
            }

            if ((file.toString()).endsWith(".mp3") || (file.toString()).endsWith(".Mp3") || (file.toString())
                    .endsWith(".MP3")) {
//                 System.out.println(file);
                output(file);
            }
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
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("MusicCrawler.html");

        // Define initial value and type of parameters
        defineParameter("folderDir", "/Users/eddie/Music/triana", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Choose MP3 Directory $title folderDir File /Users/eddie/Music/triana *.*\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        folderDir = (String) getParameter("folderDir");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up MusicCrawler (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("folderDir")) {
            folderDir = (String) value;
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
        return new String[]{"java.lang.Object"};
    }
}
