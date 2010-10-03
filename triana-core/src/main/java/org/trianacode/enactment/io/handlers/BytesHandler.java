package org.trianacode.enactment.io.handlers;

import org.trianacode.enactment.io.IoTypeHandler;
import org.trianacode.taskgraph.TaskGraphException;

import java.io.InputStream;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 3, 2010
 */
public class BytesHandler extends IoTypeHandler<byte[]> {
    @Override
    public String[] getKnownTypes() {
        return new String[]{"bytes"};
    }

    @Override
    public byte[] handle(String type, InputStream source) throws TaskGraphException {
        if (type.equals("bytes")) {
            return readAsBytes(source);
        }
        return null;
    }
}
