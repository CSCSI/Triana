package org.trianacode.error;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 10/07/2011
 * Time: 19:37
 * To change this template use File | Settings | File Templates.
 */
public interface ErrorListener {

    public void errorOccurred(ErrorEvent errorEvent);

    public List<String> listenerInterest();
}
