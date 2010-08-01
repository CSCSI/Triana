package org.trianacode.discovery.protocols.thirdparty;

/**
 * Metadata for a service type
 *
 * User: scmijt
 * Date: Jul 27, 2010
 * Time: 3:34:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProtocolMetadata {
    String description;
    TDP protocol =null;

    ProtocolMetadata(String description) {
        this.description = description;
    }

    public ProtocolMetadata(String description, TDP protocol) {
        this.description = description;
        this.protocol = protocol;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TDP getProtocol() {
        return protocol;
    }

    public void setProtocol(TDP protocol) {
        this.protocol = protocol;
    }
}