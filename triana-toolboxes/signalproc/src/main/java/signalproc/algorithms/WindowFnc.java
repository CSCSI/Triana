package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.GraphType;
import triana.types.util.SigAnalWindows;

/**
 * WindowFnc allows the user to apply one of 6 window functions to the input signal: Bartlett, Blackman, Gaussian,
 * Hamming, Hanning, or Welch. Static methods allow the same functions to be applied to data from other units, and allow
 * the windows to be applied to frequency-domain data as well. The frequency-domain methods allow one to apply only the
 * right-half or left-half window so that windowing can be correctly applied to spectra stored according to the Triana
 * storage model. If rounding of narrow bandwidths is desired this will be done automatically if the input spectrum is
 * narrow-band, but not if it is padded with zeros.
 *
 * @author Ian Taylor
 * @author Bernard Schutz
 * @version 2.01 18 March 2001
 */
public class WindowFnc extends Unit {

    String WindowFunction = "(none)";

    public void process() throws Exception {
        GraphType input, result;

        input = (GraphType) getInputAtNode(0);

        if (WindowFunction.equals("(none)")) {
            result = input;
        } else {
            result = SigAnalWindows.applyWindowFunction(input, WindowFunction, true);
        }

        output(result);
    }


    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
//    public void setGUIInformation() {
//        addGUILine("Window Function ? $title WindowFunction Choice " + SigAnalWindows.listOfWindows());
//    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Applies a window function to the input";
    }

    /**
     * Initialses information specific to FFT.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Window Function ? $title WindowFunction Choice " + SigAnalWindows.listOfWindows();
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Reset's WindowFnc public void reset() { super.reset(); }
     * <p/>
     * /** Saves FFT's parameters.
     */
//    public void saveParameters() {
//        saveParameter("WindowFunction", WindowFunction);
//    }

    /**
     * Used to set each of FFT's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("WindowFunction")) {
            WindowFunction = (String) value;
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to FFT, each separated by a white space.
     */
//    public String inputTypes() {
//        return "VectorType TimeFrequency";
//    }
//
//    /**
//     * @return a string containing the names of the types output from FFT, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "VectorType TimeFrequency";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType", "triana.types.TimeFrequency"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType", "triana.types.TimeFrequency"};
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "WindowFnc.html";
    }
}
















