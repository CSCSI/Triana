package common.string;

import org.trianacode.taskgraph.Unit;

/**
 * Replace all the occurances of a regular expression within a string
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class ReplaceAll extends Unit {

    // parameter data type definitions
    private String regex;
    private String replaceWith;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        java.lang.String input = (java.lang.String) getInputAtNode(0);

        output(input.replaceAll(regex, replaceWith));
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
        setPopUpDescription("Replace all the occurances of a regular expression within a string");
        setHelpFileLocation("ReplaceAll.html");

        // Define initial value and type of parameters
        defineParameter("regex", "", USER_ACCESSIBLE);
        defineParameter("replaceWith", "", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Regular Expression $title regex TextField \n";
        guilines += "Replace With $title replaceWith TextField \n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        regex = (String) getParameter("regex");
        replaceWith = (String) getParameter("replaceWith");
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

        if (paramname.equals("replaceWith")) {
            replaceWith = (String) value;
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



