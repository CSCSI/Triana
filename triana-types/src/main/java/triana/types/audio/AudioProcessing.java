package triana.types.audio;

import javax.sound.sampled.AudioFormat;
import triana.types.SampleSet;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 4, 2010
 */

public class AudioProcessing {

    /**
     * @return the max value of a double array. Assumes the wave form is centred around zero.
     */
    public static double max(double[] dataIn) {
        double max = 0;

        for (int i = 0; i < dataIn.length; ++i) {
            max = Math.max(dataIn[i], max);
        }

        return max;
    }

    /**
     * Normalizes the input SampleSet according to its sample size i.e. 8-bit 16-bit etc and the percentage of amplitude
     * within this sample size. e.g. if percentage = 100 and the sample size is 8 bits then the max value of the sample
     * set will be 127.
     */
    public static void normalize(SampleSet input, int percentage, AudioFormat format) {
        int j;
        double dataIn[] = input.data;
        double range;

        double max = max(dataIn);

        range = Math.pow(2, format.getSampleSizeInBits() - 1) - 1;

        if (max < range) {
            return;
        }

//        System.out.println("Range = " + range);

        range = range * (percentage / 100);

        double scaler = range / max;

        for (j = 0; j < dataIn.length; ++j) {
            dataIn[j] *= scaler;
        }
    }

    /**
     * Noramlizes to 16-bit resolution to 90% of the bandwidth
     */
    public static void normalizeTo16Bit(SampleSet input) {
        int j;
        double dataIn[] = input.data;
        double range;

        range = (Math.pow(2, 7) - 1) * 0.9;

        double max = max(dataIn);

        if (max < range) {
            return;
        }

        double scaler = range / max;

        for (j = 0; j < dataIn.length; ++j) {
            dataIn[j] *= scaler;
        }
    }
}

