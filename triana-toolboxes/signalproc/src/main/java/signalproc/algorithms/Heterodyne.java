package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSpectrum;
import triana.types.util.Str;

/**
 * A Heterodyne unit to ..
 *
 * @author B F Schutz
 * @version 1.0 05 Mar 2001
 */
public class Heterodyne extends Unit {

    int shift = 500;
    double bandwidth = 100;
    String window = "(none)";


    /**
     * ********************************************* ** USER CODE of Heterodyne goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        ComplexSpectrum input = (ComplexSpectrum) getInputAtNode(0);


    }


    /**
     * Initialses information specific to Heterodyne.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Heterodyne frequency $title shift IntScroller 0 1000 500\n";
        guilines += "Bandwidth $title bandwidth Scroller 0 200 100\n";
        guilines += "Choose window for smoothing filter edges before going back to time domain $title window Choice (none)\n";
        setGUIBuilderV2Info(guilines);

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Heterodyne frequency $title shift IntScroller 0 1000 500");
//        addGUILine("Bandwidth $title bandwidth Scroller 0 200 100");
//        addGUILine(
//                "Choose window for smoothing filter edges before going back to time domain $title window Choice (none)");
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

    /**
     * Saves Heterodyne's parameters.
     */
//    public void saveParameters() {
//        saveParameter("shift", shift);
//        saveParameter("bandwidth", bandwidth);
//        saveParameter("window", window);
//    }


    /**
     * Used to set each of Heterodyne's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("shift")) {
            shift = Str.strToInt((String) value);
        }
        if (name.equals("bandwidth")) {
            bandwidth = Str.strToDouble((String) value);
        }
        if (name.equals("window")) {
            window = (String) value;
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Heterodyne, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "ComplexSpectrum Spectrum TimeFrequency";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Heterodyne, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "ComplexSpectrum Spectrum TimeFrequency";
//    }


    public String[] getInputTypes() {
        return new String[]{"triana.types.ComplexSpectrum", "triana.types.Spectrum", "triana.types.TimeFrequency"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.ComplexSpectrum", "triana.types.Spectrum", "triana.types.TimeFrequency"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put Heterodyne's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Shift.html";
    }
}




