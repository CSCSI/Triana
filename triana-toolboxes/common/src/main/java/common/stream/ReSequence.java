package common.stream;


import java.util.ArrayList;
import java.util.Hashtable;

import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.clipin.ClipInStore;
import triana.types.clipins.SequenceClipIn;


/**
 * Returns a stream into sequence order
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class ReSequence extends Unit {

    private static String BUFFER_PLACE_HOLDER = "##BUFFER_PLACE_HOLDER##";
    private static String LAST_PLACE_HOLDER = "##LAST_PLACE_HOLDER##";

    // parameter data type definitions
    private boolean restore;


    // a hashtable of buffer lists for each group, keyed by groupid
    private Hashtable grouptable = new Hashtable();

    // an array list indicating the output sequence of groups
    private ArrayList grouporder = new ArrayList();

    // a ptr to the current position in the current group
    private int curptr = 0;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        getData();
        outputData();
    }

    /**
     * Poll each input for data and add to buffer.
     */
    private void getData() {
        Object data;

        for (int count = 0; count < getInputNodeCount(); count++) {
            if (isInputAtNode(count)) {
                data = getInputAtNode(count);

                if (!isClipInName(SequenceClipIn.SEQUENCE_CLIPIN_TAG)) {
                    notifyError("Error Resequencing: No sequence information set");
                } else {
                    SequenceClipIn clipin = (SequenceClipIn) getClipIn(SequenceClipIn.SEQUENCE_CLIPIN_TAG);
                    String gid = clipin.getGroupID();

                    if (!grouptable.containsKey(gid)) {
                        grouptable.put(gid, new ArrayList());
                        grouporder.add(gid);
                    }

                    ArrayList buffer = (ArrayList) grouptable.get(gid);

                    if (grouporder.indexOf(gid) == 0) {
                        while (buffer.size() <= clipin.getIndex() - curptr) {
                            buffer.add(BUFFER_PLACE_HOLDER);
                        }

                        buffer.set(clipin.getIndex() - curptr, new DataHolder(data, extractClipInState()));
                    } else {
                        while (buffer.size() <= clipin.getIndex()) {
                            buffer.add(BUFFER_PLACE_HOLDER);
                        }

                        buffer.set(clipin.getIndex(), new DataHolder(data, extractClipInState()));
                    }

                    if (clipin.isLastInSequence() || (clipin.getIndex() == clipin.getLength())) {
                        buffer.add(LAST_PLACE_HOLDER);
                    }
                }
            }
        }

    }

    private void outputData() {
        String curgroup;
        ArrayList buffer;
        Object obj;

        do {
            if (grouporder.isEmpty()) {
                obj = BUFFER_PLACE_HOLDER;
            } else {
                curgroup = (String) grouporder.get(0);
                buffer = (ArrayList) grouptable.get(curgroup);

                do {
                    if (buffer.isEmpty()) {
                        obj = BUFFER_PLACE_HOLDER;
                    } else {
                        obj = buffer.get(0);
                    }

                    if (obj instanceof DataHolder) {
                        restoreClipInState(((DataHolder) obj).getClipIns());

                        if (restore) {
                            restoreSubClipIn();
                        }

                        output(((DataHolder) obj).getData());

                        buffer.remove(0);
                        curptr++;
                    }
                } while (obj instanceof DataHolder);

                if (obj == LAST_PLACE_HOLDER) {
                    grouporder.remove(0);
                    grouptable.remove(curgroup);
                    curptr = 0;
                }
            }
        } while (obj != BUFFER_PLACE_HOLDER);
    }

    private void restoreSubClipIn() {
        Object seqclip = removeClipIn(SequenceClipIn.SEQUENCE_CLIPIN_TAG);

        if (seqclip != null) {
            seqclip = ((SequenceClipIn) seqclip).getSubSequenceClipIn();

            if (seqclip != null) {
                putClipIn(SequenceClipIn.SEQUENCE_CLIPIN_TAG, seqclip);
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
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);
        setDefaultNodeRequirement(OPTIONAL);

        // Initialise pop-up description and help file location
        setPopUpDescription("Returns a stream into sequence order");
        setHelpFileLocation("ReSequence.html");

        // Define initial value and type of parameters
        defineParameter("restore", "true", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Restore SubSequence $title restore Checkbox false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        restore = new Boolean((String) getParameter("restore")).booleanValue();

        grouporder.clear();
        grouptable.clear();
        curptr = 0;
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up ReSequence (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("restore")) {
            restore = new Boolean((String) value).booleanValue();
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
        return new String[]{"java.lang.Object"};
    }


    private static class DataHolder {

        private Object data;
        private ClipInStore clipins;

        public DataHolder(Object data, ClipInStore clipins) {
            this.data = data;
            this.clipins = clipins;
        }

        public Object getData() {
            return data;
        }

        public ClipInStore getClipIns() {
            return clipins;
        }

    }

}



