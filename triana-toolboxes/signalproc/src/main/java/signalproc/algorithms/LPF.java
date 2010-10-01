package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSpectrum;
import triana.types.Spectrum;
import triana.types.util.FlatArray;
import triana.types.util.Str;

/**
 * A LPF unit to ..
 *
 * @author B F Schutz
 * @version 2.0 02 Mar 2001
 */
public class LPF extends Unit {

    double lowPass = 100;
    boolean noZeros = false;


    /**
     * ********************************************* ** USER CODE of LPF goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        Object input;
        Object output;
        int startVal, givenLow;
        double[] full, narrowR, narrowI;
        boolean newNarrow = true;


        input = getInputAtNode(0);
        output = input;

        if (input instanceof ComplexSpectrum) {
            ComplexSpectrum s = (ComplexSpectrum) input;
            if (lowPass < s.getUpperFrequencyBound()) {
                startVal = (int) (lowPass / s.getFrequencyResolution());
                full = FlatArray
                        .convertToFullSpectrum(s.getDataReal(), s.getOriginalN(), !s.isTwoSided(), true, s.isNarrow(),
                                (int) (s.getLowerFrequencyBound() / s.getFrequencyResolution()));
                narrowR = FlatArray.convertToNarrowBand(full, false, 0, startVal);
                if (!noZeros) {
                    narrowR = FlatArray.convertToFullSpectrum(narrowR, s.getOriginalN(), false, true, true,
                            (int) (s.getLowerFrequencyBound() / s.getFrequencyResolution()));
                    newNarrow = false;
                }
                if (!s.isTwoSided()) {
                    narrowR = FlatArray.convertToOneSided(narrowR, s.getOriginalN(), newNarrow, true);
                }
                full = FlatArray
                        .convertToFullSpectrum(s.getDataImag(), s.getOriginalN(), !s.isTwoSided(), false, s.isNarrow(),
                                (int) (s.getLowerFrequencyBound() / s.getFrequencyResolution()));
                narrowI = FlatArray.convertToNarrowBand(full, false, 0, startVal);
                if (!noZeros) {
                    narrowI = FlatArray.convertToFullSpectrum(narrowI, s.getOriginalN(), false, false, true,
                            (int) (s.getLowerFrequencyBound() / s.getFrequencyResolution()));
                }
                if (!s.isTwoSided()) {
                    narrowI = FlatArray.convertToOneSided(narrowI, s.getOriginalN(), newNarrow, true);
                }
                output = new ComplexSpectrum(s.isTwoSided(), newNarrow, narrowR, narrowI, s.getOriginalN(),
                        s.getFrequencyResolution(), (newNarrow) ? lowPass : s.getUpperFrequencyBound());
            }
        } else if (input instanceof Spectrum) {
            Spectrum s = (Spectrum) input;
            if (lowPass < s.getUpperFrequencyBound()) {
                startVal = (int) (lowPass / s.getFrequencyResolution());
                full = FlatArray
                        .convertToFullSpectrum(s.getDataReal(), s.getOriginalN(), !s.isTwoSided(), true, s.isNarrow(),
                                (int) (s.getLowerFrequencyBound() / s.getFrequencyResolution()));
                narrowR = FlatArray.convertToNarrowBand(full, false, 0, startVal);
                if (!noZeros) {
                    narrowR = FlatArray.convertToFullSpectrum(narrowR, s.getOriginalN(), false, true, true,
                            (int) (s.getLowerFrequencyBound() / s.getFrequencyResolution()));
                    newNarrow = false;
                }
                if (!s.isTwoSided()) {
                    narrowR = FlatArray.convertToOneSided(narrowR, s.getOriginalN(), newNarrow, true);
                }
                output = new Spectrum(s.isTwoSided(), newNarrow, narrowR, s.getOriginalN(), s.getFrequencyResolution(),
                        (newNarrow) ? lowPass : s.getUpperFrequencyBound());
            }

        }

        output(output);


    }


    /**
     * Initialses information specific to LPF.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
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
        guilines += "Give upper frequency limit (Hz) $title lowPass Scroller 0 1000 100\n";
        guilines += "Output narrow-band? (Do not check if you want full-band output with zeros.) $title noZeros Checkbox false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Give upper frequency limit (Hz) $title lowPass Scroller 0 1000 100");
//        addGUILine(
//                "Output narrow-band? (Do not check if you want full-band output with zeros.) $title noZeros Checkbox false");
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
//     * Saves LPF's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("lowPass", lowPass);
//        saveParameter("noZeros", noZeros);
//    }

    /**
     * Used to set each of LPF's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("lowPass")) {
            lowPass = Str.strToDouble((String) value);
        }
        if (name.equals("noZeros")) {
            noZeros = Str.strToBoolean((String) value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put LPF's brief description here";
    }

    /**
     * @return a string containing the names of the types allowed to be input to LPF, each separated by a white space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.ComplexSpectrum", "triana.types.Spectrum"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.ComplexSpectrum", "triana.types.Spectrum"};
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "";
    }
}




