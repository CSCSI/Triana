package imageproc.output;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */


import java.awt.Image;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import org.trianacode.gui.panels.FilePanel;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.OldUnit;
import triana.types.TrianaImage;
import triana.types.TrianaPixelMap;
import triana.types.image.GifEncoder;

/**
 * A WriteGIF unit to ..
 *
 * @author ian
 * @version 1.0 Final 11 Aug 2000
 */
public class WriteGIF extends OldUnit {
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
            ErrorDialog.show("No output file chosen in " + getName() + " please choose one now");
            doubleClick();
        }

        BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(imageName));
        GifEncoder ge = new GifEncoder(image, bo);
//        Dimension d = trimage.getImageDimensions();
        //      ge.encodeStart(d.width, d.height);
        ge.encode();
//        ge.encodeDone();
        //    bo.flush();
        bo.close();
    }

    public void doubleClick() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Write GIF");

        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            imageName = chooser.getSelectedFile().getAbsolutePath();
            updateParameter("file", imageName);
        }
    }

    /**
     * Initialses information specific to WriteGIF.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
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
    public void starting() {
        super.starting();
    }

    /**
     * Saves ImageReader's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter(FilePanel.FILE_NAME, FileUtils.convertToVirtualName(imageName));
    }

    /**
     * Loads ImageReader's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
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
    public String inputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * @return a string containing the names of the types output from WriteGIF, each separated by a white space.
     */
    public String outputTypes() {
        return "none";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put WriteGIF's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "WriteGIF.html";
    }
}



