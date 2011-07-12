package common.output;

import org.trianacode.annotation.Tool;
import org.trianacode.messenger.MessageEvent;
import org.trianacode.messenger.MessageListener;
import org.trianacode.messenger.MessageSender;
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
public class MessageBusTest implements MessageListener, TaskConscious {

    private Task task;

    @org.trianacode.annotation.Process(gather = true)
    public void process(List in) {

        task.getProperties().getEngine().getMessageBus().addMessageListener(this);

        Test test = new Test();
        test.sendMessage(
                new MessageEvent(
                        new ProxyInstantiationException("ARGHHHHH"),
                        "An error occured"));
    }


    @Override
    public void messagePerformed(MessageEvent messageEvent) {
        if (Throwable.class.isAssignableFrom(messageEvent.getType().getClass())) {
            System.out.println("Throwable : " + messageEvent.getMessage());
        }
    }

    public List<Throwable> listenerInterest() {
        return new ArrayList<Throwable>();
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }


    class Test extends MessageSender {

    }

}
