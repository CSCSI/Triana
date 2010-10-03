package org.trianacode.enactment.io;

import org.trianacode.taskgraph.TaskGraphException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 3, 2010
 */
public class StreamResolver {


    public InputStream handle(String source) throws TaskGraphException {

        File f;
        if (source.startsWith("file:")) {
            try {
                f = new File(new URI(source));

            } catch (Exception e) {
                throw new TaskGraphException(e);
            }
        } else {
            f = new File(source);
        }
        if (!f.exists() || f.length() == 0) {
            throw new TaskGraphException("cannot load file:" + source);
        }
        try {
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            throw new TaskGraphException(e);
        }
    }
}
