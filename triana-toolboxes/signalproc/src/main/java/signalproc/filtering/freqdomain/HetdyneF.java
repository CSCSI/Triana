/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2003 University of Wales, Cardiff. All rights reserved.
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
 */
package signalproc.filtering.freqdomain;


import org.trianacode.taskgraph.Unit;
import signalproc.algorithms.Shift;
import triana.types.ComplexSpectrum;
import triana.types.Const;
import triana.types.ProcessDoublesInterface;
import triana.types.util.Str;

/**
 * A HetdyneF_old unit to take an input ComplexSpectrum and output two ComplexSpectrum's that represent the FTs of the
 * two quadruature components of the heterodyne of the time-series that led to the input ComplexSpectrum. (These are the
 * results of heterodyning with cosine and sine at the heterodyne frequency.) The user can choose the heterodyne
 * frequency and bandwidth. The user can select that the output spectra should have their Nyquist frequency reduced so
 * that when they are inverted to the time-domain they are sampled at the rate given by the heterodyne bandwidth rather
 * than the original sampling rate.
 *
 * @author B F Schutz
 * @version $Revision: 2921 $
 */
public class HetdyneF extends Unit implements ProcessDoublesInterface {

    // parameter data type definitions
    private double freq;
    private double bandwidth;
    private String window;
    private boolean nyquist;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        ComplexSpectrum inputPos = (ComplexSpectrum) getInputAtNode(0);
        ComplexSpectrum inputNeg = (ComplexSpectrum) inputPos.copyMe();

        int steps = (int) Math.round(freq / inputPos.getFrequencyResolution());
        /*
        println("inputPos:");
        for ( k = 0; k < inputPos.size(); k++ ) println( String.valueOf( inputPos.getDataReal()[k] ) );
        */
        Shift.shiftData(inputPos, -steps);
        /*
        println("inputPos after shift to right:");
        for ( k = 0; k < inputPos.size(); k++ ) println( String.valueOf( inputPos.getDataReal()[k] ) );
        */
        Shift.shiftData(inputNeg, steps);
        /*
        println("inputNeg after shift to left:");
        for ( k = 0; k < inputNeg.size(); k++ ) println( String.valueOf( inputNeg.getDataReal()[k] ) );
        */

        ComplexSpectrum temp = (ComplexSpectrum) inputPos.copyMe();
        inputPos = (ComplexSpectrum) inputPos.add(inputNeg);
        /*
        println("inputPos after adding:");
        for ( k = 0; k < inputPos.size(); k++ ) println( String.valueOf( inputPos.getDataReal()[k] ) );
        */
        temp = (ComplexSpectrum) temp.subtract(inputNeg);
        temp = (ComplexSpectrum) temp.multiply(new Const(0.0, 1.0));
        /*
        println("temp after subtracting and multiplying:");
        for ( k = 0; k < temp.size(); k++ ) println( String.valueOf( temp.getDataReal()[k] ) );
        */

        ComplexSpectrum cosineQuad = inputPos;
        ComplexSpectrum sineQuad = temp;

        ComplexSpectrum sineHet = (ComplexSpectrum) LowPass.filterToMax(sineQuad, bandwidth / 2, true, window, nyquist);
        /*
        println("sineHet:");
        for ( k = 0; k < sineHet.size(); k++ ) println( String.valueOf( sineHet.getDataReal()[k] ) );
        */
        ComplexSpectrum cosineHet = (ComplexSpectrum) LowPass
                .filterToMax(cosineQuad, bandwidth / 2, true, window, nyquist);
        /*
        println("cosineHet:");
        for ( k = 0; k < cosineHet.size(); k++ ) println( String.valueOf( cosineHet.getDataReal()[k] ) );
        */

        outputAtNode(0, cosineHet);
        outputAtNode(1, sineHet);
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        defineParameter("requireDoubleInputs", new Boolean(false), INTERNAL);
        defineParameter("canProcessDoubleArrays", new Boolean(false), INTERNAL);

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(2);
        setMinimumOutputNodes(2);
        setMaximumOutputNodes(2);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription(
                "Produce heterodyned narrow-band ComplexSpectra (cosine and sine quadratures) from input ComplexSpectrum");
        setHelpFileLocation("HetdyneF.html");

        // Define initial value and type of parameters
        defineParameter("freq", "500", USER_ACCESSIBLE);
        defineParameter("bandwidth", "100", USER_ACCESSIBLE);
        defineParameter("window", "Rectangle", USER_ACCESSIBLE);
        defineParameter("nyquist", "true", USER_ACCESSIBLE);

        // Initialise custom panel interface
        setParameterPanelClass("signalproc.filtering.freqdomain.HetdyneFpanel");
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        freq = new Double((String) getParameter("freq")).doubleValue();
        bandwidth = new Double((String) getParameter("bandwidth")).doubleValue();
        window = (String) getParameter("window");
        nyquist = new Boolean((String) getParameter("nyquist")).booleanValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up NewHetdyneF (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("freq")) {
            freq = Str.strToDouble((String) value);
        }

        if (paramname.equals("bandwidth")) {
            bandwidth = Str.strToDouble((String) value);
        }

        if (paramname.equals("window")) {
            window = (String) value;
        }

        if (paramname.equals("nyquist")) {
            nyquist = Str.strToBoolean((String) value);
        }
    }

    public boolean getRequireDoubleInputs() {
        return ((Boolean) getParameter("requireDoubleInputs")).booleanValue();
    }

    public boolean getCanProcessDoubleArrays() {
        return ((Boolean) getParameter("canProcessDoubleArrays")).booleanValue();
    }

    public void setRequireDoubleInputs(boolean state) {
        setParameter("requireDoubleInputs", new Boolean(state));
    }

    public void setCanProcessDoubleArrays(boolean state) {
        setParameter("canProcessDoubleArrays", new Boolean(state));
    }

    /**
     * @return an array of the input types for NewHetdyneF
     */
    public String[] getInputTypes() {
        return new String[]{"ComplexSpectrum"};
    }

    /**
     * @return an array of the output types for NewHetdyneF
     */
    public String[] getOutputTypes() {
        return new String[]{"ComplexSpectrum"};
    }

}



