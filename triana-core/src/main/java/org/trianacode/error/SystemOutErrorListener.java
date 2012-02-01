package org.trianacode.error;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 23/08/2011
 * Time: 10:40
 * To change this template use File | Settings | File Templates.
 */
public class SystemOutErrorListener implements ErrorListener {
    @Override
    public void errorOccurred(ErrorEvent errorEvent) {
        System.out.println("Error caught by ErrorTracker : " + errorEvent.errorMsg);
        if (errorEvent.getThrown() != null) {
            System.out.println("    Trace : " +
                    errorEvent.getThrown().getStackTrace().getClass().getSimpleName());
        }
    }

    @Override
    public List<String> listenerInterest() {
        List<String> interest = new ArrayList<String>();
        interest.add(ErrorTracker.ALLERRORS);
        return interest;
    }
}
