package signalproc.injection;

import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.util.Str;

/**
 * A Chirp unit to add coalescing binary waveforms to an input data set in the time domain. The signal accelerates in
 * frequency according to the lowest-order radiation-reaction approximation (usually called the Newtonian waveform). The
 * orbit is assumed quasi-circular with adiabatic removal of energy.
 *
 * @author B F Schutz
 * @version 2.4 12 Dec 2001
 */
public class Chirp extends Unit {

    double ampl = 1;
    double chirpMass = 1;
    double f0 = 1;
    double phase = 0;
    boolean relative = true;
    double t0 = 1;

    double f, tstart, chirpLife, arg;

    double exp1 = -8. / 3.;
    double exp2 = -3. / 8;
    double alpha = 1.5454352E-6;

    boolean continuation = false;


    /**
     * ********************************************* ** USER CODE of Chirp goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        int jLim;

        if (!continuation) {
            f = f0;
            tstart = t0;
            arg = phase;
        }
        System.out.println("inside Chirp.process, continuation= " + continuation);
        double beta = alpha * Math.pow(chirpMass, 5. / 3.);
        chirpLife = Math.pow(f0, exp1) / beta;

        SampleSet input = (SampleSet) getInputAtNode(0);

        double rate = input.getSamplingRate();
        double interval = 1.0 / rate;
        double radians = interval * 2 * Math.PI;
        double acq = input.getAcquisitionTime();
        double duration = interval * input.size();

        if (!relative) {
            tstart = t0 - acq;
        }
        if ((tstart < 0) || (tstart > duration)) {
            output(input);
            return;
        }

        double[] dataIn = input.getData();
        double t = 0;
        if (duration < tstart + chirpLife) {
            System.out.println("input data shorter than chirp");
            continuation = true;
            jLim = input.size();
        } else {
            jLim = (int) Math.floor((tstart + chirpLife) * rate);
        }
        System.out.println("jLim= " + jLim);

        System.out.println(getToolName() + ": rate = " + String.valueOf(rate) + ", intial f = " + String.valueOf(f)
                + ", initial arg = " + String.valueOf(arg) + ", interval = " + String.valueOf(interval) + ", tstart = "
                + String.valueOf(tstart) + ", chirpLife = " + String.valueOf(chirpLife));

        for (int j = (int) Math.floor(tstart * rate); j < jLim; j++) {
            dataIn[j] += ampl * Math.pow(f / f0, 2. / 3.) * Math.sin(arg);
            t += interval;
            f = Math.pow(Math.pow(f0, exp1) - t * beta, exp2);
            arg += radians * f;
        }

        if (t < chirpLife) {
            continuation = true;
            System.out.println("t < chirpLife");
        }


        output(input);

    }


    /**
     * Initialses information specific to Chirp.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Initial amplitude of chirp $title ampl Scroller 0 100 1\n";
        guilines += "chirp mass in solar masses $title chirpMass Scroller 0 100 1\n";
        guilines += "initial frequency in Hz $title f0 Scroller 0 1000 10\n";
        guilines += "initial phase of chirp (fraction of 2*Pi) $title phase Scroller 0 1 0\n";
        guilines += "Is the time (below) relative to data set start?\n (Un-check if absolute time) $title relative Checkbox true\n";
        guilines += "start time of chirp in seconds $title t0 Scroller 0 100 1\n";
        setGUIBuilderV2Info(guilines);

//        setResizableInputs(false);
//        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Initial amplitude of chirp $title ampl Scroller 0 100 1");
//        addGUILine("chirp mass in solar masses $title chirpMass Scroller 0 100 1");
//        addGUILine("initial frequency in Hz $title f0 Scroller 0 1000 10");
//        addGUILine("initial phase of chirp (fraction of 2*Pi) $title phase Scroller 0 1 0");
//        addGUILine(
//                "Is the time (below) relative to data set start?\n (Un-check if absolute time) $title relative Checkbox true");
//        addGUILine("start time of chirp in seconds $title t0 Scroller 0 100 1");
//    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        continuation = false;
        super.reset();
    }

    /**
     * Called when the stop button is pressed within the MainTriana Window
     */
    public void stopping() {
        super.stopping();
    }

    /**
     * Called when the start button is pressed within the MainTriana Window
     */
//    public void starting() {
//        super.starting();
//    }
//
//    /**
//     * Saves Chirp's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("ampl", ampl);
//        saveParameter("chirpMass", chirpMass);
//        saveParameter("f0", f0);
//        saveParameter("phase", phase);
//        saveParameter("relative", relative);
//        saveParameter("t0", t0);
//    }

    /**
     * Used to set each of Chirp's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("ampl")) {
            ampl = Str.strToDouble((String) value);
        }
        if (name.equals("chirpMass")) {
            chirpMass = Str.strToDouble((String) value);
        }
        if (name.equals("f0")) {
            f0 = Str.strToDouble((String) value);
        }
        if (name.equals("phase")) {
            phase = Str.strToDouble((String) value) * 2 * Math.PI;
        }
        if (name.equals("relative")) {
            relative = Str.strToBoolean((String) value);
        }
        if (name.equals("t0")) {
            t0 = Str.strToDouble((String) value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Chirp, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "SampleSet";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Chirp, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "SampleSet";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Adds Newtonian chirp to input";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Chirp.html";
    }
}




