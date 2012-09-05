package org.trianacode.shiwaall.iwir.exporter;

import org.shiwa.fgi.iwir.AbstractPort;
import org.trianacode.taskgraph.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 7/12/11
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodePortTranslator {

    /** The port node hash map. */
    HashMap<String, Node> portNodeHashMap;
    
    /** The node abstract port id hash map. */
    HashMap<Node, String> nodeAbstractPortIDHashMap;
    
    /** The abstract port hash map. */
    HashMap<String, AbstractPort> abstractPortHashMap;
    
    /** The node proxies. */
    Vector<NodeProxy> nodeProxies;

    /**
     * Instantiates a new node port translator.
     */
    public NodePortTranslator() {
        portNodeHashMap = new HashMap<String, Node>();
        nodeAbstractPortIDHashMap = new HashMap<Node, String>();
        abstractPortHashMap = new HashMap<String, AbstractPort>();
        nodeProxies = new Vector<NodeProxy>();
    }

    /**
     * Adds the node proxy.
     *
     * @param nodeProxy the node proxy
     */
    public void addNodeProxy(NodeProxy nodeProxy) {
        portNodeHashMap.put(nodeProxy.getAbstractPort().getUniqueId(), nodeProxy.getNode());
        nodeAbstractPortIDHashMap.put(nodeProxy.getNode(), nodeProxy.getAbstractPort().getUniqueId());
        abstractPortHashMap.put(nodeProxy.getAbstractPort().getUniqueId(), nodeProxy.getAbstractPort());
        nodeProxies.add(nodeProxy);

        System.out.println("    Recording  abstract : " + nodeProxy.getAbstractPort().getUniqueId() + " node : " + nodeProxy.getNode().getName());

//        listAll();
    }

    /**
     * Gets the node for abstract port.
     *
     * @param abstractPortID the abstract port id
     * @return the node for abstract port
     */
    public Node getNodeForAbstractPort(String abstractPortID) {
        Node node = portNodeHashMap.get(abstractPortID);
        if (node != null) {
            return node;
        } else {
            System.out.println("Error finding node for " + abstractPortID);
            return null;
        }
    }

    /**
     * Gets the abstract port for node.
     *
     * @param node the node
     * @return the abstract port for node
     */
    public AbstractPort getAbstractPortForNode(Node node) {
        AbstractPort abstractPort = abstractPortHashMap.get(nodeAbstractPortIDHashMap.get(node));
        if (abstractPort != null) {
            return abstractPort;
        } else {
            System.out.println("Error finding abstractPort for " + node.getName());
            return null;
        }
    }

    /**
     * List all.
     */
    public void listAll() {
        System.out.println("\n*** Listing all node stuff, abstract key");
        for (Map.Entry entry : portNodeHashMap.entrySet()) {
            String abstractPortID = (String) entry.getKey();
            Node node = (Node) entry.getValue();
            System.out.println(abstractPortID + " " + node.getName());
        }
        System.out.println("\n***Node key");
        for (Map.Entry entry : nodeAbstractPortIDHashMap.entrySet()) {
            String abstractPortID = (String) entry.getValue();
            Node node = (Node) entry.getKey();
            System.out.println(node.getName() + " " + abstractPortID);
        }
    }
}
