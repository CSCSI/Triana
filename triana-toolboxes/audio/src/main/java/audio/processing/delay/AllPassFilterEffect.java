package audio.processing.delay;

/**
 * A Allpass-delay Effect which allows user to add a delayed signal onto the original by combing two Comb Filters. An
 * allpass filter is a building block of a Schroeder reverb device. The user can set the attenuation level of the
 * delayed signal and also adjust the delay time by adjusting the settings using the GUI.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2921 $
 * @see AllPassFilter
 */

public class AllPassFilterEffect {

    int delayLength;
    String filter;
    private int delayBufferSize = 0;
    int readIndex;
    int writeIndex;
    int bufIndex;
    int feedbackFactor;
    int delaySamples;
    int delayedInput;
    int delayedOutput;
    boolean chunkedData;
    float samplingFrequency;

    // Create buffer arrays
    short[] output;
    short[] outputData;
    private short[] delayBuffer = null;

    /**
     * Creates an allpass delay effect with a particular forward memory size.
     *
     * @param forwardMemSize the size of the forward memory to be used by AudioEffect16bit class.
     * @param feedback       the level of attenutation of the delayed signal in the the feedback comb filter. Should be
     *                       divided by 100 to give value in percent.
     * @param chunked        boolean value to indicate if the user is using chunked data. Used by process method to
     *                       choose suitable method.
     */

    public AllPassFilterEffect(int forwardMemSize, int feedback, boolean chunked, float delayInMs, float sampleRate) {
        //super(forwardMemSize);
        samplingFrequency = sampleRate;
        setFeedback(feedback);
        setDelayOffset(delayInMs);
        setChunked(chunked);
        doInitialisation();
    }

    public void doInitialisation() {

        delayBufferSize = delayLength;

        // Allocate new delay buffer
        delayBuffer = new short[delayBufferSize];

        // Index where dry sample is written
        writeIndex = 0;
        // Index where wet sample is read
        readIndex = 0;
        bufIndex = 0;
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
     * Sets the delay size in samples that the delayed signal will be delayed by. Also calculates the delay's total time to
     * live.
     *
     * @param offset this is the size of the delay in samples. This is used to set the forward buffer when using chunks,
     *               and also to calculate the size of the final output array.
     */

    public void setDelayOffset(float offset) {
        double loopGain = (double) (20 * (Math.log(((double) feedbackFactor / 100)) / (Math.log(10.0))));
        double decay60dbTime = (double) ((60 / -loopGain) * offset);
        delaySamples = ((int) (decay60dbTime * samplingFrequency) / 1000);
        delayLength = ((int) (offset * (float) samplingFrequency / (float) 1000));
        doInitialisation();
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
     * creating creating further delayed signals with an initial signal multiplied by -feedbackFactor. Works in 32bit to
     * avoid any (unintentional) clipping from using shorts.
     *
     * @param input short array containing the input data to be manipulated by algorithm
     */

    public short[] allPassProcess(short input[]) {

        short[] output = new short[input.length + delaySamples]; // Input + time to decay 60db
        int outputSample = 0;

        // For each sample in the output array
        for (int n = 0; n < output.length; n++) {

            // delayedPosition is only set once n is large enough
            int delayedPosition = n - delayLength;

            if (delayedPosition < 0) {
                delayedInput = 0;
                delayedOutput = 0;
            } else {
                if (n < input.length) { // When end of file reached
                    delayedInput = input[delayedPosition];
                }
                delayedOutput = output[delayedPosition];
            }

            short inputSample = 0;

            if (n < input.length) {
                inputSample = input[n];
            }

            if (n > input.length) {
                inputSample = 0;
            }
            // This is the algorithm to create an all pass filter
            outputSample = (int) ((-feedbackFactor * inputSample / 100) + delayedInput + (feedbackFactor * delayedOutput
                    / 100));

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
     * creating creating further delayed signals with an initial signal multiplied by -feedbackFactor. Works in 32bit to
     * avoid any (unintentional) clipping from using shorts.
     *
     * @param input short array containing the input data to be manipulated by algorithm
     */

    public short[] allPassProcessChunked(short input[]) {

        short[] outputArray = new short[input.length];

        for (int i = 0; i < input.length; i++) {

            double outputSample = delayBuffer[bufIndex] - (feedbackFactor * input[i]) / 100;
            delayBuffer[bufIndex++] = (short) (input[i] + (feedbackFactor * outputSample) / 100);
            bufIndex %= delayBufferSize;
            outputArray[i] = (short) outputSample;
        }
        return outputArray;
    }
}
