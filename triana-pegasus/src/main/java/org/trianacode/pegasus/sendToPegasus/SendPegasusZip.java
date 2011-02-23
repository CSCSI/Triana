package org.trianacode.pegasus.sendToPegasus;

import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.Resource;
import org.thinginitself.http.Response;
import org.thinginitself.streamable.StreamableFile;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 22/02/2011
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class SendPegasusZip {
    public static String SendFile(String httpAddress, File file) {
        Response ret = null;
        try {
            RequestContext c = new RequestContext(httpAddress);
            c.setResource(new Resource(new StreamableFile(file)));
            HttpPeer peer = new HttpPeer();
            ret = peer.post(c);
        } catch (Exception e) {
            System.out.println("Failed to send zip to TriPeg");
        }
        if(ret != null){
            return ret.toString();
        }else{
            return "Fail";
        }
    }
}
