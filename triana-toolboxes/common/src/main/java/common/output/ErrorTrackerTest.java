package common.output;

import org.trianacode.annotation.Tool;
import org.trianacode.error.ErrorEvent;
import org.trianacode.error.ErrorListener;
import org.trianacode.error.ErrorTracker;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 10/07/2011
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class ErrorTrackerTest implements ErrorListener, TaskConscious {

    private Task task;

    @org.trianacode.annotation.Process(gather = true)
    public void process(List in) {

        task.getProperties().getEngine().getErrorTracker().addErrorListener(this);

        ErrorTracker.getErrorTracker().broadcastError(
                new ErrorEvent(task,
                        new ProxyInstantiationException("ARGHHHHH"),
                        "An error occured"));
        System.out.println("Doing something stupid");
        int[] array = new int[2];
        System.out.println(array[3]);

    }


    @Override
    public void errorOccurred(ErrorEvent errorEvent) {
        if (Throwable.class.isAssignableFrom(errorEvent.getThrown().getClass())) {
            System.out.println("Throwable : " + errorEvent.getThrown());
        }
    }

    public List<String> listenerInterest() {
        List<String> interest = new ArrayList<String>();
        interest.add(ErrorTracker.ALLERRORS);
        return interest;
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }


}
