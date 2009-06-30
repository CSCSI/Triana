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

import triana.types.util.Triplet;

/**
 * Histogramming is an interface that can be implemented by any
 * Triana data types that include data that represent
 * a histogram. The key point is that histograms differ from
 * other functions in that the histogram data is related to
 * $intervals^ of the independent variable(s), rather than
 * to single values of these variables. This means that the
 * independent data for a histogram must define the intervals,
 * and this is done by storing $delimiters^ as independent data.
 * </p><p>
 * Delimiters are the boundaries of the intervals that histogram
 * values refer to. The intervals are assumed to be contiguous,
 * so that a given delimiter is the right-hand boundary of one
 * interval and the left-hand boundary of the next higher interval.
 * Only the lowest and highest delimiters are boundaries of only
 * one interval. Delimiter values are assumed to be monotonically
 * increasing.
 * </p><p>
 * In each dimension there is one more delimiter value than
 * the number of intervals, so the number of independent data
 * values is one larger than the length of the corresponding
 * dimension of the dependent data array (histogram values).
 * The Histogramming interface provides methods to set and
 * access the extra independent data. It allows for the possibility
 * that the lowest or highest interval may be unbounded, so that
 * the first delimiter could have the value $Double.NEGATIVE_INFINITY^
 * or the last could be $Double.POSITIVE_INFINITY^.
 * </p><p>
 * The Histogramming interface does not specify
 * how a type should implement the storage of this data.
 * This is up to the programmer. In
 * the TrianaType called Histogram, which implements the
 * Histogramming interface for one-dimensional histograms,
 * the extra delimiter value is stored as a parameter.
 *
 * @see Histogram
 * @see GraphType
 * @see Signal
 * @see Spectral
 *
 * @author      Bernard Schutz
 * @created     26 August 2000
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public interface Histogramming {


    /**
     * Returns the whole set of delimiters for
     * the intervals of the histogram in the given dimension (independent
     * variable).  If the lowest interval is unbounded below,
     * then the first delimiter (first element of the array returned by
     * this method) should be $Double.NEGATIVE_INFINITY^, and if the
     * highest interval is unbounded above then the last element
     * should be $Double.POSITIVE_INFINITY^.
     *
     * @param dim The index of the independent variable being queried
     * @return The array of interval delimiters
     */
    public double[] getDelimiterArray(int dim);

    /**
     * Returns a Triplet that describes the
     * set of delimiters in the given dimension. A Triplet can only describe
     * a uniformly spaced set of numbers, so if the interval delimiters are
     * not uniform or if at least one end delimiter is infinite, then
     * this method should return null.
     *
     * @param dim The index of the independent variable being queried
     * @return The Triplet that can generate the interval delimiters
     */
    public Triplet getDelimiterTriplet(int dim);

    /**
     * Returns $true^ if the set of delimiters
     * in the given dimension is uniform, $false^ otherwise. This excludes
     * the possibility that either the lowest or the highest is infinite.
     *
     * @param dim The index of the independent variable being queried
     * @return True if the delimiters are uniformly spaced
     */
    public boolean isUniformDelimiterSet(int dim);

    /**
     * Sets the delimiters associated with the given
     * dimension to the given double[] array. The array's first and last
     * elements can be $Double.NEGATIVE_INFINITY^ or $Double.POSITIVE_INFINITY^,
     * respectively. The user is expected to check that the length of this
     * array is correct.
     *
     * @param del The array of delimiters
     * @param dim The index of the independent variable being queried
     */
    public void setDelimiters(double[] del, int dim);


    /**
     * Sets the delimiters associated
     * with the given dimension to the given Triplet.  The user is expected
     * to check that the length of this array is correct. This
     * method can only be used if there are no infinite end delimiters.
     *
     * @param del The array of delimiters
     * @param dim The index of the independent variable being queried
     */
    public void setDelimiters(Triplet del, int dim);


    /**
     * Returns $true^ if the first interval
     * in the given dimension is unbounded below, $i.e.^ if the first delimiter
     * is $Double.NEGATIVE_INFINITY^, and $false^ otherwise.
     *
     * @param dim The index of the independent variable being queried
     * @return True if the lowest interval reaches to negative infinity
     */
    public boolean isUnboundedIntervalBelow(int dim);


    /**
     * Returns $true^ if the last interval
     * in the given dimension is unbounded above, i.e. if the first delimiter
     * is $Double.POSITIVE_INFINITY^, and false otherwise.
     *
     * @param dim The index of the independent variable being queried
     * @return True if the highest interval reaches to positive infinity
     */
    public boolean isUnboundedIntervalAbove(int dim);


}








