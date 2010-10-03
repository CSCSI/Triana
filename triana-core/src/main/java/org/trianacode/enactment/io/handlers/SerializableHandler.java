package org.trianacode.enactment.io.handlers;

import org.trianacode.enactment.io.IoTypeHandler;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.Base64;
import org.trianacode.taskgraph.ser.TrianaObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 3, 2010
 */
public class SerializableHandler extends IoTypeHandler<Serializable> {
    @Override
    public String[] getKnownTypes() {
        return new String[]{"java", "java64"};
    }

    @Override
    public Serializable handle(String type, InputStream source) throws TaskGraphException {
        if (type.equals("java64")) {
            try {
                byte[] bytes = Base64.decode(readAsString(source));
                ObjectInputStream in = new TrianaObjectInputStream(new ByteArrayInputStream(bytes));
                return (Serializable) in.readObject();
            }
            catch (Exception e) {
                throw new TaskGraphException(e);
            }
        } else if (type.equals("java")) {
            try {
                ObjectInputStream in = new TrianaObjectInputStream(source);
                return (Serializable) in.readObject();
            }
            catch (Exception e) {
                throw new TaskGraphException(e);
            }
        }
        return null;
    }
}
