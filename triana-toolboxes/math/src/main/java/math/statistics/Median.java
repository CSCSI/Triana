package math.statistics;

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


import triana.types.Const;
import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.util.FlatArray;
import triana.types.util.Str;
import triana.types.util.TrianaSort;

/**
 * A Median unit to compute the median (or other percentile value) of the data in any input data set. The percentile
 * value is chosen by the user in the parameter window. The value returned is the value of the smallest element that is
 * larger than or equal to the given percentage of the elements of the input data set. The median is the 50th
 * percentile.
 *
 * @author B F Schutz
 * @version 1.1 9 June 2001
 */
public class Median extends OldUnit {

    int dv = 0;
    double percentile = 0.50;


    /**
     * ********************************************* ** USER CODE of Median goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType input = (GraphType) getInputAtNode(0);

        FlatArray flatR;
        double[] dataR, dataSorted;
        int k, len;

        /* The method is to sort the data into ascending order and
       * find the index that is equal to the given percentage of
       * the total length of the array, minus 1 in order to take
       * account of the fact that index counting starts at zero.
       * If the index computed in this way is fractional, then
       * the index value is rounded up.
       */

        if (input.isArithmeticArray(dv)) {
            flatR = new FlatArray(input.getDataArrayReal(dv));
            dataR = (double[]) flatR.getFlatArray();
            dataSorted = TrianaSort.mergeSort(dataR, 1.0);
            len = dataR.length;
            k = (int) Math.ceil(percentile * len) - 1;
            output(new Const(dataSorted[k]));
        }

    }


    /**
     * Initialses information specific to Median.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setRequireDoubleInputs(false);
        setCanProcessDoubleArrays(false);

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine(
                "Which dependent variable do you want to compute the sum of squares of? $title dv IntScroller 0 5 0");
        addGUILine(
                "This unit will return the smallest data value larger than or equal to the given fraction of all the data values. For the median set the fraction to 0.50. $title percentile Scroller 0 1 0.5");
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
     * Saves Median's parameters.
     */
    public void saveParameters() {
        saveParameter("dv", dv);
        saveParameter("percentile", percentile);
    }


    /**
     * Used to set each of Median's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("dv")) {
            dv = strToInt(value);
        }
        if (name.equals("percentile")) {
            percentile = Str.strToDouble(value);
            if (percentile < 0) {
                percentile = 0;
            } else if (percentile > 1.0) {
                percentile = 1.0;
            }
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Median, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType";
    }

    /**
     * @return a string containing the names of the types output from Median, each separated by a white space.
     */
    public String outputTypes() {
        return "Const";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Compute median or other percentile value of the elements of the data";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Median.html";
    }
}




