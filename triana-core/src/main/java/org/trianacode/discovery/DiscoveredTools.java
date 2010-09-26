package org.trianacode.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mil.navy.nrl.discovery.json.*;
import org.trianacode.discovery.toolinfo.ToolMetadata;
import mil.navy.nrl.discovery.api.DiscoveredServicesInterface;
import mil.navy.nrl.discovery.api.ServiceInfo;
import mil.navy.nrl.discovery.api.ServiceInfoEndpoint;
import mil.navy.nrl.discovery.tools.DNSServiceNames;
import mil.navy.nrl.discovery.web.resources.ServiceResource;

/**
 * List of discovered tools from all supported bonjour protocols.  Tools are stored as lists of tools according to their
 * service type.
 * <p/>
 * User: scmijt Date: Jul 30, 2010 Time: 11:44:32 AM To change this template use File | Settings | File Templates.
 */
public class DiscoveredTools implements DiscoveredServicesInterface {

    private Hashtable<ServiceInfoEndpoint, List> tools;

    private List<ServiceInfoEndpoint> protocols;
    private DiscoverTools discoverTools;

    public DiscoveredTools(DiscoverTools discoverTools) {
        this.discoverTools = discoverTools;
        tools = new Hashtable<ServiceInfoEndpoint, List>();
        protocols = new ArrayList<ServiceInfoEndpoint>();
    }

    public void addService(ServiceInfoEndpoint serviceInfoEndpoint) {
        if (!protocols.contains(serviceInfoEndpoint)) {
            protocols.add(serviceInfoEndpoint);
        }
    }


    public Hashtable<ServiceInfoEndpoint, List> getTools() {
        return tools;
    }

    public List<ServiceInfoEndpoint> getProtocols() {
        return protocols;
    }

    public void removeService(ServiceInfo serviceInfo) {

        List listCopy = new ArrayList(protocols);

        ServiceInfoEndpoint toDelete = null;

        if (listCopy != null) {
            for (final Iterator iterator = listCopy.iterator(); iterator.hasNext();) {
                ServiceInfoEndpoint service = ((ServiceInfoEndpoint) iterator.next());

                if (service.getServiceName().equals(serviceInfo.getServiceName())
                        && (service.getQualifiedServiceType().equals(serviceInfo.getQualifiedServiceType()))) {
                    toDelete = service;
                }
            }
            if (toDelete != null) {
                protocols.remove(toDelete);
            }
        }
    }

    public void addTool(ToolMetadata tool, ServiceInfoEndpoint serviceType) {

        List list = null;

        synchronized (tools) {
            list = tools.get(serviceType);
            if (list == null) { // create a list for this service type
                list = Collections.synchronizedList(new LinkedList<ServiceResource>());
                tools.put(serviceType, list);
            }
            if (!list.contains(tool)) {
                list.add(tool);
            } // add the service
        }
    }


    protected void removeTool(ToolMetadata toolMetadata, ServiceInfoEndpoint serviceType) {
        List list = null;
        ArrayList listCopy = null;
        synchronized (tools) {
            list = (List) tools.get(serviceType);

            if (list != null) {
                listCopy = new ArrayList(list);
            }
        }
        ToolMetadata toDelete = null;
        if (listCopy != null) {
            for (final Iterator iterator = listCopy.iterator(); iterator.hasNext();) {
                ToolMetadata tool = ((ToolMetadata) iterator.next());

                if (tool.getToolName().equals(toolMetadata.getToolName())
                        && (tool.getUrl().equals(toolMetadata.getUrl()))) {
                    toDelete = tool;
                }
            }
            if (toDelete != null) {
                list.remove(toDelete);
            }
        }
    }

    /**
     * Gets HTML list of all the services, arranged in type order
     *
     * @return
     */

    public String getHTMLList() {
        StringBuffer serverList = new StringBuffer();

        Enumeration keys = tools.keys();

        System.out.println("Retrieving tool List .... with values " + tools.size());

        while (keys.hasMoreElements()) {
            ServiceInfoEndpoint key = (ServiceInfoEndpoint) keys.nextElement();
            System.out.println("Retrieving Key is: " + key);

            List serviceType = tools.get(key);

            serverList.append("<b>" + key + "</b>");
            serverList.append("<ol>");

            for (Object tool : serviceType) {
                ToolMetadata t = (ToolMetadata) tool;
                serverList.append(t.getDisplayName() +
                        " -  <a href=" + t.getUrl() +
                        "> View Service Details" + "</a><br>\n");
            }
            serverList.append("</ol>");
        }

        return serverList.toString();
    }


    /**
     * Gets HTML list of all the services, arranged in type order
     *
     * @return
     */
    public String getJsTreeList() {
        Enumeration keys = tools.keys();
        JsTree tree = new JsTree();

        String serviceName;
        System.out.println("Retrieving tool List .... with values " + tools.size());

        while (keys.hasMoreElements()) {
            ServiceInfoEndpoint key = (ServiceInfoEndpoint) keys.nextElement();

            List serviceType = tools.get(key);

            serviceName = key.getServiceName();
            Node serviceNameTreeNode = new Node(serviceName);

            tree.add(serviceNameTreeNode );

            for (Object tool : serviceType) {
                ToolMetadata tr = (ToolMetadata) tool;
                Attribute attr = new Attribute(key.getServiceAddress() + ":"
                        + key.getPort() + "/" + tr.getUrl());

                Node lastNode = recurseToolTree(tr.getToolName(), serviceNameTreeNode);

                LeafNode child = new LeafNode(getName(tr.getToolName()), attr);
                lastNode.getChildren().add(new LeafNodeObject(child));
            }
        }

        return tree.doSerializeJSON();
    }

    private String getName(String fullName) {
        return fullName.substring(fullName.lastIndexOf(".")+1);
    }

    private Node recurseToolTree(String toolURL, Node thisLevel) {

        int index = toolURL.indexOf(".");

        Node nextLevel=thisLevel;
       
        if (index != -1) {
            String mylevel = toolURL.substring(0, index);
            String rest = toolURL.substring(index + 1); // rest of url

            Node match = getMatchingChild(mylevel, thisLevel);
            if (match==null) {
                nextLevel = new Node(mylevel);
                thisLevel.getChildren().add(nextLevel);
            } else {
                nextLevel = match;
            }
            nextLevel=recurseToolTree(rest, nextLevel);
        }
        return nextLevel;
    }

    public Node getMatchingChild(String childName, Node node) {
        Children children = node.getChildren();

        for (Object child: children) {
            if ((child instanceof Node) && (((Node)child).getData().equals(childName) ))
                    return (Node)child;
        }
        return null;
    }
}
