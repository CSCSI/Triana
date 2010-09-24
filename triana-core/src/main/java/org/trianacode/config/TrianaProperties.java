package org.trianacode.config;

import org.trianacode.TrianaInstance;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 */
public class TrianaProperties extends Properties {

    public static String DEFAULT_COMMENTS =
            "This is the property configuration for Triana\n" +
            "To change properties use the GUI, set properties using the command line\n" +
            " or edit this file directly.  However, there is a defined order for property\n" +
                "overriding, which needs to be taken into account\n";

    public static String DOMAIN = "org.trianacode";


    // SEARCH PATHS comma separated file list of property files.

    public static final String PROPERTY_SEARCH_PATH_PROPERTY = DOMAIN + ".property.search.path";
    public static final String TOOLBOX_SEARCH_PATH_PROPERTY =  DOMAIN + ".toolbox.search.path";
    public static final String TEMPLATE_SEARCH_PATH_PROPERTY =  DOMAIN + ".template.search.path";

    // List of property files that can contains properties (1 or more).  Default is DEFAULT_PROPERTY_FILE
    // but this list overides the default.  Comma separated list of files.

    public static final String PROPERTY_FILE_LIST_PROPERTY = DOMAIN + ".property.file.list";

    public static final String WEB_TEMPLATE_PROPERTY =  DOMAIN + ".web.template";
    public static final String WEB_TOOL_TEMPLATE_PROPERTY =  DOMAIN + ".web.tool.template";
    public static final String TOOLBOX_DESCRIPTION_TEMPLATE_PROPERTY =  DOMAIN + ".toolbox.description.template";
    public static final String TOOL_HELP_TEMPLATE_PROPERTY =  DOMAIN + ".tool.help.template";
    public static final String CREATE_TOOL_INSTANCE_PROPERTY =  DOMAIN + ".create.tool.instance";
    public static final String TOOL_INSTANCE_PROPERTY =  DOMAIN + ".tool.instance";
    public static final String TOOL_PARAMETER_PROPERTY =  DOMAIN + ".tool.parameter.window.template";

    TrianaInstance engine;

    public TrianaProperties(TrianaInstance engine) {
        this(engine, null);
    }

    /**
     * Initialises the properties giving the defaults
     * 
     * @param initValues
     */
    public TrianaProperties(TrianaInstance engine, Properties initValues) {
        this.engine=engine;
        if (initValues==null) {
            // put default values
            this.putAll(getDefaultConfiguration());
        } else {
            putAll(initValues);
        }
   }

    public void overrideUsingSystemProperties() {
        // copy system properties to OVERWRITE any of the defaults i.e. these are
         // specified and should over-ride all default variables or those specified from
         // config files.

         Properties systemprops = System.getProperties();
         Enumeration propNames = systemprops.keys();
         while (propNames.hasMoreElements()) {
             String prop=(String)propNames.nextElement();
             String value = systemprops.getProperty(prop);

             if ((value!=null) && (value.contains(DOMAIN))) {
                 remove(prop);
                 setProperty(prop, value);
             }
         }
    }


    public static Properties getDefaultConfiguration() {
        Properties properties = new Properties();

        properties.put(Home.DEFAULT_PROPERTY_FILE, Home.getApplicationDataDir() + DOMAIN + " .properties");
        // PROPERTY_FILE_LIST is null

        String homeDir = Home.getHomeProper() + "/";

        properties.put(TOOLBOX_SEARCH_PATH_PROPERTY, homeDir + "triana-toolboxes, " + homeDir + "triana-pegasus");

        properties.put(TEMPLATE_SEARCH_PATH_PROPERTY, "");
        properties.put(WEB_TEMPLATE_PROPERTY,  "");
        properties.put(WEB_TOOL_TEMPLATE_PROPERTY,  "");
        properties.put(TOOLBOX_DESCRIPTION_TEMPLATE_PROPERTY,  "");
        properties.put(TOOL_HELP_TEMPLATE_PROPERTY,  "");
        properties.put(CREATE_TOOL_INSTANCE_PROPERTY,  "");
        properties.put(TOOL_INSTANCE_PROPERTY,   "");
        properties.put(TOOL_PARAMETER_PROPERTY, "");

        return properties;
    }


    /**
     * Creates a string version of the current property configuration
     *
     * @return a String of the properties.
     */
    public String toString() {
        Writer stringWriter = new StringWriter();

        String contents=null;

        try {
            this.store(stringWriter, DEFAULT_COMMENTS);
            stringWriter.flush();
            contents = stringWriter.toString();
            stringWriter.close();
        } catch (IOException e) {
            // is this possible?
            return null;
        }

        return contents;
    }
    

    /**
     * Saves the current properties to App_Dir/org.trianacode.properties
     *
     * @throws IOException
     */
    public void saveProperties() throws IOException {
        saveProperties(DEFAULT_COMMENTS);
        }
    

    /**
     *  Saves the properties to the default config file
     */
    public void saveProperties(String comments) throws IOException {
        File file = new File(Home.getDefaultConfigFile());

        OutputStream outstream = new FileOutputStream(file);

        this.store(outstream, comments);

        outstream.flush();
        outstream.close();
    }


}

