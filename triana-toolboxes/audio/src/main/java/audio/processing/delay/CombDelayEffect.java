package audio.processing.delay;

import triana.types.audio.AudioEffect16Bit;


/**
 * A Comb-delay Effect which allows user to add a delayed signal onto the original by creating a feedforward and
 * feedback Comb Filter based on a building block of a reverb device. The user can set the attenuation the level of the
 * delayed signal and also adjust the delay time by adjusting the settings using the GUI.CombDelayEffect exends
 * AudioEffect 16bit to allow for chunked data.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see CombDelay
 */

public class CombDelayEffect extends AudioEffect16Bit {

    int feedbackFactor;
    int delaySamples;
    int delayedInput;
    int delayedOutput;
    String filter;
    boolean chunkedData;
    float samplingFrequency;

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

    public CombDelayEffect(int forwardMemSize, int feedback, String filterType, boolean chunked, float delayInMs,
                           float sampleRate) {
        super(forwardMemSize);
        samplingFrequency = sampleRate;
        setFeedback(feedback);
        setDelayOffset(delayInMs);
        setFilterType(filterType);
        setChunked(chunked);
    }

    /**
     * Sets the feedback factor. To be specific this sets how loud the delayed signal will be.
     *
     * @param feed the level of feedback. This value must be divided by 100 in order to be used, as the feedback factor
     *             must be less than 1.
     */

    public void setFeedback(int feed) {
        feedbackFactor = feed;
    }

    /**
     * Sets the delay size in samples that the delayed signal will be delayed by
     *
     * @param offset this is the size of the delay in samples. This is used to set the forward buffer when using chunks,
     *               and also to calculate the size of the final output array.
     */

    public void setDelayOffset(float offset) {
        delaySamples = ((int) (offset * (float) samplingFrequency / (float) 1000));
    }

    /**
     * Sets the type of filter to be used to create the delay. Can be Standard Feedback Filter or Feedback Loop Only
     *
     * @param filt the type of filter to create the delay
     */

    public void setFilterType(String filt) {
        filter = filt;
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
            outputData = combProcess(input);
        } else {
            outputData = combProcessChunked(input);
        }
        return outputData;
    }

    /**
     * Method for non chunked data. Creates a delayed signal after the original data, and also acts as a feedback delay
     * creating further delayed signals Works in 32bit (ints) to avoid any (unintentional) clipping from using shorts.
     *
     * @param input short array containing the input data to be manipulated by algorithm
     */

    public short[] combProcess(short input[]) {

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

            if (filter.equals("Standard Feedback Filter")) {

                // This is the algorithm to create a comb filter with a feedback loop. This represents an IIR
                // recursive delay comb filter.
                outputSample = (int) ((inputSample) + (feedbackFactor * delayedOutput / 100));
            } else {

                // This is the algorithm to create ONLY THE feedback loop. This represents an IIR
                // recursive delay comb filter.

                outputSample = (int) ((delayedInput) + (feedbackFactor * delayedOutput / 100));
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

        return output;
    }

    /**
     * Method for chunked data. Creates a delayed signal after the original data, and also acts as a feedback delay
     * creating further delayed signals Works in 32bit (ints) to avoid any (unintentional) clipping from using shorts.
     *
     * @param input short array containing the input data to be manipulated by algorithm
     */

    public short[] combProcessChunked(short input[]) {

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

            if (filter.equals("Standard Feedback Filter")) {

                // This is the algorithm to create a comb filter with a feedback loop. This represents an IIR
                // recursive delay comb filter.
                outputSample = (int) ((inputSample) + (feedbackFactor * delayedOutput / 100));
            } else {

                // This is the algorithm to create ONLY THE feedback loop. This represents an IIR
                // recursive delay comb filter.
                outputSample = (int) ((delayedInput) + (feedbackFactor * delayedOutput / 100));
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
