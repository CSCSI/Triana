package signalproc.dataparam;

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


import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.SampleSet;


/**
 * A SetLabels unit to reset the axis labels of a data set.
 *
 * @author David Churches
 * @version 1.1 06 Nov 2003
 */
public class SetLabels extends OldUnit {

    String xlabel = "";
    String ylabel = "";


    /**
     * ********************************************* ** USER CODE of SetLabels goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        GraphType input = (SampleSet) getInputNode(0);

        if (!xlabel.equals("")) {
            input.setIndependentLabels(0, xlabel);
        }
        if (!ylabel.equals("")) {
            input.setDependentLabels(0, ylabel);
        }

        output(input);

    }


    /**
     * Initialses information specific to SetLabels.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setRequireDoubleInputs(false);
        setCanProcessDoubleArrays(false);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("New x-axis label: $title xlabel TextField");
        addGUILine("New y-axis label: $title ylabel TextField");
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
     * Saves SetLabels's parameters.
     */
    public void saveParameters() {
        saveParameter("xlabel", xlabel);
        saveParameter("ylabel", ylabel);
    }


    /**
     * Used to set each of SetLabels's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("xlabel")) {
            xlabel = value;
        }
        if (name.equals("ylabel")) {
            ylabel = value;
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to SetLabels, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "SampleSet";
    }

    /**
     * @return a string containing the names of the types output from SetLabels, each separated by a white space.
     */
    public String outputTypes() {
        return "SampleSet";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Resets the axis labels of a SampleSet";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "SetLabels.html";
    }
}




