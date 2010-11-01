package org.trianacode.http;

import org.thinginitself.http.MimeType;
import org.thinginitself.http.Resource;
import org.thinginitself.streamable.Streamable;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 1, 2010
 */
public class RendererResource extends Resource {

    private Renderer renderer;
    private String template;
    private String mime;

    public RendererResource(String path, Renderer renderer, String template, String mime) {
        super(path);
        this.renderer = renderer;
        this.template = template;
        this.mime = mime;
    }

    public RendererResource(String path, Renderer renderer, String template) {
        this(path, renderer, template, "text/html");
    }

    public Streamable getStreamable(List<MimeType> mimeTypes) {
        return renderer.render(template, mime);
    }
}
