package common.processing;

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
import triana.types.ComplexSampleSet;
import triana.types.ComplexSpectrum;
import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.SampleSet;
import triana.types.Spectrum;
import triana.types.util.FlatArray;

/**
 * A Complexify unit to join two input data sets into a single complex set.  The (real part of the) first input is the
 * real part of the output, and the (real part of the) second input is the imaginary part of the output. The two data
 * sets must be compatible. If they are SampleSets, then the output is a ComplexSampleSet; if they are Spectrums then
 * the output is a ComplexSpectrum. For any other GraphType the data type is copied to a similar type. For a Const a new
 * complex Const is output. If the inputs have imaginary parts, these are ignored. If the second input node is absent,
 * or if there is no data at that node, then a complex output is created with zero imaginary part. If there is no data
 * at the first input node, then a complex output is created with zero real part. If there is no data at either node,
 * then the unit fails.
 * <p/>
 * This version conforms to Triana Type 2 data type conventions.
 *
 * @author B.F. Schutz
 * @version 2.1 13 January 2001
 */
public class Complexify extends OldUnit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts the two inputs into a complex output";
    }

    /**
     * ********************************************* ** USER CODE of Complexify goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, input2;
        Object output = null;


        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }

        input2 = getInputAtNode(1);
        if (input2 instanceof EmptyingType) {
            return;
        }


        if (input instanceof GraphType) {

            boolean realOnly = (input2 == null);

            if (!realOnly) {
                if (!(((GraphType) input).isCompatible(input2))) {
                    new ErrorDialog(null, "Input sets to Complexify are not " +
                            "compatible!!! Check their size, number of data sets, and other parameters.");
                    stop();
                    return;
                }
            }

            if (input instanceof SampleSet) {
                output = new ComplexSampleSet((SampleSet) input);
                if (!realOnly) {
                    ((ComplexSampleSet) output).setDataImag(((SampleSet) input2).getDataReal());
                } else {
                    ((ComplexSampleSet) output).setDataImag(new double[((SampleSet) input).size()]);
                }

            } else if (input instanceof Spectrum) {
                output = new ComplexSpectrum((Spectrum) input);
                if (!realOnly) {
                    ((ComplexSpectrum) output).setDataImag(((Spectrum) input2).getDataReal());
                } else {
                    ((ComplexSpectrum) output).setDataImag(new double[((Spectrum) input).size()]);
                }
            } else {
                output = (GraphType) input;
                for (int dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                    if (((GraphType) input).isArithmeticArray(dv)) {
                        if (!realOnly) {
                            ((GraphType) output).setDataArrayImag(((GraphType) input2).getDataArrayReal(dv), dv);
                        } else {
                            ((GraphType) output).setDataArrayImag(
                                    FlatArray.multiArrayImitate(((GraphType) input).getDataArrayReal(dv)), dv);
                        }
                    }
                }
            }
        } else if (input instanceof Const) {
            if (!(input2 instanceof Const)) {
                new ErrorDialog(null, "Input sets to Complexify are not " +
                        "of the same type!!! Check their types.");
                stop();
                return;
            }

            output = (Const) input;
            ((Const) output).setComplex(((Const) input).getReal(), ((Const) input2).getReal());

        }

        setOutputType(output.getClass());
        output(output);

    }


    /**
     * Initialses information specific to Complexify.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(2);
        setResizableInputs(false);
        setResizableOutputs(true);

    }

    /**
     * Resets Complexify
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Complexify's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads Complexify's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Complexify, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from Complexify, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Complexify.html";
    }


}



















