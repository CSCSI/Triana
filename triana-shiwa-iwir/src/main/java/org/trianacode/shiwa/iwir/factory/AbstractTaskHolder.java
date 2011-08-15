package org.trianacode.shiwa.iwir.factory;

import org.shiwa.fgi.iwir.AbstractTask;
import org.trianacode.shiwa.iwir.creation.IwirRegister;
import org.trianacode.shiwa.iwir.execute.Executable;
import org.trianacode.taskgraph.Unit;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 24/06/2011
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTaskHolder extends Unit implements TaskHolder {
    Executable executable;
    AbstractTask abstractTask;

    public Executable getExecutable() {
        return executable;
    }

    public void setExecutable(Executable executable) {
        this.executable = executable;
    }

    public AbstractTask getIWIRTask() {
        return abstractTask;
    }

    public void setIWIRTask(AbstractTask abstractTask) {
        this.abstractTask = abstractTask;
    }

    public void init() {
        setParameterPanelClass(BasicIWIRPanel.class.getCanonicalName());

    }

    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

    public void registerIWIRTask(AbstractTask abstractTask) {
        IwirRegister.getIwirRegister().addTask(abstractTask);
    }

}

