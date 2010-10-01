package signalproc.converters;

import java.awt.event.ActionEvent;

import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.VectorType;
import triana.types.util.Str;

/**
 * A VectToSSet unit to convert a Vect dtata type into a SampleSet type.
 *
 * @author Ian Taylor
 * @version 1.0 alpha 07 May 1997
 */
public class VectToSSet extends Unit {

    String sampFreq = "1024";
    String time = "0";
    String description = "Data from VectToSSet";

    /**
     * The UnitWindow for VectToSSet
     */
    VectToGenPanel myPanel;

    /**
     * ********************************************* ** USER CODE of VectToSSet goes here    ***
     * *********************************************
     */
    public void process() {
        VectorType raw = (VectorType) getInputAtNode(0);

        SampleSet s = convert(raw, Str.strToDouble(sampFreq));

        //addDescription(description);

        // s.setTimeStamp(myPanel.getDate());

        output(s);
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts a VectorType type into a SampleSet";
    }

    /**
     * Converts a raw data type to a SampleSet
     * <p/>
     * return a SampleSet
     */
    public static synchronized SampleSet convert(VectorType raw, double sf) {
        return new SampleSet(sf, raw.getData());
    }

    /**
     * Initialses information specific to VectToSSet.
     */
    public void init() {
        super.init();
//        setResizableInputs(false);
//        setResizableOutputs(true);
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        myPanel = new VectToGenPanel();
//        myPanel.setObject(this, VectToGenPanel.SAMP);
    }

    /**
     * Reset's VectToSSet
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves parameters
     */
//    public void saveParameters() {
//        saveParameter("sampFreq", sampFreq);
//        saveParameter("time", time);
//        saveParameter("description", description);
//    }

    /**
     * Sets parameters
     */
    public void parameterUpdate(String name, Object value) {
        if (name.equals("sampFreq")) {
            sampFreq = (String) value;
        }

        if (name.equals("time")) {
            time = (String) value;
        }

        if (name.equals("description")) {
            description = (String) value;
        }
    }

    public void updateWidgetFor(String name) {
        if (name.equals("sampFreq")) {
            myPanel.sampFreq.setText(sampFreq);
        }

        if (name.equals("time")) {
            myPanel.time.setText(time);
        }

        if (name.equals("description")) {
            myPanel.description.setText(description);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to VectToSSet, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "VectorType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from VectToSSet, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "SampleSet";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "converters.html";
    }


    /**
     * @return VectToSSet's parameter window sp that Triana can move and display it.
     */
    public UnitPanel getParameterPanel() {
        return myPanel;
    }


    /**
     * Captures the events thrown out by VectToGenPanel.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//    }

}


















