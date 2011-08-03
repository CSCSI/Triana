package org.trianacode.shiwa.iwir.creation;

import org.shiwa.fgi.iwir.AbstractPort;
import org.trianacode.taskgraph.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 7/12/11
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodePortTranslator {

    HashMap<String, Node> portNodeHashMap;
    HashMap<Node, String> nodeAbstractPortIDHashMap;
    HashMap<String, AbstractPort> abstractPortHashMap;
    Vector<NodeProxy> nodeProxies;

    public NodePortTranslator() {
        portNodeHashMap = new HashMap<String, Node>();
        nodeAbstractPortIDHashMap = new HashMap<Node, String>();
        abstractPortHashMap = new HashMap<String, AbstractPort>();
        nodeProxies = new Vector<NodeProxy>();
    }

    public void addNodeProxy(NodeProxy nodeProxy) {
        portNodeHashMap.put(nodeProxy.getAbstractPort().getUniqueId(), nodeProxy.getNode());
        nodeAbstractPortIDHashMap.put(nodeProxy.getNode(), nodeProxy.getAbstractPort().getUniqueId());
        abstractPortHashMap.put(nodeProxy.getAbstractPort().getUniqueId(), nodeProxy.getAbstractPort());
        nodeProxies.add(nodeProxy);

        System.out.println("    Recording  abstract : " + nodeProxy.getAbstractPort().getUniqueId() + " node : " + nodeProxy.getNode().getName());

//        listAll();
    }

    public Node getNodeForAbstractPort(String abstractPortID) {
        Node node = portNodeHashMap.get(abstractPortID);
        if (node != null) {
            return node;
        } else {
            System.out.println("Error finding node for " + abstractPortID);
            return null;
        }
    }

    public AbstractPort getAbstractPortForNode(Node node) {
        AbstractPort abstractPort = abstractPortHashMap.get(nodeAbstractPortIDHashMap.get(node));
        if (abstractPort != null) {
            return abstractPort;
        } else {
            System.out.println("Error finding abstractPort for " + node.getName());
            return null;
        }
    }

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
