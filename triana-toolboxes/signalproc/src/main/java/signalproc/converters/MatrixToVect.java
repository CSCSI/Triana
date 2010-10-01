package signalproc.converters;

import org.trianacode.taskgraph.Unit;
import triana.types.MatrixType;
import triana.types.VectorType;

/**
 * Convert matrix to three vectors of index x, y and data z
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class MatrixToVect extends Unit {

    private String order;    // convert order: row or column major

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        MatrixType input = (MatrixType) getInputAtNode(0);

        // Insert main algorithm for MatrixToVect
        double[] xs = input.getXorYReal(0);
        double[] ys = input.getXorYReal(1);
        double[][] zs = input.getDataReal();

        double[] x = new double[xs.length * ys.length];
        double[] y = new double[xs.length * ys.length];
        double[] z = new double[xs.length * ys.length];

        if (order.startsWith("Row")) {
            int idx = 0;
            for (int i = 0; i < xs.length; i++) {
                for (int j = 0; j < ys.length; j++) {
                    x[idx] = xs[i];
                    y[idx] = ys[j];
                    z[idx] = zs[i][j];
                    idx++;
                }
            }
        } else {
            int idx = 0;
            for (int j = 0; j < ys.length; j++) {
                for (int i = 0; i < xs.length; i++) {
                    x[idx] = xs[i];
                    y[idx] = ys[j];
                    z[idx] = zs[i][j];
                    idx++;
                }
            }
        }
        outputAtNode(0, new VectorType(x));
        outputAtNode(1, new VectorType(y));
        outputAtNode(2, new VectorType(z));
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

        setDefaultOutputNodes(3);
        setMinimumOutputNodes(3);
        setMaximumOutputNodes(3);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Convert matrix to three vectors of index x, y and data z");
        setHelpFileLocation("MatrixToVect.html");

        // Define initial value and type of parameters
        defineParameter("order", "Row Major", USER_ACCESSIBLE);

        String guilines = "";
        guilines
                += "Convert matrix in: $title order Choice [Row major (Y changes fast)] [Column major (X changes fast)]\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        order = (String) getParameter("rowMajor");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up MatrixToVect (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("order")) {
            order = (String) value;
        }

    }


    /**
     * @return an array of the input types for MatrixToVect
     */
    public String[] getInputTypes() {
        return new String[]{"MatrixType"};
    }

    /**
     * @return an array of the output types for MatrixToVect
     */
    public String[] getOutputTypes() {
        return new String[]{"VectorType"};
    }

}



