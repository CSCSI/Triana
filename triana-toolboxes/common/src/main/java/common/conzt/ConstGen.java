package common.conzt;

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

import triana.types.Const;
import triana.types.OldUnit;
import triana.types.util.Str;

/**
 * A ConstGen unit to Generate a Constant value and wrap it into a Const type.
 *
 * @author Ian Taylor
 * @version 1.0 19 May 2000
 */
public class ConstGen extends OldUnit {

    String constant = "0.0";
    String imagval = "0.0";

    /**
     * ********************************************* ** USER CODE of ConstGen goes here    ***
     * *********************************************
     */
    public void process() {
        output(new Const(Str.strToDouble(constant), Str.strToDouble(imagval)));
    }

    /**
     * Initialses information specific to ConstGen.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

        defineParameter("constant", "0.0", USER_ACCESSIBLE);
        defineParameter("imagval", "0.0", USER_ACCESSIBLE);

        setUseGUIBuilder(true);
    }


    public void setGUIInformation() {
        addGUILine("Real Value : $title constant TextField 0.0");
        addGUILine("Imag Value : $title imagval TextField 0.0");
    }

    /**
     * Reset's ConstGen
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ReplaceAll's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter("constant", constant);
        saveParameter("imagval", imagval);
    }

    /**
     * Used to set each of ReplaceAll's parameters.
     */
    public void setParameter(String name, String value) {
        if (name.equals("constant")) {
            constant = value;
        }
        if (name.equals("imagval")) {
            imagval = value;
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to ConstGen, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "none";
    }

    /**
     * @return a string containing the names of the types output from ConstGen, each separated by a white space.
     */
    public String outputTypes() {
        return "Const";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Generates a constant";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ConstGen.html";
    }
}

