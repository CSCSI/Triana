package org.trianacode.shiwaall.string;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class DatePattern implements StringPattern, Serializable {

    private static long serialVersionUID = -1;    

    private Date date;
    private SimpleDateFormat formatter;

    public DatePattern(Date date, String format) {
        this.date = date;
        this.formatter = new SimpleDateFormat(format);
    }

    public DatePattern(String format) {
        this(new Date(), format);
    }

    public String next() {
        return formatter.format(date);
    }

    @Override
    public void resetCount() {

    }

}
