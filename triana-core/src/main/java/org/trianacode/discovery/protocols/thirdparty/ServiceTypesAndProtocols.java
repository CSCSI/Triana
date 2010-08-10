package org.trianacode.discovery.protocols.thirdparty;

import java.util.Hashtable;

/**
 * Resgisters Service Types and Protocols
 * <p/>
 * Created by scmijt Date: Jun 24, 2010 Time: 5:28:37 PM
 */
public class ServiceTypesAndProtocols {
    Hashtable<String, ProtocolMetadata> types = new Hashtable<String, ProtocolMetadata>();

    public ServiceTypesAndProtocols() {
    }

    /**
     * Registers a service type with an input form for interfacing with a application for interfacing with a command
     * line application that is designed to connect to this service.
     *
     * @param type
     * @param description
     * @param protocol
     */
    public void registerServiceType(String type, String description, BonjourService protocol) {
        types.put(type, new ProtocolMetadata(description, protocol));
    }

    /**
     * Registers a service type with an input form for interfacing with a application for interfacing with a command
     * line application that is designed to connect to this service.
     *
     * @param type
     * @param description
     */
    public void registerServiceType(String type, String description) {
        types.put(type, new ProtocolMetadata(description));
    }

    /**
     * Returns an input form for the given type
     *
     * @param type
     * @return
     */
    public BonjourService getProtocolFor(String type) {
        ProtocolMetadata metadata = types.get(type);
        if (metadata != null) {
            return metadata.getProtocol();
        } else {
            return null;
        }
    }

    /**
     * Gets the long description for the type
     *
     * @param type
     * @return
     */
    public String getLongDescriptionFor(String type) {
        ProtocolMetadata metadata = types.get(type);
        if (metadata != null) {
            return metadata.getDescription();
        } else {
            return null;
        }
    }
}
