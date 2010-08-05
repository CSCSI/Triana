package triana.types.util;

import java.util.Hashtable;

import org.trianacode.taskgraph.TaskException;
import triana.types.GraphType;
import triana.types.Spectral;
import triana.types.TimeFrequency;
import triana.types.VectorType;


/**
 * Class to provide utility methods for applying windowing functions in the context of signal analysis. Used by units in
 * more than one toolbox. Static methods allow these functions to be applied to time-domain or frequency-domain data.
 * The frequency-domain methods allow one to apply only the right-half or left-half window so that windowing can be
 * correctly applied to spectra stored according to the Triana storage model. If rounding of narrow bandwidths is
 * desired this will be done automatically if the input spectrum is narrow-band, but not if it is padded with zeros.
 * There are methods also for providing a list of available window functions to units to use in drop-downs in parameter
 * windows. <\p><p> So far there are 6 defined window functions: Bartlett, Blackman, Gaussian, Hamming, Hanning, Welch.
 * Further functions can be added by writing three methods for each window.
 *
 * @author Bernard Schutz
 * @version $Revision: 4048 $
 */
public class SigAnalWindows {

    /*
     * Hashtable that holds previously computed window functions. Methods creating a
     * window should store it here. Methods needing a window should look here first before
     * asking for one to be created.
     *
     * The windows are stored as double[] arrays with keys that are Strings that obey the
     * following convention:
     *
     * WindowName(ParameterValues)(NumberOfPointsSpanningWindow)
     *
     * Thus, a Hamming window with 9 points would be called
     * Hamming()(9). All windows should be symmetric.
     * Parameter values are given in the form, eg, alpha =0.1. If the window has
     * infinite length in principle, then the points-spanning argument is left empty.
     */
    static Hashtable windows = new Hashtable(10);

    /**
     * Array of the possible window function names
     */
    private static String[] windowListStrArray = new String[]{"Rectangle", "Bartlett", "Blackman", "Gaussian",
            "Hamming", "Hann(Hanning)", "Welch", "BlackmanHarris92",
            "Nuttall3", "Nuttall3a", "Nuttall3b", "Nuttall4",
            "Kaiser3", "Kaiser4", "Kaiser5", "Kaiser6",
            "Kaiser7", "SFT3F", "SFT4F", "SFT5F",
            "SFT3M", "SFT4M", "SFT5M", "FTNI",
            "FTHP", "FTSRS", "HFT70", "HFT95"};
    /**
     * A space separated string of the possible window function names, statically initialised from the array version.
     */
    private static String windowListStr = "";

    static {
        for (int i = 0; i < windowListStrArray.length; i++) {
            windowListStr = windowListStr + windowListStrArray[i] + " ";
        }
    }

    /*
     * Some useful constants.
     */
    static double pi = Math.PI;
    static double twopi = pi * 2;
    static double pihalf = pi / 2;

    /**
     * Method to apply a window function to an input data set on the assumption that the data are stored as a simple
     * sequential set. This is true for time-domain data but not for spectral data, whose storage model is more
     * complicated. For spectral data use the method <i>applyWindowToSpectral<\i>. Input data must be VectorType or
     * TimeFrequency. If another GraphType is input, then it will be output unchanged. (Note: no error flag is
     * returned.)
     *
     * @param input    Must be either of type VectorType or TimeFrequency
     * @param function The keyword naming the window function to be applied
     * @param allocMem <i>True<\i> if output is to be a copy of input, <i>false</i> if input is overwritten
     * @return The windowed data, of the same type as the input
     */
    public static GraphType applyWindowFunction(GraphType input,
                                                String function, boolean allocMem) {

        if (function.equals("(none)")) {
            return input;
        }

        GraphType result = null;
        if (allocMem) {
            result = (GraphType) input.copyMe();
        } else {
            result = input;
        }

        double[] real = null;
        double[] imag = null;
        Spectral s;
        VectorType v;
        TimeFrequency tf;
        boolean complex;

        if (result instanceof VectorType) {
            v = (VectorType) result;
            real = v.getDataReal();
            if (v instanceof Spectral) {
                s = (Spectral) v;
                applyWindowToSpectral(real, function, s.getLowerFrequencyBound(0), s.isTwoSided(), s.isNarrow(0));
            } else {
                doFullWindow(function, real, 0, real.length - 1);
            }

            complex = v.isDependentComplex(0);
            if (complex) {
                imag = v.getDataImag();
                if (v instanceof Spectral) {
                    s = (Spectral) result;
                    applyWindowToSpectral(imag, function, s.getLowerFrequencyBound(0), s.isTwoSided(), s.isNarrow(0));
                } else {
                    doFullWindow(function, imag, 0, imag.length - 1);
                }
            }
        } else if (result instanceof TimeFrequency) {
            s = (Spectral) result;
            tf = (TimeFrequency) result;
            int[] lengths = tf.size();
            int nSpectra = lengths[0];
            double[][] matrix = tf.getDataReal();
            for (int j = 0; j < nSpectra; j++) {
                applyWindowToSpectral(matrix[j], function, s.getLowerFrequencyBound(0), s.isTwoSided(), s.isNarrow(0));
            }
        }

        return result;
    }


    /**
     * Method to apply a window function to an input double[] array that is stored using the Triana model for Spectral
     * data sets. For non-spectral data use the method <i>applyWindowFunction<\i>. The method does not return any
     * values; it simply modifies the input data.
     *
     * @param input    Should be stored in the way that Spectra are stored in Triana
     * @param function The keyword naming the window function to be applied
     * @param low      The lower frequency bound (only tested to see if it is non-zero if narrow band)
     * @param twoSided <i>True<\i> if the data are stored in a two-sided way
     * @param narrow   <i>True<\i> if the data are narrow-band
     */
    public static void applyWindowToSpectral(double[] input, String function, double low, boolean twoSided,
                                             boolean narrow) {

        int len, idx;
        boolean evenLen;
        double[] temp;
        double overlap, secondOverlap;

        len = input.length;
        evenLen = ((len % 2) == 0);

        if (twoSided) {
            if (narrow) {
                if (low == 0) {
                    idx = (len - 1) / 2;
                    overlap = input[0];
                    doHalfWindow(function, input, 0, idx, true);
                    temp = new double[idx + 1];
                    System.arraycopy(input, idx + 1, temp, 0, idx);
                    temp[idx] = overlap;
                    doHalfWindow(function, temp, 0, idx, false);
                    System.arraycopy(temp, 0, input, idx + 1, idx);
                } else {
                    if (evenLen) {
                        idx = len / 2 - 1;
                        doFullWindow(function, input, 0, idx);
                        temp = new double[idx + 1];
                        System.arraycopy(input, idx + 1, temp, 0, idx + 1);
                        doFullWindow(function, temp, 0, idx);
                        System.arraycopy(temp, 0, input, idx + 1, idx + 1);
                    } else {
                        idx = (len - 1) / 2;
                        overlap = input[idx];
                        doFullWindow(function, input, 0, idx);
                        temp = new double[idx + 1];
                        System.arraycopy(input, idx + 1, temp, 1, idx);
                        temp[0] = overlap;
                        doFullWindow(function, temp, 0, idx);
                        System.arraycopy(temp, 1, input, idx + 1, idx);
                    }
                }
            } else {
                if (evenLen) {
                    idx = len / 2;
                    overlap = input[0];
                    secondOverlap = input[idx];
                    doHalfWindow(function, input, 0, idx, true);
                    temp = new double[idx + 1];
                    System.arraycopy(input, idx + 1, temp, 1, idx - 1);
                    temp[0] = overlap;
                    temp[idx] = secondOverlap;
                    doHalfWindow(function, temp, 0, idx, false);
                    System.arraycopy(temp, 1, input, idx + 1, idx - 1);
                } else {
                    idx = (len - 1) / 2;
                    overlap = input[0];
                    doHalfWindow(function, input, 0, idx, true);
                    temp = new double[idx + 1];
                    System.arraycopy(input, idx + 1, temp, 1, idx);
                    temp[0] = overlap;
                    doHalfWindow(function, temp, 0, idx, false);
                    System.arraycopy(temp, 1, input, idx + 1, idx);
                }
            }
        } else {
            if (narrow) {
                if (low == 0) {
                    doHalfWindow(function, input, 0, len - 1, true);
                } else {
                    doFullWindow(function, input, 0, len - 1);
                }
            } else {
                doHalfWindow(function, input, 0, len - 1, true);
            }
        }
    }

    /**
     * Returns the list of windows available. Update this if you change the window functions. The output of this is used
     * by <i>setGUIInformation()</i> below and can be called by other units that use the static methods of this unit and
     * want to offer the up-to-date list of windows in their own parameter window.
     *
     * @return String list of available windows
     */
    public static String listOfWindows() {
        return windowListStr;
    }

    /**
     * @return a copy of the string array of all possible window function names.
     */
    public static String[] listOfWindowsAsArray() {
        String[] result = new String[windowListStrArray.length];
        System.arraycopy(windowListStrArray, 0, result, 0, windowListStrArray.length);
        return result;
    }

    /**
     * Selects the correct window and applies its full width to the input array between the two given indices. Data
     * inside the range of the window (between the given indices) are multiplied by the appropriate window function.
     * Data outside the window are not affected.
     * <p/>
     * The method can be extended to any window function by providing a method with the name of the window and one int
     * argument, which returns the window values in a double[] array of length equal to the argument.
     *
     * @param function The name of the window to be applied
     * @param input    The data set to be multiplied by the window
     * @param from     The index in the input data set where the window starts
     * @param to       The index in the input data set where the window terminates
     */
    public static void doFullWindow(String function, double[] input, int from, int to) {

        double[] window = null;
        int span = to - from + 1;
        String spanString = String.valueOf(span);
        String key = function + "()(" + spanString + ")";
        if (windows.size() > 0) {
            window = (double[]) windows.get(key);
        }
        if (window == null) {
            if (function.equals("Bartlett")) {
                window = Bartlett(span);
            } else if (function.equals("Hann(Hanning)")) {
                window = Hann(span);
            } else if (function.equals("Welch")) {
                window = Welch(span);
            } else if (function.equals("Blackman")) {
                window = Blackman(span);
            } else if (function.equals("Gaussian")) {
                window = Gaussian(span);
            } else if (function.equals("Hamming")) {
                window = Hamming(span);
            } else if (function.equals("Rectangle")) {
                window = Rectangle(span);
            } else if (function.equals("BlackmanHarris92")) {
                window = BlackmanHarris92(span);
            } else if (function.equals("Nuttall3")) {
                window = Nuttall3(span);
            } else if (function.equals("Nuttall3a")) {
                window = Nuttall3a(span);
            } else if (function.equals("Nuttall3b")) {
                window = Nuttall3b(span);
            } else if (function.equals("Nuttall4")) {
                window = Nuttall4(span);
            } else if (function.equals("Kaiser3")) {
                window = Kaiser3(span);
            } else if (function.equals("Kaiser4")) {
                window = Kaiser4(span);
            } else if (function.equals("Kaiser5")) {
                window = Kaiser5(span);
            } else if (function.equals("Kaiser6")) {
                window = Kaiser6(span);
            } else if (function.equals("Kaiser7")) {
                window = Kaiser7(span);
            } else if (function.equals("SFT3F")) {
                window = SFT3F(span);
            } else if (function.equals("SFT4F")) {
                window = SFT4F(span);
            } else if (function.equals("SFT5F")) {
                window = SFT5F(span);
            } else if (function.equals("SFT3M")) {
                window = SFT3M(span);
            } else if (function.equals("SFT4M")) {
                window = SFT4M(span);
            } else if (function.equals("SFT5M")) {
                window = SFT5M(span);
            } else if (function.equals("FTNI")) {
                window = FTNI(span);
            } else if (function.equals("FTHP")) {
                window = FTHP(span);
            } else if (function.equals("FTSRS")) {
                window = FTSRS(span);
            } else if (function.equals("HFT70")) {
                window = HFT70(span);
            } else if (function.equals("HFT95")) {
                window = HFT95(span);
            }

            windows.put(key, window);
        }

        for (int i = from; i <= to; ++i) {
            input[i] *= window[i];
        }

    }

    /**
     * Selects the given window and applies half of its width to the input array between the two given indices. The
     * value of the boolean argument determines which half: if <i>true</i> it is the right half of the window and if
     * <i>false</i> the left half. Data inside the range of the window (between the given indices) are multiplied by the
     * appropriate window function. Data outside the window are not affected.
     * <p/>
     * This method allows correct treatment of spectra, where the negative and positive frequencies are not stored in a
     * way that is continuous across zero frequency, so the window needs to be split and applied to different regions of
     * the stored spectrum.
     * <p/>
     * The method can be extended to any window function by providing a method with the name of the window and one int
     * argument, which returns the window values in a double[] array of length equal to the argument.
     *
     * @param function The name of the window to be applied
     * @param input    The data set to be multiplied by the window
     * @param from     The index in the input data set where the window starts
     * @param to       The index in the input data set where the window terminates
     */
    public static void doHalfWindow(String function, double[] input, int from, int to, boolean right) {

        double[] window = null;
        int span = to - from + 1;
        int points = 2 * span - 1;
        String pointsString = String.valueOf(points);
        String key = function + "()(" + pointsString + ")(" + pointsString + ")";
        if (windows.size() > 0) {
            window = (double[]) windows.get(key);
        }
        if (window == null) {
            if (function.equals("Bartlett")) {
                window = Bartlett(points);
            } else if (function.equals("Hann(Hanning)")) {
                window = Hann(points);
            } else if (function.equals("Welch")) {
                window = Welch(points);
            } else if (function.equals("Blackman")) {
                window = Blackman(points);
            } else if (function.equals("Gaussian")) {
                window = Gaussian(points);
            } else if (function.equals("Hamming")) {
                window = Hamming(points);
            } else if (function.equals("Rectangle")) {
                window = Rectangle(points);
            } else if (function.equals("BlackmanHarris92")) {
                window = BlackmanHarris92(points);
            } else if (function.equals("Nuttall3")) {
                window = Nuttall3(points);
            } else if (function.equals("Nuttall3a")) {
                window = Nuttall3a(points);
            } else if (function.equals("Nuttall3b")) {
                window = Nuttall3b(points);
            } else if (function.equals("Nuttall4")) {
                window = Nuttall4(points);
            } else if (function.equals("Kaiser3")) {
                window = Kaiser3(points);
            } else if (function.equals("Kaiser4")) {
                window = Kaiser4(points);
            } else if (function.equals("Kaiser5")) {
                window = Kaiser5(points);
            } else if (function.equals("Kaiser6")) {
                window = Kaiser6(points);
            } else if (function.equals("Kaiser7")) {
                window = Kaiser7(points);
            } else if (function.equals("SFT3F")) {
                window = SFT3F(points);
            } else if (function.equals("SFT4F")) {
                window = SFT4F(points);
            } else if (function.equals("SFT5F")) {
                window = SFT5F(points);
            } else if (function.equals("SFT3M")) {
                window = SFT3M(points);
            } else if (function.equals("SFT4M")) {
                window = SFT4M(points);
            } else if (function.equals("SFT5M")) {
                window = SFT5M(points);
            } else if (function.equals("FTNI")) {
                window = FTNI(points);
            } else if (function.equals("FTHP")) {
                window = FTHP(points);
            } else if (function.equals("FTSRS")) {
                window = FTSRS(points);
            } else if (function.equals("HFT70")) {
                window = HFT70(points);
            } else if (function.equals("HFT95")) {
                window = HFT95(points);
            } else {
                (new TaskException("triana.util.SigAnalWindows.doHalfWindow() function: " + function + " not found"))
                        .printStackTrace();
            }
            windows.put(key, window);
        }

        int start = (right) ? span - 1 : 0;
        int i, j;
        for (i = from, j = start; i <= to; ++i, ++j) {
            input[i] *= window[j];
        }

    }

    /*
     * Insert here the specific window shapes desired. Each method takes two int
     * arguments: the number of points that must be computed in spanning
     * the whole domain of the window, and the number of points to be computed for the
     * returned window function. The method returns the window values in
     * a double[]. To add more windows, copy one of the methods below and
     * modify only two lines: the domain and the line in the loop that gives
     * the functional form of the window.
     */


    /**
     * Create a Hann window spanning the given number of points. This window is also known as a Hanning window.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Hann(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.5 * (1 + Math.cos(x));
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }


    /**
     * Create a Bartlett window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Bartlett(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = 2.0;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 1 - x;
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a Welch window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Welch(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = 2.0;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 1 - x * x;
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a Hamming window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Hamming(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.54 + 0.46 * Math.cos(x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a Gaussian window spanning the given number of points. Its domain is fixed by the place where it falls to
     * exp(-4.5) = 0.0111.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Gaussian(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = Math.sqrt(18);

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = Math.exp(-x * x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a Blackman window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Blackman(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.42 + 0.5 * Math.cos(x) + 0.08 * Math.cos(x + x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }


    /**
     * Create a Rectangle window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Rectangle(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */

        int i, istart;
        double[] window = new double[points];

        istart = points / 2;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart; i < points; ++i) {
            window[i] = 1;
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a Blackman-Harris 92dB window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] BlackmanHarris92(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.35875 + 0.48829 * Math.cos(x) + 0.14128 * Math.cos(x + x) + 0.01168 * Math.cos(3 * x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a Nuttall3 window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Nuttall3(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.375 + 0.5 * Math.cos(x) + 0.125 * Math.cos(x + x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a Nuttall3a window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Nuttall3a(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.40897 + 0.5 * Math.cos(x) + 0.09103 * Math.cos(x + x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a Nuttall3b window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Nuttall3b(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.4243801 + 0.4973406 * Math.cos(x) + 0.0782793 * Math.cos(x + x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a Nuttall4 window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Nuttall4(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.3125 + 0.46875 * Math.cos(x) + 0.1875 * Math.cos(x + x) + 0.03125 * Math.cos(3 * x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a Kaiser window spanning the given number of points and depending on the given parameter alpha.
     *
     * @param points The number of points that span the full size of the window
     * @param alpha  The standard parameter of the Kaiser family
     * @return The window values
     */
    public static double[] Kaiser(int points, double alpha) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = 2;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];
        double beta = pi * alpha;

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = Bessel0(beta * Math.sqrt(1.0 - x * x)) / Bessel0(beta);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    public static double Bessel0(double x) {
        double error = 1e-8;
        double bess = 1;
        double arg = x / 2;
        double term = 1;
        int index = 0;
        while (Math.abs(term) > error) {
            index++;
            term /= index;
            term *= arg;
            bess += term * term;
        }
        return bess;
    }

    /**
     * Create a Kaiser3 window spanning the given number of points (with parameter alpha = 3).
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Kaiser3(int points) {
        return Kaiser(points, 3.0);
    }

    /**
     * Create a Kaiser4 window spanning the given number of points (with parameter alpha = 4).
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Kaiser4(int points) {
        return Kaiser(points, 4.0);
    }

    /**
     * Create a Kaiser5 window spanning the given number of points (with parameter alpha = 5).
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Kaiser5(int points) {
        return Kaiser(points, 5.0);
    }

    /**
     * Create a Kaiser6 window spanning the given number of points (with parameter alpha = 6).
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Kaiser6(int points) {
        return Kaiser(points, 6.0);
    }

    /**
     * Create a Kaiser7 window spanning the given number of points (with parameter alpha = 7).
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] Kaiser7(int points) {
        return Kaiser(points, 7.0);
    }

    /**
     * Create a SFT3F window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] SFT3F(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.26526 + 0.5 * Math.cos(x) + 0.23474 * Math.cos(x + x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a SFT4F window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] SFT4F(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.21706 + 0.42103 * Math.cos(x) + 0.28294 * Math.cos(x + x) + 0.07897 * Math.cos(3 * x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a SFT5F window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] SFT5F(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.1881 + 0.36923 * Math.cos(x) + 0.28702 * Math.cos(x + x) + 0.13077 * Math.cos(3 * x)
                    + .02488 * Math.cos(4 * x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a SFT3M window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] SFT3M(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.28235 + 0.52105 * Math.cos(x) + 0.19659 * Math.cos(x + x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a SFT4M window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] SFT4M(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.241906 + 0.460841 * Math.cos(x) + 0.255381 * Math.cos(x + x) + 0.041872 * Math.cos(3 * x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a SFT5M window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] SFT5M(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.209671 + 0.407331 * Math.cos(x) + 0.281225 * Math.cos(x + x) + 0.092669 * Math.cos(3 * x)
                    + 0.0091036 * Math.cos(4 * x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a FTNI window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] FTNI(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 0.2810639 + 0.5208972 * Math.cos(x) + 0.1980399 * Math.cos(x + x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a FTHP window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] FTHP(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 1.0 + 1.912510941 * Math.cos(x) + 1.079173272 * Math.cos(x + x) + 0.1832630879 * Math
                    .cos(3 * x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a FTSRS window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] FTSRS(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 1.0 + 1.93 * Math.cos(x) + 1.29 * Math.cos(x + x) + 0.388 * Math.cos(3 * x) + 0.028 * Math
                    .cos(4 * x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a HFT70 window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] HFT70(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 1 + 1.90796 * Math.cos(x) + 1.07349 * Math.cos(x + x) + 0.18199 * Math.cos(3 * x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }

    /**
     * Create a HFT95 window spanning the given number of points.
     *
     * @param points The number of points that span the full size of the window
     * @return The window values
     */
    public static double[] HFT95(int points) {

        /*
         * Give the total domain of the window function.
         * The domain and the window function are  assumed symmetrical about zero.
         */
        double domain = twopi;

        double step = domain / (points - 1);
        int i, istart;
        double x, xstart;
        double[] window = new double[points];

        istart = points / 2;
        boolean even = (istart * 2 == points);
        xstart = (even) ? step / 2 : 0;

/*
	 * CoreCompute the right-hand half of the window.
	 */
        for (i = istart, x = xstart; i < points; ++i, x += step) {
            window[i] = 1 + 1.9383379 * Math.cos(x) + 1.3045202 * Math.cos(x + x) + 0.4028270 * Math.cos(3 * x)
                    + 0.0350665 * Math.cos(4 * x);
        }

        /*
         * Store the right-hand values in the left-hand part of the window symmetrically.
         */
        int j;
        for (i = 0, j = points - 1; i < istart; ++i, --j) {
            window[i] = window[j];
        }

        return window;
    }


}


