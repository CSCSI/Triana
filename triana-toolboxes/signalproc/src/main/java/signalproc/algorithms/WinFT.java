package signalproc.algorithms;

import java.util.ArrayList;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.TimeFrequency;
import triana.types.TrianaType;
import triana.types.VectorType;
import triana.types.util.Str;


/**
 * A WinFT unit to perform a windowed Fourier Transform
 *
 * @author B F Schutz
 * @version 1.0 05 Jan 2001
 */
public class WinFT extends Unit {

    String windowType = "Rectangle";
    int windowSize = 256;
    int windowStep = 4;
    double[] oldWindowInput = null;
    String oldWindowType = "";
    int oldWindowSize = 0;
    double[] oldWindowCreated = null;

    /**
     * ********************************************* ** USER CODE of WinFT goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        double[] window = null;
        boolean rectWindow = (windowType.equals("Rectangle"));

        SampleSet input = (SampleSet) getInputAtNode(0);
        double[] data = input.getData();

        if (getTask().getDataInputNodeCount() > 1) {
            VectorType windowIn = (VectorType) getInputAtNode(1);
            if (windowIn == TrianaType.NOT_READY) {
                if (oldWindowInput == null) {
                    ErrorDialog.show(null,
                            "WinFT: No input has yet been received at the second input node, so no window can be constructed. Using a rectangular window of length about 10% of the input data set length.");
                    windowSize = data.length / 10 + 1;
                    rectWindow = true;
                } else {
                    window = oldWindowInput;
                    windowSize = window.length;
                    rectWindow = false;
                }
            } else {
                window = windowIn.getData();
                normalize(window);
                windowSize = window.length;
                rectWindow = false;
                oldWindowInput = window;
            }
        } else if (!rectWindow) {
            if (!windowType.equals(oldWindowType) || (windowSize != oldWindowSize)) {
                window = makeWindow(windowType, windowSize);
                oldWindowCreated = window;
                oldWindowSize = windowSize;
                oldWindowType = windowType;
            } else {
                window = oldWindowCreated;
                windowSize = oldWindowSize;
            }
        }

        int transformSize = windowSize;
        int dataLength = data.length;
        int excessLength = dataLength - windowSize;
        if (excessLength < 0) {
            excessLength = 0;
            transformSize = dataLength;
        }

        int nSteps = excessLength / windowStep + 1;
        int step, j, k, windowStart;
        double[][] tFR = new double[nSteps][transformSize];
        double[][] tFI = new double[nSteps][transformSize];
        double[] tempR, tempI;
        ArrayList fourier;

        for (step = 0, windowStart = 0; step < nSteps; windowStart += windowStep, step++) {
            tempR = new double[transformSize];
            if (rectWindow) {
                System.arraycopy(data, windowStart, tempR, 0, transformSize);
            } else {
                for (j = 0, k = windowStart; j < transformSize; j++, k++) {
                    tempR[j] = data[k] * window[j];
                }
            }
            fourier = FFTC.FFT_C(tempR, null, true, false);
            tFR[step] = (double[]) fourier.get(0);
            tFI[step] = (double[]) fourier.get(1);
        }

        double sf = ((SampleSet) input).getSamplingRate();
        double acq = ((SampleSet) input).getAcquisitionTime();

        output(new TimeFrequency(tFR, tFI, true, false, transformSize, sf / transformSize, sf / 2.0, windowStep / sf,
                acq));
    }

    private double[] makeWindow(String type, int length) {
        double[] win = new double[length];
        double scale, x;
        int j, reflect;
        boolean even = (length % 2 == 0);
        double shift = (even) ? 0.5 : 0.0;
        int half = length / 2;

        if (type.equals("Hamming")) {
            scale = 2.0 * Math.PI / length;
            for (j = 0, x = shift - half, reflect = length - 1; j < half; j++, x++, reflect--) {
                win[j] = 0.54 + 0.46 * Math.cos(scale * x);
                win[reflect] = win[j];
            }
            if (!even) {
                win[half + 1] = 1.0;
            }
        } else if (type.equals("Gaussian")) {
            scale = 18.0 / length / length;
            for (j = 0, x = shift - half, reflect = length - 1; j < half; j++, x++, reflect--) {
                win[j] = Math.exp(-scale * x * x);
                win[reflect] = win[j];
            }
            if (!even) {
                win[half + 1] = 1.0;
            }
        } else if (type.equals("Hanning")) {
            scale = Math.PI / length;
            double y;
            for (j = 0, x = shift - half, reflect = length - 1; j < half; j++, x++, reflect--) {
                y = Math.cos(scale * x);
                win[j] = y * y;
                win[reflect] = win[j];
            }
            if (!even) {
                win[half + 1] = 1.0;
            }
        } else if (type.equals("Blackman")) {
            scale = 2.0 * Math.PI / length;
            double scale2 = 2 * scale;
            for (j = 0, x = shift - half, reflect = length - 1; j < half; j++, x++, reflect--) {
                win[j] = 0.42 + 0.5 * Math.cos(scale * x) + 0.08 * Math.cos(scale2 * x);
                win[reflect] = win[j];
            }
            if (!even) {
                win[half + 1] = 1.0;
            }
        } else if (type.equals("Bartlett")) {
            scale = 2.0 / length;
            for (j = 0, x = shift - half, reflect = length - 1; j < half; j++, x++, reflect--) {
                win[j] = 1 - Math.abs(scale * x);
                win[reflect] = win[j];
            }
            if (!even) {
                win[half + 1] = 1.0;
            }
        } else if (type.equals("Welch")) {
            scale = 4.0 / length / length;
            for (j = 0, x = shift - half, reflect = length - 1; j < half; j++, x++, reflect--) {
                win[j] = 1 - scale * x * x;
                win[reflect] = win[j];
            }
            if (!even) {
                win[half + 1] = 1.0;
            }
        }

        normalize(win);
        return win;
    }

    private void normalize(double[] f) {
        double l2 = 0;
        int j;
        for (j = 0; j < f.length; j++) {
            l2 += f[j] * f[j];
        }
        double norm = Math.sqrt(l2);
        for (j = 0; j < f.length; j++) {
            f[j] /= norm;
        }
    }

    /**
     * Initialses information specific to WinFT.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setResizableInputs(true);
//        setResizableOutputs(true);
//        if (getInputNodes() > 1) {
//            setOptional(1);
//        }  // read in a window but only once

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Choose time-domain window $title windowType Choice Rectangle Bartlett Blackman Gaussian Hamming Hanning Welch\n";
        guilines += "Width of window (number of points) $title windowSize IntScroller 0 4096 256\n";
        guilines += "Step between successive windows (number of points) $title windowStep IntScroller 0 256 4\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine(
//                "Choose time-domain window $title windowType Choice Rectangle Bartlett Blackman Gaussian Hamming Hanning Welch");
//        addGUILine("Width of window (number of points) $title windowSize IntScroller 0 4096 256");
//        addGUILine("Step between successive windows (number of points) $title windowStep IntScroller 0 256 4");
//    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
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

    /**
     * Saves WinFT's parameters.
     */
//    public void saveParameters() {
//        saveParameter("windowType", windowType);
//        saveParameter("windowSize", windowSize);
//        saveParameter("windowStep", windowStep);
//    }


    /**
     * Used to set each of WinFT's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("windowType")) {
            windowType = (String) value;
        }
        if (name.equals("windowSize")) {
            windowSize = Str.strToInt((String) value);
        }
        if (name.equals("windowStep")) {
            windowStep = Str.strToInt((String) value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to WinFT, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "SampleSet";
//    }
//
//    /**
//     * @return a string containing the names of the types output from WinFT, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "TimeFrequency";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.TimeFrequency"};
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Performs a windowed Fourier transform";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "WinFT.html";
    }
}




