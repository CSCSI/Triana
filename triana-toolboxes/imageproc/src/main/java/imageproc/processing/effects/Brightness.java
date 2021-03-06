package imageproc.processing.effects;

import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;


/**
 * A Brightness unit to ..
 *
 * @author Melanie Lewis
 * @version 1.0 alpha 20 Aug 1997
 */
public class Brightness extends Unit {

    // some examples of parameters

    //public int addvalue;

    /**
     * The UnitWindow for Brightness
     */
//    IntScrollerWindow myWindow;
    String parameterName = "Brightness";
    int min = -255, max = 255;

    /**
     * ********************************************* ** USER CODE of Brightness goes here    ***
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

            int addvalue = Integer.parseInt((String) getTask().getParameter(parameterName));

            r += addvalue;
            g += addvalue;
            b += addvalue;
            r = r > 255 ? 255 : (r < 0 ? 0 : r);
            g = g > 255 ? 255 : (g < 0 ? 0 : g);
            b = b > 255 ? 255 : (b < 0 ? 0 : b);

            newPixels[i] = a | (r << 16) | (g << 8) | b;
        }

        output(new TrianaPixelMap(newPixelMap));
    }

    /**
     * Initialses information specific to Brightness.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

//        setUseGUIBuilder(true);

/*        addvalue = 0;

        myWindow = new IntScrollerWindow(this, "Enter Brigtness level (-255 to +255)");
        myWindow.setParameterName(parameterName);
        myWindow.setValues(min, max, addvalue);
        myWindow.updateWidgets(); */


        String guilines = "";
        guilines += "Enter brightness level (" + min + " to " + max + ") $title " + parameterName + " IntScroller " + min
                + " " + max + " 0\n";
        setGUIBuilderV2Info(guilines);        
    }

//    public void setGUIInformation() {
//        addGUILine("Enter brightness level (" + min + " to " + max + ") $title " + parameterName + " IntScroller " + min
//                + " " + max + " 0");
//    }


    /**
     * Reset's Brightness
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Brightness's parameters to the parameter file.
     */
//    public void saveParameters() {
//        if (getTask().isParameterName(parameterName)) {
//            saveParameter(parameterName, (String) getTask().getParameter(parameterName));
//        } else {
//            saveParameter(parameterName, 0);
//        }
//
//        saveParameter("minimum", min);
//        saveParameter("maximum", max);
//    }


    public void setParameter(String name, String value) {
        /*if (name.equals(parameterName))
            addvalue= strToInt(value);
        else if (name.equals("minimum")) 
            min = strToInt(value);
        else if (name.equals("maximum")) 
            max = strToInt(value);*/
    }


    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
        //myWindow.setValues(min,max,addvalue);
        //myWindow.updateWidgets();
    }

    /**
     * @return a string containing the names of the types allowed to be input to Brightness, each separated by a white
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
        return "Alters the brightness of a TrianaPixelMap.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Brightness.html";
    }

    /**
     * @return Brightness's parameter window sp that Triana 
     * can move and display it.
     */
    /*public Window getParameterWIndow() {
        return myWindow;
        }*/
}

















