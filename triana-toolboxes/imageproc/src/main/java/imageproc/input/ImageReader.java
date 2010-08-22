package imageproc.input;

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

import org.trianacode.gui.panels.FilePanel;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.FileName;
import triana.types.OldUnit;
import triana.types.TrianaImage;
import triana.types.TrianaPixelMap;
import triana.types.TrianaType;

/**
 * A ImageReader : A unit which imports a GIF or JPG file.
 *
 * @author Ian Taylor
 * @version 1.0 alpha 21 Aug 1997
 */
public class ImageReader extends OldUnit {

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
            TrianaType t = getInputNode(0);
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

        setResizableInputs(true);
        setMaximumInputNodes(1);
        setResizableOutputs(true);
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
     * @return a string containing the names of the types allowed to be input to ImageReader, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "FileName";
    }

    /**
     * @return a string containing the names of the types output from ImageReader, each separated by a white space.
     */
    public String outputTypes() {
        return "TrianaPixelMap";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ImageReader.html";
    }
}













