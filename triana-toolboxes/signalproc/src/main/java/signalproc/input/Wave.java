package signalproc.input;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;

/**
 * A sine-wave generator with a variable frequency which is changed by control double-clicking on the units icon.
 * Version 0.1 written by Ian Taylor then adapted by B.F. Shutz to add the control of phase and intensity and  G. S.
 * Jones to have the ability to retain phase and to control the type of wave to output i.e. sinusoidal, triangular, saw
 * tooth and square.
 *
 * @author Ian Taylor, Bernard Schutz, Gareth S. Jones
 * @version 1.4 2 March 2002
 */
public class Wave extends Unit {

    /**
     * The frequency of the wave
     */
    double frequency;
    double min, max;
    /**
     * The sampling rate
     */
    double samplingRate;
    /**
     * The initial phase of the wave (between 0 and 1), updated for successive output data sets if <i>phaseRet</i> i set
     * to true.
     */
    double phase;

    /**
     * The intensity of the wave
     */
    double intensity;

    /**
     * The number of samples to output
     */
    int samples;

    /**
     * The actual sine-wave
     */
    SampleSet wave;

    /**
     * Running value of Phase of wave
     */
    double phasePos;

    /**
     * Variable containing reference to the type of wave desired.
     */
    String type;

    /**
     * Variable indicating whether the phase is to be retained.
     */
    boolean phaseRet;

    /**
     * Time of first sample of current set, stored as the acquisition time. This is updated for successive output sets
     * if <i>phaseRet</i> is set to true.
     */
    double timeStamp;

    int count = 0;

    /**
     * Initialses information specific to Wave. You can put things in constructors which you need to initialise.
     * LoadParameters gets called after this function, so if there is a parameter file then the default vaues will get
     * taken over
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(0);
        setMinimumInputNodes(0);
        setMaximumInputNodes(0);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(Task.PROCESS_UPDATE);

        // Initialise task parameters to default values (if not already initialised)
        Task task = getTask();

        if (!task.isParameterName("frequency")) {
            task.setParameter("frequency", "200");
        }

        if (!task.isParameterName("intensity")) {
            task.setParameter("intensity", "1");
        }

        if (!task.isParameterName("phase")) {
            task.setParameter("phase", "0");
        }

        if (!task.isParameterName("phaseRet")) {
            task.setParameter("phaseRet", "false");
        }

        if (!task.isParameterName("samples")) {
            task.setParameter("samples", "512");
        }

        if (!task.isParameterName("samplingRate")) {
            task.setParameter("samplingRate", "8000.0");
        }

        if (!task.isParameterName("time")) {
            task.setParameter("time", "0.0");
        }

        if (!task.isParameterName("type")) {
            task.setParameter("type", "Sinusoid Wave");
        }

        // Initialise GUI Builder interface
        String guilines = "";
        guilines += "Frequency (Hz) $title frequency Scroller 1 4000 0 false\n";
        guilines += "Amplitude (Peak) $title intensity Scroller 0 100 0 false\n";
        guilines += "Phase (0-1) $title phase TextField\n";
        guilines += "Retain phase $title phaseRet Checkbox\n";
        guilines += "Samples $title samples TextField\n";
        guilines += "Sampling rate (samples/sec) $title samplingRate TextField\n";
        guilines += "Time of first sample $title time TextField\n";
        guilines
                += "Type of wave $title type Choice [Sinusoid Wave] [Saw Tooth] [Triangular Wave] [Square Wave] [Impulse]\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Outputs a wave
     */
    public void process() {
        if (intensity == 0.0) {
            wave = new SampleSet(samplingRate, samples, timeStamp);
        } else {
            wave = new SampleSet(samplingRate, createWaveform(), timeStamp);
        }

        wave.setTitle(getTask().getToolName());

        if (phaseRet) {
//            updateWidgetFor("time");
//            updateWidgetFor("phase");
            timeStamp += samples / samplingRate;
            phase = phasePos;
        }

        output(wave);
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("frequency")) {
            frequency = new Double((String) value).doubleValue();
        }

        if (paramname.equals("intensity")) {
            intensity = new Double((String) value).doubleValue();
        }

        if (paramname.equals("phase")) {
            phase = new Double((String) value).doubleValue();
        }

        if (paramname.equals("phaseRet")) {
            phaseRet = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("samples")) {
            samples = new Integer((String) value).intValue();
        }

        if (paramname.equals("samplingRate")) {
            samplingRate = new Double((String) value).doubleValue();
        }

        if (paramname.equals("time")) {
            timeStamp = new Double((String) value).doubleValue();
        }

        if (paramname.equals("type")) {
            type = (String) value;
        }
    }


    /**
     * @returns the list of allowed input types for this units.
     */
//    public String inputTypes() {
//        return "";
//    }
//
//    /**
//     * @returns the list of output types that this unit can produce.
//     */
//    public String outputTypes() {
//        return "SampleSet";
//    }

    public String[] getInputTypes() {
        return new String[]{};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does.
     */
    public String getPopUpDescription() {
        return "Generates a waveform with a choice of shapes and other parameters";
    }

    /**
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Wave.html";
    }


    /*
     * Returns a double[] containing a sinusoidal, saw tooth,
     * triangular, impulsive, or square wave
     * with the parameters given by the global variables frequency,
     * intensity (for the amplitude), phase (for the initial
     * phase), samples, and samplingRate.
     */

    private double[] createWaveform() {
        double[] wave = new double[samples];
        double f = (frequency / samplingRate); // freq normalized to cycles per sample
        double twopi = 2.0 * Math.PI;
        double radianphase = twopi * phase;
        double radianstep = twopi * f; // step in radians per sample

        int i;
        double saw;
        double x;
        double amp;
        double arg;

        /*
         * Impulse Wave: a sequence of impulses (data of width one
         * sample) occuring with the given frequency, at a position
         * within each period given by phase, and with an amplitude given
         * by intensity. Each impulse occurs at a time rounded to the
         * nearest sampling time. To get one impulse
         * in each data set, set frequency = 0 and then
         * phase determines where the impulse falls.
         */
        if (type.equals("Impulse")) {
            int pos;
            if (frequency == 0) {
                pos = (int) Math.round(phase * samples);
                wave[pos] = intensity;
                phasePos = phase;
            } else {
                double period = 1.0 / f;
                x = period * phase; // ideal location of impulse (not rounded to int)
                while (x <= samples - 1) {
                    pos = (int) Math.round(x);
                    wave[pos] = intensity;
                    x += period;
                }
                phasePos = (x - samples + 1) * f; // beginning phase of first impulse of next data set
            }
        } else if (type.equals("Sinusoid Wave")) {
            x = radianphase; // radian phase of wave at sampling points
            for (i = 0; i < samples; ++i) {
                wave[i] = intensity * Math.sin(x);
                x += radianstep;
            }
            phasePos = (x / twopi) % (1.0); // beginning phase of first sample of next data set
        } else if (type.equals("Saw Tooth")) {
            x = phase; // normalized phase (cycles) of wave at sampling points
            arg = x + 0.5; // needed to get wave with phase = 0 to
            // look like sawtooth version of sine wave
            amp = 2.0 * intensity; // needed in loop
            for (i = 0; i < samples; ++i) {
                saw = (arg) % (1.0); // normalized phase within a single waveform
                wave[i] = amp * (saw - 0.5);
                arg += f;
            }
            phasePos = (arg - 0.5) % (1.0); // beginning phase of first sample of next data set
        } else if (type.equals("Triangular Wave")) {
            x = phase; // normalized phase (cycles) of wave at sampling points
            arg = x + 0.75; // needed to get wave with phase = 0 to
            //look like triangular version of sine wave
            amp = 4.0 * intensity; // needed in loop
            for (i = 0; i < samples; ++i) {
                saw = (arg) % (1.0); // normalized phase within a single waveform
                wave[i] = amp * (Math.abs(saw - 0.5) - 0.25);
                arg += f;
            }
            phasePos = (arg - 0.75) % (1.0); // beginning phase of first sample of next data set
        } else if (type.equals("Square Wave")) {
            x = phase; // normalized phase (cycles) of wave at sampling points
            amp = 2.0 * intensity; // needed in loop
            for (i = 0; i < samples; ++i) {
                saw = (x) % (1.0); // normalized phase within a single waveform
                wave[i] = amp * (1.5 - Math.round(saw + 1.0));
                x += f;
            }
            phasePos = (x) % (1.0); // beginning phase of first sample of next data set
        }

        return wave;
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        Task task = getTask();
        frequency = new Double((String) task.getParameter("frequency")).doubleValue();
        intensity = new Double((String) task.getParameter("intensity")).doubleValue();
        phase = new Double((String) task.getParameter("phase")).doubleValue();
        phaseRet = new Boolean((String) task.getParameter("phaseRet")).booleanValue();
        samples = new Integer((String) task.getParameter("samples")).intValue();
        samplingRate = new Double((String) task.getParameter("samplingRate")).doubleValue();
        timeStamp = new Double((String) task.getParameter("time")).doubleValue();
        type = (String) task.getParameter("type");
    }
}














