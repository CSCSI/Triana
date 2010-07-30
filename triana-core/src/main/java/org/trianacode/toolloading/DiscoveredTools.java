package org.trianacode.toolloading;

import mil.navy.nrl.discovery.api.ServiceInfo;
import mil.navy.nrl.discovery.api.ServiceInfoEndpoint;
import mil.navy.nrl.discovery.json.*;
import mil.navy.nrl.discovery.tools.DNSServiceNames;
import mil.navy.nrl.discovery.web.resources.ServiceResource;

import java.util.*;

/**
 * List of discovered tools from all supported bonjour protocols
 *
 * User: scmijt
 * Date: Jul 30, 2010
 * Time: 11:44:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class DiscoveredTools {

    private Hashtable<String,List> serviceTypes;

    public DiscoveredTools() {
        serviceTypes= new Hashtable<String,List>();
    }


    public void addService(ServiceResource service) {

        System.out.println("Adding service : " + service.getInfo().getServiceName());

        final String serviceType= DNSServiceNames.getServiceTypeFrom(service.getInfo().getQualifiedServiceType());

        List list = null;

        synchronized (serviceTypes)
        {
            list = serviceTypes.get(serviceType);
            if (list == null) { // create a list for this service type
                list = Collections.synchronizedList(new LinkedList<ServiceResource>());
                System.out.println("Adding service type " + serviceType);
                serviceTypes.put(serviceType, list);
            }
            System.out.println("Adding service to list ");
            if (!list.contains(service))
                list.add(service); // add the service
        }
    }


    protected void removeService(ServiceResource service) {

        final String serviceType=DNSServiceNames.getServiceTypeFrom(service.getInfo().getQualifiedServiceType());

        List list = null;
        ArrayList listCopy = null;
        synchronized (serviceTypes) {
            list = (List) serviceTypes.get(serviceType);

            if (list != null) {
                list.remove(service);
            }
        }
    }

    protected void removeService(ServiceInfo serviceinfo) {

        final String serviceType=DNSServiceNames.getServiceTypeFrom(serviceinfo.getQualifiedServiceType());

        List list = null;
        ArrayList listCopy = null;
        synchronized (serviceTypes) {
            list = (List) serviceTypes.get(serviceType);

            if (list != null) {
                listCopy = new ArrayList(list);
            }
        }
        ServiceResource toDelete=null;
        if (listCopy != null) {
            for (final Iterator iterator = listCopy.iterator(); iterator.hasNext();) {
                ServiceResource service = ((ServiceResource) iterator.next());

                if (service.getInfo().getServiceName().equals(serviceinfo.getServiceName())
                        && (service.getInfo().getQualifiedServiceType().equals(serviceinfo.getQualifiedServiceType())))
                    toDelete=service;
            }
            if (toDelete!=null) {
                list.remove(toDelete);
                toDelete.getWebPeer().removeTarget(toDelete); // remove the tool from Web peer also
            }
        }

    }

    /**
     * Gets HTML list of all the services, arranged in type order
     * @return
     */

    public String getHTMLList() {
        StringBuffer serverList = new StringBuffer();

        Enumeration keys = serviceTypes.keys();

        System.out.println("Retrieving Service List .... with values " + serviceTypes.size());

        while (keys.hasMoreElements()){
            String key = (String)keys.nextElement();
            System.out.println("Retrieving Key is: " + key);

            List serviceType = serviceTypes.get(key);

            serverList.append("<b>" + key + "</b>");
            serverList.append("<ol>");

            for (Object service: serviceType) {
                ServiceResource serviceResource = (ServiceResource)service;
                serverList.append(serviceResource.getInfo().getServiceName() +
                 " -  <a href=" + serviceResource.getServiceURL() +
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
        Enumeration keys = serviceTypes.keys();
        JsTree tree = new JsTree();

        System.out.println("Retrieving Service List .... with values " + serviceTypes.size());

        while (keys.hasMoreElements()){
            String key = (String)keys.nextElement();

            List serviceType = serviceTypes.get(key);

            Node serviceTypeTreeNode = new Node(key);

            tree.add(serviceTypeTreeNode);

            for (Object service: serviceType) {
                ServiceResource serviceResource = (ServiceResource)service;
                Attribute attr = new Attribute(serviceResource.getRelativeServiceURL());
                ServiceInfoEndpoint info=serviceResource.getInfo();
                Node serviceDetails = new Node(info.getServiceName() + "/" + info.getServiceAddress() + "/" + info.getPort());
                //serviceDetails.setAttr(attr);

                serviceTypeTreeNode.getChildren().add(serviceDetails);

                LeafNode child = new LeafNode("Go To Service Definition ...", attr);

                serviceDetails.getChildren().add(new LeafNodeObject(child));
            }
        }

        return tree.doSerializeJSON();
    }

}
