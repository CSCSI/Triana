package common.array;

import org.trianacode.taskgraph.Unit;

/**
 * View the contents of an array
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class ArrayViewer extends Unit {

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        Object[] input = (Object[]) getInputAtNode(0);

        setParameter("array", input);
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

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("View the contents of an array");
        setHelpFileLocation("ArrayViewer.html");

        // Initialise custom panel interface
        setParameterPanelClass("common.array.ArrayViewerPanel");

        // define parameters
        defineParameter("array", new Object[0], TRANSIENT);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        setParameter("array", new Object[0]);
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up ArrayViewer (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
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
        return new String[]{"java.lang.Object[]"};
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
        return new String[]{};
    }

}



