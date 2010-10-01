package signalproc.dataparam;

import org.trianacode.taskgraph.Unit;
import triana.types.GraphType;
import triana.types.Signal;
import triana.types.TimeFrequency;
import triana.types.VectorType;
import triana.types.util.Str;

/**
 * A SetAcqTime unit to reset the acquisition time.
 *
 * @author B F Schutz
 * @version 1.1 27 June 2001
 */
public class SetAcqTime extends Unit {

    double newTime = 0;
    double lastTime;
    boolean firstTimeCalled = true;


    /**
     * ********************************************* ** USER CODE of SetAcqTime goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        double timeToSet;
        int length;

        GraphType in = (GraphType) getInputAtNode(0);

        if (in instanceof Signal) {

            Signal input = (Signal) in;

            timeToSet = (firstTimeCalled) ? newTime : lastTime;
            firstTimeCalled = false;

            if (input instanceof VectorType) {
                lastTime = timeToSet + ((VectorType) input).size() / input.getSamplingRate();
            } else if (input instanceof TimeFrequency) {
                lastTime = timeToSet + ((TimeFrequency) input).getDimensionLengths(0) * ((TimeFrequency) input)
                        .getInterval();
            } else {
                lastTime = newTime;
            }

            input.setAcquisitionTime(timeToSet);

            output((GraphType) input);
        } else {
            output(in);
        }
    }


    /**
     * Initialses information specific to SetAcqTime.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Give new acquisition time $title newTime Scroller 0 100000 0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Give new acquisition time $title newTime Scroller 0 100000 0");
//    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
        firstTimeCalled = true;
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
//
//    /**
//     * Saves SetAcqTime's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("newTime", newTime);
//    }

    /**
     * Used to set each of SetAcqTime's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("newTime")) {
            newTime = Str.strToDouble((String) value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to SetAcqTime, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "GraphType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from SetAcqTime, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "GraphType";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.GraphType"};
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Set a new acquisition time";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "SetAcqTime.html";
    }
}




