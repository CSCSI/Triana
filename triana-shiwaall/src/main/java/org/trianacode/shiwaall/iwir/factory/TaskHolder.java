package org.trianacode.shiwaall.iwir.factory;

import org.shiwa.fgi.iwir.AbstractTask;

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

    public AbstractTask getIWIRTask();

    public void setIWIRTask(AbstractTask abstractTask);

    public void registerIWIRTask(AbstractTask abstractTask);
}
