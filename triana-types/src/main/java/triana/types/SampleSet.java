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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * SampleSet stores a real double[] array or two real double[] arrays
 * (representing a complex array) by extending the VectorType class
 * to implement the Signal interface.  This requires two further parameters
 * for the sampling frequency and the acquisition time. The class allows
 * for irregularly sampled data, for which it sets the sampling frequency
 * to zero.
 * </p><p>
 *
 * @see ComplexSampleSet
 * @see ComplexSpectrum
 * @see Spectrum
 * @see VectorType
 * @see triana.types.util.Triplet
 * @author      Ian Taylor
 * @author      Bernard Schutz
 * @created     09 January 2001
 * @revised     2003/07/07 by B F Schutz
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class SampleSet extends VectorType implements AsciiComm, Signal {
    /*
     * Class SampleSet is a data class holding only real data of
     * the type VectorType, and implementing the Signal interface.
     *
     * New parameters are:
     */

    /**
     * The sampling frequency (in samples per second, or Hz),
     * which should be set to zero for irregular sampling.
     */
    private double samplingRate;

    /**
     * The time of acquisition of the first
     * element of the data set. It is measured in seconds from the zero
     * of time, which must be externally defined by the experiment.
     */
    private double acquisitionTime;

    /**
     * (Parameter that is kept for consistency with previous versions,
     * but which is obsolete. It must be kept to the right values by
     * the <i>updateObsoletePointers</i> method and/or constructors and
     * <i>set...</i> methods.)
     * </p><p>
     * A synonym for the array stored in the
     * Hashtable <i>dataContainer</i> and accessed by method <i>getDataReal</i>.
     */
    public double[] data;

    /**
     * (Parameter that is kept for consistency with previous versions,
     * but which is obsolete. It must be kept to the right values by
     * the <i>updateObsoletePointers</i> method and/or constructors and
     * <i>set...</i> methods.)
     * </p><p>
     * A synonym for <i>samplingRate</i>.
     */
    public double samplingFrequency;

    /**
     * Constructors
     */

    /**
     * Creates an empty SampleSet.
     */
    public SampleSet() {
        super();
    }

    /**
     * Creates a new SampleSet with the given sampling frequency and number
     * of samples. It allocates memory for the data but does not initialise it.
     * Use method <i>initialiseData</i> to set all elements to zero if necessary.
     * This Constructor also sets the Triplet for the independent variable
     * to generate data starting at 0 with step <i>1/samplingFrequency</i>, which
     * is the sampling interval.
     *
     * @param sf The sampling frequency in Hz
     * @param samples The number of samples
     * @see #setDataArrayReal
     * @see #initialiseData
     * @see triana.types.util.Triplet
     */
    public SampleSet(double sf, int samples) {
        this(sf, samples, true);
    }

    /**
     * Creates a new real-valued SampleSet with the given sampling frequency and number
     * of samples. It allocates memory for the data but does not initialise it.
     * Use method <i>initialiseData</i> to set all elements to zero if necessary.
     * This Constructor also sets the Triplet for the independent variable
     * to generate data starting at 0 with step <i>1/samplingFrequency</i>, which
     * is the sampling interval.
     *
     * @param sf The sampling frequency in Hz
     * @param samples The number of samples
     * @param allocateMem if true, allocates the memory for the array
     * @see #setDataArrayReal
     * @see #initialiseData
     * @see triana.types.util.Triplet
     */
    public SampleSet(double sf, int samples, boolean allocateMem) {
        this();
        samplingRate = sf;
        if (allocateMem)
            setData(new double[samples]);
        if (sf > 0) {
            setX(new Triplet(samples, 0, 1 / sf));
        }
        else {
            setX(new double[samples]);
        }
    }

    /**
     * Creates a new real-valued SampleSet with given sampling times.
     * It allocates memory for the data but does not initialise it.
     * Use <i>initialiseData</i> to set all elements to zero if necessary.
     * If the data are not uniform, the constructor sets the sampling
     * rate to zero as a signal that the data are irregularly sampled.
     *
     * @param samplingTimes The sampling times array
     * @see #setDataArrayReal
     * @see #initialiseData
     */
    public SampleSet(double[] samplingTimes) {
        this();
        if (Triplet.testUniform(samplingTimes)) {
            samplingRate = 1.0 / (samplingTimes[1] - samplingTimes[0]);
        }
        else {
            samplingRate = 0;
        }
        setData(new double[samplingTimes.length]);
        setX(samplingTimes);
    }

    /**
     * Creates a new real-valued SampleSet with the given sampling frequency,
     * number of samples, and data acquisition time.
     * It allocates memory for the data but does not initialise it.
     * Use method <i>initialiseData</i> to set all elements to zero if necessary.
     * This Constructor also sets the Triplet for the independent variable
     * to generate data starting at the acquisition time and with step
     * <i>1/samplingFrequency</i>, which is the sampling interval.
     *
     * @param sf The sampling frequency in Hz
     * @param samples The number of samples
     * @param acquisitionTime The time of the first sample, in seconds
     * @see #setDataArrayReal
     * @see #initialiseData
     * @see triana.types.util.Triplet
     */
    public SampleSet(double sf, int samples, double acquisitionTime) {
        this(sf, samples, true);
        setAcquisitionTime(acquisitionTime);
        if (sf > 0) setX(new Triplet(samples, acquisitionTime, 1 / sf));
    }

    /**
     * Creates a new real-valued SampleSet with the given sampling times and data
     * acquisition time. It allocates memory for the sampled data but
     * does not initialise it. Use method <i>initialiseData</i> to set all elements
     * to zero if necessary. If the sampling times are not uniform, the
     * sampling rate is set to zero to indicate
     * that the data are irregularly sampled.
     *
     * @param samplingTimes The sampling times array, in seconds
     * @param acquisitionTime The time of the first sample, in seconds
     * @see #setDataArrayReal
     * @see #initialiseData
     */
    public SampleSet(double[] samplingTimes, double acquisitionTime) {
        this(samplingTimes);
        setAcquisitionTime(acquisitionTime);
    }

    /**
     * Creates a new real-valued SampleSet with the given sampling frequency
     * and array of sampled data. This Constructor also sets the Triplet for
     * the independent variable to generate data starting at 0 with step
     * <i>1/samplingFrequency</i>, which is the sampling interval.
     *
     * @param sf The sampling frequency in Hz
     * @param d The sampled data
     * @see triana.types.util.Triplet
     */
    public SampleSet(double sf, double[] d) {
        this(sf, d.length, false);
        setData(d);
    }

    /**
     * Creates a new real-valued SampleSet with the given sampling times and
     * array of sampled data. If the sampling times are not uniformly spaced,
     * the Constructor sets the sampling rate to zero
     * as a signal that the data are irregularly sampled.
     *
     * @param samplingTimes The sampling times array in seconds
     * @param d The sampled data
     */
    public SampleSet(double[] samplingTimes, double[] d) {
        this(samplingTimes);
        setData(d);
    }

    /**
     * Creates a new real-valued SampleSet with the given sampling frequency, the
     * array of sampled data, and the acquisition time of the first sample.
     * This Constructor also sets the Triplet for the independent variable
     * to generate data starting at the acquisition time and with step
     * <i>1/samplingFrequency</i>, which is the sampling interval.
     *
     * @param sf The sampling frequency in Hz
     * @param d The sampled data
     * @param acquisitionTime The time of the first sample, in seconds
     * @see triana.types.util.Triplet
     */
    public SampleSet(double sf, double[] d, double acquisitionTime) {
        this(sf, d);
        setAcquisitionTime(acquisitionTime);
        if (sf > 0) setX(new Triplet(d.length, acquisitionTime, 1 / sf));
    }

    /**
     * Creates a new real-valued SampleSet with the given sampling times, data array,
     * and data acquisition time. If the data are not uniformly sampled,
     * the Constructor sets the sampling frequency to zero as a signal
     * that the data are irregularly sampled.
     *
     * @param samplingTimes The sampling times in seconds
     * @param d The sampled data
     * @param acquisitionTime The time of the first sample, in seconds
     */
    public SampleSet(double[] samplingTimes, double[] d, double acquisitionTime) {
        this(samplingTimes, d);
        setAcquisitionTime(acquisitionTime);
    }

    /**
     * Creates a new complex-valued SampleSet with a certain sampling frequency and
     * arrays containing the sampled data. This Constructor also sets the
     * Triplet for the independent variable to generate data starting at 0
     * with step 1/samplingFrequency, which is the sampling interval.
     *
     * @param sf The sampling frequency
     * @param dr The real part of the sampled data
     * @param di The imaginary part of the sampled data
     * @see triana.types.util.Triplet
     */
    public SampleSet(double sf, double[] dr, double[] di) {
        this(sf, dr.length);
        setData(dr, di);
    }

    /**
     * Creates a new complex-valued SampleSet with the given sampling times and
     * arrays containing the sampled data. If the sampling times are not
     * uniformly spaced, the
     * Constructor sets the sampling rate to zero
     * as a signal that the data are irregularly sampled.
     *
     * @param samplingTimes The sampling times array
     * @param dr The real part of the sampled data
     * @param di The imaginary part of the sampled data
     */
    public SampleSet(double[] samplingTimes, double[] dr, double[] di) {
        this(samplingTimes);
        setData(dr, di);
    }

    /**
     * Creates a new complex-valued SampleSet with a certain sampling frequency, the
     * arrays of sampled data, and the acquisition time of the first sample.
     * This Constructor also sets the Triplet for the independent variable
     * to generate data starting at the acquisition time and with step
     * <i>1/samplingFrequency</i>, which is the sampling interval.
     *
     * @param sf The sampling frequency
     * @param dr The real part of the sampled data
     * @param di The imaginary part of the sampled data
     * @param acquisitionTime The time of the first sample, in seconds
     * @see triana.types.util.Triplet
     */
    public SampleSet(double sf, double[] dr, double[] di, double acquisitionTime) {
        this(sf, dr, di);
        setAcquisitionTime(acquisitionTime);
        if (sf > 0) setX(new Triplet(dr.length, acquisitionTime, 1 / sf));
    }

    /**
     * Creates a new complex-valued SampleSet with the given sampling times,
     * data arrays, and data acquisition time. If the data are not
     * uniformly sampled,
     * the Constructor sets the sampling frequency to zero as a signal
     * that the data are irregularly sampled.
     *
     * @param samplingTimes The sampling times
     * @param dr The real part of the sampled data
     * @param di The imaginary part of the sampled data
     * @param acquisitionTime The time of the first sample, in seconds
     */
    public SampleSet(double[] samplingTimes, double[] dr, double[] di, double acquisitionTime) {
        this(samplingTimes, dr, di);
        setAcquisitionTime(acquisitionTime);
    }



    /**
     * Creates a real-valued SampleSet from a ComplexSampleSet by keeping only the
     * real part of the data. The second argument allows the user to
     * choose whether the new data are copied from the old or simply
     * a reference to the old.
     *
     * @param css The input data set
     * @param copy True if the new data are to be copied from the old, false if passed by reference
     */
    public SampleSet(ComplexSampleSet css, boolean copy) {
        this(css.getSamplingRate(), css.size(), css.getAcquisitionTime());
        if (copy) {
            if (css.isTriplet())
                this.setX(css.getXTriplet().copy());
            else {
                double[] oldX = css.getXArray();
                double[] newX = new double[oldX.length];
                System.arraycopy(oldX, 0, newX, 0, oldX.length);
                this.setX(newX);
            }
            double[] oldData = css.getDataReal();
            double[] newData = new double[oldData.length];
            System.arraycopy(oldData, 0, newData, 0, oldData.length);
            this.setDataReal(newData);
        }
        else {
            if (css.isTriplet())
                this.setX(css.getXTriplet());
            else
                this.setX(css.getXArray());
            this.setDataReal(css.getDataReal());
        }
    }

    /**
     * Creates a real-valued SampleSet from a ComplexSampleSet by keeping only the
     * real part of the data. The new data are  simply
     * a reference to the old.
     *
     * @param css The input data set
     */
    public SampleSet(ComplexSampleSet css) {
        this(css, false);
    }

    /**
     * Added by I. Taylor, August 2001 : This function sets the default labeling
     * scheme used for the axis by this data type. All constructors call this
     * function to set default values for the axis.
     */
    public void setDefaultAxisLabelling() {
        String labelx = "Time (secs)";
        String labely = "Amplitude";
        setIndependentLabels(0, labelx);
        setDependentLabels(0, labely);
    }

    /**
     * Implement methods of the Signal interface
     */

    /**
     * Returns the sampling frequency of the data. If
     * the data are irregularly sampled it returns 0.
     *
     * @return double The sampling frequency
     */
    public double getSamplingRate() {
        if (isUniform()) return samplingRate;
        return 0;
    }

    /**
     * Sets the sampling rate of the signal and
     * adjusts the data for the independent variable accordingly.
     * If the sampling frequency argument <i>r</i> is negative, nothing is done.
     * If it is zero, irregular sampling is assumed. If positive, the
     * sampling frequency is set to <i>r</i> and the values of the independent
     * variable are re-adjusted accordingly if necessary, assuming the
     * same starting time as before the change in the rate. This method
     * also writes the new sampling frequency into the <i>description</i>
     * StringVector and calls method <i>updateObsoletePointers</i>. If
     * there is an audio format, it is updated with the new sampling rate.
     *
     * @param r The sampling rate
     */
    public void setSamplingRate(double r) {
        if (r < 0) return;
        samplingRate = r;
        if (r > 0) {
            if (isTriplet(0))
                getXTriplet().setStep(1 / r);
            else {
                double[] x = getXArray();
                setX(new Triplet(x.length, x[0], 1 / r));
            }
        }
        else {
            if (isTriplet(0)) setX((Triplet) null);
        }
        this.updateObsoletePointers();
    }

    /**
     * Returns the acquisition time as a double
     * giving the number of seconds since the reference time, which
     * should be the same reference time as for the method <i>setAcquisitionTime</i>.
     * The time set is interpreted as the moment of acquisition
     * of the first sample in the data set.
     *
     * @return double The time of acquisition in seconds since the reference time
     */
    public double getAcquisitionTime() {
        return acquisitionTime;
    }

    /**
     * Sets the acquisition time. This
     * method also writes the acquisition time into the <i>description</i>
     * StringVector. If the given acquisition time is not equal
     * to the starting time of the independent variable, all values
     * of the independent variable are shifted by the difference
     * between the given time and the previous starting time.
     *
     * @param t The acquisition time in seconds
     */
    public void setAcquisitionTime(double t) {
        double[] d;
        int j;
        double shift;
        if (isTriplet(0))
            getXTriplet().setStart(t);
        else {
            d = getXArray();
            shift = t - d[0];
            if (shift != 0) for (j = 0; j < d.length; j++) d[j] += shift;
        }
        acquisitionTime = t;
    }


    /**
     * Methods that are part of Version 1 of SampleSet and are kept for
     * consistency, but which are obsolete and should be eliminated
     */


    /**
     * Obsolete synonym for <i>getSamplingRate</i>.
     *
     * @return double The sampling frequency in Hz
     */
    public double samplingFrequency() {//Obsolete: replaced by getSamplingRate()
        return getSamplingRate();
    }

    /**
     * Obsolete synonym for <i>setSamplingRate</i>.
     *
     * @param s The new sampling frequency in Hz
     */
    public void setSamplingFrequency(double s) {//Obsolete. Replaced by setSamplngRate;
        setSamplingRate(s);
    }

    /**
     * Obsolete synonym for <i>size</i>.
     *
     * @return int The number of samples in this SampleSet
     */
    public int samples() {//Obsolete. Replaced by size()
        return size();
    }


    /**
     * Adds zeros between existing data values using the method of
     * the same name in VectorType. Adjusts<i>samplingRate</i>
     * accordingly. If  added before the first sample, then it
     * adjusts the value of <i>acquisitionTime</i> so that it
     * gives the time of the first zero if the previous
     * first data point is acquired at the previous value
     * of <i>acquisitionTime</i>.
     */
    public void interpolateZeros(int factor, boolean before) {
        if (factor <= 0) return;
        super.interpolateZeros(factor, before);
        setSamplingRate(getSamplingRate() * (factor + 1));
        if (before) setAcquisitionTime(getAcquisitionTime() - factor / getSamplingRate());
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
        SampleSet s = null;
        try {
            s = (SampleSet) getClass().newInstance();
            s.copyData(this);
            s.copyParameters(this);
        }
        catch (IllegalAccessException ee) {
            System.out.println("Illegal Access: " + ee.getMessage());
        }
        catch (InstantiationException ee) {
            System.out.println("Couldn't be instantiated: " + ee.getMessage());
        }
        return s;
    }


    /**
     * Copies modifiable parameters from the argument object
     * to the current object. The copying is by value, not by
     * reference. Parameters are defined as data not held in
     * <i>dataContainer</i>. They are modifiable if they have
     * <i>set...</i> methods. Parameters that cannot be modified, but
     * which are set by constructors, should be placed correctly
     * into the copied object when it is constructed.
     * </p><p>
     * In SampleSet, only the parameters <i>samplingRate</i>, <i>acquisitionTime</i>,
     * and <i>uniformlySampled</i> need to be copied. The obsolete parameter
     * <i>samplingFrequency</i> is generated automatically when the other
     * parameters are copied and set.
     * </p><p>
     * This must be overridden by any subclass that defines new parameters.
     * The overriding method should invoke its super method. It should use
     * the <i>set...</i> and <i>get...</i> methods for the parameters in question.
     * This method is protected so that it cannot be called except by
     * objects that inherit from this one. It is called by <i>copyMe</i>.
     *
     * @param source Data object that contains the data to be copied.
     */
    protected void copyParameters(TrianaType source) {
        super.copyParameters(source);
        setSamplingRate(((SampleSet) source).getSamplingRate());
        setAcquisitionTime(((SampleSet) source).getAcquisitionTime());
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
        dos.println(getSamplingRate());
        dos.println(getAcquisitionTime());
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
        this.setSamplingRate((Double.valueOf(dis.readLine())).doubleValue());
        this.acquisitionTime = (Double.valueOf(dis.readLine())).doubleValue();
    }

    /**
     * Tests the argument object to determine if
     * it makes sense to perform arithmetic operations between
     * it and the current object.
     * </p><p>
     * In SampleSet, this method first tests for compatibility with superior
     * classes, and then (if the input object is a SampleSet) tests that
     * the input has the same sampling rate as the current object.
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
        if ((test) && (obj instanceof SampleSet)) test = (getSamplingRate() == ((SampleSet) obj).getSamplingRate());
        return test;
    }


    /**
     * Determines whether the argument TrianaType is equal to
     * the current SampleSet. They are equal if the argument is
     * a SampleSet with the same size, parameters, and data.
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
     * </p><p>
     * @param obj The object being tested
     * @return <i>true</i> if they are equal or <i>false</i> otherwise
     */
    public boolean equals(TrianaType obj) {
        if (!(obj instanceof SampleSet)) return false;
        if (!isCompatible(obj)) return false;
        if (getAcquisitionTime() != ((SampleSet) obj).getAcquisitionTime()) return false;
        return super.equals(obj);
    }

    /*
     * Method updateObsoletePointers is used to make the new
     * types derived from TrianaType backward-compatible with
     * older types. It must be called by any method that
     * modifies data in dataContainer or in any other variable
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
        samplingFrequency = samplingRate;
        data = getDataReal();
        super.updateObsoletePointers();
    }
}










