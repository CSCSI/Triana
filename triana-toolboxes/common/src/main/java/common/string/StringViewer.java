package common.string;


import org.trianacode.taskgraph.Unit;
import triana.types.TrianaType;


/**
 * View any object as a String
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class StringViewer extends Unit {

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        String str = "";
        Object obj;

        boolean append = new Boolean((String) getParameter("append")).booleanValue();

        if (append) {
            str = (String) getParameter("str");
        }

        for (int count = 0; count < getInputNodeCount(); count++) {
            obj = getInputAtNode(count);

            if (!obj.equals(TrianaType.NOT_CONNECTED)) {
                if (obj instanceof byte[]) {
                    str += new String((byte[]) obj) + "\n";
                } else {
                    str += obj.toString() + "\n";
                }
            }
        }

        setParameter("str", str);
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setDefaultNodeRequirement(ESSENTIAL_IF_CONNECTED);

        // Initialise pop-up description and help file location
        setPopUpDescription("View any object as a String");
        setHelpFileLocation("StringViewer.html");

        // Define initial value and type of parameters
        defineParameter("str", "", USER_ACCESSIBLE);
        defineParameter("append", "false", USER_ACCESSIBLE);

        // Initialise custom panel interface
        setParameterPanelClass("common.string.StringViewPanel");
        setParameterPanelInstantiate(ON_USER_ACCESS);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        setParameter("str", "");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up StringViewer (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
    }


    /**
     * @return an array of the input types for StringViewer
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for StringViewer
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }

}



