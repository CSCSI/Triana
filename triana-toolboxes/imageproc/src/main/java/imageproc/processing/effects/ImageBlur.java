package imageproc.processing.effects;

import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.Convolution;
import triana.types.image.PixelMap;

/**
 * A ImageBlur unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 10 Sep 1997
 */
public class ImageBlur extends Unit {

    /**
     * ********************************************* ** USER CODE of ImageBlur goes here    ***
     * *********************************************
     */
    public void process() {
        int[] blur = {1, 1, 1, 1, 1, 1, 1, 1, 1};
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputAtNode(0);
        Convolution convolution = new Convolution(trianaPixelMap.getPixelMap(), blur);
        PixelMap newPixelMap = convolution.getResult();
        output(new TrianaPixelMap(newPixelMap));
    }


    /**
     * Initialses information specific to ImageBlur.
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
     * Reset's ImageBlur
     */
    public void reset() {
        super.reset();
    }

    public void setParameter(String name, String value) {
    }

    /**
     * Loads ImageBlur's parameters of from the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ImageBlur, each separated by a white
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
        return "Blurs an image";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ImageBlur.html";
    }
}













