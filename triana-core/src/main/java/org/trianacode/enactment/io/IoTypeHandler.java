package org.trianacode.enactment.io;

import org.trianacode.taskgraph.TaskGraphException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 3, 2010
 */
public abstract class IoTypeHandler<T> {

    public abstract String[] getKnownTypes();

    public abstract T read(String type, InputStream source) throws TaskGraphException;

    public abstract void write(T t, OutputStream sink) throws TaskGraphException;

    public String readAsString(InputStream in) throws TaskGraphException {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] bytes = new byte[8192];
            int c;
            while ((c = in.read(bytes)) != -1) {
                bout.write(bytes, 0, c);
            }
            bout.flush();
            bout.close();
            return new String(bout.toByteArray());
        } catch (IOException e) {
            throw new TaskGraphException(e);
        }
    }

    public byte[] readAsBytes(InputStream in) throws TaskGraphException {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] bytes = new byte[8192];
            int c;
            while ((c = in.read(bytes)) != -1) {
                bout.write(bytes, 0, c);
            }
            bout.flush();
            bout.close();
            return bout.toByteArray();
        } catch (IOException e) {
            throw new TaskGraphException(e);
        }
    }


}
