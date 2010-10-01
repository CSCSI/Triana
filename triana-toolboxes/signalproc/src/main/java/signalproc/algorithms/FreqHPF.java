package signalproc.algorithms;

import java.awt.Window;
import java.awt.event.ActionEvent;

import org.trianacode.gui.windows.ScrollerWindow;
import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSpectrum;
import triana.types.Spectrum;
import triana.types.util.Str;

/**
 * FreqLPF : A Unit to high-pass filter a frequency spectrum.
 *
 * @author Ian Taylor
 * @version 1.0 alpha 02 Apr 1997
 */
public class FreqHPF extends Unit {

    // some examples of parameters

    public double highPass;

    double min = 0.0, max = 10.0;
    String parameterName = "highpass";


    /**
     * The UnitWindow for FreqHPF
     */
    ScrollerWindow myWindow;

    /**
     * Initialses information specific to FreqHPF.
     */
    public void init() {
        super.init();

        setMinimumInputNodes(1);
        setMaximumInputNodes(1);
        setDefaultInputNodes(1);
        setMinimumOutputNodes(1);
        setDefaultOutputNodes(1);

        highPass = 1.0;
        myWindow = new ScrollerWindow(this, "High Pass Cut Off ?");
        myWindow.setParameterName(parameterName);
        myWindow.setValues(0.0, 100.0, highPass);
        myWindow.updateWidgets();
    }

    /**
     * Reset's FreqHPF
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves FreqHPF's parameters to the parameter file.
     */
//    public void saveParameters() {
//        saveParameter(parameterName, highPass);
//        saveParameter("minimum", min);
//        saveParameter("maximum", max);
//    }

    public void parameterUpdate(String name, String value) {
        if (name.equals(parameterName)) {
            highPass = Str.strToDouble(value);
        } else if (name.equals("minimum")) {
            min = Str.strToDouble(value);
        } else if (name.equals("maximum")) {
            max = Str.strToDouble(value);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
        myWindow.setValues(min, max, highPass);
        myWindow.updateWidgets();
    }

    /**
     * @return a string containing the names of the types allowed to be input to FreqHPF, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "Spectrum ComplexSpectrum";
//    }
//
//    /**
//     * @return a string containing the names of the types output from FreqHPF, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "Spectrum ComplexSpectrum";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.Spectrum", "triana.types.ComplexSpectrum"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.Spectrum", "triana.types.ComplexSpectrum"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Performs a high pass filter in the frequency domain";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "FreqHPF.html";
    }

    /**
     * The main functionality of FreqLPF goes here
     */
    public void process() {
        Object input;
        Object output;
        int endVal;

        input = getInputAtNode(0);
        output = input;

        if (input instanceof ComplexSpectrum) {
            ComplexSpectrum s = (ComplexSpectrum) input;
            endVal = (int) (highPass / s.frequencyResolution());
            if (endVal > s.size()) {
                endVal = s.size();
            }
            for (int i = 0; i < endVal; ++i) {
                s.real[i] = 0.0;
                s.imag[i] = 0.0;
            }
            output = new ComplexSpectrum(s.samplingFrequency,
                    s.real, s.imag);
        } else if (input instanceof Spectrum) {
            Spectrum s = (Spectrum) input;
            endVal = (int) (highPass / s.frequencyResolution());
            if (endVal > s.size()) {
                endVal = s.size();
            }
            for (int i = 0; i < endVal; ++i) {
                s.data[i] = 0.0;
            }
            output = new Spectrum(s.samplingFrequency, s.data);
        }

        output(output);  // change result to incorporate other inputs
    }

    /**
     * @return FreqLPF's parameter window sp that Triana can move and display it.
     */
    public Window getParameterWindow() {
        return myWindow;
    }


    /**
     * Captures the events thrown out by ScrollerWindow.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//    }
}

















