package imageproc.processing.effects;

import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.ImageHistogram;
import triana.types.image.PixelMap;

/**
 * A EnhContrast unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 10 Sep 1997
 */
public class EnhContrast extends Unit {

    /**
     * ********************************************* ** USER CODE of EnhContrast goes here    ***
     * *********************************************
     */
    public void process() {
        int i, p, r, g, b;

        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputAtNode(0);
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

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
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













