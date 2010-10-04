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
public class StreamHandler extends IoTypeHandler<InputStream> {
    @Override
    public String[] getKnownTypes() {
        return new String[]{"stream"};
    }

    @Override
    public InputStream read(String type, InputStream source) throws TaskGraphException {
        if (type.equals("stream")) {
            return source;
        }
        return null;
    }

    @Override
    public void write(InputStream inputStream, OutputStream sink) throws TaskGraphException {
        byte[] bytes = new byte[8192];
        int c;
        try {
            while ((c = inputStream.read(bytes)) != -1) {
                sink.write(bytes, 0, c);
            }
            sink.flush();
        } catch (IOException e) {
            throw new TaskGraphException(e);
        }
    }
}
