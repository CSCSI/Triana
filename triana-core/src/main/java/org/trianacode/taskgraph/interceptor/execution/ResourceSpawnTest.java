package org.trianacode.taskgraph.interceptor.execution;

import org.trianacode.TrianaInstance;
import org.trianacode.http.HTTPServices;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 22, 2010
 */

public class ResourceSpawnTest {

    public static void main(String[] args) throws Exception {

        TrianaInstance engine = new TrianaInstance();
        try {
            engine.init();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Tool tool = engine.getToolResolver().getTool(args[0]);

        if (tool == null) {
            System.out.println("Could not load tool:" + args[0] + ". Please check the name is correct.");
            System.exit(0);
        }
        if (!(tool instanceof Task)) {
            System.out.println("Tool is not a Task:" + args[0] + ". I need a Task.");
            System.exit(0);
        }
        engine.getHttpServices().getWorkflowServer().addExecutableTask("spawn", (Task) tool);
    }
}
