package signalproc.time;

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
import triana.types.VectorType;


/**
 * A Stretch unit to enlarge a data set by inserting a certain number of zeros at regular intervals.
 *
 * @author B.F. Schutz
 * @version 2.0 alpha 20 September 2000
 */
public class Stretch extends OldUnit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Inserts zeros at regular intervals";
    }


    /**
     * Parameter telling whether to insert before or after element
     */
    String place = "AfterEachElement";

    /**
     * Parameter giving number of zeros inserted at each insertion
     */
    int pad = 0;

    /**
     * ********************************************* ** USER CODE of Stretch goes here    ***
     * *********************************************
     */
    public void process() {

        VectorType input = (VectorType) getInputNode(0);
        VectorType output = (VectorType) input.copyMe();

        boolean before = place.equals("BeforeEachElement");

        if (pad < 1) {
            println("Data not modified by " + getName() + " because given number of zeros to be inserted ("
                    + String.valueOf(pad) + ") is zero or negative.");
        } else {
            output.interpolateZeros(pad, before);
        }

        output(output);
    }


    /**
     * Initialses information specific to Stretch.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setResizableInputs(false);
        setResizableOutputs(true);

        place = "AfterEachElement";
        pad = 0;

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
    public void setGUIInformation() {
        addGUILine("Choose where to add extra zeros: $title place Choice AfterEachElement BeforeEachElement");
        addGUILine("Give number of zeros to add in each location $title pad IntScroller 0 15 0");
    }

    /**
     * Resets Stretch
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Stretch's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter("place", place);
        saveParameter("pad", pad);
    }


    public void setParameter(String name, String value) {
        if (name.equals("place")) {
            place = value;
        }
        if (name.equals("pad")) {
            pad = strToInt(value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to Stretch, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "VectorType";
    }

    /**
     * @return a string containing the names of the types output from Stretch, each separated by a white space.
     */
    public String outputTypes() {
        return "VectorType";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Stretch.html";
    }


}

















