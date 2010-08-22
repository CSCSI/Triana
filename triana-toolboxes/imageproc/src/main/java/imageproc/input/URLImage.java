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

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.Input;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.util.Env;
import triana.types.OldUnit;
import triana.types.TrianaImage;
import triana.types.TrianaPixelMap;

/**
 * A URLImage unit which loads an image from a HTTP adddress
 *
 * @author Ian Taylor
 * @author Melanie Rhianna Lewis
 * @version 2.0 10 Aug 2000
 */
public class URLImage extends OldUnit {
    String imageUrlString;

    /**
     * ********************************************* ** USER CODE of ImageLoader goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaImage trianaImage = null;

        if (!imageUrlString.equals("")) {
            Image image = FileUtils.getImage(imageUrlString);
            trianaImage = new TrianaImage(image);
        }

        if (trianaImage == null) {
            ErrorDialog.show(null,
                    getName() + ": " + Env.getString("ImageError"));
            stop();  // stops the scheduler and hence this process!
        } else {
            output(new TrianaPixelMap(trianaImage));
        }
    }


    /**
     * Initialses information specific to ImageLoader.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

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
    public void saveParameters() {
        saveParameter("Url", imageUrlString);
    }

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
    public String inputTypes() {
        return "";
    }

    /**
     * @return a string containing the names of the types output from ImageLoader, each separated by a white space.
     */
    public String outputTypes() {
        return "TrianaPixelMap";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Loads an image from a URL";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "URLImage.html";
    }


    public void doubleClick() {
        updateParameter("Url", Input.aString("Enter the URL of the GIF/JPEG file below :"));
    }
}

















