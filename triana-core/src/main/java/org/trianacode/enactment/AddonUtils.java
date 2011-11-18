package org.trianacode.enactment;

import org.trianacode.TrianaInstance;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.addon.CLIaddon;
import org.trianacode.enactment.addon.ConversionAddon;
import org.trianacode.enactment.addon.ExecutionAddon;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.Tool;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 03/11/2011
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class AddonUtils {
    public static Set<Object> getCLIaddons(TrianaInstance engine) {
        Set<Object> addons = engine.getExtensions(CLIaddon.class);
        System.out.println("Found " + addons.size() + " CLIaddons");
        return addons;
    }

//    public static CLIaddon getService(TrianaInstance engine, OptionValues vals) {
//        for (Object service : getCLIaddons(engine)) {
//            if (service instanceof CLIaddon) {
//                CLIaddon addon = ((CLIaddon) service);
//                if (vals.hasOption(addon.getShortOption())) {
//                    System.out.println("Returning service " + addon.getShortOption());
//                    return (CLIaddon) service;
//                }
//            }
//        }
//        System.out.println("No executionService requested");
//        return null;
//    }

    public static CLIaddon getService(TrianaInstance engine, String longOpt) {
        for (Object service : getCLIaddons(engine)) {
            if (service instanceof CLIaddon) {
                CLIaddon addon = ((CLIaddon) service);
                System.out.println("Comparing " + addon.getLongOption() + ":" + longOpt);
                if (addon.getLongOption().equals(longOpt)) {
                    System.out.println(addon.getLongOption());
                    System.out.println("Returning service " + addon.getLongOption());
                    return (CLIaddon) service;
                }
            }
        }
        return null;
    }

    public static ConversionAddon getConversionAddon(TrianaInstance engine, String longOpt) {
        CLIaddon i = getService(engine, longOpt);
        if (i instanceof ConversionAddon) {
            return (ConversionAddon) i;
        } else {
            System.out.println("Addon is not a ConvertionAddon, or no Addon found");
            return null;
        }
    }

    public static ExecutionAddon getExecutionAddon(TrianaInstance engine, String longOpt) {
        CLIaddon i = getService(engine, longOpt);
        if (i instanceof ExecutionAddon) {
            return (ExecutionAddon) i;
        } else {
            System.out.println("Service found not an ExecutionAddon");
            return null;
        }
    }

    public static Tool makeTool(Class clazz, String name, TrianaProperties properties) throws ProxyInstantiationException, TaskException {
        return makeTool(clazz.getSimpleName(), clazz.getPackage().getName(), name, properties);
    }

    public static ToolImp makeTool(String simpleName, String packageName, String name, TrianaProperties properties) throws ProxyInstantiationException, TaskException {
        ToolImp tool = new ToolImp(properties);
        tool.setProxy(new JavaProxy(simpleName, packageName));
        tool.setToolPackage(packageName);
        tool.setToolName(name);
        System.out.println("New : " + tool.getToolName() + " " + packageName + "." + simpleName);
        return tool;
    }

}
