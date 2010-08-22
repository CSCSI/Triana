package signalproc.algorithms;

/*
 * Copyright (c) 1995, 1996, 1997, 1998 University of Wales College of Cardiff
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


import triana.types.GraphType;
import triana.types.OldUnit;
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
public class WindowFnc extends OldUnit {

    String WindowFunction = "(none)";

    public void process() throws Exception {
        GraphType input, result;

        input = (GraphType) getInputNode(0);

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
    public void setGUIInformation() {
        addGUILine("Window Function ? $title WindowFunction Choice " + SigAnalWindows.listOfWindows());
    }


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

        setUseGUIBuilder(true);

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * Reset's WindowFnc public void reset() { super.reset(); }
     * <p/>
     * /** Saves FFT's parameters.
     */
    public void saveParameters() {
        saveParameter("WindowFunction", WindowFunction);
    }

    /**
     * Used to set each of FFT's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("WindowFunction")) {
            WindowFunction = value;
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to FFT, each separated by a white space.
     */
    public String inputTypes() {
        return "VectorType TimeFrequency";
    }

    /**
     * @return a string containing the names of the types output from FFT, each separated by a white space.
     */
    public String outputTypes() {
        return "VectorType TimeFrequency";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "WindowFnc.html";
    }
}
















