package common.conzt;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSampleSet;
import triana.types.ComplexSpectrum;
import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
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
public class ImagPart extends Unit {

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
        //setOutputType(output.getClass());
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

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
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
    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ImagPart.html";
    }
}















