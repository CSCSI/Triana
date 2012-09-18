package org.trianacode.enactment.io.handlers;

import org.trianacode.enactment.io.IoTypeHandler;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.Base64;
import org.trianacode.taskgraph.ser.TrianaObjectInputStream;

import java.io.*;

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
    public Serializable read(String type, InputStream source) throws TaskGraphException {
        if (type.equals("java64")) {
            System.out.println("should be java64 encoded");
            try {
                byte[] bytes = Base64.decode(readAsString(source));
                ObjectInputStream in = new TrianaObjectInputStream(new ByteArrayInputStream(bytes));
                return (Serializable) in.readObject();
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new TaskGraphException(e);
            }
        } else if (type.equals("java")) {
            System.out.println("should be java encoded");
            try {
                ObjectInputStream in = new TrianaObjectInputStream(source);
                return (Serializable) in.readObject();
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new TaskGraphException(e);
            }
        }
        return null;
    }

    @Override
    public void write(Serializable serializable, OutputStream sink) throws TaskGraphException {
        System.out.println("serializing " + serializable.toString());
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream marshall = new ObjectOutputStream(bout);
            marshall.writeObject(serializable);
            String ret = Base64.encode(bout.toByteArray());
            sink.write(ret.getBytes("UTF-8"));

            marshall.close();
            bout.close();
        } catch (IOException e) {
            throw new TaskGraphException(e);
        }
    }
}
