package math.functions;

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
import triana.types.Arithmetic;
import triana.types.Const;

/**
 * A Adder unit to subtract, with possible scaling, from the data from the first input node, the data from all the
 * remaining input nodes.
 * <p/>
 * This OldUnit obeys the conventions of Triana Type 2 data types.
 *
 * @author ian
 * @version 2.0 10 August 2000
 */
public class Subtracter extends Unit {

    // some examples of parameters

    public double scaler = 1.0;

    /**
     * Initialses information specific to Subtracter.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(2);
        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
        setMinimumOutputNodes(0);

        setHelpFileLocation("Subtracter.html");

        setGUIBuilderV2Info("Scaler value $title scaler Scroller 0 10 1");
    }


    public String[] getInputTypes() {
        return new String[]{"java.lang.Number",
                "triana.types.Arithmetic",
                "triana.types.GraphType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"java.lang.Number",
                "triana.types.Arithmetic",
                "triana.types.GraphType"};
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Adds all the inputs together";
    }

    /**
     * The main functionality of Subtracter goes here
     */
    public void process() {
        Object nextInput;
        Object result = getInputAtNode(0);

        for (int i = 1; i < getInputNodeCount(); ++i) {
            nextInput = getInputAtNode(i);

            if (result instanceof Arithmetic) {
                if (((Arithmetic) result).isCompatible(nextInput)) {
                    result = ((Arithmetic) result).subtract(nextInput);
                } else {
                    notifyError("Incompatible data sets " + result.getClass().getName() + "/" + nextInput.getClass()
                            .getName());
                }
            } else if ((result instanceof Number) && (nextInput instanceof Number)) {
                result = new Double(((Number) result).doubleValue() - ((Number) nextInput).doubleValue());
            } else {
                notifyError(
                        "Incompatible data sets " + result.getClass().getName() + "/" + nextInput.getClass().getName());
            }
        }

        if (scaler != 1.0) {
            if (result instanceof Arithmetic) {
                result = ((Arithmetic) result).multiply(new Const(scaler));
            } else {
                result = new Double(((Number) result).doubleValue() * scaler);
            }
        }

        output(result);
    }


    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("scaler")) {
            scaler = new Double((String) value).doubleValue();
        }
    }

}
