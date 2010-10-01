package math.functions;

import org.trianacode.taskgraph.Unit;
import triana.types.Arithmetic;
import triana.types.Const;

/**
 * A Adder unit to divide, with possible scaling, the data from the first input node by the data from all the remaining
 * input nodes.
 * <p/>
 * This Unit obeys the conventions of Triana Type 2 data types.
 *
 * @author ian
 * @version 2.0 10 August 2000
 */
public class Divider extends Unit {

    // some examples of parameters

    public double scaler = 1.0;

    /**
     * Initialses information specific to Divider.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(2);
        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
        setMinimumOutputNodes(0);

        setHelpFileLocation("Divider.html");

        setGUIBuilderV2Info("Scaler value $title scaler Scroller 0 10 1");
    }


    public String[] getInputTypes() {
        return new String[]{"java.lang.Number",
                "triana.types.Arithmetic",
                "triana.types.GraphType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"java.lang.Number",
                "triana.types.Arithmetic",
                "triana.types.GraphType"};
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Adds all the inputs together";
    }


    /**
     * The main functionality of Divider goes here
     */
    public void process() {
        Object nextInput;
        Object result = getInputAtNode(0);

        for (int i = 1; i < getInputNodeCount(); ++i) {
            nextInput = getInputAtNode(i);

            if (result instanceof Arithmetic) {
                if (((Arithmetic) result).isCompatible(nextInput)) {
                    result = ((Arithmetic) result).divide(nextInput);
                } else {
                    notifyError("Incompatible data sets " + result.getClass().getName() + "/" + nextInput.getClass()
                            .getName());
                }
            } else if ((result instanceof Number) && (nextInput instanceof Number)) {
                result = new Double(((Number) result).doubleValue() / ((Number) nextInput).doubleValue());
            } else {
                notifyError(
                        "Incompatible data sets " + result.getClass().getName() + "/" + nextInput.getClass().getName());
            }
        }

        if (scaler != 1.0) {
            if (result instanceof Arithmetic) {
                result = ((Arithmetic) result).multiply(new Const(scaler));
            } else {
                result = new Double(((Number) result).doubleValue() * scaler);
            }
        }

        output(result);
    }


    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("scaler")) {
            scaler = new Double((String) value).doubleValue();
        }
    }

}
