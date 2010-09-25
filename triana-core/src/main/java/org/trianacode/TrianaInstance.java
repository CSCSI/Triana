package org.trianacode;

import org.trianacode.config.ArgumentParser;
import org.trianacode.config.PropertyLoader;
import org.trianacode.config.TrianaProperties;
import org.trianacode.discovery.DiscoverTools;
import org.trianacode.discovery.ResolverRegistry;
import org.trianacode.discovery.ToolMetadataResolver;
import org.trianacode.discovery.protocols.tdp.imp.trianatools.ToolResolver;
import org.trianacode.http.HTTPServices;
import org.trianacode.http.RendererRegistry;
import org.trianacode.http.ToolRenderer;
import org.trianacode.http.ToolboxRenderer;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.databus.DataBus;
import org.trianacode.taskgraph.databus.DataBusInterface;
import org.trianacode.taskgraph.interceptor.Interceptor;
import org.trianacode.taskgraph.interceptor.InterceptorChain;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.ser.Base64ObjectDeserializer;
import org.trianacode.taskgraph.ser.ObjectDeserializationManager;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.ToolTableImpl;
import org.trianacode.taskgraph.util.ExtensionFinder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This class represents an instance of Triana and allows arguments to be passed to it and properties for
 * defining the various parameters that Triana can accept.
 * <p/>
 * All references are now NON STATIC and propagated these through the many classes that expected
 * static references.  Also configured this so that it can accept
 * command line arguments (need to be defined) and it now creates all of the necessary classes required by
 * an instance e.g. properties.
 *
 * @author Andrew Harrison, rewrite by Ian T
 * @version 1.0.0 Jul 22, 2010
 */

public class TrianaInstance {

    String args[] = null;

    private HTTPServices httpServices;
    private ToolResolver toolResolver;

    private Map<Class, List<Object>> extensions = new HashMap<Class, List<Object>>();
    TrianaProperties props;
    PropertyLoader propertyLoader;
    DiscoverTools discoveryTools;

    ToolTable toolTable;

    ArgumentParser parser;


    public TrianaInstance(String[] args, Class... extensions) throws IOException {
        this(null, args, extensions);
    }


    /**
     * Creates am instance of Triana.
     *
     * @param progress   a notifier to notify upon the progress of the initialization
     * @param args       command line arguments
     * @param extensions list of extensions to load
     * @throws Exception
     */
    public TrianaInstance(TrianaInstanceProgressListener progress, String[] args, Class... extensions) throws IOException {

        if (progress != null) {
            progress.setProgressSteps(4);
            progress.showCurrentProgress("Initializing Engine");
        }
        this.args = args;

        if (args != null) {
            parser = new ArgumentParser(args);
        }


        propertyLoader = new PropertyLoader(this, null);
        props = propertyLoader.getProperties();

        if (progress != null) {
            progress.showCurrentProgress("Searching for local tools");
        }
        toolResolver = new ToolResolver(props);
        toolTable = new ToolTableImpl(toolResolver);

        httpServices = new HTTPServices();
        toolResolver.addToolListener(httpServices.getWorkflowServer());
        httpServices.startServices(toolResolver);
        discoveryTools = new DiscoverTools(toolResolver, httpServices.getHttpEngine(), props);

        toolResolver.resolve();
        if (progress != null) {
            progress.showCurrentProgress("Started Discovery and HTTP Services");
        }
        ProxyFactory.initProxyFactory();
        TaskGraphManager.initTaskGraphManager();
        initObjectDeserializers();
        initExtensions(extensions);

        new ShutdownHook().createHook();
        if (progress != null) {
            progress.showCurrentProgress("Triana Initialization complete");
        }
    }


    public ToolResolver getToolResolver() {
        return toolResolver;
    }

    public HTTPServices getHttpServices() {
        return httpServices;
    }

    public TrianaProperties getProps() {
        return props;
    }

    public DiscoverTools getDiscoveryTools() {
        return discoveryTools;
    }

    public ToolTable getToolTable() {
        return toolTable;
    }

    private void initExtensions(Class... exten) {

        List ext = new ArrayList<Class>();
        ext.add(Interceptor.class);
        ext.add(ToolRenderer.class);
        ext.add(ToolboxRenderer.class);
        ext.add(ToolMetadataResolver.class);
        ext.add(DataBusInterface.class);
        if (exten != null) {
            for (Class aClass : exten) {
                if (!ext.contains(aClass)) {
                    ext.add(aClass);
                }
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
            } else if (key.equals(DataBusInterface.class)) {
                List<Object> exts = extensions.get(key);
                for (Object o : exts) {
                    DataBusInterface e = (DataBusInterface) o;
                    DataBus.registerDataBus(e);
                }
            }
        }
    }

    private static void initObjectDeserializers() {
        ObjectDeserializationManager.registerObjectDeserializer(Base64ObjectDeserializer.BASE64_OBJECT_DESERIALIZER,
                new Base64ObjectDeserializer());
    }

    public Map<Class, List<Object>> getExtensions() {
        return Collections.unmodifiableMap(extensions);
    }

    public List<Object> getExtensions(Class cls) {
        List<Object> exts = extensions.get(cls);
        if (exts != null) {
            return Collections.unmodifiableList(exts);
        }
        return new ArrayList<Object>();
    }


    private class ShutdownHook extends Thread {

        private void add() {
            try {
                Method shutdownHook = java.lang.Runtime.class
                        .getMethod("addShutdownHook", new Class[]{java.lang.Thread.class});
                shutdownHook.invoke(Runtime.getRuntime(), new Object[]{this});
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void createHook() {
            add();
        }

        public void run() {
            System.out.println("TrianaInstance$ShutdownHook.run ENTER");
            try {
                toolResolver.shutdown();
                discoveryTools.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("TrianaInstance$ShutdownHook.run EXIT");
        }
    }

}
