package imageproc.processing.effects;

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


import org.trianacode.gui.panels.UnitPanel;
import triana.types.OldUnit;
import triana.types.TrianaPixelMap;
import triana.types.image.Convolution;
import triana.types.image.PixelMap;
import triana.types.util.StringSplitter;

/**
 * A Convolve unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 04 Sep 1997
 */
public class Convolve extends OldUnit {
    /**
     * The UnitPanel for Convolve
     */
    ConvolveWeights myPanel;
    int weights[] = new int[9];

    /**
     * ********************************************* ** USER CODE of Convolve goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputNode(0);
        Convolution convolution = new Convolution(trianaPixelMap.getPixelMap(), weights);
        PixelMap newPixelMap = convolution.getResult();
        output(new TrianaPixelMap(newPixelMap));
    }

    /**
     * Initialses information specific to Convolve.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

        myPanel = new ConvolveWeights();
        myPanel.setObject(this);
    }

    /**
     * Reset's Convolve
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Convolve's parameters to the parameter file.
     */
    public void saveParameters() {
        String str = "";

        for (int i = 0; i < 9; ++i) {
            str += weights[i] + " ";
        }

        saveParameter("weights", str.trim());
    }

    /**
     * Loads Convolve's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
        StringSplitter sv = new StringSplitter(value);
        for (int i = 0; i < sv.size(); ++i) {
            weights[i] = (int) strToDouble(sv.at(i));
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to Convolve, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * @return a string containing the names of the types output from Convolve, each separated by a white space.
     */
    public String outputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Performs a convolution of an image";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Convolve.html";
    }


    /**
     * @return Convolve's parameter window sp that Triana can move and display it.
     */
    public UnitPanel getParameterPanel() {
        return myPanel;
    }
}


