package org.trianacode.enactment.io.handlers;

import org.trianacode.enactment.io.IoTypeHandler;
import org.trianacode.taskgraph.TaskGraphException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    public String read(String type, InputStream source) throws TaskGraphException {
        if (type.equals("string")) {
            return readAsString(source);
        }
        return null;
    }

    @Override
    public void write(String s, OutputStream sink) throws TaskGraphException {
        try {
            sink.write(s.getBytes("UTF-8"));
            sink.flush();
        } catch (IOException e) {
            throw new TaskGraphException(e);
        }
    }
}
