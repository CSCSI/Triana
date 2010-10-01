package signalproc.algorithms;

/**
 * Class Counter provides counters that can simulate some operations using nested loops. This is useful when a program
 * does not know to what depth loops must be nested, eg if this depends on a parameter. Such loops cannot be programmed,
 * but can be achieved by recursion. Counter provides an alternative. Each Counter counts iterations of one loop; the
 * Counters are linked in a list, and when one Counter finishes its loop cycle it is re-initialized and the
 * next-previous Counter in the list is advanced by one.
 * <p/>
 * As presently constructed, Counters do not allow for execution of statements between loops: all the action must be in
 * the innermost loop. Thus, Counters are an alternative to equivalent combinatorial algorithms, as are usual in FFTs.
 *
 * @author B F Schutz
 * @version 1.0  25 October 1999
 */
public class Counter {

    private int alpha, nFactors, dimension;
    private int[] factors;
    private Counter outermost, inner;
    private boolean verbose, isOutermost, isInnermost, isLast, changeTwiddle;
    private double[] twiddleReal, twiddleImag, ph;
    private FFTPhase phase;
    private int tOffset, twiddleStep, tStep, fStep, alphaRange, l, tRange, fRange, nTwiddles, twiddleTStep, tIndex,
            fIndex, n, twiddleRange;

    /**
     * Constructor for class Counter. Only one constructor is provided: supply the total number of dimensions nF of the
     * hyper-matrix, the dimension number d for this counter (ranging from 0 to nF-1), the list f of the sizes of each
     * dimension of the hyper-matrix, the total size n of the input data vector, the counter i that will be nested
     * inside this one, and two vectors of doubles that contain the complex phase factors needed for twiddles.
     */
    public Counter(int d, int[] f, Counter i, FFTPhase p, boolean information) {
        verbose = information;
        factors = f; // list of dimension sizes
        nFactors = f.length; // total number of factors, or dimensions in the hyper-matrix
        dimension = d; // the number of the dimension counted by this Counter (d from 0 to nF-1)
        phase = p; // pointer to the object that controls the phase factors
        alphaRange = factors[dimension] - 1; // the index for this dimension runs to this maximum
        n = 1;
        for (l = 0; l < nFactors; l++) {
            n *= factors[l];
        } // calculate total number of points
        fStep = 1; // this will be the step in the FFT separating elements in this dimension
        if (dimension == nFactors - 1) {
            isLast = true; // never changes: marks the one that starts out as innermost
            isInnermost = true; // is updated when Counters are rearranged
            inner = null; // is updated when Counters are rearranged
        } else {
            for (l = nFactors - 1; l > dimension; l--) {
                fStep *= factors[l];
            }
            isLast = false;
            isInnermost = false;
            inner = i;
        }
        fRange = fStep * alphaRange; // the distance between the first and last elements in FFT
        tStep = 1; // this will be the step in x separating elements in this direction
        if (dimension == 0) {
            isOutermost = true;
        } else {
            isOutermost = false;
            for (l = 0; l < dimension; l++) {
                tStep *= factors[l];
            }
        }
        tRange = tStep * alphaRange; // the distance between the first and last elements in x
        tOffset = 0; // this will be the starting index in x for a hyper-row
        twiddleStep = 0; // this will be the phase index step if twiddles are calculated
    }

    /**
     * Make certain parameters accessible from outside: tOffset, tStep, fIndex, tIndex, twiddleReal, and twiddleImag.
     */
    public int getTOffset() {
        return tOffset;
    }

    public int getTStep() {
        return tStep;
    }

    public int getFIndex() {
        return fIndex;
    }

    public int getTIndex() {
        return tIndex;
    }

    public double[] getTwiddleReal() {
        return twiddleReal;
    }

    public double[] getTwiddleImag() {
        return twiddleImag;
    }

    /**
     * Method cycleCounters rearranges the list of counters, moving the innermost one to become the outermost, and
     * keeping the rest in the original order. It is invoked from the top-most (outer) Counter of the current list, and
     * chains along the list until the method of the innermost Counter is invoked. The method needs arguments o for the
     * outermost Counter before rearrangement, and u for the Counter that is invoking the method. (When it is invoked
     * from outside the outermost Counter, this argument can be set to null.  It is not looked at until it is invoked in
     * the innermost Counter.) For each of the outer Counters the method invokes the same method of the next inner
     * Counter, and then it returns the pointer that was returned to it from inside. The innermost Counter uses the
     * method to set up various arrays and to set its pointer to its new inner neighbor, the old outer Counter o. This
     * innermost Counter then returns the pointer to the new innermost Counter, whose pointer it received as an argument
     * when its method was invoked by this Counter.  In this way, by invoking the outermost method, one gets in return
     * the pointer to the new outermost method.
     */
    public Counter cycleCounters(Counter o, Counter u) {
        if (!isInnermost) {
            return inner.cycleCounters(o, this);
        } // chain inwards
        /*
         The rest of the code is executed only for the innermost Counter, which is
         to be moved to become the outermost. In this position, its next inner
         Counter will be the previous outermost one, o.
      */
        inner = o;
        u.isInnermost = true;
        u.inner = null;
        isInnermost = false;
        tIndex = 0;
        fIndex = 0;
        /*
        An important part of this is to set up the twiddle factors.  They are used
        to multiply the values of x before they are transformed in the next set of
        transformations. The range twiddleRange of the twiddles' index is the same as the range
        of the transform, which will be done along the new innermost Counter direction, u.
        The twiddle steps are proportional to the index times the step u.tStep in x that
        is used in this dimension. Then set up arrays to hold the twiddle factors. They are
        retrieved from the phase array when the appropriate point in the looping is reached.
        Set default values of (1,0) for all twiddle phases until they are recalculated.
      */
        twiddleRange = u.alphaRange;
        nTwiddles = twiddleRange + 1;
        twiddleTStep = u.tStep;
        twiddleReal = new double[nTwiddles];
        twiddleImag = new double[nTwiddles];
        for (l = 0; l < nTwiddles; l++) {
            twiddleReal[l] = 1.0;
        }
        o.twiddleReal = null; // Free up memory that had been used for twiddles before.
        o.twiddleImag = null; // In this way, twiddles are stored only in the outermost Counter.
        reset(this); // reset all the Counters now that this Counter is the outermost.
        return u;
    }

    /**
     * Method reset is another chaining method, which re-initializes all the Counters, setting their loop indices to
     * zero and telling them which is the new outermost one.
     */
    public void reset(Counter o) {
        outermost = o;
        alpha = 0;
        changeTwiddle = false;
        isOutermost = (o == this);
        if (!isInnermost) {
            inner.reset(o);
        }
    }

    /**
     * Method changeIndex is the standard looping method. It is called from the outermost Counter, and it causes the
     * innermost one to increment. If any Counter reaches the end of its range, it is set back to zero and the next
     * outer one is incremented. This is achieved by the fact that this method returns true if the current index has not
     * reached the end of its range and false if it reaches the end. In this way, the call to the outermost Counter will
     * return true if there are more iterations to be done, but it returns false when all iterations are finished.
     * <p/>
     * In order to record the current iteration, each Counter modifies the appropriate indices that are held in the
     * outermost Counter.  It adds the change in its index alpha times the appropriate step size tStep and fStep. When
     * the current index is reset, the appropriate amount is subtracted. The method helps accomplish the rearrangement
     * of elements from the time domain to the Fourier domain.
     */
    boolean changeIndex() {
        boolean returnValue = true;
        if (isInnermost) {
            if (alpha < alphaRange) {
                alpha++;
                outermost.tIndex += tStep;
                outermost.fIndex += fStep;
            } else {
                alpha = 0;
                returnValue = false;
                outermost.tIndex -= tRange;
                outermost.fIndex -= fRange;
            }
        } else if (!inner.changeIndex()) {
            if (alpha < alphaRange) {
                alpha++;
                outermost.tIndex += tStep;
                outermost.fIndex += fStep;
            } else {
                alpha = 0;
                returnValue = false;
                outermost.tIndex -= tRange;
                outermost.fIndex -= fRange;
            }
        }
        return returnValue;
    }

    /**
     * Method changeTransform is similar to changeIndex, in that it runs through iterations of the loops in the same
     * way.  However, the purpose of this iteration is to find the starting point of all the hyper-rows associated with
     * the dimension of the current Fourier transform, which is the innermost Counter dimension.  So the innermost
     * Counter does not increment. In  addition, the method updates the twiddle factors where appropriate, which means
     * every time the indices of the Counters that have at one time been innermost change. These are the Counters that
     * include or are outside the one for which isLast is true. (This is the original innermost one.)
     */
    boolean changeTransform() {
        int twiddleAdvance, twiddleEnd, l, m;
        boolean returnValue = true;
        if (isInnermost) {
            return false;
        } // ensures that the innermost Counter is kept at 0
        /*
         If the Counter being changed is the originally last one, then the
         twiddle factor must change. If this part of the method is
         reached in this Counter, then the inner loops have forced a change of the
         Counters that represent the deepest original levels. This
         forces a change in the twiddle factors.
      */
        if (!inner.changeTransform()) {
            if (isLast) {
                outermost.changeTwiddle = true;
            }
            if (alpha < alphaRange) {
                alpha++;
                outermost.tOffset += tStep;
                if (outermost.changeTwiddle) {
                    outermost.twiddleStep += fStep;
                }
            } else {
                alpha = 0;
                returnValue = false;
                outermost.tOffset -= tRange;
                if (outermost.changeTwiddle) {
                    outermost.twiddleStep -= fRange;
                }
            }
        }
        /*
        When the looping reaches the outermost level, we check to see if
        twiddles have to be reset, since they are stored here. Any appropriate
        inner level of the iteration may have set variable changeTwiddles
        to true, in which case we change the twiddle values and then put
        changeTwiddle back to false.

        The twiddle factors are taken from the phase array computed in
        FFTPhase. This has n elements, so the index must be mod-ed by n to
        get the appropriate index. The phase factors run counter-clockwise
        around the unit circle, so the twiddle factors will do this too.
        The FFT methods in FFTUtils must decide whether to use these phases
        or their complex conjugates, depending on whether the transform
        is forwards or backwards.

        Don't do this step if returnValue is false, because that means that
        we have reached the end of looping over transforms and are about to
        cycle the Counters.
      */
        if ((isOutermost) && (changeTwiddle) && (returnValue)) {
            if (verbose) {
                System.out.println("Setting up new twiddles.");
            }
            twiddleAdvance = twiddleTStep * twiddleStep;
            twiddleEnd = twiddleAdvance * nTwiddles;
            twiddleReal[0] = 1.;
            twiddleImag[0] = 0.;
            for (l = twiddleAdvance, m = 1; l < twiddleEnd; l += twiddleAdvance, m++) {
                ph = phase.getPhase(l % n);
                twiddleReal[m] = ph[0];
                twiddleImag[m] = ph[1];
            }
            changeTwiddle = false;
        }
        return returnValue;
    }

}
    
