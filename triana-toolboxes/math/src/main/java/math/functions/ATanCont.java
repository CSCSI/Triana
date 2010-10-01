package math.functions;

import java.awt.Window;
import java.awt.event.ActionEvent;

import org.trianacode.gui.windows.ScrollerWindow;
import org.trianacode.taskgraph.Unit;
import triana.types.EmptyingType;
import triana.types.Histogram;
import triana.types.SampleSet;
import triana.types.Spectrum;
import triana.types.VectorType;
import triana.types.util.Str;

/**
 * A ATanCont unit to find the angle of a vector (or complex number) from two input sequences that represent the x and y
 * components of the vector, attempting to resolve ambiguities about the multiple of 2 Pi in the angle by maintaining
 * continuity.
 *
 * @author B.F. Schutz
 * @version 1.0 alpha 04 Oct 1997
 */
public class ATanCont extends Unit {
    /**
     * The UnitWindow for ATanCont
     */
    ScrollerWindow myWindow;

    /**
     * Initial angle parameter
     */
    double phase = 0.0;

    /**
     * A useful constant
     */
    double TwoPi = 2.0 * Math.PI;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Finds the angle of a vector (or complex number) from two input sequences";
    }

    /**
     * ********************************************* ** USER CODE of ATanCont goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, input2;
        double[] inputdataX = {0.0};
        double[] inputdataY = {0.0};

        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        Class inputClass = input.getClass();
        //setOutputType(inputClass);
        input2 = getInputAtNode(1);

        if (input instanceof VectorType) {
            inputdataX = ((VectorType) input).getData();
            inputdataY = ((VectorType) input2).getData();
        } else if (input instanceof SampleSet) {
            inputdataX = ((SampleSet) input).data;
            inputdataY = ((SampleSet) input2).data;
        } else if (input instanceof Spectrum) {
            inputdataX = ((Spectrum) input).data;
            inputdataY = ((Spectrum) input2).data;
        } else if (input instanceof Histogram) {
            inputdataX = ((Histogram) input).data;
            inputdataY = ((Histogram) input2).data;
        }


        int sizeOfData = inputdataX.length;

        double[] outputdata = inputdataX; // IT, for effieciency new double[sizeOfData];
        double lastAngle = Math.atan2(inputdataX[0], inputdataY[0]);
        double thisAngle, diffAngle;
        double jump = phase;

        for (int i = 1; i < sizeOfData; i++) {
            thisAngle = Math.atan2(inputdataX[i], inputdataY[i]);
            diffAngle = thisAngle - lastAngle;
            if (diffAngle > Math.PI) {
                jump = jump - TwoPi;
            } else if (diffAngle < -Math.PI) {
                jump = jump + TwoPi;
            }
            outputdata[i] = thisAngle + jump;
            lastAngle = thisAngle;
        }

        if (input instanceof VectorType) {
            VectorType output = new VectorType(outputdata);
            output(output);
        } else if (input instanceof SampleSet) {
            SampleSet output = new SampleSet(
                    ((SampleSet) input).samplingFrequency(), outputdata);
            output(output);
        } else if (input instanceof Spectrum) {
            Spectrum output = new Spectrum(
                    ((Spectrum) input).samplingFrequency(), outputdata);
            output(output);
        } else if (input instanceof Histogram) {
            Histogram output = new Histogram(
                    ((Histogram) input).binLabel,
                    ((Histogram) input).hLabel,
                    ((Histogram) input).delimiters, outputdata);
            output(output);
        }


    }


    /**
     * Initialses information specific to ATanCont.
     */
    public void init() {
        super.init();

//        changeInputNodes(2);
//        setResizableInputs(false);
//        setResizableOutputs(true);
//        // This is to ensure that we receive arrays containing double-precision numbers
//        setRequireDoubleInputs(true);
//        setCanProcessDoubleArrays(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
                               
        myWindow = new ScrollerWindow(this, "Initial angle in radians added to output");
        myWindow.setValues(0.0, 2.0 * Math.PI, phase);

    }

    /**
     * Reset's ATanCont
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ATanCont's parameters to the parameter file.
     */
//    public void saveParameters() {
//        saveParameter("phase", phase);
//    }

    /**
     * Loads ATanCont's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
        phase = Str.strToDouble(value);
    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType", "triana.types.SampleSet", "triana.types.Spectrum", "triana.types.Histogram"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType", "triana.types.SampleSet", "triana.types.Spectrum", "triana.types.Histogram"};
    }


    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ATanCont.html";
    }


    /**
     * @return ATanCont's parameter window sp that Triana can move and display it.
     */
    public Window getParameterWindow() {
        return myWindow;
    }


    /**
     * Captures the events thrown out by ATanCont.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//
//        //if (e.getSource() == myWindow.slider) {
//        //    phase = myWindow.getValue();
//        //    }
//    }
}



















