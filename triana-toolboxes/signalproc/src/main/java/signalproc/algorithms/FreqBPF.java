package signalproc.algorithms;

/*
 * Copyright (c) 1995 - 1998 University of Wales College of Cardiff
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


/**
 * A FreqBP unit to ..
 *
 * @author ian
 * @version 1.0 beta 24 Sep 1998
 */
public class FreqBPF extends OldUnit {

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

        double center = strToDouble(centerFreq);
        double band = strToDouble(bandwidth);
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

        setUseGUIBuilder(true);

        setResizableInputs(true);
        setResizableOutputs(true);
        setParameter("centerFreq", "100");
        setParameter("bandwidth", "50");
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
    public void setGUIInformation() {
        addGUILine("Enter The Centre Frequency ? $title centerFreq TextField 100.0");
        addGUILine("Enter The Bandwidth $title bandwidth TextField 50.0");
    }

    /**
     * Reset's FreqBP
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves FreqBP's parameters.
     */
    public void saveParameters() {
        saveParameter("centerFreq", centerFreq);
        saveParameter("bandwidth", bandwidth);
    }

    /**
     * Used to set each of FreqBP's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("centerFreq")) {
            centerFreq = value;
        }
        if (name.equals("bandwidth")) {
            bandwidth = value;
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to FreqBP, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "Spectrum ComplexSpectrum Const";
    }

    /**
     * @return a string containing the names of the types output from FreqBP, each separated by a white space.
     */
    public String outputTypes() {
        return "Spectrum ComplexSpectrum";
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













