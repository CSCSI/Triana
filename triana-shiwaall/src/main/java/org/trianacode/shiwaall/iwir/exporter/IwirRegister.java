package org.trianacode.shiwaall.iwir.exporter;

import org.shiwa.fgi.iwir.AbstractTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 14/04/2011
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */

public class IwirRegister {
    private static IwirRegister register = new IwirRegister();
    Map<String, AbstractTask> abstractTasks;

    private IwirRegister() {
        abstractTasks = new HashMap<String, AbstractTask>();
    }

    public static IwirRegister getIwirRegister() {
        return register;
    }

    public synchronized void listAll() {
        log("+++++++++++++++++++++++++++++++LIST ALL +++++++++++++++++++++++++");
        for (AbstractTask task : abstractTasks.values()) {
            System.out.println("Abstract task : " + task);
        }
    }

    public Collection<AbstractTask> getRegisteredTasks() {
        return abstractTasks.values();
    }

    public synchronized void addTask(AbstractTask task) {
        abstractTasks.put(task.getUniqueId(), task);
    }

    public AbstractTask getTaskFromUniqueID(String id) {
        return abstractTasks.get(id);
    }

    public void clear() {
        abstractTasks.clear();
    }

    private void log(String s) {
//        Log log = Loggers.DEV_LOGGER;
//        log.debug(s);
        System.out.println(s);
    }
}
