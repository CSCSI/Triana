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
public class BytesHandler extends IoTypeHandler<byte[]> {
    @Override
    public String[] getKnownTypes() {
        return new String[]{"bytes"};
    }

    @Override
    public byte[] read(String type, InputStream source) throws TaskGraphException {
        if (type.equals("bytes")) {
            return readAsBytes(source);
        }
        return null;
    }

    @Override
    public void write(byte[] bytes, OutputStream sink) throws TaskGraphException {
        try {
            sink.write(bytes);
            sink.flush();
        } catch (IOException e) {
            throw new TaskGraphException(e);
        }
    }
}
