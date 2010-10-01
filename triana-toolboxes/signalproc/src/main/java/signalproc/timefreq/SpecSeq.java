package signalproc.timefreq;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSpectrum;
import triana.types.Spectral;
import triana.types.Spectrum;
import triana.types.TimeFrequency;
import triana.types.VectorType;
import triana.types.util.Str;

/**
 * A SpecSeq unit to accumulate a sequence of spectra into a TimeFrequency matrix.
 *
 * @author B F Schutz
 * @version 1.0 30 Dec 2000
 */
public class SpecSeq extends Unit {

    int nSpec = 64;
    double[][] real, imag;
    Spectrum spec;
    ComplexSpectrum cspec;
    VectorType firstInput;
    boolean complex = false;
    boolean start = true;
    int count;
    double interval;
    boolean roll = true;

    /**
     * ********************************************* ** USER CODE of SpecSeq goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        int last = nSpec - 1;
        int next = 1;
        if (start) {
            start = false;
            count = 0;
            System.out.println(getToolName() + " count = " + String.valueOf(count));
            VectorType input = (VectorType) getInputAtNode(0);
            real = new double[nSpec][input.size()];
            imag = null;
            if (input instanceof ComplexSpectrum) {
                imag = new double[nSpec][input.size()];
                complex = true;
                cspec = (ComplexSpectrum) input.copyMe();
                real[0] = cspec.getDataReal();
                imag[0] = cspec.getDataImag();
            } else {
                complex = false;
                spec = (Spectrum) input.copyMe();
                real[0] = spec.getData();
            }
            firstInput = input;
            interval = 1.0 / ((Spectral) input).getFrequencyResolution(0);
        } else {
            count++;
            System.out.println(getToolName() + " count = " + String.valueOf(count));
            VectorType input = (VectorType) getInputAtNode(0);
            if (!input.isCompatible(firstInput)) {
                System.out.println(getToolName() + " Input " + String.valueOf(count)
                        + " is not compatible with first one. Do nothing.");
                return;
            }
            if ((count > last) && roll) {
                for (int k = 1; k < nSpec; k++) {
                    real[k - 1] = real[k];
                    if (complex) {
                        imag[k - 1] = imag[k];
                    }
                }
                next = last;
            } else {
                next = count;
            }
            if (complex) {
                cspec = (ComplexSpectrum) input.copyMe();
                real[next] = cspec.getDataReal();
                imag[next] = cspec.getDataImag();
            } else {
                spec = (Spectrum) input.copyMe();
                real[next] = spec.getData();
            }
            if (roll) {
                output(new TimeFrequency(real, imag, ((Spectral) input).isTwoSided(), ((Spectral) input).isNarrow(0),
                        ((Spectral) input).getOriginalN(0), ((Spectral) input).getFrequencyResolution(0),
                        ((Spectral) input).getUpperFrequencyBound(0), interval, 0.0));
            } else if (next == last) {
                start = true;
                output(new TimeFrequency(real, imag, ((Spectral) input).isTwoSided(), ((Spectral) input).isNarrow(0),
                        ((Spectral) input).getOriginalN(0), ((Spectral) input).getFrequencyResolution(0),
                        ((Spectral) input).getUpperFrequencyBound(0), interval, 0.0));
            }
        }
    }


    /**
     * Initialses information specific to SpecSeq.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(0);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

//        setResizableInputs(false);
//        setResizableOutputs(true);

        String guilines = "";
        guilines += "How many input spectra should be accumulated? $title nSpec IntScroller 1 1000 64\n";
        guilines += "Rolling list? $title roll Checkbox true\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("How many input spectra should be accumulated? $title nSpec IntScroller 1 1000 64");
//        addGUILine("Rolling list? $title roll Checkbox true");
//    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        start = true;
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
//     * Saves SpecSeq's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("nSpec", nSpec);
//        saveParameter("roll", roll);
//    }

    /**
     * Used to set each of SpecSeq's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("nSpec")) {
            nSpec = Str.strToInt((String) value);
        }
        if (name.equals("roll")) {
            roll = Str.strToBoolean((String) value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to SpecSeq, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "ComplexSpectrum Spectrum";
//    }
//
//    /**
//     * @return a string containing the names of the types output from SpecSeq, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "TimeFrequency";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.ComplexSpectrum", "triana.types.ComplexSpectrum"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.TimeFrequency"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Accumulates spectra to make a time-frequency object";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "SpecSeq.html";
    }
}




