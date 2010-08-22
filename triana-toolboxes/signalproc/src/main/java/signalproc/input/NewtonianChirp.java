package signalproc.input;


import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;


/**
 * Generates a Newtonian chirp
 *
 * @author David Churches
 * @version $Revision: 2921 $
 */


public class NewtonianChirp extends Unit {

    // parameter data type definitions
    private double mass1;
    private double mass2;
    private double initialPhase;
    private double fCutoff;
    private double amplitudeScaling;
    private double fLower;
    private double totalMass;
    private double fHigh;
    private double vlso;
    private double flso;
    private double eta;
    private double theta;
    private double t;
    private double tc;
    private double phase;
    private double freq;
    private double tmax;
    private double fold;
    private double dt;
    private double x;
    private double phi0;
    private double v;
    private double amp;
    private double ndx;
    private double length;
    private double samplingRate;
    private int count;
    private int numberOfSamples;
    private int startPad;
    private int endPad;
    private int i;
    private SampleSet chirp;
    private boolean lengthChosen;
    private boolean roundUp;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        totalMass = (mass1 + mass2);
        eta = (mass1 * mass2) / (totalMass * totalMass);
        totalMass *= 4.92549095e-6;
        vlso = Math.sqrt(1.0 / 6.0);
        flso = Math.pow(vlso, 3.0) / (Math.PI * totalMass);
        tc = 5.0 / (256.0 * eta * Math.pow(totalMass, 5.0 / 3.0) * Math.pow((Math.PI * fLower), 8.0 / 3.0));
        dt = 1.0 / samplingRate;


        System.out.println("tc= " + tc);
        System.out.println("samplingRate= " + samplingRate);
        x = tc * samplingRate + startPad + endPad;

        if (roundUp) {
            ndx = Math.ceil((Math.log(x) / Math.log(2.0)));
            length = Math.pow(2.0, (double) ndx);
        } else {
            length = Math.ceil(x);
        }
        if (!lengthChosen) {
            System.out.println("length= " + length);
        } else {
            System.out.println("length= " + numberOfSamples);
        }

        if (!lengthChosen) {
            setParameter("numberOfSamples", String.valueOf((int) length));
        }

        chirp = new SampleSet((int) samplingRate, numberOfSamples);

        if (fCutoff > 0) {
            if (fCutoff < flso) {
                fHigh = fCutoff;
                System.out.println("fCutoff < flso, so taking fHigh=fCutoff");
            } else {
                fHigh = flso;
                System.out.println("flso < fCutoff , so taking fHigh=fCutoff");
            }
        } else {
            fHigh = flso;
            System.out.println("not using fCutoff , so taking fHigh=flso");
        }
        System.out.println("fHigh= " + fHigh);

        t = 0;
        theta = eta * (tc - t) / (5.0 * totalMass);
        phase = -(2.0 / eta) * Math.pow(theta, 5.0 / 8.0);
        freq = Math.pow(theta, -3.0 / 8.0) / (8 * Math.PI * totalMass);
        phi0 = -phase + initialPhase;


        count = 0;
        tmax = tc - dt;
        fold = 0.0;
        i = 0;

        while (i < startPad) {
            chirp.data[i++] = 0.0;
        }

        while (freq < fHigh && t < tmax && freq > fold) {
            fold = freq;
            v = Math.pow(freq * Math.PI * totalMass, 1.0 / 3.0);
            amp = v * v;
            chirp.data[i++] = amplitudeScaling * amp * Math.cos(phase + phi0);
            ++count;
            t = count * dt;
            theta = eta * (tc - t) / (5.0 * totalMass);
            phase = -(2.0 / eta) * Math.pow(theta, 5.0 / 8.0);
            freq = Math.pow(theta, -3.0 / 8.0) / (8 * Math.PI * totalMass);
        }

        int end = i + endPad;
        while (i < end) {
            chirp.data[i++] = 0.0;
        }
        if (lengthChosen) {
            while (i < numberOfSamples) {
                chirp.data[i++] = 0.0;
            }
        }

        output(chirp);
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
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
        setParameterUpdatePolicy(Task.IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Generates a Newtonian chirp");
        setHelpFileLocation("NewtonianChirp.html");

        // Define initial value and type of parameters
        defineParameter("mass1", "10.0", USER_ACCESSIBLE);
        defineParameter("mass2", "10.0", USER_ACCESSIBLE);
        defineParameter("initialPhase", "0.0", USER_ACCESSIBLE);
        defineParameter("fCutoff", "0.0", USER_ACCESSIBLE);
        defineParameter("amplitudeScaling", "1.0", USER_ACCESSIBLE);
        defineParameter("fLower", "60.0", USER_ACCESSIBLE);
        defineParameter("samplingRate", "16384.0", USER_ACCESSIBLE);
        defineParameter("lengthChosen", "false", USER_ACCESSIBLE);
        defineParameter("roundUp", "true", USER_ACCESSIBLE);
        defineParameter("numberOfSamples", "0", USER_ACCESSIBLE);
        defineParameter("startPad", "0", USER_ACCESSIBLE);
        defineParameter("endPad", "0", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "mass1 $title mass1 Scroller 0.1 100 10 false\n";
        guilines += "mass2 $title mass2 Scroller 0.1 100 10 false\n";
        guilines += "initialPhase $title initialPhase TextField 0.0\n";
        guilines += "fCutoff $title fCutoff TextField 0.0\n";
        guilines += "amplitude Scaling $title amplitudeScaling TextField 0.0\n";
        guilines += "fLower $title fLower TextField 60.0\n";
        guilines += "samplingRate $title samplingRate TextField 16384.0\n";
        guilines
                += "fix number of points (if unchecked unit will calculate length internally) $title lengthChosen Checkbox false\n";
        guilines += "number of Samples (only edit if above box is checked) $title numberOfSamples TextField 16384.0\n";
        guilines
                += "round up length to nearest power of 2 (also disabled if number of points are fixed) $title roundUp Checkbox true\n";
        guilines += "number of zeros at start of waveform $title startPad TextField 0\n";
        guilines += "number of zeros at end of waveform $title endPad TextField 0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        mass1 = new Double((String) getParameter("mass1")).doubleValue();
        mass2 = new Double((String) getParameter("mass2")).doubleValue();
        initialPhase = new Double((String) getParameter("initialPhase")).doubleValue();
        fCutoff = new Double((String) getParameter("fCutoff")).doubleValue();
        amplitudeScaling = new Double((String) getParameter("amplitudeScaling")).doubleValue();
        fLower = new Double((String) getParameter("fLower")).doubleValue();
        samplingRate = new Double((String) getParameter("samplingRate")).doubleValue();
        lengthChosen = new Boolean((String) getParameter("lengthChosen")).booleanValue();
        roundUp = new Boolean((String) getParameter("roundUp")).booleanValue();
        numberOfSamples = new Integer((String) getParameter("numberOfSamples")).intValue();
        startPad = new Integer((String) getParameter("startPad")).intValue();
        endPad = new Integer((String) getParameter("endPad")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up NewtonianChirp (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("mass1")) {
            mass1 = new Double((String) value).doubleValue();
        }

        if (paramname.equals("mass2")) {
            mass2 = new Double((String) value).doubleValue();
        }

        if (paramname.equals("initialPhase")) {
            initialPhase = new Double((String) value).doubleValue();
        }

        if (paramname.equals("fCutoff")) {
            fCutoff = new Double((String) value).doubleValue();
        }

        if (paramname.equals("amplitudeScaling")) {
            amplitudeScaling = new Double((String) value).doubleValue();
        }

        if (paramname.equals("fLower")) {
            fLower = new Double((String) value).doubleValue();
        }

        if (paramname.equals("samplingRate")) {
            samplingRate = new Double((String) value).doubleValue();
        }

        if (paramname.equals("lengthChosen")) {
            lengthChosen = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("roundUp")) {
            roundUp = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("numberOfSamples")) {
            numberOfSamples = new Integer((String) value).intValue();
        }

        if (paramname.equals("startPad")) {
            startPad = new Integer((String) value).intValue();
        }

        if (paramname.equals("endPad")) {
            endPad = new Integer((String) value).intValue();
        }
    }


    /**
     * @return an array of the input types for NewtonianChirp
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for NewtonianChirp
     */
    public String[] getOutputTypes() {
        return new String[]{"SampleSet"};
    }

}



