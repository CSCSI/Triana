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


import triana.types.OldUnit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A GradientEdge unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 04 Sep 1997
 */
public class GradientEdge extends OldUnit {
    /**
     * The UnitWindow for GradientEdge
     */

    PixelMap sourceMap, destMap;

    /**
     * ********************************************* ** USER CODE of GradientEdge goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap sourceGPM = (TrianaPixelMap) getInputNode(0);
        TrianaPixelMap destGPM = new TrianaPixelMap(sourceGPM);
        sourceMap = sourceGPM.getPixelMap();
        destMap = destGPM.getPixelMap();
        processImage();
        output(destGPM);
    }


    /**
     * Initialses information specific to GradientEdge.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * Reset's GradientEdge
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves GradientEdge's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads GradientEdge's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to GradientEdge, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * @return a string containing the names of the types output from GradientEdge, each separated by a white space.
     */
    public String outputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "A gradient edge detector";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "GradientEdge.html";
    }

    void processImage() {
        int x, y;
        int iA, iB, iC, iD, iE, iF, iG, iH, iI; // pixel interators
        int pA, pB, pC, pD, pE, pF, pG, pH, pI; // pixel values (32 bit)
        int rA, rB, rC, rD, rE, rF, rG, rH, rI; // red channel
        int gA, gB, gC, gD, gE, gF, gG, gH, gI; // green channel
        int bA, bB, bC, bD, bE, bF, bG, bH, bI; // blue channel
        int r, g, b;                            // results
        int width = sourceMap.getWidth();
        int[] source = sourceMap.getPixels();
        int[] dest = destMap.getPixels();

        System.out.println("Processing...");

        for (y = 1; y < sourceMap.getHeight() - 1; y++) {
            iE = 1 + y * width;
            iD = iE - 1;
            iF = iE + 1;
            iA = iD - width;
            iB = iE - width;
            iC = iF - width;
            iG = iD + width;
            iH = iE + width;
            iI = iF + width;

            for (x = 1; x < width - 1; x++) {
                pA = source[iA];
                pB = source[iB];
                pC = source[iC];
                pD = source[iD];
                pE = source[iE];
                pF = source[iF];
                pG = source[iG];
                pH = source[iH];
                pI = source[iI];

                rA = (pA >> 16) & 0xff;
                rB = (pB >> 16) & 0xff;
                rC = (pC >> 16) & 0xff;
                rD = (pD >> 16) & 0xff;
                rE = (pE >> 16) & 0xff;
                rF = (pF >> 16) & 0xff;
                rG = (pG >> 16) & 0xff;
                rH = (pH >> 16) & 0xff;
                rI = (pI >> 16) & 0xff;

                r = (Math.abs(rE - rB) + Math.abs(rE - rH)) / 2;

                gA = (pA >> 8) & 0xff;
                gB = (pB >> 8) & 0xff;
                gC = (pC >> 8) & 0xff;
                gD = (pD >> 8) & 0xff;
                gE = (pE >> 8) & 0xff;
                gF = (pF >> 8) & 0xff;
                gG = (pG >> 8) & 0xff;
                gH = (pH >> 8) & 0xff;
                gI = (pI >> 8) & 0xff;

                g = (Math.abs(gE - gB) + Math.abs(gE - gH)) / 2;

                bA = pA & 0xff;
                bB = pB & 0xff;
                bC = pC & 0xff;
                bD = pD & 0xff;
                bE = pE & 0xff;
                bF = pF & 0xff;
                bG = pG & 0xff;
                bH = pH & 0xff;
                bI = pI & 0xff;

                b = (Math.abs(bE - bB) + Math.abs(bE - bH)) / 2;

                dest[iE] = (pE & 0xff000000) | (r << 16) | (g << 8) | b;

                iA++;
                iB++;
                iC++;
                iD++;
                iE++;
                iF++;
                iG++;
                iH++;
                iI++;
            }
        }
    }
}

















