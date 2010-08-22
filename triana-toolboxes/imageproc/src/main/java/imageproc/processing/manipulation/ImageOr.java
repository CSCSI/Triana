package imageproc.processing.manipulation;

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


import org.trianacode.gui.windows.ErrorDialog;
import triana.types.OldUnit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A ImageOr unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 10 Sep 1997
 */
public class ImageOr extends OldUnit {

    /**
     * ********************************************* ** USER CODE of ImageOr goes here    ***
     * *********************************************
     */
    public void process() {
        int i, p1, p2, r, g, b;

        TrianaPixelMap trianaPixelMap1 = (TrianaPixelMap) getInputNode(0);
        TrianaPixelMap trianaPixelMap2 = (TrianaPixelMap) getInputNode(1);
        PixelMap pixelMap1 = trianaPixelMap1.getPixelMap();
        PixelMap pixelMap2 = trianaPixelMap2.getPixelMap();

        if ((pixelMap1.width != pixelMap2.width) ||
                (pixelMap1.height != pixelMap2.height)) {
            new ErrorDialog(
                    getName() + ": Error, incompatible image dimensions." + "\n" +
                            "Dimensions for the source images must be similar!");
            stop();  // stops the scheduler and hence this process!
        } else {
            PixelMap newPixelMap = new PixelMap(pixelMap1);
            int[] pixels1 = pixelMap1.pixels;
            int[] pixels2 = pixelMap2.pixels;
            int[] newPixels = newPixelMap.pixels;

            for (i = 0; i < pixels1.length; i++) {
                p1 = pixels1[i];
                p2 = pixels2[i];

                r = ((p1 >> 16) & 0xff) | ((p2 >> 16) & 0xff);
                g = ((p1 >> 8) & 0xff) | ((p2 >> 8) & 0xff);
                b = (p1 & 0xff) & (p2 | 0xff);

                newPixels[i] = 0xff000000 | (r << 16) | (g << 8) | b;
            }

            output(new TrianaPixelMap(newPixelMap));
        }
    }


    /**
     * Initialses information specific to ImageOr.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Reset's ImageOr
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ImageOr's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads ImageOr's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ImageOr, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * @return a string containing the names of the types output from ImageOr, each separated by a white space.
     */
    public String outputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Logically ors the values of the pixels in two PixelMaps.";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "ImageOr.html";
    }
}













