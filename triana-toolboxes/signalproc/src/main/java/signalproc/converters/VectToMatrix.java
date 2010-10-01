package signalproc.converters;

import java.util.Arrays;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.MatrixType;
import triana.types.VectorType;

/**
 * Convert three vectors of index x, y and data z (same length) to matrix
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class VectToMatrix extends Unit {

    private double fillValue;
    private String dupAction;

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        //VectorType input = (VectorType) getInputAtNode(0);

        // Insert main algorithm for VectToMatrix
        double[] x = ((VectorType) getInputAtNode(0)).getGraphReal();
        double[] y = ((VectorType) getInputAtNode(1)).getGraphReal();
        double[] z = ((VectorType) getInputAtNode(2)).getGraphReal();

        if (!(x.length == y.length && x.length == z.length)) {
            ErrorDialog.show("Index x, y and data z must be in same length");
            return;
        }

        double[] xx = sort_uniq(x);
        double[] yy = sort_uniq(y);
        System.out.println("length (data, xx, yy): " + z.length + ", " + xx.length + ", " + yy.length);
        double[][] zz = new double[xx.length][yy.length];
        boolean[][] seen = new boolean[xx.length][yy.length];

        for (int i = 0; i < xx.length; i++) {
            for (int j = 0; j < yy.length; j++) {
                zz[i][j] = fillValue;
            }
        }

        for (int i = 0; i < z.length; i++) {
            int ix = Arrays.binarySearch(xx, x[i]);
            int iy = Arrays.binarySearch(yy, y[i]);
            if (!seen[ix][iy]) {
                zz[ix][iy] = z[i];
                seen[ix][iy] = true;
            } else {
                System.out.println("seen at (i, ix, iy, z[i]): " + i + ", " + ix + ", " + iy + ", " + z[i]);
                if (dupAction.equals("Last")) {
                    zz[ix][iy] = z[i];
                } else if (dupAction.equals("First")) {
                    ;
                } else if (dupAction.equals("Max")) {
                    zz[ix][iy] = Math.max(zz[ix][iy], z[i]);
                } else if (dupAction.equals("Min")) {
                    zz[ix][iy] = Math.min(zz[ix][iy], z[i]);
                }
            }
        }

        output(new MatrixType(xx, yy, zz));
    }

    private double[] sort_uniq(double[] a) {
        double[] copy = new double[a.length];
        System.arraycopy(a, 0, copy, 0, a.length);
        Arrays.sort(copy);
        int dup = 0;
        double old = Double.NaN;
        for (int i = 0; i < copy.length; i++) {
            if (old == copy[i]) {
                dup++;
            } else {
                old = copy[i];
            }
        }
        double[] r = new double[copy.length - dup];
        old = Double.NaN;
        for (int i = 0, j = 0; i < copy.length; i++) {
            if (old != copy[i]) {
                old = r[j++] = copy[i];
            }
        }

        return r;
    }

    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(3);
        setMinimumInputNodes(3);
        setMaximumInputNodes(3);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Convert three vectors of index x, y and data z (same length) to matrix");
        setHelpFileLocation("VectToMatrix.html");

        defineParameter("fillValue", "NaN", USER_ACCESSIBLE);
        defineParameter("dupAction", "Last", USER_ACCESSIBLE);

        String guilines = "";
        guilines += "Default value to fill the matrix $title fillValue TextField 0.0 \n";
        guilines
                += "When data z value not unique wrt. the same index x and y, then use $title dupAction Choice [Last] [First] [Max] [Min]\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        fillValue = new Double((String) getParameter("fillValue")).doubleValue();
        dupAction = (String) getParameter("dupAction");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up VectToMatrix (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("fillValue")) {
            fillValue = new Double((String) value).doubleValue();
        }

        if (paramname.equals("dupAction")) {
            dupAction = (String) value;
        }

    }


    /**
     * @return an array of the input types for VectToMatrix
     */
    public String[] getInputTypes() {
        return new String[]{"VectorType"};
    }

    /**
     * @return an array of the output types for VectToMatrix
     */
    public String[] getOutputTypes() {
        return new String[]{"MatrixType"};
    }

}



