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

    HashMap<AbstractPort, Node> portNodeHashMap;
    HashMap<Node, AbstractPort> nodeAbstractPortHashMap;
    Vector<NodeProxy> nodeProxies;

    public NodePortTranslator() {
        portNodeHashMap = new HashMap<AbstractPort, Node>();
        nodeAbstractPortHashMap = new HashMap<Node, AbstractPort>();
        nodeProxies = new Vector<NodeProxy>();
    }

    public void addNodeProxy(NodeProxy nodeProxy) {
        portNodeHashMap.put(nodeProxy.getAbstractPort(), nodeProxy.getNode());
        nodeAbstractPortHashMap.put(nodeProxy.getNode(), nodeProxy.getAbstractPort());
        nodeProxies.add(nodeProxy);

        System.out.println("\n\n\nRecording  abstract : " + nodeProxy.getAbstractPort().getUniqueId() + " node : " + nodeProxy.getNode().getName());

        listAll();
    }

    public Node getNodeForAbstractPort(AbstractPort abstractPort) {
        return portNodeHashMap.get(abstractPort);
    }

    public AbstractPort getAbstractPortForNode(Node node) {
        return nodeAbstractPortHashMap.get(node);
    }

    public void listAll() {
        System.out.println("\n*** Listing all node stuff, abstract key");
        for (Map.Entry entry : portNodeHashMap.entrySet()) {
            AbstractPort abstractPort = (AbstractPort) entry.getKey();
            Node node = (Node) entry.getValue();
            System.out.println(abstractPort.getUniqueId() + " " + node.getName());
        }
        System.out.println("\n***Node key");
        for (Map.Entry entry : nodeAbstractPortHashMap.entrySet()) {
            AbstractPort abstractPort = (AbstractPort) entry.getValue();
            Node node = (Node) entry.getKey();
            System.out.println(node.getName() + " " + abstractPort.getUniqueId());
        }
    }
}
