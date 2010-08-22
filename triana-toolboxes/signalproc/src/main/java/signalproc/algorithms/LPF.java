package signalproc.algorithms;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */


import triana.types.ComplexSpectrum;
import triana.types.OldUnit;
import triana.types.Spectrum;
import triana.types.util.FlatArray;


/**
 * A LPF unit to ..
 *
 * @author B F Schutz
 * @version 2.0 02 Mar 2001
 */
public class LPF extends OldUnit {

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

        setUseGUIBuilder(true);

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Give upper frequency limit (Hz) $title lowPass Scroller 0 1000 100");
        addGUILine(
                "Output narrow-band? (Do not check if you want full-band output with zeros.) $title noZeros Checkbox false");
    }

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
    public void starting() {
        super.starting();
    }

    /**
     * Saves LPF's parameters.
     */
    public void saveParameters() {
        saveParameter("lowPass", lowPass);
        saveParameter("noZeros", noZeros);
    }


    /**
     * Used to set each of LPF's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("lowPass")) {
            lowPass = strToDouble(value);
        }
        if (name.equals("noZeros")) {
            noZeros = strToBoolean(value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to LPF, each separated by a white space.
     */
    public String inputTypes() {
        return "ComplexSpectrum Spectrum";
    }

    /**
     * @return a string containing the names of the types output from LPF, each separated by a white space.
     */
    public String outputTypes() {
        return "ComplexSpectrum Spectrum";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put LPF's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "";
    }
}




