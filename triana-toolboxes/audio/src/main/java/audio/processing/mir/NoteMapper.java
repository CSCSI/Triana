package audio.processing.mir;

import org.trianacode.taskgraph.Unit;

/**
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2915 $
 */
public class NoteMapper extends Unit {

    // parameter data type definitions
    private int freq;
    private String notes;
    int[] freqArray;
    NoteWriter notewriter = null;
    int noOfResults;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        Object input = (Object) getInputAtNode(0);

        freq = ((Integer) input).intValue();

        if (notewriter == null) {
            notewriter = new NoteWriter(freq);
            notewriter.addNote(freq);
        } else {
            notewriter.addNote(freq);
        }

        noOfResults++;
        //System.out.println("noOfResults = " + noOfResults);

        if (noOfResults == 672) {
            notewriter.findNoteMap();
            notewriter.writeFile();
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
        setHelpFileLocation("NoteMapper.html");

        // Define initial value and type of parameters
        defineParameter("freq", "0", USER_ACCESSIBLE);
        defineParameter("notes", "", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Frequencies $title freq Label \n";
        guilines += "Notes: $title notes TextField \n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        freq = new Integer((String) getParameter("freq")).intValue();
        notes = (String) getParameter("notes");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up NoteMapper (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("freq")) {
            freq = new Integer((String) value).intValue();
        }

        if (paramname.equals("notes")) {
            notes = (String) value;
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



