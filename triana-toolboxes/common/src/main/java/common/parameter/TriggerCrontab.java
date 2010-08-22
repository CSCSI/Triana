package common.parameter;


import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.trianacode.taskgraph.Unit;

/**
 * Trigger by crontab-like scheduling
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class TriggerCrontab extends Unit {

    // parameter data type definitions
    private String minute;
    private String hour;
    private String dayOfMonth;
    private String month;
    private String dayOfWeek;
    private String zone;

    private final int MINUTE = 1;
    private final int HOUR = 2;
    private final int DAY_OF_MONTH = 3;
    private final int MONTH = 4;
    private final int DAY_OF_WEEK = 5;

    private final long ONE_YEAR_IN_MILLIS = 365 * 24 * 3600 * 1000;
    private final int MAX_TARDINESS = 500; // 0.5s

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        // Insert main algorithm for TriggerCrontab
        Timer timer = new Timer();
        Vector m, h, d, M, D;

        // normalized form
        if (!checkSkips()) {
            System.err.println("only one time unit can have skips");
            return;
        }
        System.err.println("checkSkips OK");

        m = normalizeForm(minute, MINUTE);
        if (m == null) {
            System.err.println("minute format error");
            return;
        }
        System.err.println("minute format OK");
        h = normalizeForm(hour, HOUR);
        if (h == null) {
            System.err.println("hour format error");
            return;
        }
        System.err.println("hour format OK");
        d = normalizeForm(dayOfMonth, DAY_OF_MONTH);
        if (d == null) {
            System.err.println("day of month format error");
            return;
        }
        System.err.println("date format OK");
        M = normalizeForm(month, MONTH);
        if (M == null) {
            System.err.println("month format error");
            return;
        }
        System.err.println("month format OK");
        D = normalizeForm(dayOfWeek, DAY_OF_WEEK);
        if (D == null) {
            System.err.println("day of week format error");
            return;
        }
        System.err.println("day format OK");

        //...
        Calendar cal;
        final Calendar curr;
        if (zone.equals("UTC")) {
            cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        } else {
            cal = Calendar.getInstance();
        }
        curr = (Calendar) cal.clone();

        for (int ll = 0; ll < M.size(); ll++) {
            for (int kk = 0; kk < d.size(); kk++) {
                for (int jj = 0; jj < h.size(); jj++) {
                    for (int ii = 0; ii < m.size(); ii++) {
                        cal.setTime(curr.getTime());
                        // join
                        cal.set(Calendar.MONTH, ((Integer) M.get(ll)).intValue() - 1);
                        cal.set(Calendar.DAY_OF_MONTH, ((Integer) d.get(kk)).intValue());
                        cal.set(Calendar.HOUR, ((Integer) h.get(jj)).intValue());
                        cal.set(Calendar.MINUTE, ((Integer) m.get(ii)).intValue());

                        // intersection
                        for (int mm = 0, tmp = cal.get(Calendar.DAY_OF_WEEK); mm < D.size(); mm++) {
                            if (((Integer) D.get(mm)).intValue() % 7 + 1 == tmp) {
                                if (cal.before(curr)) {
                                    cal.add(Calendar.YEAR, 1);
                                }
                                timer.schedule(new MyTimerTask(), cal.getTime(), ONE_YEAR_IN_MILLIS);
                                System.out.println("setting: " + cal.getTime());
                            }
                        }
                    }
                }
            }
        }

        try {
            System.out.println("crontab now sleeping");
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            System.out.println("pipeline reset, crontab canceled");
        } finally {
            timer.cancel();
        }
    }

    private boolean checkSkips() {
        int cnt = 0;
        if (minute.indexOf('/') != -1) {
            cnt++;
        }
        if (hour.indexOf('/') != -1) {
            cnt++;
        }
        if (dayOfMonth.indexOf('/') != -1) {
            cnt++;
        }
        if (month.indexOf('/') != -1) {
            cnt++;
        }
        if (dayOfWeek.indexOf('/') != -1) {
            cnt++;
        }

        return (cnt > 1) ? false : true;
    }

    private Vector normalizeForm(String time, int type) {
        Vector res = new Vector();
        int i, j;

        if (!time.trim().endsWith(",")) {
            time += ",";
        }
        System.err.println(" time string is: " + time);
        for (i = 0, j = time.indexOf(','); j != -1; i = j + 1, j = time.indexOf(',', i)) {
            String s = time.substring(i, j).trim();
            int k = s.indexOf('/');
            int m = s.indexOf('-');
            int n = s.indexOf('*');
            int skip = 1;
            int begin = 0, end = -1;

            if (k != s.lastIndexOf('/')) {
                System.err.println("too many skips in: " + time);
                return null;
            }
            if (k != -1) {         // no skip
                try {
                    skip = Integer.parseInt(s.substring(k + 1).trim());
                } catch (NumberFormatException e) {
                    System.err.println(e.toString());
                    return null;
                }
            } else {
                k = s.length();
            }

            if (n != -1 && s.substring(0, k).trim().length() != 1) {
                System.err.println("wildcard range ('*') can only used alone: " + time);
                return null;
            }
            if (m != s.lastIndexOf('-')) {
                System.err.println("too many ranges in: " + time);
                return null;
            }
            if (n != -1) {     // '*' range
                switch (type) {
                    case MINUTE:
                        begin = 0;
                        end = 59;
                        break;
                    case HOUR:
                        begin = 0;
                        end = 23;
                        break;
                    case DAY_OF_MONTH:
                        begin = 1;
                        end = 31;
                        break;
                    case MONTH:
                        begin = 1;
                        end = 12;
                        break;
                    case DAY_OF_WEEK:
                        begin = 0;
                        end = 7;
                        break;
                    default:
                        System.err.println("unknown type");
                        break;
                }
            } else if (m != -1) { // '-' range
                try {
                    begin = Integer.parseInt(s.substring(0, m).trim());
                    end = Integer.parseInt(s.substring(m + 1, k).trim());
                } catch (NumberFormatException e) {
                    System.err.println(e.toString());
                    return null;
                }
            } else if (s.substring(0, k).trim().length() > 0) { // one number
                try {
                    begin = Integer.parseInt(s.substring(0, k).trim());
                    end = begin;
                } catch (NumberFormatException e) {
                    System.err.println(e.toString());
                    return null;
                }
            }

            for (int idx = begin; idx <= end; idx += skip) {
                try {
                    switch (type) {
                        case MINUTE:
                            if (idx < 0 || idx > 59) {
                                throw new RuntimeException("Out of range: " + idx);
                            }
                            break;
                        case HOUR:
                            if (idx < 0 || idx > 23) {
                                throw new RuntimeException("Out of range: " + idx);
                            }
                            break;
                        case DAY_OF_MONTH:
                            if (idx < 1 || idx > 31) {
                                throw new RuntimeException("Out of range: " + idx);
                            }
                            break;
                        case MONTH:
                            if (idx < 1 || idx > 12) {
                                throw new RuntimeException("Out of range: " + idx);
                            }
                            break;
                        case DAY_OF_WEEK:
                            if (idx < 0 || idx > 7) {
                                throw new RuntimeException("Out of range: " + idx);
                            }
                            break;
                        default:
                            System.err.println("unknown calender unit");
                            break;
                    }
                } catch (RuntimeException e) {
                    System.err.println(e.toString());
                    return null;
                }
                res.add(new Integer(idx));
            }
        }

        return res;
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
        setPopUpDescription("Trigger by crontab-like scheduling");
        setHelpFileLocation("TriggerCrontab.html");

        // Define initial value and type of parameters
        defineParameter("minute", "0", USER_ACCESSIBLE);
        defineParameter("hour", "0", USER_ACCESSIBLE);
        defineParameter("dayOfMonth", "1", USER_ACCESSIBLE);
        defineParameter("month", "1", USER_ACCESSIBLE);
        defineParameter("dayOfWeek", "1", USER_ACCESSIBLE);
        defineParameter("zone", "System Default", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Minute (0-59) $title minute TextField 0\n";
        guilines += "Hour (0-23) $title hour TextField 0\n";
        guilines += "Day of Month (1-31) $title dayOfMonth TextField 1\n";
        guilines += "Month (1-12) $title month TextField 1\n";
        guilines += "Day of Week (0-7) $title dayOfWeek TextField 1\n";
        guilines += "Zone $title zone Choice [System Default] [UTC] 0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        minute = (String) getParameter("minute");
        hour = (String) getParameter("hour");
        dayOfMonth = (String) getParameter("dayOfMonth");
        month = (String) getParameter("month");
        dayOfWeek = (String) getParameter("dayOfWeek");
        zone = (String) getParameter("zone");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up TriggerCrontab (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("minute")) {
            minute = (String) value;
        }

        if (paramname.equals("hour")) {
            hour = (String) value;
        }

        if (paramname.equals("dayOfMonth")) {
            dayOfMonth = (String) value;
        }

        if (paramname.equals("month")) {
            month = (String) value;
        }

        if (paramname.equals("dayOfWeek")) {
            dayOfWeek = (String) value;
        }

        if (paramname.equals("zone")) {
            zone = (String) value;
        }
    }


    /**
     * @return an array of the input types for TriggerCrontab
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for TriggerCrontab
     */
    public String[] getOutputTypes() {
        return new String[]{"Parameter"};
    }

    private class MyTimerTask extends TimerTask {
        public void run() {
            output(new triana.types.Parameter(new Long(scheduledExecutionTime())));
            System.err.println("tardiness: " + (System.currentTimeMillis() - scheduledExecutionTime()));
        }
    }

}



