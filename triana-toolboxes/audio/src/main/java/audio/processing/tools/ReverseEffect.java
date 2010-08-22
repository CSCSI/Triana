package audio.processing.tools;

/**
 * A Reverse Effect which reverses the input wave.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see Reverse
 */

public class ReverseEffect {

    // Create buffer arrays
    short[] output;

    // Class constructor creates Reverse effect

    public ReverseEffect() {
    }

    /*
      * Reverses the array
      *
      * @param input short array containing the input data to be manipulated by algorithm
      * @returns a short array which has been manipulated.
      */

    public short[] process(short input[]) {

        short[] output = new short[input.length];
        //System.out.println("volumeLevel test: " + volumeLevel);

        short outputSample = 0;

        // For each sample in the array
        for (int i = 0; i < input.length; i++) {

            outputSample = input[input.length - 1 - i];
            input[input.length - 1 - i] = outputSample;
            output[i] = outputSample;
        }

        // End of for loop

        return output;

    }  // End process method

}
