package org.trianacode.taskgraph.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        ProxyFactory.initProxyFactory();
        TaskGraphManager.initTaskGraphManager();
        if (TaskGraphManager.getToolTable() == null) {
            if (table == null) {
                table = new ToolTableImp();
            }
            TaskGraphManager.initToolTable(table);
            Toolboxes.loadToolboxes(TaskGraphManager.getToolTable());
        }
        initObjectDeserializers();
        initExtensions(extensions);
    }

    public static void init() {
        init(null, new Class[0]);
    }

    private static void initExtensions(Class... exten) {
        List ext = new ArrayList<Class>();
        for (Class aClass : exten) {
            ext.add(aClass);
        }
        ext.add(Interceptor.class);
        extensions = ExtensionFinder.services(ext);
        Set<Class> keys = extensions.keySet();
        for (Class key : keys) {
            if (key.equals(Interceptor.class)) {
                List<Object> exts = extensions.get(key);
                for (Object o : exts) {
                    Interceptor e = (Interceptor) o;
                    InterceptorChain.register(e);
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
