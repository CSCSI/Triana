package org.trianacode.shiwa.iwir.test;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 26/09/2011
 * Time: 20:23
 * To change this template use File | Settings | File Templates.
 */

public class TaskTypeToTool {
    private HashMap<String, Class> typeTaskMap = new HashMap<String, Class>();
    private HashMap<Class, String> classTypeMap = new HashMap<Class, String>();

    private static TaskTypeToTool taskTypeToTool = new TaskTypeToTool();

    private static TaskTypeToTool getTaskTypeToTool() {
        return taskTypeToTool;
    }

    public static void addTaskType(String type, Class clazz) {
        getTaskTypeToTool().typeTaskMap.put(type, clazz);
        getTaskTypeToTool().classTypeMap.put(clazz, type);
    }

    public static Class getTaskFromType(String type) {
        return getTaskTypeToTool().typeTaskMap.get(type);
    }

    public static String getTypeFromToolClass(Class clazz) {
        return getTaskTypeToTool().classTypeMap.get(clazz);
    }

}
