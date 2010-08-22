package audio.processing.dynamic;

import audio.processing.tools.NormaliserEffect;

/**
 * **************************************************************************** A Hard-Knee Compressor Effect which
 * allows the user to attenuate the level of signal which is over a threshold set by the user. The attenuation is
 * decided by the 'compression ratio', and the speed at which the signal is attenuated is decided by the attack and
 * release times.
 *
 * @author Eddie Al-Shakarchi - e.alshakarchi@cs.cf.ac.uk
 * @version $Revision: 4052 $
 * @see Compressor
 */

public class CompressorEffect {

    float samplingFrequency;
    float thresholdLevel; // In samples
    float thresholdInDBs; // In (negative) Decibels
    float attackIncrement;
    float releaseIncrement;
    double currentCompressionRatio = 1.0; // start at 1.0
    double thresholdValue;
    double compressionRatio;
    double gainLevel; // In Samples
    int attackTime; // In Samples
    int releaseTime; // In Samples
    int threshWidth;
    boolean autoGain;
    String detection;
    NormaliserEffect normalise = null;

    float numOfDbs;
    float excursion;
    float gainCoeff;
    float correction;

    // Create buffer arrays
    short[] output;
    short[] outputData;

    /**
     * Creates a Compressor effect
     *
     * @param threshold      the threshold level in (negative) decibels. When the signal goes over this threshold,
     *                       compression is applied
     * @param ratio          the compression ratio is the ratio of change of the input signal versus the change in the
     *                       output signal
     * @param attack         the time taken before the compressor kicks into action. In milliseconds.
     * @param release        the time taken for the compression to stop after the signal falls below the threshold. In
     *                       milliseconds.
     * @param gain           make up gain to boost the signal level after comrpession
     * @param autoGain       boolean value to indicate if the user wishes to automatically normalise the audio after
     *                       compression
     * @param detectionType  multiple choice string input which chooses the type of choices by process method to choose
     *                       suitable method. Chooses between Peak, Region Average, Region Max, and RMS.
     * @param thresholdWidth the size of the window which is the 'region' being examined too be able to set the
     *                       threshold
     * @param sampleRate     the sample rate of the incoming audio
     */

    public CompressorEffect(float threshold, double ratio, double attack, double release, double gain,
                            boolean autoGain, String detectionType, int thresholdWidth, float sampleRate) {
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
        thresholdInDBs = (float) (thresh);
    }

    /**
     * Sets the compression ratio.
     *
     * @param rat this is compression ratio value. Any samples which are over the threshold level are divided by this
     *            value, but first taking into account the attack/release times
     */

    public void setRatio(double rat) {
        compressionRatio = rat;
    }

    /**
     * Sets the compressor's attack time in samples, after converting from milliseconds
     *
     * @param att this is the size of the attack 'delay'. This is used to set delay before the compression is applied, and
     *            create the compression envelope
     */

    public void setAttackTime(double att) {
        attackTime = ((int) (att * (float) samplingFrequency / (float) 1000));
        attackIncrement = (float) (1 - (Math.pow(0.01, -1.0 / attackTime)));
    }

    /**
     * Sets the compressors release time in samples, after converting from milliseconds
     *
     * @param rel this is the size of the release 'delay'. This is used before the compression is stopped being applied,
     *            and creates the compression release envelope
     */

    public void setReleaseTime(double rel) {
        releaseTime = ((int) (rel * (float) samplingFrequency / (float) 1000));
        releaseIncrement = (float) (1 - (Math.pow(0.01, -1.0 / releaseTime)));
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

    public float getRegionMax(short[] currentChunk, int currentPosition) {

        float max = 0;
        float regionMaxInDB = 0;
        int startPos = currentPosition - (threshWidth / 2);
        int endPos = startPos + threshWidth;
        float currentSample = 0;

        // This makes sure that the area being examined is not bigger than the chunk size
        if (endPos > currentChunk.length) {
            endPos = currentChunk.length;
            startPos = startPos - (endPos - currentChunk.length);
        }

        // Ensures StartPos doesn't give ArrayOutOfBounds at the start of audio
        if (startPos < 0) {
            startPos = 0;
        }

        // For each sample in the little segment
        for (int i = startPos; i < endPos; i++) {
            //currentSample = (float)(currentChunk[i] / 32767.0);
            if (Math.abs(currentSample) > Math.abs(max)) {
                max = currentSample;
            }
        }

        regionMaxInDB = (float) (20 * Math.log(Math.abs(max)));
        return regionMaxInDB;
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

    public float getRegionAverage(short[] currentChunk, int currentPosition) {

        float temp = 0;
        float average;
        float regionAverageInDB = 0;
        int startPos = currentPosition - (threshWidth / 2);
        int endPos = startPos + (threshWidth / 2);

        // This makes sure that the area being examined is not bigger than the chunk size
        if (endPos > currentChunk.length) {
            endPos = currentChunk.length;
            startPos = startPos - (endPos - currentChunk.length);
        }

        // Ensures StartPos doesn't give ArrayOutOfBounds at the start of audio
        if (startPos < 0) {
            startPos = 0;
        }

        // For each sample in the little segment
        for (int i = startPos; i < endPos; i++) {
            temp += Math.abs(currentChunk[i]);
        }

        average = (float) (temp / threshWidth);
        //average = (float)(average / 32767.0);
        regionAverageInDB = (float) (20 * Math.log(Math.abs(average)));

        return regionAverageInDB;
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

    public float getRegionRMS(short[] currentChunk, int currentPosition) {

        short temp = 0;
        float regionRMSInDB = 0;
        short rms;
        int startPos = currentPosition - (threshWidth / 2);
        int endPos = startPos + threshWidth;

        // This makes sure that the area being examined is not bigger than the chunk size
        if (endPos > currentChunk.length) {
            endPos = currentChunk.length;
            startPos = startPos - (endPos - currentChunk.length);
        }

        // Ensures StartPos doesn't give ArrayOutOfBounds at the start of audio
        if (startPos < 0) {
            startPos = 0;
        }

        // For each sample in the little segment
        for (int i = startPos; i < endPos; ++i) {
            temp += (short) (currentChunk[i]);
        }

        rms = (short) (Math.sqrt(temp / currentChunk.length));
        regionRMSInDB = (float) (20 * Math.log(Math.abs(rms)));

        return regionRMSInDB;
    }

/**********************************
 * The Gain Side-Chain Algorithm...*
 ***********************************/

    /**
     * Sidechain thing to work out... many things, y'see
     *
     * @return a short array which has been manipulated.
     */

    public float gainSideChain(float inputSam, float attackCoefficient, float releaseCoefficient, short[] array,
                               int n) {

        float currentCorrection = 0;

        if (detection.equals("Peak")) {
            numOfDbs = (float) (20 * Math.log(Math.abs(inputSam)));
            excursion = numOfDbs - thresholdInDBs;
        } else if (detection.equals("Region Max")) {
            numOfDbs = getRegionMax(array, n); // Get threshold In DB
            excursion = (numOfDbs - thresholdInDBs); // Calculate excursion
        } else if (detection.equals("Region Average")) {
            numOfDbs = getRegionAverage(array, n); // Get threshold In DB
            excursion = (numOfDbs - thresholdInDBs); // Calculate excursion
        } else { // detection.equals("Region RMS")
            numOfDbs = getRegionRMS(array, n); // Get threshold In DB
            excursion = (numOfDbs - thresholdInDBs); // Calculate excursion
        }

        if (excursion > 0) {
            correction = (float) (excursion * ((1 / compressionRatio) - 1));
        } else {
            correction = 0;
        }

        // This is the two parallel 1st order single pole filters for attack and the release

        if (correction < currentCorrection) { // Then ATTACK!
            currentCorrection = ((float) (currentCorrection * (1 - attackCoefficient)) + (correction
                    * attackCoefficient));
        } else { // Then RELEASE!
            currentCorrection = ((float) (currentCorrection * (1 - releaseCoefficient)) + (correction
                    * releaseCoefficient));
        }

        gainCoeff = ((float) Math.pow(10, (-currentCorrection / 20)));
        //System.out.println("gainCoeff = " + gainCoeff);
        return gainCoeff;
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
        float gainCoefficient;
        float inSample;

//		System.out.println("attackIncrement = " + attackIncrement);
//		System.out.println("attackTime = " + attackTime);
//		System.out.println("compressionRatio = " + compressionRatio);
//      System.out.println("ThresholdLevel = " + thresholdLevel);

        // For each sample in the array...
        for (int n = 0; n < input.length; n++) {
            inSample = (float) (input[n] / 32767.0); // Converts to value between -1 and 1
            gainCoefficient = gainSideChain(inSample, attackIncrement, releaseIncrement, input, n);
            outputSample = (int) ((float) input[n] * gainCoefficient);
            temp[n] = outputSample;
        }

        // If autogain is not selected, then attentuate the signal level and limit so that there is no signal overflow
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

                // Turns int back into short value in output array after manipulation and limiting
                output[n] = (short) limit;

            }// End of for loop

            return output;
        }// End of if

        // Post processing -> sorting out the normalisation. If autogain is chosen,then the signal is normalised
        // to 100% and the make up gain slider is ignored. Otherwise, the signal is always passed throught
        // the make up gain slider (above)

        else {
            short[] finalOutput = new short[output.length];

            if (normalise==null){
				normalise = new NormaliserEffect(100);
			}

			finalOutput = normalise.process(temp);
			return finalOutput;
		}
    }
}
