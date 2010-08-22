package signalproc.algorithms;


/*
 * Copyright (c) 1999 University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */


import java.util.Hashtable;

/**
 * Class Factorizer provides a method, factorize(n), that factors an integer n into its prime factors and returns them
 * as a vector of integers. (It does not return the universal factor 1 in this vector unless n=1.)  If given a negative
 * integer, it returns a vector whose first element is -1 and whose remaining elements are the prime factors of n.  If
 * given an argument zero, it returns a single element 0.
 * <p/>
 * Factorizer keeps track of previous integers it has factored in a static Hashtable, so that it can return their
 * factorizations instead of doing it anew.
 *
 * @author B F Schutz
 * @version 1.0 25 October 1999
 */

public class Factorizer {

    /**
     * The list of primes here can be enlarged if necessary, but it seems unlikely that anyone will want to do an FFT on
     * an array with a large prime factor, since this reduces to a time-consuming DFT.
     */
    static private int nprimes = 36;
    static private int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79,
            83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151};
    static double[] primesqrts = {1.414213562373, 1.7320508075688, 2.2360679774997, 2.6457513110645, 3.3166247903554,
            3.6055512754639, 4.1231056256176, 4.3588989435406, 4.7958315233127, 5.3851648071345, 5.56776436283,
            6.0827625302982, 6.4031242374328, 6.557438524302, 6.855654600401, 7.2801098892805, 7.6811457478686,
            7.8102496759066, 8.1853527718724, 8.4261497731763, 8.5440037453175, 8.8881944173155, 9.1104335791443,
            9.4339811320566, 9.8488578017961, 10.04987562112, 10.148891565092, 10.344080432788, 10.44030650891,
            10.630145812734, 11.269427669584, 11.445523142259, 11.704699910719, 11.789826122551, 12.206555615733,
            12.288205727444};

    /**
     * This hashtable remembers factorizations of the length n of the data set.  It will continue to be added to while
     * the program runs, if data sets with different values of n are used.
     */
    static private Hashtable factorDictionary = new Hashtable(100);

    /*
     * Private variable verbose to govern writing diagnostics to System.out.
     */
    private boolean verbose;

    /**
     * Set up elementary and often-used value of n in the factorDictionary 
     * when the class is instantiated for the first time, using a static 
     * initializer.
     */
    static {
        int j, m, r;

        /*
        Elementary unit-length array is the first entry in the dictionary.
      */
        Integer k = new Integer(1);
        int[] f = new int[1];
        f[0] = 1;
        factorDictionary.put(k, f);
        /*
        Enter all the stored primes (up to 151) into the dictionary.
      */
        for (j = 0; j < nprimes; j++) {
            k = new Integer(primes[j]);
            f = new int[1];
            f[0] = primes[j];
            factorDictionary.put(k, f);
        }
        /*
        Enter all powers of 2 up to 2^40 ~ 1e12 into the
        dictionary.
      */
        m = 2;
        for (j = 2; j < 40; j++) {
            m *= 2;
            k = new Integer(m);
            f = new int[j];
            for (r = 0; r < j; r++) {
                f[r] = 2;
            }
            factorDictionary.put(k, f);
        }
    }

    /**
     * Constructor for Factorizer sets the verbose flag, to determine whether comments are written to System.out.
     */
    public Factorizer(boolean information) {
        verbose = information;
    }

    private void display1(int[] r) {
        int size = r.length;
        System.out.println("Here are the factors:");
        for (int k = 0; k < size; k++) {
            System.out.println(String.valueOf(r[k]));
        }
        return;
    }


    /**
     * Method factorize returns an integer vector containing the prime factors of the integer argument n. Factors with
     * multiplicity greater than 1 are repeated elements of the vector. The method finds all the prime factors less than
     * or equal to the smaller of sqrt(n) and 151.  Any prime factor larger than 151 is not found, but the residual
     * quotient of n by all the found factors is returned as the last element of the vector. In this way, the product of
     * all the elements of the vector is n.
     * <p/>
     * If n is negative, the returned vector's first element is -1, then follow the factors of -n.
     *
     * @param n The number to be factored
     */
    public int[] factorize(int n) {
        int k, nfactors, rem, m;
        int[] hits = new int[nprimes];
        int hit = 0;
        int[] factors;
        double rootn;
        /*
        Handle the case where the argument is negative or zero.
      */
        if (n < 1) {
            if (n == 0) {
                factors = new int[1];
                factors[0] = 0;
                if (verbose) {
                    display1(factors);
                }
                return factors;
            } else {
                int[] lastFactors = factorize(-n);
                factors = new int[lastFactors.length + 1];
                factors[0] = -1;
                System.arraycopy(lastFactors, 0, factors, 1, lastFactors.length);
                if (verbose) {
                    display1(factors);
                }
                return factors;
            }
        }
        /*
         If n has been factored before, retrieve the factors from the hashtable
         and return.
      */
        Integer nObj = new Integer(n);
        if (factorDictionary.containsKey(nObj)) {
            if (verbose) {
                display1((int[]) factorDictionary.get(nObj));
            }
            return (int[]) factorDictionary.get(nObj);
        }
        /*
        Otherwise search for all factors up to the largest built-in prime in
        the list, or up to sqrt(n), whichever is smaller. If a factor
        can't be found up to sqrt(n), then n is prime. Do this recursively,
        re-defining n to be the quotient of n by the last factor that has
        been found.  Keep multiplicity in array hits[]. Total number of
        prime factors found is in variable hit.
      */
        rootn = Math.sqrt(n);
        k = 0;
        while ((n > 1) && (k < nprimes) && (primes[k] <= rootn)) {
            if (n % primes[k] == 0) {
                hits[k]++;
                hit++;
                n = n / primes[k];
                rootn = rootn / primesqrts[k];
            } else {
                k++;
            }
        }
        /*
         Return appropriate vectors of prime factors after storing them
         in the hashtable.
      */
        if (hit == 0) {
            factors = new int[1];
            factors[0] = n;
        } else {
            if (n == 1) {
                factors = new int[hit];
            } else {
                factors = new int[hit + 1];
            }
            m = 0;
            for (k = 0; k < nprimes; k++) {
                while (hits[k] > 0) {
                    factors[m] = primes[k];
                    hits[k]--;
                    m++;
                }
            }
            if (n > 1) {
                factors[m] = n;
            }
        }
        factorDictionary.put(nObj, factors);
        if (verbose) {
            display1(factors);
        }
        return factors;
    }

}


