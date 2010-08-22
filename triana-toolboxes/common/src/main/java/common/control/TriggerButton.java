package common.control;


import org.trianacode.taskgraph.Unit;


/**
 * Trigger when the user presses the button
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class TriggerButton extends Unit {

    // parameter data type definitions
    private String buttonevent;
    private String lastevent;
    private int triggercount;

    private Thread thread;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        thread = Thread.currentThread();

        triggercount = 0;
        setParameter("triggercount", String.valueOf(triggercount));

        buttonevent = "";
        lastevent = "";

        while (!lastevent.equals("STOP")) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException except) {
            }

            if ((!buttonevent.equals(lastevent)) && (!buttonevent.equals("STOP"))) {
                output(String.valueOf(triggercount++));
                setParameter("triggercount", String.valueOf(triggercount));
            }

            lastevent = buttonevent;
        }

        thread = null;
        setParameter("triggercount", "-1");
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

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("Trigger when the user presses the button");
        setHelpFileLocation("TriggerButton.html");

        // Define initial value and type of parameters
        defineParameter("buttonevent", "", TRANSIENT);
        defineParameter("triggercount", "-1", TRANSIENT);

        // Initialise custom panel interface
        setParameterPanelClass("common.control.TriggerButtonPanel");
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        buttonevent = (String) getParameter("buttonevent");
        triggercount = new Integer((String) getParameter("triggercount")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up TriggerButton (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("buttonevent")) {
            buttonevent = (String) value;

            if (thread != null) {
                thread.interrupt();
            }
        }

        if (paramname.equals("triggercount")) {
            triggercount = new Integer((String) value).intValue();
        }
    }


    public void stopping() {
        buttonevent = "STOP";

        if (thread != null) {
            thread.interrupt();

            while ((thread != null) && thread.isInterrupted()) {
                Thread.yield();
            }
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
        return new String[]{};
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
        return new String[]{"java.lang.Integer"};
    }

}



