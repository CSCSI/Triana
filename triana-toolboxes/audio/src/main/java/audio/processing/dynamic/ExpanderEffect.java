package audio.processing.dynamic;

import audio.processing.tools.NormaliserEffect;

/**
 * **************************************************************************** A Hard-Knee Expander Effect which allows
 * the user to lower the level of signal which is under a threshold set by the user. The attenuation is decided by the
 * 'expansion ratio', and the speed at which the signal is attenuated is decided by the attack and release times. An
 * expander is the opposite of a compressor.
 *
 * @author Eddie Al-Shakarchi - e.alshakarchi@cs.cf.ac.uk
 * @version $Revision: 4052 $
 * @see Expander
 */

public class ExpanderEffect {

    float samplingFrequency;
    float thresholdLevel; // In (negative) Decibels
    double attackIncrement;
    double releaseIncrement;
    double currentExpansionRatio = 1.0; // start at 1.0
    double thresholdValue;
    double expansionRatio;
    double gainLevel; // In Samples
    int attackTime; // In Samples
    int releaseTime; // In Samples
    int threshWidth;
    boolean autoGain;
    String detection;
    NormaliserEffect normalise = null;

    // Create buffer arrays
    short[] output;
    short[] outputData;

    /**
     * Creates an Expander effect
     *
     * @param threshold      the threshold level in (negative) decibels. When the signal goes under this threshold,
     *                       expansion is applied
     * @param ratio          the expansion ratio is the ratio of change of the input signal versus the change in the
     *                       output signal
     * @param attack         the time taken before the expander kicks into action. In milliseconds.
     * @param release        the time taken for the 'expansion' to stop after the signal falls below the threshold. In
     *                       milliseconds.
     * @param gain           make up gain to boost the signal level after expansion
     * @param autoGain       boolean value to indicate if the user wishes to automatically normalise the audio after
     *                       compression
     * @param detectionType  multiple choice string input which chooses the type of choices by process method to choose
     *                       suitable method. Chooses between Peak, Region Average, Region Max, and RMS.
     * @param thresholdWidth the size of the window which is the 'region' being examined too be able to set the
     *                       threshold
     * @param sampleRate     the sample rate of the incoming audio
     */

    public ExpanderEffect(float threshold, double ratio, int attack, int release,
                          double gain, boolean autoGain, String detectionType,
                          int thresholdWidth, float sampleRate) {
        samplingFrequency = sampleRate;
        setThreshold(threshold);
        setRatio(ratio);
        setAttackTime(attack);
        setReleaseTime(release);
        setGain(gain);
        setDetectionType(detectionType);
        setThresholdWidth(thresholdWidth);
        setAutoGain(autoGain);
    }

    /**
     * Sets the threshold level in DECIBELS. 0dB is the nominal/maximum value, the threshold is therefore relative to this,
     * in negative decibels. This works for short (a maximum of 32767)
     *
     * @param thresh this is an integer value, which is converted to a decibel value, relative to the maximum short value
     *               of 32767
     */

    public void setThreshold(float thresh) {
        thresholdLevel = (int) (Math.pow(10, (thresh / 20)) * 32767);
    }

    /**
     * Sets the compression ratio.
     *
     * @param rat this is compression ratio value. Any samples which are over the threshold level are divided by this
     *            value, but first taking into account the attack/release times
     */

    public void setRatio(double rat) {
        expansionRatio = rat;
    }

    /**
     * Sets the compressor's attack time in samples, after converting from milliseconds
     *
     * @param att this is the size of the attack 'delay'. This is used to set delay before the compression is applied, and
     *            create the compression envelope
     */

    public void setAttackTime(int att) {
        attackTime = ((int) (att * (float) samplingFrequency / (float) 1000));
    }

    /**
     * Sets the compressors release time in samples, after converting from milliseconds
     *
     * @param rel this is the size of the release 'delay'. This is used before the compression is stopped being applied,
     *            and creates the compression release envelope
     */

    public void setReleaseTime(int rel) {
        releaseTime = ((int) (rel * (float) samplingFrequency / (float) 1000));
    }

    /**
     * Sets the make up gain. After the signal is compressed, the audio may be quieter and so the user may wish to
     * increase the volume to compensate. This value is ignored if 'auto gain' is selected.
     *
     * @param vol this can be used to increase the volume of the signal, after compression. This is in decibels
     *            (logarithmic).
     */

    public void setGain(double vol) {
        gainLevel = (double) (Math.pow(10, (vol / 20)));
    }

    /**
     * Sets the type of threshold calculator that will be used (Peak, RMS, Region Average or Region Max)
     *
     * @param detect this is the type of threshold calculator which will be used.
     */

    public void setDetectionType(String detect) {
        detection = detect;
    }

    /**
     * Sets the threshold width that will be used in samples, after converting from milliseconds. This is used when region
     * max, region average, and RMS threshold calculators are used. This is essentially a 'window' where only a small part
     * of the audio file is examined. If the user chooses 'Region Average' and sets a threshold width of 10ms, then the
     * average value of the samples in the regions' 'window' is taken
     *
     * @param tWidth this is the threshold width, in milliseconds. This method is used to convert tWidth to samples.
     */

    public void setThresholdWidth(int tWidth) {
        threshWidth = ((int) (tWidth * (float) samplingFrequency / (float) 1000));
    }

    /**
     * Selects if the signal will be normalised before outputting
     *
     * @param auto this is a boolean variable. This is used to to decide if the signal will be normalised to 100%, or if
     *             the make up gain is used
     */

    public void setAutoGain(boolean auto) {
        autoGain = auto;
    }

/************************************
 * The Three Threshold Calculators...*
 *************************************/

    /**
     * Method to calculate the threshold when 'Regional Max' is selected. This calculates the threshold by finding the
     * maximum value in the window of the array which is being considered. The window size is set by the thresholdWidth
     * variable
     *
     * @param currentChunk    the current short array or 'audio chunk' being considered. If chunking is not being used,
     *                        this will stay constant.
     * @param currentPosition the current position of the sample being considered in for loop in the process method.
     */

    public short getRegionMax(short[] currentChunk, int currentPosition) {

        short max = 0;
        int startPos = currentPosition - (threshWidth / 2);
        int endPos = startPos + threshWidth;

        // This makes sure that the area being examined is not bigger than the chunk size
        if (endPos > currentChunk.length) {
            endPos = currentChunk.length;
            startPos = startPos - (endPos - currentChunk.length);
        }

        // This makes sure that for the start of the entire wave, that StartPos does not
        // give ArrayOutOfBounds at the beginning of the wave
        if (startPos < 0) {
            startPos = 0;
        }

        // For each sample in the little segment
        for (int i = startPos; i < endPos; i++) {

            if (Math.abs(currentChunk[i]) > Math.abs(max)) {
                max = currentChunk[i];
            }
        }
        return max;
    }

    /**
     * Method to calculate the threshold when 'Regional Average' is selected. This calculates the threshold by finding
     * the average value in the window of the array which is being considered. This can help to give a more subtle and
     * natural compressor sound. The window size is set by the thresholdWidth variable
     *
     * @param currentChunk    the current short array or 'audio chunk' being considered. If chunking is not being used,
     *                        this will stay constant.
     * @param currentPosition the current position of the sample being considered in for loop in the process method.
     */

    public short getRegionAverage(short[] currentChunk, int currentPosition) {

        short temp = 0;
        short average;
        int startPos = currentPosition - (threshWidth / 2);
        int endPos = startPos + threshWidth;

        // This makes sure that the area being examined is not bigger than the chunk size
        if (endPos > currentChunk.length) {
            endPos = currentChunk.length;
            startPos = startPos - (endPos - currentChunk.length);
        }

        // This makes sure that for the start of the entire wave, that StartPos does not
        // give ArrayOutOfBounds at the beginning of the wave
        if (startPos < 0) {
            startPos = 0;
        }

        // For each sample in the little segment
        for (int i = startPos; i < endPos; ++i) {
            temp += (short) currentChunk[i];
        }

        average = (short) (temp / currentChunk.length);
        return average;
    }

    /**
     * Method to calculate the threshold when 'Regional RMS' is selected. This calculates the threshold by finding the
     * value which is the 'Root Mean Square' in the window of the array which is being considered. This models human
     * hearing more accurately to human hearing. The window size is set by the thresholdWidth variable.
     *
     * @param currentChunk    the current short array or 'audio chunk' being considered. If chunking is not being used,
     *                        this will stay constant.
     * @param currentPosition the current position of the sample being considered in for loop in the process method.
     */

    public short getRegionRMS(short[] currentChunk, int currentPosition) {

        short temp = 0;
        short rms;
        int startPos = currentPosition - (threshWidth / 2);
        int endPos = startPos + threshWidth;

        // This makes sure that the area being examined is not bigger than the chunk size
        if (endPos > currentChunk.length) {
            endPos = currentChunk.length;
            startPos = startPos - (endPos - currentChunk.length);
        }

        // This makes sure that for the start of the entire wave, that StartPos does not
        // give ArrayOutOfBounds at the beginning of the wave
        if (startPos < 0) {
            startPos = 0;
        }

        // For each sample in the little segment
        for (int i = startPos; i < endPos; ++i) {
            temp += (short) currentChunk[i];
        }

        rms = (short) (temp / currentChunk.length);

        return rms;
    }

    /**
     * Process method that compresses the audio. A threshold calculator is chosen and the attack and release envelopes are
     * created, to create a subtle compression effect. The values that are over the threshold are divided by the current
     * compression ratio, which is worked out depending on the attack/release times.Works in 32bit (ints) to avoid any
     * (unintentional) clipping from using shorts.
     *
     * @param input short array containing the input data to be manipulated by algorithm
     * @return a short array which has been manipulated.
     */

    public short[] process(short input[]) {

        short[] output = new short[input.length];
        int[] temp = new int[input.length];
        int outputSample = 0;
        int finalSample = 0;
        int maxValue = 0;

        attackIncrement = (double) expansionRatio / (double) attackTime;
        releaseIncrement = (double) expansionRatio / (double) releaseTime;

//		// For each sample, find the maximum
//		for (int n = 0; n < input.length; n++) {
//
//			outputSample = (int)(input[n]);
//
//			if (Math.abs(outputSample) > Math.abs(maxValue)){
//				maxValue = outputSample;
//			}
//		}

//      thresholdValue = maxValue * ((double)thresholdLevel);
//      System.out.println("ThresholdValue = " + thresholdValue);
        System.out.println("ThresholdLevel = " + thresholdLevel);


        // For each sample in the array...
        for (int n = 0; n < input.length; n++) {

            if (detection.equals("Peak")) {

                if (Math.abs(input[n]) < (int) thresholdLevel)  //switch on
                {
                    currentExpansionRatio = currentExpansionRatio + attackIncrement;
                } else {
                    currentExpansionRatio = currentExpansionRatio - releaseIncrement;
                }
            } else if (detection.equals("RMS")) {

                if (getRegionRMS(input, n) < (int) thresholdLevel)  //switch on
                {
                    currentExpansionRatio = currentExpansionRatio + attackIncrement;
                } else {
                    currentExpansionRatio = currentExpansionRatio - releaseIncrement;
                }
            } else if (detection.equals("Region Max")) {

                if (getRegionMax(input, n) < (int) thresholdLevel)  //switch on
                {
                    currentExpansionRatio = currentExpansionRatio + attackIncrement;
                } else {
                    currentExpansionRatio = currentExpansionRatio - releaseIncrement;
                }
            } else {  // If detection = "Region Average"

                if (getRegionAverage(input, n) < (int) thresholdLevel)  //switch on
                {
                    currentExpansionRatio = currentExpansionRatio + attackIncrement;
                } else {
                    currentExpansionRatio = currentExpansionRatio - releaseIncrement;
                }
            }

            // Stops the Expansion ratio from going out of range
            if (currentExpansionRatio > expansionRatio) {
                currentExpansionRatio = expansionRatio;
            }

            // Stops the Expansion ratio from going out of range
            if (currentExpansionRatio < 1.0) {
                currentExpansionRatio = 1.0;
            }

            // Actually compress the values over the set threshold
            if (Math.abs(input[n]) < (int) thresholdLevel) {
                outputSample = (int) (input[n] / currentExpansionRatio);
            } else {
                outputSample = input[n];
            }

            temp[n] = outputSample;

        } // End of main for loop

        // If autogain is not selected, then attentuate the signal level and limit
        // so that there is no signal overflow

        if (autoGain == false) {

            for (int n = 0; n < input.length; n++) {

                temp[n] = (int) (temp[n] * gainLevel);

                // Limits outputSample to max 16bit (short) value
                int limit = temp[n];

                if (limit > 32767) {
                    limit = 32767;
                } else if (limit < -32767) {
                    limit = -32767;
                }

                // Turns int back into short value in output array after manipulation
                // and limiting
                output[n] = (short) limit;

            }// End of for loop

            return output;
        }// End of if

        // Post processing -> sorting out the normalisation. If autogain is chosen,
        // then the signal is normalised to 100% and the make up gain slider is ignored.
        // Otherwise, the signal is always passed throught the make up gain slider (above)

        else {
            short[] finalOutput = new short[output.length];

            if (normalise == null){
				normalise = new NormaliserEffect(100);
			}

			finalOutput = normalise.process(temp);
			return finalOutput;
		}
    }
}
