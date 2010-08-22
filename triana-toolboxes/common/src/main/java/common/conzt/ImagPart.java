package common.conzt;

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
 * ImagPart takes a complex input and outputs just the imaginary part as a <i>real</i> data object. If a ComplexSpectrum
 * is input then a Spectrum type is output and if a ComplexSampleSet is input then a SampleSet is output. For any other
 * GraphType the output data type is a similar type; the imaginary part of the input is placed into the real part of the
 * output and the imaginary part of the output is set to <i>null</i>. For Const, a new purely real Const is output,
 * containing only the imaginary part of the input. </p><p> This version conforms to Triana Type 2 data type
 * conventions.
 *
 * @author Ian Taylor, B. F. Schutz
 * @version 2.1 13 January 2001
 */
public class ImagPart extends OldUnit {

    public void process() {
        Object input;
        Object output = null;

        input = getInputAtNode(0);

        if (input instanceof EmptyingType) {
            return;
        }
        if (input instanceof ComplexSpectrum) {
            ComplexSpectrum s = (ComplexSpectrum) input;
            output = new Spectrum(s.isTwoSided(), s.isNarrow(),
                    s.getDataImag(), s.getOriginalN(), s.getFrequencyResolution(),
                    s.getUpperFrequencyBound());
        } else if (input instanceof ComplexSampleSet) {
            ComplexSampleSet s = (ComplexSampleSet) input;
            output = new SampleSet(s.getSamplingRate(), s.getDataImag(), s.getAcquisitionTime());
            if (s.getXTriplet() != null) {
                ((SampleSet) output).setX(s.getXTriplet());
            } else {
                ((SampleSet) output).setX(s.getXArray());
            }
        } else if (input instanceof GraphType) {
            GraphType s = (GraphType) input;
            output = s;
            for (int dv = 0; dv < s.getDependentVariables(); dv++) {
                if (s.isArithmeticArray(dv)) {
                    if (s.isDependentComplex(dv)) {
                        ((GraphType) output).setDataArrayReal(s.getDataArrayImag(dv), dv);
                    } else {
                        FlatArray.initializeArray(s.getDataArrayReal(dv));
                    }
                }
            }
        } else if (input instanceof Const) {
            Const s = (Const) input;
            output = s;
            s.setComplex(s.getImag(), 0.0);
        }
        setOutputType(output.getClass());
        output(output);
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Outputs the imaginary part of a set of complex numbers";
    }

    /**
     * Initialses information specific to ImagPart.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * Resets ImagPart
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ImagPart's parameters to the parameter file.
     */
    public void saveParameters() {

    }

    /**
     * Loads ImagPart's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ImagPart, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from ImagPart, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ImagPart.html";
    }
}















