package imageproc.processing.detection;

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
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A CountBlobs unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 29 Aug 1997
 */
public class CountBlobs extends OldUnit {

    /**
     * ********************************************* ** USER CODE of CountBlobs goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputNode(0);
        PixelMap newPixelMap = new PixelMap(trianaPixelMap.getPixelMap());
        int i = 0;
        int count = 0;

        for (int y = 0; y < newPixelMap.getHeight(); y++) {
            for (int x = 0; x < newPixelMap.getWidth(); x++) {
                if ((newPixelMap.getPixel(x, y) & 0xffffff) != 0) {
                    count++;
                    newPixelMap.fillErase(x, y);
                }
            }
        }

        System.err.println("count=" + count);
        output(new Const(count));
    }

    /**
     * Initialses information specific to CountBlobs.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Reset's CountBlobs
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves CountBlobs's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads CountBlobs's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to CountBlobs, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * @return a string containing the names of the types output from CountBlobs, each separated by a white space.
     */
    public String outputTypes() {
        return "Const";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Counts the number of non black objects in a TrianaPixelMap.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "CountBlobs.html";
    }
}













