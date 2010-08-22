package signalproc.output;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */


import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.VectorType;


/**
 * Shows database events on a text area
 *
 * @author David Churches
 * @version $Revision: 2921 $
 */


public class EventViewer extends Unit {

    // parameter data type definitions
    private String textString;
    private String finalString;
    private int firstIndex, secondIndex, thirdIndex, length;
    private boolean stop;
    private int i;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {


        for (int count = 0; count < getInputNodeCount(); count++) {
            System.out.println("In Unit, count= " + count);

            if (getTask().getDataInputNode(count).isConnected()) {

                Object input = (Object) getInputAtNode(count);

                if (input instanceof VectorType) {
                    System.out.println("VectorType input");
                    VectorType data = (VectorType) input;
                    length = data.length();
                    System.out.println("In Unit, length= " + length);
                    for (int i = 0; i < length; ++i) { // loop over rows in query
                        getTask().setParameterType("substitution_" + count + i, Task.TRANSIENT);
                        getTask().setParameter("substitution_" + count + i, String.valueOf(data.getData()[i]));
                    }

                } else if (input instanceof String[]) {
                    System.out.println("String[] input");
                    String[] data = (String[]) input;
                    length = data.length;
                    System.out.println("In Unit, length= " + length);
                    for (int i = 0; i < length; ++i) { // loop over rows in query
                        getTask().setParameterType("substitution_" + count + i, Task.TRANSIENT);
                        getTask().setParameter("substitution_" + count + i, String.valueOf(data[i]));
                    }
                } else {
                }
            }
        }

        for (int i = 0; i < length; ++i) {
            firstIndex = 0;
            finalString = "";
            for (int count = 0; count < getInputNodeCount(); count++) {
                String searchString = "$" + count;
                secondIndex = textString.indexOf(searchString);
                thirdIndex = secondIndex + 2;
                finalString += textString.substring(firstIndex, secondIndex) + (String) getTask()
                        .getParameter("substitution_" + count + i);
                firstIndex = thirdIndex;
            }
            finalString += textString.substring(thirdIndex) + "....\n\n";

            System.out.println("In Unit, finalString= " + finalString);
            getTask().setParameter("textAreaString", finalString);
        }
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

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Shows database events on a text area");
        setHelpFileLocation("EventViewer.html");

        // Define initial value and type of parameters
        defineParameter("textString", "", USER_ACCESSIBLE);

        setParameterPanelClass("signalproc.output.EventViewerPanel");
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        textString = (String) getParameter("textString");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up EventViewer (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("textString")) {
            textString = (String) value;
            System.out.println("Unit recieved textString as " + textString);
        }
    }


    /**
     * @return an array of the input types for EventViewer
     */
    public String[] getInputTypes() {
        return new String[]{"VectorType", "java.lang.String[]"};
    }

    /**
     * @return an array of the output types for EventViewer
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }

}



