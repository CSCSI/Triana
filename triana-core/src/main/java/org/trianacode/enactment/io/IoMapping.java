package org.trianacode.enactment.io;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 2, 2010
 */
public class IoMapping {

    private IoType ioType;
    private String nodeName;

    public IoMapping(IoType ioType, String nodeName) {
        this.ioType = ioType;
        this.nodeName = nodeName;
    }

    public IoType getIoType() {
        return ioType;
    }

    public String getNodeName() {
        return nodeName;
    }
}
