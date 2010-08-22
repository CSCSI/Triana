package signalproc.input;


import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;


/**
 * Generates noise with a Gaussian distribution
 *
 * @author Dave Churches
 * @version $Revision: 2921 $
 */


public class GenRandNoise extends Unit {

    // parameter data type definitions
    private double lowerBound = 0.0;
    private double upperBound = 1.0;
    private int numberOfPoints;
    private double samplingRate;
    private SampleSet output;
    private double scale;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        scale = upperBound - lowerBound;
        output = new SampleSet((int) samplingRate, numberOfPoints);
        for (int i = 0; i < numberOfPoints; i++) {
            output.data[i] = Math.random() * scale + lowerBound;
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

        // Initialise parameter update policy
        setParameterUpdatePolicy(Task.IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Generates uniformly distributed noise between and upper and lower limit");
        setHelpFileLocation("GenRandNoise.html");

        // Define initial value and type of parameters
        defineParameter("lowerBound", "0.0", USER_ACCESSIBLE);
        defineParameter("upperBound", "1.0", USER_ACCESSIBLE);
        defineParameter("numberOfPoints", "16384", USER_ACCESSIBLE);
        defineParameter("samplingRate", "16384.0", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "lower bound $title lowerBound TextField 0.0\n";
        guilines += "upper bound $title upperBound TextField 1.0\n";
        guilines += "number of data points $title numberOfPoints TextField 16384\n";
        guilines += "sampling rate $title samplingRate TextField 16384.0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        lowerBound = new Double((String) getParameter("lowerBound")).doubleValue();
        upperBound = new Double((String) getParameter("upperBound")).doubleValue();
        numberOfPoints = new Integer((String) getParameter("numberOfPoints")).intValue();
        samplingRate = new Double((String) getParameter("samplingRate")).doubleValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up GenRandNoise (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("lowerBound")) {
            lowerBound = new Double((String) value).doubleValue();
        }

        if (paramname.equals("upperBound")) {
            upperBound = new Double((String) value).doubleValue();
        }

        if (paramname.equals("numberOfPoints")) {
            numberOfPoints = new Integer((String) value).intValue();
        }

        if (paramname.equals("samplingRate")) {
            samplingRate = new Double((String) value).doubleValue();
        }
    }


    /**
     * @return an array of the input types for GenRandNoise
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for GenRandNoise
     */
    public String[] getOutputTypes() {
        return new String[]{"SampleSet"};
    }

}



