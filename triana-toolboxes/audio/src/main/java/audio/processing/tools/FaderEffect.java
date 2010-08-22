package audio.processing.tools;

/**
 * A Fader Effect which allows user to adjust the level of the incoming signal my multiplying each sample in the signal
 * by a float value determined in the GUI. This effect does not need to extend AudioEffect16bit to deal with chunks as
 * no forwardBuffer is added to the signal.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see Fader
 */

public class FaderEffect {

    double volumeLevel;

    // Create buffer arrays
    short[] output;

    /* Class constructor creates Fader effect with a particular
     * volume level.
     *
     * @param volume the float volume number which is to be multipled with the sample value
     * 				in order to adjust the volume of the sample.
     */

    public FaderEffect(double volumeInDB) {
        setVolume(volumeInDB);
    }

    /**
     * Sets the volume level. To be specific this sets how loud the signal will be. The entire array for the wave is
     * multipled by this value. In order to increase volume, a value greater than one is used. Using a value of less than
     * one decreases the value.
     *
     * @param vol volume level. This value should be less than one in order to decreases the volume. A value more than one
     *            increases the volume
     */

    public void setVolume(double vol) {
        volumeLevel = (double) (Math.pow(10, (vol / 20)));
        System.out.println("volumeLevel in set volume: " + volumeLevel);
        System.out.println("vol in set volume: " + vol);
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
        System.out.println("volumeLevel test: " + volumeLevel);

        // For each sample in the array
        for (int n = 0; n < input.length; n++) {

            // Actual algorithm below
            int outputSample = (int) (input[n] * volumeLevel);

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

    }  // End faderProcess method

}
