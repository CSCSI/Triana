package common.output;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;


/**
 * Outputs the history of a data item as a taskgraph
 *
 * @author Ian Wang
 * @version $Revision $
 */


public class HistoryWriter extends Unit {

    // parameter data type definitions
    private String filename;


    /*                                
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        notifyError("History writer is not working, please contact Triana team");
        /*  Object input = getInputAtNode(0);
     HistoryClipIn history = (HistoryClipIn) getClipIn(input, HistoryClipIn.DEFAULT_NAME);
     removeClipIn(HistoryClipIn.DEFAULT_NAME);

     if (history != null) {
         XMLWriter xmlwriter = new XMLWriter(new FileWriter(filename));
         xmlwriter.writeComponent(history.getHistory());
         xmlwriter.close();
     }   */
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
        setParameterUpdatePolicy(Task.PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Outputs the history of a data item as a taskgraph");
        setHelpFileLocation("HistoryWriter.html");

        // Initialise task parameters with default values (if not already initialised)
        if (!isParameter("filename")) {
            setParameter("filename", "");
        }

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "filename $title filename File null *.xml\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        filename = (String) getParameter("filename");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up HistoryWriter (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("filename")) {
            filename = (String) value;
        }
    }


    /**
     * @return an array of the input types for HistoryWriter
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for HistoryWriter
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }

}



