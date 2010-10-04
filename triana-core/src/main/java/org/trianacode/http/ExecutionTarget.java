package org.trianacode.http;

import org.thinginitself.http.*;
import org.thinginitself.streamable.Streamable;
import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoHandler;

import java.io.IOException;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 4, 2010
 */
public class ExecutionTarget implements Target {


    @Override
    public Path getPath() {
        return new Path("run");
    }

    @Override
    public Resource getResource(RequestContext requestContext) throws RequestProcessException {
        return null;
    }

    @Override
    public void onGet(RequestContext requestContext) throws RequestProcessException {
    }

    @Override
    public void onPut(RequestContext requestContext) throws RequestProcessException {
    }

    @Override
    public void onPost(RequestContext requestContext) throws RequestProcessException {
        IoHandler handler = new IoHandler();
        Streamable s = requestContext.getRequestEntity();
        IoConfiguration config = null;
        try {
            config = handler.deserialize(s.getInputStream());
        } catch (IOException e) {
            throw new RequestProcessException("Error reading configuration file", 400);
        }
        String tool = config.getToolName();


    }

    @Override
    public void onDelete(RequestContext requestContext) throws RequestProcessException {
    }

    @Override
    public void onOptions(RequestContext requestContext) throws RequestProcessException {
    }
}
