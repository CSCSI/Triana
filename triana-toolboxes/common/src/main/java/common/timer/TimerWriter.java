package common.timer;


import java.io.FileWriter;
import java.io.PrintWriter;

import org.trianacode.taskgraph.Unit;
import triana.types.clipins.TimerClipIn;


/**
 * Outputs the algorithm timings to file
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class TimerWriter extends Unit {

    // parameter data type definitions
    private int count;
    private double total = 0;
    private String filename;

    private boolean header = true;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        Object input = (Object) getInputAtNode(0);

        if (isClipInName(TimerClipIn.TIMER_CLIPIN_TAG)) {
            TimerClipIn clipin = (TimerClipIn) getClipIn(TimerClipIn.TIMER_CLIPIN_TAG);

            total += ((double) clipin.getDuration());

            String average = String.valueOf(total / count);
            average = average.substring(0, Math.min(average.lastIndexOf('.') + 3, average.length()));

            if ((filename != null) && (!filename.equals(""))) {
                PrintWriter writer = new PrintWriter(new FileWriter(filename, !header));

                if (header) {
                    writer.println("Iterations     Time (millis)  Average (millis)");
                    header = false;
                }

                String output = String.valueOf(++count) + "              ";
                output = output.substring(0, 15);

                output += String.valueOf(clipin.getDuration()) + "              ";
                output = output.substring(0, 30);

                output += average;

                writer.println(output);
                writer.flush();
                writer.close();
            }

            setParameter("count", String.valueOf(++count));
            setParameter("last", String.valueOf(clipin.getDuration()));
            setParameter("average", average);

        }

        output(input);
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Outputs the algorithm timings to file");
        setHelpFileLocation("TimerWriter.html");

        // Define initial value and type of parameters
        defineParameter("last", "0", USER_ACCESSIBLE);
        defineParameter("average", "0", USER_ACCESSIBLE);
        defineParameter("filename", "", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "File Name $title filename File null *.*\n";
        guilines += "Iterations $title count Label 0\n";
        guilines += "Last Time (millis) $title last Label 0\n";
        guilines += "Average Time (millis) $title average Label 0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        count = 0;
        total = 0;
        header = true;

        setParameter("count", "0");
        setParameter("last", "0");
        setParameter("average", "0");

        filename = (String) getParameter("filename");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up TimerWriter (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("filename")) {
            filename = (String) value;
        }
    }


    /**
     * @return an array of the input types for TimerWriter
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for TimerWriter
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}



