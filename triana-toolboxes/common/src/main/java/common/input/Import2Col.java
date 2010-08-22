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
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.Curve;
import triana.types.OldUnit;
import triana.types.util.Str;
import triana.types.util.StringVector;

/**
 * A Import2Col unit to allow the user to input a set of 2-D pairs of values.  This unit can then be connected to
 * Graph2D in order to view them graphically.
 *
 * @author Ian Taylor
 * @version 1.0 alpha 11 May 1997
 */
public class Import2Col extends OldUnit {

    String xlabel = "X Data";
    String ylabel = "Y Data";
    String data = "";

    /**
     * The UnitWindow for Import2Col
     */
    TwoDWindow myWindow;

    /**
     * ********************************************* ** USER CODE of Import2Col goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
//        StringSplitter sp=new StringSplitter(data);
        StringVector lines = new StringVector(1000);
        String sx, sy;
        BufferedReader sv = new BufferedReader(new StringReader(data));
        String line;

        try {
            while ((line = sv.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            throw new Exception("Error Reading from input in " + getName() +
                    " Have you specified the data in 2D format ?");
        }

        System.out.println("lines ok");

        double x[] = new double[lines.size()];
        double y[] = new double[lines.size()];

        Vector<String> s;

        for (int i = 0; i < lines.size(); ++i) {
            s = FileUtils.splitLine(lines.at(i));
            x[i] = Str.strToDouble(s.get(0));
            y[i] = Str.strToDouble(s.get(1));
        }

//        VectorType vt = new VectorType(x, y);

        Curve vt = new Curve(x, y);

        vt.setIndependentLabels(0, myWindow.xlabel.getText());
        vt.setDependentLabels(0, myWindow.ylabel.getText());

        output(vt);
        FileUtils.closeReader(sv);
    }

    /**
     * Initialses information specific to Import2Col.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

        myWindow = new TwoDWindow();
        myWindow.setObject(this);
    }

    /**
     * Reset's Import2Col
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Import2Col's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter("data", data);
        saveParameter("xlabel", xlabel);
        saveParameter("ylabel", ylabel);
    }

    /**
     * Sets Import2Col's parameters.
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
     * @return a string containing the names of the types allowed to be input to Import2Col, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "none";
    }

    /**
     * @return a string containing the names of the types output from Import2Col, each separated by a white space.
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
        return "Import2Col.html";
    }

    /**
     * @return Import2Col's parameter window sp that Triana can move and display it.
     */
    public UnitPanel getParameterPanel() {
        return myWindow;
    }
}

