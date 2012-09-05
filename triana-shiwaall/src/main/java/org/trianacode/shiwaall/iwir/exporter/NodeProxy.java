package org.trianacode.shiwaall.iwir.exporter;

import org.shiwa.fgi.iwir.AbstractPort;
import org.trianacode.taskgraph.Node;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 15/04/2011
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class NodeProxy {

    /** The name. */
    private String name;
    
    /** The node number. */
    private int nodeNumber;
    
    /** The attached. */
    private boolean attached;
    
    /** The from. */
    private String from;
    
    /** The to. */
    private String to;
    
    /** The type. */
    private int type;
    
    /** The input node. */
    private static int inputNode = 0;
    
    /** The output node. */
    private static int outputNode = 1;
    
    /** The abstract port. */
    private AbstractPort abstractPort;
    
    /** The node. */
    private Node node;

    /**
     * Instantiates a new node proxy.
     *
     * @param name the name
     * @param nodeNumber the node number
     * @param attached the attached
     * @param type the type
     * @param externalNode the external node
     */
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

    /**
     * Instantiates a new node proxy.
     *
     * @param node the node
     * @param abstractPort the abstract port
     */
    public NodeProxy(Node node, AbstractPort abstractPort) {
        this.node = node;
        this.abstractPort = abstractPort;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the node number.
     *
     * @return the node number
     */
    public int getNodeNumber() {
        return nodeNumber;
    }

    /**
     * Sets the node number.
     *
     * @param nodeNumber the new node number
     */
    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    /**
     * Checks if is attached.
     *
     * @return true, if is attached
     */
    public boolean isAttached() {
        return attached;
    }

    /**
     * Sets the attached.
     *
     * @param attached the new attached
     */
    public void setAttached(boolean attached) {
        this.attached = attached;
    }

    /**
     * Gets the from.
     *
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the from.
     *
     * @param from the new from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Gets the to.
     *
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the to.
     *
     * @param to the new to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(int type) {
        if (type == NodeProxy.inputNode || type == NodeProxy.outputNode) {
            this.type = type;
        }
    }

    /**
     * Gets the node.
     *
     * @return the node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Gets the abstract port.
     *
     * @return the abstract port
     */
    public AbstractPort getAbstractPort() {
        return abstractPort;
    }
}
