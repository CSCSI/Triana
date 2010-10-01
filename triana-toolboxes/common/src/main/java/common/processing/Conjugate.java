package common.processing;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
import triana.types.util.FlatArray;

/**
 * A Conjugate unit to output the complex conjugate of the input data set.
 *
 * @author Ian Taylor, Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class Conjugate extends Unit {

    /**
     * Conjugate takes a complex input and outputs its complex conjugate, <i>i.e.</i> the same complex data set with the
     * sign of the imaginary parts of all the data values changed. The output type is the same as the input.
     */
    public void process() {
        Object input;
        Object output = null;

        input = getInputAtNode(0);

        if (input instanceof EmptyingType) {
            return;
        }
        if (input instanceof GraphType) {
            GraphType s = (GraphType) input;
            output = s;
            for (int dv = 0; dv < s.getDependentVariables(); dv++) {
                if (s.isArithmeticArray(dv)) {
                    if (s.isDependentComplex(dv)) {
                        FlatArray.scaleArray(s.getDataArrayImag(dv), -1.0);
                    }
                }
            }
        } else if (input instanceof Const) {
            Const s = (Const) input;
            output = s;
            s.setImag(-s.getImag());
        }
        //setOutputType(output.getClass());
        output(output);
    }


    /**
     * Initialses information specific to Conjugate.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);                
    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
    }

    /**
     * Called when the stop button is pressed within the MainTriana Window
     */
    public void stopping() {
        super.stopping();
    }

    /**
     * Called when the start button is pressed within the MainTriana Window
     */
//    public void starting() {
//        super.starting();
//    }

    /**
     * Saves Conjugate's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of Conjugate's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Conjugate, each separated by a white
     *         space.
     */
     public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Form complex conjugate of input";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Conjugate.html";
    }
}



