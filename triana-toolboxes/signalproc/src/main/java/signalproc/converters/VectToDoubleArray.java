package signalproc.converters;

import org.trianacode.taskgraph.Unit;
import triana.types.VectorType;

/**
 * Converts a vector type into two double arrays
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */


public class VectToDoubleArray extends Unit {


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        VectorType input = (VectorType) getInputAtNode(0);

        outputAtNode(0, input.getIndependentScaleReal(0));
        outputAtNode(1, input.getGraphArrayReal(0));
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

        setDefaultOutputNodes(2);
        setMinimumOutputNodes(2);
        setMaximumOutputNodes(2);

        // Initialise parameter update policy
        setParameterUpdatePolicy("PROCESS_UPDATE");

        // Initialise pop-up description and help file location
        setPopUpDescription("Converts a vector type into double arrays");
        setHelpFileLocation("VectToDoubleArray.html");
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
        // Insert code to clean-up VectToDoubleArray (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
    }


    /**
     * @return an array of the input types for VectToDoubleArray
     */
    public String[] getInputTypes() {
        return new String[]{"VectorType"};
    }

    /**
     * @return an array of the output types for VectToDoubleArray
     */
    public String[] getOutputTypes() {
        return new String[]{"double[]"};
    }

}



