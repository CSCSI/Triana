package signalproc.output;

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


import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;

import org.trianacode.taskgraph.Unit;
import triana.types.Curve;
import triana.types.GraphType;
import triana.types.SampleSet;
import triana.types.VectorType;

/**
 * A grapher unit using external program Gnuplot
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class GnuplotGrapher extends Unit {

    // parameter data type definitions
    private String dumy;
    private String command;
    private boolean autoReplot;    // automatically replot after `set'

    // non-parameter
    private long counter = 0;
    private long process_counter = 0;
    private long collectfeedback_counter = 0;
    private Process gnuplotProcess = null;
    private PrintWriter outputToGnuplot = null;
    private CharArrayWriter tmpOut = new CharArrayWriter(); // command
    private PrintWriter tmpOutHelper = new PrintWriter(tmpOut);
    private CharArrayWriter tmpOut2 = new CharArrayWriter(); // inline data
    private PrintWriter tmpOut2Helper = new PrintWriter(tmpOut2);
    private Vector inputData = null;
    private String[] oldInputTypes = new String[0];

    // others


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        //GraphType input = (GraphType) getInputAtNode(0);
        // Insert main algorithm for GnuplotGrapher
        System.out.println("==> GnuplotGraper " + (counter++));
        ensureRunning();
        prepareInputData();
        displayGraph();
    }

    /**
     * Test whether Gnuplot is running
     */
    private boolean isRunning() {
        if (null == gnuplotProcess) {
            return false;
        }

        try {
            gnuplotProcess.exitValue();
        } catch (IllegalThreadStateException e) {
            return true;
        }

        return false;
    }

    /**
     * Ensure that Gnuplot is running before sending it data
     */
    private void ensureRunning() {

        if (isRunning()) {
            return;
        }

        String[] exePathnames = {"gnuplot", "gnuplot -display :0.0"};

        boolean next = true;
        int n = 0;
        while (next) {
            try {
                String cmd = exePathnames[n];

                //TODO: it'd be better path configurable, e.g. as a parameter?
                if (n < exePathnames.length) {
                    System.out.println("try to execute Gnuplot with pathname: " + cmd);
                    gnuplotProcess = Runtime.getRuntime().exec(cmd);
                } else {
                    throw new RuntimeException("Other possibilities to excute external Gnuplot program?");
                }

                next = false;
            } catch (IOException e) {
                next = true;
            }

            n++;
        }

        outputToGnuplot = new PrintWriter(new OutputStreamWriter(gnuplotProcess.getOutputStream()), true);
        new CollectFeedback(gnuplotProcess.getErrorStream(), "stderr").start();
        new CollectFeedback(gnuplotProcess.getInputStream(), "stdout").start();
        System.out.println("--> gnuplot process " + (process_counter++));
    }

    /**
     * make the input data ready for initializeGraph() and/or displayGraph()
     */
    private void prepareInputData() {
        inputData = new Vector();
        int num = getInputNodeCount();

        if (oldInputTypes.length != num) {
            //initialized = false;
            oldInputTypes = new String[num];
            Arrays.fill(oldInputTypes, "");
        }
        for (int count = 0; count < num; count++) {
            GraphType d;
            String t;
            if (isInputAtNode(count)) {
                d = (GraphType) getInputAtNode(count);
            } else {
                d = new VectorType();
            }
            t = d.getClass().getName();

            inputData.add(d);
            if (!oldInputTypes[count].equals(t)) {
                //initialized = false;
                oldInputTypes[count] = t;
            }
        }
    }

    /**
     * Send Gnuplot commands and data
     */
    private void displayGraph() {
        GraphType input;

        tmpOut.reset();
        tmpOut2.reset();

        for (int count = 0; count < getInputNodeCount(); count++) {
            input = (GraphType) inputData.get(count);
            System.out.println("@@@ gnuplot grapher input is " + input);

            if (input instanceof SampleSet) {
                SampleSet data = (SampleSet) input;
                int len = data.size();
                if (!data.isIndependentComplex(0) && !data.isDependentComplex(0)) {
                    double[] x = data.getXReal();
                    double[] y = data.getGraphReal();

                    // if (getTask().isContinuous())
                    System.out.println("len: " + len);
                    tmpOutHelper.println("plot '-'");
                    for (int i = 0; i < len; i++) {
                        tmpOut2Helper.println("" + x[i] + " " + y[i]);
                    }
                    tmpOut2Helper.println("e");
                }
            } else if (input instanceof VectorType) {
                VectorType data = (VectorType) input;
                int len = data.size();
                if (!data.isIndependentComplex(0) && !data.isDependentComplex(0)) {
                    double[] x = data.getIndependentArrayReal(0);
                    double[] y = data.getGraphReal();

                    System.out.println("len: " + len);
                    tmpOutHelper.println("plot '-'");
                    for (int i = 0; i < len; i++) {
                        tmpOut2Helper.println("" + x[i] + " " + y[i]);
                    }
                    tmpOut2Helper.println("e");
                }
            } else if (input instanceof Curve) {
                Curve data = (Curve) input;
                int len = data.size();
                System.out.println("len: " + len);
                if (!data.isIndependentComplex(0) && data.getDependentVariables() == 1
                        && !data.isDependentComplex(0)) {
                    double[] x = data.getXReal();
                    double[] y = (double[]) data.getGraphArrayReal(-1);

                    tmpOutHelper.println("plot '-'");
                    for (int i = 0; i < len; i++) {
                        tmpOut2Helper.println("" + x[i] + " " + y[i]);
                    }
                    tmpOut2Helper.println("e");
                } else if (data.getDependentVariables() == 2 && !data.isDependentComplex(0)
                        && !data.isDependentComplex(1)) {
                    double[] x = (double[]) data.getGraphArrayReal(-1);
                    double[] y = (double[]) data.getGraphArrayReal(0);

                    tmpOutHelper.println("plot '-'");
                    for (int i = 0; i < len; i++) {
                        tmpOut2Helper.println("" + x[i] + " " + y[i]);
                    }
                    tmpOut2Helper.println("e");
                } else if (data.getDependentVariables() == 3 && !data.isDependentComplex(0)
                        && !data.isDependentComplex(1) && !data.isDependentComplex(2)) {
                    double[] x = (double[]) data.getGraphArrayReal(-1);
                    double[] y = (double[]) data.getGraphArrayReal(0);
                    double[] z = (double[]) data.getGraphArrayReal(1);

                    tmpOutHelper.println("splot '-'");
                    for (int i = 0; i < len; i++) {
                        tmpOut2Helper.println("" + x[i] + " " + y[i] + " " + z[i]);
                    }
                    tmpOut2Helper.println("e");
                }
            }
        }

        reDisplayGraph();
    }

    private void reDisplayGraph() {

        try {
            tmpOut.writeTo(outputToGnuplot);
            tmpOut2.writeTo(outputToGnuplot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputToGnuplot.flush();
    }

    private void reDisplayGraph(String newCommand) {

        try {
            outputToGnuplot.println(newCommand);
            tmpOut2.writeTo(outputToGnuplot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputToGnuplot.flush();
    }

    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("A grapher unit using external program Gnuplot");
        setHelpFileLocation("GnuplotGrapher.html");

        // Define initial value and type of parameters
        defineParameter("dumy", "hello dummy", USER_ACCESSIBLE);
        defineParameter("plotFeedback", "", TRANSIENT);
        defineParameter("command", "", USER_ACCESSIBLE);
        defineParameter("autoReplot", "false", USER_ACCESSIBLE);

        // Initialise custom panel interface
        setParameterPanelClass("signalproc.output.GnuplotGrapherPanel");

        // ???
        //setDefaultNodeRequirement(ESSENTIAL_IF_CONNECTED);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        dumy = (String) getParameter("dumy");
        //plotFeedback
        command = (String) getParameter("command");
        autoReplot = new Boolean((String) getParameter("autoReplot")).booleanValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up GnuplotGrapher (e.g. close open files)
        System.out.println("try to close Gnuplot");
        if (isRunning()) {
            outputToGnuplot.println("exit");
            outputToGnuplot.close();
        }
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("dumy")) {
            dumy = (String) value;
        }

        // plotFeedback not needed

        if (paramname.equals("command")) {
            command = (String) value;
            if (!command.equals("")) {
                setParameter("plotFeedback", "command> " + command);
                ensureRunning();
                if (command.startsWith("replot")) {
                    // only handle replot without extra parameters.
                    // doing it explicitly because we need to pipe data too
                    reDisplayGraph("replot");
                } else if (command.startsWith("plot") || command.startsWith("splot")) {
                    int n = getInputNodeCount();
                    int c = 0;
                    for (int i = 0; i >= 0;) {
                        int j = command.indexOf("'-'", i);
                        int k = command.indexOf("\"-\"", i);
                        i = (j >= 0) ? j + 3 : ((k >= 0) ? k + 3 : -1);
                        if (i >= 0) {
                            c++;
                        }
                    }
                    if (n == c) {
                        reDisplayGraph(command);
                    } else {
                        setParameter("plotFeedback", "Wrong number of input streams ('-'): expected "
                                + n + " but got " + c);
                    }
                } else {
                    outputToGnuplot.println(command);
                    if (autoReplot && command.startsWith("set ")) {
                        reDisplayGraph();
                    }
                    // send a blank command to get back to top level in case of 'help' et el.
                    outputToGnuplot.println("");
                }
            }
        }

        if (paramname.equals("autoReplot")) {
            autoReplot = new Boolean((String) value).booleanValue();
        }
    }


    /**
     * @return an array of the types accepted by each input node. For node indexes not covered the types specified by
     *         getInputTypes() are assumed.
     */
    public String[][] getNodeInputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types accepted by nodes not covered by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return new String[]{"GraphType"};
    }


    /**
     * @return an array of the types output by each output node. For node indexes not covered the types specified by
     *         getOutputTypes() are assumed.
     */
    public String[][] getNodeOutputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types output by nodes not covered by getNodeOutputTypes().
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }

    private class CollectFeedback extends Thread {
        InputStream is;
        String comment;

        CollectFeedback(InputStream is, String comment) {
            this.is = is;
            this.comment = comment;
        }

        public void run() {
            System.out.println(
                    "--> CollectFeedback thread (" + comment + "): " + (collectfeedback_counter++) + " " + this);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String s = "";
                String s2 = "";
                while ((s = br.readLine()) != null) {
                    //s = "(" + comment + ") " + s;

                    // Because setParameter() squeezes the adjacent
                    // identical lines to one, making them not equal
                    // by appending one space does the trick.
                    if (s.equals(s2)) {
                        s += " ";
                    }
                    setParameter("plotFeedback", s);
                    s2 = s;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}



