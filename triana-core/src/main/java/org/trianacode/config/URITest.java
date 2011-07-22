package org.trianacode.config;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 22/07/2011
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */
public class URITest {

    public static void main(String[] args) throws IOException {

        String fileName = "/folder1/folder2/file.txt";
        File file = new File(fileName);

        System.out.println(file.getAbsolutePath());
        System.out.println(file.getCanonicalPath());
        System.out.println(file.getName());
        System.out.println(file.toURI());
        System.out.println(file.toURI().toURL());
        System.out.println(file.toURI().toASCIIString());
        System.out.println(file.toURI().toString());
        System.out.println(file.toURI().toURL().toExternalForm());
        System.out.println(file.toURI().toURL().toString());
    }
}
