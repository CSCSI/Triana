package signalproc.output;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Vector;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSampleSet;
import triana.types.ComplexSpectrum;
import triana.types.Curve;
import triana.types.GraphType;
import triana.types.Histogram;
import triana.types.MatrixType;
import triana.types.SampleSet;
import triana.types.Spectrum;
import triana.types.VectorType;

/**
 * A grapher unit using external program Grace
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */


public class GraceGrapher extends Unit {

    // in point (inch*72)
    private static final int DISPLAY_W = 200;
    private static final int DISPLAY_H = 154;

    // dot per inch (for display i.e. pixel per inch)
    private static final int DISPLAY_DPI = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();

    // parameter data type definitions
    private boolean singleGraph;
    private String pageSizeDisplay;
    private int pageSizeDisplayCustomW, pageSizeDisplayCustomH;
    private boolean freePageLayout;
    private String parameterFile;
    private boolean zaxisLogarithmic;
    private float zCutoffFraction;
    private double zThresholdByMedian;
    private double zRangeLower;
    private double zRangeUpper;
    private boolean reInitialize;

    private String title_string; // meanful only for a single graph
    private String legend_string; // meanful only for a single curve
    private String xlabel_string; // meanful only for a single graph
    private String ylabel_string; // meanful only for a single graph

    // non-parameter
    private Process grace = null;
    private PrintWriter output = null;
    private boolean initialized = false;
    private Vector inputData = null;
    private long counter = 0;

    private String[] oldInputTypes = new String[0];

    /**
     * test whether Grace is running
     */
    private boolean isRunning() {
        if (null == grace) {
            return false;
        }

        try {
            grace.exitValue();
        } catch (IllegalThreadStateException e) {
            return true;
        }

        return false;
    }

    /**
     * ensure that Grace is running before sending it data
     */
    private void ensureGraceRunning() {

        if (isRunning()) {
            return;
        }

        String[] exePathnames = {"grace.bat", "xmgrace", "xmgrace -display :0.0"};

        boolean next = true;
        int n = 0;
        while (next) {
            try {
                initialized = false;
                String cmd = exePathnames[n];
                cmd += " -dpipe 0 -noask";

                //TODO: it'd be better path configurable!!!
                if (n < exePathnames.length) {
                    if (freePageLayout) {
                        cmd += " -free";
                    }
                    System.out.println("try to execute Grace with pathname: " + cmd);
                    grace = Runtime.getRuntime().exec(cmd);
                } else {
                    throw new RuntimeException("Other possibilities to excute external Grace program?");
                }

                next = false;
            } catch (IOException e) {
                next = true;
            }

            n++;
        }

        output = new PrintWriter(grace.getOutputStream(), true);
    }

    /**
     * make the input data ready for initializeGraph() and/or displayGraph()
     */
    private void prepareInputData() {
        inputData = new Vector();
        int num = getInputNodeCount();

        if (oldInputTypes.length != num) {
            initialized = false;
            oldInputTypes = new String[getInputNodeCount()];
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
                initialized = false;
                oldInputTypes[count] = t;
            }
        }
    }

    /**
     * initialize the graph, e.g. title, axes labels, legends, etc.
     */
    private void initializeGraph() {
        GraphType input;
        String xlabel, ylabel, title, legend;

        // killing or hiding graph both not really work, so
        output.println("flush");
        output.println("new");
        output.println("version 50107");

        if (freePageLayout) {
        } else if (pageSizeDisplay.equals("Custom")) {
            output.println("device \"X11\" dpi " + DISPLAY_DPI);
            int w = pageSizeDisplayCustomW * DISPLAY_DPI / 72;
            int h = pageSizeDisplayCustomH * DISPLAY_DPI / 72;
            output.println("device \"X11\" page size " + w + ", " + h);
        } else {
        }

        {
            output.println("map color 0 to (255, 255, 255), \"white\"");
            output.println("map color 1 to (0, 0, 0), \"black\"");
            output.println("map color 2 to (255, 0, 0), \"red\"");
            output.println("map color 3 to (0, 255, 0), \"green\"");
            output.println("map color 4 to (0, 0, 255), \"blue\"");
            output.println("map color 5 to (255, 255, 0), \"yellow\"");
            output.println("map color 6 to (188, 143, 143), \"brown\"");
            output.println("map color 7 to (220, 220, 220), \"grey\"");
            output.println("map color 8 to (148, 0, 211), \"violet\"");
            output.println("map color 9 to (0, 255, 255), \"cyan\"");
            output.println("map color 10 to (255, 0, 255), \"magenta\"");
            output.println("map color 11 to (255, 165, 0), \"orange\"");
            output.println("map color 12 to (114, 33, 188), \"indigo\"");
            output.println("map color 13 to (103, 7, 72), \"maroon\"");
            output.println("map color 14 to (64, 224, 208), \"turquoise\"");
            output.println("map color 15 to (0, 139, 0), \"green4\"");
            output.println("map color 16 to (0, 0, 127), \"color_0\"");
            output.println("map color 17 to (0, 0, 143), \"color_1\"");
            output.println("map color 18 to (0, 0, 159), \"color_2\"");
            output.println("map color 19 to (0, 0, 175), \"color_3\"");
            output.println("map color 20 to (0, 0, 191), \"color_4\"");
            output.println("map color 21 to (0, 0, 207), \"color_5\"");
            output.println("map color 22 to (0, 0, 223), \"color_6\"");
            output.println("map color 23 to (0, 0, 239), \"color_7\"");
            output.println("map color 24 to (0, 0, 255), \"color_8\"");
            output.println("map color 25 to (0, 11, 255), \"color_9\"");
            output.println("map color 26 to (0, 27, 255), \"color_10\"");
            output.println("map color 27 to (0, 43, 255), \"color_11\"");
            output.println("map color 28 to (0, 59, 255), \"color_12\"");
            output.println("map color 29 to (0, 75, 255), \"color_13\"");
            output.println("map color 30 to (0, 91, 255), \"color_14\"");
            output.println("map color 31 to (0, 107, 255), \"color_15\"");
            output.println("map color 32 to (0, 123, 255), \"color_16\"");
            output.println("map color 33 to (0, 139, 255), \"color_17\"");
            output.println("map color 34 to (0, 155, 255), \"color_18\"");
            output.println("map color 35 to (0, 171, 255), \"color_19\"");
            output.println("map color 36 to (0, 187, 255), \"color_20\"");
            output.println("map color 37 to (0, 203, 255), \"color_21\"");
            output.println("map color 38 to (0, 219, 255), \"color_22\"");
            output.println("map color 39 to (0, 235, 255), \"color_23\"");
            output.println("map color 40 to (0, 251, 255), \"color_24\"");
            output.println("map color 41 to (7, 255, 247), \"color_25\"");
            output.println("map color 42 to (23, 255, 231), \"color_26\"");
            output.println("map color 43 to (39, 255, 215), \"color_27\"");
            output.println("map color 44 to (55, 255, 199), \"color_28\"");
            output.println("map color 45 to (71, 255, 183), \"color_29\"");
            output.println("map color 46 to (87, 255, 167), \"color_30\"");
            output.println("map color 47 to (103, 255, 151), \"color_31\"");
            output.println("map color 48 to (119, 255, 135), \"color_32\"");
            output.println("map color 49 to (135, 255, 119), \"color_33\"");
            output.println("map color 50 to (151, 255, 103), \"color_34\"");
            output.println("map color 51 to (167, 255, 87), \"color_35\"");
            output.println("map color 52 to (183, 255, 71), \"color_36\"");
            output.println("map color 53 to (199, 255, 55), \"color_37\"");
            output.println("map color 54 to (215, 255, 39), \"color_38\"");
            output.println("map color 55 to (231, 255, 23), \"color_39\"");
            output.println("map color 56 to (247, 255, 7), \"color_40\"");
            output.println("map color 57 to (255, 247, 0), \"color_41\"");
            output.println("map color 58 to (255, 231, 0), \"color_42\"");
            output.println("map color 59 to (255, 215, 0), \"color_43\"");
            output.println("map color 60 to (255, 199, 0), \"color_44\"");
            output.println("map color 61 to (255, 183, 0), \"color_45\"");
            output.println("map color 62 to (255, 167, 0), \"color_46\"");
            output.println("map color 63 to (255, 151, 0), \"color_47\"");
            output.println("map color 64 to (255, 135, 0), \"color_48\"");
            output.println("map color 65 to (255, 119, 0), \"color_49\"");
            output.println("map color 66 to (255, 103, 0), \"color_50\"");
            output.println("map color 67 to (255, 87, 0), \"color_51\"");
            output.println("map color 68 to (255, 71, 0), \"color_52\"");
            output.println("map color 69 to (255, 55, 0), \"color_53\"");
            output.println("map color 70 to (255, 39, 0), \"color_54\"");
            output.println("map color 71 to (255, 23, 0), \"color_55\"");
            output.println("map color 72 to (255, 7, 0), \"color_56\"");
            output.println("map color 73 to (246, 0, 0), \"color_57\"");
            output.println("map color 74 to (228, 0, 0), \"color_58\"");
            output.println("map color 75 to (211, 0, 0), \"color_59\"");
            output.println("map color 76 to (193, 0, 0), \"color_60\"");
            output.println("map color 77 to (175, 0, 0), \"color_61\"");
            output.println("map color 78 to (158, 0, 0), \"color_62\"");
            output.println("map color 79 to (140, 0, 0), \"color_63\"");
            output.println("map color 80 to (120, 0, 0), \"color_63\"");

            output.println("box loctype view");
            output.println("box linestyle 0");
            output.println("box linewidth 0");
            output.println("box fill pattern 1");
            double y = 0.2;
            for (int i = 0; i < 64; i++) {
                output.println("with box " + i);
                output.println("box off");
                output.println("box fill color \"color_" + i + "\"");
                output.println("box 1.05, " + y + ", 1.1, " + (y + 0.01));
                output.println("box def");
                y += 0.01;
            }

            output.println("string loctype view");
            output.println("string color 1");
            output.println("string just 12");
            y = 0.2;
            for (int i = 0; i < 64; i++) {
                output.println("with string " + i);
                output.println("string off");
                output.println("string 1.1, " + y);
                output.println("box def");
                y += 0.01;
            }
        }

        if (parameterFile.trim().length() > 0) {
            BufferedReader par = null;
            String s;

            try {
                par = new BufferedReader(new FileReader(new File(parameterFile.trim())));
            } catch (java.io.FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                if (par != null) {
                    while ((s = par.readLine()) != null) {
                        output.println(s);
                    }
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        if (singleGraph) {
            output.println("g0 on");
            output.println("arrange(1, 1, 0.2, 0.9, 0.9)");
            output.println("focus g0");
            output.println("with g0");
            input = (GraphType) inputData.get(0);

            title = input.getTitle();
            if (title == null) {
                title = "(n/a)";
            }
            xlabel = input.getIndependentLabels(0);
            if (xlabel == null) {
                xlabel = "(n/a)";
            }
            ylabel = input.getDependentLabels(0);
            if (ylabel == null) {
                ylabel = "(n/a)";
            }
            legend = title;

            if (!title_string.equals("")) {
                title = title_string;
            }
            if (!xlabel_string.equals("")) {
                xlabel = xlabel_string;
            }
            if (!ylabel_string.equals("")) {
                ylabel = ylabel_string;
            }
            if (!legend_string.equals("")) {
                legend = legend_string;
            }

            output.println("title " + "\"" + title + "\"");
            output.println("xaxis label " + "\"" + xlabel + "\"");
            output.println("yaxis label " + "\"" + ylabel + "\"");
            output.println("g0.s0 legend " + "\"" + legend + "\"");
            for (int count = 1 /* one */; count < getInputNodeCount(); count++) {
                input = (GraphType) inputData.get(count);
// 		title += "\\n" + input.getTitle();
// 		xlabel += "\\n" + input.getIndependentLabels(0);
// 		ylabel += "\\n" + input.getDependentLabels(0);
                legend = input.getTitle();
                if (legend == null) {
                    legend = "(n/a)";
                }
                output.println("g0.s" + count + " legend " + "\"" + legend + "\"");
            }
            output.println("legend 0.99, 0.99");

        } else {
            int m = getInputNodeCount();
            for (int count = 0 /* zero */; count < m; count++) {
                output.println("g" + count + " on");
            }
            output.println("arrange(" + m + ", 1, 0.2, 0.9, 0.9)");
            for (int count = 0 /* zero */; count < m; count++) {
                input = (GraphType) inputData.get(count);
                title = input.getTitle();
                if (title == null) {
                    title = "(n/a)";
                }
                xlabel = input.getIndependentLabels(0);
                if (xlabel == null) {
                    xlabel = "(n/a)";
                }
                ylabel = input.getDependentLabels(0);
                if (ylabel == null) {
                    ylabel = "(n/a)";
                }

                output.println("focus g" + count);
                output.println("with g" + count);
                output.println("g" + count + ".s0 legend " + "\"" + title + "\"");

                double f = (m - count) / (double) m;
                output.println("legend 0.99, " + f);

                output.println("title " + "\"" + title + "\"");
                output.println("xaxis label " + "\"" + xlabel + "\"");
                output.println("yaxis label " + "\"" + ylabel + "\"");
            }
        }

        initialized = true;
    }

    /**
     * send Grace data and let it do the real graphical work
     */
    private void displayGraph() {
        GraphType input;
        int gnum, snum;

        for (int count = 0; count < getInputNodeCount(); count++) {

            if (singleGraph) {
                gnum = 0;
                snum = count;
            } else {
                gnum = count;
                snum = 0;
            }

            input = (GraphType) inputData.get(count);
            System.out.println("@@@ grace grapher input is " + input);

            if (input instanceof SampleSet) {
                SampleSet data = (SampleSet) input;
                int len = data.size();
                if (!data.isIndependentComplex(0) && !data.isDependentComplex(0)) {
                    double[] x = data.getXReal();
                    double[] y = data.getGraphReal();

                    // if (getTask().isContinuous())
                    output.println("kill g" + gnum + ".s" + snum + " saveall");
                    output.println("with g" + gnum);
                    for (int i = 0; i < len; i++) {
                        output.println("g" + gnum + ".s" + snum + " point " + x[i] + ", " + y[i]);
                    }
// 		    output.println("world xmin " + x[0]);
// 		    output.println("world xmax " + x[x.length-1]);
// 		    output.println("autoticks");
                    output.println("autoscale");
                }
            } else if (input instanceof ComplexSampleSet) {
                ComplexSampleSet data = (ComplexSampleSet) input;
                int len = data.size();
                if (data.isDependentComplex(0)) {
                    double[] x = data.getXReal();
                    double[] y = data.getGraphReal();
                    double[] z = data.getGraphImag();

                    output.println("kill g" + gnum + ".s" + snum + " saveall");
                    output.println("with g" + gnum);
                    for (int i = 0; i < len; i++) {
                        output.println("g" + gnum + ".s" + snum + " point " + x[i] + ", " + y[i]);
                    }
                    for (int i = 0; i < len; i++) {
                        output.println(
                                "g" + gnum + ".s" + (snum + getInputNodeCount()) + " point " + x[i] + ", " + z[i]);
                    }
                    output.println("autoscale");
                }
            } else if (input instanceof Histogram) {
                Histogram data = (Histogram) input;
                int len = data.size();
                if (!data.isIndependentComplex(0) && !data.isDependentComplex(0)) {
                    double[] x = data.getFiniteDelimiters(0);
                    double[] y = data.getGraphReal();

                    output.println("kill g" + gnum + ".s" + snum + " saveall");
                    output.println("with g" + gnum);
                    for (int i = 0; i < len; i++) {
                        output.println("g" + gnum + ".s" + snum + " point " + x[i] + ", " + y[i]);
                    }
                    output.println("g" + gnum + ".s" + snum + "point " + x[len] + ", " + y[len - 1]);
                    output.println("g" + gnum + ".s" + snum + " line color " + (snum + 1));
                    // output.println("g" + gnum + ".s" + snum + " symbol color " + (snum+1));
                    // output.println("g" + gnum + ".s" + snum + " type bar");
                    output.println("g" + gnum + ".s" + snum + " line type 3");
                    // output.println("g" + gnum + ".s" + snum + " fill type 2");
                    // output.println("g" + gnum + ".s" + snum + " fill color " + (snum+1));
                    output.println("g" + gnum + ".s" + snum + " dropline on");
                    // output.println("g" + gnum + ".s" + snum + " line pattern 4");
                    output.println("autoscale");
                }
            } else if (input instanceof Curve) {
                Curve data = (Curve) input;
                int len = data.size();
                if (!data.isIndependentComplex(0) && data.getDependentVariables() == 1
                        && !data.isDependentComplex(0)) {
                    double[] x = data.getXReal();
                    double[] y = (double[]) data.getGraphArrayReal(-1);

                    output.println("kill g" + gnum + ".s" + snum + " saveall");
                    output.println("with g" + gnum);
                    for (int i = 0; i < len; i++) {
                        output.println("g" + gnum + ".s" + snum + " point " + x[i] + ", " + y[i]);
                    }
                    output.println("autoscale");
                } else if (data.getDependentVariables() == 2 && !data.isDependentComplex(0)
                        && !data.isDependentComplex(1)) {
                    double[] x = (double[]) data.getGraphArrayReal(-1);
                    double[] y = (double[]) data.getGraphArrayReal(0);

                    output.println("kill g" + gnum + ".s" + snum + " saveall");
                    output.println("with g" + gnum);
                    for (int i = 0; i < len; i++) {
                        output.println("g" + gnum + ".s" + snum + " point " + x[i] + ", " + y[i]);
                    }
                    output.println("autoscale");
                } else if (data.getDependentVariables() == 3 && !data.isDependentComplex(0)
                        && !data.isDependentComplex(1) && !data.isDependentComplex(2)) {
                    double[] x = (double[]) data.getGraphArrayReal(-1);
                    double[] y = (double[]) data.getGraphArrayReal(0);
                    double[] z = (double[]) data.getGraphArrayReal(1);
                    plotColorMap(x, y, z, gnum, snum);
                }
            } else if (input instanceof MatrixType) {
                MatrixType data = (MatrixType) input;

                double[] xs = data.getXorYReal(0);
                double[] ys = data.getXorYReal(1);
                double[][] zs = data.getDataReal();

                int n = xs.length * ys.length;
                for (int i = 0; i < xs.length; i++) {
                    for (int j = 0; j < ys.length; j++) {
                        if (Double.isNaN(zs[i][j])) {
                            n--;
                        }
                    }
                }

                double[] x = new double[n];
                double[] y = new double[n];
                double[] z = new double[n];

                int idx = 0;
                for (int i = 0; i < xs.length; i++) {
                    for (int j = 0; j < ys.length; j++) {
                        if (!Double.isNaN(zs[i][j])) {
                            x[idx] = xs[i];
                            y[idx] = ys[j];
                            z[idx] = zs[i][j];
                            idx++;
                        }
                    }
                }
                plotColorMap(x, y, z, gnum, snum);
            } else if (input instanceof Spectrum) {
                Spectrum data = (Spectrum) input;
                int len = data.size();

                if (!data.isDependentComplex(0)) {
                    double[] x = data.getFrequencyArray(0);
                    double[] y = (double[]) data.getOrderedSpectrumReal();

                    output.println("kill g" + gnum + ".s" + snum + " saveall");
                    output.println("with g" + gnum);
                    output.println("yaxes scale logarithmic");
                    for (int i = 0; i < len; i++) {
                        output.println("g" + gnum + ".s" + snum + " point " + x[i] + ", " + y[i]);
                    }
                    output.println("autoscale");
                }
            } else if (input instanceof ComplexSpectrum) {
                ComplexSpectrum data = (ComplexSpectrum) input;
                int len = data.size();

                double[] x = data.getFrequencyArray(0);
                double[] y = (double[]) data.getOrderedSpectrumReal();
                double[] z = (double[]) data.getOrderedSpectrumImag();
                double[] amp = new double[len];

                for (int i = 0; i < len; i++) {
                    amp[i] = Math.sqrt(y[i] * y[i] + z[i] * z[i]);
                }

                output.println("kill g" + gnum + ".s" + snum + " saveall");
                output.println("with g" + gnum);
                output.println("yaxes scale logarithmic");
                for (int i = 0; i < len; i++) {
                    output.println("g" + gnum + ".s" + snum + " point " + x[i] + ", " + amp[i]);
                }
// 		for (int i=0; i<len ; i++)
// 		    output.println("g" + gnum + ".s" + (snum+getInputNodeComponentCount())  + " point " + x[i] + ", " + y[i]);
// 		for (int i=0; i<len ; i++)
// 		    output.println("g" + gnum + ".s" + (snum+getInputNodeComponentCount()*2)  + " point " + x[i] + ", " + z[i]);
                output.println("autoscale");
            } else if (input instanceof VectorType) {
                VectorType data = (VectorType) input;
                int len = data.size();
                if (!data.isIndependentComplex(0) && !data.isDependentComplex(0)) {
                    double[] x = data.getIndependentArrayReal(0);
                    double[] y = data.getGraphReal();

                    output.println("kill g" + gnum + ".s" + snum + " saveall");
                    output.println("with g" + gnum);
                    for (int i = 0; i < len; i++) {
                        output.println("g" + gnum + ".s" + snum + " point " + x[i] + ", " + y[i]);
                    }
                }
                output.println("autoscale");
            } else {        // catch all plot
// 		int xs = input.getIndependentVariables();
// 		int ys = input.getDependentVariables();
// 		int xlen = input.getIndependentArrayReal(0).length;
// 		int ylen = ((double[])input.getDataArrayRealAsDoubles(0)).length;
// 		Vector x = new Vector();
// 		Vector xi = new Vector();
// 		Vector y = new Vector();
// 		Vector yi = new Vector();

// 		for (int i=0; i<xs; i++) {
// 		    x.add(input.getIndependentArrayReal(i));
// 		    xi.add(input.getIndependentArrayImag(i));
// 		}

// 		for (int i=0; i<ys; i++) {
// 		    y.add(input.getDataArrayRealAsDoubles(i));
// 		    yi.add(input.getDataArrayImagAsDoubles(i));
// 		}

// 		output.println("kill g" + gnum + ".s" + snum + " saveall");
// 		output.println("with g" + gnum);
// 		for (int j=0; j<xlen-1; j++) // one data point less just for safety
// 		    output.println("g" + gnum + ".s" + snum  + " point "
// 				   + ((double[]) x.get(0))[j] + ", "
// 				   + ((double[]) y.get(0))[j]);

// 		output.println("autoscale");
            }
        }

        output.println("redraw");
    }

    private void plotColorMap(double[] x, double[] y, double[] z, int gnum, int snum) {
        if (!(x.length == y.length && x.length == z.length)) {
            return;
        }

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        int len = z.length;

        for (int i = 0; i < z.length; i++) {
            max = Math.max(max, z[i]);
            min = Math.min(min, z[i]);
        }
        double[] zz = new double[z.length];
        if (min >= max) {
            Arrays.fill(zz, 16);
        } else {
            if (zCutoffFraction > 0.0) { // not a good way, median might better
                int nbins = (len <= 50) ? 6 : 6 + (int) Math.round(6. * Math.log(len / 50.) / Math.log(2.));
                int[] hist = new int[nbins];
                int[] bidx = new int[len];
                double[] delim = new double[nbins + 1];

                delim[0] = min;
                for (int i = 1; i < nbins; i++) {
                    delim[i] = delim[i - 1] + (max - min) / nbins;
                }
                delim[nbins] = (max < Double.MAX_VALUE) ? max + Double.MIN_VALUE : max;

                for (int i = 0; i < len; i++) {
                    for (int j = 0; j < nbins; j++) {
                        if (z[i] >= delim[j] && z[i] <= delim[j + 1]) {
                            hist[j]++;
                            bidx[i] = j;
                            break;
                        }
                    }
                }

                for (int i = 0; i < nbins; i++) {
                    System.out.println("hist[" + i + "]: " + hist[i]);
                }

                int hmax = 0;
                for (int i = 0; i < nbins; i++) {
                    hmax = Math.max(hmax, hist[i]);
                }
                int hcut = (int) Math.round(hmax * zCutoffFraction / 100.0);

                int nlen = len;
                for (int i = 0; i < len; i++) {
                    if (hist[bidx[i]] <= hcut) {
                        nlen--;
                    }
                }

                double[] nx = new double[nlen];
                double[] ny = new double[nlen];
                double[] nz = new double[nlen];
                for (int i = 0, j = 0; i < len; i++) {
                    if (hist[bidx[i]] > hcut) {
                        nx[j] = x[i];
                        ny[j] = y[i];
                        nz[j] = z[i];
                        j++;
                    }
                }

                double nmin = Double.POSITIVE_INFINITY;
                double nmax = Double.NEGATIVE_INFINITY;
                for (int i = 0; i < nlen; i++) {
                    nmax = Math.max(nmax, nz[i]);
                    nmin = Math.min(nmin, nz[i]);
                }
                System.out.println(
                        "min, max, len; nmin, nmax, nlen; nbins, hmax, hcut: " + min + ", " + max + ", " + len + "; "
                                + nmin + ", " + nmax + ", " + nlen + "; " + nbins + ", " + hmax + ", " + hcut);
                min = nmin;
                max = nmax;
                len = nlen;
                x = nx;
                y = ny;
                z = nz;
            }
            if (!Double.isInfinite(zRangeLower) || !Double.isInfinite(zRangeUpper)) {
                int nlen = len;
                for (int i = 0; i < len; i++) {
                    if (z[i] < zRangeLower || z[i] > zRangeUpper) {
                        nlen--;
                    }
                }
                double[] nx = new double[nlen];
                double[] ny = new double[nlen];
                double[] nz = new double[nlen];
                for (int i = 0, j = 0; i < len; i++) {
                    if (z[i] >= zRangeLower && z[i] <= zRangeUpper) {
                        nx[j] = x[i];
                        ny[j] = y[i];
                        nz[j] = z[i];
                        j++;
                    }
                }
                double nmin = Double.POSITIVE_INFINITY;
                double nmax = Double.NEGATIVE_INFINITY;
                for (int i = 0; i < nlen; i++) {
                    nmax = Math.max(nmax, nz[i]);
                    nmin = Math.min(nmin, nz[i]);
                }
                System.out.println(
                        "min, max, len; nmin, nmax, nlen: " + min + ", " + max + ", " + len + "; " + nmin + ", " + nmax
                                + ", " + nlen);
                min = nmin;
                max = nmax;
                len = nlen;
                x = nx;
                y = ny;
                z = nz;
            }
            if (zThresholdByMedian > 0.0) {
                double[] ztmp = new double[len];
                System.arraycopy(z, 0, ztmp, 0, len);
                Arrays.sort(ztmp);
                double median = ztmp[len / 2];
                int nlen = len;
                for (int i = 0; i < len; i++) {
                    if (Math.abs(z[i] - median) > zThresholdByMedian) {
                        nlen--;
                    }
                }
                double[] nx = new double[nlen];
                double[] ny = new double[nlen];
                double[] nz = new double[nlen];
                for (int i = 0, j = 0; i < len; i++) {
                    if (Math.abs(z[i] - median) <= zThresholdByMedian) {
                        nx[j] = x[i];
                        ny[j] = y[i];
                        nz[j] = z[i];
                        j++;
                    }
                }
                double nmin = Double.POSITIVE_INFINITY;
                double nmax = Double.NEGATIVE_INFINITY;
                for (int i = 0; i < nlen; i++) {
                    nmax = Math.max(nmax, nz[i]);
                    nmin = Math.min(nmin, nz[i]);
                }
                System.out.println(
                        "min, max, len; nmin, nmax, nlen; median: " + min + ", " + max + ", " + len + "; " + nmin + ", "
                                + nmax + ", " + nlen + "; " + median);
                min = nmin;
                max = nmax;
                len = nlen;
                x = nx;
                y = ny;
                z = nz;
            }
            if (max == min) {
                for (int i = 0; i < z.length; i++) {
                    zz[i] = 16;
                }
            } else if (!zaxisLogarithmic) {
                for (int i = 0; i < z.length; i++) {
                    zz[i] = (z[i] - min) * 64.0 / (max - min) + 16;
                }
            } else {
                if (min > 0) {
                    min = Math.log(min) * 0.43429448190325182765d;
                    max = Math.log(max) * 0.43429448190325182765d;
                    for (int i = 0; i < z.length; i++) {
                        zz[i] = (Math.log(z[i]) * 0.43429448190325182765d - min) * 64.0 / (max - min) + 16;
                    }
                } else {
                    ErrorDialog.show("Positive data expected");
                    return;
                }
            }
        }

        output.println("kill g" + gnum + ".s" + snum + " saveall");
        output.println("with g" + gnum);
        output.println("g" + gnum + ".s" + snum + " type xycolor");
        for (int i = 0; i < len; i++) {
            output.println("g" + gnum + ".s" + snum + " point " + x[i] + ", " + y[i]);
            output.println("g" + gnum + ".s" + snum + ".y1[g" + gnum + ".s" + snum + ".length-1]=" + zz[i]);
        }

        output.println("g" + gnum + ".s" + snum + " line type 0");
        output.println("g" + gnum + ".s" + snum + " symbol " + (snum + 1));
        output.println("g" + gnum + ".s" + snum + " symbol fill pattern 1");
        output.println("g" + gnum + ".s" + snum + " symbol linestyle 0");
        output.println("view xmax 1");

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(7);
        nf.setGroupingUsed(false);
        for (int i = 0; i < 64; i++) { // 64 colors
            output.println("with box " + i);
            output.println("box on");

            if (i % 16 == 0 || i == 63) {
                output.println("with string " + i);
                output.println("string def \"" + nf.format(min + i * (max - min) / 64) + "\"");
                output.println("string on");
            }
        }

        output.println("autoscale");
    }

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        // GraphType input;

        // Insert main algorithm for GraceGrapher
        System.out.println("GraceGraper " + (counter++));
        ensureGraceRunning();
        prepareInputData();
        if (!initialized || reInitialize) {
            initializeGraph();
        }
        displayGraph();
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);

        // Initialise parameter update policy
        setParameterUpdatePolicy(Task.PROCESS_UPDATE);

        setPopUpDescription("A grapher unit using external program Grace");
        setHelpFileLocation("GraceGrapher.html");

        // Define initial value and type of parameters
        defineParameter("singleGraph", "true", USER_ACCESSIBLE);
        defineParameter("pageSizeDisplay", "Grace default", USER_ACCESSIBLE);
        defineParameter("pageSizeDisplayCustomW", Integer.toString(DISPLAY_W), USER_ACCESSIBLE);
        defineParameter("pageSizeDisplayCustomH", Integer.toString(DISPLAY_H), USER_ACCESSIBLE);
        defineParameter("freePageLayout", "false", USER_ACCESSIBLE);
        defineParameter("parameterFile", "", USER_ACCESSIBLE);
        defineParameter("zaxisLogarithmic", "false", USER_ACCESSIBLE);
        defineParameter("zCutoffFraction", "0.0", INTERNAL); // hides it at first.
        defineParameter("zThresholdByMedian", "NaN", USER_ACCESSIBLE);
        defineParameter("zRangeLower", Double.toString(Double.NEGATIVE_INFINITY), USER_ACCESSIBLE);
        defineParameter("zRangeUpper", Double.toString(Double.POSITIVE_INFINITY), USER_ACCESSIBLE);
        defineParameter("reInitialize", "true", USER_ACCESSIBLE);

        defineParameter("title_string", "", USER_ACCESSIBLE);
        defineParameter("xlabel_string", "", USER_ACCESSIBLE);
        defineParameter("ylabel_string", "", USER_ACCESSIBLE);
        defineParameter("legend_string", "", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Display in one single graph: $title singleGraph Checkbox true\n";
        guilines += "Reinitialize the graph by each run: $title reInitialize Checkbox true\n";
        guilines += "Page size just for display (in pixel): $title pageSizeDisplay Choice [Custom] [Grace default]\n";
        guilines += "Custom width: $title pageSizeDisplayCustomW TextField\n";
        guilines += "Custom height: $title pageSizeDisplayCustomH TextField\n";
        guilines += "Use Free Page Layout: $title freePageLayout Checkbox false\n";
        guilines += "Grace parameter file: $title parameterFile File null *.par\n";
        //guilines += "Z insensity histogram cutoff fraction (in percentile): $title zCutoffFraction TextField\n";
        guilines += "Lower bound of Z range to display: $title zRangeLower TextField\n";
        guilines += "Upper bound of Z range to display: $title zRangeUpper TextField\n";
        guilines += "Z outliers cutoff threshold (abs(z-median(z)) > threshold): $title zThresholdByMedian TextField\n";
        guilines += "Z by color map (XYCOLOR Plot) in logarithmic scale: $title zaxisLogarithmic Checkbox false\n";
        setGUIBuilderV2Info(guilines);

        setDefaultNodeRequirement(ESSENTIAL_IF_CONNECTED);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        singleGraph = new Boolean((String) getParameter("singleGraph")).booleanValue();
        pageSizeDisplay = (String) getParameter("pageSizeDisplay");
        pageSizeDisplayCustomW = new Integer((String) getParameter("pageSizeDisplayCustomW")).intValue();
        pageSizeDisplayCustomH = new Integer((String) getParameter("pageSizeDisplayCustomH")).intValue();
        freePageLayout = new Boolean((String) getParameter("freePageLayout")).booleanValue();
        parameterFile = (String) getParameter("parameterFile");
        zaxisLogarithmic = new Boolean((String) getParameter("zaxisLogarithmic")).booleanValue();
        zCutoffFraction = new Float((String) getParameter("zCutoffFraction")).floatValue();
        zThresholdByMedian = new Double((String) getParameter("zThresholdByMedian")).doubleValue();
        zRangeLower = new Double((String) getParameter("zRangeLower")).doubleValue();
        zRangeUpper = new Double((String) getParameter("zRangeUpper")).doubleValue();
        reInitialize = new Boolean((String) getParameter("reInitialize")).booleanValue();

        title_string = "";
        xlabel_string = "";
        ylabel_string = "";
        legend_string = "";

        initialized = false;
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up GraceGrapher (e.g. close open files)
        System.out.println("try to close Grace");
        if (isRunning()) {
            output.println("exit");
        }
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("singleGraph")) {
            singleGraph = new Boolean((String) value).booleanValue();
            initialized = false;    // so we reinitialize Grace by next process()
        }

        if (paramname.equals("reInitialize")) {
            reInitialize = new Boolean((String) value).booleanValue();
            initialized = false;
        }

        if (paramname.equals("pageSizeDisplay")) {
            pageSizeDisplay = (String) value;
            if (pageSizeDisplay.equals("Grace default") && isRunning()) {
                output.println("exit");    // TODO: might be better save parameter (or not) then reload default
                ensureGraceRunning();
            }
            initialized = false;
        }

        if (paramname.equals("pageSizeDisplayCustomW")) {
            pageSizeDisplayCustomW = new Integer((String) value).intValue();
            initialized = false;
        }

        if (paramname.equals("pageSizeDisplayCustomH")) {
            pageSizeDisplayCustomH = new Integer((String) value).intValue();
            initialized = false;
        }

        if (paramname.equals("freePageLayout")) {
            freePageLayout = new Boolean((String) value).booleanValue();
            if (isRunning()) {
                output.println("exit");
                ensureGraceRunning();
            }
            initialized = false;
        }

        if (paramname.equals("parameterFile")) {
            parameterFile = (String) value;
            initialized = false;
        }

        if (paramname.equals("zaxisLogarithmic")) {
            zaxisLogarithmic = new Boolean((String) value).booleanValue();
            //initialized = false;
        }

        if (paramname.equals("zCutoffFraction")) {
            zCutoffFraction = new Float((String) value).floatValue();
            initialized = false;
        }

        if (paramname.equals("zThresholdByMedian")) {
            zThresholdByMedian = new Double((String) value).doubleValue();
            initialized = false;
        }

        if (paramname.equals("zRangeLower")) {
            zRangeLower = new Double((String) value).doubleValue();
            initialized = false;
        }

        if (paramname.equals("zRangeUpper")) {
            zRangeUpper = new Double((String) value).doubleValue();
            initialized = false;
        }

        if (paramname.equals("title_string")) {
            title_string = (String) value;
            initialized = false;
        }

        if (paramname.equals("xlabel_string")) {
            xlabel_string = (String) value;
            initialized = false;
        }
        if (paramname.equals("ylabel_string")) {
            ylabel_string = (String) value;
            initialized = false;
        }
        if (paramname.equals("legend_string")) {
            legend_string = (String) value;
            initialized = false;
        }
    }


    /**
     * @return an array of the input types for GraceGrapher
     */
    public String[] getInputTypes() {
        return new String[]{"GraphType"};
    }

    /**
     * @return an array of the output types for GraceGrapher
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }

}



