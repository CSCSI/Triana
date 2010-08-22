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
package signalproc.output;


import org.trianacode.taskgraph.Unit;
import triana.types.GraphType;
import triana.types.ProcessDoublesInterface;
import triana.types.TrianaType;


/**
 * A JPlotter unit.
 *
 * @author David Churches
 * @version $Revision: 2921 $
 */
public class JPlotter extends Unit implements ProcessDoublesInterface {

    private int counter = 0;

    public void process() throws Exception {
        TrianaType input;
        System.out.println("inside process");

        for (int count = 0; count < getInputNodeCount(); count++) {
            input = (TrianaType) getInputAtNode(count);

            if (input instanceof GraphType) {
                System.out.println("Unit sending parameter JPlotterData_" + count);
                getTask().setParameterType("JPlotterData_" + count, TRANSIENT);
                getTask().setParameter("JPlotterData_" + count, input);
            }
        }
        System.out.println("Unit sending parameter GraphData");
        getTask().setParameterType("GraphData", TRANSIENT);
        getTask().setParameter("GraphData", String.valueOf(counter++));
    }


    /**
     * Initialses information specific to JPlotter.
     */
    public void init() {
        super.init();
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);
        // set these to true if your unit can process double-precision
        // arrays       setRequireDoubleInputs(false);
        defineParameter("requireDoubleInputs", new Boolean(false), INTERNAL);
        defineParameter("canProcessDoubleArrays", new Boolean(false), INTERNAL);
        setParameterPanelClass("signalproc.output.JPlotterPanel");

        setHelpFileLocation("JPlotter.html");
        setPopUpDescription("A graphical-displaying unit for rendering input signals");

    }

    public String[] getInputTypes() {
        return new String[]{"GraphType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"none"};
    }

    public void parameterUpdate(String name, Object value) {
    }

    public boolean getRequireDoubleInputs() {
        return ((Boolean) getParameter("requireDoubleInputs")).booleanValue();
    }

    public boolean getCanProcessDoubleArrays() {
        return ((Boolean) getParameter("canProcessDoubleArrays")).booleanValue();
    }

    public void setRequireDoubleInputs(boolean state) {
        setParameter("requireDoubleInputs", new Boolean(state));
    }

    public void setCanProcessDoubleArrays(boolean state) {
        setParameter("canProcessDoubleArrays", new Boolean(state));
    }


}





