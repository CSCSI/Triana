package audio.processing.eq;

import triana.types.audio.AudioEffect16Bit;

/**
 * A Comb-delay Effect which allows user to add a delayed signal onto the original by creating a feedforward and
 * feedback Comb Filter based on a building block of a reverb device. The user can set the attenuation the level of the
 * delayed signal and also adjust the delay time by adjusting the settings using the GUI.CombDelayEffect exends
 * AudioEffect 16bit to allow for chunked data.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see triana.audio.processing.delay.HighPass
 * @see triana.audio.AudioEffect16Bit
 */

public class BandPassEffect extends AudioEffect16Bit {

    double frequencyPeak;
    int delaySamples;
    double delayedInput;
    double delayedInput2;
    double delayedOutput;
    double delayedOutput2;
    boolean chunkedData;
    float samplingFrequency;
    float q;
    float dbGainLevel;

    // Create buffer arrays
    short[] output;
    short[] outputData;

    /**
     * Creates an comb filter delay effect with a particular forward memory size.
     *
     * @param forwardMemSize the size of the forward memory to be used by AudioEffect16bit class.
     * @param feedback       the level of attenutation of the delayed signal in the the feedback comb filter. Should be
     *                       divided by 100 to give value in percent.
     * @param delayOffset    the size of the delay in samples. Used to calculate appropriate buffer size
     * @param filterType     sets which type of filter to use. Can be Standard Feedback Filter or Feedback Loop Only
     * @param chunked        boolean value to indicate if the user is using chunked data. Used by process method to
     *                       choose suitable method.
     */

    public BandPassEffect(int forwardMemSize, float frequency, boolean chunked, float delayInMs, float sampleRate,
                          float Q, float dbGain) {
        super(forwardMemSize);
        samplingFrequency = sampleRate;
        setFrequency(frequency);
        setDelayOffset(delayInMs);
        setChunked(chunked);
        setQ(Q);
        setDBGain(dbGain);
    }

    /**
     * Sets the feedback factor. To be specific this sets how loud the delayed signal will be.
     *
     * @param feed the level of feedback. This value must be divided by 100 in order to be used, as the feedback factor
     *             must be less than 1.
     */

    public void setFrequency(float freq) {
        frequencyPeak = freq;
        System.out.println("freq = " + freq);
    }

    /**
     * Sets the delay size in samples that the delayed signal will be delayed by
     *
     * @param offset this is the size of the delay in samples. This is used to set the forward buffer when using chunks,
     *               and also to calculate the size of the final output array.
     */

    public void setDelayOffset(float offset) {
        //delaySamples = ((int)(offset * (float)samplingFrequency / (float)1000));
        delaySamples = 1;
        System.out.println("delaySamples = " + delaySamples);
    }

    /**
     * Sets the delay size in samples that the delayed signal will be delayed by
     *
     * @param offset this is the size of the delay in samples. This is used to set the forward buffer when using chunks,
     *               and also to calculate the size of the final output array.
     */

    public void setQ(float qValue) {
        //delaySamples = ((int)(offset * (float)samplingFrequency / (float)1000));
        q = qValue;
    }

    /**
     * Sets the delay size in samples that the delayed signal will be delayed by
     *
     * @param qValue this is the size of the delay in samples. This is used to set the forward buffer when using chunks,
     *               and also to calculate the size of the final output array.
     */

    public void setDBGain(float gain) {
        //delaySamples = ((int)(offset * (float)samplingFrequency / (float)1000));
        dbGainLevel = gain;
    }

    /**
     * Determines whether chunked data is being used or not.
     *
     * @param chunk returns true if the user has indicated that chunked data is being used.
     */

    public void setChunked(boolean chunk) {
        chunkedData = chunk;
    }

    /**
     * Process method calls appropriate combProcess or combProcessChunked method depending on the users decision to use or
     * not chunked data.
     *
     * @param input short array containing the input data to be passed on to respective method
     */

    public short[] process(short[] input) {

        if (chunkedData == false) {
            outputData = bandPassProcess(input);
            System.out.println("chunked off");
        } else {
            outputData = bandPassProcessChunked(input);
            System.out.println("chunked on");
        }
        return outputData;
    }

    /**
     * Method for non chunked data. Creates a delayed signal after the original data, and also acts as a feedback delay
     * creating further delayed signals Works in 32bit (ints) to avoid any (unintentional) clipping from using shorts.
     *
     * @param input short array containing the input data to be manipulated by algorithm
     */

    public short[] bandPassProcess(short input[]) {

        short[] output = new short[input.length + delaySamples];
        double outputSample = 0;
        double Fs = samplingFrequency;
        double f0 = (double) frequencyPeak;
        double Q = q;
        System.out.println("Q = " + Q);
        double w0 = 2 * (Math.PI * f0) / Fs;
        System.out.println("w0 = " + w0);
        double alpha = Math.sin(w0) / (2 * Q);
        System.out.println("alpha = " + alpha);

        // Computer the coefficients for the filter
        double b0 = alpha;
        double b1 = 0;
        double b2 = -alpha;
        double a0 = 1 + alpha;
        double a1 = -2 * (Math.cos(w0));
        double a2 = 1 - alpha;

        // For each sample in the output array
        for (int n = 0; n < output.length; n++) {

            // delayedPosition is only set once n is large enough
            int delayedPosition = n - delaySamples;
            int delayedPosition2 = n - 2;

            if (delayedPosition < 0) {
                delayedInput = 0;
                delayedOutput = 0;
            } else if (delayedPosition2 > 2) {
                delayedInput = input[delayedPosition]; // This is x[n-1]
                delayedOutput = output[delayedPosition]; // This is y[n-1]
                delayedInput2 = input[delayedPosition2]; // This is x[n-2]
                delayedOutput2 = output[delayedPosition2]; // This is y[n-2]
            }

            double inputSample = 0;

            if (n < input.length) {
                inputSample = input[n];
            }

            // This is the algorithm to create a comb filter with a feedback loop. This represents an IIR
            // recursive delay comb filter.
            outputSample = ((b0 / a0) * inputSample) + ((b1 / a0) * delayedInput) + ((b2 / a0) * delayedInput2) -
                    ((a1 / a0) * delayedOutput) - ((a2 / a0) * delayedOutput2);

            // Limits outputSample to max 16bit (short) value
            double limit = outputSample;
            if (limit > 32767) {
                limit = 32767;
            } else if (limit < -32767) {
                limit = -32767;
            }

            // Turns int back into short value in output array after manipulation and limiting
            output[n] = (short) limit;
            //System.out.println("output[n] = " + output[n]);

        }// End of for loop
        return output;
    }

    /**
     * Method for chunked data. Creates a delayed signal after the original data, and also acts as a feedback delay
     * creating further delayed signals Works in 32bit (ints) to avoid any (unintentional) clipping from using shorts.
     *
     * @param input short array containing the input data to be manipulated by algorithm
     */

    public short[] bandPassProcessChunked(short input[]) {

        // inserts current data into array which comprises of current data + forwardBuffer
        // from the previous chunk
        super.preProcess(input);

        short[] forwardBuffer = new short[delaySamples];
        short[] output = new short[input.length + delaySamples];
        int outputSample = 0;

        // For each sample in the output array
        for (int n = 0; n < output.length; n++) {

            // delayedPosition is only set once n is large enough
            int delayedPosition = n - delaySamples;

            if (delayedPosition < 0) {
                delayedInput = 0;
                delayedOutput = 0;
            } else {
                delayedInput = input[delayedPosition];
                delayedOutput = output[delayedPosition];
            }

            short inputSample = 0;

            if (n < input.length) {
                inputSample = input[n];
            }

            // Limits outputSample to max 16bit (short) value
            int limit = outputSample;
            if (limit > 32767) {
                limit = 32767;
            } else if (limit < -32767) {
                limit = -32767;
            }
            // Turns int back into short value in output array after manipulation and limiting
            output[n] = (short) limit;
        }// End of for loop

        // Copies the echoes into the forward buffer, to be added next time by the preProcess method
        System.arraycopy(output, input.length, forwardBuffer, 0, forwardBuffer.length);

        try {
            super.initialiseForward(forwardBuffer);
            // this will set up the forward data so that it gets added to the 'allData' in AudioEffect16bit
            // next time preProcess is called
		}
		catch (Exception ee) {
				ee.printStackTrace();
		}
		return output;
	}
}
