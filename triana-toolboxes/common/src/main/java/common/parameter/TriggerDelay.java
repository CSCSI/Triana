package common.parameter;

import org.trianacode.taskgraph.Unit;


/**
 * Trigger after certain time of delay
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class TriggerDelay extends Unit {

    // parameter data type definitions
    private long delay;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        // Insert main algorithm for TriggerDelay
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
        }
        output(new triana.types.Parameter(new Long(delay)));
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
        setPopUpDescription("Trigger after certain time of delay");
        setHelpFileLocation("TriggerDelay.html");

        // Define initial value and type of parameters
        defineParameter("delay", "0", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Delay (s) $title delay TextField 0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        delay = new Long((String) getParameter("delay")).longValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up TriggerDelay (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("delay")) {
            delay = new Long((String) value).longValue();
        }
    }


    /**
     * @return an array of the input types for TriggerDelay
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for TriggerDelay
     */
    public String[] getOutputTypes() {
        return new String[]{"Parameter"};
    }

}



