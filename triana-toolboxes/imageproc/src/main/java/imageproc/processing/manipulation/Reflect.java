package imageproc.processing.manipulation;

import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A Reflect unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 29 Aug 1997
 */
public class Reflect extends Unit {

    /**
     * ********************************************* ** USER CODE of Reflect goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputAtNode(0);
        PixelMap pixelMap = trianaPixelMap.getPixelMap();
        int[] pixels = pixelMap.getPixels();
        int width = pixelMap.getWidth();
        PixelMap newPixelMap = new PixelMap(pixelMap);
        int[] newPixels = newPixelMap.getPixels();
        int r1 = width;
        int i1 = r1 - 1;
        int i2 = 0;
        int x, y;

        for (y = 0; y < pixelMap.getHeight(); y++) {
            for (x = 0; x < width; x++) {
                newPixels[i2++] = pixels[i1--];
            }
            r1 += width;
            i1 = r1 - 1;
        }

        output(new TrianaPixelMap(newPixelMap));
    }

    /**
     * Initialses information specific to Reflect.
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
     * Reset's Reflect
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Reflect's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads Reflect's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Reflect, each separated by a white
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
        return "Reflects a TrianaPixelMap.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Reflect.html";
    }
}













