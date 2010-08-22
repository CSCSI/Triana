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
 * A Snb unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 04 Sep 1997
 */
public class Snb extends OldUnit {
    /**
     * The UnitWindow for Snb
     */

    PixelMap sourceMap, destMap;

    /**
     * ********************************************* ** USER CODE of Snb goes here    ***
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
     * Initialses information specific to Snb.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * Reset's Snb
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Snb's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads Snb's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Snb, each separated by a white space.
     */
    public String inputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * @return a string containing the names of the types output from Snb, each separated by a white space.
     */
    public String outputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Sets the value of a pixel to the value of the smallest 8 way connected pixel";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Snb.html";
    }

    void processImage() {
        int x, y;
        int iA, iB, iC, iD, iE, iF, iG, iH, iI; // pixel interators
        int pA, pB, pC, pD, pE, pF, pG, pH, pI; // pixel values (32 bit)
        int rA, rB, rC, rD, rE, rF, rG, rH, rI; // red channel
        int gA, gB, gC, gD, gE, gF, gG, gH, gI; // green channel
        int bA, bB, bC, bD, bE, bF, bG, bH, bI; // blue channel
        int red, green, blue;                   // results
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

                red = rA;
                red = red < rB ? red : rB;
                red = red < rC ? red : rC;
                red = red < rD ? red : rD;
                red = red < rE ? red : rE;
                red = red < rF ? red : rF;
                red = red < rG ? red : rG;
                red = red < rH ? red : rH;
                red = red < rI ? red : rI;

                gA = (pA >> 8) & 0xff;
                gB = (pB >> 8) & 0xff;
                gC = (pC >> 8) & 0xff;
                gD = (pD >> 8) & 0xff;
                gE = (pE >> 8) & 0xff;
                gF = (pF >> 8) & 0xff;
                gG = (pG >> 8) & 0xff;
                gH = (pH >> 8) & 0xff;
                gI = (pI >> 8) & 0xff;

                green = gA;
                green = green < gB ? green : gB;
                green = green < gC ? green : gC;
                green = green < gD ? green : gD;
                green = green < gE ? green : gE;
                green = green < gF ? green : gF;
                green = green < gG ? green : gG;
                green = green < gH ? green : gH;
                green = green < gI ? green : gI;

                bA = pA & 0xff;
                bB = pB & 0xff;
                bC = pC & 0xff;
                bD = pD & 0xff;
                bE = pE & 0xff;
                bF = pF & 0xff;
                bG = pG & 0xff;
                bH = pH & 0xff;
                bI = pI & 0xff;

                blue = bA;
                blue = blue < bB ? blue : bB;
                blue = blue < bC ? blue : bC;
                blue = blue < bD ? blue : bD;
                blue = blue < bE ? blue : bE;
                blue = blue < bF ? blue : bF;
                blue = blue < bG ? blue : bG;
                blue = blue < bH ? blue : bH;
                blue = blue < bI ? blue : bI;

                dest[iE] = (pE & 0xff000000) | red << 16 | green << 8 | blue;

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

















