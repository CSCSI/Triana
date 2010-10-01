package imageproc.processing.effects;

import java.awt.event.ActionEvent;

import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;
import triana.types.util.Str;

/**
 * A Threshold unit to ..
 *
 * @author Melanie Lewis
 * @version 1.0 alpha 20 Aug 1997
 */
public class Threshold extends Unit {

    // some examples of parameters

    public int threshold = 128;

    /**
     * The UnitWindow for Threshold
     */
    //IntScrollerWindow myWindow;
    String parameterName = "Threshold";
    int min = 0, max = 255;

    /**
     * ********************************************* ** USER CODE of Threshold goes here    ***
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

            r = (r > threshold) ? 0xff : 0;
            g = (g > threshold) ? 0xff : 0;
            b = (b > threshold) ? 0xff : 0;

            newPixels[i] = a | (r << 16) | (g << 8) | b;
        }

        output(new TrianaPixelMap(newPixelMap));
    }

    /**
     * Initialses information specific to Threshold.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Threshold level (" + min + " to " + max + ") $title " + parameterName + " IntScroller " + min + " " + max + " " + threshold;
        setGUIBuilderV2Info(guilines);



        /*myWindow = new IntScrollerWindow(this, "Enter Threshold level (0 to 255)");
        myWindow.setParameterName(parameterName);
        myWindow.setValues(min, max, threshold);
        myWindow.updateWidgets();*/
    }

//    public void setGUIInformation() {
//        addGUILine("Threshold level (" + min + " to " + max + ") $title " + parameterName + " IntScroller " + min + " " + max + " " + threshold);
//    }

    /**
     * Reset's Threshold
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Brightness's parameters to the parameter file.
     */
//    public void saveParameters() {
//        saveParameter(parameterName, threshold);
//        saveParameter("minimum", min);
//        saveParameter("maximum", max);
//    }


    public void parameterUpdate(String name, String value) {
        if (name.equals(parameterName)) {
            threshold = Str.strToInt(value);
        } else if (name.equals("minimum")) {
            min = Str.strToInt(value);
        } else if (name.equals("maximum")) {
            max = Str.strToInt(value);
        }
    }


    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
        //myWindow.setValues(min,max,threshold);
        //myWindow.updateWidgets();
    }

    /**
     * @return a string containing the names of the types allowed to be input to Threshold, each separated by a white
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
        return "Thresholds a TrianaPixelMap at the given value.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Threshold.html";
    }

    /**
     * @return Threshold's parameter window sp that Triana 
     * can move and display it.
     */
    /*public Window getParameterWIndow() {
        return myWindow;
        }*/


    /**
     * Captures the events thrown out by ScrollerWindow.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//
//        /*if (e.getSource() == myWindow.slider) {
//        threshold = myWindow.getValue();
//        }*/
//    }
}

















