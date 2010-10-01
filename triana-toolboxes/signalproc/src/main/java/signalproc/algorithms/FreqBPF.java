package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSpectrum;
import triana.types.Spectrum;
import triana.types.util.Str;


/**
 * A FreqBP unit to ..
 *
 * @author ian
 * @version 1.0 beta 24 Sep 1998
 */
public class FreqBPF extends Unit {

    String centerFreq;
    String bandwidth;


    /**
     * ********************************************* ** USER CODE of FreqBP goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Object input;
        Object output;
        int startVal;
        int endVal;

        input = getInputAtNode(0);
        output = input;

        double center = Str.strToDouble(centerFreq);
        double band = Str.strToDouble(bandwidth);
        double low = center - (band / 2);
        if (low < 0) {
            low = 0;
        }
        double high = center + (band / 2);

        if (input instanceof ComplexSpectrum) {
            ComplexSpectrum s = (ComplexSpectrum) input;
            startVal = (int) (low / s.frequencyResolution());
            if (high > s.size()) {
                high = s.size();
            }
            if (startVal > s.size()) {
                startVal = s.size();
            }
            for (int i = 0; i < startVal; ++i) {
                s.real[i] = 0.0;
                s.imag[i] = 0.0;
            }
            endVal = (int) (high / s.frequencyResolution());
            for (int i = endVal; i < s.size(); ++i) {
                s.real[i] = 0.0;
                s.imag[i] = 0.0;
            }
            System.out.println("Bandpass :");
            output = new ComplexSpectrum(false, false, s.real, s.imag, s.real.length,
                    s.samplingFrequency / s.real.length, (s.samplingFrequency / 2.0));
        } else if (input instanceof Spectrum) {
            Spectrum s = (Spectrum) input;
            startVal = (int) (low / s.frequencyResolution());
            if (high > s.size()) {
                high = s.size();
            }
            if (startVal > s.size()) {
                startVal = s.size();
            }
            for (int i = 0; i < startVal; ++i) {
                s.data[i] = 0.0;
            }
            endVal = (int) (high / s.frequencyResolution());
            for (int i = endVal; i < s.size(); ++i) {
                s.data[i] = 0.0;
            }
            output = new Spectrum(true, false, s.data, s.data.length, s.samplingFrequency / s.data.length,
                    (int) (s.samplingFrequency / 2.0));
        }

        output(output);
    }


    /**
     * Initialses information specific to FreqBP.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setResizableInputs(true);
//        setResizableOutputs(true);
        setParameter("centerFreq", "100");
        setParameter("bandwidth", "50");

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Enter The Centre Frequency ? $title centerFreq TextField 100.0\n";
        guilines += "Enter The Bandwidth $title bandwidth TextField 50.0\n";
        setGUIBuilderV2Info(guilines);                
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
//    public void setGUIInformation() {
//        addGUILine("Enter The Centre Frequency ? $title centerFreq TextField 100.0");
//        addGUILine("Enter The Bandwidth $title bandwidth TextField 50.0");
//    }

    /**
     * Reset's FreqBP
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves FreqBP's parameters.
     */
//    public void saveParameters() {
//        saveParameter("centerFreq", centerFreq);
//        saveParameter("bandwidth", bandwidth);
//    }

    /**
     * Used to set each of FreqBP's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("centerFreq")) {
            centerFreq = (String) value;
        }
        if (name.equals("bandwidth")) {
            bandwidth = (String) value;
        }
    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.Spectrum", "triana.types.ComplexSpectrum", "triana.types.Const"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.Spectrum", "triana.types.ComplexSpectrum"};
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Performs a frequency based band-pass filter on the input frequency spectrum";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "FreqBPF.html";
    }
}













