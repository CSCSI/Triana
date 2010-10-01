package imageproc.processing.effects;

import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A Negate unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 29 Aug 1997
 */
public class Negate extends Unit {

    /**
     * ********************************************* ** USER CODE of Negate goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputAtNode(0);
        PixelMap pixelMap = trianaPixelMap.getPixelMap();
        PixelMap newPixelMap = new PixelMap(pixelMap);
        int[] newPixels = newPixelMap.getPixels();
        int p, a, r, g, b;

        for (int i = 0; i < newPixels.length; i++) {
            p = newPixels[i];

            a = p & 0xff000000;
            r = (p >> 16) & 0xff;
            g = (p >> 8) & 0xff;
            b = p & 0xff;

            r ^= 255;
            g ^= 255;
            b ^= 255;

            newPixels[i] = a | (r << 16) | (g << 8) | b;
        }

        output(new TrianaPixelMap(newPixelMap));
    }

    /**
     * Initialses information specific to Negate.
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
     * Reset's Negate
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Negate's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads Negate's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Negate, each separated by a white
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
        return "Negates an image";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Negate.html";
    }
}













