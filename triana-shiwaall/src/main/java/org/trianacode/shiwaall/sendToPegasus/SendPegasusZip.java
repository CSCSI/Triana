package org.trianacode.shiwaall.sendToPegasus;

import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.Resource;
import org.thinginitself.http.Response;
import org.thinginitself.streamable.StreamableFile;

import java.io.File;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 22/02/2011
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class SendPegasusZip {

    /**
     * Send file.
     *
     * @param httpAddress the http address
     * @param file the file
     * @return the response
     */
    public static Response sendFile(String httpAddress, File file) {
        try {
            RequestContext c = new RequestContext(httpAddress);

            c.setResource(new Resource(new StreamableFile(file)));
            HttpPeer peer = new HttpPeer();
            Response ret = peer.post(c);
            System.out.println("Received reply :" + ret.toString());
            return ret;
        } catch (Exception e) {
            System.out.println("Sending failure");
            return null;
        }
    }
}
