package signalproc.input;

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


import java.util.Random;

import triana.types.MatrixType;
import triana.types.OldUnit;


/**
 * A MakeMatrix unit to create a MatrixType data object containing a matrix with given paramters.
 *
 * @author B Schutz
 * @version 1.0 30 Dec 2000
 */
public class MakeMatrix extends OldUnit {

    int columns = 2;
    int rows = 2;
    String fill = "zeros";
    String symmetry = "(none)";


    /**
     * ********************************************* ** USER CODE of MakeMatrix goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        double[][] matrix = new double[rows][columns];
        double x;
        int j, k, l;

        if (fill.equals("randoms")) {
            Random ran = new Random();
            if ((rows != columns) || symmetry.equals("(none)")) {
                for (j = 0; j < rows; j++) {
                    for (k = 0; k < columns; k++) {
                        matrix[j][k] = ran.nextDouble();
                    }
                }
            } else if (symmetry.equals("symmetric")) {
                for (j = 0; j < rows; j++) {
                    for (k = j; k < rows; k++) {
                        matrix[j][k] = ran.nextDouble();
                    }
                }
                for (j = 1; j < rows; j++) {
                    for (k = 0; k < j; k++) {
                        matrix[j][k] = matrix[k][j];
                    }
                }
            } else if (symmetry.equals("antisymmetric")) {
                for (j = 0; j < rows; j++) {
                    for (k = j + 1; k < rows; k++) {
                        matrix[j][k] = ran.nextDouble();
                    }
                }
                for (j = 1; j < rows; j++) {
                    for (k = 0; k < j; k++) {
                        matrix[j][k] = -matrix[k][j];
                    }
                }
            } else if (symmetry.equals("diagonal")) {
                for (j = 0; j < rows; j++) {
                    matrix[j][j] = ran.nextDouble();
                }
            }
        } else if (fill.equals("IntegersByRow")) {
            l = 0;
            if ((rows != columns) || symmetry.equals("(none)")) {
                for (j = 0; j < rows; j++) {
                    for (k = 0; k < columns; k++) {
                        matrix[j][k] = l++;
                    }
                }
            } else if (symmetry.equals("symmetric")) {
                for (j = 0; j < rows; j++) {
                    for (k = j; k < rows; k++) {
                        matrix[j][k] = l++;
                    }
                }
                for (j = 1; j < rows; j++) {
                    for (k = 0; k < j; k++) {
                        matrix[j][k] = matrix[k][j];
                    }
                }
            } else if (symmetry.equals("antisymmetric")) {
                l = 1;
                for (j = 0; j < rows; j++) {
                    for (k = j + 1; k < rows; k++) {
                        matrix[j][k] = l++;
                    }
                }
                for (j = 1; j < rows; j++) {
                    for (k = 0; k < j; k++) {
                        matrix[j][k] = -matrix[k][j];
                    }
                }
            } else if (symmetry.equals("diagonal")) {
                for (j = 0; j < rows; j++) {
                    matrix[j][j] = l++;
                }
            }
        } else if (fill.equals("IntegersByColumn")) {
            l = 0;
            if ((rows != columns) || symmetry.equals("(none)")) {
                for (k = 0; k < columns; k++) {
                    for (j = 0; j < rows; j++) {
                        matrix[j][k] = l++;
                    }
                }
            } else if (symmetry.equals("symmetric")) {
                for (k = 0; k < rows; k++) {
                    for (j = k; j < rows; j++) {
                        matrix[j][k] = l++;
                    }
                }
                for (k = 1; k < rows; k++) {
                    for (j = 0; j < k; j++) {
                        matrix[j][k] = matrix[k][j];
                    }
                }
            } else if (symmetry.equals("antisymmetric")) {
                l = 1;
                for (k = 0; k < rows; k++) {
                    for (j = k + 1; j < rows; j++) {
                        matrix[j][k] = l++;
                    }
                }
                for (k = 1; k < rows; k++) {
                    for (j = 0; j < k; j++) {
                        matrix[j][k] = -matrix[k][j];
                    }
                }
            } else if (symmetry.equals("diagonal")) {
                for (j = 0; j < rows; j++) {
                    matrix[j][j] = l++;
                }
            }
        }

        MatrixType m = new MatrixType(matrix);

        output(m);

    }


    /**
     * Initialses information specific to MakeMatrix.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setMinimumInputNodes(0);
        setMaximumInputNodes(0);
        setDefaultInputNodes(0);
        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Number of columns $title columns IntScroller 1 100 2");
        addGUILine("Number of rows $title rows IntScroller 1 100 2");
        addGUILine("Fill matrix with: $title fill Choice zeros randoms IntegersByRow IntegersByColumn");
        addGUILine(
                "For square matrices, give symmetry: $title symmetry Choice (none) symmetric antisymmetric diagonal");
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
     * Saves MakeMatrix's parameters.
     */
    public void saveParameters() {
        saveParameter("columns", columns);
        saveParameter("rows", rows);
        saveParameter("fill", fill);
        saveParameter("symmetry", symmetry);
    }


    /**
     * Used to set each of MakeMatrix's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("columns")) {
            columns = strToInt(value);
        }
        if (name.equals("rows")) {
            rows = strToInt(value);
        }
        if (name.equals("fill")) {
            fill = value;
        }
        if (name.equals("symmetry")) {
            symmetry = value;
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to MakeMatrix, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "none";
    }

    /**
     * @return a string containing the names of the types output from MakeMatrix, each separated by a white space.
     */
    public String outputTypes() {
        return "MatrixType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Creates and fills a 2D matrix";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "MakeMatrix.html";
    }
}




