package signalproc.algorithms;

/*
/*
 * Copyright (c) 1995 - 1998 University of Wales College of Cardiff
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


import java.util.ArrayList;

import jfftw.Plan;
import triana.types.ComplexSampleSet;
import triana.types.ComplexSpectrum;
import triana.types.GraphType;
import triana.types.MatrixType;
import triana.types.OldUnit;
import triana.types.SampleSet;
import triana.types.Signal;
import triana.types.Spectral;
import triana.types.Spectrum;
import triana.types.Spectrum2D;
import triana.types.VectorType;
import triana.types.audio.MultipleAudio;
import triana.types.util.SigAnalWindows;
import triana.types.util.Triplet;

/**
 * A FFT unit to perform a Fast Fourier Transform on the input data.
 * <p/>
 * If the input implements the Signal Interface, then signal parameters (sampling rate, etc) are used to produce a
 * correctly normalized spectrum, ie an approximation to the continuous FT. If the input data are real, then the output
 * are one-sided. The output is a ComplexSpectrum data type.
 * <p/>
 * If the input data implements Spectral, then an inverse FFT is performed. If the data are one-sided, the the output is
 * a SampleSet. If the data are two-sided, then the output is a ComplexSampleSet. If the data are narrow-band, they are
 * converted to full bandwidth (padding with zeroes) before the inverse transform is applied. In each case, the
 * normalization is based on the data in the Spectral data set.
 * <p/>
 * If the input data does not implement the Spectral or Signal Interfaces, then it is assumed to require a direct
 * (forward) FFT, and the output is a ComplexSpectrum data set with no normalization applied. This is the same as for an
 * input SampleSet with sampling frequency = 1.
 * <p/>
 * If this automatic behavior is not desired, then use the Units DirectFFT and InverseFFT instead. These Units always
 * perform the FFT in the indicated direction and apply no normalization, not even the 1/N. Their output is always a
 * VectorType (real or complex as appropriate) and the data are always two-sided.
 * <p/>
 * If the input data are not uniformly sampled, then an error is generated and no output takes place.
 * <p/>
 * The FFT is performed using the FFTC algorithm.
 *
 * @author Ian Taylor
 * @author B F Schutz
 * @author Rob Davies
 * @author Craig Robinson
 * @version 2.11 09 March 2001
 * @see FFTC
 * @see Spectral
 * @see Signal
 * @see ComplexSpectrum
 * @see Spectrum
 * @see SampleSet
 * @see ComplexSampleSet
 */
public class FFTW extends OldUnit {

    String style = "Automatic";
    String WindowFunction = "(none)";
    boolean padding = false;
    //added by Rob 30/06/03
    //don't need for fftw
    //String opt = "MaximumSpeed";
    FFTC fft;

    //variables added Rob 01/07/03
    Plan createdPlan;
    int planLength;
    boolean planForward;
    FFTWLibrary fftwlib;
    String planType = "Estimate";
//    private String wisdomFile = System.getProperty("user.home") + System.getProperty("file.separator") + "fftw.wisdom";

    /**
     * ********************************************* ** A Java FFT algorithm    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType result = null;
        GraphType input;

        int points = 1;
        int points0 = 1;
        int kk, j;
        int targetN = 1;
        double sf = 0;
        double sf0 = 0;
        double maxFreq = 0.5;
        double maxFreq0 = 0.5;
        boolean applyNorm = true;
        double norm = 1;
        double norm0 = 1;
        double resolution = 1;
        double resolution0 = 1;
        boolean direct = true;
        boolean oneSide = true;
        boolean conjugateSymmetric = false;
        boolean conjugateAntisymmetric = false;
        int conjugateAnswer = 0;
        //added by Rob 30/06/03
        //don't need for fftw
        //boolean speed = opt.equals("MaximumSpeed");
        double[] dataR = null;
        double[] dataI = null;
        double[] real = null;
        double[] imag = null;
        double[][] mdataR = null;
        double[][] mdataI = null;
        double[][] mreal = null;
        double[][] mimag = null;
        Class inClass = null;
        Class[] inClasses = null;
        int numberOfChannels = 1;
        ArrayList dataList = null;
        int[] chSize = null;
        String acqTime = "";
        int typeInt;

        if (planType.equals("Estimate")) {
            typeInt = jfftw.Plan.ESTIMATE | jfftw.Plan.USE_WISDOM;
        } else {
            typeInt = jfftw.Plan.MEASURE | jfftw.Plan.USE_WISDOM;
        }

        input = (GraphType) getInputNode(0);

        if (input instanceof MultipleAudio) {
            MultipleAudio au = (MultipleAudio) input;
            double[] data = (double[]) au.getDataArrayRealAsDoubles(0);
            input = new SampleSet(au.getAudioChannelFormat(0).getSamplingRate(), data);
        }

        boolean oneD = (input instanceof VectorType);
        boolean twoD = (input instanceof MatrixType);
        boolean multipleOneD = (input instanceof MultipleAudio);
        boolean inputSignal = (input instanceof Signal);

        if (inputSignal) {
            acqTime = (new StringBuffer("FFT: Acquistion time of first sample of data from which output is built = "))
                    .append(((Signal) input).getAcquisitionTime()).toString();
        }


        /* Debugging output
      println("Got input." );
      System.out.print("It is of type ");
      if ( input instanceof Signal ) println("Signal.");
      else if (input instanceof Spectral ) println("Spectral. It contains parameter nFull = " + String.valueOf(((Spectral)input).getOriginalN(0) ) );
      */

        if (padding) {
            if (oneD) {
                points = ((VectorType) input).size();
                targetN = 1;
                while (targetN < points) {
                    targetN *= 2;
                }
                if (targetN > points) {
                    ((VectorType) input).extendWithZeros(targetN, false);
                }
            } else if (twoD) {
                // padding not yet implemented for matrices
            } else if (multipleOneD) {
                // padding not yet implemented in MultipleAudio
            }
        }

        // implement windowing only for one-dimensional transforms of single vectors
        if (oneD && !WindowFunction.equals("(none)")) {
            input = (VectorType) SigAnalWindows.applyWindowFunction((VectorType) input, WindowFunction, true);
        }

        //	println("Got past window function.");
        boolean inputComplex = input.isDependentComplex(0);
        if (oneD) {
            inClass = input.getDataArrayClass(0);
            dataR = ((VectorType) input).getDataReal();
            dataI = null;
            if (inputComplex) {
                dataI = ((VectorType) input).getDataImag();
            }
            //change by Rob 07/07/03
            if (inputComplex) {
                //may not be, maybe points = dataR.length + dataI.lentgh - 1;
                //points = dataR.length + dataI.length - 2;
                points = FFTWLibrary.mergeLength(dataR, dataI);
            } else {
                points = ((VectorType) input).size();
            }
            //end change by Rob
        } else if (twoD) {
            inClass = input.getDataArrayClass(0);
            mdataR = ((MatrixType) input).getDataReal();
            mdataI = null;
            if (inputComplex) {
                mdataI = ((MatrixType) input).getDataImag();
            }
            points = (((MatrixType) input).size())[1];
            points0 = (((MatrixType) input).size())[0];
        } else if (multipleOneD) {
            numberOfChannels = input.getDependentVariables();
            inClasses = new Class[numberOfChannels];
            dataList = new ArrayList(numberOfChannels);

            dataR = (double[]) ((MultipleAudio) input).getDataArrayReal(0);
            dataI = null;
            points = ((MultipleAudio) input).getChannelLength(0);

/*		for ( int ch = 0; ch < numberOfChannels; ch++ ) {
            inClasses[ ch ] = input.getDataArrayClass( ch );
            dataList.add( ((MultipleAudio)input).getDataArrayReal( ch ) );
            chSize[ ch ] = ((MultipleAudio)input).getChannelLength( ch );
        } */
        }

        //	println("Got size = " + String.valueOf( points ) );

        if (style.equals("Direct")) {
            applyNorm = false;
            direct = true;
            oneSide = false;
            if (oneD) {
                //added by Rob 30/06/03
                if (!inputComplex) {
                    oneSide = true;
                    System.out.println("creating real plan");
                    createdPlan = fftwlib.realPlan(dataR.length, true, typeInt);
                } else {
                    //todo : check if this dimension is correct!!!
                    createdPlan = fftwlib.complexPlan(dataR.length, true, typeInt);
                }
                //end of changes by Rob
                sf = 1.0 / ((VectorType) input).getXTriplet().getStep();
            }
            if (twoD) {
                //added Rob 01/07/03
                //todo : check that these plans are created properly!!!
                if (!inputComplex) {
                    createdPlan = fftwlib.realNdPlan(new int[]{mdataR.length}, true, typeInt);
                } else {
                    createdPlan = fftwlib.complexNdPlan(new int[]{mdataR.length}, true, typeInt);

                }
                //end Rob
                sf = 1.0 / ((MatrixType) input).getXorYTriplet(1).getStep();
                sf0 = 1.0 / ((MatrixType) input).getXorYTriplet(0).getStep();
                resolution0 = sf0 / points0;
                maxFreq0 = sf0 / 2.0;
            }
            resolution = sf / points;
            maxFreq = sf / 2.0;
        } else if (style.equals("Direct/normalized(1/N)")) {
            applyNorm = true;
            direct = true;
            norm = 1. / points;
            oneSide = false;
            if (oneD) {
                //added by Rob 07/07/03
                if (!inputComplex) {
                    oneSide = true;
                    System.out.println("creating real plan");
                    createdPlan = fftwlib.realPlan(dataR.length, true, typeInt);
                } else {
                    //todo : check if this dimension is correct!!!
                    createdPlan = fftwlib.complexPlan(dataR.length, true, typeInt);
                }
                //end of changes by Rob
                sf = 1.0 / ((VectorType) input).getXTriplet().getStep();
            }
            if (twoD) {

                //added Rob 07/07/03
                //todo : check that these plans are created properly!!!
                if (!inputComplex) {
                    createdPlan = fftwlib.realNdPlan(new int[]{mdataR.length}, true, typeInt);
                } else {
                    createdPlan = fftwlib.complexNdPlan(new int[]{mdataR.length}, true, typeInt);

                }
                //end Rob

                sf = 1.0 / ((MatrixType) input).getXorYTriplet(1).getStep();
                sf0 = 1.0 / ((MatrixType) input).getXorYTriplet(0).getStep();
                resolution0 = sf0 / points0;
                maxFreq0 = sf0 / 2.0;
                norm0 = 1. / points0;
            }
            resolution = sf / points;
            maxFreq = sf / 2.0;
        } else if (style.equals("Inverse")) {
            applyNorm = false;
            direct = false;
            oneSide = false;
            if (oneD) {
                //added by Rob 30/06/03
                if ((inputComplex) && ((input instanceof Spectral))) {
                    if (!((Spectral) input).isTwoSided()) {
                        oneSide = true;
                        System.out.println("creating inverse fftw plan");
                        //todo : this isn't correct. The Need to sort out the length being passed!!!
                        //maybe dataR.length+dataI.length-1
                        int length = FFTWLibrary.mergeLength(dataR, dataI);
                        createdPlan = fftwlib.realPlan(length, direct, typeInt);
                    } else {
                        createdPlan = fftwlib.complexPlan(dataR.length, direct, typeInt);
                    }
                } else {
                    createdPlan = fftwlib.complexPlan(dataR.length, direct, typeInt);
                }
                //end of changes by Rob
                resolution = ((VectorType) input).getXTriplet().getStep();
            }
            if (twoD) {
                resolution = ((MatrixType) input).getXorYTriplet(1).getStep();
                resolution0 = ((MatrixType) input).getXorYTriplet(0).getStep();
                sf0 = resolution0 * points0;
            }
            sf = resolution * points;
            System.out.println("***resolution : " + resolution);
            System.out.println("***points : " + points);
            System.out.println("***sf : " + sf);
        } else if (style.equals("Inverse/normalized(1/N)")) {
            applyNorm = true;
            direct = false;
            norm = 1. / points;
            oneSide = false;
            if (oneD) {
                //added by Rob 30/06/03
                //changed 08/07/03
                if ((inputComplex) && ((input instanceof Spectral))) {
                    if (!((Spectral) input).isTwoSided()) {
                        oneSide = true;
                        System.out.println("creating inverse fftw plan");
                        //maybe dataR.length+dataI.length-1
                        int length = FFTWLibrary.mergeLength(dataR, dataI);
                        createdPlan = fftwlib.realPlan(length, direct, typeInt);
                    } else {
                        createdPlan = fftwlib.complexPlan(dataR.length, direct, typeInt);
                    }
                } else {
                    createdPlan = fftwlib.complexPlan(dataR.length, direct, typeInt);
                }
                //end of changes by Rob
                resolution = ((VectorType) input).getXTriplet().getStep();
            }
            if (twoD) {
                resolution = ((MatrixType) input).getXorYTriplet(1).getStep();
                resolution0 = ((MatrixType) input).getXorYTriplet(0).getStep();
                sf0 = resolution0 * points0;
                norm0 = 1. / points0;
            }
            sf = resolution * points;
        } else if (style.equals("Automatic")) {

            if (input instanceof Signal) {
                applyNorm = true;
                sf = ((Signal) input).getSamplingRate();
                maxFreq = sf / 2.0;
                resolution = sf / points;
                norm = 1. / sf;
                //	println("Signal object: norm = " + String.valueOf(norm) + ", resolution = " + String.valueOf(resolution) );
                direct = true;
                //changes buy Rob 01/07/03
                //if ( speed ) oneSide = false;
                //else oneSide = !inputComplex;
                oneSide = !inputComplex;
                //end changes by Rob
                //	println("About to test conjugate symmetry.");
                //change by Rob 01/07/03
                //if ( !speed ) {
                //craigif ( !oneSide ) {
                //end of Rob change

                //	    conjugateAnswer = fft.testConjugateSymmetry( dataR, dataI );
                //	    if ( conjugateAnswer == 1 ) conjugateSymmetric = true;
                //	    else if (conjugateAnswer == -1 ) conjugateAntisymmetric = true;
                //	    	System.out.println("Result of test: " + String.valueOf(conjugateSymmetric) + " " + String.valueOf(conjugateAntisymmetric) );
                //craig	}
            } else if (input instanceof Spectral) {

                applyNorm = true;
                if (oneD) {
                    oneSide = !((Spectral) input).isTwoSided();
                } else {
                    oneSide = false;
                }
                resolution = ((Spectral) input).getFrequencyResolution(0);

                if (twoD) {
                    resolution = ((Spectral) input).getFrequencyResolution(1);
                    resolution0 = ((Spectral) input).getFrequencyResolution(0);
                }
                //	println("About to convert from dataR with first element = " + String.valueOf(dataR[0]) + ", original N = " + String.valueOf(((Spectral)input).getOriginalN( 0 )) );
                /*Craig	if ( oneSide || ((Spectral)input).isNarrow( 0 ) ) {
                dataR = FlatArray.convertToFullSpectrum( dataR, ((Spectral)input).getOriginalN( 0 ), oneSide, true, ((Spectral)input).isNarrow( 0 ), (int)Math.round(((Spectral)input).getLowerFrequencyBound( 0 )/((Spectral)input).getFrequencyResolution( 0 )) );
                println("Calling convertToFullSpectrum with arguments:");
                println("nfull = " + String.valueOf(((Spectral)input).getOriginalN( 0 )));
                println("oneside = " + String.valueOf( oneSide ) );
                println("narrow = " + String.valueOf( ((Spectral)input).isNarrow( 0 ) ) );
                println("low index = " + String.valueOf((int)Math.round(((Spectral)input).getLowerFrequencyBound( 0 )/((Spectral)input).getFrequencyResolution( 0 ))) );
                int pd;
                println("Real data full spectrum:");
                for( pd = 0; pd < dataR.length; pd++ ) println(String.valueOf(dataR[pd]));
                if ( inputComplex ) {
                dataI = FlatArray.convertToFullSpectrum( dataI, ((Spectral)input).getOriginalN( 0 ),  oneSide, false, ((Spectral)input).isNarrow( 0 ), (int)Math.round(((Spectral)input).getLowerFrequencyBound( 0 )/((Spectral)input).getFrequencyResolution( 0 )) );
                println("Imaginary data full spectrum:");
                for( pd = 0; pd < dataI.length; pd++ ) println(String.valueOf(dataI[pd]));
                }
            }Craig*/

                norm = resolution;
                //	println("Spectral object: norm = " + String.valueOf(norm) + ", resolution = " + String.valueOf(resolution) );
                points = ((Spectral) input).getOriginalN(1);
                sf = resolution * points;
                if (twoD) {
                    //points0 = ((Spectral)input).getOriginalN( 1 );
                    sf = resolution0 * points0;
                    norm0 = /*1.0 / points0*/ resolution0;
                    //Craig points0 = ((Spectral)input).getOriginalN( 0 );
                    sf0 = resolution0 * points0;
                    maxFreq0 = sf0 / 2.;
                }
                maxFreq = sf / 2.;
                direct = false;
                //change by Rob 01/07/03
                //if ( !speed && oneD ) {
                /*Craigif ( oneD ) {
            //end change Rob
                if ( oneSide ) conjugateSymmetric = true;
                else {
                conjugateAnswer = fft.testConjugateSymmetry( dataR, dataI );
                if ( conjugateAnswer == 1 ) conjugateSymmetric = true;
                else if (conjugateAnswer == -1 ) conjugateAntisymmetric = true;
                }
                //		    println("Result of test: " + String.valueOf(conjugateSymmetric) + " " + String.valueOf(conjugateAntisymmetric) );
            }Craig*/

            } else {
                applyNorm = false; //Craig
                if (oneD) {
                    //	System.out.println("It gets here");
                    sf = 1.0; /// ((VectorType)input).getXTriplet().getStep();
                    //Craig applyNorm = false;
                    maxFreq = 0.5;
                }
                //	println("Other object: norm = " + String.valueOf(norm) + ", resolution = " + String.valueOf(resolution) );
                direct = true;
                if (twoD) {
                    oneSide = false;
                    sf = 1.0;// / ((MatrixType)input).getXorYTriplet( 1 ).getStep();
                    sf0 = 1.0;// / ((MatrixType)input).getXorYTriplet( 0 ).getStep();
                    resolution0 = sf0 / points0;
                    maxFreq0 = 0.5;
                    //maxFreq0 = sf0 / 2.0; //Craig
                } else {
                    //changes by Rob 01/07/03
                    //if ( speed ) oneSide = false;
                    //else oneSide = !inputComplex;
                    oneSide = !inputComplex;
                    //  System.out.println("oneside = " + oneSide);
                    //if ( !speed ) {
                    //Craigif ( !oneSide ) {
                    //end changes by Rob
                    //		conjugateAnswer = fft.testConjugateSymmetry( dataR, dataI );
                    //		if ( conjugateAnswer == 1 ) conjugateSymmetric = true;
                    //		else if (conjugateAnswer == -1 ) conjugateAntisymmetric = true;
                    //	println("Result of test: " + String.valueOf(conjugateSymmetric) + " " + String.valueOf(conjugateAntisymmetric) );
                    //Craig}
                }
                resolution = sf / points;
                //maxFreq = sf / 2.0; // Craig

            }

            if (direct == true) {
                if (oneD) {
                    if (!inputComplex) {
                        createdPlan = fftwlib.realPlan(dataR.length, true, typeInt);
                    } else {
                        createdPlan = fftwlib.complexPlan(dataR.length, true, typeInt);
                    }
                }
                if (twoD) {
                    if (!inputComplex) {
                        createdPlan = fftwlib.realNdPlan(new int[]{mdataR.length}, true, typeInt);
                    } else {
                        createdPlan = fftwlib.complexNdPlan(new int[]{mdataR.length}, true, typeInt);
                    }
                }
            } else {
                if (oneD) {
                    if (inputComplex) {
                        if (oneSide) {
                            int length = FFTWLibrary.mergeLength(dataR, dataI);
                            createdPlan = fftwlib.realPlan(length, false, typeInt);
                        } else {
                            createdPlan = fftwlib.complexPlan(dataR.length, false, typeInt);
                        }
                    } else {
                        createdPlan = fftwlib.complexPlan(dataR.length, false, typeInt);
                    }

                }
                if (twoD) {
                    if (!inputComplex) {
                        createdPlan = fftwlib.realNdPlan(new int[]{mdataR.length}, false, typeInt);
                    } else {
                        createdPlan = fftwlib.complexNdPlan(new int[]{mdataR.length}, false, typeInt);
                    }
                }
            }

        }

        ArrayList fourier;
        if (oneD) {
            real = dataR;
            if (dataI != null) {
                imag = dataI;
                //complex transform
                //fourier = fftwlib.complexTransform(real,imag);
                if (createdPlan instanceof jfftw.real.Plan) {
                    System.out.println("reverse half_complex transform");
                    fourier = fftwlib.realTransform(FFTWLibrary.merge4(real, imag), direct);
                    //fourier = fftwlib.realTransform(FFTWLibrary.merge2(real,imag),direct);
                } else {
                    //    fourier = new ArrayList();
                    fourier = fftwlib.complexTransform(real, imag);
                }
            } else {
                imag = new double[points];
                if (createdPlan instanceof jfftw.real.Plan) {
                    System.out.println("fftw real transform");
                    fourier = fftwlib.realTransform(real, direct);
                } else {
                    fourier = fftwlib.complexTransform(real, imag);
                }
            }
            //	println("Going into FFT_C.");

            //fourier = fft.FFT_C( real, imag, direct, false );
            real = (double[]) fourier.get(0);
            imag = (double[]) fourier.get(1);
            if (applyNorm) {
                for (j = 0; j < real.length; j++) {
                    real[j] *= norm;
                    imag[j] *= norm;
                }
            }
        } else if (twoD) {
            mreal = mdataR;
            if (mdataI != null) {
                mimag = mdataI;
            } else {
                mimag = new double[points0][points];
            }
            int row, column;
            for (row = 0; row < points0; row++) {
                fourier = fft.FFT_C(mreal[row], mimag[row], direct, false);
                mreal[row] = (double[]) fourier.get(0);
                mimag[row] = (double[]) fourier.get(1);
            }
            if (applyNorm) {
                norm *= norm0;
            }
            double[][] transposeReal = new double[points][points0];
            double[][] transposeImag = new double[points][points0];
            for (row = 0; row < points0; row++) {
                for (column = 0; column < points; column++) {
                    transposeReal[column][row] = mreal[row][column];
                    transposeImag[column][row] = mimag[row][column];
                }
            }
            for (column = 0; column < points; column++) {
                fourier = fft.FFT_C(transposeReal[column], transposeImag[column], direct, false);
                transposeReal[column] = (double[]) fourier.get(0);
                transposeImag[column] = (double[]) fourier.get(1);
                if (applyNorm) {
                    for (j = 0; j < points0; j++) {
                        transposeReal[column][j] *= norm;
                        transposeImag[column][j] *= norm;
                    }
                }
            }
            for (row = 0; row < points0; row++) {
                for (column = 0; column < points; column++) {
                    mreal[row][column] = transposeReal[column][row];
                    mimag[row][column] = transposeImag[column][row];
                }
            }

        }


        /* Debugging output
      println("Exited from FFT_C with data real:");
      for( kk = 0; kk < real.length; kk++) System.out.print(String.valueOf(real[kk]) + " " );
      println(" ");
      println("and imag:");
      for( kk = 0; kk < imag.length; kk++) System.out.print(String.valueOf(imag[kk]) + " " );
      println(" ");
      */

        /*
       * Begin output
       */

        if ((style.equals("Direct")) || (style.equals("Direct/normalized(1/N)"))) {
            if (oneD) {
                //changes Rob 01/07/03
                //  System.out.println("It is direct");
                //result = new ComplexSpectrum( true, false, real, imag, points, resolution, maxFreq );
                System.out.println("oneSide : " + oneSide);
                System.out.println("Points : " + points);
                System.out.println("Points2 : " + real.length);
                System.out.println("Resolution : " + resolution);
                System.out.println("MaxFreq : " + maxFreq);
                result = new ComplexSpectrum(!oneSide, false, real, imag, points, resolution, maxFreq);
                //end Rob changes
            } else if (twoD) {
                double[] df = {resolution0, resolution};
                result = new Spectrum2D(mreal, mimag, df);
            }
        } else if ((style.equals("Inverse")) || (style.equals("Inverse/normalized(1/N)"))) {
            if (oneD) {
                //changes Rob 01/07/03
                //System.out.println("It is inverse");
                if ((inputComplex) && (input instanceof Spectral && ((Spectral) input).isTwoSided())) {
                    result = new ComplexSampleSet(sf, real, imag);
                } else {
                    System.out.println("***Output is Real*** : " + sf);
                    System.out.println(real.length);
                    result = new SampleSet(sf, real);
                }

                //end Rob changes
            } else if (twoD) {
                result = new MatrixType(new Triplet(points0, 0, 1. / sf0), new Triplet(points, 0, 1. / sf), mreal,
                        mimag);
            }
        } else if (style.equals("Automatic")) {
            //System.out.println("It is automatic");
            if (input instanceof Spectral) {
                //		System.out.println("Output route for input being Spectral:");
                if (oneD) {
                    //change by Rob 01/07/03
                    //if ( !speed ) {
                    /*Craig change if ( !oneSide ) {
                 //end change Rob
                 if ( conjugateSymmetric ) {
                            System.out.println("conjugateSymmetric is true.");
                     result = new SampleSet( sf, real );
                 }
                 else {
                             System.out.println("conjugateSymmetric is false.");
                     if ( conjugateAntisymmetric ) {
                         System.out.println("conjugateAntisymmetric is true.");
                     FlatArray.initializeArray( real );
                     }
                     result = new ComplexSampleSet( sf, real, imag );
                 }
         Craig change	    }*/
                    if ((inputComplex) && (((Spectral) input).isTwoSided())) {
                        /*	    else */
                        result = new ComplexSampleSet(sf, real, imag);
                    } else {
                        result = new SampleSet(sf, real);
                    } //end Craig change
                } else if (twoD) {
                    result = new MatrixType(new Triplet(points0, 0, 1.0 / sf0), new Triplet(points, 0, 1. / sf), mreal,
                            mimag);
                }
            } else {
                if (oneD) {
                    //change by Rob 01/07/03
                    //if ( !speed ) {
                    //Craig change if ( !oneSide ) {
                    //end change Rob
                    //		    System.out.println("Output route for input NOT being Spectral:");
                    /*Craigif ( conjugateSymmetric ) {
                         System.out.println("conjugateSymmetric is true.");
                     if ( oneSide ) real = FlatArray.convertToOneSided( real, points, false, true );
                     result = new Spectrum( !oneSide, false, real, points, resolution, maxFreq );
                 }
                 else {
                         System.out.println("conjugateSymmetric is false.");
                     if ( conjugateAntisymmetric ) {
                         System.out.println("conjugateAntisymmetric is true.");
                     if ( oneSide ) {
                         real = FlatArray.convertToOneSided( real, points, false, true );
                         imag = FlatArray.convertToOneSided( imag, points, false, true );
                     }
                     }
                     else {
                         System.out.println("Neither conjugateSymmetric not conjugateAntisymmetric is true.");
                     if ( oneSide ) {
                         real = FlatArray.convertToOneSided( real, points, false, true );
                         imag = FlatArray.convertToOneSided( imag, points, false, true );
                     }
                     }
                 }Craig*/

                    /* Debugging output
                    println("About to create ComplexSpectrum with data real:");
                    for(  kk = 0; kk < real.length; kk++) System.out.print(String.valueOf(real[kk]) + " " );
                    println(" ");
                    println("and imag:");
                    for( kk = 0; kk < imag.length; kk++ ) System.out.print(String.valueOf(imag[kk]) + " " );
                    println(" ");
                    println("Number of points = " + String.valueOf(points));
                 */
                    result = new ComplexSpectrum(!oneSide, false, real, imag, points, resolution, maxFreq);
                    //Craig change}
                    //Craig change else result = new ComplexSpectrum( true, false, real, imag, points, resolution, maxFreq );
                } else if (twoD) {
                    double[] df = {resolution0, resolution};
                    result = new Spectrum2D(mreal, mimag, df);
                }
            }
        }

        //if ( inputSignal) result.addToLegend( acqTime );
        output(result);
//	System.out.println("It gets here");
//	Wisdom.save(new File(wisdomFile));
    }


    /**
     * Initialses information specific to FFT.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setMinimumInputNodes(1);
        setMaximumInputNodes(1);
        setDefaultInputNodes(1);
        setMinimumOutputNodes(1);
        setDefaultOutputNodes(1);
        // This is to ensure thfat we receive arrays containing double-precision numbers
        setRequireDoubleInputs(true);
        setCanProcessDoubleArrays(true);

        fftwlib = new FFTWLibrary();
//	try {
//	Wisdom.load(new File(wisdomFile));
//	}
//	catch(IOException e) {}
    }

    /**
     * return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface. Such
     * lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine(
                "Operation of transform: $title style Choice Automatic Direct Direct/normalized(1/N) Inverse Inverse/normalized(1/N)");
        //addGUILine("For 1D transform, optimize for: $title opt Choice MaximumSpeed MinimumStorage");
        addGUILine("Plan - Estimate or Measure: $title planType Choice Estimate Measure");
        addGUILine("For 1D transform, apply this window to the data: $title WindowFunction Choice " + SigAnalWindows
                .listOfWindows());
        addGUILine("For 1D transform, pad input with zeros to a power of 2: $title padding Checkbox false");
    }

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
    public void starting() {
        super.starting();
    }

    /**
     * Saves FFT's parameters.
     */
    public void saveParameters() {
        saveParameter("style", style);
        saveParameter("planType", planType);
        //saveParameter("opt", opt);
        saveParameter("WindowFunction", WindowFunction);
        saveParameter("padding", padding);
    }

    /**
     * Used to set each of FFT's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("style")) {
            style = value;
        }

        if (name.equals("planType")) {
            planType = value;
        }

        /*if (name.equals("opt")) {
            opt = value;
            }*/
        if (name.equals("WindowFunction")) {
            WindowFunction = value;
        }
        if (name.equals("padding")) {
            padding = strToBoolean(value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to FFT, each separated by a white space.
     */
    public String inputTypes() {
        return "MultipleAudio VectorType MatrixType";
    }

    /**
     * @return a string containing the names of the types output from FFT, each separated by a white space.
     */
    public String outputTypes() {
        return "ComplexSpectrum Spectrum ComplexSampleSet SampleSet Spectrum2D MatrixType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Performs a Fast Fourier Transform or its inverse using fftw.";
    }

    /**
     * @return the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "FFTW.html";
    }

}




