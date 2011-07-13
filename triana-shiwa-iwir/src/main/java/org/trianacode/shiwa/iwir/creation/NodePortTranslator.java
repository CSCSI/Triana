package org.trianacode.shiwa.iwir.creation;

import org.shiwa.fgi.iwir.AbstractPort;
import org.trianacode.taskgraph.Node;

import java.util.HashMap;
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
    }

    public Node getNodeForAbstractPort(AbstractPort abstractPort) {
        return portNodeHashMap.get(abstractPort);
    }

    public AbstractPort getAbstractPortForNode(Node node) {
        return nodeAbstractPortHashMap.get(node);
    }
}
