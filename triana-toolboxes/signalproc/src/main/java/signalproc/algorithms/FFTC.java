package signalproc.algorithms;

import java.util.ArrayList;

/**
 * FFTC is a class in which all methods are declared static.  This means that you don't have to instantiate it to use
 * the functions within it.
 *
 * @author B F Schutz
 * @version 1.1 09 January 2001
 * @see FFT
 * @see Counter
 * @see Factorizer
 * @see FFTPhase
 */
public final class FFTC {

    /**
     * Don't let anyone instantiate this class.
     */
    private FFTC() {
    }


    /*
    * Utility programs
    */


    private static void display4(double[] real, double[] imag) {
        int size = real.length;
        System.out.print("Print " + String.valueOf(size) + " twiddles: (1,0) ");
        for (int k = 1; k < size; k++) {
            System.out.print("(" + String.valueOf(real[k]) + ", " + String.valueOf(imag[k]) + ") ");
        }
        System.out.println(" ");
        return;
    }

    private static void display5(double[] r, double[] i) {
        System.out.println("Print results");
        int size = r.length;
        for (int k = 0; k < size; k++) {
            System.out.println(String.valueOf(r[k]) + ", " + String.valueOf(i[k]));
        }
        return;
    }

    /*
    * Main FFT program
    */

    /**
     * Class method that makes the Fourier transform of input argument vectors xReal and xImag, which can be of
     * arbitrary length. It has a boolean argument to denote forward or backward (inverse) and a boolean argument to
     * govern whether the input data are overwritten by the returned data or not. It returns an ArrayList containing the
     * real and imaginary parts of the discrete Fourier transform of the input. The input vectors can be of any length,
     * and they are not padded to 2^m. No normalizing factors are applied to the data; divisions by n or sqrt(n) are
     * left to the calling method.
     * <p/>
     * The method is a prime-factor radix method using a block-matrix decomposition approach, in which the input data
     * set is put into a hyper-matrix of dimension equal to the number of prime factors of the number of input elements.
     * The method uses a succession of discrete Fourier transforms along the different directions, where the data
     * transformed is the result of the previous transform times complex factors called twiddle factors.
     * <p/>
     * The method does not know ahead of time how many factors the data set length has (dimensions of the hyper-matrix),
     * so it uses a list of instantiations of the class Counter to keep track of the indices in the different
     * dimensions.
     * <p/>
     * The FFT elements are in the order where all positive frequencies are first, in ascending order, and then all
     * negative frequencies, again in ascending order. This is the form of the output data for a forward transform, and
     * it is the expected form of the input data for an inverse transform. Thus, the data must be two-sided; one-sided
     * data sets will be supported in a future version.
     *
     * @param double[] xReal The real part of the input complex data
     * @param double[] xImag The imaginary part of the input complex data
     * @param boolean  forward True denotes a forward or normal Fourier transform (time->frequency); false denotes an
     *                 inverse transform
     * @param boolean  overwrite True if input data is to be overwritten by output
     * @param boolean  verbose True if printing log to System.out is desired
     * @return ArrayList The real and imaginary parts of the transform
     */
    public synchronized static ArrayList FFT_C(double[] xReal, double[] xImag, boolean forward, boolean overwrite,
                                               boolean verbose) {
        int d, j, k, m, phaseStep, offset, step, index;
        double a;
        ArrayList fourier = new ArrayList(2);
        Counter outer, inner, newInner;
        boolean moreTransforms;
        double[] outReal, outImag, tw, xR, xI, ph, phReal, phImag, ar, ai;
        int n = xReal.length;
        if (overwrite) {
            outReal = xReal;
            if (xImag == null) {
                outImag = new double[n];
            } else {
                outImag = xImag;
            }
        } else {
            outReal = new double[n];
            outImag = new double[n];
        }
        //	if ( xImag == null ) FlatArray.initializeArray( outImag );
        if (verbose) {
            System.out.println(n);
        }
        if (n == 1) {
            if (!overwrite) {
                outReal[0] = xReal[0];
                if (xImag != null) {
                    outImag[0] = xImag[0];
                }
            }
            if (verbose) {
                display5(outReal, outImag);
            }
            fourier.add(outReal);
            fourier.add(outImag);
            return fourier;
        }
        if (n == 2) {
            if (overwrite) {
                a = outReal[0];
                outReal[0] += outReal[1];
                outReal[1] = a - outReal[1];
                if (xImag != null) {
                    a = outImag[0];
                    outImag[0] += outImag[1];
                    outImag[1] = a - outImag[1];
                }
            } else {
                outReal[0] = xReal[0] + xReal[1];
                outReal[1] = xReal[0] - xReal[1];
                if (xImag != null) {
                    outImag[0] = xImag[0] + xImag[1];
                    outImag[1] = xImag[0] - xImag[1];
                }
            }
            if (verbose) {
                display5(outReal, outImag);
            }
            fourier.add(outReal);
            fourier.add(outImag);
            return fourier;
        }
        Factorizer f = new Factorizer(verbose);
        if (verbose) {
            System.out.println("Factorizer called with argument n = " + String.valueOf(n));
        }
        int[] factors = f.factorize(n);
        int dimensions = factors.length;
        if (verbose) {
            System.out.print("Factorizer returns with list of " + String.valueOf(dimensions) + " factors: ");
            for (j = 0; j < dimensions; j++) {
                System.out.print(String.valueOf(factors[j]) + " ");
            }
            System.out.println(" ");
        }
        int forw = forward ? -1 : 1; // forward transform must use complex-conjugate phases.
        /*
         First set up phases vector.
      */
        FFTPhase p = new FFTPhase(verbose);
        p.makePhase(n);
        if (verbose) {
            p.display3();
        }
        phReal = new double[2];
        phImag = new double[2];
        /*
         Next create a linked list of Counters, one for each dimension.
         The Counters keep track of iterations in such a way that the
         top of the list (the outer iteration loop) maintains and updates
         variables that tell us how to step through the vector x for each
         hyper-row of the hyper-matrix parallel to the dimension represented
         by the bottom Counter of the list (inner iteration loop). The outer
         Counter also assembles the twiddle factors from the phase array
         that are needed for the Fourier transforms along the direction
         of the inner Counter.  When all hyper-rows of the inner Counter
         direction are done, the Counters are rearranged in a cyclic way
         so that the old inner one becomes the new outer one. Then the
         transforms over the hyper-rows in the dimension corresponding
         to the new inner Counter can be done.
      */
        outer = new Counter(dimensions - 1, factors, null, p, verbose);
        inner = outer;
        if (dimensions > 1) {
            for (d = dimensions - 2; d >= 0; d--) {
                outer = new Counter(d, factors, outer, p, verbose);
            }
        }
        outer.reset(outer);
        /*
         Loop over dimensions (factors of n). For each dimension multiply
         the values of x by appropriate twiddle factors and do transforms
         along the hyper-rows of that dimension.  The dimension to be
         transformed is always represented by the innermost Counter. Work
         in place on the arrays outReal and outImag. If not overwriting
         the input arrays, copy them to the output arrays.
      */
        if (!overwrite) {
            System.arraycopy(xReal, 0, outReal, 0, n);
            if (xImag != null) {
                System.arraycopy(xImag, 0, outImag, 0, n);
            }
        }
        for (d = dimensions - 1; d >= 0; d--) {
            /* If the dimension has more than two elements (the prime factor
              is larger than 2) then the Fourier transform will need non-trivial
              phase factors. Get them from the phase matrix and store them in
              (phReal, phImag).
           */
            if (factors[d] > 2) {
                phaseStep = n / factors[d];
                if (verbose) {
                    System.out.println("Factors[d] = " + String.valueOf(factors[d]) + ", phaseStep = " + String
                            .valueOf(phaseStep));
                }
                phReal = new double[factors[d]];
                phImag = new double[factors[d]];
                if (forward) {
                    for (k = 0; k < factors[d]; k++) {
                        if (verbose) {
                            System.out.println("Asked for phase for index " + String.valueOf(k * phaseStep));
                        }
                        ph = p.getPhase(k * phaseStep);
                        phReal[k] = ph[0];
                        phImag[k] = -ph[1];
                    }
                } else {
                    for (k = 0; k < factors[d]; k++) {
                        if (verbose) {
                            System.out.println("Asked for phase for index " + String.valueOf(k * phaseStep));
                        }
                        ph = p.getPhase(k * phaseStep);
                        phReal[k] = ph[0];
                        phImag[k] = ph[1];
                    }
                }
            }
            /* The boolean moreTransforms is true until all the hyper-rows of
              the current dimension have been transformed. The contents of
              the while-loop are performed for each hyper-row.
           */
            moreTransforms = true;
            while (moreTransforms) {
                /* Variable offset locates first element of the current hyper-row
               in the data vector x. Variable step gives the interval between
               the elements of this hyper-row in x. These variables are held
               in the outermost loop Counter, and are changed at the end of
               the while-loop when the hyper-row is changed.
            */
                offset = outer.getTOffset();
                step = inner.getTStep();
                /*
              Apply twiddle factors that are stored in outermost Counter.
              Twiddle factors are unity if alpha = 0 so don't do that step
              (ie start with k = offset + step and m = 1), and they are unity
              if beta = 0, which is the first transform to do. So pass up the
              first transform of any set (where offset = 0). Twiddle factors
              are taken from the phase vector, so they run counter-clockwise
              around the unit circle in the complex plane. This is what is
              needed for the inverse transform, but for a forward transform
              the complex conjugates must be used.
            */
                if ((d < dimensions - 1) && (offset > 0)) {
                    if (verbose) {
                        System.out.println("Apply twiddles to data.");
                    }
                    //System.out.println("data offset = " + String.valueOf(offset));
                    //System.out.println("data step = " + String.valueOf(step));
                    if (verbose) {
                        display4(outer.getTwiddleReal(), outer.getTwiddleImag());
                    }
                    if (forward) {
                        for (k = offset + step, m = 1; k < offset + step * factors[d]; k += step, m++) {
                            if (verbose) {
                                System.out.println(
                                        "k=" + String.valueOf(k) + " xreal=" + String.valueOf(outReal[k]) + " ximag="
                                                + String.valueOf(outImag[k]) + " twiddleReal="
                                                + String.valueOf(outer.getTwiddleReal()[m]) + " twiddleImag=" + String
                                                .valueOf(outer.getTwiddleImag()[m]));
                            }
                            a = outReal[k] * outer.getTwiddleReal()[m] + outImag[k] * outer.getTwiddleImag()[m];
                            outImag[k] = outImag[k] * outer.getTwiddleReal()[m] - outReal[k] * outer
                                    .getTwiddleImag()[m];
                            outReal[k] = a;
                        }
                    } else {
                        for (k = offset + step, m = 1; k < offset + step * factors[d]; k += step, m++) {
                            if (verbose) {
                                System.out.println(
                                        "k=" + String.valueOf(k) + " xreal=" + String.valueOf(outReal[k]) + " ximag="
                                                + String.valueOf(outImag[k]) + " twiddleReal="
                                                + String.valueOf(outer.getTwiddleReal()[m]) + " twiddleImag=" + String
                                                .valueOf(outer.getTwiddleImag()[m]));
                            }
                            a = outReal[k] * outer.getTwiddleReal()[m] - outImag[k] * outer.getTwiddleImag()[m];
                            outImag[k] = outReal[k] * outer.getTwiddleImag()[m] + outImag[k] * outer
                                    .getTwiddleReal()[m];
                            outReal[k] = a;
                        }
                    }
                }
                /*
              Begin the Fourier transform along the current dimension. If it
              is of length 2, do it without looking up (trivial) phase
              factors. If it is a prime number larger than 2, then must
              look up phase factors.
            */
                if (factors[d] == 2) {
                    if (verbose) {
                        System.out.println("offset = " + String.valueOf(offset));
                    }
                    if (verbose) {
                        System.out.println("step = " + String.valueOf(step));
                    }
                    index = offset + step;
                    if (verbose) {
                        System.out.println(
                                "x0=(" + String.valueOf(outReal[offset]) + "," + String.valueOf(outImag[offset])
                                        + ") x1=(" + String.valueOf(outReal[index]) + ","
                                        + String.valueOf(outImag[index]) + ")");
                    }
                    a = outReal[offset] - outReal[index];
                    outReal[offset] += outReal[index];
                    outReal[index] = a;
                    a = outImag[offset] - outImag[index];
                    outImag[offset] += outImag[index];
                    outImag[index] = a;
                } else {
                    if (verbose) {
                        System.out.println("offset = " + String.valueOf(offset));
                    }
                    if (verbose) {
                        System.out.println("step = " + String.valueOf(step));
                    }
                    xR = new double[factors[d]];
                    xI = new double[factors[d]];
                    ar = new double[factors[d]];
                    ai = new double[factors[d]];
                    ar[0] = outReal[offset];
                    ai[0] = outImag[offset];
                    if (verbose) {
                        System.out.println("Beginning zero frequency element loop.");
                    }
                    for (j = offset + step; j < offset + step * factors[d]; j += step) {
                        ar[0] += outReal[j];
                        ai[0] += outImag[j];
                    }
                    if (verbose) {
                        System.out.println("Beginning non-zero frequency elements loops.");
                    }
                    for (k = 1; k < factors[d]; k++) {
                        ar[k] = outReal[offset];
                        ai[k] = outImag[offset];
                        for (j = offset + step, m = 1; j < offset + step * factors[d]; j += step, m++) {
                            index = (m * k) % factors[d];
                            ar[k] += outReal[j] * phReal[index] - outImag[j] * phImag[index];
                            ai[k] += outReal[j] * phImag[index] + outImag[j] * phReal[index];
                        }
                    }
                    if (verbose) {
                        System.out.println("Beginning translation loop.");
                    }
                    for (j = offset, m = 0; j < offset + step * factors[d]; j += step, m++) {
                        outReal[j] = ar[m];
                        outImag[j] = ai[m];
                    }
                }
                /*
               Now that the Fourier transform along the current hyper-row
               is done, change the hyper-row by calling the appropriate
               method of the outer Counter. When all hyper-rows have been
               done, this method returns false and the while-loop ends.
            */
                moreTransforms = outer.changeTransform();
            }
            /*
              The transforms for all hyper-rows in a given dimension are
              finished. Change to a new dimension by re-ordering the Counters
              and going through it all again.
           */
            if (dimensions > 1) {
                newInner = outer.cycleCounters(outer, null);
                outer = inner;
                inner = newInner;
            } else {
                outer.reset(outer);
            }
        }
        /*
        Now rearrange the elements so that the output is in the expected order.
        The Counters have been recycled in the previous loop so that they are
        in the original order, so they can be used to move through the array
        and rearrange the elements.

        NB This can be done with less storage. Examine butterfly diagrams
        and implement!
      */
        double[] reorderReal = new double[n];
        double[] reorderImag = new double[n];
        reorderReal[0] = outReal[0];
        reorderImag[0] = outImag[0];
        int tx, fx;
        if (verbose) {
            System.out.println("Reorder output.");
        }
        while (outer.changeIndex()) {
            tx = outer.getTIndex();
            fx = outer.getFIndex();
            if (verbose) {
                System.out.println("Reorder tx = " + String.valueOf(tx) + " to fx = " + String.valueOf(fx));
            }
            reorderReal[fx] = outReal[tx];
            reorderImag[fx] = outImag[tx];
        }
        if (verbose) {
            display5(reorderReal, reorderImag);
        }
        outReal = reorderReal;
        outImag = reorderImag;
        fourier.add(outReal);
        fourier.add(outImag);
        return fourier;
    }

    /**
     * Class method that takes the Fourier transform of input argument vectors xReal and xImag of arbitrary length,
     * overwriting the input data. A boolean argument denotes forward or backward (inverse) transforms. The method
     * returns an ArrayList containing the real and imaginary parts of the discrete Fourier transform of the input. The
     * input vectors can be of any length, and they are not padded to 2^m. No normalizing factors are applied to the
     * data; divisions by n or sqrt(n) are left to the calling method.
     * <p/>
     * The method is a prime-factor radix method using a block-matrix decomposition approach, in which the input data
     * set is put into a hyper-matrix of dimension equal to the number of prime factors of the number of input elements.
     * The method uses a succession of discrete Fourier transforms along the different directions, where the data
     * transformed is the result of the previous transform times complex factors called twiddle factors.
     * <p/>
     * The method does not know ahead of time how many factors the data set length has (dimensions of the hyper-matrix),
     * so it uses a list of instantiations of the class Counter to keep track of the indices in the different
     * dimensions.
     * <p/>
     * The FFT elements are returned in the order where all positive frequencies are first, in ascending order, and then
     * all negative frequencies, again in ascending order. This is the form of the output data for a forward transform,
     * and it is the expected form of the input data for an inverse transform. Thus, the data must be two-sided;
     * one-sided data sets will be supported in a future version.
     * <p/>
     * To cover the case where the input data is real, this method returns the ArrayList containing the transform even
     * though the operation is performed in place and the real part at least can be read from the input data vectors.
     *
     * @param double[] xReal The real part of the input complex data, to be replaced by the real part of its Fourier
     *                 transform
     * @param double[] xImag The imaginary part of the input complex data, to be replaced by the imaginary part of its
     *                 Fourier transform if it exists
     * @param boolean  forward True denotes a forward or normal Fourier transform (time->frequency); false denotes an
     *                 inverse transform
     * @param boolean  verbose True if printing log to System.out is desired
     * @return ArrayList The real and imaginary parts of the transform
     */
    public synchronized static ArrayList FFT_C(double[] xReal, double[] xImag, boolean forward, boolean verbose) {
        return FFT_C(xReal, xImag, forward, true, verbose);
    }


    /**
     * Class method to test whether a data set has the symmetry property that will result in its transform being pure
     * real or pure imaginary. If, for a full data set (not one-sided spectra, which will automatically transform to
     * real), x[k] = ComplexConjugate(x[N-k]), then the transform will be real. If  x[k] = -ComplexConjugate(x[N-k])
     * then the transform will be imaginary. This test should only be applied to the full data set that will be
     * transformed.
     * <p/>
     * The return value is an int. It has value +1 if the transform will be real, -1 if imaginary, 0 if there is no
     * special symmetry.
     *
     * @param double[] xr The real part of the input data set (can be null)
     * @param double[] xi The imaginary part of the input data set (can be null)
     * @return int Takes value =1, 0, or -1
     */
    public synchronized static int testConjugateSymmetry(double[] xr, double[] xi) {
        boolean symmetric = true;
        boolean antisymmetric = true;
        int k, len, lk;
        if (xi == null) {
            if (xr == null) {
                return 0;
            } else {
                if (xr[0] != 0) {
                    antisymmetric = false;
                }
                k = 1;
                len = xr.length;
                while ((k < len / 2) && (symmetric || antisymmetric)) {
                    lk = len - k;
                    if ((symmetric) && (xr[k] != xr[lk])) {
                        symmetric = false;
                    }
                    if ((antisymmetric) && (xr[k] != -xr[lk])) {
                        antisymmetric = false;
                    }
                    k++;
                }
            }
        } else {
            if (xr == null) {
                if (xi[0] != 0) {
                    symmetric = false;
                }
                k = 1;
                len = xi.length;
                while ((k < len / 2) && (symmetric || antisymmetric)) {
                    lk = len - k;
                    if ((symmetric) && (xi[k] != -xi[lk])) {
                        symmetric = false;
                    }
                    if ((antisymmetric) && (xi[k] != xi[lk])) {
                        antisymmetric = false;
                    }
                    k++;
                }
            } else {
                if (xr[0] != 0) {
                    antisymmetric = false;
                }
                if (xi[0] != 0) {
                    symmetric = false;
                }
                k = 1;
                len = xr.length;
                while ((k < len / 2) && (symmetric || antisymmetric)) {
                    lk = len - k;
                    if ((symmetric) && ((xr[k] != xr[lk]) || (xi[k] != -xi[lk]))) {
                        symmetric = false;
                    }
                    if ((antisymmetric) && ((xr[k] != -xr[lk]) || (xi[k] != xi[lk]))) {
                        antisymmetric = false;
                    }
                    k++;
                }
            }
        }
        if (symmetric) {
            return 1;
        }
        if (antisymmetric) {
            return -1;
        }
        return 0;
    }


}

