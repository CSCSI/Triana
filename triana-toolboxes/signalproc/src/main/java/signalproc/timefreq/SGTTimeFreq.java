package signalproc.timefreq;


import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.MatrixType;


/**
 * A graphical display unit for rendering time-frequency signals
 *
 * @author Rob Davies
 * @version $Revision: 2921 $
 */


public class SGTTimeFreq extends Unit {

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        MatrixType input = (MatrixType) getInputAtNode(0);

        //System.out.println(input.hasParent() + " " + input);
        setParameter(SGTTimeFreqPanel.GRAPH_DATA, input);


// Insert main algorithm for SGTTimeFreq
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
        setParameterUpdatePolicy(Task.IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("A graphical display unit for rendering time-frequency signals");
        setHelpFileLocation("SGTTimeFreq.html");

        defineParameter(SGTTimeFreqPanel.GRAPH_DATA, new triana.types.MatrixType(), TRANSIENT);

        // Initialise custom panel interface
        setParameterPanelClass("signalproc.timefreq.SGTTimeFreqPanel");
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up SGTTimeFreq (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
    }


    /**
     * @return an array of the input types for SGTTimeFreq
     */
    public String[] getInputTypes() {
        return new String[]{"MatrixType"};
    }

    /**
     * @return an array of the output types for SGTTimeFreq
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }

}



