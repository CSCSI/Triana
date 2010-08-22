package audio.processing.mir;


import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.trianacode.taskgraph.Unit;
import triana.types.Spectrum;

/**
 * Detects pitch and stuff.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2915 $
 */
public class PitchDetection extends Unit {

    // parameter data type definitions
    private int noFreqPoints;
    private int noOfHarmonics;
    int sampleFreq = 44100;     // hardcoded... don't bother using wav/aiff data which isn't 16bit/44.1khz
    double freqSpacing;         // this is the frequency resolution... multiply it by index to give frequency
    double[] topFFreqValues = null;    // array containing actual y-axis values of spectrum array... needed for sorting
    double[] power = null;             // this is the main array containing the actual spectrum
    double[] powerCopy = null;         // this is a copy of the main array.. gets reversed and sorted
    double[] freqArray = null;         // this array essentially stores the top 30 fundamental frequencies
    int[] indexArray = null;           // this array contains index locations (for power[]) of top values...
    double[] freqResults = new double[22050]; // essentially stores summed integer multiples of fundamental + harmonics
    double max = 0;
    double freqdiv;
    int result;
    double temp;
    int temp2;
    int freqbin;
    int spectrumPoint;
    double freq;
    double key;
    double arrayPointDouble;
    double arrayPointInt;
    int arrayPoint;
    double fraction;

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {

        Spectrum input = (Spectrum) getInputAtNode(0);

        topFFreqValues = new double[noFreqPoints];

        freqSpacing = input.getFrequencyResolution();

        power = new double[input.size()];
        power = input.getData();

        powerCopy = new double[input.size()];

        System.arraycopy(power, 0, powerCopy, 0, power.length); // copied incase original input array needs to be output
        Arrays.sort(powerCopy);   // sort ascending so we can find the largest etc
        ArrayUtils.reverse(powerCopy); // reverse so array is descending in size

        for (int l = 0; l < noFreqPoints; l++) { // default is 30
            topFFreqValues[l] = powerCopy[l]; // copy highest n values into topNValues array
            //System.out.println("top value " + (l+1) + " = " + topFFreqValues[l]);
        }

        indexArray = new int[topFFreqValues.length];

        /* For each value in topFFreqValues, go through power spectrum and look to find out the index/position of where
         * its location is. Create a new array called 'indexArray' containing these locations...         
         */
        for (int j = 0; j < topFFreqValues.length; j++) {
            key = topFFreqValues[j];
            //System.out.println("Key = " + key);
            for (int i = 0; i < power.length; i++) {
                if ((power[i] != 0) && power[i] == key) {
                    indexArray[j] = i;
                }
            }
        }

        // calls investigateSpectrumPoints() method to do work on indexArray[] and turn indexes into frequencies
        investigateSpectrumPoints();

        result = 0;

        //  goes through results of top summed frequency factors and finds the actual highest summed value, called 'max'
        for (int i = 0; i < freqResults.length; ++i) {
            if (freqResults[i] > 0) {
                if (freqResults[i] > max) {
                    max = freqResults[i];
                    result = i;
                }
            }
        }
        //System.out.println("Max summed peak = " + max);
        System.out.println("Pitch Detection Result = " + result + "Hz");

        output((Object) result); // Outputs the original... just for now... whatever
        resetVariables();

    }

    public void resetVariables() {
        max = 0;
        result = 0;
        freqSpacing = 0;
        freqdiv = 0;
        temp = 0;
        temp2 = 0;
        java.util.Arrays.fill(topFFreqValues, 0);
        java.util.Arrays.fill(indexArray, 0);
        java.util.Arrays.fill(power, 0);
        java.util.Arrays.fill(powerCopy, 0);
        java.util.Arrays.fill(freqArray, 0);
        java.util.Arrays.fill(freqResults, 0);
        freqbin = 0;
        spectrumPoint = 0;
        freq = 0;
        key = 0;
        arrayPointDouble = 0;
        arrayPointInt = 0;
        arrayPoint = 0;
        fraction = 0;

    }

    /*
    * Method to go through top n values, multiply them by the frequncy resolution,
    * and invoke detectPitch() on the top 30 fundamental frequencies and around these also +- freqSpacing
    */

    public void investigateSpectrumPoints() {

        freqArray = new double[topFFreqValues.length];
        freqdiv = freqSpacing / Math.floor(freqSpacing); // to give double increment amount.

        for (int i = 0; i < noFreqPoints;
             i++) {                 // for however many frequency points are given - default 30
            freqArray[i] = indexArray[i] * freqSpacing;      // Fiving the *fundamental frequency*
            //System.out.println("Top 30 fundamental frequencies = " + freqArray[i]);
        }

        //System.out.println("(freqSpacing/2) + 1 = " + ((freqSpacing/2)+1));

        temp = Math.rint(((freqSpacing / 2) + 1));
        temp2 = (int) temp;

        for (int i = 0; i < freqArray.length;
             i++) { // call detectPitch on each fundamental frequency and from ff +/- (freqSpacing/2)
            detectPitch(freqArray[i]);
            //System.out.println("testing the first forloop");

            for (int j = 1; j < temp2; j++) {
                // System.out.println("j = " + j);
                if (((freqArray[i] - (freqdiv * (double) j)) > 0)) {
                    detectPitch(freqArray[i] - (freqdiv * (double) j));
                }

                if (((freqArray[i] + (freqdiv * (double) j)) < 22050)) {
                    detectPitch(freqArray[i] + (freqdiv * (double) j));
                }
            }
        }
    }

    /*
    * Method to map back from a frequency value, back to a value in the data array 'power'
    */

    int freqToArray(double freq) {
        arrayPointDouble = freq / freqSpacing;
        arrayPointInt = Math.floor(arrayPointDouble);

        // System.out.println("arrayPointInt = " + arrayPointInt);

        arrayPoint = (int) arrayPointInt;
        fraction = arrayPointDouble - arrayPointInt;

        if (fraction > 0.5) {
            arrayPoint = arrayPoint + 1;
        }
        //System.out.println("arrayPoint = " + arrayPoint);

        return arrayPoint;
    }

    /*
    * Method to take a single frequency, look at all its integer factors, and add them together, and search for the
    * lowest frequency with the highest value. Only looks at first 'noOfHarmonics' (default 6)
    */

    // FREQ AND FREQBIN TURN OUT TO BE 22050 WHEN KEY = 0...

    void detectPitch(double fundamental) {

        freq = fundamental;
        //System.out.println("freq = " + freq);
        freqbin = (int) Math.floor(freq); // index of the frequency (fundamental pitch) point
        //System.out.println("freqbin = " + freqbin);


        //System.out.println("power.length = " + power.length);

        for (int i = 0; i < noOfHarmonics; i++) { // for each harmonic
            // System.out.println("test detectpitch = " + i);
            spectrumPoint = freqToArray(freq + (freq * (double) i)); // frequency + integer multiples of itself
            //System.out.println("spectrumPoint = " + spectrumPoint);
            if (spectrumPoint < power.length) {
                freqResults[freqbin] = freqResults[freqbin]
                        + power[spectrumPoint]; // power is the main, original spectrum array
                //System.out.println("freqResults[freqbin] = " + freqResults[freqbin]);
            }
        }
    }

    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("PitchDetection.html");

        // Define initial value and type of parameters
        defineParameter("noFreqPoints", "30", USER_ACCESSIBLE);
        defineParameter("noOfHarmonics", "20", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Number Of Frequency Points $title noFreqPoints TextField 30\n";
        guilines += "No Of Harmonics $title noOfHarmonics TextField 20\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        noFreqPoints = new Integer((String) getParameter("noFreqPoints")).intValue();
        noOfHarmonics = new Integer((String) getParameter("noOfHarmonics")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up PitchDetection (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("noFreqPoints")) {
            noFreqPoints = new Integer((String) value).intValue();
        }

        if (paramname.equals("noOfHarmonics")) {
            noOfHarmonics = new Integer((String) value).intValue();
        }
    }


    /**
     * @return an array of the types accepted by each input node. For node indexes not covered the types specified by
     *         getInputTypes() are assumed.
     */
    public String[][] getNodeInputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types accepted by nodes not covered by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }


    /**
     * @return an array of the types output by each output node. For node indexes not covered the types specified by
     *         getOutputTypes() are assumed.
     */
    public String[][] getNodeOutputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types output by nodes not covered by getNodeOutputTypes().
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}



