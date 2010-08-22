package signalproc.converters;

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


import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.VectorType;


/**
 * Outputs a single element of a Vector as a Const
 *
 * @author David Churches
 * @version $Revision: 2921 $
 */


public class VectToConst extends Unit {

    // parameter data type definitions
    private int element;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        VectorType input = (VectorType) getInputAtNode(0);
        output(new Const(input.getData()[element]));
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

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Outputs a single element of a Vector as a Const");
        setHelpFileLocation("converters.html");

        // Define initial value and type of parameters
        defineParameter("element", "0", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Element Index $title element TextField 0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        element = new Integer((String) getParameter("element")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up VectToConst (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("element")) {
            element = new Integer((String) value).intValue();
        }
    }


    /**
     * @return an array of the input types for VectToConst
     */
    public String[] getInputTypes() {
        return new String[]{"VectorType"};
    }

    /**
     * @return an array of the output types for VectToConst
     */
    public String[] getOutputTypes() {
        return new String[]{"Const"};
    }

}



