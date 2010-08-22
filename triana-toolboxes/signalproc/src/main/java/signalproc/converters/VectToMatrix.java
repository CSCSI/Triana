package signalproc.converters;

/**********************************************************************
 The University of Wales, Cardiff Triana Project Software License (Based
 on the Apache Software License Version 1.1)

 Copyright (c) 2003 University of Wales, Cardiff. All rights reserved.

 Redistribution and use of the software in source and binary forms, with
 or without modification, are permitted provided that the following
 conditions are met:

 1.  Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.

 2.  Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any,
 must include the following acknowledgment: "This product includes
 software developed by the University of Wales, Cardiff for the Triana
 Project (http://www.trianacode.org)." Alternately, this
 acknowledgment may appear in the software itself, if and wherever
 such third-party acknowledgments normally appear.

 4. The names "Triana" and "University of Wales, Cardiff" must not be
 used to endorse or promote products derived from this software
 without prior written permission. For written permission, please
 contact triana@trianacode.org.

 5. Products derived from this software may not be called "Triana," nor
 may Triana appear in their name, without prior written permission of
 the University of Wales, Cardiff.

 6. This software may not be sold, used or incorporated into any product
 for sale to third parties.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 THE POSSIBILITY OF SUCH DAMAGE.

 ------------------------------------------------------------------------

 This software consists of voluntary contributions made by many
 individuals on behalf of the Triana Project. For more information on the
 Triana Project, please see. http://www.trianacode.org.

 This license is based on the BSD license as adopted by the Apache
 Foundation and is governed by the laws of England and Wales.

 **********************************************************************/


import java.util.Arrays;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.MatrixType;
import triana.types.VectorType;

/**
 * Convert three vectors of index x, y and data z (same length) to matrix
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class VectToMatrix extends Unit {

    private double fillValue;
    private String dupAction;

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        //VectorType input = (VectorType) getInputAtNode(0);

        // Insert main algorithm for VectToMatrix
        double[] x = ((VectorType) getInputAtNode(0)).getGraphReal();
        double[] y = ((VectorType) getInputAtNode(1)).getGraphReal();
        double[] z = ((VectorType) getInputAtNode(2)).getGraphReal();

        if (!(x.length == y.length && x.length == z.length)) {
            ErrorDialog.show("Index x, y and data z must be in same length");
            return;
        }

        double[] xx = sort_uniq(x);
        double[] yy = sort_uniq(y);
        System.out.println("length (data, xx, yy): " + z.length + ", " + xx.length + ", " + yy.length);
        double[][] zz = new double[xx.length][yy.length];
        boolean[][] seen = new boolean[xx.length][yy.length];

        for (int i = 0; i < xx.length; i++) {
            for (int j = 0; j < yy.length; j++) {
                zz[i][j] = fillValue;
            }
        }

        for (int i = 0; i < z.length; i++) {
            int ix = Arrays.binarySearch(xx, x[i]);
            int iy = Arrays.binarySearch(yy, y[i]);
            if (!seen[ix][iy]) {
                zz[ix][iy] = z[i];
                seen[ix][iy] = true;
            } else {
                System.out.println("seen at (i, ix, iy, z[i]): " + i + ", " + ix + ", " + iy + ", " + z[i]);
                if (dupAction.equals("Last")) {
                    zz[ix][iy] = z[i];
                } else if (dupAction.equals("First")) {
                    ;
                } else if (dupAction.equals("Max")) {
                    zz[ix][iy] = Math.max(zz[ix][iy], z[i]);
                } else if (dupAction.equals("Min")) {
                    zz[ix][iy] = Math.min(zz[ix][iy], z[i]);
                }
            }
        }

        output(new MatrixType(xx, yy, zz));
    }

    private double[] sort_uniq(double[] a) {
        double[] copy = new double[a.length];
        System.arraycopy(a, 0, copy, 0, a.length);
        Arrays.sort(copy);
        int dup = 0;
        double old = Double.NaN;
        for (int i = 0; i < copy.length; i++) {
            if (old == copy[i]) {
                dup++;
            } else {
                old = copy[i];
            }
        }
        double[] r = new double[copy.length - dup];
        old = Double.NaN;
        for (int i = 0, j = 0; i < copy.length; i++) {
            if (old != copy[i]) {
                old = r[j++] = copy[i];
            }
        }

        return r;
    }

    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(3);
        setMinimumInputNodes(3);
        setMaximumInputNodes(3);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Convert three vectors of index x, y and data z (same length) to matrix");
        setHelpFileLocation("VectToMatrix.html");

        defineParameter("fillValue", "NaN", USER_ACCESSIBLE);
        defineParameter("dupAction", "Last", USER_ACCESSIBLE);

        String guilines = "";
        guilines += "Default value to fill the matrix $title fillValue TextField 0.0 \n";
        guilines
                += "When data z value not unique wrt. the same index x and y, then use $title dupAction Choice [Last] [First] [Max] [Min]\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        fillValue = new Double((String) getParameter("fillValue")).doubleValue();
        dupAction = (String) getParameter("dupAction");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up VectToMatrix (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("fillValue")) {
            fillValue = new Double((String) value).doubleValue();
        }

        if (paramname.equals("dupAction")) {
            dupAction = (String) value;
        }

    }


    /**
     * @return an array of the input types for VectToMatrix
     */
    public String[] getInputTypes() {
        return new String[]{"VectorType"};
    }

    /**
     * @return an array of the output types for VectToMatrix
     */
    public String[] getOutputTypes() {
        return new String[]{"MatrixType"};
    }

}



