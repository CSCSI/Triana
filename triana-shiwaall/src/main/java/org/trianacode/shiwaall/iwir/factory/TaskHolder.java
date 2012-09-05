package org.trianacode.shiwaall.iwir.factory;

import org.shiwa.fgi.iwir.AbstractTask;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 24/06/2011
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public interface TaskHolder {

//    public Executable getExecutable();

//    public void setExecutable(Executable executable);

    /**
 * Gets the iWIR task.
 *
 * @return the iWIR task
 */
public AbstractTask getIWIRTask();

    /**
     * Sets the iWIR task.
     *
     * @param abstractTask the new iWIR task
     */
    public void setIWIRTask(AbstractTask abstractTask);

    /**
     * Register iwir task.
     *
     * @param abstractTask the abstract task
     */
    public void registerIWIRTask(AbstractTask abstractTask);
}
