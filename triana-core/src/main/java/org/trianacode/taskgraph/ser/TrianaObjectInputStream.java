package org.trianacode.taskgraph.ser;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.trianacode.taskgraph.tool.ClassLoaders;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jun 25, 2010
 */

public class TrianaObjectInputStream extends ObjectInputStream {

    public TrianaObjectInputStream(InputStream inputStream) throws IOException {
        super(inputStream);
    }

    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        return ClassLoaders.forName(desc.getName());
    }
}
