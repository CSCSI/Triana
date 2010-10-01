package math.calculator;

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


import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.GraphType;
import triana.types.TrianaType;
import triana.types.VectorType;
import triana.types.util.FlatArray;
import triana.types.util.Str;
import triana.types.util.StringSplitter;

/**
 * A MathCalc unit to provide an interface to the Compute software
 *
 * @author Ian Taylor
 * @author Bernard Schutz
 * @version 1.1 13 January 2001
 */
public class MathCalc extends Unit implements ComputeManager {

//    public DebugWindow debug = null;

    /**
     * The UnitPanel for MathCalc
     */
//    MathCalcPanel myPanel;

    ComputeCalc c;
    boolean showopt = false;
    String expression = "";

    /**
     * First non-Const type among the possible multiple inputs to the unit. The rest are checked to be compatible in
     * type and size to this. If the computation results in a vector output, then the output data replace the first
     * arithmetic data set of this data object; the rest of the data and parameters are passed to the output unchanged.
     * This pointer is reset to null after each call to process().
     */
    Object firstType = null;

    /**
     * The unit will only operate on the first arithmetic data set in the input. This is the index of the one that was
     * found.
     */
    int dvReturned = 0;

    /**
     * The flattened array of the data passed to Compute.
     */
    FlatArray dvFlattened = null;


    public String getPopUpDescription() {
        return "A unit to apply any mathematical expression to an input or self-generated data sets.";
    }


    public String defineVariable(String undefinedVariable) {
        return null;
    }


    /**
     * ********************************************* ** USER CODE of MathCalc goes here    ***
     * *********************************************
     * <p/>
     * MathCalc is unlike most other Units in that it does not itself directly initiate the input of the data. Instead,
     * the user types an expression into the parameter window. When the user types "Return" the expression is sent to
     * Compute, which parses it and decides what the output type will be and whether input data is needed. (This is
     * indicated by the presence in the expression of identifiers for data from input nodes of the form #0c and #1s
     * etc.) Method process() asks Compute for the answer, and this initiates the actual computation, during which, if
     * data is needed, Compute calls method getInputData below. When the expression has been evaluated it is returned to
     * method process() so that process() can do the output.
     */
    public void process() {
        boolean matchedType = false;
        GraphType output;

        if (c.endType == Compute.SCALAR) { // easy for a constant out
            try {
                output(new Const(c.returnScalarResult()));
                matchedType = true;
            } catch (ComputeExpressionException ee) {
                ErrorDialog.show(ee);
            }
        } else {

            double ans[] = null;

            try {
                ans = c.returnVectorResult();
            } catch (Exception ee) {
                ErrorDialog.show(ee);
                return;
            }

            if (firstType == null) { // i.e. only null if nothing is input
                output(new VectorType(ans)); // output a safe! raw data
                matchedType = true;
            } else {
                output = (GraphType) firstType;
                dvFlattened.setFlatArray(ans);
                output.setDataArrayReal(dvFlattened.restoreArray(true), dvReturned);
                output(output);
                matchedType = true;
            }
        }

        firstType = null; // reset type

        if (!matchedType) {
            ErrorDialog.show(null, "Incompatible Types in " + getTask().getToolName());
            stop();
        }
    }


    /**
     * Gets the data from the <i>i</i>th input node from the unit which implements this interface.  A double array is
     * returned containing the data. If the length of the data is one then the input data must have been a scaler value,
     * otherwise an array of values is returned.
     */
    public double[] getInputData(int i) {

        Object g = getInputAtNode(i);

        if ((g == TrianaType.NOT_CONNECTED) || (g == TrianaType.NOT_READY)) {
            return null;
        }

        if (firstType == null) {
            if (!(g instanceof Const)) {
                firstType = g;
            }
            //setOutputType(g.getClass()); // set output anyway
        }

        if (g instanceof Const) {
            double d[] = new double[1];
            d[0] = ((Const) g).getReal();
            return d;
        }

        // firstType must be set by now so :-

        if (firstType instanceof GraphType) {
            if (!(g instanceof GraphType)) {
                new ErrorDialog(null, "MathCalc: input not Const or GraphType");
                return null;
            }
            if (!(((GraphType) g).isCompatible(firstType))) {
                new ErrorDialog(null, "MathCalc: input at node number " + String.valueOf(i)
                        + " not compatible with GraphType input(s) at previous node(s).");
                return null;
            }
            for (int dv = 0; dv < ((GraphType) g).getDependentVariables(); dv++) {
                if (((GraphType) g).isArithmeticData(dv)) {
                    dvReturned = dv;
                    dvFlattened = new FlatArray(((GraphType) g).getDataArrayReal(dv));
                    return (double[]) FlatArray.toDoubleArray(dvFlattened.getFlatArray());
                }
            }

            new ErrorDialog(null, "MathCalc: Input data has no arithmetic contents; method fails.");
        }
        return null;
    }


    /**
     * Prints the text to the debug stream for the unit followed by a line feed.  This function just returns if there is
     * no debug window.
     */
    public void println(String text) {
/*        if (debug == null)
            System.out.println(text);
        else
            debug.println(text); */
    }

    /**
     * Prints the text to the debug stream for the unit.  This function just returns if there is no debug window.
     */
    public void print(String text) {
/*        if (debug == null)
            System.out.print(text);
        else
            debug.print(text); */
    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    /**
     * Initialses information specific to MathCalc.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        Task task = getTask();

        if (!task.isParameterName("expression")) {
            task.setParameter("expression", "");
        }

        if (!task.isParameterName("optimise")) {
            task.setParameter("optimise", "true");
        }

        if (!task.isParameterName("optimised")) {
            task.setParameter("optimised", "N/A");
        }

        c = new ComputeCalc(this);

        setParameterUpdatePolicy(IMMEDIATE_UPDATE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Expression $title expression TextField\n";
        guilines += "Optimised expression $title optimised Label true\n";
        guilines += "Optimise $title optimise Checkbox true\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Reset's MathCalc
     */
    public void reset() {
        super.reset();
    }


    /**
     * Loads MathCalc's parameters.
     */
    public void setParameter(String name, String value) {
        if (name.equals("expression")) {
            c.setExpression(value);
            expression = value;
            if (!value.equals("")) {
                try {
                    c.parse();
                } catch (Exception ee) {
                    String tokens[] = new String[2];
                    tokens[0] = ".";
                    tokens[1] = ":";
                    StringSplitter s = new StringSplitter(ee.getMessage(), tokens, false);
                    String mes = "";
                    for (int i = 0; i < s.size(); ++i) {
                        mes += s.at(i) + "\n";
                    }
                    ErrorDialog.show(mes);
                }
            }

        }
        if (name.equals("debug")) {
            c.displayProgress = Str.strToBoolean(value);
        }
        if (name.equals("optimise")) {
            c.optimise = Str.strToBoolean(value);
        }

        if (name.equals("optimise") || name.equals("expression")) {
            if ((c.optimise) && (!value.equals(""))) {
                getTask().setParameter("optimised", c.getOptimisedString());
            } else {
                getTask().setParameter("optimised", "N/A");
            }
        }
    }


/*    public void updateWidgetFor(String name) {
        if (name.equals("expression"))
            myPanel.expression.setText(expression);
        if (name.equals("debug"))
            myPanel.debug.setSelected(c.displayProgress);
        if (name.equals("optimise"))
            myPanel.optimise.setSelected(c.optimise);
        if (name.equals("showOpt")) {
            myPanel.showOptimisedExpression.setSelected(showopt);
            myPanel.layoutPanel();
        }
    } */

    /**
     * @return a string containing the names of the types allowed to be input to MathCalc, each separated by a white
     *         space.
     */
    
    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "MathCalc.html";
    }


    /**
     * @return MathCalc's parameter window sp that Triana
     * can move and display it.
     */
/*    public UnitPanel getParameterPanel() {
        return myPanel;
    } */


    /**
     * for the checkboxes
     */
/*    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == myPanel.debug) {
            if (debug == null) {
                debug = new DebugWindow("Debug Panel for " + getName());
                Display.centralise(debug);
            }

            if (myPanel.debug.isSelected())
                debug.setVisible(true);
            else
                debug.setVisible(false);
        }
    } */

}

















