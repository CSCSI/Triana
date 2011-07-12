package org.trianacode.messenger;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 10/07/2011
 * Time: 19:37
 * To change this template use File | Settings | File Templates.
 */
public class ErrorTracker {

    Vector<MessageListener> allListeners;

    private static ErrorTracker thisMessageBus = new ErrorTracker();

    private ErrorTracker() {
        allListeners = new Vector<MessageListener>();
    }

    public static ErrorTracker getMessageBus() {
        return thisMessageBus;
    }

    public void addMessageListener(MessageListener messageListener) {
        allListeners.add(messageListener);
    }

    public void removeMessageListener(MessageListener messageListener) {
        allListeners.remove(messageListener);
    }

    public void broadcastMessage(MessageEvent messageEvent) {
        for (MessageListener listener : allListeners) {
            if (listener.listenerInterest().contains(messageEvent.type)) {
                listener.messagePerformed(messageEvent);
            }
        }
    }
}
