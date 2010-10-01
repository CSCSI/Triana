package imageproc.processing.manipulation;

import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A RotateLeft unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 29 Aug 1997
 */
public class RotateLeft extends Unit {

    /**
     * ********************************************* ** USER CODE of RotateLeft goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputAtNode(0);
        PixelMap pixelMap = trianaPixelMap.getPixelMap();
        int[] pixels = pixelMap.getPixels();
        int width = pixelMap.getWidth();
        int height = pixelMap.getHeight();
        PixelMap newPixelMap = new PixelMap(height, width);
        int[] newPixels = newPixelMap.getPixels();
        int i1 = 0;
        int i2, x, y;

        for (y = 0; y < height; y++) {
            i2 = y + newPixels.length - height;
            for (x = 0; x < width; x++) {
                newPixels[i2] = pixels[i1++];
                i2 -= height;
            }
        }

        output(new TrianaPixelMap(newPixelMap));
    }

    /**
     * Initialses information specific to RotateLeft.
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
     * Reset's RotateLeft
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves RotateLeft's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads RotateLeft's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to RotateLeft, each separated by a white
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
        return "Rotate a TrianaPixelMap 90deg to the left.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "RotateLeft.html";
    }
}













