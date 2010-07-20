package org.trianacode.http;

import java.util.logging.Logger;

import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.interceptor.Interceptor;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class HttpInterceptor implements Interceptor {

    private static Logger log = Logger.getLogger(HttpInterceptor.class.getName());



    @Override
    public String getName() {
        return "HTTP_EXPOSE_INTERCEPTOR";
    }

    @Override
    public boolean canMediate(Node sendNode, Node receiveNode) {
        return false;
    }

    @Override
    public Object interceptSend(Node sendNode, Node receiveNode, Object data) {
        log.info("ENTER");
        Task task = receiveNode.getTask();
        String guiDesc = (String) task.getParameter(Tool.GUI_BUILDER);
        if(guiDesc == null) {
            return data;
        }

        return data;
    }

    @Override
    public Object interceptReceive(Node sendNode, Node receiveNode, Object data) {
        log.info("ENTER");
        return data;
    }
}
