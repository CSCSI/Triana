package signalproc.converters;

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


import triana.types.Curve;
import triana.types.OldUnit;
import triana.types.VectorType;


/**
 * A CurveToVect unit to ..
 *
 * @author Ian Taylor
 * @version 1.0 alpha 13 May 1997
 */
public class CurveToVect extends OldUnit {

    /**
     * ********************************************* Main routine of CurveToVect which takes in a spectrum and converts
     * it into a vector data type for input to the Grapher. *********************************************
     */
    public void process() {
        Curve curv = (Curve) getInputNode(0);

        VectorType curv2D = convert(curv);

        output(curv2D);
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts Curve to a VectorType";
    }

    /**
     * Converts a Curve data type to a VectorType
     *
     * @return a VectorType
     */
    public static synchronized VectorType convert(Curve c) {

        VectorType v = new VectorType(c.getDataReal(1));

        String labelx = "x";
        String labely = "y";

        v.setX(c.getDataReal(0));

        v.setIndependentLabels(0, labelx);
        v.setDependentLabels(0, labely);

        return v;
    }

    /**
     * Initialses information specific to CurveToVect.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setDefaultOutputNodes(1);
        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Resets CurveToVect
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of the parameters.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to CurveToVect, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "Curve";
    }

    /**
     * @return a string containing the names of the types output from CurveToVect, each separated by a white space.
     */
    public String outputTypes() {
        return "VectorType";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Converters.html";
    }
}














