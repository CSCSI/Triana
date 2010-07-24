package org.trianacode.http;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import org.thinginitself.http.HttpPeer;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.util.EngineInit;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class TrianaHttpServer {

    private HttpPeer peer;

    public TrianaHttpServer() {
        peer = new HttpPeer();
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        EngineInit.init();
    }

    public void addTask(ResourceSpawn task) {
        peer.addTarget(task);
    }


    /**
     * Adds a blob of data (Java object) and makes it available via restless as a resource
     *
     * @param deploymentURL
     * @param workflowObject
     */
    public void addDataResource(String deploymentURL, Serializable workflowObject) {
        new DataResource(deploymentURL, workflowObject, peer);
    }

    public void start() throws IOException {
        peer.open();
    }

    public void stop() throws IOException {
        peer.close();
    }

    /**
     * Adds a workflow to the resource tree of this Http server
     *
     * @param workflowFile
     * @throws IOException
     * @throws TaskGraphException
     */
    public void addWorkflow(String workflowFile) throws IOException, TaskGraphException {
        File file = new File(workflowFile);
        XMLReader reader = new XMLReader(new FileReader(file));
        ResourceSpawn res = new ResourceSpawn((Task) reader.readComponent());
        addTask(res);
    }

    public static void main(String[] args) throws Exception {
        TrianaHttpServer serverTriana = new TrianaHttpServer();
        serverTriana.addWorkflow(args[0]);
    }
}
