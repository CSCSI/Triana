package signalproc.filtering.freqdomain;

import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.util.Str;


/**
 * A LowPassAv unit to ..
 *
 * @author ian
 * @version 1.0 beta 18 Jun 1999
 */
public class LowPassAv extends Unit {

    double CutOff;


    /**
     * ********************************************* ** USER CODE of LowPassAv goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        SampleSet wave = (SampleSet) getInputAtNode(0);

        double MovingAverage = 0.0;
        int i;
        double out[] = new double[wave.size()];

        for (i = 0; i < wave.size(); i++) {
            MovingAverage += wave.data[i];
            if (i - CutOff >= 0) {
                MovingAverage -= wave.data[i - (int) CutOff];
            }
            out[i] = MovingAverage;
        }

        output(new SampleSet(
                ((SampleSet) wave).samplingFrequency(), out));
    }


    /**
     * Initialses information specific to LowPassAv.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Set the Low-Pass Cut-Off Average Value $title CutOff Scroller 0 500 5\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
//    public void setGUIInformation() {
//        addGUILine("Set the Low-Pass Cut-Off Average Value $title CutOff Scroller 0 500 5");
//    }

    /**
     * Reset's LowPassAv
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves LowPassAv's parameters.
     */
//    public void saveParameters() {
//        saveParameter("CutOff", CutOff);
//    }

    /**
     * Used to set each of LowPassAv's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("CutOff")) {
            CutOff = Str.strToDouble((String) value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to LowPassAv, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "SampleSet";
    }

    /**
     * @return a string containing the names of the types output from LowPassAv, each separated by a white space.
     */
    public String outputTypes() {
        return "SampleSet";
    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put LowPassAv's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "LowPassAv.html";
    }
}













