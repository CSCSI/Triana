package org.trianacode.shiwaall.extras;

import java.io.BufferedWriter;
import java.io.FileWriter;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Jan 17, 2011
 * Time: 6:44:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileBuilder {

    /**
     * Instantiates a new file builder.
     *
     * @param name the name
     * @param contents the contents
     */
    public FileBuilder(String name, String contents) {
        try {
            // Create file
            FileWriter fstream = new FileWriter(name);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(contents);
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
