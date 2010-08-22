package audio.processing.delay;

/**
 * A Comb-delay Effect which allows user to add a delayed signal onto the original by creating a feedforward and
 * feedback Comb Filterbased on a building block of a reverb device. The user can set the attenuation level of the
 * delayed signal and also adjust the delay time by adjusting the settings using the GUI.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see CombFilter
 */

public class CombFilterEffect {

    float feedbackFactor;
    int delaySamples;
    int delayedInput;
    int delayedOutput;
    int delayLength;
    String filter;
    boolean chunkedData;
    float samplingFrequency;
    int noOfChannels2;
    int delaySizeTest;

    private int delayBufferSize = 0;
    private short[] delayBuffer = null;
    private int readIndex = 0;
    private int writeIndex = 0;

    // Create buffer arrays
    short[] output;
    short[] outputData;

    /**
     * Creates an comb filter delay effect with a particular forward memory size.
     *
     * @param feedback   the level of attenutation of the delayed signal in the the feedback comb filter. Should be
     *                   divided by 100 to give value in percent.
     * @param filterType sets which type of filter to use. Can be Standard Feedback Filter or Feedback Loop Only
     * @param chunked    boolean value to indicate if the user is using chunked data. Used by process method to choose
     *                   suitable method.
     */

    public CombFilterEffect(int feedback, String filterType, boolean chunked, float delayInMs, float sampleRate,
                            int noOfChannels) {
        samplingFrequency = sampleRate;
        noOfChannels2 = noOfChannels;
        setFeedback(feedback);
        setDelayOffset(delayInMs);
        setFilterType(filterType);
        setChunked(chunked);
        doInitialisation();
    }

    public void doInitialisation() {

        //int delayOffset = delayInMs
        delayBufferSize = delaySizeTest;

        // Allocate new delay buffer
        delayBuffer = new short[delayBufferSize];

        // Index where dry sample is written
        writeIndex = 0;
        // Index where wet sample is read
        readIndex = 0;
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
        double loopGain = (double) (20 * (Math.log(((double) feedbackFactor / 100)) / (Math.log(10.0))));
        double decay60dbTime = (double) ((60 / -loopGain) * offset);
        delaySamples = ((int) (decay60dbTime * samplingFrequency) / 1000);
        delayLength = ((int) ((offset * (float) samplingFrequency) / (float) 1000));
        delaySizeTest = ((int) ((offset * (float) samplingFrequency * noOfChannels2) / (float) 1000));
        doInitialisation();
        //System.out.println("delayLength = " + delayLength);
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
                if (n < input.length) { // Only before end of file is reached
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

            if (filter.equals("Standard Feedback Filter")) {
                // Algorithm to create comb filter with feedback loop. This represents an IIR recursive delay comb filter.
                outputSample = (int) ((inputSample) + (feedbackFactor * delayedOutput / 100));
            } else {
                // Algorithm to create ONLY THE feedback loop. This represents an IIR recursive delay comb filter.
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

    public short[] combProcessChunked(short input[]) {

        short[] outputArray = new short[input.length];

        // Do the processing
        for (int i = 0; i < input.length; i++) {

            double inputSample = (double) input[i];
            double delaySample = delayBuffer[readIndex++];
            //double outputSample = delayBuffer[readIndex++];

            // Apply gain and feedback to sample
            inputSample += (delaySample * feedbackFactor) / 100;

            // Store sample in delay buffer
            if (filter.equals("Standard Feedback Filter")) {
                outputArray[i] = (short) (inputSample + (delaySample - inputSample));
            } else {
                outputArray[i] = (short) ((delaySample * feedbackFactor) / 100);
            }

            delayBuffer[writeIndex++] = (short) inputSample;

            // Update buffer indices
            readIndex %= delayBufferSize;
			writeIndex %= delayBufferSize;
		}
		return outputArray;
    }
}
