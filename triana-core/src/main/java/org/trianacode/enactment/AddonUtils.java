package org.trianacode.enactment;

import org.trianacode.TrianaInstance;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.addon.CLIaddon;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.Tool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 03/11/2011
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class AddonUtils {

    public static final String TASKGRAPH_FORMAT = "taskgraph";
    public static final String IWIR_FORMAT = "iwir";
    public static final String DAX_FORMAT = "dax";

    public static String getWorkflowType(File file) {
        String workflowType = null;
        if (file != null && file.exists()) {
            try {
                System.out.println(file.getAbsolutePath());

                DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
                Document doc = docBuilder.parse(file);
                Element i = doc.getDocumentElement();
                NamedNodeMap l = i.getAttributes();
                for (int m = 0; m < l.getLength(); m++) {
                    org.w3c.dom.Node o = l.item(m);
                    System.out.println(o.toString());
                }
                String rootName = i.getNodeName();
                System.out.println("XML root is : " + rootName);
                if (rootName.toLowerCase().equals(AddonUtils.IWIR_FORMAT)) {
                    return AddonUtils.IWIR_FORMAT;
                } else if (rootName.toLowerCase().equals("tool")) {
                    return AddonUtils.TASKGRAPH_FORMAT;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return workflowType;
    }

    public static Set<Object> getCLIaddons(TrianaInstance engine) {
        Set<Object> addons = engine.getExtensions(CLIaddon.class);
        System.out.println("Found " + addons.size() + " CLIaddons");
        return addons;
    }

    public static CLIaddon getService(TrianaInstance engine, String longOpt, Class clazz) {
        Set<Object> extensions = getCLIaddons(engine);
        for (Object service : extensions) {
            if (clazz.isAssignableFrom(service.getClass())) {
                CLIaddon addon = ((CLIaddon) service);
//                System.out.println("Comparing " + addon.getLongOption() + ":" + longOpt);
                if (addon.getLongOption().equals(longOpt)) {
                    System.out.println("Returning service " + addon.getLongOption());
                    return (CLIaddon) service;
                }
            } else {
                System.out.println(service.toString() + " is not a " + clazz.getCanonicalName());
            }
        }
        return null;
    }

    public static Tool makeTool(Class clazz, String name, TrianaProperties properties) throws ProxyInstantiationException, TaskException {
        return makeTool(clazz.getSimpleName(), clazz.getPackage().getName(), name, properties);
    }

    public static ToolImp makeTool(String simpleName, String packageName, String name, TrianaProperties properties) throws ProxyInstantiationException, TaskException {
        ToolImp tool = new ToolImp(properties);
        tool.setProxy(new JavaProxy(simpleName, packageName));
        tool.setToolPackage(packageName);
        tool.setToolName(name);
//        System.out.println("New : " + tool.getToolName() + " " + packageName + "." + simpleName);
        return tool;
    }

}
