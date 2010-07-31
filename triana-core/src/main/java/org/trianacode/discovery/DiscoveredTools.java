package org.trianacode.discovery;

import mil.navy.nrl.discovery.api.DiscoveredServicesInterface;
import mil.navy.nrl.discovery.api.ServiceInfo;
import mil.navy.nrl.discovery.api.ServiceInfoEndpoint;
import mil.navy.nrl.discovery.json.*;
import mil.navy.nrl.discovery.tools.DNSServiceNames;
import mil.navy.nrl.discovery.web.resources.ServiceResource;
import org.trianacode.discovery.toolinfo.ToolMetadata;

import java.util.*;

/**
 * List of discovered tools from all supported bonjour protocols.  Tools are stored
 * as lists of tools according to their service type.
 *
 * User: scmijt
 * Date: Jul 30, 2010
 * Time: 11:44:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class DiscoveredTools implements DiscoveredServicesInterface {

    private Hashtable<ServiceInfoEndpoint,List> tools;

    private List<ServiceInfoEndpoint> protocols;

    public DiscoveredTools() {
        tools = new Hashtable<ServiceInfoEndpoint ,List>();
        protocols=new ArrayList<ServiceInfoEndpoint>();
    }

    public void addService(ServiceInfoEndpoint serviceInfoEndpoint) {
        if(!protocols.contains(serviceInfoEndpoint))
            protocols.add(serviceInfoEndpoint);
    }

    public void removeService(ServiceInfo serviceInfo) {

        List listCopy = new ArrayList(protocols);

        ServiceInfoEndpoint toDelete=null;

        if (listCopy != null) {
            for (final Iterator iterator = listCopy.iterator(); iterator.hasNext();) {
                ServiceInfoEndpoint service = ((ServiceInfoEndpoint) iterator.next());

                if (service.getServiceName().equals(serviceInfo.getServiceName())
                        && (service.getQualifiedServiceType().equals(serviceInfo.getQualifiedServiceType())))
                    toDelete=service;
            }
            if (toDelete!=null) {
                protocols.remove(toDelete);
            }
        }
    }

    public void addTool(ToolMetadata tool, ServiceInfoEndpoint serviceType) {

        System.out.println("Adding tool : " + tool.getDisplayName());

        List list = null;

        synchronized (tools)
        {
            list = tools.get(serviceType);
            if (list == null) { // create a list for this service type
                list = Collections.synchronizedList(new LinkedList<ServiceResource>());
                System.out.println("Adding service type " + serviceType);
                tools.put(serviceType, list);
            }
            System.out.println("Adding tool to list ");
            if (!list.contains(tool))
                list.add(tool); // add the service
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
        ToolMetadata toDelete=null;
        if (listCopy != null) {
            for (final Iterator iterator = listCopy.iterator(); iterator.hasNext();) {
                ToolMetadata tool = ((ToolMetadata) iterator.next());

                if (tool.getToolName().equals(toolMetadata.getToolName())
                        && (tool.getUrl().equals(toolMetadata.getUrl())))
                    toDelete=tool;
            }
            if (toDelete!=null) {
                list.remove(toDelete);
            }
        }
    }

    /**
     * Gets HTML list of all the services, arranged in type order
     * @return
     */

    public String getHTMLList() {
        StringBuffer serverList = new StringBuffer();

        Enumeration keys = tools.keys();

        System.out.println("Retrieving tool List .... with values " + tools.size());

        while (keys.hasMoreElements()){
            ServiceInfoEndpoint key = (ServiceInfoEndpoint)keys.nextElement();
            System.out.println("Retrieving Key is: " + key);

            List serviceType = tools.get(key);

            serverList.append("<b>" + key + "</b>");
            serverList.append("<ol>");

            for (Object tool: serviceType) {
                ToolMetadata t= (ToolMetadata)tool;
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
     * @return
     */
    public String getJsTreeList() {
        Enumeration keys = tools.keys();
        JsTree tree = new JsTree();

        String type;
        System.out.println("Retrieving tool List .... with values " + tools.size());

        while (keys.hasMoreElements()){
            ServiceInfoEndpoint key = (ServiceInfoEndpoint)keys.nextElement();

            List serviceType = tools.get(key);

            type = DNSServiceNames.getServiceTypeFrom(key.getQualifiedServiceType());
            Node serviceTypeTreeNode = new Node(type);

            tree.add(serviceTypeTreeNode);

            for (Object tool: serviceType) {
                ToolMetadata tr= (ToolMetadata)tool;
                Attribute attr = new Attribute(key.getServiceAddress() + ":"
                        + key.getPort() + "/" +  tr.getUrl());

                Node lastNode = recurseToolTree(tr.getUrl(),serviceTypeTreeNode);

                LeafNode child = new LeafNode(tr.getDisplayName(), attr);
                lastNode.getChildren().add(new LeafNodeObject(child));
            }
        }

        return tree.doSerializeJSON();
    }


    private Node recurseToolTree(String toolURL, Node thisLevel) {

        int index = toolURL.indexOf("/");

        if (index!=-1) {
            String mylevel= toolURL.substring(0,index);
            String rest= toolURL.substring(index+1); // rest of url

            Node nextLevel = new Node(mylevel);
            thisLevel.getChildren().add(nextLevel);
            recurseToolTree(rest,nextLevel);
        }
        return thisLevel;
    }
}
