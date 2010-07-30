package org.trianacode.taskgraph.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mil.navy.nrl.discovery.WebBootstrap;
import mil.navy.nrl.discovery.types.ServiceTypes;
import mil.navy.nrl.discovery.web.template.WebDefines;
import org.trianacode.http.RendererRegistry;
import org.trianacode.http.ToolRenderer;
import org.trianacode.http.ToolboxRenderer;
import org.trianacode.http.TrianaHttpServer;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.interceptor.Interceptor;
import org.trianacode.taskgraph.interceptor.InterceptorChain;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.ser.Base64ObjectDeserializer;
import org.trianacode.taskgraph.ser.ObjectDeserializationManager;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.ToolTableImp;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 22, 2010
 */

public class EngineInit {

    private static Map<Class, List<Object>> extensions = new HashMap<Class, List<Object>>();

    public static void init(ToolTable table, Class... extensions) {
 //       try {
  //          new TrianaHttpServer().start();
   //     } catch (IOException e) {
     //       throw new RuntimeException(e);
       // }

        ServiceTypes st = new ServiceTypes();

        st.registerServiceType("http._tcp", "HTTP is cool....");

        WebDefines webDefines = new WebDefines(null,null,null,null,null);

        try {
            WebBootstrap wd = new WebBootstrap("TrianaServer", "triana", "Triana Bonjour Service!",
                    "Published Services", webDefines ,st);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        ProxyFactory.initProxyFactory();
        TaskGraphManager.initTaskGraphManager();
        if (TaskGraphManager.getToolTable() == null) {
            if (table == null) {
                table = new ToolTableImp();
            }
            TaskGraphManager.initToolTable(table);
        }

        Toolboxes.loadToolboxes(TaskGraphManager.getToolTable());

        initObjectDeserializers();
        initExtensions(extensions);
    }

    public static void init() {
        init(null, new Class[0]);
    }

    private static void initExtensions(Class... exten) {
        List ext = new ArrayList<Class>();
        ext.add(Interceptor.class);
        ext.add(ToolRenderer.class);
        ext.add(ToolboxRenderer.class);
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
