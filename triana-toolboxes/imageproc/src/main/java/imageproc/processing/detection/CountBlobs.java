package imageproc.processing.detection;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;

/**
 * A CountBlobs unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 29 Aug 1997
 */
public class CountBlobs extends Unit {

    /**
     * ********************************************* ** USER CODE of CountBlobs goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputAtNode(0);
        PixelMap newPixelMap = new PixelMap(trianaPixelMap.getPixelMap());
        int i = 0;
        int count = 0;

        for (int y = 0; y < newPixelMap.getHeight(); y++) {
            for (int x = 0; x < newPixelMap.getWidth(); x++) {
                if ((newPixelMap.getPixel(x, y) & 0xffffff) != 0) {
                    count++;
                    newPixelMap.fillErase(x, y);
                }
            }
        }

        System.err.println("count=" + count);
        output(new Const(count));
    }

    /**
     * Initialses information specific to CountBlobs.
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
     * Reset's CountBlobs
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves CountBlobs's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads CountBlobs's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to CountBlobs, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.TrianaPixelMap"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.Const"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Counts the number of non black objects in a TrianaPixelMap.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "CountBlobs.html";
    }
}













