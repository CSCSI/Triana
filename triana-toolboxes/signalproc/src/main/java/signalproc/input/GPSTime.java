package signalproc.input;

import java.util.GregorianCalendar;

public class GPSTime extends GregorianCalendar {

    GPSTime gpsEPOCH;
    long gpsEPOCHMSec;

    public GPSTime() {
        super();
    }

    public void setEPOCH() {

        //gpsEPOCH = new GPSTime(1980,0,6,0,59,47);

        gpsEPOCH = new GPSTime(1980, 0, 6, 0, 0, 0);

        gpsEPOCHMSec = gpsEPOCH.getTimeInMillis();
//        System.out.println("Date Object : " + gpsEPOCH);
        System.out.println("Date MS : " + gpsEPOCHMSec);
    }

    public GPSTime(int year, int month, int day, int min, int sec) {
        super(year, month, day, min, sec);
    }

    public GPSTime(int year, int month, int day, int hour, int min, int sec) {
        super(year, month, day, hour, min, sec);
    }

    public long getSeconds() {

        long msec = super.getTimeInMillis();
        msec -= gpsEPOCHMSec;
        long sec = msec / 1000;

        System.out.println("Date Object Cur : " + toString());
        System.out.println("Date MS : " + getTimeInMillis());

        return sec;
    }

    public void setSeconds(long gpsTime) {
        long time = (gpsTime * 1000) + gpsEPOCHMSec;
        super.setTimeInMillis(time);
    }

    public long getTimeInMillis() {
        return super.getTimeInMillis();
    }

    public void ToNiceString() {
        /*       yearVal = today.get(Calendar.YEAR);
monthVal = today.get(Calendar.MONTH);
dayVal = today.get(Calendar.DAY_OF_MONTH);
hourVal = today.get(Calendar.HOUR_OF_DAY);
minVal = today.get(Calendar.MINUTE);
secVal = today.get(Calendar.SECOND);      */
    }
}
    
