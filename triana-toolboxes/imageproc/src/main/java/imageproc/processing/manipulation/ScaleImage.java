package imageproc.processing.manipulation;

import java.awt.Canvas;
import java.awt.Image;
import java.awt.event.ActionEvent;

import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;
import triana.types.image.PixelMap;
import triana.types.util.Str;

/**
 * A ScaleImage unit to ..
 *
 * @author Melanie Lewis
 * @version 1.0 alpha 20 Aug 1997
 */
public class ScaleImage extends Unit {

    // some examples of parameters

    public int scale = 100;

    /**
     * The UnitWindow for Threshold
     */
    //IntScrollerWindow myWindow;
    String parameterName = "scale";
    int min = 10, max = 1000;

    /**
     * ********************************************* ** USER CODE of Threshold goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) getInputAtNode(0);
        PixelMap newPixelMap = new PixelMap(trianaPixelMap.getPixelMap());

        Canvas canvas = new Canvas(); // Used as an image consumer
        Image image = canvas.createImage(newPixelMap.getImageProducer());

        int x = (newPixelMap.getWidth() * scale) / 100;
        int y = (newPixelMap.getHeight() * scale) / 100;

        output(new TrianaPixelMap(new
                PixelMap(image.getScaledInstance(x, y, Image.SCALE_SMOOTH))));
    }

    /**
     * Initialses information specific to Threshold.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Scaling factor (" + min + "% to " + max + "%) $title " + parameterName + " IntScroller " + min + " "
                + max + " " + scale;
        setGUIBuilderV2Info(guilines);

/*        myWindow = new IntScrollerWindow(this, "Enter the scaling factor in %");
        myWindow.setParameterName(parameterName);
        myWindow.setValues(min, max, scale);
        myWindow.updateWidgets();*/
    }


//    public void setGUIInformation() {
//        addGUILine("Scaling factor (" + min + "% to " + max + "%) $title " + parameterName + " IntScroller " + min + " "
//                + max + " " + scale);
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
//        saveParameter(parameterName, scale);
//        saveParameter("minimum", min);
//        saveParameter("maximum", max);
//    }


    public void parameterUpdate(String name, String value) {
        if (name.equals(parameterName)) {
            scale = Str.strToInt(value);
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
        //myWindow.setValues(min,max,scale);
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
        return "Scales a TrianaPixelMap.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ScaleImage.html";
    }

    /**
     * @return Threshold's parameter window sp that Triana 
     * can move and display it.
     */
    /*public Window getParameterWIndow() {
        return myWindow;
        } */


    /**
     * Captures the events thrown out by ScrollerWindow.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//
//        /*if (e.getSource() == myWindow.slider) {
//       scale = myWindow.getValue();
//       } */
//    }
}
