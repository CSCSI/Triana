package common.string;

import org.trianacode.taskgraph.Unit;

/**
 * Filter strings that match a regular expression
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class Filter extends Unit {

    // parameter data type definitions
    private String regex;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        String input = (String) getInputAtNode(0);

        if (input.matches(regex)) {
            output(input);
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

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Only outputs strings that match a regular expression");
        setHelpFileLocation("Filter.html");

        // Define initial value and type of parameters
        defineParameter("regex", "", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Regular Expression $title regex TextField \n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        regex = (String) getParameter("regex");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up ReplaceAll (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("regex")) {
            regex = (String) value;
        }
    }


    /**
     * @return an array of the input types for ReplaceAll
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.String"};
    }

    /**
     * @return an array of the output types for ReplaceAll
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.String"};
    }

}



