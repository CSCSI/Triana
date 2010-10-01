package math.functions;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;

/**
 * Generates a random number
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class Random extends Unit {

    // parameter data type definitions
    private int max;
    private boolean integer;
    private int min;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        double val = Math.random();

        if (integer) {
            val = Math.floor(min + (val * (max - min + 1)));
            output(new Const(val));
        } else {
            val = min + (val * (max - min));
            output(new Const(val));
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
        setPopUpDescription("Generates a random number");
        setHelpFileLocation("Random.html");

        // Define initial value and type of parameters
        defineParameter("max", "1", USER_ACCESSIBLE);
        defineParameter("integer", "false", USER_ACCESSIBLE);
        defineParameter("min", "0", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Minimum Value $title min TextField 0\n";
        guilines += "Maximum Value $title max TextField 1\n";
        guilines += "Integers Only $title integer Checkbox false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        max = new Integer((String) getParameter("max")).intValue();
        integer = new Boolean((String) getParameter("integer")).booleanValue();
        min = new Integer((String) getParameter("min")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Random (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("max")) {
            max = new Integer((String) value).intValue();
        }

        if (paramname.equals("integer")) {
            integer = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("min")) {
            min = new Integer((String) value).intValue();
        }
    }


    /**
     * @return an array of the input types for Random
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for Random
     */
    public String[] getOutputTypes() {
        return new String[]{"Const"};
    }

}



