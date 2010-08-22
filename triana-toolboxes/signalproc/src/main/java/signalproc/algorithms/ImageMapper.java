package signalproc.algorithms;

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


import triana.types.ImageMap;
import triana.types.MatrixType;
import triana.types.OldUnit;
import triana.types.util.FlatArray;


/**
 * A ImageMapper unit to create an ImageMap from an input matrix; this produces a false-color representation of a matrix
 * of numerical values.
 *
 * @author B F Schutz
 * @version 1.1 09 January 2001
 */
public class ImageMapper extends OldUnit {

    String red = "(auto)";
    String green = "(auto)";
    String blue = "(auto)";
    boolean low = true; // true if low values mapped to black
    boolean high = true; // true if high values mapped to white
    boolean log = false; // true if mapping is logarithmic in data values


    /**
     * ********************************************* ** USER CODE of ImageMapper goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        double minVal, midVal, maxVal;

        MatrixType input = (MatrixType) getInputNode(0);
        ImageMap im = null;
        setOutputType(ImageMap.class);
        Object inputData = input.getGraphArrayReal(0);
        double[][] matrix = (double[][]) FlatArray.toDoubleArray(inputData);

        if (red.indexOf("auto") > -1) {
            minVal = FlatArray.minArray(matrix);
        } else {
            minVal = Double.valueOf(red).doubleValue();
        }
        if (minVal <= 0) {
            if (log) {
                println("Minimum value in the input matrix was non-positive: log plot not possible.");
            }
            log = false;
        }
        if (blue.indexOf("auto") > -1) {
            maxVal = FlatArray.maxArray(matrix);
        } else {
            maxVal = Double.valueOf(blue).doubleValue();
        }
        if (green.indexOf("auto") > -1) {
            if (log) {
                midVal = Math.sqrt(minVal * maxVal);
            } else {
                midVal = (minVal + maxVal) / 2.0;
            }
        } else {
            midVal = Double.valueOf(green).doubleValue();
        }

        im = new ImageMap(input, minVal, midVal, maxVal, !low, high, log);

        output(im);
    }


    /**
     * Initialses information specific to ImageMapper.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Give lowest data value to be mapped to a color (red) $title red TextField (auto)");
        addGUILine("Give mid-range data value of the mapping (green) $title green TextField (auto)");
        addGUILine("Give highest data value to be mapped to a color (blue) $title blue TextField (auto)");
        addGUILine("Map values below minimum to black? $title low Checkbox true");
        addGUILine("Map values above maximum to white? $title high Checkbox true");
        addGUILine("Scale values logarithmically? $title log Checkbox false");
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
     * Saves ImageMapper's parameters.
     */
    public void saveParameters() {
        saveParameter("red", red);
        saveParameter("green", green);
        saveParameter("blue", blue);
        saveParameter("low", low);
        saveParameter("high", high);
        saveParameter("log", log);
    }


    /**
     * Used to set each of ImageMapper's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("red")) {
            red = value;
        }
        if (name.equals("green")) {
            green = value;
        }
        if (name.equals("blue")) {
            blue = value;
        }
        if (name.equals("low")) {
            low = strToBoolean(value);
        }
        if (name.equals("high")) {
            high = strToBoolean(value);
        }
        if (name.equals("log")) {
            log = strToBoolean(value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ImageMapper, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "MatrixType";
    }

    /**
     * @return a string containing the names of the types output from ImageMapper, each separated by a white space.
     */
    public String outputTypes() {
        return "ImageMap";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Convert a matrix to a color image";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ImageMapper.html";
    }
}




