package common.processing;


import java.util.ArrayList;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.Curve;


/**
 * Project 3D curves to 2D from any given angle of view
 *
 * @author B F Schutz
 * @version $Revision: 2921 $
 */
public class ViewPoint extends Unit {

    /**
     * Theta and phi are the standard spherical-polar coordinates of the viewpoint, i.e. the location from which the
     * observer looks at the curves. The curves are projected onto a plane perpendicular to this viewpoint. The values
     * of theta and phi are entered using a single String called view.
     */
    private String view;
    private double theta;
    private double phi;
    private ArrayList in, out;
    private boolean haveData = false;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        Task task = getTask();
        int curves = task.getDataInputNodeCount();
        in = new ArrayList(curves);
        for (int j = 0; j < curves; j++) {
            in.add((Curve) getInputAtNode(j));
        }
        haveData = true;
        project();
        sendOut();
    }

    private void project() {
        int len = in.size();
        out = new ArrayList(len);
        double[] x, y, z, X, Y;
        int j, k, points;
        Curve current;
        double st = Math.sin(theta);
        double ct = Math.cos(theta);
        double sp = Math.sin(phi);
        double cp = Math.cos(phi);
        for (j = 0; j < len; j++) {
            current = (Curve) in.get(j);
            x = (double[]) current.getDataArrayReal(0);
            y = (double[]) current.getDataArrayReal(1);
            z = (double[]) current.getDataArrayReal(2);
            points = x.length;
            X = new double[points];
            Y = new double[points];
            for (k = 0; k < points; k++) {
                X[k] = ct * (x[k] * cp + y[k] * sp) - z[k] * st;
                Y[k] = -x[k] * sp + y[k] * cp;
            }
            out.add(new Curve(X, Y));
        }
    }

    private void sendOut() {
        int len = out.size();
        for (int j = 0; j < len; j++) {
            outputAtNode(j, (Curve) out.get(j));
        }
    }

    private void setAngles() {
        String work = view.trim();
        theta = Math.PI / 180. * Double.parseDouble(work.substring(0, work.indexOf(" ")));
        phi = Math.PI / 180. * Double.parseDouble(work.substring(work.indexOf(" ") + 1));
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Project 3D curves to 2D from any given angle of view");
        setHelpFileLocation("ViewPoint.html");

        // Define initial value and type of parameters
        defineParameter("view", "0.0 0.0", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Spherical coordinates of viewpoint, in degrees, e.g. 45 45: $title view TextField 0.0 0.0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        haveData = false; // On reset ensure that unit waits for new input data before outputting.
        view = (String) getParameter("view");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up ViewPoint (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("view")) {
            view = (String) value;
            setAngles();
            if (haveData) {       // whenever viewpoint changes, do more output.
                project();
                sendOut();
            }
        }
    }


    /**
     * @return an array of the input types for ViewPoint
     */
    public String[] getInputTypes() {
        return new String[]{"Curve"};
    }

    /**
     * @return an array of the output types for ViewPoint
     */
    public String[] getOutputTypes() {
        return new String[]{"Curve"};
    }

}
