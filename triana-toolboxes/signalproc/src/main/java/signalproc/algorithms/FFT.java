package signalproc.algorithms;

import java.util.ArrayList;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSampleSet;
import triana.types.ComplexSpectrum;
import triana.types.GraphType;
import triana.types.MatrixType;
import triana.types.SampleSet;
import triana.types.Signal;
import triana.types.Spectral;
import triana.types.Spectrum;
import triana.types.Spectrum2D;
import triana.types.VectorType;
import triana.types.audio.MultipleAudio;
import triana.types.util.FlatArray;
import triana.types.util.SigAnalWindows;
import triana.types.util.Str;
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
 * @version 2.11 09 March 2001
 * @see FFTC
 * @see triana.types.Spectral
 * @see triana.types.Signal
 * @see triana.types.ComplexSpectrum
 * @see triana.types.Spectrum
 * @see triana.types.SampleSet
 * @see triana.types.ComplexSampleSet
 */
public class FFT extends Unit {

    String style = "Automatic";
    String WindowFunction = "(none)";
    boolean padding = false;
    String opt = "MaximumSpeed";
    FFTC fft;

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
        boolean speed = opt.equals("MaximumSpeed");
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

        input = (GraphType) getInputAtNode(0);

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
            points = ((VectorType) input).size();
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
                sf = 1.0 / ((VectorType) input).getXTriplet().getStep();
            }
            if (twoD) {
                sf = 1.0 / ((MatrixType) input).getXorYTriplet(1).getStep();
                sf0 = 1.0 / ((MatrixType) input).getXorYTriplet(0).getStep();
                resolution0 = sf0 / points0;
                maxFreq0 = resolution0 * (points0 / 2);
            }
            resolution = sf / points;
            maxFreq = resolution * (points / 2);
        } else if (style.equals("Direct/normalized(1/N)")) {
            applyNorm = true;
            direct = true;
            norm = 1. / points;
            oneSide = false;
            if (oneD) {
                sf = 1.0 / ((VectorType) input).getXTriplet().getStep();
            }
            if (twoD) {
                sf = 1.0 / ((MatrixType) input).getXorYTriplet(1).getStep();
                sf0 = 1.0 / ((MatrixType) input).getXorYTriplet(0).getStep();
                resolution0 = sf0 / points0;
                maxFreq0 = resolution0 * (points0 / 2);
                norm0 = 1. / points0;
            }
            resolution = sf / points;
            maxFreq = resolution * (points / 2);
        } else if (style.equals("Inverse")) {
            applyNorm = false;
            direct = false;
            oneSide = false;
            if (oneD) {
                resolution = ((VectorType) input).getXTriplet().getStep();
            }
            if (twoD) {
                resolution = ((MatrixType) input).getXorYTriplet(1).getStep();
                resolution0 = ((MatrixType) input).getXorYTriplet(0).getStep();
                sf0 = resolution0 * points0;
            }
            sf = resolution * points;
        } else if (style.equals("Inverse/normalized(1/N)")) {
            applyNorm = true;
            direct = false;
            norm = 1. / points;
            oneSide = false;
            if (oneD) {
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
                //    println("Input is Signal.");
                applyNorm = true;
                sf = ((Signal) input).getSamplingRate();
                resolution = sf / points;
                maxFreq = resolution * (points / 2);
                norm = 1. / sf;
                //	println("Signal object: norm = " + String.valueOf(norm) + ", resolution = " + String.valueOf(resolution) );
                direct = true;
                if (speed) {
                    oneSide = false;
                } else {
                    oneSide = !inputComplex;
                }
                //	println("About to test conjugate symmetry.");
                if (!speed) {
                    conjugateAnswer = fft.testConjugateSymmetry(dataR, dataI);
                    if (conjugateAnswer == 1) {
                        conjugateSymmetric = true;
                    } else if (conjugateAnswer == -1) {
                        conjugateAntisymmetric = true;
                    }
                    //	println("Result of test: " + String.valueOf(conjugateSymmetric) + " " + String.valueOf(conjugateAntisymmetric) );
                }
            } else if (input instanceof Spectral) {
                //	println("Input is Spectral.");
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
                if (oneSide || ((Spectral) input).isNarrow(0)) {
                    dataR = FlatArray.convertToFullSpectrum(dataR, ((Spectral) input).getOriginalN(0), oneSide, true,
                            ((Spectral) input).isNarrow(0), (int) Math
                                    .round(((Spectral) input).getLowerFrequencyBound(0) / ((Spectral) input)
                                            .getFrequencyResolution(0)));
                    System.out.println("Calling convertToFullSpectrum with arguments:");
                    System.out.println("nfull = " + String.valueOf(((Spectral) input).getOriginalN(0)));
                    System.out.println("oneside = " + String.valueOf(oneSide));
                    System.out.println("narrow = " + String.valueOf(((Spectral) input).isNarrow(0)));
                    System.out.println("low index = " + String.valueOf((int) Math
                            .round(((Spectral) input).getLowerFrequencyBound(0) / ((Spectral) input)
                                    .getFrequencyResolution(0))));
                    int pd;
                    System.out.println("Real data full spectrum:");
                    for (pd = 0; pd < dataR.length; pd++) {
                        System.out.println(String.valueOf(dataR[pd]));
                    }
                    if (inputComplex) {
                        dataI = FlatArray
                                .convertToFullSpectrum(dataI, ((Spectral) input).getOriginalN(0), oneSide, false,
                                        ((Spectral) input).isNarrow(0), (int) Math
                                                .round(((Spectral) input).getLowerFrequencyBound(0) / ((Spectral) input)
                                                        .getFrequencyResolution(0)));
                        System.out.println("Imaginary data full spectrum:");
                        for (pd = 0; pd < dataI.length; pd++) {
                            System.out.println(String.valueOf(dataI[pd]));
                        }
                    }
                }

                norm = resolution;
                //	println("Spectral object: norm = " + String.valueOf(norm) + ", resolution = " + String.valueOf(resolution) );
                points = ((Spectral) input).getOriginalN(0);
                sf = resolution * points;
                if (twoD) {
                    points = ((Spectral) input).getOriginalN(1);
                    sf = resolution * points;
                    norm0 = resolution0;
                    points0 = ((Spectral) input).getOriginalN(0);
                    sf0 = resolution0 * points0;
                    maxFreq0 = resolution0 * (points0 / 2);
                    ;
                }
                maxFreq = resolution * (points / 2);
                direct = false;
                if (!speed && oneD) {
                    if (oneSide) {
                        conjugateSymmetric = true;
                    } else {
                        conjugateAnswer = fft.testConjugateSymmetry(dataR, dataI);
                        if (conjugateAnswer == 1) {
                            conjugateSymmetric = true;
                        } else if (conjugateAnswer == -1) {
                            conjugateAntisymmetric = true;
                        }
                    }
                    //		    println("Result of test: " + String.valueOf(conjugateSymmetric) + " " + String.valueOf(conjugateAntisymmetric) );
                }
            } else {
                sf = 1.0;
                applyNorm = false;
                resolution = 1.0 / points;
                maxFreq = resolution * (points / 2);
                //	println("Other object: norm = " + String.valueOf(norm) + ", resolution = " + String.valueOf(resolution) );
                direct = true;
                if (twoD) {
                    oneSide = false;
                    sf0 = 1.0;
                    resolution0 = 1.0 / points0;
                    maxFreq0 = resolution0 * (points0 / 2);
                } else {
                    if (speed) {
                        oneSide = false;
                    } else {
                        oneSide = !inputComplex;
                    }
                    if (!speed) {
                        conjugateAnswer = fft.testConjugateSymmetry(dataR, dataI);
                        if (conjugateAnswer == 1) {
                            conjugateSymmetric = true;
                        } else if (conjugateAnswer == -1) {
                            conjugateAntisymmetric = true;
                        }
                        //	println("Result of test: " + String.valueOf(conjugateSymmetric) + " " + String.valueOf(conjugateAntisymmetric) );
                    }
                }
            }
        }

        ArrayList fourier;
        if (oneD) {
            real = dataR;
            if (dataI != null) {
                imag = dataI;
            } else {
                imag = new double[points];
            }
            //	println("Going into FFT_C.");
            fourier = fft.FFT_C(real, imag, direct, false);
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
            System.out.println("Pos3: " + String.valueOf(points) + " " + String.valueOf(resolution) + " " + String
                    .valueOf(maxFreq));
            if (oneD) {
                result = new ComplexSpectrum(true, false, real, imag, points, resolution, maxFreq);
            } else if (twoD) {
                double[] df = {resolution0, resolution};
                result = new Spectrum2D(mreal, mimag, df);
            }
        } else if ((style.equals("Inverse")) || (style.equals("Inverse/normalized(1/N)"))) {
            if (oneD) {
                result = new ComplexSampleSet(sf, real, imag);
            } else if (twoD) {
                result = new MatrixType(new Triplet(points0, 0, 1. / sf0), new Triplet(points, 0, 1. / sf), mreal,
                        mimag);
            }
        } else if (style.equals("Automatic")) {

            if (input instanceof Spectral) {
                //	println("Output route for input being Spectral:");
                if (oneD) {
                    if (!speed) {
                        if (conjugateSymmetric) {
                            //	    println("conjugateSymmetric is true.");
                            result = new SampleSet(sf, real);
                        } else {
                            //	    println("conjugateSymmetric is false.");
                            if (conjugateAntisymmetric) {
                                //	println("conjugateAntisymmetric is true.");
                                FlatArray.initializeArray(real);
                            }
                            result = new ComplexSampleSet(sf, real, imag);
                        }
                    } else {
                        result = new ComplexSampleSet(sf, real, imag);
                    }
                } else if (twoD) {
                    result = new MatrixType(new Triplet(points0, 0, sf0), new Triplet(points, 0, sf), mreal, mimag);
                }
            } else {
                if (oneD) {
                    if (!speed) {
                        //    println("Output route for input NOT being Spectral:");
                        if (conjugateSymmetric) {
                            //	println("conjugateSymmetric is true.");
                            if (oneSide) {
                                real = FlatArray.convertToOneSided(real, points, false, true);
                            }
                            result = new Spectrum(!oneSide, false, real, points, resolution, maxFreq);
                        } else {
                            //	println("conjugateSymmetric is false.");
                            if (conjugateAntisymmetric) {
                                //    println("conjugateAntisymmetric is true.");
                                if (oneSide) {
                                    real = FlatArray.convertToOneSided(real, points, false, true);
                                    imag = FlatArray.convertToOneSided(imag, points, false, true);
                                }
                            } else {
                                //    println("Neither conjugateSymmetric not conjugateAntisymmetric is true.");
                                if (oneSide) {
                                    real = FlatArray.convertToOneSided(real, points, false, true);
                                    imag = FlatArray.convertToOneSided(imag, points, false, true);
                                }
                            }
                        }

                        /* Debugging output
                             println("About to create ComplexSpectrum with data real:");
                             for(  kk = 0; kk < real.length; kk++) System.out.print(String.valueOf(real[kk]) + " " );
                             println(" ");
                             println("and imag:");
                             for( kk = 0; kk < imag.length; kk++ ) System.out.print(String.valueOf(imag[kk]) + " " );
                             println(" ");
                             println("Number of points = " + String.valueOf(points));
                          */
                        System.out.println(
                                "Pos1: " + String.valueOf(points) + " " + String.valueOf(resolution) + " " + String
                                        .valueOf(maxFreq));
                        result = new ComplexSpectrum(!oneSide, false, real, imag, points, resolution, maxFreq);
                    } else {
                        System.out.println(
                                "Pos2: " + String.valueOf(points) + " " + String.valueOf(resolution) + " " + String
                                        .valueOf(maxFreq));
                        result = new ComplexSpectrum(true, false, real, imag, points, resolution, maxFreq);
                    }
                } else if (twoD) {
                    double[] df = {resolution0, resolution};
                    result = new Spectrum2D(mreal, mimag, df);
                }
            }
        }

        //if ( inputSignal) result.addToLegend( acqTime );
        if (input.getTitle() != null) {
            result.setTitle(input.getTitle());
        }
        output(result);
    }


    /**
     * Initialses information specific to FFT.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);

        setMinimumInputNodes(1);
        setMaximumInputNodes(1);
        setDefaultInputNodes(1);
        setMinimumOutputNodes(1);
        setDefaultOutputNodes(1);
        // This is to ensure that we receive arrays containing double-precision numbers
//        setRequireDoubleInputs(true);
//        setCanProcessDoubleArrays(true);

        String guilines = "";
        guilines += "Operation of transform: $title style Choice Automatic Direct Direct/normalized(1/N) Inverse Inverse/normalized(1/N)\n";
        guilines += "For 1D transform, optimize for: $title opt Choice MaximumSpeed MinimumStorage\n";
        guilines += "For 1D transform, apply this window to the data: $title WindowFunction Choice " + SigAnalWindows.listOfWindows() + "\n";
        guilines += "For 1D transform, pad input with zeros to a power of 2: $title padding Checkbox false\n";
        System.out.println("guilines = " + guilines);
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine(
//                "Operation of transform: $title style Choice Automatic Direct Direct/normalized(1/N) Inverse Inverse/normalized(1/N)");
//        addGUILine("For 1D transform, optimize for: $title opt Choice MaximumSpeed MinimumStorage");
//        addGUILine("For 1D transform, apply this window to the data: $title WindowFunction Choice " + SigAnalWindows
//                .listOfWindows());
//        addGUILine("For 1D transform, pad input with zeros to a power of 2: $title padding Checkbox false");
//    }

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
//    public void starting() {
//        super.starting();
//    }
//
//    /**
//     * Saves FFT's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("style", style);
//        saveParameter("opt", opt);
//        saveParameter("WindowFunction", WindowFunction);
//        saveParameter("padding", padding);
//    }

    /**
     * Used to set each of FFT's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("style")) {
            style = (String) value;
        }
        if (name.equals("opt")) {
            opt = (String) value;
        }
        if (name.equals("WindowFunction")) {
            WindowFunction = (String) value;
        }
        if (name.equals("padding")) {
            padding = Str.strToBoolean((String) value);
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

    public String[] getInputTypes() {
        return new String[]{"triana.types.MultipleAudio", "triana.types.VectorType", "triana.types.MatrixType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.MultipleAudio", "triana.types.ComplexSpectrum", "triana.types.Spectrum", "triana.types.SampleSet", "triana.types.Spectrum2D", "triana.types.MatrixType"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Performs a Fast Fourier Transform or its inverse.";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "fft.html";
    }
}




