package org.trianacode.taskgraph.interceptor.execution;

import org.trianacode.EngineInit;
import org.trianacode.http.HTTPServices;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 22, 2010
 */

public class ResourceSpawnTest {

    public static void main(String[] args) throws Exception {
        EngineInit.init();
        Tool tool = EngineInit.getToolResolver().getTool(args[0]);
        if (tool == null) {
            System.out.println("Could not load tool:" + args[0] + ". Please check the name is correct.");
            System.exit(0);
        }
        if (!(tool instanceof Task)) {
            System.out.println("Tool is not a Task:" + args[0] + ". I need a Task.");
            System.exit(0);
        }
        HTTPServices.getWorkflowServer().addExecutableTask("spawn", (Task) tool);
    }
}
