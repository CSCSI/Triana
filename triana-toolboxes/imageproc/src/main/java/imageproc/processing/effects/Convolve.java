package imageproc.processing.effects;

import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.Convolution;
import triana.types.image.PixelMap;
import triana.types.util.Str;
import triana.types.util.StringSplitter;

/**
 * A Convolve unit to ..
 *
 * @author Melanie Rhianna Lewis
 * @version 1.0 alpha 04 Sep 1997
 */
public class Convolve extends Unit {
    /**
     * The UnitPanel for Convolve
     */
    ConvolveWeights myPanel;
    int weights[] = new int[9];

    /**
     * ********************************************* ** USER CODE of Convolve goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputAtNode(0);
        Convolution convolution = new Convolution(trianaPixelMap.getPixelMap(), weights);
        PixelMap newPixelMap = convolution.getResult();
        output(new TrianaPixelMap(newPixelMap));
    }

    /**
     * Initialses information specific to Convolve.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
        
        myPanel = new ConvolveWeights();
        myPanel.setObject(this);
    }

    /**
     * Reset's Convolve
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Convolve's parameters to the parameter file.
     */
    public void saveParameters() {
        String str = "";

        for (int i = 0; i < 9; ++i) {
            str += weights[i] + " ";
        }

        parameterUpdate("weights", str.trim());
    }

    /**
     * Loads Convolve's parameters of from the parameter file.
     */
    public void parameterUpdate(String name, String value) {
        StringSplitter sv = new StringSplitter(value);
        for (int i = 0; i < sv.size(); ++i) {
            weights[i] = (int) Str.strToDouble(sv.at(i));
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to Convolve, each separated by a white
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
        return "Performs a convolution of an image";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Convolve.html";
    }


    /**
     * @return Convolve's parameter window sp that Triana can move and display it.
     */
    public UnitPanel getParameterPanel() {
        return myPanel;
    }
}


