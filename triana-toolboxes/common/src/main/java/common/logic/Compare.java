package common.logic;

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
import triana.types.Const;
import triana.types.OldUnit;
import triana.types.VectorType;

/**
 * A Compare unit test two VectorType inputs in a way chosen by the user in the parameter window: =, >, >=, <, <=, !=.
 * The output is a Const that is 1 if the test is passed and 0 if it fails. To pass the test, all elements of the two
 * sets must pass the test. The two inputs must be compatible. They may be real or complex, but if they are complex only
 * the tests = and !- are allowed.
 *
 * @author B.F. Schutz
 * @version 2.0 20 August 2000
 */
public class Compare extends OldUnit {

    /**
     * The UnitWindow for Compare
     */
//    TextFieldWindow myWindow;

    /**
     * Comparison operator parameter
     */
    String operator = "=";


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Tests inputs on =, >, etc.";
    }

    /**
     * ********************************************* ** USER CODE of Compare goes here    ***
     * *********************************************
     */
    public void process() {
        Object input, input2;
        Const output;
        double[] inputdata = null;
        double[] inputdata2 = null;
        double[] inputdatai = null;
        double[] inputdata2i = null;
        boolean complex = false;

        input = getInputAtNode(0);
        input2 = getInputAtNode(1);

        System.out.println("Compare run");

        if (input instanceof VectorType) {
            if (!((VectorType) input).isCompatible(input2)) {
                new ErrorDialog(null, "Inputs to " + getName() + " are not compatible. Execution fails.");
                stop();
                return;
            }

            inputdata = ((VectorType) input).getDataReal();
            inputdata2 = ((VectorType) input2).getDataReal();
            if (((VectorType) input).isDependentComplex(0)) {
                complex = true;
                inputdatai = ((VectorType) input).getDataImag();
                inputdata2i = ((VectorType) input2).getDataImag();
            }

        } else if (input instanceof Const) {
            inputdata = new double[1];
            inputdata2 = new double[1];
            inputdata[0] = ((Const) input).getReal();
            inputdata2[0] = ((Const) input2).getReal();

            System.out.println("Compare " + inputdata[0] + " " + operator + " " + inputdata2[0]);
            if (((Const) input).isComplex()) {
                complex = true;
                inputdatai = new double[1];
                inputdata2i = new double[1];
                inputdatai[0] = ((Const) input).getImag();
                inputdata2i[0] = ((Const) input2).getImag();
            }
        }

        boolean comparison = true;
        int i;
        int numberOfData = inputdata.length;

        if (operator.equals("=")) {
            for (i = 0; i < numberOfData; i++) {
                if (inputdata[i] != inputdata2[i]) {
                    comparison = false;
                    break;
                }
                if (complex) {
                    if (inputdatai[i] != inputdata2i[i]) {
                        comparison = false;
                        break;
                    }
                }
            }
        } else if (operator.equals(">")) {
            if (complex) {
                new ErrorDialog(null, "Cannot Compare complex data for inequality!!! ");
                stop();
                return;
            }
            for (i = 0; i < numberOfData; i++) {
                if (inputdata[i] <= inputdata2[i]) {
                    comparison = false;
                    break;
                }
            }
        } else if (operator.equals(">=")) {
            if (complex) {
                new ErrorDialog(null, "Cannot Compare complex data for inequality!!! ");
                stop();
                return;
            }
            for (i = 0; i < numberOfData; i++) {
                if (inputdata[i] < inputdata2[i]) {
                    comparison = false;
                    break;
                }
            }
        } else if (operator.equals("<")) {
            if (complex) {
                new ErrorDialog(null, "Cannot Compare complex data for inequality!!! ");
                stop();
                return;
            }
            for (i = 0; i < numberOfData; i++) {
                if (inputdata[i] >= inputdata2[i]) {
                    comparison = false;
                    break;
                }
            }
        } else if (operator.equals("<=")) {
            if (complex) {
                new ErrorDialog(null, "Cannot Compare complex data for inequality!!! ");
                stop();
                return;
            }
            for (i = 0; i < numberOfData; i++) {
                if (inputdata[i] > inputdata2[i]) {
                    comparison = false;
                    break;
                }
            }
        }

        if (operator.equals("!=")) {
            comparison = false;
            for (i = 0; i < numberOfData; i++) {
                if (inputdata[i] != inputdata2[i]) {
                    comparison = true;
                    break;
                }
                if (complex) {
                    if (inputdatai[i] != inputdata2i[i]) {
                        comparison = true;
                        break;
                    }
                }
            }
        }


        if (comparison) {
            output(new Const(1.0));
        } else {
            output(new Const(0.0));
        }
    }


    /**
     * Initialses information specific to Compare.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

        // Initialise task parameters with default values (if not already initialised)
        Task task = getTask();

        if (!task.isParameterName("operator")) {
            task.setParameter("operator", "=");
        }

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Comparison operator $title operator Choice [=] [>] [>=] [<] [<=] [!=]\n";
        setGUIBuilderV2Info(guilines);

//        String[] inputNames = new String[1];
//        inputNames[0] = "Enter comparison operator: =, >, >=, <, <=, != ";

//        myWindow = new TextFieldWindow(this, 1, 40, inputNames);
//        myWindow.setContents(0, "=");
    }

    /**
     * Resets Compare
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Compare's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter("operator", operator);
    }

    /**
     * Loads Compare's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
        if (name.equals("operator")) {
            operator = value;
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to Compare, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "VectorType Const";
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String outputTypes() {
        return "Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Compare.html";
    }


    /**
     * @return parameter window sp that triana
     * can move and display it.
     */
    //public Window getParameterWIndow() {
    //    return myWindow;
    //    }


}

















