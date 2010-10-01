package imageproc.input;

import java.awt.Image;

import org.trianacode.gui.panels.FilePanel;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.FileName;
import triana.types.TrianaImage;
import triana.types.TrianaPixelMap;
import triana.types.TrianaType;

/**
 * A ImageReader : A unit which imports a GIF or JPG file.
 *
 * @author Ian Taylor
 * @version 1.0 alpha 21 Aug 1997
 */
public class ImageReader extends Unit {

    String imageName = "";

    TrianaImage currentImage = null;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "A unit to read in a GIF/JPEG file from disk";
    }

    /**
     * ********************************************* ** USER CODE of ImageReader goes here    ***
     * *********************************************
     */
    public void process() {
        if (getTask().getDataInputNodeCount() > 0) {
            TrianaType t = (TrianaType) getInputAtNode(0);
            process(((FileName) t).getFile());
        } else {
            process(imageName);
        }
    }

    /**
     * outputs the image for the specified filename
     */
    private void process(String filename) {
        Image image = FileUtils.getImage(filename);
        currentImage = new TrianaImage(image);

        output(new TrianaPixelMap(currentImage));
    }


    /**
     * Initialses information specific to ImageReader.
     */
    public void init() {
        super.init();

//        setResizableInputs(true);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);                      
    }

    /**
     * Reset's ImageReader
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ImageReader's parameters to the parameter file.
     */
//    public void saveParameters() {
//        saveParameter(FilePanel.FILE_NAME, FileUtils.convertToVirtualName(imageName));
//    }

    /**
     * Loads ImageReader's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
        if (name.equals(FilePanel.FILE_NAME)) {
            imageName = FileUtils.convertFromVirtualName(value);
        }
    }    

    public String[] getInputTypes() {
        return new String[]{"triana.types.FileName"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.TrianaPixelMap"};
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ImageReader.html";
    }
}













