package org.trianacode.shiwaall.string;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class DatePattern.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class DatePattern implements StringPattern, Serializable {

    /** The serial version uid. */
    private static long serialVersionUID = -1;    

    /** The date. */
    private Date date;
    
    /** The formatter. */
    private SimpleDateFormat formatter;

    /**
     * Instantiates a new date pattern.
     *
     * @param date the date
     * @param format the format
     */
    public DatePattern(Date date, String format) {
        this.date = date;
        this.formatter = new SimpleDateFormat(format);
    }

    /**
     * Instantiates a new date pattern.
     *
     * @param format the format
     */
    public DatePattern(String format) {
        this(new Date(), format);
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.string.StringPattern#next()
     */
    public String next() {
        return formatter.format(date);
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.string.StringPattern#resetCount()
     */
    @Override
    public void resetCount() {

    }

}
