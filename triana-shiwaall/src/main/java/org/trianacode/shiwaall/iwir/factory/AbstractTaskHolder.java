package org.trianacode.shiwaall.iwir.factory;

import org.shiwa.fgi.iwir.AbstractTask;
import org.trianacode.shiwaall.iwir.exporter.IwirRegister;
import org.trianacode.taskgraph.Unit;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 24/06/2011
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTaskHolder extends Unit implements TaskHolder {
//    Executable executable;
    /** The abstract task. */
AbstractTask abstractTask;

//    public Executable getExecutable() {
//        return executable;
//    }

//    public void setExecutable(Executable executable) {
//        this.executable = executable;
//        this.setParameter(Executable.TASKTYPE, executable.getTaskType());
//    }

    /* (non-Javadoc)
 * @see org.trianacode.shiwaall.iwir.factory.TaskHolder#getIWIRTask()
 */
public AbstractTask getIWIRTask() {
        return abstractTask;
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.iwir.factory.TaskHolder#setIWIRTask(org.shiwa.fgi.iwir.AbstractTask)
     */
    public void setIWIRTask(AbstractTask abstractTask) {
        this.abstractTask = abstractTask;
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#init()
     */
    public void init() {
        setParameterPanelClass(BasicIWIRPanel.class.getCanonicalName());
        setParameterPanelInstantiate(ON_TASK_INSTANTATION);
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#getInputTypes()
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#getOutputTypes()
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.iwir.factory.TaskHolder#registerIWIRTask(org.shiwa.fgi.iwir.AbstractTask)
     */
    public void registerIWIRTask(AbstractTask abstractTask) {
        IwirRegister.getIwirRegister().addTask(abstractTask);
    }

}

