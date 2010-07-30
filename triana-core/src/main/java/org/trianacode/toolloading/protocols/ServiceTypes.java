package org.trianacode.toolloading.protocols;

import java.util.Hashtable;

/**
 * The ... class ...
 * <p/>
 * Created by scmijt
 * Date: Jun 24, 2010
 * Time: 5:28:37 PM
 */
public class ServiceTypes {
    Hashtable<String, TypeMetadata> types = new Hashtable<String, TypeMetadata>();

    public ServiceTypes() {
    }

    /**
     * Registers a service type with an input form for interfacing with a application for
     * interfacing with a command line application that is designed to connect to
     * this service.
     *
     * @param type
     * @param description
     * @param inputForm
     * @param commandLine command line of an executable that can be l;aunched to connect
     * to this app
     */
    public void registerServiceType(String type, String description, Class inputForm, String commandLine) {
        types.put(type, new TypeMetadata(description, inputForm, commandLine));
    }

    /**
     * Registers a service type with an input form for interfacing with a application for
     * interfacing with a command line application that is designed to connect to
     * this service.

     * @param type
     * @param description
     * @param inputForm
     */
    public void registerServiceType(String type, String description, Class inputForm) {
        types.put(type, new TypeMetadata(description, inputForm));
    }

    /**
     * Registers a service type with an input form for interfacing with a application for
     * interfacing with a command line application that is designed to connect to
     * this service.      
     * @param type
     * @param description
     */
    public void registerServiceType(String type, String description) {
        types.put(type, new TypeMetadata(description));
    }
    
    /**
     * Returns an input form for the given type
     *
     * @param type
     * @return
     */
    public Class getInputFormFor(String type) {
        TypeMetadata metadata = types.get(type);
        if (metadata!=null)
            return metadata.getInputForm();
        else
            return null;
    }

    /**
     * Gets the long description for the type
     * 
     * @param type
     * @return
     */
    public String getLongDescriptionFor(String type) {
        TypeMetadata metadata = types.get(type);
        if (metadata!=null)
            return metadata.getDescription();
        else
            return null;
    }

    public String getcommandLineFor(String type) {
        TypeMetadata metadata = types.get(type);
        if (metadata!=null)
            return metadata.getCommandLine();
        else
            return null;
    }

}
