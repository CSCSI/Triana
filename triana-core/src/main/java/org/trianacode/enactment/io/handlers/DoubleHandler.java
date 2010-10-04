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
public class DoubleHandler extends IoTypeHandler<Double> {
    @Override
    public String[] getKnownTypes() {
        return new String[]{"double"};
    }

    @Override
    public Double read(String type, InputStream source) throws TaskGraphException {
        if (type.equals("double")) {
            try {
                return Double.parseDouble(readAsString(source));
            } catch (Exception e) {
                throw new TaskGraphException(e);
            }
        }
        return null;
    }

    @Override
    public void write(Double d, OutputStream sink) throws TaskGraphException {
        try {
            sink.write(d.toString().getBytes("UTF-8"));
            sink.flush();
        } catch (IOException e) {
            throw new TaskGraphException(e);
        }
    }
}
