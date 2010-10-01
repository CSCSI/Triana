package signalproc.output;

import java.awt.event.ActionEvent;

import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.taskgraph.Unit;
import triana.types.GraphType;
import triana.types.TrianaType;

/**
 * A Histogrammer unit.
 *
 * @author Rob Davies
 * @version $Revision $
 */

public class Histogrammer extends Unit {


    public void process() throws Exception {
        TrianaType input;

        for (int count = 0; count < getTask().getDataInputNodeCount(); count++) {
            input = (TrianaType) getInputAtNode(count);

            if (input instanceof GraphType) {
                getTask().setParameter("HistogrammerData_" + count, input);
            }
        }
    }


    /**
     * Initialses information specific to Histogrammer.
     */
    public void init() {
        super.init();
//        setResizableInputs(true);
//        setResizableOutputs(false);
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);
        // set these to true if your unit can process double-precision
        // arrays       setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);
        setParameterPanelClass("signalproc.output.HistogrammerPanel");
    }


    /**
     * Saves Histogrammer's parameters.
     */
    public void saveParameters() {
    }


    /**
     * Used to set each of Histogrammer's parameters.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */

    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Histogrammer, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "GraphType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Histogrammer, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "none";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType"};
    }

    public String[] getOutputTypes() {
        return new String[]{};
    }
        
    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "A graphical-display unit for creating and rendering histograms";
    }

    /**     * @returns the location of the help file for this unit.     */

    public String getHelpFile() {
        return "Histogrammer.html";
    }

    /**
     * @return Histogrammer's parameter panel
     */
    public UnitPanel getParameterPanel() {
        return null;
    }

    /**
     * Captures the events thrown out by Histogrammer.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//    }
}





