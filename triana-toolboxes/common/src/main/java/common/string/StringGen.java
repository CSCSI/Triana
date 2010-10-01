package common.string;

import org.trianacode.taskgraph.Unit;


/**
 * Generates a String
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class StringGen extends Unit {

    // parameter data type definitions
    private String str;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        output(str);
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
        setPopUpDescription("Generates a String");
        setHelpFileLocation("StringGen.html");

        // Define initial value and type of parameters
        defineParameter("str", "", USER_ACCESSIBLE);

        // Initialise custom panel interface
        setParameterPanelClass("common.string.StringGenPanel");
        setParameterPanelInstantiate(ON_USER_ACCESS);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        str = (String) getParameter("str");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("str")) {
            str = (String) value;
        }
    }


    /**
     * @return an array of the input types for StringGen
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for StringGen
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.String"};
    }

}



