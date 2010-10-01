package org.trianacode.config.cl;

/**
 * This exception is thrown when an argument (say from the command line) does not parse corretly
 * <p/>
 * User: scmijt
 * Date: Sep 24, 2010
 * Time: 9:43:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArgumentParsingException extends Exception {

    public ArgumentParsingException() {
    }

    public ArgumentParsingException(String message) {
        super(message);
    }

    public ArgumentParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgumentParsingException(Throwable cause) {
        super(cause);
    }
}
