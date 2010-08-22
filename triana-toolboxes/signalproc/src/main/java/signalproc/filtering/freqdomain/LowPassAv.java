package signalproc.filtering.freqdomain;

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


import triana.types.OldUnit;
import triana.types.SampleSet;


/**
 * A LowPassAv unit to ..
 *
 * @author ian
 * @version 1.0 beta 18 Jun 1999
 */
public class LowPassAv extends OldUnit {

    double CutOff;


    /**
     * ********************************************* ** USER CODE of LowPassAv goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        SampleSet wave = (SampleSet) getInputNode(0);

        double MovingAverage = 0.0;
        int i;
        double out[] = new double[wave.size()];

        for (i = 0; i < wave.size(); i++) {
            MovingAverage += wave.data[i];
            if (i - CutOff >= 0) {
                MovingAverage -= wave.data[i - (int) CutOff];
            }
            out[i] = MovingAverage;
        }

        output(new SampleSet(
                ((SampleSet) wave).samplingFrequency(), out));
    }


    /**
     * Initialses information specific to LowPassAv.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
    public void setGUIInformation() {
        addGUILine("Set the Low-Pass Cut-Off Average Value $title CutOff Scroller 0 500 5");
    }

    /**
     * Reset's LowPassAv
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves LowPassAv's parameters.
     */
    public void saveParameters() {
        saveParameter("CutOff", CutOff);
    }

    /**
     * Used to set each of LowPassAv's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("CutOff")) {
            CutOff = strToDouble(value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to LowPassAv, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "SampleSet";
    }

    /**
     * @return a string containing the names of the types output from LowPassAv, each separated by a white space.
     */
    public String outputTypes() {
        return "SampleSet";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put LowPassAv's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "LowPassAv.html";
    }
}













