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
import triana.types.image.ImageHistogram;
import triana.types.image.PixelMap;

/**
 * A EnhContrast unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 10 Sep 1997
 */
public class EnhContrast extends OldUnit {

    /**
     * ********************************************* ** USER CODE of EnhContrast goes here    ***
     * *********************************************
     */
    public void process() {
        int i, p, r, g, b;

        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputNode(0);
        PixelMap pixelMap = trianaPixelMap.getPixelMap();
        PixelMap newPixelMap = new PixelMap(pixelMap);
        ImageHistogram histogram = pixelMap.getIntensityHistogram();
        double min = (double) histogram.getMinValue();
        double max = (double) histogram.getMaxValue();
        double scale = max != min ? 255 / (max - min) : 255;
        int[] pixels = pixelMap.getPixels();
        int[] newPixels = newPixelMap.getPixels();

        System.err.println("min = " + min + ", max = " + max + ", scale = " +
                scale);

        for (i = 0; i < newPixels.length; i++) {
            p = pixels[i];

            r = (int) ((((double) ((p >> 16) & 0xff) - min)) * scale);
            r = r > 255 ? 255 : (r < 0 ? 0 : r);
            g = (int) ((((double) ((p >> 8) & 0xff) - min)) * scale);
            g = g > 255 ? 255 : (g < 0 ? 0 : g);
            b = (int) (((double) ((p & 0xff) - min)) * scale);
            b = b > 255 ? 255 : (b < 0 ? 0 : b);

            newPixels[i] = 0xff000000 | (r << 16) | (g << 8) | b;
        }

        output(new TrianaPixelMap(newPixelMap));
    }


    /**
     * Initialses information specific to EnhContrast.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Reset's EnhContrast
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves EnhContrast's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads EnhContrast's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to EnhContrast, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * @return a string containing the names of the types output from EnhContrast, each separated by a white space.
     */
    public String outputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Enhances the contrast of an image";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "EnhContrast.html";
    }
}













