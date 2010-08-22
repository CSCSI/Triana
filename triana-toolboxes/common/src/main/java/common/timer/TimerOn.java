package common.timer;


import org.trianacode.taskgraph.Unit;
import triana.types.clipins.TimerClipIn;


/**
 * Starts the algorithm timer
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class TimerOn extends Unit {


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        Object input = getInputAtNode(0);

        putClipIn(TimerClipIn.TIMER_CLIPIN_TAG, new TimerClipIn());

        output(input);
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

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Starts the algorithm timer");
        setHelpFileLocation("TimerOn.html");
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up TimerOn (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
    }


    /**
     * @return an array of the input types for TimerOn
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for TimerOn
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}



