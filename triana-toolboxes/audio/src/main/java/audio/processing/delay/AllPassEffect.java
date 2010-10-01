package audio.processing.delay;

import triana.types.audio.AudioEffect16Bit;


/**
 * A Allpass-delay Effect which allows user to add a delayed signal onto the original by combing two Comb Filters. An
 * allpass filter is a building block of a Schroeder reverb device. The user can set the attenuation level of the
 * delayed signal and also adjust the delay time by adjusting the settings using the GUI. AllPassEffect extends
 * AudioEffect 16bit to allow for chunked data.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see AllPass
 */

public class AllPassEffect extends AudioEffect16Bit {

    int feedbackFactor;
    int delaySamples;
    int delayedInput;
    int delayedOutput;
    boolean chunkedData;
    float samplingFrequency;

    // Create buffer arrays
    short[] output;
    short[] outputData;

    /**
     * Creates an allpass delay effect with a particular forward memory size.
     *
     * @param forwardMemSize the size of the forward memory to be used by AudioEffect16bit class.
     * @param feedback       the level of attenutation of the delayed signal in the the feedback comb filter. Should be
     *                       divided by 100 to give value in percent.
//     * @param delayOffset    the size of the delay in samples. Used to calculate appropriate buffer size
     * @param chunked        boolean value to indicate if the user is using chunked data. Used by process method to
     *                       choose suitable method.
     */

    public AllPassEffect(int forwardMemSize, int feedback, boolean chunked, float delayInMs, float sampleRate) {
        super(forwardMemSize);
        samplingFrequency = sampleRate;
        setFeedback(feedback);
        setDelayOffset(delayInMs);
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
     * Sets the delay size in samples that the delayed signal will be delayed by.
     *
     * @param offset this is the size of the delay in samples. This is used to set the forward buffer when using chunks,
     *               and also to calculate the size of the final output array.
     */

    public void setDelayOffset(float offset) {
        delaySamples = ((int) (offset * (float) samplingFrequency / (float) 1000));
    }

    /**
     * Determines whether chunked data is being used or not
     *
     * @param chunk returns true if the user has indicated that chunked data is being used.
     */

    public void setChunked(boolean chunk) {
        chunkedData = chunk;
    }

    /**
     * Process method calls appropriate allpassProcess or allPassProcessChunked method depending on the users decision to
     * use or not chunked data.
     *
     * @param input short array containing the input data to be passed on to respective method
     */

    public short[] process(short[] input) {

        if (chunkedData == false) {
            outputData = allPassProcess(input);
        } else {
            outputData = allPassProcessChunked(input);
        }
        return outputData;
    }

    /**
     * Method for non chunked data. Creates a delayed signal after the original data, and also combines two comb delays
     * creating creating further delayed signals with an initial signal multiplied by -feedbackFactor. Works in 32bit
     * (ints) to avoid any (unintentional) clipping from using shorts.
     *
     * @param input short array containing the input data to be manipulated by algorithm
     */

    public short[] allPassProcess(short input[]) {

        short[] output = new short[input.length + delaySamples];

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

            // This is the algorithm to create an all pass filter
            int outputSample = (int) ((-feedbackFactor * inputSample / 100) + delayedInput + (
                    feedbackFactor * delayedOutput / 100));

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
     * Method for chunked data. Creates a delayed signal after the original data, and also combines two comb delays
     * creating creating further delayed signals with an initial signal multiplied by -feedbackFactor. Works in 32bit
     * (ints) to avoid any (unintentional) clipping from using shorts.
     *
     * @param input short array containing the input data to be manipulated by algorithm
     */

    public short[] allPassProcessChunked(short input[]) {

        // inserts current data into array which comprises of current data + forwardBuffer
        // from the previous chunk
        super.preProcess(input);

        short[] forwardBuffer = new short[delaySamples];
        short[] output = new short[input.length + delaySamples];

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

            // This is the algorithm to create an all pass filter
            int outputSample = (int) ((-feedbackFactor * inputSample / 100) + delayedInput + (
                    feedbackFactor * delayedOutput / 100));

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
