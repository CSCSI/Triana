package signalproc.algorithms;

import java.util.Vector;

import org.trianacode.taskgraph.Unit;
import triana.types.Spectrum;
import triana.types.util.Str;

/**
 * A SpecAver unit to ..
 *
 * @author Ian Taylor
 * @version 1.0 alpha 11 Mar 1997
 */
public class SpecAver extends Unit {
    /**
     * A vector storing all of the input spectra.
     */
    Vector spectra;

    /**
     * The number of spectra to average over
     */
    int numberOfSpectra = 10;

    /**
     * The number of input spectra taken so far.
     */
    public int size;

    int current;
    int min = 1, max = 100;
    //int points;
    String parameterName = "spectra";

    /**
     * The UnitWindow for SpecAver
     */
    //IntScrollerWindow myWindow;

    /**
     * Initialses information specific to SpecAver.
     */
    public void init() {
        super.init();

        spectra = new Vector();
//        setResizableInputs(false);
//        setResizableOutputs(true);
//
//        setUseGUIBuilder(true);

        //myWindow = new IntScrollerWindow(this, "Change Number of Spectra to average ?");
        //myWindow.setParameterName(parameterName);
        reset();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Number of Spectra to average $title " + parameterName + " IntScroller " + min + " " + max + " " + 10 + "\n";
        setGUIBuilderV2Info(guilines);
    }

//    public void setGUIInformation() {
//        addGUILine(
//                "Number of Spectra to average $title " + parameterName + " IntScroller " + min + " " + max + " " + 10);
//    }

    /**
     * Reset's SpecAver
     */
    public void reset() {
        super.reset();
        //numberOfSpectra = 10;
        //myWindow.setValues(min, max, numberOfSpectra);
        //myWindow.updateWidgets();
        size = 0;
        current = 0;
        spectra = new Vector(numberOfSpectra);
        spectra.setSize(numberOfSpectra);
    }

    /**
     * Saves SpecAver's parameters to the parameter file.
     */
//    public void saveParameters() {
//        saveParameter(parameterName, numberOfSpectra);
//        saveParameter("minimum", min);
//        saveParameter("maximum", max);
//    }

    public void parameterUpdate(String name, Object value) {
        if (name.equals(parameterName)) {
            numberOfSpectra = Str.strToInt((String) value);
        } else if (name.equals("minimum")) {
            min = Str.strToInt((String) value);
        } else if (name.equals("maximum")) {
            max = Str.strToInt((String) value);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
        //myWindow.setValues(min,max,numberOfSpectra);
        //myWindow.updateWidgets();
    }

    /**
     * @return a string containing the names of the types allowed to be input to SpecAver, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "Spectrum";
//    }
//
//    /**
//     * @return a string containing the names of the types output from SpecAver, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "Spectrum";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.Spectrum"};
    }

    /**
     * @return an array of the output types for MyMakeCurve
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Spectrum"};
    }
    
    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Averages the input spectra";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "SpecAver.html";
    }

    /**
     * The main functionality of SpecAver goes here
     */
    public void process() {
        Spectrum s, s1;
        int i, j;

        spectra.setSize(numberOfSpectra);

        if (current >= spectra.size()) {
            current = 0;
        }

        s = (Spectrum) getInputAtNode(0);

        if (spectra.size() == 0) {
            output(s);
            return;
        }

        //if (firstTimeCalled())
        //points = s.size();

        spectra.setElementAt(s, current);

        ++current;

        if (current > size) {
            ++size;
        }

        if (size > spectra.size()) {
            size = spectra.size();
        }

        double[] data = new double[s.size()];

        for (i = 0; i < s.size(); ++i) {
            data[i] = 0.0;
        }

        for (j = 0; j < size; ++j) {
            s1 = (Spectrum) spectra.elementAt(j);

            for (i = 0; i < s1.size(); ++i) {
                data[i] += s1.data[i];
            }
        }

        for (i = 0; i < s.size(); ++i) {
            data[i] /= (double) size;
        }

        output(new Spectrum(s.samplingFrequency, data));
    }

    /**
     * @return SpecAver's parameter window sp that Triana 
     * can move and display it.
     */
    /*public Window getParameterWIndow() {
        return myWindow;
        }*/
}

















