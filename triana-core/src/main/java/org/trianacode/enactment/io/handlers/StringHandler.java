package org.trianacode.enactment.io.handlers;

import org.trianacode.enactment.io.IoTypeHandler;
import org.trianacode.taskgraph.TaskGraphException;

import java.io.InputStream;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 3, 2010
 */
public class StringHandler extends IoTypeHandler<String> {
    @Override
    public String[] getKnownTypes() {
        return new String[]{"string"};
    }

    @Override
    public String handle(String type, InputStream source) throws TaskGraphException {
        if (type.equals("string")) {
            return readAsString(source);
        }
        return null;
    }
}
