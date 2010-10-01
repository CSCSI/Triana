package imageproc.processing.color;

import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A ToGreyScale unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 21 Aug 1997
 */
public class ToGreyScale extends Unit {

    /**
     * ********************************************* ** USER CODE of ToGreyScale goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputAtNode(0);
        PixelMap pixelMap = trianaPixelMap.getPixelMap();
        PixelMap newPixelMap = new PixelMap(pixelMap);
        int[] newPixels = newPixelMap.getPixels();
        int p, a, r, g, b, gs;

        for (int i = 0; i < newPixels.length; i++) {
            p = newPixels[i];

            a = p & 0xff000000;
            r = (p >> 16) & 0xff;
            g = (p >> 8) & 0xff;
            b = p & 0xff;

            gs = (r + g + b) / 3;

            newPixels[i] = a | (gs << 16) | (gs << 8) | gs;
        }

        output(new TrianaPixelMap(newPixelMap));
    }

    /**
     * Initialses information specific to ToGreyScale.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
    }


    /**
     * Reset's ToGreyScale
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ToGreyScale's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads ToGreyScale's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ToGreyScale, each separated by a white
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
        return "Coverts a colour TrianaPixelMap to its grey scale equivalent";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ToGreyScale.html";
    }
}













