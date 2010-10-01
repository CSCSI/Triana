package common.parameter;

import org.trianacode.taskgraph.Unit;

/**
 * Trigger certain numbers of rounds
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class TriggerRounds extends Unit {

    // parameter data type definitions
    private int numOfRounds;

    // a flag indicating this unit is stopping
    private boolean stopping;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        stopping = false;

        // Insert main algorithm for TriggerRounds
        for (int i = numOfRounds; (i > 0) && (!stopping); i--) {
            output(new triana.types.Parameter(i - 1));
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

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Trigger certain numbers of rounds");
        setHelpFileLocation("TriggerRounds.html");

        // Define initial value and type of parameters
        defineParameter("numOfRounds", "1", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Numer of Rounds $title numOfRounds TextField 1\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        numOfRounds = new Integer((String) getParameter("numOfRounds")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up TriggerRounds (e.g. close open files) 
    }

    public void stopping() {
        stopping = true;
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("numOfRounds")) {
            numOfRounds = new Integer((String) value).intValue();
        }
    }


    /**
     * @return an array of the input types for TriggerRounds
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for TriggerRounds
     */
    public String[] getOutputTypes() {
        return new String[]{"Parameter"};
    }

}



