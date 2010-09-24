package org.trianacode.config;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 *
 * Class that attempts to look in various places for the specified file
 * 
 * Date: Sep 23, 2010
 * Time: 5:10:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceManagement {
    public enum Type {TEMPLATE, PROPERTY, TOOL};


    /**
     * Gets an input stream for the given filename and searches in locations specified for that type of
     * file.
     *
     * @param filen the name of the file to be searched for
     * @param type the type of file this is.
     * 
     * @return
     */
    public static InputStream getInputStreamFor(String filen, Type type) throws FileNotFoundException, IOException {
        String filelist;
        InputStream stream;

        switch (type) {
            case PROPERTY :
                filelist = System.getProperty(TrianaProperties.PROPERTY_SEARCH_PATH_PROPERTY);
                break;
            case TEMPLATE :
                filelist = System.getProperty(TrianaProperties.TEMPLATE_SEARCH_PATH_PROPERTY);
                break;
            case TOOL :
                filelist = System.getProperty(TrianaProperties.TOOLBOX_SEARCH_PATH_PROPERTY);
                break;
            default: throw new IOException("Type for file " + filen + " Not Found!");
        }


        String dirlistarr[];

        dirlistarr=filelist.split(",");
         for (String dir: dirlistarr) {
             String file = dir + File.separator + filen;
             stream = getInputStreamFor(file);
             if (stream !=null)
                 return stream; // if we find a valid stream then return
         }

        return getInputStreamFor(filen); // lastly try the filename itself
    }


    /**
     * Attempts to load the provided file from the file system and then try the jar file. The method
     * simply uses the absolute path provided. 
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static InputStream getInputStreamFor(String file) throws FileNotFoundException, IOException {
        if (file==null) return null;
        
        InputStream stream=null;

        File filen=new File(file);
       // try load the file from the filesystem and if not, search for it in a jar.

        if (filen.exists()) {
            stream = new FileInputStream(filen);
        } else {
            URL fileURL = Thread.currentThread().getContextClassLoader().getResource(file);
            if (fileURL!=null)
                stream = fileURL.openStream();
        }
        return stream;
    }

}
