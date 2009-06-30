/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */
package triana.types;

import triana.types.util.FlatArray;
import triana.types.util.Triplet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Histogram is a  data type that represents numerical values (the
 * dependent variable) that are assigned to <i>intervals</i> of
 * a single independent variable, rather than being assigned to
 * values of the independent variable, as in the case of functions.
 * It is typically used to count the number of times the value of a
 * variable falls within a certain interval (frequency histogram).
 * Histogram is a sub-class of VectorType that implements the Histogramming
 * interface.
 * </p><p>
 * The dependent variable of Histogram is the histogrammed data. The
 * independent variable is the set of <b>delimiters</b> of the intervals to which
 * the dependent values relate. The convention is that the dependent data
 * value whose index is <i>j</i> is defined on the interval between the
 * independent data values (delimiters) with indices <i>j</i> and <i>j + 1</i>.
 * The delimiters must be monotonically increasing but
 * they can define intervals of variable length.
 * /p><p>
 * The GraphType data model requires that the number of points in the
 * independent and dependent variable should be the same. For a histogram,
 * however, the highest delimiter (the upper end of the last interval)
 * must have an index value one larger than the last index of the
 * dependent data. To solve this problem, Histogram adopts
 * the convention that the independent variable stores all the delimiters
 * except the last one. This is stored in the parameter <i>lastDelimiter</i>.
 * </p><p>
 * Some histograms may be binned in such a way that the first or the last
 * interval might be infinite; this happens for example if the data point represents
 * the number of times a variable exceeds a certain value <i>x</i> (last interval
 * stretches from <i>x</i> to infinity). In such cases Histogram
 * stores the value Double.NEGATIVE_INFINITY in the first
 * delimiter, and/or the value Double.POSITIVE_INFINITY in the parameter
 * lastDelimiter, as appropriate.  Histogram implements tests to determine
 * if these infinite values are present, and it implements a method to
 * return finite values for such delimiters, in order to aid graphing the
 * data.
 *
 * @see VectorType
 * @see Histogramming
 * @see GraphType
 * @see triana.types.util.Triplet
 * @author Bernard Schutz
 * @created     22 October 2000
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class Histogram extends VectorType implements Histogramming, AsciiComm {

    /**
     * The parameter to contain the value of the highest delimiter. This
     * is extra to the delimiters held in the array of independent data.
     */
    private double lastDelimiter;


    /**
     * (Parameter that is kept for consistency with previous versions,
     * but which is obsolete. It must be kept to the right values by
     * constructors and <i>set...</i> methods.)
     * </p><p>
     * The bin-axis (independent variable) label. Must be kept
     * up-to-date with <i>GraphType.independentLabels</i>.
     */
    public String binLabel;

    /**
     * (Parameter that is kept for consistency with previous versions,
     * but which is obsolete. It must be kept to the right values by
     * constructors and <i>set...</i> methods.)
     * </p><p>
     * The h-axis (dependent variable) label. Must be kept
     * up-to-date with <i>GraphType.dependentLabels</i>.
     */
    public String hLabel;

    /**
     * (Parameter that is kept for consistency with previous versions,
     * but which is obsolete. It must be kept to the right values by
     * constructors and <i>set...</i> methods.)
     * </p><p>
     * The interval delimiter data. It is not identical with the GraphType
     * independent variable, since this array contains the last delimiter,
     * which is stored as the parameter <i>lastDelimiter</i>.
     */
    public double[] delimiters;

    /**
     * (Parameter that is kept for consistency with previous versions,
     * but which is obsolete. It must be kept to the right values by
     * constructors and <i>set...</i> methods.)
     * </p><p>
     * The histogram data. Must be kept up-to-date with the dependent variable.
     */
    public double[] data;

    /*
     * Constructors.
     */

    /**
     * Creates a new Histogram, setting its state to <i>false</i>
     */
    public Histogram() {
        super();
    }

    /**
     * Creates a Histogram with given delimiter array and value array.
     * The given delimiter array (first argument) should have one more
     * element than the values array (second argument), and should contain
     * all the delimiters. If one wants to specify the independent variable
     * array for the delimiters, use the next constructor.
     *
     * @param delims An array containing all the delimiters, including the last
     * @param values The array of histogram data
     */
    public Histogram(double[] delims, double[] values) {
        super();
        double[] d = new double[values.length];
        lastDelimiter = delims[delims.length - 1];
        System.arraycopy(delims, 0, d, 0, values.length);
        setData(values);
        setX(d);
    }

    /**
     * Creates a Histogram with given delimiter array, value array, and an
     * explicitly given last delimiter.
     * The given delimiter array (first argument) should have the same length
     * as the values array (second argument), and should contain
     * all the delimiters except the last. The final argument gives the
     * last delimiter. If one wants to specify
     * all the delimiters in one array, use the previous constructor.
     *
     * @param delims An array containing all the delimiters, including the last
     * @param values The array of histogram data
     * @param last The last (greatest) delimiter
     */
    public Histogram(double[] delims, double[] values, double last) {
        super();
        lastDelimiter = last;
        setData(values);
        setX(delims);
    }

    /**
     * (Obsolete constructor kept for compatibility with previous
     * version of Histogram. Will be removed in subsequent versions.)
     * </p><p>
     * Creates a new Histogram of a given size, with
     * given labels for the intervals and histogram values. It allocates
     * storage for the data vectors, but only  assigns values to
     * possible infinite end delimiters. This follows the obsolete algorithm
     * of the earlier version. First, if the number of delimiters <i>nB</i>
     * is one less than the number of histogram values <i>nH</i>, the
     * first interval is assumed to start at negative infinity and
     * the final one to end at positive infinity. Second, if <i>nB</i>
     * equals <i>nH</i>, then only the final interval is assumed infinite.
     * If <i>nB</i> is one larger than <i>nH</i> then all intervals are of finite
     * size. If <i>nB</i> and <i>nH</i> do not satisfy one of these conditions,
     * then an empty Histogram is created.
     *
     * @param nB the number of bin delimiter values.
     * @param nH the number of histogram values.
     * @param labelB the label for the bin values.
     * @param labelH the label for the histogram values.
     */
    public Histogram(int nB, int nH, String labelB, String labelH) {
        this();
        if (Math.abs(nH - nB) > 1)
            return; // maybe create an Error Dialog here or
        else {                             // have a TrianaTypeException thrown
            if (labelB != null) setHistogramBinLabel(labelB);
            if (labelH != null) setHistogramDataLabel(labelH);
            double[] delims = new double[nH + 1];
            if (nB <= nH) {
                delims[nH] = Double.POSITIVE_INFINITY;
                if (nB < nH) delims[0] = Double.NEGATIVE_INFINITY;
            }
            setData(new double[nH]);
            setDelimiters(delims, 0);
        }
    }

    /**
     * (Obsolete constructor kept for compatibility with previous version.
     * It will be removed in later versions.)
     * </p><p>
     * Creates a new Histogram for given values of
     * the histogram and bin labels, with references to data for the bin
     * delimiters and the histogram values, and with a <i>description</i>.
     * </p><p>
     * Normally the given delimiter array <i>bs</i> should have length
     * one greater than that of the given histogram array <i>hv</i>.
     * For compatibility with previous versions, however, <i>bs</i>
     * is also allowed to have length equal to or one less than that
     * of <i>hv</i>. The algorithm for constructing the delimiter set
     * is given in the previous constructor.
     *
     * @param labelB the bin label.
     * @param labelH the histogram axis label.
     * @param bs the bin-delimiter data in an array of doubles
     * @param hv the histogram data in an array of doubles
     */
    public Histogram(String labelB, String labelH, double[] bs, double[] hv) {
        this();
        int nH = hv.length;
        int nB = bs.length;
        int startcopy = 0;
        if (Math.abs(nH - nB) > 1)
            return; // maybe create an Error Dialog here or
        else {                             // have a TrianaTypeException thrown
            if (labelB != null) setHistogramBinLabel(labelB);
            if (labelH != null) setHistogramDataLabel(labelH);
            double[] delims = new double[nH + 1];
            if (nB <= nH) {
                delims[nH] = Double.POSITIVE_INFINITY;
                if (nB < nH) {
                    delims[0] = Double.NEGATIVE_INFINITY;
                    startcopy = 1;
                }
                System.arraycopy(bs, 0, delims, startcopy, bs.length);
            }
            else
                delims = bs;
            setDelimiters(delims, 0);
            setData(hv);
        }
    }

    /**
     * Added by I. Taylor, August 2001 : This function sets the default labeling
     * scheme used for all GraphTypes. Default values are 'X' for the X-axis and
     * 'Y' for the Y axis.  The various subclasses can override this function
     * with their specific axis-labelling conventions.
     */
    public void setDefaultAxisLabelling() {
        String labelx = "Value";
        String labely = "N";
        setIndependentLabels(0, labelx);
        setDependentLabels(0, labely);
    }

    /*
     * Usual <i>get..</i> and <i>set..</i> methods.
     */

    /**
     * Return value of <i>lastDelimiter</i> (the right-most delimiter).
     *
     * @return double the last delimiter
     */
    public double getLastDelimiter() {
        return lastDelimiter;
    }

    /**
     * Set value of <i>lastDelimiter</i> (the right-most delimiter) to the given value.
     *
     */
    public void setLastDelimiter(double ld) {
        lastDelimiter = ld;
    }

    /**
     * Return value of the first delimiter (the left-most delimiter).
     *
     * @return double The first delimiter
     */
    public double getFirstDelimiter() {
        double dl;
        if (isTriplet(0))
            dl = getXTriplet().getStart();
        else
            dl = (getXArray())[0];
        return dl;
    }


    /*
    * Methods that are required to be implemented by the Histogramming
    * inteface.
    */

    /**
     * Returns the whole set of delimiters (including the last one) for
     * the intervals of the histogram in the given dimension (independent
     * variable). Since there is only one dimension in this case, the
     * given value of <i>dim</i> is ignored.
     *
     * @param dim The index of the independent variable being queried,
     ignored here
     * @return The array of interval delimiters
     */
    public double[] getDelimiterArray(int dim) {
        int oneLess = size();
        double[] delim = new double[oneLess + 1];
        double[] lower = getXArray();
        System.arraycopy(lower, 0, delim, 0, oneLess);
        delim[oneLess] = lastDelimiter;
        return delim;
    }

    /**
     * Returns an entire set of delimiters in finite positions for
     * the intervals of the histogram in the given dimension (independent
     * variable). If the first and/or last delimiter is infinite, it is
     * replaced by a finite value chosen in such a way that the final
     * (or first) interval has the same width as the one adjacent to it.
     * This is useful for graphing purposes, where the infinite interval
     * must be displayed in a finite way.
     * Since there is only one dimension in this case, the
     * given value of <i>dim</i> is ignored.
     *
     * @param dim The index of the independent variable being queried,
     ignored here
     * @return The array of interval delimiters as finite numbers
     */
    public double[] getFiniteDelimiters(int dim) {
        double[] delim = getDelimiterArray(dim);
        int n = delim.length;
        if (delim[0] == Double.NEGATIVE_INFINITY) {
            if (n == 2)
                delim[0] = Math.min(2 * delim[1], 0.5 * delim[1]);
            else
                delim[0] = delim[1] * 2 - delim[2];
        }
        if (delim[n - 1] == Double.POSITIVE_INFINITY) {
            if (n == 2)
                delim[1] = Math.max(2 * delim[0], 0.5 * delim[0]);
            else
                delim[n - 1] = delim[n - 2] * 2 - delim[n - 3];
        }
        return delim;
    }

    /**
     * Returns a Triplet that describes the
     * set of delimiters in the given dimension. A Triplet can only describe
     * a uniformly spaced set of numbers, so if the interval delimiters are
     * not uniform this returns <i>null</i>. Since there is only one
     * dimension in this case, the given value of <i>dim</i> is ignored.
     *
     * @param dim The index of the independent variable being queried,
     ignored here
     * @return The Triplet that can generate the interval delimiters
     * @see triana.types.util.Triplet
     */
    public Triplet getDelimiterTriplet(int dim) {
        if ((!isUniformDelimiterSet(0))) return null;
        Triplet t = getXTriplet();
        t.setLength(t.getLength() + 1);
        return t;
    }

    /**
     * Returns <i>true</i> if the set of delimiters
     * in the given dimension is uniform, <i>false</i> otherwise. This excludes
     * the possibility that either the lowest or the highest is infinite.
     * Since there is only one dimension in this case, the given value of
     * <i>dim</i> is ignored.
     *
     * @param dim The index of the independent variable being queried,
     ignored here
     * @return <I>True</I> if the delimiters are uniformly spaced
     */
    public boolean isUniformDelimiterSet(int dim) {
        if ((!isUniform(0)) || (lastDelimiter == Double.POSITIVE_INFINITY) || (getFirstDelimiter() == Double.NEGATIVE_INFINITY)) return false;
        Triplet t = getXTriplet();
        double last = t.getLast();
        if ((lastDelimiter - last) != t.getStep()) return false;
        return true;
    }


    /**
     * Sets the independent variable data array and the parameter
     * <i>lastDelimiter</i> from the data held in the given array. If the
     * delimiters are uniformly spaced (apart possibly from the last)
     * then they will be stored as a Triplet and not an array. The
     * method sets the parameter <i>delimiters</i> to the correct reference.
     * This method should be used to set delimiters; <i>VectorType.setX</i> will
     * not work properly because it does not know about the delimiter
     * model for histograms, in particular the extra last delimiter.
     *
     * @param delims An array containing all the delimiters
     * @param dim The index of the independent variable being queried,
     ignored here
     * @see triana.types.util.Triplet
     */
    public void setDelimiters(double[] delims, int dim) {
        double[] d = new double[delims.length - 1];
        System.arraycopy(delims, 0, d, 0, d.length);
        lastDelimiter = delims[delims.length - 1];
        if (Triplet.testUniform(d)) {
            Triplet t = Triplet.convertToTriplet(d);
            super.setX(t);
        }
        else
            super.setX(d);
    }


    /**
     * Sets the delimiters associated with the given dimension to
     * values given by the given Triplet.  The user is expected
     * to check that the length of this array is correct (i.e. one more
     * than the length of the dependent data array). The highest value
     * of the Triplet is stored in <i>lastDelimiter</i>, and the remaining
     * values are kept as a new Triplet with a shorter length.
     *
     * @param del The Triplet that generates the array of delimiters
     * @param dim The index of the independent variable being queried, ignored in this case
     * @see triana.types.util.Triplet
     */
    public void setDelimiters(Triplet del, int dim) {
        lastDelimiter = del.getLast();
        del.setLength(del.getLength() - 1);
        setX(del);
    }

    /**
     * Returns <i>true</i> if the first interval
     * in the given dimension is unbounded below, i.e. if the first delimiter
     * is Double.NEGATIVE_INFINITY, and <i>false</i> otherwise.
     *
     * @param dim The index of the independent variable being queried
     * @return <I>True</I> if the lowest interval reaches to negative infinity
     */
    public boolean isUnboundedIntervalBelow(int dim) {
        return (getFirstDelimiter() == Double.NEGATIVE_INFINITY);
    }


    /**
     * Returns <i>true</i> if the last interval
     * in the given dimension is unbounded above, i.e. if the first delimiter
     * is Double.POSITIVE_INFINITY, and <i>false</i> otherwise.
     *
     * @param dim The index of the independent variable being queried
     * @return <I>True</I> if the highest interval reaches to positive infinity
     */
    public boolean isUnboundedIntervalAbove(int dim) {
        return (lastDelimiter == Double.POSITIVE_INFINITY);
    }



    /*
     * Methods that are required for compatibility with previous  versions
     * but which are obsolete and should be eliminated eventually.
     */

    /**
     * (Obsolete method required for compatibility with previous versions.)
     * </p><p>
     * This is identical to size() but provides a name that makes it
     * clear that the number being returned is the number of
     * histogram values, which is one less than the number of bin delimiters.
     *
     * @return the number of histogram values in this Histogram type.
     */
    public int hsize() {
        return size();
    }

    /**
     * (Obsolete method required for compatibility with previous versions.)
     * </p><p>
     * Returns the number of non-infinite bin delimiters.
     *
     * @return the number of bin delimiters in this Histogram type.
     */
    public int bsize() {
        return delimiters.length;
    }


    /**
     * (Obsolete method required for compatibility with previous versions.)
     * </p><p>
     * Resets the independent variable array to the array whose
     * reference is passed to it. This is identical to <i>setData</i>.
     *
     * @param hdata the new histogram data
     */
    public void resetHData(double[] hdata) {
        setData(hdata);
    }

    /**
     * (Obsolete method required for compatibility with previous versions.)
     * </p><p>
     * Sets the graph label for the histogram intervals.
     *
     * @param labelB The histogram bin axis label
     */
    public void setHistogramBinLabel(String labelB) {
        setIndependentLabels(0, labelB);
    }

    /**
     * (Obsolete method required for compatibility with previous versions.)
     * </p><p>
     * Sets the graph label for the histogram values.
     *
     * @param labelH The histogram value axis label
     */
    public void setHistogramDataLabel(String labelH) {
        setDependentLabels(0, labelH);
    }

    /*
     * Methods that override methods of higher types for Histogram data sets.
     */

    /**
     * Returns the independent data scaled the way they should be graphed.
     * For Histogram, this returns an array of delimiters, one longer
     * than the array of dependent data. This method simply returns the
     * value of <i>getFiniteDelimiters</i>, which return finite values for
     *  the delimiters even if
     * the histogram is formed with end intervals that are infinite in
     * length. The user should use other methods to check if the
     * true delimiters are infinite.
     *
     * @param dim The independent dimension under consideration
     * @return The scaled independent data values
     * @see #getFiniteDelimiters
     */
    public double[] getIndependentScaleReal(int dim) {
        return getFiniteDelimiters(dim);
    }

    /**
     * Returns null because the data is real.
     *
     * @param dim The independent dimension under consideration
     * @return The scaled independent data values
     */
    public double[] getIndependentScaleImag(int dim) {
        return null;
    }

    /**
     * Returns x-axis values for graphing a histogram with a standard
     * grapher that expects the same number of x-values as y-values.
     * The x-values returned are the midpoints of the histogram intervals,
     * after making them finite if one or both ends extend to infinity.
     *
     * @param dim The independent dimension under consideration
     * @return The array of independent data
     */
    public double[] getDelimiterMidpoints(int dim) {
        double[] dlms = getFiniteDelimiters(dim);
        int ldlms = dlms.length;
        double[] mids = new double[ldlms - 1];
        for (int j = 0; j < ldlms-1; j++ )
            mids[j] = 0.5*(dlms[j] + dlms[j+1]);
        return mids;
    }

    /**
     * Returns logarithmic x-axis values for graphing a histogram with a standard
     * grapher that expects the same number of x-values as y-values.
     * The x-values returned are the midpoints of the histogram intervals,
     * after making them finite if one or both ends extend to infinity.
     *
     * @param dim The independent dimension under consideration
     * @return The array of independent data
     */
    public double[] getDelimiterMidpointsLog10(int dim) {
        double[] mids = getDelimiterMidpoints(dim);
        double scaling = 1.0 / Math.log(10);
        for (int j = 0; j < mids.length; j++)
            mids[j] = Math.log(mids[j]) * scaling;
        return mids;
    }

    /*
     * Miscellaneous methods that override methods of VectorType.
     */

    /**
     * Extends the histogram data (dependent data)
     * to a longer set by padding with zeros. The given integer
     * argument gives the new length after padding, and the
     * boolean argument determines whether the padding is at
     * the front or the back.
     * </p><p>
     * If the new length is shorter than the old, nothing is done.
     * </p><p>
     * For a Histogram, the extra zeros must be accompanied by an
     * equal number of extra delimiters. If the zeros are to be added
     * at the beginning, then the first delimiter must be finite;
     * if it is NEGATIVE_INFINITY then the method returns without doing
     * anything and prints a warning. Otherwise, it simply uses the
     * extension method of VectorType. If the zeros are to be added
     * at the end, then the last delimiter must be finite;
     * if it is POSITIVE_INFINITY then the method returns without doing
     * anything and prints a warning. Otherwise, it must move the last
     * delimiter's position. It does this by artificially defining
     * a scratch VectorType data type to hold a set of one greater
     * size that can be interpolated in the usual way, and then
     * makes end adjustments.
     * </p><p>
     * Derived types should override this if necessary to provide
     * for the correct handling of parameters and other special
     * features.
     *
     * @param newLength The new length of the data set
     * @param front True if padding is at the front, false for padding at the back
     */
    public void extendWithZeros(int newLength, boolean front) {
        int oldsize = size();
        if (newLength <= oldsize) return;

        if (front) {
            if (getFirstDelimiter() == Double.NEGATIVE_INFINITY) {
                System.out.println("Warning: attempt to extend a Histogram at the front when its first delimiter is negative infinity. This is illegal. Data set unchanged.");
                return;
            }
            else
                super.extendWithZeros(newLength, front);
        }

        else {
            if (getLastDelimiter() == Double.POSITIVE_INFINITY) {
                System.out.println("Warning: attempt to extend a Histogram at the end when its last delimiter is infinity. This is illegal. Data set unchanged.");
                return;
            }
            else {
                double[] newX = getDelimiterArray(0);
                double[] newY = new double[newX.length];
                System.arraycopy(getData(), 0, newY, 0, size());
                newY[newX.length - 1] = 0;
                if (newLength == newX.length) {
                    setData(newY);
                    setX(newX);
                    setLastDelimiter(2 * newX[newX.length - 1] - newX[newX.length - 2]);
                }
                else {
                    VectorType scratch = new VectorType(newX, newY);
                    scratch.extendWithZeros(newLength, front);
                    setData(scratch.getData());
                    setX(scratch.getXArray());
                    setLastDelimiter(2 * getXArray()[size() - 1] - getXArray()[size() - 2]);
                }
            }
        }
    }


    /**
     * Overrides the method of VectorType, which inserts zeros between
     * existing elements of the data set, to do nothing at all in
     * Histogram. The operation does not make sense in terms of
     * the definition of the Histogram.
     *  </p><p>
     *
     * @param factor The number of zeros per data point to be inserted
     * @param before <i>True</i> if the zeros go before each point, <i>false</i> if after
     */
    public void interpolateZeros(int factor, boolean before) {
        System.out.println("Warning: attempt to interpolate zeros into a Histogram . This is illegal. Data set unchanged.");
        return;
    }


    /**
     * Tests the argument object to determine if
     * it makes sense to perform arithmetic operations between
     * it and the current object.
     * </p><p>
     * In Histogram, this method first tests for compatibility with superior
     * classes, and then (if the input object is a Histogram) tests that
     * the input has the same delimiters as the current object.
     * </p><p>
     * Classes derived from this should over-ride this method with further
     * tests as appropriate. The over-riding method should normally have the
     * first lines <PRE>
     *      boolean test = super.isCompatible( obj );
     * </PRE>followed by other tests. If other types
     * not subclassed from GraphType or Const should be allowed to be
     * compatible then other tests must be implemented.
     *
     * @param obj The data object to be compared with the current one
     * @return <I>True</I> if the object can be combined with the current one
     */
    public boolean isCompatible(TrianaType obj) {
        boolean test = super.isCompatible(obj);
        if ((test) && (obj instanceof Histogram)) test = FlatArray.equalArrays(getDelimiterArray(0), ((Histogram) obj).getDelimiterArray(0));
        return test;
    }


    /**
     * Determines whether the argument TrianaType is equal to
     * the current Histogram. They are equal if the argument is
     * a Histogram with the same size, parameters, and data.
     * </p><p>
     * This method must be over-ridden in derived types. In a derived
     * type called xxx the method should begin<PRE>
     *	     if ( !( obj instanceof xxx ) ) return false;
     *       if ( !isCompatible( obj ) ) return false;
     * </PRE>followed by tests that are specific to type xxx (testing its
     * own parameters) and then as a last line<PRE>
     * 	     return super.equals( obj );
     * </PRE>This line invokes the other equals methods up the chain to
     * GraphType. Each superior object tests its own parameters.
     *
     * @param obj The object being tested
     * @return <i>true</i> if they are equal or <i>false</i> otherwise
     */
    public boolean equals(TrianaType obj) {
        if (!(obj instanceof Histogram)) return false;
        if (!isCompatible(obj)) return false;
        if (lastDelimiter != ((Histogram) obj).getLastDelimiter()) return false;
        return super.equals(obj);
    }


    /**
     * This is one of the most important methods of Triana data.
     * types. It returns a copy of the type invoking it. This <b>must</b>
     * be overridden for every derived data type derived. If not, the data
     * cannot be copied to be given to other units. Copying must be done by
     * value, not by reference.
     * </p><p>
     * To override, the programmer should not invoke the <i>super.copyMe</i> method.
     * Instead, create an object of the current type and call methods
     * <i>copyData</i> and <i>copyParameters</i>. If these have been written correctly,
     * then they will do the copying.  The code should read, for type YourType:
     * <PRE>
     *        YourType y = null;
     *        try {
     *            y = (YourType)getClass().newInstance();
     *	          y.copyData( this );
     *	          y.copyParameters( this );
     *            y.setLegend( this.getLegend() );
     *            }
     *        catch (IllegalAccessException ee) {
     *            System.out.println("Illegal Access: " + ee.getMessage());
     *            }
     *        catch (InstantiationException ee) {
     *            System.out.println("Couldn't be instantiated: " + ee.getMessage());
     *            }
     *        return y;
     * </PRE>
     * </p><p>
     * The copied object's data should be identical to the original. The
     * method here modifies only one item: a String indicating that the
     * object was created as a copy is added to the <i>description</i>
     * StringVector.
     *
     * @return TrianaType Copy by value of the current Object except for an
     updated <i>description</i>
     */
    public TrianaType copyMe() {
        Histogram h = null;
        try {
            h = (Histogram) getClass().newInstance();
            h.copyData(this);
            h.copyParameters(this);
        }
        catch (IllegalAccessException ee) {
            System.out.println("Illegal Access: " + ee.getMessage());
        }
        catch (InstantiationException ee) {
            System.out.println("Couldn't be instantiated: " + ee.getMessage());
        }
        return h;
    }

    /**
     * Copies the data held in this object, by value not reference. It
     * over-rides the same method of GraphType, invoking that method and
     * then tidying up references to obsolete paramters. If these
     * are elimited in future versions, then this method can be eliminated.
     *
     * @param source The data object being copied
     */
    protected void copyData(TrianaType source) {
        super.copyData(source);
        double[] dl;
        if (isTriplet(0)) {
            Triplet t = getXTriplet();
            dl = new double[t.getLength() + 1];
            System.arraycopy(t.convertToArray(), 0, dl, 0, t.getLength());
        }
        else {
            double[] x = getXArray();
            dl = new double[x.length + 1];
            System.arraycopy(x, 0, dl, 0, x.length);
        }
        delimiters = dl;
    }

    /**
     * Copies parameters from the source to the current object. Many
     * of the statements keep the obsolete parameters up-to-date, so
     * they can be eliminated when these parameters are eliminated in
     * future versions.
     *
     * @param source The object from which the copy is made
     */
    protected void copyParameters(TrianaType source) {
        lastDelimiter = ((Histogram) source).getLastDelimiter();
        delimiters[delimiters.length - 1] = lastDelimiter; //Obsolete
        setIndependentLabels(0, ((Histogram) source).getIndependentLabels(0));
        setDependentLabels(0, ((Histogram) source).getDependentLabels(0));
    }

    /**
     * Used when Triana types want to be able to
     * send ASCII data to other programs using strings.  This is used to
     * implement socket and to run other executables, written in C or
     * other languages. With ASCII you don't have to worry about
     * ENDIAN'ness as the conversions are all done via text. This is
     * obviously slower than binary communication since you have to format
     * the input and output within the other program.
     * </p><p>
     * This method must be overridden in every subclass that defines new
     * data or parameters. The overriding method should first call<<PRE>
     *      super.outputToStream(dos)
     * </PRE>to get output from superior classes, and then new parameters defined
     * for the current subclass must be output. Moreover, subclasses
     * that first dimension their data arrays must explicitly transfer
     * these data arrays.
     *
     * @param dos The data output stream
     */
    public void outputToStream(PrintWriter dos) throws IOException {
        super.outputToStream(dos);
        dos.println(lastDelimiter);
    }


    /**
     * Used when Triana types want to be able to
     * receive ASCII data from the output of other programs.  This is used to
     * implement socket and to run other executables, written in C or
     * other languages. With ASCII you don't have to worry about
     * ENDIAN'ness as the conversions are all done via text. This is
     * obviously slower than binary communication since you have to format
     * the input and output within the other program.
     * </p><p>
     * This method must be overridden in every subclass that defines new
     * data or parameters. The overriding method should first call<PRE>
     *      super.inputFromStream(dis)
     * </PRE>to get input from superior classes, and then new parameters defined
     * for the current subclass must be input. Moreover, subclasses
     * that first dimension their data arrays must explicitly transfer
     * these data arrays.
     *
     * @param dis The data input stream
     */
    public void inputFromStream(BufferedReader dis) throws IOException {
        super.inputFromStream(dis);
        lastDelimiter = Double.valueOf(dis.readLine()).doubleValue();
        double[] dl;
        if (isTriplet(0)) {
            Triplet t = getXTriplet();
            dl = new double[t.getLength() + 1];
            System.arraycopy(t.convertToArray(), 0, dl, 0, t.getLength());
        }
        else {
            double[] x = getXArray();
            dl = new double[x.length + 1];
            System.arraycopy(x, 0, dl, 0, x.length);
        }
        delimiters = dl;
        delimiters[delimiters.length - 1] = lastDelimiter;
    }

    /*
     * Used to make the new
     * types derived from TrianaType backward-compatible with
     * older types. It must be called by any method that
     * modifies data in <i>dataContainer</i> or in any other variable
     * that replaces a storage location used previously by any
     * type. It must be implemented (over-ridden) in any type
     * that re-defines storage or access methods to any
     * variable. The implementation should assign the
     * new variables to the obsolete ones, and ensure that
     * obsolete access methods retrieve data from the new
     * locations. Any over-riding method should finish
     * with the line<PRE>
     *       super.updateObsoletePointers;
     * </PRE>
     */
    protected void updateObsoletePointers() {
        binLabel = getIndependentLabels(0);
        hLabel = getDependentLabels(0);
        if ((data = getDataReal()) != null) {
            double[] d = new double[data.length + 1];
            if (isTriplet(0)) {
                System.arraycopy(getIndependentTriplet(0).convertToArray(), 0, d, 0, data.length);
                d[data.length] = lastDelimiter;
            }
            else {
                System.arraycopy(getIndependentArrayReal(0), 0, d, 0, data.length);
                d[data.length] = lastDelimiter;
            }
            delimiters = d;
        }
        super.updateObsoletePointers();
    }

}




