package org.trianacode.http;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.thinginitself.http.HttpPeer;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.util.EngineInit;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class HttpServer {


    private HttpPeer peer;

    public HttpServer() {
        peer = new HttpPeer();
    }

    public void addTask(ResourceSpawn task) {
        peer.addTarget(task);
    }

    public void start() throws IOException {
        peer.open();
    }

    public void stop() throws IOException {
        peer.close();
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = new HttpServer();
        File file = new File(args[0]);
        EngineInit.init();
        XMLReader reader = new XMLReader(new FileReader(file));
        ResourceSpawn res = new ResourceSpawn((Task) reader.readComponent());
        server.addTask(res);
        server.start();
    }
}
