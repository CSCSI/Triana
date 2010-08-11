package org.trianacode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.trianacode.discovery.ResolverRegistry;
import org.trianacode.discovery.ToolMetadataResolver;
import org.trianacode.http.HTTPServices;
import org.trianacode.http.RendererRegistry;
import org.trianacode.http.ToolRenderer;
import org.trianacode.http.ToolboxRenderer;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.interceptor.Interceptor;
import org.trianacode.taskgraph.interceptor.InterceptorChain;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.ser.Base64ObjectDeserializer;
import org.trianacode.taskgraph.ser.ObjectDeserializationManager;
import org.trianacode.taskgraph.tool.ToolResolver;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.ToolTableImpl;
import org.trianacode.taskgraph.util.ExtensionFinder;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 22, 2010
 */

public class EngineInit {

    private static HTTPServices httpServices;
    private static ToolResolver resolver = new ToolResolver();

    private static Map<Class, List<Object>> extensions = new HashMap<Class, List<Object>>();

    public static void init(ToolTable table, Class... extensions) throws Exception {
        ProxyFactory.initProxyFactory();
        TaskGraphManager.initTaskGraphManager();
        if (TaskGraphManager.getToolTable() == null) {
            if (table == null) {
                table = new ToolTableImpl(resolver);
            }
            TaskGraphManager.initToolTable(table);
        }
        initObjectDeserializers();
        initExtensions(extensions);

        httpServices = new HTTPServices();
        httpServices.startServices(resolver);
        resolver.addToolListener(HTTPServices.getWorkflowServer());
        resolver.resolve();

    }

    public static ToolResolver getToolResolver() {
        return resolver;
    }

    public static void init() throws Exception {
        init(null, new Class[0]);
    }

    private static void initExtensions(Class... exten) {
        List ext = new ArrayList<Class>();
        ext.add(Interceptor.class);
        ext.add(ToolRenderer.class);
        ext.add(ToolboxRenderer.class);
        ext.add(ToolMetadataResolver.class);
        for (Class aClass : exten) {
            if (!ext.contains(aClass)) {
                ext.add(aClass);
            }
        }
        extensions = ExtensionFinder.services(ext);
        Set<Class> keys = extensions.keySet();
        for (Class key : keys) {
            if (key.equals(Interceptor.class)) {
                List<Object> exts = extensions.get(key);
                for (Object o : exts) {
                    Interceptor e = (Interceptor) o;
                    InterceptorChain.register(e);
                }
            } else if (key.equals(ToolRenderer.class)) {
                List<Object> exts = extensions.get(key);
                for (Object o : exts) {
                    ToolRenderer e = (ToolRenderer) o;
                    RendererRegistry.registerToolRenderer(e);
                }
            } else if (key.equals(ToolboxRenderer.class)) {
                List<Object> exts = extensions.get(key);
                for (Object o : exts) {
                    ToolboxRenderer e = (ToolboxRenderer) o;
                    RendererRegistry.registerToolboxRenderer(e);
                }
            } else if (key.equals(ToolMetadataResolver.class)) {
                List<Object> exts = extensions.get(key);
                for (Object o : exts) {
                    ToolMetadataResolver e = (ToolMetadataResolver) o;
                    ResolverRegistry.registerResolver(e);
                }
            }
        }
    }

    private static void initObjectDeserializers() {
        ObjectDeserializationManager.registerObjectDeserializer(Base64ObjectDeserializer.BASE64_OBJECT_DESERIALIZER,
                new Base64ObjectDeserializer());
    }

    public static Map<Class, List<Object>> getExtensions() {
        return Collections.unmodifiableMap(extensions);
    }

    public static List<Object> getExtensions(Class cls) {
        List<Object> exts = extensions.get(cls);
        if (exts != null) {
            return Collections.unmodifiableList(exts);
        }
        return new ArrayList<Object>();
    }

}
