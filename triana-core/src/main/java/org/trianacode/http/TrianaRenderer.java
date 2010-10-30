package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.TrianaInstance;
import org.trianacode.config.TrianaProperties;
import org.trianacode.velocity.Output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class TrianaRenderer implements Renderer {

    public static String TRIANA_TEMPLATE = "triana.template";

    private String path;
    private String templatePath;


    public void init(TrianaInstance instance, String path) {

        this.path = path;
        templatePath = instance.getProps().getProperty(TrianaProperties.TRIANA_TEMPLATE_PROPERTY);

        try {
            Output.registerDefaults(instance.getProps());
            Output.registerTemplate(TRIANA_TEMPLATE, templatePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getRenderTypes() {
        return new String[]{TRIANA_TEMPLATE};
    }

    public Streamable render(String type) {
        Map<String, Object> properties = new HashMap<String, Object>();

        return Output.output(properties, type);
    }
}
