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
public class BooleanHandler extends IoTypeHandler<Boolean> {
    @Override
    public String[] getKnownTypes() {
        return new String[]{"boolean"};
    }

    @Override
    public Boolean read(String type, InputStream source) throws TaskGraphException {
        if (type.equals("boolean")) {
            try {
                return Boolean.parseBoolean(readAsString(source));
            } catch (Exception e) {
                throw new TaskGraphException(e);
            }
        }
        return null;
    }

    @Override
    public void write(Boolean b, OutputStream sink) throws TaskGraphException {
        try {
            sink.write(b.toString().getBytes("UTF-8"));
            sink.flush();
        } catch (IOException e) {
            throw new TaskGraphException(e);
        }
    }

}
