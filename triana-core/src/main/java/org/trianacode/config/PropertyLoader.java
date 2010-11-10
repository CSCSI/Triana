package org.trianacode.config;

import org.trianacode.TrianaInstance;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Property loader:  This tries several ways of initialising the properties.   There are two use
 * cases:
 * <p/>
 * <ol>
 * <li> When app is ran from the command line
 * <li> When app is embedded.
 * </ol>
 * <p/>
 * When run from the command line the system defaults to its default values by loading in from the triana
 * properties file in the app directory or from default values.  When ran from an app,
 * it starts with the values passed from a properties object (if available).   Thereafter, it
 * goes through a sequence of searching the property file list if available and then overriding any
 * property with a property that has been defined using the System properties.
 * <p/>
 * User: Ian Taylor
 * Date: Sep 23, 2010
 * Time: 11:28:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class PropertyLoader {

    TrianaProperties props;
    TrianaInstance engine;

    /**
     * Creates a config by loading in the properties from the various sources of
     * property files and system properties.
     *
     * @throws java.io.IOException
     */
    public PropertyLoader(TrianaInstance engine, Properties properties) throws IOException {
        this.engine = engine;
        // either load from system OR from the properties provided.
        if (properties == null) {
            this.props = new TrianaProperties(engine);
            String defaultvals = System.getProperty(Locations.DEFAULT_PROPERTY_FILE);
            if (defaultvals != null) {
                InputStream stream = ResourceManagement.getInputStreamFor(defaultvals);
                if (stream != null)
                    props.load(stream);
            }
        } else {   // assume another app is in control and load properties from there
            this.props = new TrianaProperties(engine, properties);

        }

        // loads in the file list using the property.file.list system property
        String filelist = System.getProperty(TrianaProperties.PROPERTY_FILE_LIST_PROPERTY);


        if (filelist != null) { // we have other configuration files
            String filelistarr[];

            filelistarr = filelist.split(",");
            for (String file : filelistarr) {
                InputStream stream = ResourceManagement.getInputStreamFor(file, ResourceManagement.Type.PROPERTY);
                if (stream != null)
                    props.load(stream);
            }
        }

        // over-ride any of the values with system values, in case, the user specified any of the
        // value directly on the command line or within another application
        props.overrideUsingSystemProperties();

        props.saveProperties();
    }


    /**
     * Returns the properties specific to this GumpConfig
     * <p/>
     * Implementors of MulticastSockets and Transports can be configured using these properties
     */
    public TrianaProperties getProperties() {
        return props;
    }


    public void printProperties() {
        System.out.println("Properties are :");
        Enumeration propNames;
        // copy into the system properties
        propNames = System.getProperties().keys();

        while (propNames.hasMoreElements()) {
            String el = (String) propNames.nextElement();

            System.out.println(el + " = " + System.getProperty(el));
        }
    }
}
