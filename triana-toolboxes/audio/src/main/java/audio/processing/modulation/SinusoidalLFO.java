package audio.processing.modulation;

/**
 * This class implements the LFO interface and creates a low frequency oscillator with a sinusoidal oscillation
 * pattern.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see LFO
 * @see trianagularLFO
 * @see VariableDelay
 * @see VariableDelayEffect
 */

public class SinusoidalLFO implements LFO {

    double theta = 0.0;
    double amplitude = 1.0;
    int period;

    /* Class constructor creates sinusoidal oscillator with particular
     * period of complete oscillation and gain levels
     *
     * @param period this can be adjusted to change the number of sampless for
     * 				output oscillator to complete one complete cycle or oscillation.
     * @param amplitude can be adjusted to vary the level aplitude of the oscillation
     * 				   in order to change the delay time
     */

    SinusoidalLFO(int period, double amplitude) {
        setPeriod(period);
        setAmplitude(amplitude);
    }

    /**
     * Method simply sets the size of the oscillation period. If the sample rate of an audio file is 44100 then a period
     * set to 44100 corresponds to 1 second per oscillation
     *
     * @param period value to set the size of the oscillation
     */

    public void setPeriod(int period) {
        this.period = period;
    }

    /**
     * Method simply sets the size of the amplitude and thus decides the amplitude (ie sets the maximum 'peak' value and
     * minimum 'trough' value) of the sinusoidal waveform
     *
     * @param amplitude value to set the size of the amplitude of the waveform
     */

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    /**
     * Advances the waveform. This is called for each sample in an array and actually creates the sinusoidal waveform using
     * the forumla "theta = theta + (2*PI/period);"
     */

    public void advance() {
        theta += (2 * Math.PI / period);
    }

    /**
     * Returns the value of theta * amplitude as a double
     *
     * @return the value of theta (the 'x' value of theta) multiplied by the amplitude ('y' value)
     */

    public double getDouble() {
        return Math.sin(theta) * amplitude;
    }

    /**
     * This method takes the value returned by the getDouble method and uses the Math.rint(double) to return the integer
     * value that is closest in value to getDouble() and is equal to a mathematical integer. This double is cast as an
     * integer and returned.
     *
     * @return the closest integer to the value returned in getDouble()
     */


    public int getInt(){
		return (int)Math.rint(getDouble());
	}
}
