package imageproc.processing.effects;

import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A ColumnIntegrate unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 29 Aug 1997
 */
public class ColIntegrate extends Unit {

    /**
     * ********************************************* ** USER CODE of ColumnIntegrate goes here    ***
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

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);    }


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













