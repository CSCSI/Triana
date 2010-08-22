package audio.processing.modulation;

/**
 * This is simply an interface class which allows lists a set of public abstract methods contained in classes
 * sinusoidalLFO and triangularLFO. While it was possible not to use an interface class, the use of an interface here
 * enables the use of different types of low frequency oscillators, and allows for future development of new types of
 * oscillation patterns, such as logarithmic, sawtooth etc. Also, other non-audio related classes and programs could use
 * this interface.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 * @see SinusoidalLFO
 * @see TriangularLFO
 * @see VariableDelay
 * @see VariableDelayEffect
 */

public interface LFO {

    void setPeriod(int period);

    void setAmplitude(double amplitude);

    void advance();

    double getDouble();

    int getInt();
}
