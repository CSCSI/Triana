package org.trianacode.shiwaall.iwir.execute;

import org.shiwa.fgi.iwir.AbstractPort;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.event.NodeEvent;
import org.trianacode.taskgraph.event.NodeListener;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 18/09/2012
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class ExecutableNode implements NodeListener{

    private Node node = null;
    private AbstractPort abstractPort = null;
    private String filename = null;

    public ExecutableNode(Node node, AbstractPort abstractPort) {
        this.node = node;
        this.abstractPort = abstractPort;
    }

    public ExecutableNode(AbstractPort abstractPort, String filename) {
        this.abstractPort = abstractPort;
        this.filename = filename;
    }

    public ExecutableNode(Node node, String fileName) {
        this.node = node;
        filename = fileName;
    }


    public String toString(){
        String nodeName = null;
        if(node != null) {
            nodeName = node.getName();
        }
        return "[" + nodeName + " : " + abstractPort + " : " + filename + "]";
    }

    public AbstractPort getAbstractPort() {
        return abstractPort;
    }

    public void setAbstractPort(AbstractPort abstractPort) {
        this.abstractPort = abstractPort;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public void nodeConnected(NodeEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void nodeDisconnected(NodeEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void nodeParentChanged(NodeEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void nodeChildChanged(NodeEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void parameterNameSet(NodeEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
