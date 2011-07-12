package org.trianacode.messenger;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 10/07/2011
 * Time: 19:37
 * To change this template use File | Settings | File Templates.
 */
public interface MessageListener {

    public void messagePerformed(MessageEvent messageEvent);

    public List<Throwable> listenerInterest();
}
