package org.trianacode.http;

import org.thinginitself.http.MimeType;
import org.thinginitself.http.Resource;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableData;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 1, 2010
 */
public class ToolWriterResource extends Resource {

    private Tool tool;

    public ToolWriterResource(String path, Tool tool) {
        super(path);
        this.tool = tool;
    }

    public Streamable getStreamable(List<MimeType> mimeTypes) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            XMLWriter writer = new XMLWriter(new BufferedWriter(new OutputStreamWriter(bout)));
            writer.writeComponent(tool);
            writer.close();
            return new StreamableData(bout.toByteArray(), "text/xml");
        } catch (IOException e) {
            e.printStackTrace();
            // todo
            return null;
        }

    }
}
