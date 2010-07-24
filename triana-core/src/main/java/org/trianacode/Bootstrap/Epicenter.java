package org.trianacode.Bootstrap;

import org.trianacode.http.TrianaHttpServer;

/**
 * Created by IntelliJ IDEA.
 * User: scmijt
 * Date: Jul 24, 2010
 * Time: 2:06:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Epicenter {
    static TrianaHttpServer httpServer;

    public Epicenter() {
         httpServer = new TrianaHttpServer();
    }

    public static TrianaHttpServer getHttpServer() {
        return httpServer;
    }
}
