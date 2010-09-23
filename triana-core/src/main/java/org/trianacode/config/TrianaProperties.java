package org.trianacode.config;

import java.util.Enumeration;
import java.util.Properties;

/**
 *
 */
public class TrianaProperties extends Properties {

    public static String domain = "org.trianacode";

    // Defines for property names

    public static final String PROPERTY_FILE = domain + " .properties";        // Property file
    public static final String PROPERTY_FILE_LIST = domain + ".property.file.list";  // comma separated file list
    // of property files.
    // Property Names:

    public static final String TOOLBOX_PATH_PROPERTY =  domain + ".toolboxes";
    public static final String TEMPLATE_SEARCH_PATH_PROPERTY =  domain + ".template.search.path";
    public static final String WEB_TEMPLATE_PROPERTY =  domain + ".web.template";
    public static final String WEB_TOOL_TEMPLATE_PROPERTY =  domain + ".web.tool.template";
    public static final String TOOLBOX_DESCRIPTION_TEMPLATE_PROPERTY =  domain + ".toolbox.description.template";
    public static final String TOOL_HELP_TEMPLATE_PROPERTY =  domain + ".tool.help.template";
    public static final String CREATE_TOOL_INSTANCE_PROPERTY =  domain + ".create.tool.instance";
    public static final String TOOL_INSTANCE_PROPERTY =  domain + ".tool.instance";
    public static final String TOOL_PARAMETER_PROPERTY =  domain + ".tool.parameter.window.template";


    public TrianaProperties() {
        this(null);
    }

    /**
     * Initialises the properties giving the defaults
     * 
     * @param initValues
     */
    public TrianaProperties(Properties initValues) {
        if (initValues!=null) {
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

             if ((value!=null) && (value.contains(domain))) {
                 remove(prop);
                 setProperty(prop, value);
             }
         }
    }


    public static Properties getDefaultConfiguration() {
        Properties properties = new Properties();

        properties.put(PROPERTY_FILE, Home.getApplicationDataDir() + domain + " .properties");
        // PROPERTY_FILE_LIST is null

        properties.put(TOOLBOX_PATH_PROPERTY, "");
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


}

