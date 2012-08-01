package org.trianacode.shiwa.executionServices;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 31/07/2012
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public class TaskTypeToolDescriptor {

    private String tasktype;
    private Class clazz;
    private Properties properties;

    public TaskTypeToolDescriptor(String tasktype, Class clazz, Properties properties){
        this.tasktype = tasktype;
        this.clazz = clazz;
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Class getToolClass() {
        return clazz;
    }

    public void setToolClass(Class clazz) {
        this.clazz = clazz;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }
}
