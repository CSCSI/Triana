package org.trianacode.http;

import org.thinginitself.http.*;
import org.thinginitself.http.util.MimeHandler;
import org.thinginitself.http.util.StreamableFileHandler;
import org.thinginitself.streamable.StreamableData;
import org.thinginitself.streamable.StreamableStream;
import org.thinginitself.streamable.StreamableString;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is all still messy - but just about works.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Aug 11, 2010
 */

public class ToolResource extends Resource {

    public static final String HELP = "help.html";
    public static final String DEFINITION = "definition.xml";
    public static final String CLASSPATH = "classpath";
    public static final String EXEC = "exec";

    private Tool tool;
    private String helpFile = null;
    private NoHelp noHelp = null;
    private ToolboxClasspath cp = null;


    public ToolResource(Path path, Tool tool) {
        super(path, Http.Method.GET);
        this.tool = tool;
        noHelp = new NoHelp(tool);
        helpFile = tool.getToolName() + ".html";
        List<String> libs = tool.getToolBox().getLibPaths();
        List<String> ammended = new ArrayList<String>();
        for (String lib : libs) {
            if (lib.startsWith("/")) {
                lib = "classpath" + lib;
            } else {
                lib = "classpath/" + lib;
            }
            ammended.add(lib);
        }
        cp = new ToolboxClasspath(tool.getToolBox().getName(), tool.getQualifiedToolName(), ammended);
    }


    @Override
    public void onGet(RequestContext requestContext) throws RequestProcessException {
        String path = requestContext.getRequestPath();
        System.out.println("ToolResource.onGet request path:" + path);

        String me = getPath().toString();
        String res = path.substring(path.indexOf(me) + me.length(),
                path.length());
        if (res.startsWith("/")) {
            res = res.substring(1, res.length());
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
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
                requestContext.setResponseEntity(new StreamableData(bout.toByteArray(), "text/xml"));
            } catch (IOException e) {
                requestContext.setResponseCode(500);
            }
        } else if (path.endsWith(CLASSPATH + ".html")) {
            requestContext.setResponseEntity(cp.getStreamable("text/html"));
        } else if (path.endsWith(CLASSPATH + ".xml")) {
            requestContext.setResponseEntity(cp.getStreamable("text/xml"));
        } else if (path.endsWith(getPath().getLast())) {
            requestContext.setResponseEntity(new StreamableString("Tool", "text/plain")); // TODO
        } else if (res.startsWith(CLASSPATH)) {
            String sub = res.substring(CLASSPATH.length(), res.length());
            File f = tool.getToolBox().getFile(sub);
            if (f != null && f.exists() && f.length() > 0) {
                requestContext.setResponseEntity(
                        new StreamableFileHandler(f, tool.getToolBox().getVisibleRoots()).getStreamable());
            } else {
                requestContext.setResponseCode(404);
            }
        } else {
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
        requestContext.setResponseCode(405);
    }

    @Override
    public void onPost(RequestContext requestContext) throws RequestProcessException {
        requestContext.setResponseCode(405);
    }

    @Override
    public void onDelete(RequestContext requestContext) throws RequestProcessException {
        requestContext.setResponseCode(405);
    }

}
