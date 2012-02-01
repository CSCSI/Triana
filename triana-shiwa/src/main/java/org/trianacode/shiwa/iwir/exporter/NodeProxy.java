package org.trianacode.shiwa.iwir.exporter;

import org.shiwa.fgi.iwir.AbstractPort;
import org.trianacode.taskgraph.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 15/04/2011
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class NodeProxy {

    private String name;
    private int nodeNumber;
    private boolean attached;
    private String from;
    private String to;
    private int type;
    private static int inputNode = 0;
    private static int outputNode = 1;
    private AbstractPort abstractPort;
    private Node node;

    public NodeProxy(String name, int nodeNumber, boolean attached, int type, String externalNode) {
        this.name = name;
        this.nodeNumber = nodeNumber;
        this.attached = attached;
        this.type = type;
        if (type == NodeProxy.inputNode) {
            this.from = externalNode;
        }
        if (type == NodeProxy.outputNode) {
            this.to = externalNode;
        }
    }

    public NodeProxy(Node node, AbstractPort abstractPort) {
        this.node = node;
        this.abstractPort = abstractPort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public boolean isAttached() {
        return attached;
    }

    public void setAttached(boolean attached) {
        this.attached = attached;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        if (type == NodeProxy.inputNode || type == NodeProxy.outputNode) {
            this.type = type;
        }
    }

    public Node getNode() {
        return node;
    }

    public AbstractPort getAbstractPort() {
        return abstractPort;
    }
}
