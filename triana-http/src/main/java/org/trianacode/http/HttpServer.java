package org.trianacode.http;

import java.io.IOException;

import org.thinginitself.http.HttpPeer;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class HttpServer {


    private HttpPeer peer;

    public HttpServer() {
        peer = new HttpPeer();
    }

    public void addTask(TaskTarget task) {
        peer.addTarget(task);
    }

    public void start() throws IOException {
        peer.open();
    }

    public void stop() throws IOException {
        peer.close();
    }
}
