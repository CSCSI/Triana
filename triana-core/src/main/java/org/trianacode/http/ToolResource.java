package org.trianacode.http;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.thinginitself.http.Http;
import org.thinginitself.http.Path;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.RequestProcessException;
import org.thinginitself.http.Resource;
import org.thinginitself.http.util.MimeHandler;
import org.thinginitself.http.util.StreamableListDir;
import org.thinginitself.streamable.StreamableData;
import org.thinginitself.streamable.StreamableFile;
import org.thinginitself.streamable.StreamableStream;
import org.thinginitself.streamable.StreamableString;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 11, 2010
 */

public class ToolResource extends Resource {

    public static final String HELP = "help";
    public static final String DEFINITION = "definition";
    public static final String CLASSPATH = "classpath";
    public static final String EXEC = "exec";

    private Tool tool;
    private String helpFile = null;
    private NoHelp noHelp = null;
    private Classpath cp = null;


    public ToolResource(Path path, Tool tool) {
        super(path, Http.Method.GET);
        this.tool = tool;
        noHelp = new NoHelp(tool);
        helpFile = tool.getToolName() + ".html";
        System.out.println("ToolResource.ToolResource HELP FILE:" + helpFile);
        List<String> libs = tool.getToolBox().getLibPaths();
        cp = new Classpath(libs);
    }


    @Override
    public void onGet(RequestContext requestContext) throws RequestProcessException {
        String path = requestContext.getRequestPath();
        String me = getPath().toString();
        String res = path.substring(path.indexOf(me) + me.length(),
                path.length());
        if (res.startsWith("/")) {
            res = res.substring(1, res.length());
        }
        if (path.endsWith(HELP)) {
            if (helpFile != null) {
                InputStream in = tool.getToolBox().getClassLoader().getResourceAsStream(helpFile);
                if (in != null) {
                    StreamableStream ss = new StreamableStream(in, "text/html");
                    requestContext.setResponseEntity(ss);
                } else {
                    requestContext.setResponseEntity(noHelp.getStreamable());
                }
            } else {
                requestContext.setResponseEntity(noHelp.getStreamable());
            }
        } else if (path.endsWith(DEFINITION)) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                XMLWriter writer = new XMLWriter(new BufferedWriter(new OutputStreamWriter(bout)));
                writer.writeComponent(tool);
                writer.close();
                requestContext.setResponseEntity(new StreamableData(bout.toByteArray()));
            } catch (IOException e) {
                requestContext.setResponseCode(500);
            }
        } else if (path.endsWith(CLASSPATH)) {
            requestContext.setResponseEntity(cp.getStreamable());
        } else if (path.endsWith(getPath().getLast())) {
            requestContext.setResponseEntity(new StreamableString("Tool", "text/plain")); // TODO
        } else if (res.startsWith(CLASSPATH)) {
            String sub = res.substring(CLASSPATH.length(), res.length());
            File f = tool.getToolBox().getLibFile(sub);
            if (f.exists() && f.length() > 0) {
                if (f.isDirectory()) {
                    requestContext.setResponseEntity(new StreamableListDir(f).getStreamable());
                } else {
                    requestContext.setResponseEntity(new StreamableFile(f, MimeHandler.getMime(f.getName())));
                }
            } else {
                requestContext.setResponseCode(404);
            }


        } else {
            System.out.println("ToolResource.onGet REQUESTED RESOURCE:" + res);
            InputStream in = tool.getToolBox().getClassLoader().getResourceAsStream(res);
            if (in != null) {
                StreamableStream ss = new StreamableStream(in, MimeHandler.getMime(res));
                requestContext.setResponseEntity(ss);
            } else {
                requestContext.setResponseCode(404);
            }

        }
    }

    @Override
    public void onPut(RequestContext requestContext) throws RequestProcessException {

    }

    @Override
    public void onPost(RequestContext requestContext) throws RequestProcessException {
    }

    @Override
    public void onDelete(RequestContext requestContext) throws RequestProcessException {
    }

    @Override
    public void onOptions(RequestContext requestContext) throws RequestProcessException {
    }
}
