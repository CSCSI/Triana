package org.trianacode.enactment.io.handlers;

import org.trianacode.enactment.io.IoTypeHandler;
import org.trianacode.taskgraph.TaskGraphException;

import java.io.InputStream;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 3, 2010
 */
public class StreamHandler extends IoTypeHandler<InputStream> {
    @Override
    public String[] getKnownTypes() {
        return new String[]{"stream"};
    }

    @Override
    public InputStream handle(String type, InputStream source) throws TaskGraphException {
        if (type.equals("stream")) {
            return source;
        }
        return null;
    }
}
