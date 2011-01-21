package org.trianacode.pegasus.bonjour;

import org.thinginitself.http.*;
import org.thinginitself.http.target.MemoryTarget;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableString;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Jan 19, 2011
 * Time: 2:47:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class PegasusPostServer extends MemoryTarget {
    private ExecutePegasus pegasusExec;
    String name = "";

    public PegasusPostServer(String name) {
        super(name);
        this.name = name;
    }

    private void run() throws IOException {
        HttpPeer peer = new HttpPeer();
        peer.addTarget(this);
        // If this is not specified, then files will go into a temp folder.
        TargetProperties props = getTargetProperties();
        File out = new File("files");
        out.mkdirs();
        props.setOutputDirectory(out);
        props.setForceWriteToFile(true);
        store.put(new Resource(getPath(), Http.Method.POST));
        peer.open();

        System.out.println("Restless (" + name + ") started server on port:" + peer.getPort());


        new PegasusService("_http._tcp.local.", peer.getPort(), "Pegasus");

    }

    public void onPost(RequestContext context) {
        System.out.println("Got Request !!!!!!!!!!!");

        Streamable stream = context.getRequestEntity();

        System.out.println("Stream : " + stream.toString() +
                " Length : " + stream.getLength() +
                " Mime : " + stream.getMimeType() +
                " Content : \n" + stream.getContent().toString());

        String output=null;
        PegasusWorkflowData data;

        ObjectInputStream r = null;

        try {
            InputStream is = stream.getInputStream();
            System.out.println("got input stream : ");

            r = new ObjectInputStream(is);
            System.out.println("added stream to objectinputstream object");
            data = (PegasusWorkflowData)r.readObject();

            System.out.println(data.toString());

            pegasusExec=new ExecutePegasus(data);
            output=pegasusExec.runIt();

        } catch (IOException e) {
            output="Received object is not a Pegasus workflow object!!!!!  Permission denied\n ";
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
        }

        System.out.println("Output from Pegasus post = " + output);

        context.setResponseEntity(new StreamableString(output, "text/plain"));
    }

    public static void main(String[] args) throws IOException {
        new PegasusPostServer("remotecontrol").run();
    }
}
