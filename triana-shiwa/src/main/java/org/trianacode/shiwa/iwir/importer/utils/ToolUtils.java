package org.trianacode.shiwa.iwir.importer.utils;

import org.trianacode.config.TrianaProperties;
import org.trianacode.shiwa.iwir.factory.TaskHolder;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.Tool;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 24/10/2011
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
public class ToolUtils {
    public static Tool makeTool(Class clazz, String name, TrianaProperties properties) throws ProxyInstantiationException, TaskException {
        Tool tool = new ToolImp(properties);
        tool.setProxy(new JavaProxy(clazz.getSimpleName(), clazz.getPackage().getName()));
        tool.setToolPackage(clazz.getPackage().getName());
        tool.setToolName(name);
        return tool;
    }

    public static Tool initTool(TaskHolder taskHolder, TrianaProperties properties) {
        ToolImp tool = null;
        try {
            tool = new ToolImp(properties);

            tool.setProxy(new JavaProxy(taskHolder, taskHolder.getClass().getSimpleName(), taskHolder.getClass().getPackage().getName()));

            tool.setToolName(taskHolder.getIWIRTask().getName());
            tool.setToolPackage(taskHolder.getClass().getPackage().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tool;
    }
}
