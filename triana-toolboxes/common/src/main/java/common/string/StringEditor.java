package common.string;

import org.trianacode.taskgraph.Unit;

/**
 * Edits a String or multiple strings. If the unit has multiple string inputs then they are appended to the editor.
 *
 * @author Matthew Shields
 * @version $Revision: 2921 $
 */
public class StringEditor extends Unit {

    // parameter data type definitions
    private String inputStr;
    static final String inputParamName = "INPUT_STRING";
    static final String outputParamName = "OUTPUT_STRING";
    private boolean edited;

    /**
     * This is called when the network is forcably stopped by the user. This should be over-ridden with the desired
     * tasks.
     */
    public void stopping() {
        synchronized (this) {
            edited = true;
            this.notifyAll();
        }
    }

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        StringBuffer buff = new StringBuffer();
        Object obj;
        String str;

        for (int i = 0; i < getInputNodeCount(); i++) {
            obj = getInputAtNode(i);

            if (obj instanceof byte[]) {
                str = new String((byte[]) obj) + "\n";
            } else {
                str = obj.toString() + "\n";
            }

            buff.append(str);
        }

        setParameter(outputParamName, "");
        setParameter(inputParamName, buff.toString());
        edited = false;
        showParameterPanel();
        synchronized (this) {
            while (!edited) {
                try {
                    this.wait();
                }
                catch (InterruptedException e) {
                }
            }
        }
        output(inputStr);
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Edits a String, multiple inputs will be concatenated");
        setHelpFileLocation("StringEditor.html");

        // Define initial value and type of parameters
        defineParameter(inputParamName, "", USER_ACCESSIBLE);

        // Initialise custom panel interface
        setParameterPanelClass("common.string.StringEditorPanel");
        setParameterPanelInstantiate(ON_USER_ACCESS);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        inputStr = (String) getParameter(inputParamName);
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals(outputParamName)) {
            inputStr = (String) value;
            synchronized (this) {
                edited = true;
                this.notifyAll();
            }
        }
    }


    /**
     * @return an array of the input types for StringGen
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for StringGen
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}



