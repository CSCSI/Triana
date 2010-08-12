package org.trianacode.http;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.thinginitself.http.Http;
import org.thinginitself.http.Path;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.RequestProcessException;
import org.thinginitself.http.Resource;
import org.thinginitself.streamable.StreamableData;
import org.thinginitself.streamable.StreamableFile;
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
    public static final String EXECUTABLES = "executables";
    public static final String EXEC = "exec";

    private Tool tool;
    private StreamableFile helpFile = null;
    private NoHelp noHelp = null;
    private Classpath cp = null;


    public ToolResource(Path path, Tool tool) {
        super(path, Http.Method.GET);
        this.tool = tool;
        noHelp = new NoHelp(tool);
        String help = tool.getHelpFile();
        if (help != null) {
            File f = new File(help);
            if (f.exists() && f.length() > 0) {
                helpFile = new StreamableFile(f, "text/html");
            }
        }
        cp = new Classpath(new ArrayList<String>());
    }


    @Override
    public void onGet(RequestContext requestContext) throws RequestProcessException {
        String path = requestContext.getRequestPath();
        if (path.endsWith(HELP)) {
            if (helpFile != null) {
                requestContext.setResponseEntity(helpFile);
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
        } else if (path.endsWith(EXECUTABLES)) {
            requestContext.setResponseEntity(cp.getStreamable());
        } else if (path.endsWith(getPath().getLast())) {
            requestContext.setResponseEntity(new StreamableString("Tool", "text/plain")); // ummm
        } else {
            requestContext.setResponseCode(404);
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
