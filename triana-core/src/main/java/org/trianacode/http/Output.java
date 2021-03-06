package org.trianacode.http;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableData;
import org.trianacode.config.ResourceManagement;
import org.trianacode.config.TrianaProperties;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class Output {

    private static StringResourceRepository repository;
    private static VelocityEngine engine;
    private static boolean defaultRegistered = false;

    static {
        try {
            initVelocityEngine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerTemplate(String name, String path) throws IOException {
        StringResource existing = repository.getStringResource(name);
        if (existing == null) {
            repository.putStringResource(name, getTemplate(path));
        }
    }

    public static void registerTemplate(String name, TrianaProperties properties) throws IOException {
        StringResource existing = repository.getStringResource(name);
        if (existing == null) {
            repository.putStringResource(name, getTemplate(properties.getProperty(name)));
        }
    }

    public static void registerTemplates(TrianaProperties p) throws IOException {
        registerDefaults(p);
        registerTemplate(TrianaProperties.TOOL_CP_HTML_TEMPLATE_PROPERTY, p);
        registerTemplate(TrianaProperties.TOOL_CP_XML_TEMPLATE_PROPERTY, p);
        registerTemplate(TrianaProperties.NOHELP_TEMPLATE_PROPERTY, p);
        registerTemplate(TrianaProperties.TOOL_DESCRIPTION_TEMPLATE_PROPERTY, p);
        registerTemplate(TrianaProperties.TOOL_PARAMETER_WINDOW_TEMPLATE_PROPERTY, p);
        registerTemplate(TrianaProperties.CREATE_TOOL_INSTANCE_PROPERTY, p);
        registerTemplate(TrianaProperties.TOOL_COMPLETED_TEMPLATE_PROPERTY, p);
        registerTemplate(TrianaProperties.TOOL_DESCRIPTION_TEMPLATE_PROPERTY, p);
        registerTemplate(TrianaProperties.TOOL_INSTANCE_PROPERTY, p);
        registerTemplate(TrianaProperties.TOOLBOX_DESCRIPTION_TEMPLATE_PROPERTY, p);
        registerTemplate(TrianaProperties.TOOLBOXES_DESCRIPTION_TEMPLATE_PROPERTY, p);
        registerTemplate(TrianaProperties.TRIANA_TEMPLATE_PROPERTY, p);


    }

    public static synchronized void registerDefaults(TrianaProperties props) throws IOException {
        if (!defaultRegistered) {
            registerTemplate("header.template", props.getProperty(TrianaProperties.HEADER_TEMPLATE_PROPERTY));
            registerTemplate("footer.template", props.getProperty(TrianaProperties.FOOTER_TEMPLATE_PROPERTY));
        }
        defaultRegistered = true;
    }


    private static void initVelocityEngine() throws Exception {
        Properties p = new Properties();
        p.setProperty("resource.loader", "string");
        p.setProperty("string.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        engine = new VelocityEngine();
        engine.init(p);
        repository = StringResourceLoader.getRepository();
    }

    /**
     * Uses the resource management now to find the file rather than using the classpath ...
     *
     * @param path
     * @return
     * @throws IOException
     */
    private static String getTemplate(String path) throws IOException {
        InputStream inStream = ResourceManagement.getInputStreamFor(path, ResourceManagement.Type.TEMPLATE);
        if (inStream != null) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
        return null;
    }


    /**
     * @param parameters
     * @param templateType
     * @return
     */
    public static Streamable output(Map<String, Object> parameters, String templateType, String mime) {
        parameters = initParameters(parameters);
        VelocityContext context = new VelocityContext();
        for (String s : parameters.keySet()) {
            context.put(s, parameters.get(s));
        }
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bout));
            Template template = engine.getTemplate(templateType);
            template.merge(context, writer);
            writer.close();
            return new StreamableData(bout.toByteArray(), mime);
        }
        catch (ResourceNotFoundException rnfe) {
            rnfe.printStackTrace();
        }
        catch (ParseErrorException pee) {
            pee.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Streamable output(Map<String, Object> parameters, String templateType) {
        return output(parameters, templateType, "text/html");

    }

    public static String outputString(Map<String, Object> parameters, String templateType) {
        parameters = initParameters(parameters);

        VelocityContext context = new VelocityContext();
        for (String s : parameters.keySet()) {
            context.put(s, parameters.get(s));
        }
        try {
            StringWriter writer = new StringWriter();
            Template template = engine.getTemplate(templateType);
            template.merge(context, writer);
            writer.close();
            return writer.toString();
        }
        catch (ResourceNotFoundException rnfe) {
            rnfe.printStackTrace();
        }
        catch (ParseErrorException pee) {
            pee.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, Object> initParameters(Map<String, Object> parameters) {
        if (parameters.get("pathController") == null || !(parameters.get("pathController") instanceof PathController)) {
            parameters.put("pathController", PathController.getInstance());
        }
        if (parameters.get("title") == null) {
            parameters.put("title", "Triana");
        }
        return parameters;
    }
}
