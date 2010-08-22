package signalproc.input;


import java.util.Random;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;


/**
 * Generates noise with a Gaussian distribution
 *
 * @author Dave Churches
 * @version $Revision: 2921 $
 */


public class GenGaussian extends Unit {

    // parameter data type definitions
    private double mean;
    private double stdDev;
    private double samplingRate;
    private int numberOfPoints;
    private SampleSet output;
    private Random generator;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        output = new SampleSet((int) samplingRate, numberOfPoints);
        for (int i = 0; i < numberOfPoints; i++) {
            output.data[i] = (generator.nextGaussian()) * stdDev + mean;
        }
        output(output);
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

        generator = new Random();

        // Initialise parameter update policy
        setParameterUpdatePolicy(Task.IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Generates noise with a Gaussian distribution");
        setHelpFileLocation("GenGaussian.html");

        // Define initial value and type of parameters
        defineParameter("mean", "0.0", USER_ACCESSIBLE);
        defineParameter("stdDev", "1.0", USER_ACCESSIBLE);
        defineParameter("numberOfPoints", "16384", USER_ACCESSIBLE);
        defineParameter("samplingRate", "16384.0", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "mean $title mean TextField 0.0\n";
        guilines += "stdDev $title stdDev TextField 1.0\n";
        guilines += "number of data points $title numberOfPoints TextField 16384\n";
        guilines += "sampling rate $title samplingRate TextField 16384.0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        mean = new Double((String) getParameter("mean")).doubleValue();
        stdDev = new Double((String) getParameter("stdDev")).doubleValue();
        numberOfPoints = new Integer((String) getParameter("numberOfPoints")).intValue();
        samplingRate = new Double((String) getParameter("samplingRate")).doubleValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up GenGaussian (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("mean")) {
            mean = new Double((String) value).doubleValue();
        }

        if (paramname.equals("stdDev")) {
            stdDev = new Double((String) value).doubleValue();
        }

        if (paramname.equals("numberOfPoints")) {
            numberOfPoints = new Integer((String) value).intValue();
        }

        if (paramname.equals("samplingRate")) {
            samplingRate = new Double((String) value).doubleValue();
        }
    }


    /**
     * @return an array of the input types for GenGaussian
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for GenGaussian
     */
    public String[] getOutputTypes() {
        return new String[]{"SampleSet"};
    }

}



