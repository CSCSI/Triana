package org.trianacode.shiwaall.executionServices;

import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 31/07/2012
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public class TaskTypeToolDescriptor {

    /** The tasktype. */
    private String tasktype;
    
    /** The clazz. */
    private Class clazz;
    
    /** The properties. */
    private Properties properties;

    /**
     * Instantiates a new task type tool descriptor.
     *
     * @param tasktype the tasktype
     * @param clazz the clazz
     * @param properties the properties
     */
    public TaskTypeToolDescriptor(String tasktype, Class clazz, Properties properties){
        this.tasktype = tasktype;
        this.clazz = clazz;
        this.properties = properties;
    }

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the properties.
     *
     * @param properties the new properties
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Gets the tool class.
     *
     * @return the tool class
     */
    public Class getToolClass() {
        return clazz;
    }

    /**
     * Sets the tool class.
     *
     * @param clazz the new tool class
     */
    public void setToolClass(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * Gets the tasktype.
     *
     * @return the tasktype
     */
    public String getTasktype() {
        return tasktype;
    }

    /**
     * Sets the tasktype.
     *
     * @param tasktype the new tasktype
     */
    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }
}
