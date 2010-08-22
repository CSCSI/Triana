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


import triana.types.OldUnit;
import triana.types.SampleSet;


/**
 * A ALE unit to ..
 *
 * @author ian
 * @version 2.0 18 Sep 2000
 */
public class ALE extends OldUnit {
    String type = "NLMS";
    int tapSpacing = 1;
    String stepsize = "auto";
    int N = 200;
    ALEProcessor ale = null;

    /**
     * ********************************************* ** USER CODE of ALE goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        if (ale == null) {
            ale = new ALEProcessor(N, 1, stepsize, type, false);
            ale.setObject(this);
        }

        SampleSet input = (SampleSet) getInputAtNode(0);
        SampleSet output = (SampleSet) input.copyMe();

        ale.process(output.data);

        output(output);
    }


    /**
     * Initialses information specific to ALE.
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
        addGUILine("Adaptive Filter Type $title type Choice NLMS LMS");
        addGUILine("Enter Step Size (or choose 'auto') $title stepsize TextField auto");
        addGUILine("Enter Number of Taps : $title numberOfTaps IntScroller 0 1000 200");
        addGUILine("Enter Tap Size : $title tapSpacing IntScroller 0 100 1");
    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
        if (ale != null) {
            ale.reset();
        }
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
     * Saves ALE's parameters.
     */
    public void saveParameters() {
        saveParameter("type", type);
        saveParameter("stepsize", stepsize);
        saveParameter("numberOfTaps", N);
        saveParameter("tapSpacing", tapSpacing);
    }


    /**
     * Used to set each of ALE's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (ale == null) {
            ale = new ALEProcessor(N, 1, stepsize, type, false);
            ale.setObject(this);
        }

        if (name.equals("type")) {
            type = value;
            if (ale != null) {
                ale.setWeightUpdateType(type);
            }
        }
        if (name.equals("stepsize")) {
            stepsize = value;
            if (ale != null) {
                ale.setStepSize(stepsize);
            }
        }
        if (name.equals("numberOfTaps")) {
            N = strToInt(value);
            if (ale != null) {
                ale.setNumberOfTaps(N);
            }
        }
        if (name.equals("tapSpacing")) {
            tapSpacing = strToInt(value);
            if (ale != null) {
                ale.setTapSpacing(tapSpacing);
            }
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ALE, each separated by a white space.
     */
    public String inputTypes() {
        return "SampleSet";
    }

    /**
     * @return a string containing the names of the types output from ALE, each separated by a white space.
     */
    public String outputTypes() {
        return "SampleSet";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Adaptive Noise Cancelling to remove noise from a signal";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ALE.html";
    }


}




