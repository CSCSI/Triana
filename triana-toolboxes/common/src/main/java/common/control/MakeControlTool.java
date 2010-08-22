package common.control;


import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.constants.ControlToolConstants;
import org.trianacode.taskgraph.imp.RenderingHintImp;
import org.trianacode.taskgraph.tool.Tool;


/**
 * Makes a tool into a Control Tool
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class MakeControlTool extends Unit {

    // parameter data type definitions
    private String controlname = "";
    private String description = "";
    private boolean toplevel;
    private boolean standalone;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        Tool input = (Tool) getInputAtNode(0);

        RenderingHintImp hint = new RenderingHintImp(ControlToolConstants.CONTROL_TOOL_RENDERING_HINT, false);
        if (!controlname.equals("")) {
            hint.setRenderingDetail(ControlToolConstants.CONTROL_NAME, controlname);
        }

        hint.setRenderingDetail(ControlToolConstants.DESCRIPTION, description);
        hint.setRenderingDetail(ControlToolConstants.VALID_TOP_LEVEL, String.valueOf(toplevel));
        hint.setRenderingDetail(ControlToolConstants.VALID_STANDALONE, String.valueOf(standalone));

        input.addRenderingHint(hint);

        output(input);
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
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(COPY_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("Makes a tool into a Control Tool");
        setHelpFileLocation("MakeControlTool.html");

        // Define initial value and type of parameters
        defineParameter("controlname", "", USER_ACCESSIBLE);
        defineParameter("description", "", USER_ACCESSIBLE);
        defineParameter("toplevel", "true", USER_ACCESSIBLE);
        defineParameter("standalone", "true", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Control Name $title controlname TextField\n";
        guilines += "Description $title description TextField\n";
        guilines += "Valid Top-Level $title toplevel Checkbox true\n";
        guilines += "Valid Standalone $title standalone Checkbox true\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        controlname = (String) getParameter("controlname");
        description = (String) getParameter("description");
        toplevel = new Boolean((String) getParameter("toplevel")).booleanValue();
        standalone = new Boolean((String) getParameter("standalone")).booleanValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up MakeControlTool (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("controlname")) {
            controlname = (String) value;
        }

        if (paramname.equals("description")) {
            description = (String) value;
        }

        if (paramname.equals("toplevel")) {
            toplevel = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("standalone")) {
            standalone = new Boolean((String) value).booleanValue();
        }
    }


    /**
     * @return an array of the types accepted by each input node. For node indexes not covered the types specified by
     *         getInputTypes() are assumed.
     */
    public String[][] getNodeInputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types accepted by nodes not covered by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return new String[]{"triana.taskgraph.Tool"};
    }


    /**
     * @return an array of the types output by each output node. For node indexes not covered the types specified by
     *         getOutputTypes() are assumed.
     */
    public String[][] getNodeOutputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types output by nodes not covered by getNodeOutputTypes().
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.taskgraph.Tool"};
    }

}



