package org.trianacode;

import org.trianacode.config.Locations;
import org.trianacode.config.PropertyLoader;
import org.trianacode.config.TrianaProperties;
import org.trianacode.config.cl.ArgumentParser;
import org.trianacode.config.cl.ArgumentParsingException;
import org.trianacode.config.cl.TrianaOptions;
import org.trianacode.discovery.DiscoverTools;
import org.trianacode.discovery.ResolverRegistry;
import org.trianacode.discovery.ToolMetadataResolver;
import org.trianacode.discovery.protocols.tdp.imp.trianatools.ToolResolver;
import org.trianacode.enactment.logging.Loggers;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 20);
    private HTTPServices httpServices;
    private ToolResolver toolResolver;

    private Map<Class, List<Object>> extensions = new HashMap<Class, List<Object>>();
    private TrianaProperties props;
    private PropertyLoader propertyLoader;
    private DiscoverTools discoveryTools;

    private ToolTable toolTable;

    private ArgumentParser parser;
    private List<Class> extensionClasses = new ArrayList<Class>();
    private List<String> extraToolboxes = new ArrayList<String>();
    private boolean runServer = false;
    private boolean reresolve = false;


    public TrianaInstance() throws IOException {
        this(null);
    }


    /**
     * Creates am instance of Triana.
     *
     * @param args command line arguments
     * @throws Exception
     */
    public TrianaInstance(String[] args) throws IOException {
        if (args == null) {
            args = new String[0];
        }
        this.parser = new ArgumentParser(args);
        try {
            parser.parse();
        } catch (ArgumentParsingException e) {
            throw new RuntimeException("cannot read arguments");
        }
        this.runServer = TrianaOptions.hasOption(parser, TrianaOptions.SERVER_OPTION);
        this.reresolve = TrianaOptions.hasOption(parser, TrianaOptions.RESOLVE_THREAD_OPTION);
        List<String> toolboxes = TrianaOptions.getOptionValues(parser, TrianaOptions.EXTRA_TOOLBOXES_OPTION);
        if (toolboxes != null) {
            this.extraToolboxes.addAll(toolboxes);
        }
        String existing = Locations.getDefaultConfigFile();
        File f = new File(existing);
        if (!f.exists()) {
            propertyLoader = new PropertyLoader(this, null);
        } else {
            Properties p = new Properties();
            FileInputStream fin = new FileInputStream(f);
            p.load(fin);
            fin.close();
            propertyLoader = new PropertyLoader(this, p);
        }


        props = propertyLoader.getProperties();
    }

    public void init(TrianaInstanceProgressListener progress) throws IOException {
        if (progress != null) {
            progress.setProgressSteps(4);
            progress.showCurrentProgress("Initializing Engine");
        }


        if (progress != null) {
            progress.showCurrentProgress("Searching for local tools");
        }

        toolResolver = new ToolResolver(props);
        toolTable = new ToolTableImpl(toolResolver);

        ProxyFactory.initProxyFactory();
        TaskGraphManager.initTaskGraphManager();
        TaskGraphManager.initToolTable(toolTable);
        initObjectDeserializers();
        initExtensions(extensionClasses);

        httpServices = new HTTPServices();
        if (runServer) {
            toolResolver.addToolListener(httpServices.getWorkflowServer());
            httpServices.startServices(toolResolver);
            discoveryTools = new DiscoverTools(toolResolver, httpServices.getHttpEngine(), props);
        }
        toolResolver.resolve(reresolve, extraToolboxes);
        if (progress != null && runServer) {
            progress.showCurrentProgress("Started Discovery and HTTP Services");
        }
        new ShutdownHook().createHook();
        if (progress != null) {
            progress.showCurrentProgress("Triana Initialization complete");
        }
    }

    public void init() throws IOException {
        init(null);
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

    public void addExtensionClass(Class cls) {
        this.extensionClasses.add(cls);
    }

    public void addExtensionClasses(Class... clss) {
        for (Class cls : clss) {
            this.extensionClasses.add(cls);
        }
    }

    public void addExtraToolbox(String toolbox) {
        extraToolboxes.add(toolbox);
    }

    public void addExtraToolboxes(String... toolboxes) {
        for (String toolbox : toolboxes) {
            extraToolboxes.add(toolbox);
        }
    }

    public boolean isRunServer() {
        return runServer;
    }

    public void setRunServer(boolean runServer) {
        this.runServer = runServer;
    }

    public boolean isReresolve() {
        return reresolve;
    }

    public void setReresolve(boolean reresolve) {
        this.reresolve = reresolve;
    }

    private void initExtensions(List<Class> exten) {

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

    public void execute(Runnable runnable) {
        executorService.execute(runnable);
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
            Loggers.LOGGER.info("TrianaInstance$ShutdownHook.run ENTER");
            try {
                toolResolver.shutdown();
                if (discoveryTools != null) {
                    discoveryTools.shutdown();
                }
                executorService.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Loggers.LOGGER.info("TrianaInstance$ShutdownHook.run EXIT");
        }
    }

}
