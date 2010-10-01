package common.input;

import java.text.DecimalFormat;
import java.util.Random;

import org.trianacode.taskgraph.Unit;
import triana.types.Document;

/**
 * Create a Condor job submit description file
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */

public class CondorSubmitDescription extends Unit {


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        // Insert main algorithm for CondorSubmitDescription
        Object[][] desc = (Object[][]) getParameter("CondorSubmitDescription");

        Document doc = new Document();
        doc.setFile(System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator")
                + "triana-"
                + new DecimalFormat("00000").format(new Random().nextInt(100000))
                + ".condor");
        for (int i = 0; i < desc.length; i++) {
            if (desc[i][0].toString().startsWith("QUEUE")) {
                System.err.println(desc[i][0].toString() + " => `" + desc[i][1].toString() + "'");
                doc.addLine(desc[i][0].toString() + " " + desc[i][1].toString());
            } else {
                if (!desc[i][1].toString().equals("")) {
                    System.err.println(desc[i][0].toString() + " => `" + desc[i][1].toString() + "'");
                    doc.addLine(desc[i][0].toString() + " = " + desc[i][1].toString());
                }
            }
        }

        output(doc);
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
        setParameterUpdatePolicy(Unit.PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Create a Condor job submit description file");
        setHelpFileLocation("CondorSubmitDescription.html");

        // Initialise custom panel interface
        setParameterPanelClass("common.input.CondorSubmitDescriptionPanel");

        defineParameter("CondorSubmitDescription",
                new Object[][]{
                        {"universe", ""},
                        {"executable", ""},
                        {"requirements", ""},
                        {"rank", ""},
                        {"input", ""},
                        {"output", ""},
                        {"error", ""},
                        {"log", ""},
                        {"arguments", ""},
                        {"initialdir", ""},
                        {"notification", ""},
                        {"notify_user", ""},
                        {"QUEUE", ""},
                },
                Unit.INTERNAL);
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
        // Insert code to clean-up CondorSubmitDescription (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
    }


    /**
     * @return an array of the input types for CondorSubmitDescription
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for CondorSubmitDescription
     */
    public String[] getOutputTypes() {
        return new String[]{"Document"};
    }

}



