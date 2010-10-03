package org.trianacode.enactment.io.handlers;

import org.trianacode.enactment.io.IoTypeHandler;
import org.trianacode.taskgraph.TaskGraphException;

import java.io.InputStream;

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
    public Double handle(String type, InputStream source) throws TaskGraphException {
        if (type.equals("double")) {
            try {
                return Double.parseDouble(readAsString(source));
            } catch (Exception e) {
                throw new TaskGraphException(e);
            }
        }
        return null;
    }
}
