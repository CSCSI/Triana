package org.trianacode.error;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 10/07/2011
 * Time: 19:37
 * To change this template use File | Settings | File Templates.
 */
public class ErrorTracker {
    public static final String ALLERRORS = "allErrors";

    Vector<ErrorListener> allListeners;

    private static ErrorTracker thisMessageBus = new ErrorTracker();

    private ErrorTracker() {
        allListeners = new Vector<ErrorListener>();
    }

    public static ErrorTracker getErrorTracker() {
        return thisMessageBus;
    }

    public void addErrorListener(ErrorListener errorListener) {
        allListeners.add(errorListener);
    }

    public void removeErrorListener(ErrorListener errorListener) {
        allListeners.remove(errorListener);
    }

    public void broadcastError(ErrorEvent errorEvent) {
        for (ErrorListener listener : allListeners) {
            if (listener.listenerInterest().contains(ALLERRORS) ||
                    listener.listenerInterest().contains(errorEvent.getThrown().getStackTrace().getClass().getSimpleName())) {
                listener.errorOccurred(errorEvent);
            }
        }
    }
}
