package org.trianacode.messenger;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 10/07/2011
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
public abstract class MessageSender {

    public void sendMessage(MessageEvent event) {
        ErrorTracker.getMessageBus().broadcastMessage(event);
    }

}
