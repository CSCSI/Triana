package imageproc.input;

import java.awt.Image;

import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.TrianaImage;
import triana.types.TrianaPixelMap;

/**
 * LoadMovie loads in a set of GIF or JPEG files and outputs them one by one to the next units.  Connect this to a
 * ImageView to view a set of gifs as a movie. Use a Pause unit allows you to change the speed of the animation.
 *
 * @author Ian Taylor
 * @version 1.0 9 Aug 2000
 */
public class LoadMovie extends Unit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "A unit to read in a sequence of GIF or JPEG images (e.g. image1.gif, image2.gif...)";
    }

    /**
     * ********************************************* ** USER CODE of LoadMovie goes here    ***
     * *********************************************
     */
    public void process() {
        String base = getFileBase((String) getTask().getParameter(LoadMoviePanel.FILE_NAME));
        String ext = getFileExtension((String) getTask().getParameter(LoadMoviePanel.FILE_NAME));
        int current = getFrameNumber((String) getTask().getParameter(LoadMoviePanel.FILE_NAME));
        ;
        int last = current + Integer.parseInt((String) getTask().getParameter(LoadMoviePanel.FRAME_COUNT));
        int start = current;

        if ((base != null) && (ext != null) && (current != -1)) {
            while (current < last) {
                Image image = FileUtils.getImage(base + current + ext);
                TrianaImage currentImage = new TrianaImage(image);
                TrianaPixelMap pixelmap = new TrianaPixelMap(currentImage);

                pixelmap.setSequenceNumber(current - start);

                output(pixelmap);

                current++;
            }
        }
    }

    /**
     * @return the base filename for the filename, i.e. the filename without the frame number or extension
     */
    private String getFileBase(String filename) {
        if (filename.lastIndexOf('.') > -1) {
            return filename.substring(0, filename.lastIndexOf('.') - getFrameLetters(filename));
        } else {
            return null;
        }
    }

    /**
     * @return the file extension for the filename, e.g. '.gif'
     */
    private String getFileExtension(String filename) {
        if (filename.lastIndexOf('.') > -1) {
            return filename.substring(filename.lastIndexOf('.'));
        } else {
            return null;
        }
    }

    /**
     * @return the frame number for the filename, e.g. for 'image97.gif' this would return 97
     */
    private int getFrameNumber(String filename) {
        if ((filename.lastIndexOf('.') > -1) && (getFrameLetters(filename) > 0)) {
            return Integer.parseInt(filename.substring(filename.lastIndexOf('.') - getFrameLetters(filename),
                    filename.lastIndexOf('.')));
        } else {
            return -1;
        }
    }

    /**
     * @return the number of letters in the frame number for the filename, e.g. for 'image1234.gif' this would return 4
     */
    private int getFrameLetters(String filename) {
        int count = 0;

        while (Character.isDigit(filename.charAt(filename.lastIndexOf('.') - (count + 1)))) {
            count++;
        }

        return count;
    }


    /**
     * Initialses information specific to LoadMovie.
     */
    public void init() {
        super.init();

//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

    }


//    /**
//     * Saves LoadMovie's parameters to the parameter file.
//     */
//    public void saveParameters() {
//        super.saveParameters();
//        saveParameter(LoadMoviePanel.FRAME_COUNT, 1);
//        saveParameter(LoadMoviePanel.FILE_NAME, "");
//    }

    /**
     * Loads LoadMovie's parameters of from the parameter file.
     */
    public void parameterUpdate(String name, String value) {
        super.setParameter(name, value);
    }


    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }


    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "LoadMovie.html";
    }
}















