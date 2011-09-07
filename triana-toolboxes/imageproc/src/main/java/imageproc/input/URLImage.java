package imageproc.input;

import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.TrianaImage;
import triana.types.TrianaPixelMap;

import java.awt.*;

/**
 * A URLImage unit which loads an image from a HTTP adddress
 *
 * @author Ian Taylor
 * @author Melanie Rhianna Lewis
 * @version 2.0 10 Aug 2000
 */
public class URLImage extends Unit {
    String imageUrlString;

    /**
     * ********************************************* ** USER CODE of ImageLoader goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaImage trianaImage = null;

        Object data = getInputAtNode(0);
        if (data != null) {
            imageUrlString = data.toString();
        }
        if (!imageUrlString.equals("")) {
            Image image = FileUtils.getImage(imageUrlString);
            trianaImage = new TrianaImage(image);
        }

        if (trianaImage == null) {
            //      ErrorDialog.show(null, getTask().getToolName() + ": " + Env.getString("ImageError"));
            //stop();  // stops the scheduler and hence this process!
            System.out.println("image error");
            notifyError("Image Error", new Throwable("Image Error"));
        } else {
            output(new TrianaPixelMap(trianaImage));
        }
    }


    /**
     * Initialses information specific to ImageLoader.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        imageUrlString = "";
    }

    /**
     * Reset's ImageLoader
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ImageLoader's parameters to the parameter file.
     */
//    public void saveParameters() {
//        saveParameter("Url", imageUrlString);
//    }

    /**
     * Loads ImageLoader's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
        if (name.equals("Url")) {
            imageUrlString = value;
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to ImageLoader, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.String"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.TrianaPixelMap"};
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Loads an image from a URL";
    }

    /**
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "URLImage.html";
    }


//    public void doubleClick() {
//        updateParameter("Url", Input.aString("Enter the URL of the GIF/JPEG file below :"));
//    }
}

















