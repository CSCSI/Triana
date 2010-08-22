package common.input;

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
import java.io.StringReader;
import java.util.Vector;

import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.Curve;
import triana.types.OldUnit;
import triana.types.util.Str;

/**
 * A GenerateCurve unit to allow the user to input a set of 2-D pairs of values.  This unit can then be connected to
 * Graph2D in order to view them graphically.
 *
 * @author Ian Taylor, Bernard Schutz
 * @version 2.0 20 August 2000
 */
public class GenerateCurve extends OldUnit {

    String xlabel = "X Data";
    String ylabel = "Y Data";
    String data = "";

    /**
     * The UnitWindow for GenerateCurve
     */
    TwoDWindow myWindow;

    /**
     * ********************************************* ** USER CODE of GenerateCurve goes here    ***
     * *********************************************
     */
    public void process() {
        BufferedReader br = null;
        int count = countPairs();
        Vector<String> sv;

        br = new BufferedReader(new
                StringReader(myWindow.data.getText()));

        if (br == null) {
            ErrorDialog.show("Couldn't create a reader for 2-D data");
            stop();
        }

        double x[] = new double[count];
        double y[] = new double[count];

        // read it this time :-

        for (int i = 0; i < count; ++i) {
            sv = FileUtils.nextNonEmptySplitLine(br);
            x[i] = Str.strToDouble(sv.get(0));
            y[i] = Str.strToDouble(sv.get(1));
        }

        Curve vt = new Curve(x, y);

        vt.setIndependentLabels(0, myWindow.xlabel.getText());
        vt.setDependentLabels(0, myWindow.ylabel.getText());
        output(vt);
    }


    public int countPairs() {
        BufferedReader br = null;
        int count = 0;

        br = new BufferedReader(new
                StringReader(myWindow.data.getText()));

        if (br == null) {
            ErrorDialog.show("Couldn't create a reader for 2-D data");
            stop();
        }

        // count number of pairs

        Vector<String> sv;

        do {
            sv = FileUtils.nextNonEmptySplitLine(br);
            ++count;
        }
        while ((sv != null) && (sv.size() == 2));

        FileUtils.closeReader(br);
        return count - 1;
    }

    /**
     * Initialses information specific to GenerateCurve.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

        myWindow = new TwoDWindow();
        myWindow.setObject(this);
    }

    /**
     * Reset's GenerateCurve
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves GenerateCurve's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter("data", data);
        saveParameter("xlabel", xlabel);
        saveParameter("ylabel", ylabel);
    }

    /**
     * Sets GenerateCurve's parameters.
     */
    public void setParameter(String name, String value) {
        if (name.equals("data")) {
            data = value;
        }

        if (name.equals("xlabel")) {
            xlabel = value;
        }

        if (name.equals("ylabel")) {
            ylabel = value;
        }
    }

    public void updateWidgetFor(String name) {
        if (name.equals("data")) {
            myWindow.data.setText(data);
        }

        if (name.equals("xlabel")) {
            myWindow.xlabel.setText(xlabel);
        }

        if (name.equals("ylabel")) {
            myWindow.ylabel.setText(ylabel);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to GenerateCurve, each separated by a
     *         white space.
     */
    public String inputTypes() {
        return "none";
    }

    /**
     * @return a string containing the names of the types output from GenerateCurve, each separated by a white space.
     */
    public String outputTypes() {
        return "Curve";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Generates a 2-Dimensional (x,y) data set";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "GenerateCurve.html";
    }

    /**
     * @return GenerateCurve's parameter window sp that Triana can move and display it.
     */
    public UnitPanel getParameterPanel() {
        return myWindow;
    }
}

