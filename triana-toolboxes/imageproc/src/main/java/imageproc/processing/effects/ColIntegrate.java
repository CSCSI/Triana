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


import triana.types.OldUnit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A ColumnIntegrate unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 29 Aug 1997
 */
public class ColIntegrate extends OldUnit {

    /**
     * ********************************************* ** USER CODE of ColumnIntegrate goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputNode(0);
        PixelMap pixelMap = trianaPixelMap.getPixelMap();
        PixelMap newPixelMap = new PixelMap(pixelMap);
        int[] newPixels = newPixelMap.getPixels();
        int width = newPixelMap.getWidth();
        int height = newPixelMap.getHeight();
        int x, y, p, a, r, g, b, tr, tg, tb;
        int i = 0;

        for (x = 0; x < width; x++) {
            tr = tg = tb = 0;
            i = x;
            for (y = 0; y < height; y++) {
                p = newPixels[i];

                a = p & 0xff000000;
                r = (p >> 16) & 0xff;
                g = (p >> 8) & 0xff;
                b = p & 0xff;

                newPixels[i] = (y == 0) ? p : a | ((tr / y) << 16) | ((tg / y) << 8) | (tb / y);

                tr += r;
                tg += g;
                tb += b;
                i += width;
            }
        }

        output(new TrianaPixelMap(newPixelMap));
    }

    /**
     * Initialses information specific to ColumnIntegrate.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Reset's ColumnIntegrate
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ColumnIntegrate's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads ColumnIntegrate's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ColumnIntegrate, each separated by a
     *         white space.
     */
    public String inputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * @return a string containing the names of the types output from ColumnIntegrate, each separated by a white space.
     */
    public String outputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Integrates the value all the pixels above of a pixel.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ColIntegrate.html";
    }
}













