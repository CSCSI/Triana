package org.trianacode.shiwaall.iwir.exporter;

import org.shiwa.fgi.iwir.AbstractTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 14/04/2011
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */

public class IwirRegister {
    
    /** The register. */
    private static IwirRegister register = new IwirRegister();
    
    /** The abstract tasks. */
    Map<String, AbstractTask> abstractTasks;

    /**
     * Instantiates a new iwir register.
     */
    private IwirRegister() {
        abstractTasks = new HashMap<String, AbstractTask>();
    }

    /**
     * Gets the iwir register.
     *
     * @return the iwir register
     */
    public static IwirRegister getIwirRegister() {
        return register;
    }

    /**
     * List all.
     */
    public synchronized void listAll() {
        log("+++++++++++++++++++++++++++++++LIST ALL +++++++++++++++++++++++++");
        for (AbstractTask task : abstractTasks.values()) {
            System.out.println("Abstract task : " + task);
        }
    }

    /**
     * Gets the registered tasks.
     *
     * @return the registered tasks
     */
    public Collection<AbstractTask> getRegisteredTasks() {
        return abstractTasks.values();
    }

    /**
     * Adds the task.
     *
     * @param task the task
     */
    public synchronized void addTask(AbstractTask task) {
        abstractTasks.put(task.getUniqueId(), task);
    }

    /**
     * Gets the task from unique id.
     *
     * @param id the id
     * @return the task from unique id
     */
    public AbstractTask getTaskFromUniqueID(String id) {
        return abstractTasks.get(id);
    }

    /**
     * Clear.
     */
    public void clear() {
        abstractTasks.clear();
    }

    /**
     * Log.
     *
     * @param s the s
     */
    private void log(String s) {
//        Log log = Loggers.DEV_LOGGER;
//        log.debug(s);
        System.out.println(s);
    }
}
