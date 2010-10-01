package imageproc.processing.color;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A SetGreen unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 10 Sep 1997
 */
public class SetGreen extends Unit {

    /**
     * ********************************************* ** USER CODE of SetGreen goes here    ***
     * *********************************************
     */
    public void process() {
        int i, p1, p2, r, g, b;

        TrianaPixelMap trianaPixelMap1 = (TrianaPixelMap) getInputAtNode(0);
        TrianaPixelMap trianaPixelMap2 = (TrianaPixelMap) getInputAtNode(1);
        PixelMap pixelMap1 = trianaPixelMap1.getPixelMap();
        PixelMap pixelMap2 = trianaPixelMap2.getPixelMap();

        if ((pixelMap1.width != pixelMap2.width) ||
                (pixelMap1.height != pixelMap2.height)) {
            new ErrorDialog(
                    getTask().getToolName() + ": Error, incompatible image dimensions." + "\n" +
                            "Dimensions for the source images must be similar!");
            //stop();  // stops the scheduler and hence this process!
        } else {
            PixelMap newPixelMap = new PixelMap(pixelMap1);
            int[] pixels1 = pixelMap1.pixels;
            int[] pixels2 = pixelMap2.pixels;
            int[] newPixels = newPixelMap.pixels;

            for (i = 0; i < pixels1.length; i++) {
                p1 = pixels1[i];
                p2 = pixels2[i];

                newPixels[i] = 0xff000000 | (p1 & 0x00ff00) | (p2 & 0xff00ff);
            }

            output(new TrianaPixelMap(newPixelMap));
        }
    }


    /**
     * Initialses information specific to SetGreen.
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
     * Reset's SetGreen
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves SetGreen's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads SetGreen's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to SetGreen, each separated by a white
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
        return "Combines the green channel of one pixel map with the red and blue of another.";
    }

    /**
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "SetGreen.html";
    }
}













