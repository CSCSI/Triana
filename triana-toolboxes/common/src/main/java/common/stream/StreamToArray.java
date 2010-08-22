package common.stream;


import java.lang.reflect.Array;

import org.trianacode.taskgraph.Unit;
import triana.types.clipins.SequenceClipIn;


/**
 * Converts a stream of inputs into a fixed length array
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class StreamToArray extends Unit {

    // parameter data type definitions
    private int arraylength;
    private boolean ignore;

    private Object[] array = new Object[0];
    private int arrayptr = 0;

    private String groupid;
    private String arraytype;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        Object input = (Object) getInputAtNode(0);
        SequenceClipIn clipin = (SequenceClipIn) removeClipIn(SequenceClipIn.SEQUENCE_CLIPIN_TAG);

        if (array == null) {
            Class type = Class.forName(arraytype);

            if (!ignore && (clipin != null)) {
                if (clipin.getLength() == -1) {
                    notifyError(
                            "Array length not specified in sequence info (consider setting 'Ignore Sequence Info')");
                } else if (clipin.getComponentType() != null) {
                    array = (Object[]) Array.newInstance(Class.forName(clipin.getComponentType()), clipin.getLength());
                } else {
                    array = (Object[]) Array.newInstance(type, clipin.getLength());
                }

                groupid = clipin.getGroupID();
            } else {
                if (arraylength <= 0) {
                    notifyError("Invalid array length: " + arraylength);
                } else {
                    array = (Object[]) Array.newInstance(type, arraylength);
                }
            }
        }

        if (array != null) {
            if ((!ignore) && (clipin != null) && (clipin.getIndex() != arrayptr)) {
                notifyError(
                        "Input data not in sequence order (consider using Resequence tool/setting 'Ignore Sequence Info')");
            } else if ((!ignore) && (clipin != null) && (!clipin.getGroupID().equals(groupid))) {
                notifyError(
                        "Input data not in sequence order (consider using Resequence tool/setting 'Ignore Sequence Info')");
            } else if (!array.getClass().getComponentType().isInstance(input)) {
                notifyError("Input data incompatible with array type: " + input.getClass().getName() + "/"
                        + array.getClass().getComponentType().getName() + " (consider setting 'Ignore Sequence Info')");
            } else {
                array[arrayptr++] = input;

                if (arrayptr >= array.length) {
                    output(array);
                    array = null;
                    arrayptr = 0;
                }
            }
        }
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
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("Converts a stream of inputs into a fixed length array");
        setHelpFileLocation("StreamToArray.html");

        // Define initial value and type of parameters
        defineParameter("arraylength", "0", USER_ACCESSIBLE);
        defineParameter("arraytype", "java.lang.Object", USER_ACCESSIBLE);
        defineParameter("ignore", "false", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Ignore Sequence Info $title ignore Checkbox false\n";
        guilines += "Array Length $title arraylength TextField 0\n";
        guilines += "Array Type $title arraytype TextField java.lang.Object\n";
        setGUIBuilderV2Info(guilines);

        reset();
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        arraylength = new Integer((String) getParameter("arraylength")).intValue();
        arraytype = (String) getParameter("arraytype");
        ignore = new Boolean((String) getParameter("ignore")).booleanValue();
        array = null;
        arrayptr = 0;
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        array = null;
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("arraylength")) {
            arraylength = new Integer((String) value).intValue();

            if (ignore) {
                array = null;
                arrayptr = 0;
            }
        }

        if (paramname.equals("arraytype")) {
            arraytype = (String) value;
        }

        if (paramname.equals("ignore")) {
            ignore = new Boolean((String) value).booleanValue();
            array = null;
            arrayptr = 0;
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
        return new String[]{"java.lang.Object"};
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
        return new String[]{"java.lang.Object[]"};
    }

}



