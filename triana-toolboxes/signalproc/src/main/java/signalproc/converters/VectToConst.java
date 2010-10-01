package signalproc.converters;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.VectorType;


/**
 * Outputs a single element of a Vector as a Const
 *
 * @author David Churches
 * @version $Revision: 2921 $
 */


public class VectToConst extends Unit {

    // parameter data type definitions
    private int element;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        VectorType input = (VectorType) getInputAtNode(0);
        output(new Const(input.getData()[element]));
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
        setPopUpDescription("Outputs a single element of a Vector as a Const");
        setHelpFileLocation("converters.html");

        // Define initial value and type of parameters
        defineParameter("element", "0", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Element Index $title element TextField 0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        element = new Integer((String) getParameter("element")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up VectToConst (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("element")) {
            element = new Integer((String) value).intValue();
        }
    }


    /**
     * @return an array of the input types for VectToConst
     */
    public String[] getInputTypes() {
        return new String[]{"VectorType"};
    }

    /**
     * @return an array of the output types for VectToConst
     */
    public String[] getOutputTypes() {
        return new String[]{"Const"};
    }

}



