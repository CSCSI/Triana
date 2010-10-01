package signalproc.algorithms;

import java.util.Random;

import org.trianacode.taskgraph.Unit;


/**
 * ALEProcessor performs adaptive noise cancelling on a series of chunks of data. It accounts for the endings and
 * startings of the arrays of adjacent chunks and so treats the input chunks as one continuous input stream.  This class
 * is useful for processing continuous times series which maybe input as a series of sampled segments e.g. when
 * receiving across a network.  it saves the need to maintain buffers before processing can be applied.
 * <p/>
 * <pre>This class is a computer model of :
 * B. Widrow et al : Adaptive Noise Cancelling : Principles and Applications
 * Proceedings of the IEEE, Vol 63, No. 12, December 1975.</pre>
 *
 * @author Ian Taylor
 * @version 2.0 18 Sep 2000
 */
public class ALEProcessor {

    String type = "NLMS";

    double w[];
    double e; // error for one output sample (or node in neural network terminology..)

    double u[]; // input data : for adaptive noise cancelling the 'd' vector is equal to this
    double y[]; // output of filtered inputs

    private double sqrLenOfInput;
    private double maxIn;
    private double maxOut;

    Unit unit;

    /**
     * A String version of the Step size. This is either set to auto (for automatic calculation by finding the
     * reciprocal of the length of the input data) or a string representation of the actual chosen step size;
     */
    String stepsize = "auto";

    double alpha = 0.00000000000000001; // the step size
    double tapSpacing = 1; // tap spacing for this ALE : default 1 i.e. process every sample
    int N; // this is the number of taps i.e. weights
    boolean useBIAS;

    int prevN = -1;
    int start = 0; // starting point in the input array

    /**
     * Creates an ALE processor with a given number of taps (i.e. weight vector size), tap spacing size, a step size
     * (for the gradient descent), the type of algorithm used e.g. LMS or RLMS -> Least Mean Squared or Recursive Least
     * Mean Squared and whether you want this ALE to use a BAIS input node.
     */
    public ALEProcessor(int numerOfTaps, int tapSpacing, String stepSize, String type, boolean useBIAS) {
        N = numerOfTaps;
        this.useBIAS = useBIAS;
        this.tapSpacing = tapSpacing;
        this.type = type;
        this.stepsize = stepSize;
    }

    public void setObject(Unit unit) {
        this.unit = unit;
    }

    /**
     * Allocates the arrays used if they are different sizes from currently set.
     */
    public void allocate(double data[]) {
        if (N != prevN) {
            resetWeights();
        }

        if ((y == null) || ((N + data.length) != y.length)) {
            System.out.println("Reallocating y and u vectors...");
            y = new double[N + data.length];
            u = new double[N + data.length];
            for (int i = 0; i < N; ++i) {
                u[i] = maxIn / N;
            } // set input to max to generate a low leanring rate
            for (int i = 0; i < u.length; ++i) {
                y[i] = 0.0;
            }
        }
    }

    public void resetWeights() {
        if (N != prevN) {
            System.out.println("Resetting Vectors...");
            w = new double[N];
            prevN = N;
        }

        Random r = new Random();
        double v;
        System.out.println("Resetting the weights....");
        for (int i = 0; i < N; ++i) {
            v = (0.000001 * (r.nextDouble() - 0.5)); // very small random numbers
            w[i] = v;
        }
    }

    public void process(double data[]) {
        boolean auto = false;
        int j;

        maxIn = calcMax(data);

        allocate(data);
        // set output array to zero

        // fill in the prev Y and U arrays

        System.arraycopy(u, u.length - N, u, 0, N);
        System.arraycopy(data, 0, u, N, data.length);
        System.arraycopy(y, y.length - N, y, 0, N);

        for (int i = N; i < y.length; ++i) {
            y[i] = 0.0;
        }

        if (stepsize.equalsIgnoreCase("auto")) {
            auto = true;
        }

        if (auto) {
            setOptimumStepSize(data);
        } else {
            alpha = Double.valueOf(stepsize).doubleValue();
        }

        start = N;

        do {
            if (auto) {
                calcStepSize();
            }
            calcYn();
            calcError();
            calcNewWeights();
            start += tapSpacing;
        } while (start < u.length);

        // transfer the output to the input

        System.arraycopy(y, N, data, 0, data.length);

        maxOut = calcMax(data);

        double b = maxIn / maxOut;

        for (int i = 0; i < data.length; ++i) {
            data[i] *= b;
        }
    }

    public void calcYn() {
        int i;
        int st;
        for (i = 1; i <= N; ++i) {
            st = (start - i);
            y[start] += w[i - 1] * u[st];
        }
    }

    public void calcError() {
        int i;
        int st;
        e = 0.0;
        for (i = 1; i <= N; ++i) {
            st = (start - i);
            e += u[st] - y[st];
        }
    }

    public void calcNewWeights() {
        int i;
        double ch;
        int st;

        if (type.equals("NLMS")) {
            double b = (1 + alpha * sqrLenOfInput);
            double alpha_e = alpha * e;
            for (i = 1; i <= N; ++i) { // NLMS
                ch = (alpha_e * u[start - i]) / b;
                w[i - 1] += ch;
            }
        } else {
            for (i = 1; i <= N; ++i) { // LMS
                ch = (2.0 * alpha * u[start - i] * e);
                w[i - 1] += ch;
            }
        }
    }

    public void calcStepSize() {
        int i;
        sqrLenOfInput = 0.0;
        for (i = 1; i <= N; ++i) {
            sqrLenOfInput += (u[start - i] * u[start - i]);
        }
        double val = sqrLenOfInput * sqrLenOfInput * N;
        if (val == 0.0) {
            val = 1.0;
        }
        alpha = 1.5 / (val);
    }

    public void calcOptStepSize() {
        int i;
        sqrLenOfInput = 0.0;
        for (i = 1; i <= N; ++i) {
            sqrLenOfInput += (u[i] * u[i]);
        }
        double val = sqrLenOfInput * sqrLenOfInput * N;
        if (val == 0.0) {
            val = 1.0;
        }
        alpha = 1 / (val);
    }

    public double calcMax(double[] data) {
        int i;
        double max = 0.0;
        for (i = 0; i < data.length; ++i) {
            max = Math.max(data[i], max);
        }
        return max;
    }

    public void setOptimumStepSize(double data[]) {
        sqrLenOfInput = squareLength(data);
        alpha = 1.0 / (sqrLenOfInput * N);
    }

    public double vectlength(double arr[]) {
        return Math.sqrt(squareLength(arr));
    }

    public double squareLength(double arr[]) {
        int i;
        double accum;

        accum = 0.0;
        for (i = 0; i < arr.length; ++i) {
            accum = accum + Math.pow(arr[i], 2.0);
        }
        return accum;
    }

    public void normalise(double arr[]) { // normalizes a vector
        int i;
        double length;

        length = vectlength(arr);
        if (length > 0) {                  /* Watch out division by 0 */
            for (i = 0; i < arr.length; ++i) {
                arr[i] = (arr[i] / length);
            }
        } else {
            for (i = 0; i < arr.length; ++i) {
                arr[i] = 0;
            }
        }
    }

    public void reset() {
        resetWeights();
        if (y == null) {
            return;
        }
        if (u == null) {
            return;
        }
        for (int i = 0; i < u.length; ++i) {
            y[i] = 0.0;
            u[i] = 0.0;
        }
    }

    /**
     * Sets the tap spacing (i.e. the increment of how to proceed along the input samples) to given value
     */
    public void setTapSpacing(int spacing) {
        tapSpacing = spacing;
    }

    /**
     * Sets the number of taps (i.e. the number of weights to use) to given value
     */
    public void setNumberOfTaps(int taps) {
        N = taps;
    }

    /**
     * Sets the step size to given value
     */
    public void setStepSize(double stepSize) {
        alpha = stepSize;
    }

    /**
     * Sets the step size to the double value contained within the string or to the reciprocal of the length of the
     * input data if this string is set to auto on the next presentation of data.
     */
    public void setStepSize(String stepSize) {
        this.stepsize = stepSize;
    }

    public void setWeightUpdateType(String type) {
        this.type = type;
    }
}




