package common.processing;

import org.trianacode.taskgraph.Unit;
import triana.types.Histogramming;
import triana.types.Signal;
import triana.types.Spectral;
import triana.types.VectorType;
import triana.types.util.Str;

/**
 * A Subvector unit to extract a subrange of a VectorType, with bounds chosen in the parameter window. The user can
 * choose whether bounds apply to the index of the dependent variable array or to the values of the independent
 * variable.
 *
 * @author B F Schutz
 * @version 1.0 14 Jun 2001
 */
public class Subvector extends Unit {

    String type = "IndexValue";
    double lower = 0;
    double upper = 100;
    double[] domain, newRange, newDomain;
    int low, high, length;


    /**
     * ********************************************* ** USER CODE of Subvector goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        VectorType input = (VectorType) getInputAtNode(0);
        if (upper <= lower) {
            output(input);
            return;
        }
        /*
       * Find the index values low and high of the ends
       * of the domain of data to be extracted.
       */
        if (type.equals("IndependentVariableValue")) {
            domain = input.getXReal();
            int j = 0;
            while (domain[j] < lower) {
                j++;
            }
            low = j;
            j = domain.length - 1;
            while (domain[j] > upper) {
                j--;
            }
            high = j;
        } else {
            low = (int) Math.ceil(lower);
            high = (int) Math.floor(upper);
        }
        length = high - low + 1;
        /*
       * Do the extraction for the independent variable, depending
       * on whether it is a Triplet or not.
       */
        if (input.isTriplet()) {
            double step = input.getIndependentTriplet(0).getStep();
            input.setIndependentTriplet(length, input.getIndependentTriplet(0).getStart() + low * step, step, 0);
        } else {
            newDomain = new double[length];
            System.arraycopy(domain, low, newDomain, 0, length);
            input.setXReal(newDomain);
            if (input.isIndependentComplex(0)) {
                domain = input.getXImag();
                newDomain = new double[length];
                System.arraycopy(domain, low, newDomain, 0, length);
                input.setXImag(newDomain);
            }
        }
        /*
       * Do the extraction for the dependent variable.
       */
        newRange = new double[length];
        System.arraycopy(input.getXReal(), low, newRange, 0, length);
        input.setXReal(newRange);
        if (input.isDependentComplex(0)) {
            newRange = new double[length];
            System.arraycopy(input.getXImag(), low, newRange, 0, length);
            input.setXImag(newRange);
        }

        VectorType output = input;
        /*
       * Tidy up other parameters of known data types that are subclasses
       * of VectorType. If input implements Spectral or Histogramming
       * interface, then lose the auxiliary information: this method
       * does not do the subselection correctly. For Spectral types in
       * particular, use the filtering units.
       */
        if ((output instanceof Spectral) || (output instanceof Histogramming)) {
            boolean dependentComplex = output.isDependentComplex(0);
            boolean independentComplex = output.isIndependentComplex(0);
            boolean triplet = output.isTriplet();
            if (dependentComplex && independentComplex) {
                output = new VectorType(output.getXReal(), output.getXImag(), output.getDataReal(),
                        output.getDataImag());
            } else if (dependentComplex && triplet) {
                output = new VectorType(output.getXTriplet(), output.getDataReal(), output.getDataImag());
            } else if (dependentComplex) {
                output = new VectorType(output.getXReal(), null, output.getDataReal(), output.getDataImag());
            } else if (triplet) {
                output = new VectorType(output.getXTriplet(), output.getDataReal());
            } else {
                output = new VectorType(output.getXReal(), output.getDataReal());
            }
        } else if (output instanceof Signal) {
            ((Signal) output).setAcquisitionTime(
                    ((Signal) output).getAcquisitionTime() + low / ((Signal) output).getSamplingRate());
        }

        output(output);
    }


    /**
     * Initialses information specific to Subvector.
     */
    public void init() {
        super.init();


//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);

//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Make selection on: $title type Choice IndexValue IndependentVariableValue\n";
        guilines += "Lower bound on new domain $title lower Scroller 0 100 0\n";
        guilines += "Upper bound on new domain $title upper Scroller 0 100 100\n";
        setGUIBuilderV2Info(guilines);


    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Make selection on: $title type Choice IndexValue IndependentVariableValue");
//        addGUILine("Lower bound on new domain $title lower Scroller 0 100 0");
//        addGUILine("Upper bound on new domain $title upper Scroller 0 100 100");
//    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
    }

    /**
     * Called when the stop button is pressed within the MainTriana Window
     */
    public void stopping() {
        super.stopping();
    }

    /**
     * Called when the start button is pressed within the MainTriana Window
     */
//    public void starting() {
//        super.starting();
//    }
//
//    /**
//     * Saves Subvector's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("type", type);
//        saveParameter("lower", lower);
//        saveParameter("upper", upper);
//    }


    /**
     * Used to set each of Subvector's parameters.
     */
    public void parameterUpdate(String name, String value) {
        //updateGUIParameter(name, value);

        if (name.equals("type")) {
            type = value;
        }
        if (name.equals("lower")) {
            lower = Str.strToDouble(value);
        }
        if (name.equals("upper")) {
            upper = Str.strToDouble(value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Subvector, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Extract a subrange of values of the input VectorType.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Subvector.html";
    }
}




