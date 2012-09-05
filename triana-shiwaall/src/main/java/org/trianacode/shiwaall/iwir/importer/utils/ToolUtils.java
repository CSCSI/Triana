package org.trianacode.shiwaall.iwir.importer.utils;

import org.trianacode.config.TrianaProperties;
import org.trianacode.shiwaall.iwir.factory.TaskHolder;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.Tool;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 24/10/2011
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
public class ToolUtils {

    /**
     * Inits the tool.
     *
     * @param taskHolder the task holder
     * @param properties the properties
     * @return the tool
     */
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
