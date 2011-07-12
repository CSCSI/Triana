package org.trianacode.messenger;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 10/07/2011
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
public class MessageEvent {
    Throwable type;
    String message;

    public MessageEvent(Throwable type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getType() {
        return type;
    }
}
