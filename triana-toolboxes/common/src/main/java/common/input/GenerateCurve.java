package common.input;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Vector;

import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.Curve;
import triana.types.util.Str;

/**
 * A GenerateCurve unit to allow the user to input a set of 2-D pairs of values.  This unit can then be connected to
 * Graph2D in order to view them graphically.
 *
 * @author Ian Taylor, Bernard Schutz
 * @version 2.0 20 August 2000
 */
public class GenerateCurve extends Unit {

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
            getRunnableInterface().notifyError(null);
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
            getRunnableInterface().notifyError(null); // Used to be 'stop'
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

        setDefaultInputNodes(0);
        setMinimumInputNodes(0);
        setMaximumInputNodes(0);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        myWindow = new TwoDWindow();
        myWindow.setObject(this);
    }

    /**
     * Reset's GenerateCurve
     */
    public void reset() {
        super.reset();
    }

//    /**
//     * Saves GenerateCurve's parameters to the parameter file.
//     */
//    public void saveParameters() {
//        saveParameter("data", data);
//        saveParameter("xlabel", xlabel);
//        saveParameter("ylabel", ylabel);
//    }

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
     * @return an array of the input types accepted by nodes not covered by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return a string containing the names of the types output from GenerateCurve, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Curve"};
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

