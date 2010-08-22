package audio.processing.modulation;

import triana.types.audio.AudioEffect16Bit;


/**
 * A Variable-delay Effect which allows user to create a chorus or flanger unit by adjusting variables. A single delay
 * is created using a feedforward comb filter. These effects work on the principle of varying the length of the delay.
 * The user can set the attenuation the level of the delayed signal and also adjust the delay time, period length and
 * amplitude by adjusting the settings using the GUI. VariableDelayEffect extends AudioEffect 16bit to allow for chunked
 * data.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see VariableDelay
 */

public class VariableDelayEffect extends AudioEffect16Bit {

    int feedbackFactor;
    int forwardSize;
    int delaySamples;
    int oscillationSize;
    int amplitudeLevel;
    int delayedInput;
    int delayedOutput;
    boolean chunkedData;
    String oscillator;
    String filter;
    String lfoSummingType;
    float samplingFrequency;

    // Create buffer arrays
    short[] output;
    short[] outputData;
    LFO lfo = null;

    /**
     * Creates an variable delay effect with a particular forward memory size and oscillation type. A new instance of
     * lfo is created depending on the type of oscillation required.
     *
     * @param forwardMemSize    the size of the forward memory to be used by AudioEffect16bit class.
     * @param feedback          the level of attenutation of the delayed signal in the the feedback comb filter. Should
     *                          be divided by 100 to give value in percent.
     * @param LFOType           chooses the type of oscillation to use.
     * @param oscillationPeriod sets time it takes for one complete oscillilation
     * @param summingType       sets the way to add the returned oscillator value to the original delay
     * @param filterType        sets which type of filter to use. Can be comb, allpass, or delay only
     * @param amplitude         sets level amplitude (ie sets the maximum 'peak' value and minimum 'trough' value) of
     *                          the waveform.
     * @param delayOffset       the size of the delay in samples. Used to calculate appropriate buffer size
     * @param chunked           boolean value to indicate if the user is using chunked data. Used by process method to
     *                          choose suitable method.
     */

    public VariableDelayEffect(int forwardMemSize, int feedback, String LFOType, String filterType,
                               int oscillationPeriod,
                               String summingType, int amplitude, float delayInMs, boolean chunked, float sampleRate) {
        super(forwardMemSize);
        samplingFrequency = sampleRate;
        setFeedback(feedback);
        setDelayOffset(delayInMs);
        setChunked(chunked);
        setLFOType(LFOType);
        setSummingType(summingType);
        setFilterType(filterType);
        setOscillationPeriod(oscillationPeriod);
        setAmplitude(amplitude);
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
     * Determines which type of low frequency oscillator will be instantiated
     *
     * @param lowfreqosc the type of LFO waveform that is being used. At present can be triangular or sinusoidal, but more
     *                   LFO waveforms can be implemented in the future.
     */

    public void setLFOType(String lowfreqosc) {

        oscillator = lowfreqosc;

        if (oscillator.equals("triangular")) {
            lfo = new TriangularLFO(oscillationSize, amplitudeLevel); // returns -50 to +50 by default
        } else if (oscillator.equals("sinusoidal")) {
            lfo = new SinusoidalLFO(oscillationSize, amplitudeLevel);
        }
    }

    /**
     * Sets the type of summing of the original signal with the varied signal. The variable delay line can be added to the
     * original delay in three different ways.
     *
     * @param summer the type of summing algorithm to be applied
     */


    public void setSummingType(String summer) {
        lfoSummingType = summer;
    }

    /**
     * Sets the type of filter to be used to create the delay. This can be a comb filter, an allpass filter, or the delay
     * only can be used, in order to only give the oscillating delayed signal
     *
     * @param filt the type of filter to create the delay
     */

    public void setFilterType(String filt) {
        filter = filt;
    }

    /**
     * Sets the size of the oscillationPeriod of one complete cycle of the LFO waveform, in samples.
     *
     * @param osc the size of the oscillation period in samples.
     */

    public void setOscillationPeriod(int osc) {
        oscillationSize = osc;
        if (lfo != null) {
            lfo.setPeriod(oscillationSize);
        }
        System.out.println("osc: " + oscillationSize);
    }

    /**
     * Sets the size of the amplitude of the LFO waveform. This is essentially the maximum and minimum values that the
     * delay can vary by.
     *
     * @param amp the size if the amplitude of the LFO
     */

    public void setAmplitude(int amp) {
        amplitudeLevel = amp;
        if (lfo != null) {
            lfo.setAmplitude(amplitudeLevel);
        }
        System.out.println("amp: " + amplitudeLevel);

    }

    /**
     * Process method calls appropriate Variable delay Process method depending on the users decision to use or not chunked
     * data. Returns manipulated data.
     *
     * @param input short array containing the input data to be passed on to respective method
     */

    public short[] process(short[] input) {

        if (chunkedData == false) {
            outputData = variProcess(input);
        } else {
            outputData = variProcessChunked(input);
        }
        return outputData;
    }

    /**
     * Method for non chunked data. Creates a delayed signal after the original data which varies in length. Works in 32bit
     * (ints) to avoid any (unintentional) clipping from using shorts.
     *
     * @param input short array containing the input data to be passed on to respective method
     */

    public short[] variProcess(short input[]) {

        short[] output = new short[input.length + delaySamples];
        int outputSample = 0;
        int delayedPosition = 0;

        // For each sample in the output array
        for (int n = 0; n < output.length; n++) {

            // Select between the different summing algorithms available
            if (lfoSummingType.equals("Type 1")) {
                delayedPosition = n - delaySamples + (int) lfo.getInt();
                lfo.advance();
            } else if (lfoSummingType.equals("Type 2")) {
                delayedPosition = n - delaySamples + (amplitudeLevel + (int) lfo.getInt());
                lfo.advance();
            } else {
                int lfoDelayModulation = delaySamples * (amplitudeLevel + (int) lfo.getInt()) / (amplitudeLevel * 2);
                delayedPosition = n - delaySamples + lfoDelayModulation;
                lfo.advance();
            }

            int delayedInput = 0;
            if (delayedPosition >= 0 && delayedPosition < input.length) {
                delayedInput = input[delayedPosition];
            }

            int delayedOutput = 0;
            if (delayedPosition >= 0 && delayedPosition < output.length) {
                delayedOutput = output[delayedPosition];
            }

            short inputSample = 0;
            if (n < input.length) {
                inputSample = input[n];
            }

            // Select between the different types of filters available
            if (filter.equals("comb")) {
                outputSample = (int) ((inputSample) + (feedbackFactor * delayedInput / 100));
            } else if (filter.equals("allpass")) {
                outputSample = (int) ((inputSample) + (-feedbackFactor * inputSample / 100) + (
                        feedbackFactor * delayedInput / 100));
            } else {
                outputSample = (int) ((feedbackFactor * delayedInput / 100));
            }

            // Limits outputSample to max 16bit (short) value
            int limit = outputSample;
            if (limit > 32767) {
                limit = 32767;
            } else if (limit < -32767) {
                limit = -32767;
            }

            output[n] = (short) limit;
        }// End of for loop

        return output;
    }

    /**
     * Method for chunked data. Creates a delayed signal after the original data which varies in length. Works in 32bit
     * (ints) to avoid any (unintentional) clipping from using shorts.
     */

    public short[] variProcessChunked(short input[]) {

        // inserts current data into array which comprises of current data + forwardBuffer
        // from the previous chunk
        super.preProcess(input);

        short[] forwardBuffer = new short[forwardSize];
        short[] output = new short[input.length + forwardSize];

        int outputSample = 0;
        int delayedPosition = 0;

        for (int n = 0; n < output.length; n++) {

            // Select between the different summing algorithms available
            if (lfoSummingType.equals("Type 1")) {
                delayedPosition = n - delaySamples + (int) lfo.getInt();
                lfo.advance();
            } else if (lfoSummingType.equals("Type 2")) {
                delayedPosition = n - delaySamples + (amplitudeLevel + (int) lfo.getInt());
                lfo.advance();
            } else {
                int lfoDelayModulation = delaySamples * (amplitudeLevel + (int) lfo.getInt()) / (amplitudeLevel * 2);
                delayedPosition = n - delaySamples + lfoDelayModulation;
                lfo.advance();
            }

            int delayedInput = 0;
            if (delayedPosition >= 0 && delayedPosition < input.length) {
                delayedInput = input[delayedPosition];
            }

            int delayedOutput = 0;
            if (delayedPosition >= 0 && delayedPosition < output.length) {
                delayedOutput = output[delayedPosition];
            }

            short inputSample = 0;
            if (n < input.length) {
                inputSample = input[n];
            }

            // Select between the different types of filters available
            if (filter.equals("comb")) {
                outputSample = (int) ((inputSample) + (feedbackFactor * delayedInput / 100));
            } else if (filter.equals("allpass")) {
                outputSample = (int) ((inputSample) + (-feedbackFactor * inputSample / 100) + (
                        feedbackFactor * delayedInput / 100));
            } else {
                outputSample = (int) ((feedbackFactor * delayedInput / 100));
            }

            // Limits outputSample to max 16bit (short) value
            int limit = outputSample;
            if (limit > 32767) {
                limit = 32767;
            } else if (limit < -32767) {
                limit = -32767;
            }

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
