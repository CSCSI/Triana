package signalproc.converters;

import java.awt.event.ActionEvent;

import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSampleSet;
import triana.types.VectorType;
import triana.types.util.Str;


/**
 * A VectToCSSet unit to ..
 *
 * @author Ian Taylor
 * @version 1.0 alpha 07 May 1997
 */
public class VectToCSSet extends Unit {
    String sampFreq = "1024";
    String time = "0";
    String description = "Data from VectToCSSet";
    String complexOrder = "R I  R I  RI ..";

    /**
     * The UnitWindow for VectToCSSet
     */
    VectToGenPanel myPanel;

    /**
     * A tag to state that the data is packed in alternating order e.g. R I R I R I ...... R I
     */
    public final static int ALTERNATE = 0;

    /**
     * A tag to state that the data is packed as all reals followed all imaginary components e.g. e.g. R R R R .... R I
     * I I I .... I
     */
    public final static int NORMAL = 1;

    /**
     * ********************************************* ** USER CODE of VectToSpect goes here    ***
     * *********************************************
     */
    public void process() {
        VectorType raw = (VectorType) getInputAtNode(0);

        double sf = Str.strToDouble(sampFreq);
        String desc = description;

        int order;

        if (complexOrder.equals("R I  R I  RI ..")) {
            order = ALTERNATE;
        } else {
            order = NORMAL;
        }

        ComplexSampleSet s = convert(raw, sf, order);
        //addDescription(desc);

        // s.setTimeStamp(myPanel.getDate()); ??

        output(s);
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts a VectorType into a Complex SampleSet";
    }

    /**
     * Converts a raw data type to a ComplexSampleSet
     * <p/>
     * return a ComplexSampleSet
     */
    public static synchronized ComplexSampleSet convert(VectorType raw, double sf,
                                                        int order) {
        int csize = raw.size() / 2;
        double real[] = new double[csize];
        double imag[] = new double[csize];

        double[] data = raw.getData();

        if (order == ALTERNATE) {
            for (int i = 0; i < csize; i++) {
                real[i] = data[i * 2];
                imag[i] = data[(i * 2) + 1];
            }
        } else {
            for (int i = 0; i < csize; i++) {
                real[i] = data[i];
                imag[i] = data[i + csize];
            }
        }

        return new ComplexSampleSet(sf, real, imag);
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
//        myPanel.setObject(this, VectToGenPanel.CSAMP);
    }

    /**
     * Reset's VectToSSet
     */
    public void reset() {
        super.reset();
    }


//    public void saveParameters() {
//        saveParameter("sampFreq", sampFreq);
//        saveParameter("time", time);
//        saveParameter("description", description);
//        saveParameter("complex", complexOrder);
//    }

    /**
     * Sets parameters
     */
    public void setParameter(String name, String value) {
        if (name.equals("sampFreq")) {
            sampFreq = value;
        }

        if (name.equals("time")) {
            time = value;
        }

        if (name.equals("description")) {
            description = value;
        }

        if (name.equals("complex")) {
            complexOrder = value;
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

        if (name.equals("complex")) {
            myPanel.complexOrder.setSelectedItem(complexOrder);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to VectToCSSet, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "VectorType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from VectToCSSet, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "ComplexSampleSet";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.ComplexSampleSet"};
    }


    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "converters.html";
    }


    /**
     * @return VectToCSSet's parameter window sp that Triana can move and display it.
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


















