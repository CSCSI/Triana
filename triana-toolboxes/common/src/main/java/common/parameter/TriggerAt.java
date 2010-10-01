package common.parameter;

import java.util.Calendar;
import java.util.TimeZone;

import org.trianacode.taskgraph.Unit;


/**
 * Trigger at certain time
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class TriggerAt extends Unit {

    // parameter data type definitions
    private int year;
    private int month;
    private int date;
    private int hour;
    private int minute;
    private int second;
    private String zone;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        // Insert main algorithm for TriggerAt
        //TimeZone tz = TimeZone.getTimeZone("GMT+01");
        Calendar cal;
        if (zone.equals("UTC")) {
            cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        } else {
            cal = Calendar.getInstance();
        }
        cal.set(year, month - 1, date, hour, minute, second);
        long w = cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        if (w > 0) {
            try {
                Thread.sleep(w);
            } catch (InterruptedException e) {
            }
        }

        output(new triana.types.Parameter(new Long(w)));

        //System.out.println("setting: " + cal.getTime());
        //System.out.println("sleep (ms): " + w);
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(0);
        setMinimumInputNodes(0);
        setMaximumInputNodes(0);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Trigger at certain time");
        setHelpFileLocation("TriggerAt.html");

// not suitable here
// 	Calendar cal = Calendar.getInstance();
// 	String sY = String.valueOf(cal.get(Calendar.YEAR));
// 	String sM = String.valueOf(cal.get(Calendar.MONTH)+1);
// 	String sd = String.valueOf(cal.get(Calendar.DATE));
// 	String sH = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
// 	String sm = String.valueOf(cal.get(Calendar.MINUTE));
// 	String ss = String.valueOf(cal.get(Calendar.SECOND));

        // Define initial value and type of parameters
        defineParameter("year", "2003", USER_ACCESSIBLE);
        defineParameter("month", "10", USER_ACCESSIBLE);
        defineParameter("date", "3", USER_ACCESSIBLE);
        defineParameter("hour", "20", USER_ACCESSIBLE);
        defineParameter("minute", "0", USER_ACCESSIBLE);
        defineParameter("second", "0", USER_ACCESSIBLE);
        defineParameter("zone", "System Default", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Year $title year TextField 0\n";
        guilines += "Month $title month TextField 0\n";
        guilines += "Date $title date TextField 0\n";
        guilines += "Hour (0-23) $title hour TextField 0\n";
        guilines += "Minute $title minute TextField 0\n";
        guilines += "Second $title second TextField 0\n";
        guilines += "Zone $title zone Choice [System Default] [UTC] 0\n";
        setGUIBuilderV2Info(guilines);

    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        year = new Integer((String) getParameter("year")).intValue();
        month = new Integer((String) getParameter("month")).intValue();
        date = new Integer((String) getParameter("date")).intValue();
        hour = new Integer((String) getParameter("hour")).intValue();
        minute = new Integer((String) getParameter("minute")).intValue();
        second = new Integer((String) getParameter("second")).intValue();
        zone = (String) getParameter("zone");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up TriggerAt (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("year")) {
            year = new Integer((String) value).intValue();
        }

        if (paramname.equals("month")) {
            month = new Integer((String) value).intValue();
        }

        if (paramname.equals("date")) {
            date = new Integer((String) value).intValue();
        }

        if (paramname.equals("hour")) {
            hour = new Integer((String) value).intValue();
        }

        if (paramname.equals("minute")) {
            minute = new Integer((String) value).intValue();
        }

        if (paramname.equals("second")) {
            second = new Integer((String) value).intValue();
        }

        if (paramname.equals("zone")) {
            zone = (String) value;
        }
    }


    /**
     * @return an array of the input types for TriggerAt
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for TriggerAt
     */
    public String[] getOutputTypes() {
        return new String[]{"Parameter"};
    }

}



