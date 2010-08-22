package common.string;

import org.trianacode.taskgraph.Unit;

/**
 * View any object as a String
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class HTMLViewer extends Unit {

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        Object input = getInputAtNode(0);

        if (input instanceof byte[]) {
            setParameter("str", new String((byte[]) input));
        } else {
            setParameter("str", input.toString());
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

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setDefaultNodeRequirement(ESSENTIAL_IF_CONNECTED);

        // Initialise pop-up description and help file location
        setPopUpDescription("View any HTML document or http address");
        setHelpFileLocation("HTMLViewer.html");

        // Define initial value and type of parameters
        defineParameter("str", "", USER_ACCESSIBLE);

        // Initialise custom panel interface
        setParameterPanelClass("common.string.HTMLViewPanel");
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
        // Insert code to clean-up HTMLViewer (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
    }


    /**
     * @return an array of the input types for HTMLViewer
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for HTMLViewer
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }

}



