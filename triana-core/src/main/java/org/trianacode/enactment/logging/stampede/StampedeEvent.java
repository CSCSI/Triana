package org.trianacode.enactment.logging.stampede;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 01/09/2011
 * Time: 17:19
 * To change this template use File | Settings | File Templates.
 */
public class StampedeEvent {
    ArrayList<LogDetail> eventDetails;
    String eventName;

    public StampedeEvent(String eventName, LogDetail... logDetails) {
        eventDetails = new ArrayList<LogDetail>();
        eventDetails.addAll(Arrays.asList(logDetails));
        this.eventName = eventName;
    }

    public StampedeEvent addLogDetail(LogDetail logDetail) {
        eventDetails.add(logDetail);
        return this;
    }

    public StampedeEvent add(String key, String value) {
        eventDetails.add(new LogDetail(key, value));
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public ArrayList<LogDetail> getLogDetails() {
        return eventDetails;
    }

    public String toString() {
        String wholeString = eventName + " ";
        for (LogDetail logDetail : eventDetails) {
            wholeString += logDetail.getName() + "=" + logDetail.getDetail() + " ";
        }
        return wholeString;
    }

}
