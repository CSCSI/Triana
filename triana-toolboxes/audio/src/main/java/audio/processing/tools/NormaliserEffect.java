package audio.processing.tools;

import triana.types.SampleSet;

/**
 * A Normalisation unit which allows the user to control the gain of the audio file's selection to the maximum level
 * without clipping (digital distortion). The normalisation factor should be entered as a percentage from the maximum
 * amplitude that the audio file should be normalised to.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 */

public class NormaliserEffect {

    float normalisationLevel;

    // Create buffer arrays
    short[] output;


    /* Class constructor creates Normaliser with the normalisation level set
     *
     * @param normal the level of normalisation to which the waveform array should be
     * 				set. This value must be divided by 100 so that the value is given
     *				as a percentage.
     */

    public NormaliserEffect(float normal) {
        setNormalLevel(normal);
    }

    /**
     * Sets the level of normalisation. To be specific, this sets how loud the waveform will be in releaiton the maximum
     * value for a short integer (32767).
     *
     * @param nor the level of normalisation. This value must be divided by 100 in order to be used, as the normalisation
     *            level must be less than 1 as it is given as a percentage.
     */

    public void setNormalLevel(float norm) {
        normalisationLevel = norm;
    }

    /*
      * Adjusts the volume of the input sample. Works in 32bit
      * (ints) to avoid any clipping cause from using shorts.
      *
      * @param input short array containing the input data to be manipulated by algorithm
      * @returns a short array which has been manipulated.
      */

    public short[] process(short input[]) {

        short[] output = new short[input.length];

        double range = (double) (32767 * (normalisationLevel / 100));
        int maxValue = 0;
        int normalisedSignal = 0;

        for (int n = 0; n < input.length; n++) {
            int outputSample = (int) (input[n]);

            if (Math.abs(outputSample) > Math.abs(maxValue)) {
                maxValue = outputSample;
            }
        }

        // For each sample in the array
        for (int n = 0; n < input.length; n++) {
            int outputSample = (int) (input[n]);

            double scaler = (double) (range / maxValue);
            normalisedSignal = (int) (outputSample * scaler);

            output[n] = (short) normalisedSignal;

        }// End of for loop

        return output;


    }  // End faderProcess method


    /*
      * Overloaded method adjusts the volume of the input sample. Accepts integer
      * arrays but still returns short arrays.
      *
      * @param input integer array containing the input data to be manipulated by algorithm
      * @returns a short array which has been manipulated.
      */

    public short[] process(int input[]) {

        short[] output = new short[input.length];

        double range = (double) (32767 * (normalisationLevel / 100));
        int maxValue = 0;
        int normalisedSignal = 0;

        for (int n = 0; n < input.length; n++) {
            int outputSample = (int) (input[n]);

            if (Math.abs(outputSample) > Math.abs(maxValue)) {
                maxValue = outputSample;
            }
        }

        // For each sample in the array
        for (int n = 0; n < input.length; n++) {
            int outputSample = (int) (input[n]);

            double scaler = (double) (range / maxValue);
            normalisedSignal = (int) (outputSample * scaler);

            output[n] = (short) normalisedSignal;

        }// End of for loop

        return output;


    }  // End faderProcess method

    /*
      * Overloaded method adjusts the volume of the input sample. Accepts SAMPLESET
      * objects but still returns short arrays.
      *
      * @param input integer array containing the input data to be manipulated by algorithm
      * @returns a short array which has been manipulated.
      */

    public short[] process(SampleSet input) {

        double[] input2 = input.data;
        short[] output = new short[input2.length];

        double range = (double) (32767 * (normalisationLevel / 100));
        double maxValue = 0.0;
        int normalisedSignal = 0;

        for (int n = 0; n < input2.length; n++) {
            double outputSample = (double) (input2[n]);
            if (Math.abs(outputSample) > Math.abs(maxValue)) {
                maxValue = outputSample;
            }
        }

        // For each sample in the array
        for (int n = 0; n < input2.length; n++) {
            double outputSample = (double) (input2[n]);
            double scaler = (double) (range / maxValue);
            normalisedSignal = (int) (Math.rint(outputSample * scaler));
            output[n] = (short) normalisedSignal;

        }// End of for loop

        return output;


    }  // End faderProcess method

    /*
      * Overloaded method adjusts the volume of the input sample. Accepts SAMPLESET
      * objects but still returns short arrays.
      *
      * @param input integer array containing the input data to be manipulated by algorithm
      * @returns a short array which has been manipulated.
      */

    public short[] process8Bit(SampleSet input) {

        double[] input2 = input.data;
        short[] output = new short[input2.length];

        double range = (double) (256 * (normalisationLevel / 100));
        double maxValue = 0.0;
        int normalisedSignal = 0;

        for (int n = 0; n < input2.length; n++) {
            double outputSample = (double) (input2[n]);
            if (Math.abs(outputSample) > Math.abs(maxValue)) {
                maxValue = outputSample;
            }
        }

        // For each sample in the array
        for (int n = 0; n < input2.length; n++) {
            double outputSample = (double) (input2[n]);
            double scaler = (double) (range / maxValue);
            normalisedSignal = (int) (Math.rint(outputSample * scaler));
            output[n] = (short) normalisedSignal;

        }// End of for loop

        return output;


    }  // End faderProcess method

}
