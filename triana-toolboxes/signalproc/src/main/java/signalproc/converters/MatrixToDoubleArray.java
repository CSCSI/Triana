package signalproc.converters;

import org.trianacode.taskgraph.Unit;
import triana.types.MatrixType;

/**
 * Converts a matrix to double arrays
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */


public class MatrixToDoubleArray extends Unit {


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        MatrixType input = (MatrixType) getInputAtNode(0);

        double[][] data = (double[][]) input.getDataArrayReal(0);

        setParameter("rows", String.valueOf(data.length));

        for (int count = 0; count < data.length; count++) {
            output(data[count]);
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
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(1);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Converts a matrix to double arrays");
        setHelpFileLocation("MatrixToDoubleArray.html");

        defineParameter("rows", "0", USER_ACCESSIBLE);
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
        // Insert code to clean-up MatrixToDoubleArray (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
    }


    /**
     * @return an array of the input types for MatrixToDoubleArray
     */
    public String[] getInputTypes() {
        return new String[]{"MatrixType"};
    }

    /**
     * @return an array of the output types for MatrixToDoubleArray
     */
    public String[] getOutputTypes() {
        return new String[]{"double[]"};
    }

}



