package audio.processing.distortion;

/**
 * A Fuzz-box distortion Effect which allows user to apply a distortion effect on the audio by lowering the 'acceptable'
 * threshold level to force clipping. The user can compensate for the loss in volume caused by limiting the threshhold
 * by adjusting the gain level using the GUI.This effect does not need to extend AudioEffect16bit to deal with chunks as
 * no forwardBuffer is added to the signal.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 */

public class FuzzboxEffect {

    float gainLevel;
    int thresholdLevel;

    // Create buffer arrays
    short[] output;

    /* Class constructor creates Fuzzbox effect with particular
     * threshold and gain levels
     *
     * @param gain this value can be adjusted to compensate for loss in output volume
     * 			  due to a lowering of the treshold
     * @param threshold can be lowered in order to force the signal to clip
     */

    public FuzzboxEffect(float gain, int threshold) {
        setGain(gain);
        setThreshold(threshold);
    }

    /**
     * Sets the volume or 'gain' level. To be specific this sets how loud the signal will be. The entire array for the wave
     * is multipled by this value. In order to increase volume, a value greater than one is used. Using a value of less
     * than one decreases the value.
     *
     * @param vol volume level. This value should be less than one in order to decreases the volume. A value more than one
     *            increases the volume
     */


    public void setGain(float vol) {
        gainLevel = vol;
    }

    /**
     * Sets the maximum acceptable threshold level for the amplitude of values in the array. Decreasing the threshold of
     * acceptable values results in MORE distortion.
     *
     * @param thresh threshold value for distortion. Decreasing the threshold of acceptable values results in MORE
     *               distortion
     */

    public void setThreshold(int thresh) {
        thresholdLevel = thresh;
    }

    /**
     * Allows user to distort the audio signal (by forcing the signal to clip) and also adjust the volume of the input
     * sample to compensate for the lower output level. Works in 32bit.
     * <p/>
     * (ints) to avoid any (unintentional) clipping from using shorts, however the limiter would rarely be used.
     *
     * @param input short array containing the input data to be manipulated by algorithm
     */

    public short[] process(short input[]) {

        short[] output = new short[input.length];
        System.out.println("input.length of process in fuzzboxeffect = " + input.length);

        // For each sample in the array
        for (int n = 0; n < input.length; n++) {

            int sample = input[n];

            if (sample > thresholdLevel) {
                sample = thresholdLevel;
            } else if (sample < -thresholdLevel) {
                sample = -thresholdLevel;
            }

            int outputSample = (int) (sample * gainLevel);

            // The following code limits the int to a maximum
            // limit of 32767 so that there is no clipping when
            // outputting the audio.

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
}
