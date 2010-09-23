package org.trianacode.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: scmijt
 * Date: Sep 23, 2010
 * Time: 11:28:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class PropertyLoader {

    TrianaProperties props;

    /**
      * Creates a config by loading in the properties from the various sources of
      * property files and system properties.
      *
      * @throws java.io.IOException
      */
     public PropertyLoader(Properties properties) throws IOException {
         // either load from system OR from the properties provided.
         if(properties == null){
             this.props = new TrianaProperties();
             loadProperties(TrianaProperties.PROPERTY_FILE);

             // loads in the file list using the property.file.list system property
             String filelist = System.getProperty(TrianaProperties.PROPERTY_FILE_LIST);
             String filelistarr[];

             if (filelist!=null) {
                filelistarr=filelist.split(",");
                 for (String file: filelistarr) {
                    loadProperties(file);
                }
             }
         } else {
             this.props = new TrianaProperties(properties);
         }

        // over-ride any of the values with system values, in case, the user specified any of the
        // value directly on the command line
        
        props.overrideUsingSystemProperties();
     }


     /**
      * Returns the properties specific to this GumpConfig
      *
      * Implementors of MulticastSockets and Transports can be configured using these properties
      */
     public Properties getProperties(){
         return props;
     }

     /**
      * loads in the properties from the the given filename, which also acts as
      * the property name.  The method searches for properties in three different locations:
      *
      * <ol>
      * <li> A filename specified by the property named propertyFileName
      * <li> the user's getApplicationDataDir directory, in a directory called .gump/
      * <li> then finally in the jar file in a file called config/propertyFileName
      * </ol>
      *
      * There IS ONE propertyFileName, and several custom files that can be loaded in by the system:
      *
      * <ol>
      * <li> gump.properties
      * <li> gump.properties.file.list, which is a list of file names (comma separated) contains files
      * that will also be loaded by the system.
      * </ol>
      *
      * @throws java.io.IOException
      */
     public void loadProperties(String propertyFileName) throws IOException {
         InputStream stream=null;

         //Try 3 ways to find the properties.

         String propertyFileLocation = System.getProperty(propertyFileName);
         String propertyUserLoc = Home.getDefaultConfigFile();
         File userFile = new File(propertyUserLoc);

         if (propertyFileLocation!=null) {
             stream = new FileInputStream(propertyFileLocation);
         } else if (userFile.exists()) { // found file in ~/.gump
             stream = new FileInputStream(userFile);
         } else {
             // try second find config/gump.properties in the jar file.
             String properties = "config/" + propertyFileName;
             URL propertyURL = Thread.currentThread().getContextClassLoader().getResource(properties);
             if (propertyURL!=null)
                 stream = propertyURL.openStream();
         }

         if (stream!=null) {
             props.load(stream);
             //props.load(stream);
             stream.close();
         } else {
             System.err.println("WARNING: Cannot find " + propertyFileName + " file (at "+userFile.getAbsolutePath()+") to initialise the system");
         }
     }

     public void printProperties() {
         System.out.println("Properties are :");
         Enumeration propNames;
         // copy into the system properties
         propNames = System.getProperties().keys();

         while (propNames.hasMoreElements()) {
             String el=(String)propNames.nextElement();

             System.out.println(el + " = " + System.getProperty(el));
         }
     }
}
