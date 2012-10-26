package imageproc.output;

import org.trianacode.gui.panels.FilePanel;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.TrianaImage;
import triana.types.TrianaPixelMap;
import triana.types.image.GifEncoder;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 17/09/2012
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class WriteGifOutputFile extends Unit {
    String imageName = "";

    /**
     * ********************************************* ** USER CODE of WriteGIF goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        TrianaPixelMap input = (TrianaPixelMap) getInputAtNode(0);
        TrianaImage trimage = ((TrianaPixelMap) input).getTrianaImage();
        Image image = trimage.getImage();

        if ((imageName == null) || (imageName.equals(""))) {
            imageName = "output_" + System.currentTimeMillis() + ".gif";
//            ErrorDialog.show("No output file chosen in " + getTask().getToolName() + " please choose one now");
//            doubleClick();
        }

        File file = new File(imageName);
        BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(file));
        GifEncoder ge = new GifEncoder(image, bo);
//        Dimension d = trimage.getImageDimensions();
        //      ge.encodeStart(d.width, d.height);
        ge.encode();
//        ge.encodeDone();
        //    bo.flush();
        bo.close();

        output(file);
    }

    public void doubleClick() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Write GIF");

        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            imageName = chooser.getSelectedFile().getAbsolutePath();
            parameterUpdate("file", imageName);
        }
    }

    /**
     * Initialses information specific to WriteGIF.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);
    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
    }

    /**
     * Called when the stop button is pressed within the MainTriana Window
     */
    public void stopping() {
        super.stopping();
    }

    /**
     * Called when the start button is pressed within the MainTriana Window
     */
//    public void starting() {
//        super.starting();
//    }
//
//    /**
//     * Saves ImageReader's parameters to the parameter file.
//     */
//    public void saveParameters() {
//        saveParameter(FilePanel.FILE_NAME, FileUtils.convertToVirtualName(imageName));
//    }

    /**
     * Loads ImageReader's parameters of from the parameter file.
     */
    public void parameterUpdate(String name, String value) {
        if (name.equals(FilePanel.FILE_NAME)) {
            imageName = FileUtils.convertFromVirtualName(value);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to WriteGIF, each separated by a white
     *         space.
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.String"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.TrianaPixelMap"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put WriteGIF's brief description here";
    }

    /**
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "WriteGIF.html";
    }
}





