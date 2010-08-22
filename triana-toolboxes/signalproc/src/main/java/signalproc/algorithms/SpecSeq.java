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
import triana.types.Spectral;
import triana.types.Spectrum;
import triana.types.TimeFrequency;
import triana.types.VectorType;


/**
 * A SpecSeq unit to accumulate a sequence of spectra into a TimeFrequency matrix.
 *
 * @author B F Schutz
 * @version 1.0 30 Dec 2000
 */
public class SpecSeq extends OldUnit {

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
            println(getName() + " count = " + String.valueOf(count));
            VectorType input = (VectorType) getInputNode(0);
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
            println(getName() + " count = " + String.valueOf(count));
            VectorType input = (VectorType) getInputNode(0);
            if (!input.isCompatible(firstInput)) {
                println(getName() + " Input " + String.valueOf(count)
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

        setUseGUIBuilder(true);

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("How many input spectra should be accumulated? $title nSpec IntScroller 1 1000 64");
        addGUILine("Rolling list? $title roll Checkbox true");
    }

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
    public void starting() {
        super.starting();
    }

    /**
     * Saves SpecSeq's parameters.
     */
    public void saveParameters() {
        saveParameter("nSpec", nSpec);
        saveParameter("roll", roll);
    }


    /**
     * Used to set each of SpecSeq's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("nSpec")) {
            nSpec = strToInt(value);
        }
        if (name.equals("roll")) {
            roll = strToBoolean(value);
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
    public String inputTypes() {
        return "ComplexSpectrum Spectrum";
    }

    /**
     * @return a string containing the names of the types output from SpecSeq, each separated by a white space.
     */
    public String outputTypes() {
        return "TimeFrequency";
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




