package common.input;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Vector;

import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.Curve;
import triana.types.util.Str;
import triana.types.util.StringVector;

/**
 * A Import2Col unit to allow the user to input a set of 2-D pairs of values.  This unit can then be connected to
 * Graph2D in order to view them graphically.
 *
 * @author Ian Taylor
 * @version 1.0 alpha 11 May 1997
 */
public class Import2Col extends Unit {

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
            throw new Exception("Error Reading from input in " + getTask().getToolName() +
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
     * Reset's Import2Col
     */
    public void reset() {
        super.reset();
    }

//    /**
//     * Saves Import2Col's parameters to the parameter file.
//     */
//    public void saveParameters() {
//        saveParameter("data", data);
//        saveParameter("xlabel", xlabel);
//        saveParameter("ylabel", ylabel);
//    }

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
        return "Import2Col.html";
    }

    /**
     * @return Import2Col's parameter window sp that Triana can move and display it.
     */
    public UnitPanel getParameterPanel() {
        return myWindow;
    }
}

