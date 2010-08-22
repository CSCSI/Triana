package signalproc.converters;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
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


import triana.types.OldUnit;
import triana.types.Spectrum;
import triana.types.VectorType;


/**
 * A SpectTo2D unit to ..
 *
 * @author Ian Taylor
 * @version 1.0 alpha 13 May 1997
 */
public class SpectToVect extends OldUnit {

    /**
     * ********************************************* Main routine of SpectTo2D which takes in a spectrum and converts it
     * into a 2D data type for input to the Grapher. *********************************************
     */
    public void process() {
        Spectrum wave = (Spectrum) getInputNode(0);

        VectorType spect2D = convert(wave);

        output(spect2D);
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts Spectrum to a VectorType";
    }

    /**
     * Converts a Spectrum data type to a VectorType
     * <p/>
     * return a VectorType
     */
    public static synchronized VectorType convert(Spectrum s) {
        String labelx = "Frequency";
        String labely = "Amp";

        s.setIndependentLabels(0, labelx);
        s.setDependentLabels(0, labely);

        // work out x coordinates :-        
        s.x = s.getScaleReal();
        s.y = s.getData();

        //      System.out.println("Sampling frequency = " + s.samplingFrequency());
        //       double res = (s.samplingFrequency()/2) / s.size();
//        System.out.println("Sampling Res = " + res);

//        for (int i=0; i< s.size(); ++i)
        //          s.x[i]=i*res;

/*        for (int i=0; i< 4; ++i)
            System.out.println(s.x[i]); */
        return s;
    }

    /**
     * Initialses information specific to SpectTo2D.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Reset's SpectTo2D
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of the parameters.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to SpectTo2D, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "Spectrum";
    }

    /**
     * @return a string containing the names of the types output from SpectTo2D, each separated by a white space.
     */
    public String outputTypes() {
        return "VectorType";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "converters.html";
    }
}














