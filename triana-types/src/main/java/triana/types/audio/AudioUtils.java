package triana.types.audio;

import javax.sound.sampled.AudioFormat;
import triana.types.SampleSet;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 4, 2010
 */

public class AudioUtils {
    public static final long filt = (long) (Math.pow(2.0, 8) - 1);

    /**
     * converts the input into a 16 bit audio input stream.  It scales the values so that they can fit within 16 bits.
     */
    public static byte[] to16BitByteArray(SampleSet input, AudioFormat format) {
        double dataIn[] = input.data;

        byte[] bytedata;

        AudioProcessing.normalizeTo16Bit(input);

        bytedata = new byte[input.size() * 2];
        int b = 2;
        int i, j;
        short bitVal;
        double vals[] = input.data;
        for (j = 0; j < vals.length; ++j) {
            bitVal = (short) vals[j];
            //   System.out.println(bitVal);

            for (i = 0; i < b; ++i) {
                bytedata[b - i - 1 + (j * b)] = (byte) (bitVal & filt);
                bitVal = (short) (bitVal >> 8);
            }
        }

        return bytedata;
    }
}