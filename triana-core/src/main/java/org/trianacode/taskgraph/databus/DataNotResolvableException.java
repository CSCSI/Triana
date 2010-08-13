package org.trianacode.taskgraph.databus;

/**
 * thrown when the data URL cannot be resolve by the data bus. User: scmijt Date: Jul 23, 2010 Time: 4:25:17 PM To
 * change this template use File | Settings | File Templates.
 */
public class DataNotResolvableException extends Exception {

    public DataNotResolvableException(String message) {
        super(message);
    }
}
