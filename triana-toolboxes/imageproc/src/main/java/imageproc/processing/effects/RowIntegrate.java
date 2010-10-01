package imageproc.processing.effects;

import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A RowIntegrate unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 29 Aug 1997
 */
public class RowIntegrate extends Unit {

    /**
     * ********************************************* ** USER CODE of RowIntegrate goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputAtNode(0);
        PixelMap pixelMap = trianaPixelMap.getPixelMap();
        PixelMap newPixelMap = new PixelMap(pixelMap);
        int[] newPixels = newPixelMap.getPixels();
        int width = newPixelMap.getWidth();
        int height = newPixelMap.getHeight();
        int x, y, p, a, r, g, b, tr, tg, tb;
        int i = 0;

        for (y = 0; y < height; y++) {
            tr = tg = tb = 0;
            for (x = 0; x < width; x++) {
                p = newPixels[i];

                a = p & 0xff000000;
                r = (p >> 16) & 0xff;
                g = (p >> 8) & 0xff;
                b = p & 0xff;

                newPixels[i++] = (x == 0) ? 0 : a | ((tr / x) << 16) | ((tg / x) << 8) | (tb / x);

                tr += r;
                tg += g;
                tb += b;
            }
        }

        output(new TrianaPixelMap(newPixelMap));
    }

    /**
     * Initialses information specific to RowIntegrate.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
    }


    /**
     * Reset's RowIntegrate
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves RowIntegrate's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads RowIntegrate's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to RowIntegrate, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
         return new String[]{"triana.types.TrianaPixelMap"};
     }

     public String[] getOutputTypes() {
         return new String[]{"triana.types.TrianaPixelMap"};
     }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Integrates the value all the pixels to the left of a pixel";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "RowIntegrate.html";
    }
}













