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


import java.util.ArrayList;
import java.util.Hashtable;

import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.Task;
import triana.types.Const;
import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.TrianaType;
import triana.types.util.FlatArray;


/**
 * An AccumStat unit to calculate element-by-element statistical averages over the most recent input data sets, which
 * may be of type GraphType or Const.  The number of sets to accumulate (average) and the number of statistical measures
 * to output are set in the parameter window. The unit works by accumulating the most recent input data sets (up to the
 * desired number) and, at each time-step, outputting the given number of average moments of the accumulated input sets.
 * The input sets may be real or complex, but they must be compatible with one another. Default values of the parameters
 * are to output one statistic (the mean) averaged over the 10 most recent input sets. </p><p> The unit outputs
 * simultaneously (from an appropriate number of output nodes) a collection of data sets of the same type as the input,
 * one for each desired statistic (eg, mean, variance, etc.) The output sets contain the statistics of each of the
 * dependent data arrays of the input sets that are of arithmetic type. The statistics are computed element-by-element,
 * so that the output arrays have the same dimensionality and size as the input arrays. Dependent data that are not
 * arithmetic arrays (numbers) are passed directly through to the output. </p><p> The unit can produce mean, variance,
 * skewness (as the third moment, not normalized by the variance), kurtosis (again not normalized), and higher moments
 * up to order 8.  Data are output in the order of the moment: node 0 = mean, node 1 = variance, node 2 = skew, node 3 =
 * kurtosis, etc.  Apart from the mean, the formula for the nth moment is < (data - mean)^n >, where <..> indicates an
 * average over the accumulated sets. Note that the moments are not normalized by the variance, as is the case for the
 * usual definition of skew and kurtosis. </p><p> The accumulation continues in either single-step or continuous mode,
 * as long as the network of units is not reset. At the beginning of a sequence, before the number of input sets reaches
 * the number given in the parameter window, the output after each computation is a correct average of the sets so far
 * received. After the number of inputs has reached the desired number, then the average is made over the appropriate
 * number of the most recent input sets. If the number of moments is increased during the computation then the output
 * for the extra moments will not be exactly correct until the desired number of inputs has been reached. </p><p> The
 * algorithms saves a rolling queue of input data sets with a length equal to the number being averaged, so if input
 * sets are large or the number to be averaged is large then the memory storage requirements can grow rapidly. Users
 * should exercise caution. </p><p> If the number of data sets to accumulate is changed in the parameter window during a
 * sequence of computations, the unit will behave correctly, either accumulating more (if it is increased) or forgetting
 * the earliest ones (if it is decreased). If the number of moments to be output is reset in the parameter window to a
 * number that is more than the current number of output nodes, then the number of nodes is automatically increased.  If
 * number of nodes is larger than the number of moments to be output, then the extra nodes are used to duplicate output
 * data, just as for simple units that produce only one output. Data are output to the excess nodes in a cyclic manner:
 * eg if there are 2 moments and 4 nodes the data will be output as node0:mean, node1:variance, node2:mean,
 * node3:variance. If the network is reset then the accumulated data will be lost and the averaging will start anew.
 *
 * @author B.F. Schutz
 * @version 2.0 18 August 2000
 */
public class AccumStat extends OldUnit {

    /**
     * A Hashtable storing the most recent input data sets.
     */
    Hashtable tableOfDataTables;

    /**
     * The number of data sets to include in the statistics
     */
    int numberOfSets;

    /**
     * A Hashtable storing ArrayLists containing the accumulated powers of all the data arrays.
     */
    Hashtable tableOfPowers;

    /**
     * The highest moment that can be computed by this unit (1 is for the mean).
     */
    int momentLimit = 8;

    /**
     * The highest power to be computed (1 is for computing the mean). This is set when the user chooses the number of
     * output nodes. It must be less than or equal to momentLimit.
     */
    public int highestPower = 1;

    /**
     * The highest power that was computed for the previous data set. It must be less than or equal to momentLimit.
     */
    public int oldHighestPower = 1;

    /**
     * Number of output nodes
     */
    int numberOfNodes;

    /**
     * Sequential counter for all the input data sets
     */
    int currentSet = 0;

    /**
     * The oldest data set in dataTable for each moment
     */
    int[] oldestSet = new int[momentLimit];

    /**
     * Switch to initialize the accumulation process
     */
    boolean firstCallToProcess = true;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Produces cumulative statistics on a sequence of input data";
    }

    /**
     * Initialses information specific to AccumStat.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

        setUseGUIBuilder(true);


        reset();
    }


    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Number of data sets to average $title sets IntScroller 1 100 10");
        addGUILine("Maximum moment to compute (mean = 1, variance = 2, ... $title power IntScroller 1 8 1");
    }


    /**
     * Resets AccumStat
     */
    public void reset() {
        super.reset();

        numberOfSets = 20;
        currentSet = 0;
        tableOfDataTables = new Hashtable(4);
        tableOfPowers = new Hashtable(4);
        oldestSet = new int[momentLimit];
        firstCallToProcess = true;

    }

    /**
     * Saves AccumStat's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter("sets", numberOfSets);
        saveParameter("power", highestPower);
    }

    /**
     * Loads AccumStat's parameters from the parameter file.
     */
    public void setParameter(String name, String value) {
        if (name.equals("sets")) {
            numberOfSets = strToInt(value);
        }
        if (name.equals("power")) {
            highestPower = strToInt(value);
            highestPower = Math.min(momentLimit, highestPower);

            Task task = getTask();

            try {
                while (highestPower > task.getDataOutputNodeCount()) {
                    task.addDataOutputNode();
                }

                while (highestPower < getTask().getDataOutputNodeCount()) {
                    task.removeDataOutputNode(task.getDataOutputNode(task.getDataOutputNodeCount() - 1));
                }
            } catch (NodeException except) {
                notifyError(except.getMessage());
            }
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to AccumStat, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from AccumStat, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "AccumStat.html";
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
     * The main functionality of AccumStat goes here
     */
    public void process() {
        Object input, output;
        Object previous = null;
        int j, tableIndex;
        int[] numberSetsActuallyPresent = new int[momentLimit];
        int numberToRemove = 0;
        int realPoints = 1;
        int points = 1;
        boolean removeOldest = false;
        boolean complexInput;
        Class inputClass;
        Class previousInputClass = null;
        boolean newLimit = true;
        ArrayList outputs;

        input = getInputAtNode(0);
        inputClass = input.getClass();
        setOutputType(inputClass);
        if (previousInputClass != null) {
            if (inputClass != previousInputClass) {
                reset();
            } else if (input instanceof GraphType) {
                if (!((GraphType) input).isCompatible(previous)) {
                    reset();
                }
            }
        }
        previous = input;
        previousInputClass = inputClass;


        if ((numberOfSets <= 0) || (highestPower <= 0)) {
            output(input);
            return;
        }

        // If highest moment to compute has changed since
        // the last data set, set the boolean flag newLimit.

        newLimit = (highestPower != oldHighestPower);

        numberOfNodes = outputNodes();

        outputs = new ArrayList(highestPower);
        for (int out = 0; out < highestPower; out++) {
            if (input instanceof TrianaType) {
                outputs.add(((TrianaType) input).copyMe());
            } else {
                outputs.add(input);
            }
        }

        // If the number of data sets to average has decreased since the
        // last data set, or if the desired accumulated number of sets
        // has been reached, set the flag removeOldest.

        numberToRemove = currentSet - oldestSet[0] - numberOfSets + 1;
        if (numberToRemove > 0) {
            removeOldest = true;
        }

        if (input instanceof GraphType) {

            FlatArray temp;
            int dv, rc, kc, i;
            Integer removeSet;
            ArrayList powers, moments;
            Hashtable dataTable;
            double[] data = {0};
            double[] scratchOne = {0};
            double[] scratchTwo = {0};
            double[] removeData = {0};
            double[] momentOne, momentTwo, momentThree, momentFour, momentFive, momentSix, momentSeven, momentEight,
                    meanSquare;

            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {

                if (((GraphType) input).isArithmeticArray(dv)) {
                    complexInput = ((GraphType) input).isDependentComplex(dv);
                    rc = (complexInput) ? 1 : 0;
                    for (kc = 0; kc <= rc; kc++) {
                        tableIndex = 2 * dv + kc;
                        temp = (kc == 0) ? new FlatArray(((GraphType) input).getDataArrayReal(dv))
                                : new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        data = (double[]) temp.getFlatArray();
                        points = data.length;

                        if (firstCallToProcess) {
                            firstCallToProcess = false;

                            // Set up sizes of arrays stored in ArrayList powers on the first
                            // call to this unit, and insert powers into its Hashtable.

                            powers = new ArrayList(highestPower);
                            for (j = 0; j < highestPower; j++) {
                                scratchOne = new double[points];
                                for (i = 0; i < points; i++) {
                                    scratchOne[i] = 0.0;
                                }
                                powers.add(j, scratchOne);
                                oldestSet[j] = 0;
                            }
                            tableOfPowers.put(new Integer(tableIndex), powers);

                            dataTable = new Hashtable(numberOfSets);
                            tableOfDataTables.put(new Integer(tableIndex), dataTable);
                        } else {
                            // If the highest power has changed since
                            // the last data set, readjust the size of the ArrayList powers.
                            // If the highest moment goes up, the extra moments will not
                            // be calculated correctly until the number of data sets equals
                            // the total numberOfSets.

                            powers = (ArrayList) tableOfPowers.get(new Integer(tableIndex));
                            powers.ensureCapacity(highestPower);
                            dataTable = (Hashtable) tableOfDataTables.get(new Integer(tableIndex));

                            if (newLimit) {
                                if (highestPower > oldHighestPower) {
                                    for (j = oldHighestPower; j < highestPower; j++) {
                                        scratchOne = new double[points];
                                        for (i = 0; i < points; i++) {
                                            scratchOne[i] = 0.0;
                                        }
                                        powers.add(scratchOne);
                                        oldestSet[j] = currentSet;
                                    }
                                } else {
                                    for (j = highestPower; j < oldHighestPower; j++) {
                                        powers.remove(j);
                                    }
                                }
                            }

                        }
                        // Put new data into Hashtable dataTable

                        dataTable.put(new Integer(currentSet), data);

                        // Add powers of new data set into the ArrayList powers for as many
                        // moments as are required.  Use scratchOne as temporary storage
                        // for the latest power, so that the next can be calculated by
                        // multiplication rather than by invoking Math.pow().

                        scratchOne = new double[points];
                        scratchTwo = new double[points];
                        System.arraycopy((double[]) powers.get(0), 0, scratchTwo, 0, points);
                        for (i = 0; i < points; i++) {
                            scratchOne[i] = data[i];
                            scratchTwo[i] += scratchOne[i];
                        }
                        powers.set(0, scratchTwo);

                        if (highestPower > 1) {
                            for (j = 1; j < highestPower; j++) {
                                scratchTwo = new double[points];
                                System.arraycopy((double[]) powers.get(j), 0, scratchTwo, 0, points);
                                for (i = 0; i < points; i++) {
                                    scratchOne[i] *= data[i];
                                    scratchTwo[i] += scratchOne[i];
                                }
                                powers.set(j, scratchTwo);
                            }
                        }

                        // Remove any data sets that need to be removed from the average.
                        // This is the normal condition in a steady state, where one data set is
                        // removed, and it also happens if the number of sets to average
                        // goes down, so that more have to be removed.

                        if (removeOldest) {
                            for (int k = 1; k <= numberToRemove; k++) {
                                removeSet = new Integer(oldestSet[0]);
                                removeData = (double[]) dataTable.get(removeSet);
                                dataTable.remove(removeSet);
                                oldestSet[0]++;

                                scratchTwo = new double[points];
                                System.arraycopy((double[]) powers.get(0), 0, scratchTwo, 0, points);
                                for (i = 0; i < points; i++) {
                                    scratchOne[i] = removeData[i];
                                    scratchTwo[i] -= scratchOne[i];
                                }
                                powers.set(0, scratchTwo);

                                if (highestPower > 1) {
                                    for (j = 1; j < highestPower; j++) {
                                        if (oldestSet[j] < oldestSet[0]) {
                                            oldestSet[j]++;
                                            scratchTwo = new double[points];
                                            System.arraycopy((double[]) powers.get(j), 0, scratchTwo, 0, points);
                                            for (i = 0; i < points; i++) {
                                                scratchOne[i] *= removeData[i];
                                                scratchTwo[i] -= scratchOne[i];
                                            }
                                            powers.set(j, scratchTwo);
                                        }
                                    }
                                }

                            }
                        }

                        for (j = 0; j < highestPower; j++) {
                            numberSetsActuallyPresent[j] = currentSet - oldestSet[j] + 1;
                        }


/* Compute moments (up to 8) according to the following formulas, where
   mn is the nth moment about the mean = [sum_i(x_i - <x>)^n]/N, (except
   for m1, which is the mean <x> itself), where N = points, and where
   pn stands for the sum of the n'th powers = powers.get(n-1)
    m1 = p1/N (mean)
    m2 = p2/N - (p1/N)^2
       = p2/N - m1^2 (variance)
    m3 = p3/N - 3*p2*p1/N^2 + 2*(p1/N)^3
       = p3/N - 3*m1*m2 - m1^3 ( = skewness * variance^(3/2) )
    m4 = p4/N - 4*p3*p1/N^2 + 6*p2*p1^2/N^3 - 3*(p1/N)^4
       = p4/N - 4*m1*m3 - 6*m1^2*m2 - m1^4 ( = kurtosis * variance^2 )
    m5 = p5/N - 5*p4*p1/N^2 + 10*p3*p1^2/N^3 - 10*p2*p1^3/N^4 +
        4*(p1/N)^5
       = p5/N - 5*m1*m4 - 10*m1^2*m3 - 10*m1^3*m2 - m1^5
    m6 = p6/N - 6*p5*p1/N^2 + 15*p4*p1^2/N^3 - 20*p3*p1^3/N^4 +
        15*p2*p1^4/N^5 - 5*(p1/N)^6
       = p6/N - 6*m1*m5 - 15*m1^2*m4 - 20*m1^3*m3 - 15*m1^4*m2 - m1^6
    m7 = p7/N - 7*p6*p1/N^2 + 21*p5*p1^2/N^3 - 35*p4*p1^3/N^4 +
        35*p3*p1^4/N^5 - 21*p2*p1^5/N^6 + 6*(p1/N)^7
       = p7/N - 7*m1*m6 - 21*m1^2*m5 - 35*m1^3*m4 - 35*m1^4*m3 -
        21*m1^5*m2 - m1^7
    m8 = p8/N - 8*p7*p1/N^2 + 28*p6*p1^2/N^3 - 56*p5*p1^3/N^4 +
        70*p4*p1^4/N^5 - 56*p3*p1^5/N^6 + 28*p2*p1^6/N^7 - 7*(p1/N)^8
       = p8/N - 8*m1*m7 - 28*m1^2*m6 - 56*m1^3*m5 - 70*m1^4*m4 -
        56*m1^5*m3 - 28*m1^6*m2 - m1^8

    We stop at 8 only because it seems unlikely that moments above 8
    will be used very often.  We use the second versions of the
    expressions (involving lower moments rather than powers) to avoid
    dividing by N (numberOfSets) all the time. When we divide by N
    we use the number of sets actually accumulated for the moment
    in question, since they can be different if the maximum number
    of moments has changed. This gives a reasonable approximation
    to the right result (but not an exact one) until all moments
    are being computed using the same number of sets.
*/

                        moments = new ArrayList(highestPower);
                        momentOne = new double[points];
                        momentTwo = null;
                        momentThree = null;
                        momentFour = null;
                        momentFive = null;
                        momentSix = null;
                        momentSeven = null;
                        momentEight = null;
                        meanSquare = null;

                        System.arraycopy((double[]) powers.get(0), 0, momentOne, 0, points);
                        for (i = 0; i < points; ++i) {
                            momentOne[i] = momentOne[i] / numberSetsActuallyPresent[0];
                        }
                        //			moments.setElementAt( momentOne, 0 );
                        temp.setFlatArray(momentOne);
                        if (kc == 0) {
                            ((GraphType) outputs.get(0)).setDataArrayReal(temp.restoreArray(true), dv);
                        } else {
                            ((GraphType) outputs.get(0)).setDataArrayImag(temp.restoreArray(true), dv);
                        }

                        if (highestPower > 1) {
                            meanSquare = new double[points];
                            System.arraycopy(momentOne, 0, meanSquare, 0, points);
                            momentTwo = new double[points];
                            System.arraycopy((double[]) powers.get(1), 0, momentTwo, 0, points);
                            // m2 = p2/N - m1^2
                            for (i = 0; i < points; ++i) {
                                meanSquare[i] *= meanSquare[i]; // contains squares of the mean (m1^2)
                                momentTwo[i] = momentTwo[i] / numberSetsActuallyPresent[1] - meanSquare[i];
                            }
                            //			    moments.setElementAt( momentTwo, 1 );
                            temp.setFlatArray(momentTwo);
                            if (kc == 0) {
                                ((GraphType) outputs.get(1)).setDataArrayReal(temp.restoreArray(true), dv);
                            } else {
                                ((GraphType) outputs.get(1)).setDataArrayImag(temp.restoreArray(true), dv);
                            }
                        }

                        if (highestPower > 2) {
                            momentThree = new double[points];
                            System.arraycopy((double[]) powers.get(2), 0, momentThree, 0, points);
                            // m3 = p3/N - 3*m1*m2 - m1^3  = p3/N - m1*(3*m2 + m1^2)
                            for (i = 0; i < points; ++i) {
                                momentThree[i] = momentThree[i] / numberSetsActuallyPresent[2] -
                                        momentOne[i] * (3 * momentTwo[i] + meanSquare[i]);
                            }
                            //			    moments.setElementAt(momentThree, 2);
                            temp.setFlatArray(momentThree);
                            if (kc == 0) {
                                ((GraphType) outputs.get(2)).setDataArrayReal(temp.restoreArray(true), dv);
                            } else {
                                ((GraphType) outputs.get(2)).setDataArrayImag(temp.restoreArray(true), dv);
                            }
                        }

                        if (highestPower > 3) {
                            momentFour = new double[points];
                            System.arraycopy((double[]) powers.get(3), 0, momentFour, 0, points);
                            // m4 = p4/N - 4*m1*m3 - 6*m1^2*m2 - m1^4
                            //    = p4/N - 4*m1*m3 - m1^2*(6*m2 + m1^2)
                            for (i = 0; i < points; ++i) {
                                momentFour[i] = momentFour[i] / numberSetsActuallyPresent[3] -
                                        4 * momentOne[i] * momentThree[i] -
                                        meanSquare[i] * (6 * momentTwo[i] + meanSquare[i]);
                            }
                            //			    moments.setElementAt(momentFour, 3);
                            temp.setFlatArray(momentFour);
                            if (kc == 0) {
                                ((GraphType) outputs.get(3)).setDataArrayReal(temp.restoreArray(true), dv);
                            } else {
                                ((GraphType) outputs.get(3)).setDataArrayImag(temp.restoreArray(true), dv);
                            }
                        }

                        if (highestPower > 4) {
                            momentFive = new double[points];
                            System.arraycopy((double[]) powers.get(4), 0, momentFive, 0, points);
                            // m5 = p5/N - 5*m1*m4 - 10*m1^2*m3 - 10*m1^3*m2 - m1^5
                            //    = p5/N - 10*m1^2*m3 - m1*(5*m4 + m1^2*(10*m2 + m1^2))
                            for (i = 0; i < points; ++i) {
                                momentFive[i] = momentFive[i] / numberSetsActuallyPresent[4] -
                                        10 * meanSquare[i] * momentThree[i] - momentOne[i] *
                                        (5 * momentFour[i] + meanSquare[i] * (10 * momentTwo[i] + meanSquare[i]));
                            }
                            //			    moments.setElementAt(momentFive, 4);
                            temp.setFlatArray(momentFive);
                            if (kc == 0) {
                                ((GraphType) outputs.get(4)).setDataArrayReal(temp.restoreArray(true), dv);
                            } else {
                                ((GraphType) outputs.get(4)).setDataArrayImag(temp.restoreArray(true), dv);
                            }
                        }

                        if (highestPower > 5) {
                            momentSix = new double[points];
                            System.arraycopy((double[]) powers.get(5), 0, momentSix, 0, points);
                            // m6 = p6/N - 6*m1*m5 - 15*m1^2*m4 - 20*m1^3*m3 -
                            //      15*m1^4*m2 - m1^6
                            //    = p6/N - m1*(6*m5 + 20*m3*m1^2) - m1^2*(15*m4 +
                            //        m1^2*(15*m2 + m1^2))
                            for (i = 0; i < points; ++i) {
                                momentSix[i] = momentSix[i] / numberSetsActuallyPresent[5] -
                                        momentOne[i] * (6 * momentFive[i] + 20 * momentThree[i] * meanSquare[i]) -
                                        meanSquare[i] *
                                                (15 * momentFour[i] + meanSquare[i] * (15 * momentTwo[i]
                                                        + meanSquare[i]));
                            }
                            //			    moments.setElementAt(momentSix, 5);
                            temp.setFlatArray(momentSix);
                            if (kc == 0) {
                                ((GraphType) outputs.get(5)).setDataArrayReal(temp.restoreArray(true), dv);
                            } else {
                                ((GraphType) outputs.get(5)).setDataArrayImag(temp.restoreArray(true), dv);
                            }
                        }

                        if (highestPower > 6) {
                            momentSeven = new double[points];
                            System.arraycopy((double[]) powers.get(6), 0, momentSeven, 0, points);
                            // m7 = p7/N - 7*m1*m6 - 21*m1^2*m5 - 35*m1^3*m4 -
                            //      35*m1^4*m3 - 21*m1^5*m2 - m1^7
                            //    = p7/N - m1*(7*m6 + m1^2*(35*m4 + m1^2*(21*m2 +
                            //      m1^2))) - m1^2*(21*m5 + 35*m1^2*m3)
                            for (i = 0; i < points; ++i) {
                                momentSeven[i] = momentSeven[i] / numberSetsActuallyPresent[6] -
                                        momentOne[i] * (7 * momentSix[i] + meanSquare[i] *
                                                (35 * momentFour[i] + meanSquare[i] * (21 * momentTwo[i]
                                                        + meanSquare[i]))) -
                                        meanSquare[i] * (21 * momentFive[i] + 35 * meanSquare[i] * momentThree[i]);
                            }
                            //			    moments.setElementAt(momentSeven, 6);
                            temp.setFlatArray(momentSeven);
                            if (kc == 0) {
                                ((GraphType) outputs.get(6)).setDataArrayReal(temp.restoreArray(true), dv);
                            } else {
                                ((GraphType) outputs.get(6)).setDataArrayImag(temp.restoreArray(true), dv);
                            }
                        }

                        if (highestPower > 7) {
                            momentEight = new double[points];
                            System.arraycopy((double[]) powers.get(7), 0, momentEight, 0, points);
                            // m8 = p8/N - 8*m1*m7 - 28*m1^2*m6 - 56*m1^3*m5 - 70*m1^4*m4 -
                            //      56*m1^5*m3 - 28*m1^6*m2 - m1^8
                            //    = p8/N - m1*(8*m7 + 56*m1^2*(m5 + m1^2*m3)) -
                            //      m1^2*(28*m6 + m1^2*(70*m4 + m1^2*(28*m2 + m1^2)))
                            for (i = 0; i < points; ++i) {
                                momentEight[i] = momentEight[i] / numberSetsActuallyPresent[7] -
                                        momentOne[i] * (8 * momentSeven[i] + 56 * meanSquare[i] *
                                                (momentFive[i] + meanSquare[i] * momentThree[i])) -
                                        meanSquare[i] * (28 * momentSix[i] + meanSquare[i] *
                                                (70 * momentFour[i] + meanSquare[i] * (28 * momentTwo[i]
                                                        + meanSquare[i])));
                            }
                            //			    moments.setElementAt(momentEight, 7);
                            temp.setFlatArray(momentEight);
                            if (kc == 0) {
                                ((GraphType) outputs.get(7)).setDataArrayReal(temp.restoreArray(true), dv);
                            } else {
                                ((GraphType) outputs.get(7)).setDataArrayImag(temp.restoreArray(true), dv);
                            }
                        }
                    }
                }
            }
        } else if (input instanceof Const) {

            int dv, rc, kc, removeSet;
            double r, i, data, scratchOne, scratchTwo, removeData;
            double momentOne, momentTwo, momentThree, momentFour, momentFive, momentSix, momentSeven, momentEight,
                    meanSquare;
            double[] powers, moments, powers1;
            double[] dataTable;

            complexInput = ((Const) input).isComplex();
            rc = (complexInput) ? 1 : 0;
            r = ((Const) input).getReal();
            i = 0;
            if (complexInput) {
                i = ((Const) input).getImag();
            }
            for (kc = 0; kc <= rc; kc++) {
                tableIndex = kc;
                data = (kc == 0) ? r : i;

                if (firstCallToProcess) {
                    firstCallToProcess = false;

                    // Set up sizes of arrays stored in ArrayList powers on the first
                    // call to this unit, and insert powers into its Hashtable.

                    powers = new double[highestPower];
                    for (j = 0; j < highestPower; j++) {
                        powers[j] = 0;
                        oldestSet[j] = 0;
                    }
                    tableOfPowers.put(new Integer(tableIndex), powers);

                    dataTable = new double[numberOfSets];
                    tableOfDataTables.put(new Integer(tableIndex), dataTable);
                } else {
                    // If the highest power has changed since
                    // the last data set, readjust the size of the ArrayList powers.
                    // If the highest moment goes up, the extra moments will not
                    // be calculated correctly until the number of data sets equals
                    // the total numberOfSets.

                    powers = (double[]) tableOfPowers.get(new Integer(tableIndex));
                    dataTable = (double[]) tableOfDataTables.get(new Integer(tableIndex));

                    if (newLimit) {
                        powers1 = new double[highestPower];
                        if (highestPower > oldHighestPower) {
                            System.arraycopy(powers, 0, powers1, 0, oldHighestPower);
                            for (j = oldHighestPower; j < highestPower; j++) {
                                powers1[j] = 0.0;
                            }
                            oldestSet[j] = currentSet;
                        } else {
                            System.arraycopy(powers, 0, powers1, 0, highestPower);
                        }
                        powers = powers1;
                    }

                    // Put new data into dataTable

                    dataTable[currentSet] = data;

                    // Add powers of new data set into the ArrayList powers for as many
                    // moments as are required.  Use scratchOne as temporary storage
                    // for the latest power, so that the next can be calculated by
                    // multiplication rather than by invoking Math.pow().

                    powers[0] += data;
                    scratchOne = data;
                    if (highestPower > 1) {
                        for (j = 1; j < highestPower; j++) {
                            scratchOne *= data;
                            powers[j] += scratchOne;
                        }
                    }

                    // Remove any data sets that need to be removed from the average.
                    // This is the normal condition in a steady state, where one data set is
                    // removed, and it also happens if the number of sets to average
                    // goes down, so that more have to be removed.

                    if (removeOldest) {
                        for (int k = 1; k <= numberToRemove; k++) {

                            removeSet = oldestSet[0];
                            removeData = dataTable[removeSet];
                            dataTable[removeSet] = 0.0;
                            oldestSet[0]++;

                            powers[0] = -removeData;

                            if (highestPower > 1) {
                                for (j = 1; j < highestPower; j++) {
                                    if (oldestSet[j] < oldestSet[0]) {
                                        oldestSet[j]++;
                                        scratchOne *= removeData;
                                        powers[j] -= scratchOne;
                                    }
                                }
                            }
                        }
                    }


                    for (j = 0; j < highestPower; j++) {
                        numberSetsActuallyPresent[j] = currentSet - oldestSet[j] + 1;
                    }


/* Compute moments (up to 8) according to the following formulas, where
   mn is the nth moment about the mean = [sum_i(x_i - <x>)^n]/N, (except
   for m1, which is the mean <x> itself), where N = points, and where
   pn stands for the sum of the n'th powers = powers.ElementAt(n-1)
    m1 = p1/N (mean)
    m2 = p2/N - (p1/N)^2
       = p2/N - m1^2 (variance)
    m3 = p3/N - 3*p2*p1/N^2 + 2*(p1/N)^3
       = p3/N - 3*m1*m2 - m1^3 ( = skewness * variance^(3/2) )
    m4 = p4/N - 4*p3*p1/N^2 + 6*p2*p1^2/N^3 - 3*(p1/N)^4
       = p4/N - 4*m1*m3 - 6*m1^2*m2 - m1^4 ( = kurtosis * variance^2 )
    m5 = p5/N - 5*p4*p1/N^2 + 10*p3*p1^2/N^3 - 10*p2*p1^3/N^4 +
        4*(p1/N)^5
       = p5/N - 5*m1*m4 - 10*m1^2*m3 - 10*m1^3*m2 - m1^5
    m6 = p6/N - 6*p5*p1/N^2 + 15*p4*p1^2/N^3 - 20*p3*p1^3/N^4 +
        15*p2*p1^4/N^5 - 5*(p1/N)^6
       = p6/N - 6*m1*m5 - 15*m1^2*m4 - 20*m1^3*m3 - 15*m1^4*m2 - m1^6
    m7 = p7/N - 7*p6*p1/N^2 + 21*p5*p1^2/N^3 - 35*p4*p1^3/N^4 +
        35*p3*p1^4/N^5 - 21*p2*p1^5/N^6 + 6*(p1/N)^7
       = p7/N - 7*m1*m6 - 21*m1^2*m5 - 35*m1^3*m4 - 35*m1^4*m3 -
        21*m1^5*m2 - m1^7
    m8 = p8/N - 8*p7*p1/N^2 + 28*p6*p1^2/N^3 - 56*p5*p1^3/N^4 +
        70*p4*p1^4/N^5 - 56*p3*p1^5/N^6 + 28*p2*p1^6/N^7 - 7*(p1/N)^8
       = p8/N - 8*m1*m7 - 28*m1^2*m6 - 56*m1^3*m5 - 70*m1^4*m4 -
        56*m1^5*m3 - 28*m1^6*m2 - m1^8

    We stop at 8 only because it seems unlikely that moments above 8
    will be used very often.  We use the second versions of the
    expressions (involving lower moments rather than powers) to avoid
    dividing by N (numberOfSets) all the time. When we divide by N
    we use the number of sets actually accumulated for the moment
    in question, since they can be different if the maximum number
    of moments has changed. This gives a reasonable approximation
    to the right result (but not an exact one) until all moments
    are being computed using the same number of sets.
*/

                    momentOne = 0;
                    momentTwo = 0;
                    momentThree = 0;
                    momentFour = 0;
                    momentFive = 0;
                    momentSix = 0;
                    momentSeven = 0;
                    momentEight = 0;
                    meanSquare = 0;

                    moments = new double[highestPower];
                    momentOne = powers[0] / numberSetsActuallyPresent[0];
                    //			moments.setElementAt( momentOne, 0 );
                    if (kc == 0) {
                        ((Const) outputs.get(0)).setReal(momentOne);
                    } else {
                        ((Const) outputs.get(0)).setImag(momentOne);
                    }

                    if (highestPower > 1) {
                        meanSquare = momentOne;
                        momentTwo = powers[1];
                        // m2 = p2/N - m1^2
                        meanSquare *= meanSquare; // contains squares of the mean (m1^2)
                        momentTwo = momentTwo / numberSetsActuallyPresent[1] - meanSquare;
                        //			    moments.setElementAt( momentTwo, 1 );
                        if (kc == 0) {
                            ((Const) outputs.get(1)).setReal(momentTwo);
                        } else {
                            ((Const) outputs.get(1)).setImag(momentTwo);
                        }
                    }

                    if (highestPower > 2) {
                        momentThree = powers[2];
                        // m3 = p3/N - 3*m1*m2 - m1^3  = p3/N - m1*(3*m2 + m1^2)
                        momentThree = momentThree / numberSetsActuallyPresent[2] -
                                momentOne * (3 * momentTwo + meanSquare);
                        //			    moments.setElementAt(momentThree, 2);
                        if (kc == 0) {
                            ((Const) outputs.get(2)).setReal(momentThree);
                        } else {
                            ((Const) outputs.get(2)).setImag(momentThree);
                        }
                    }

                    if (highestPower > 3) {
                        momentFour = powers[3];
                        // m4 = p4/N - 4*m1*m3 - 6*m1^2*m2 - m1^4
                        //    = p4/N - 4*m1*m3 - m1^2*(6*m2 + m1^2)
                        momentFour = momentFour / numberSetsActuallyPresent[3] -
                                4 * momentOne * momentThree -
                                meanSquare * (6 * momentTwo + meanSquare);
                        //			    moments.setElementAt(momentFour, 3);
                        if (kc == 0) {
                            ((Const) outputs.get(3)).setReal(momentFour);
                        } else {
                            ((Const) outputs.get(3)).setImag(momentFour);
                        }
                    }

                    if (highestPower > 4) {
                        momentFive = powers[4];
                        // m5 = p5/N - 5*m1*m4 - 10*m1^2*m3 - 10*m1^3*m2 - m1^5
                        //    = p5/N - 10*m1^2*m3 - m1*(5*m4 + m1^2*(10*m2 + m1^2))
                        momentFive = momentFive / numberSetsActuallyPresent[4] -
                                10 * meanSquare * momentThree - momentOne *
                                (5 * momentFour + meanSquare * (10 * momentTwo + meanSquare));
                        //			    moments.setElementAt(momentFive, 4);
                        if (kc == 0) {
                            ((Const) outputs.get(4)).setReal(momentFive);
                        } else {
                            ((Const) outputs.get(4)).setImag(momentFive);
                        }
                    }

                    if (highestPower > 5) {
                        momentSix = powers[5];
                        // m6 = p6/N - 6*m1*m5 - 15*m1^2*m4 - 20*m1^3*m3 -
                        //      15*m1^4*m2 - m1^6
                        //    = p6/N - m1*(6*m5 + 20*m3*m1^2) - m1^2*(15*m4 +
                        //        m1^2*(15*m2 + m1^2))
                        momentSix = momentSix / numberSetsActuallyPresent[5] -
                                momentOne * (6 * momentFive + 20 * momentThree * meanSquare) -
                                meanSquare *
                                        (15 * momentFour + meanSquare * (15 * momentTwo + meanSquare));
                        //			    moments.setElementAt(momentSix, 5);
                        if (kc == 0) {
                            ((Const) outputs.get(5)).setReal(momentSix);
                        } else {
                            ((Const) outputs.get(5)).setImag(momentSix);
                        }
                    }

                    if (highestPower > 6) {
                        momentSeven = powers[6];
                        // m7 = p7/N - 7*m1*m6 - 21*m1^2*m5 - 35*m1^3*m4 -
                        //      35*m1^4*m3 - 21*m1^5*m2 - m1^7
                        //    = p7/N - m1*(7*m6 + m1^2*(35*m4 + m1^2*(21*m2 +
                        //      m1^2))) - m1^2*(21*m5 + 35*m1^2*m3)
                        momentSeven = momentSeven / numberSetsActuallyPresent[6] -
                                momentOne * (7 * momentSix + meanSquare *
                                        (35 * momentFour + meanSquare * (21 * momentTwo + meanSquare))) -
                                meanSquare * (21 * momentFive + 35 * meanSquare * momentThree);
                        //			    moments.setElementAt(momentSeven, 6);
                        if (kc == 0) {
                            ((Const) outputs.get(6)).setReal(momentSeven);
                        } else {
                            ((Const) outputs.get(6)).setImag(momentSeven);
                        }
                    }

                    if (highestPower > 7) {
                        momentEight = powers[7];
                        // m8 = p8/N - 8*m1*m7 - 28*m1^2*m6 - 56*m1^3*m5 - 70*m1^4*m4 -
                        //      56*m1^5*m3 - 28*m1^6*m2 - m1^8
                        //    = p8/N - m1*(8*m7 + 56*m1^2*(m5 + m1^2*m3)) -
                        //      m1^2*(28*m6 + m1^2*(70*m4 + m1^2*(28*m2 + m1^2)))
                        momentEight = momentEight / numberSetsActuallyPresent[7] -
                                momentOne * (8 * momentSeven + 56 * meanSquare *
                                        (momentFive + meanSquare * momentThree)) -
                                meanSquare * (28 * momentSix + meanSquare *
                                        (70 * momentFour + meanSquare * (28 * momentTwo + meanSquare)));
                        //			    moments.setElementAt(momentEight, 7);
                        if (kc == 0) {
                            ((Const) outputs.get(7)).setReal(momentEight);
                        } else {
                            ((Const) outputs.get(7)).setImag(momentEight);
                        }
                    }
                }
            }
        }


        ++currentSet;
        oldHighestPower = highestPower;

        // Now do output: if there are more output nodes than moments to
        // output, cycle over the moments to fill up the nodes.

        int currentOutputSet = 0;
        for (int count = 0; count < numberOfNodes; count++) {

            output = (TrianaType) outputs.get(currentOutputSet++);
            outputAtNode(count, output);
            if (currentOutputSet >= highestPower) {
                currentOutputSet = 0;
            }

        }

    }

}















