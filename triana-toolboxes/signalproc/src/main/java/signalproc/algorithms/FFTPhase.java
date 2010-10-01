package signalproc.algorithms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

/**
 * Class FFTPhase contains utilities for the computation of phases for the Fast Fourier Transform methods in Triana. It
 * initializes phases, keeps them in Hashtables for later use, and provides them on demand to other programs.
 *
 * @author B F Schutz
 * @version 1.0 25 October 1999
 */

public class FFTPhase {

    private double[] phaseReal, phaseImag;
    private boolean nEven, nBy2Even, verbose;
    private int phaseDim, nBy2, nBy4, nBy4T3, n;
    private int phaseRefinement = 1;
    /* 
       These hashtables remember phase factors.  They will continue to be 
       added to while the program runs, if data sets with different 
       values of n are used. 
    */
    private static Hashtable phaseDictionaryReal = new Hashtable(5);
    private static Hashtable phaseDictionaryImag = new Hashtable(5);


    /**
     * Constructor for FFTPhase sets the verbose flag, to determine whether comments are written to System.out.
     */
    public FFTPhase(boolean information) {
        verbose = information;
    }


    public double[] getPhaseRealParts() {
        return phaseReal;
    }

    public double[] getPhaseImaginaryParts() {
        return phaseImag;
    }

    public void display3() {
        // System.out.println("Print phases ");
        int size = phaseReal.length;
        // System.out.println( "Phase vector size = " + String.valueOf(size) );
        // for (int k = 0; k < size; k++) System.out.println( String.valueOf( phaseReal[k] ) + ", " + String.valueOf( phaseImag[k] ) );
        return;
    }

    /**
     * The method makePhase( int nElements) creates a vector of n complex numbers uniformly spaced counter-clockwise
     * around the unit circle. Used in FFTs, these are suitable for the inverse transform.  Direct transforms must use
     * the complex-conjugates.
     * <p/>
     * It stores their real and imaginary parts, respectively, in the vectors of doubles phaseReal[] and phaseImag[]. It
     * only computes phases if there are more than 2 points. If there are 2 or fewer, the phases are trivial and are not
     * needed by the Fourier methods.
     * <p/>
     * The method stores computed phase vectors in static Hashtables and looks for them when it is called another time,
     * so that avoids re-computing phase vectors unnecessarily.  It can use stored vectors that contain an integer
     * multiple of nElements elements.
     *
     * @param nElements Number of complex numbers on unit circle.
     */
    public void makePhase(int nElements) {
        if (verbose) {
            System.out.println("Creating phases with argument n = " + String.valueOf(nElements));
        }
        int oldPhaseSize;
        Integer nObj, oldObj;
        boolean tryPhases;
        double s, c, s0, c0, s1, c1;

        n = nElements;
        if (n <= 2) {
            return;
        }
        /*
        Determine some integers that help exploit symmetries of
        the phase vector.
      */
        nEven = (n % 2 == 0);
        if (nEven) {
            nBy2 = n / 2;
            nBy2Even = (nBy2 % 2 == 0);
            if (nBy2Even) {
                nBy4 = nBy2 / 2;
                nBy4T3 = nBy4 * 3;
            } else {
                nBy4 = (nBy2 + 1) / 2;
                nBy4T3 = nBy4 + nBy2;
            }
        } else {
            nBy2 = (n + 1) / 2;
            nBy2Even = false;
        }
        if (verbose) {
            System.out.println(nBy2);
        }
        if (nBy2Even) {
            if (verbose) {
                System.out.println(nBy4);
            }
        }
        /*
         Search the Hashtable for a phase vector that contains the
         one desired here, ie which has a length that is an integer
         multiple of the argument n.  If the memorized phase
         contains a multiple of n elements, keep track of the multiplier
         in the variable phaseRefinement. Return after assigning
         the remembered complex phases to the vectors phaseReal and
         phaseImag.
      */
        nObj = new Integer(n);
        Enumeration phasesDone = phaseDictionaryReal.keys();
        try {
            tryPhases = true;
            while (tryPhases) {
                oldObj = (Integer) phasesDone.nextElement();
                oldPhaseSize = oldObj.intValue();
                if (verbose) {
                    System.out.println("Old phase array size is " + String.valueOf(oldPhaseSize));
                }
                if (oldPhaseSize % n == 0) {
                    phaseRefinement = oldPhaseSize / n;
                    phaseReal = (double[]) phaseDictionaryReal.get(oldObj);
                    phaseImag = (double[]) phaseDictionaryImag.get(oldObj);
                    if (verbose) {
                        System.out.println(
                                "Old phases will be used with refinement factor " + String.valueOf(phaseRefinement));
                    }
                    tryPhases = false;
                }
            }
        }
        /*
         If there is no element in the hashtable that contains the
         desired set of phases, exit from the search by catching
         the exception generated by falling off the end of the
         Hashtable of saved phases.  Now generate the desired
         elements from scratch by starting with the smallest and
         multiplying them up. But don't compute any that can be
         obtained by symmetry. These will be retrieved correctly,
         taking account of symmetry, by method getPhase below.
         (We use symmetries that allow one to compute only the elements
         in the upper half-plane or the first quadrant: n is
         divisible by 2 or by 4. Higher-order symmetries save nothing
         in computational time, since they require complex
         multiplications to find new elements.)
      */
        catch (NoSuchElementException ex1) {
            phaseRefinement = 1;
            double theta = 2.0 * Math.PI / n;
            double angleStep = theta;
            //s0 = Math.sin( theta );
            //c0 = Math.cos( theta );
            if (nEven) {
                phaseDim = nBy4;
            } else {
                phaseDim = nBy2;
            }
            if (verbose) {
                System.out.println(phaseDim);
            }
            phaseReal = new double[phaseDim];
            phaseImag = new double[phaseDim];
            phaseReal[0] = 1.0;
            phaseImag[0] = 0.0;
            //c = 1.0;
            //s = 0.0;
            for (int k = 1; k < phaseDim; k++) {
                //c1 = c * c0 - s * s0;
                //s1 = s * c0 + c * s0;
                //c = c1;
                //s = s1;
                //phaseReal[k] = c1;
                //phaseImag[k] = s1;
                phaseReal[k] = Math.cos(angleStep);
                phaseImag[k] = Math.sin(angleStep);
                angleStep += theta;
            }
            phaseDictionaryReal.put(nObj, phaseReal);
            phaseDictionaryImag.put(nObj, phaseImag);
            /*
             Having created and stored a new phase array,
             clean up the Hashtables by deleting any
             previously stored phase arrays that are
             contained in the present one. This minimizes
             the storage required.
           */
            phasesDone = phaseDictionaryReal.keys();
            try {
                tryPhases = true;
                while (tryPhases) {
                    oldObj = (Integer) phasesDone.nextElement();
                    oldPhaseSize = oldObj.intValue();
                    if ((n % oldPhaseSize == 0) && (n > oldPhaseSize)) {
                        phaseDictionaryReal.remove(oldObj);
                        phaseDictionaryImag.remove(oldObj);
                        if (verbose) {
                            System.out.println("Old phases of length " + String.valueOf(oldPhaseSize)
                                    + " removed from Hashtables.");
                        }
                    }
                }
            }
            catch (NoSuchElementException ex) {
                if (verbose) {
                    System.out.println("No phases removed from Hashtables.");
                }
            }
        }
        return;
    }


    /* Method getPhase takes its argument m and returns the complex 
       phase at angle 2*Pi*m/n in the complex plane. This is returned 
       as a two-element array containing the real and imaginary parts.
       
       This complex number is extracted from the current phase set 
       (phaseReal, phaseImag) by, if necessary, undersampling it by 
       the factor phaseRefinement, which is the factor by which the 
       length of phaseReal exceeds n.  Thus, the element returned has 
       index m*phaseRefinement in phaseReal.
       
       This method also computes the correct return values when 
       a set has symmetry, so that the stored values are not 
       the full set but only the top half-plane or first quadrant.
    */

    public double[] getPhase(int m) {
        double[] t = new double[2];
        int index;
        if (phaseRefinement == 1) {
            if (nEven) {
                if (nBy2Even) {
                    if (m < nBy4) {
                        t[0] = phaseReal[m];
                        t[1] = phaseImag[m];
                    } else if (m < nBy2) {
                        index = m - nBy4;
                        t[0] = -phaseImag[index];
                        t[1] = phaseReal[index];
                    } else if (m < nBy4T3) {
                        index = m - nBy2;
                        t[0] = -phaseReal[index];
                        t[1] = -phaseImag[index];
                    } else {
                        index = m - nBy4T3;
                        t[0] = phaseImag[index];
                        t[1] = -phaseReal[index];
                    }
                } else {
                    if (m < nBy4) {
                        t[0] = phaseReal[m];
                        t[1] = phaseImag[m];
                    } else if (m <= nBy2) {
                        index = nBy2 - m;
                        t[0] = -phaseReal[index];
                        t[1] = phaseImag[index];
                    } else if (m < nBy4T3) {
                        index = m - nBy2;
                        t[0] = -phaseReal[index];
                        t[1] = -phaseImag[index];
                    } else {
                        index = n - m;
                        t[0] = phaseReal[index];
                        t[1] = -phaseImag[index];
                    }
                }
            } else {
                if (m < nBy2) {
                    t[0] = phaseReal[m];
                    t[1] = phaseImag[m];
                } else {
                    index = n - m;
                    t[0] = phaseReal[index];
                    t[1] = -phaseImag[index];
                }
            }
        } else {
            if (nEven) {
                if (nBy2Even) {
                    if (m < nBy4) {
                        index = m * phaseRefinement;
                        t[0] = phaseReal[index];
                        t[1] = phaseImag[index];
                    } else if (m < nBy2) {
                        index = (m - nBy4) * phaseRefinement;
                        t[0] = -phaseImag[index];
                        t[1] = phaseReal[index];
                    } else if (m < nBy4T3) {
                        index = (m - nBy2) * phaseRefinement;
                        t[0] = -phaseReal[index];
                        t[1] = -phaseImag[index];
                    } else {
                        index = (m - nBy4T3) * phaseRefinement;
                        t[0] = phaseImag[index];
                        t[1] = -phaseReal[index];
                    }
                } else {
                    if (m < nBy4) {
                        index = m * phaseRefinement;
                        t[0] = phaseReal[index];
                        t[1] = phaseImag[index];
                    } else if (m <= nBy2) {
                        index = (nBy2 - m) * phaseRefinement;
                        t[0] = -phaseReal[index];
                        t[1] = phaseImag[index];
                    } else if (m < nBy4T3) {
                        index = (m - nBy2) * phaseRefinement;
                        t[0] = -phaseReal[index];
                        t[1] = -phaseImag[index];
                    } else {
                        index = (n - m) * phaseRefinement;
                        t[0] = phaseReal[index];
                        t[1] = -phaseImag[index];
                    }
                }
            } else {
                if (m < nBy2) {
                    index = m * phaseRefinement;
                    t[0] = phaseReal[index];
                    t[1] = phaseImag[index];
                } else {
                    index = (n - m) * phaseRefinement;
                    t[0] = phaseReal[index];
                    t[1] = -phaseImag[index];
                }
            }
        }
        if (verbose) {
            System.out.println("Returned phase for index " + String.valueOf(m) + ": (" + String.valueOf(t[0]) + ", "
                    + String.valueOf(t[1]) + ")");
        }
        return t;
    }


}
