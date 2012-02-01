package org.trianacode.shiwa.iwir.importer.utils;

import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.shiwa.test.InOut;
import org.trianacode.taskgraph.tool.Tool;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 26/09/2011
 * Time: 20:23
 * To change this template use File | Settings | File Templates.
 */

public class TaskTypeToTool {
    private HashMap<String, Class> typeTaskMap = new HashMap<String, Class>();
    private HashMap<Class, String> classTypeMap = new HashMap<Class, String>();

    private static TaskTypeToTool singleInstance = null;

    private TaskTypeToTool() {
        addTaskType(InOut.class, "InOut");
    }

    private static TaskTypeToTool getTaskTypeToTool() {
        if (singleInstance == null) {
            singleInstance = new TaskTypeToTool();
        }

        return singleInstance;
    }

    public static void addTaskType(String type, Class clazz) {
        getTaskTypeToTool().addTaskType(clazz, type);
    }

    private void addTaskType(Class clazz, String type) {
        typeTaskMap.put(type, clazz);
        classTypeMap.put(clazz, type);
    }

    public static Tool getToolFromType(String type, String taskName, TrianaProperties properties) {
        Tool tool = null;

        Class clazz = getTaskTypeToTool().typeTaskMap.get(type);
        if (clazz != null) {
            try {
                tool = AddonUtils.makeTool(
                        clazz, taskName, properties);
            } catch (Exception ignored) {
            }
        } else {
            if (type.contains(".")) {
                String unitName = type.substring(type.lastIndexOf(".") + 1);
                String packageName = type.substring(0, type.lastIndexOf("."));
                System.out.println(packageName + unitName);
                try {
                    tool = AddonUtils.makeTool(unitName, packageName, taskName, properties);
                } catch (Exception ignored) {
                }
            }
        }

        if (tool == null) {

            TaskTypeRepo taskTypeRepo = new TaskTypeRepo();
            try {
                taskTypeRepo.getConcreteDescriptor(type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                tool = AddonUtils.makeTool(InOut.class, taskName, properties);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tool;
    }

    public static String getTypeFromToolClass(Class clazz) {
        return getTaskTypeToTool().classTypeMap.get(clazz);
    }

}
