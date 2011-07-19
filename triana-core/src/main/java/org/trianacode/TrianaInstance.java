package org.trianacode;

import org.apache.commons.logging.Log;
import org.trianacode.config.Locations;
import org.trianacode.config.ModuleClassLoader;
import org.trianacode.config.PropertyLoader;
import org.trianacode.config.TrianaProperties;
import org.trianacode.config.cl.ArgumentParser;
import org.trianacode.config.cl.ArgumentParsingException;
import org.trianacode.config.cl.TrianaOptions;
import org.trianacode.discovery.ResolverRegistry;
import org.trianacode.discovery.ToolMetadataResolver;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.http.HTTPServices;
import org.trianacode.messenger.ErrorTracker;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.databus.DataBus;
import org.trianacode.taskgraph.databus.DataBusInterface;
import org.trianacode.taskgraph.interceptor.Interceptor;
import org.trianacode.taskgraph.interceptor.InterceptorChain;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.ser.Base64ObjectDeserializer;
import org.trianacode.taskgraph.ser.ObjectDeserializationManager;
import org.trianacode.taskgraph.tool.*;
import org.trianacode.taskgraph.util.ExtensionFinder;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class represents an instance of Triana and allows arguments to be passed to it and properties for
 * defining the various parameters that Triana can accept.
 * <p/>
 * All references are now NON STATIC and propagated these through the many classes that expected
 * static references.  Also configured this so that it can accept
 * command line arguments (need to be defined) and it now creates all of the necessary classes required by
 * an instance e.g. properties.
 *
 * @author Andrew Harrison, rewrite by Ian T, rewrite and Andrew H
 * @version 1.0.0 Jul 22, 2010
 */

public class TrianaInstance {

    private static Log log = Loggers.CONFIG_LOGGER;


    private ThreadPoolExecutor executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 5,
            Runtime.getRuntime().availableProcessors() * 20,
            10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(Runtime.getRuntime().availableProcessors() * 20));

    private HTTPServices httpServices;
    private ToolResolver toolResolver;
    private ErrorTracker errorTracker;

    private Map<Class, Set<Object>> extensions = new HashMap<Class, Set<Object>>();
    private TrianaProperties props;
    private PropertyLoader propertyLoader;
    //private DiscoverTools discoveryTools;

    private ToolTable toolTable;

    private ArgumentParser parser;
    private List<Class> extensionClasses = new ArrayList<Class>();
    private List<String> extraToolboxes = new ArrayList<String>();
    private List<String> modulePaths = new ArrayList<String>();

    private boolean runServer = false;
    private boolean reresolve = false;

    private boolean suppressDefaultToolboxes = false;

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
        errorTracker = ErrorTracker.getMessageBus();
        executorService.allowCoreThreadTimeOut(true);
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
        List<String> modules = TrianaOptions.getOptionValues(parser, TrianaOptions.EXTRA_MODULES_OPTION);
        if (modules != null) {
            this.modulePaths.addAll(modules);
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

        //  This is to give the option to only load toolboxes given at the command line. No defaults to be selected.
        if (TrianaOptions.hasOption(parser, TrianaOptions.SUPPRESS_DEFAULT_TOOLBOXES)) {
            System.out.println("Default toolboxes suppressed");
            props.put(TrianaProperties.TOOLBOX_SEARCH_PATH_PROPERTY, "");
            suppressDefaultToolboxes = true;
        }
    }

    public void init(TrianaInstanceProgressListener progress, boolean resolve) throws IOException {
        if (progress != null) {
            progress.setProgressSteps(4);
            progress.showCurrentProgress("Initializing Engine");
        }

        //load modules first - this just adds stuff to the class path
        initModules(modulePaths);
        toolResolver = new ToolResolver(props, suppressDefaultToolboxes);
        toolTable = new ToolTableImpl(toolResolver);


        ProxyFactory.initProxyFactory();
        TaskGraphManager.initTaskGraphManager(props);
        TaskGraphManager.initToolTable(toolTable);
        initObjectDeserializers();


        httpServices = new HTTPServices();
        if (runServer) {
            toolResolver.addToolListener(httpServices.getWorkflowServer());
            httpServices.startServices(toolResolver);
            //discoveryTools = new DiscoverTools(toolResolver, httpServices.getHttpEngine(), props);
        }
        if (resolve) {
            if (progress != null) {
                progress.showCurrentProgress("Searching for local tools");
            }
            try {
                System.out.println("Extra toolboxes : " + extraToolboxes.toString());
                toolResolver.resolve(reresolve, extraToolboxes);
            } catch (Throwable throwable) {
                System.out.println("Error in toolResolver.resolve()" + throwable.getCause().toString());
            }
        }

        initExtensions(extensionClasses);

        if (progress != null && runServer) {
            progress.showCurrentProgress("Started Discovery and HTTP Services");
        }

        new ShutdownHook().createHook();
        if (progress != null) {
            progress.showCurrentProgress("Triana Initialization complete");
        }
    }

    public void init(boolean resolve) throws IOException {
        init(null, resolve);
    }

    public void init() throws IOException {
        init(null, true);
    }

    public void resolve() {
        toolResolver.resolve(reresolve, extraToolboxes);
    }


    public ToolResolver getToolResolver() {
        return toolResolver;
    }

    public HTTPServices getHttpServices() {
        return httpServices;
    }

    public TrianaProperties getProperties() {
        return props;
    }

//    public DiscoverTools getDiscoveryTools() {
//        return discoveryTools;
//    }

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

    public void addModulePath(String path) {
        if (!modulePaths.contains(path)) {
            modulePaths.add(path);
        }
    }

    private void initModules(List<String> modulePaths) {
        ClassLoaders.addClassLoader(ModuleClassLoader.getInstance());
        String moduleRoot = props.getProperty(TrianaProperties.MODULE_SEARCH_PATH_PROPERTY);
        if (moduleRoot != null) {
            File f = new File(moduleRoot);
            if (f.exists() && f.isDirectory()) {
                File[] files = f.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() && !file.getName().startsWith(".") && !file.getName().equals("CVS");
                    }
                });
                for (File file : files) {
                    loadModule(file.getAbsolutePath());
                }
            }
        }
        for (String modulePath : modulePaths) {
            loadModule(modulePath);
        }

    }

    public void loadModule(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            try {
                ModuleClassLoader.getInstance().addModule(file.toURI().toURL());
            } catch (Exception e) {
                log.error(e);
            }
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
        ext.add(ToolMetadataResolver.class);
        ext.add(DataBusInterface.class);
        ext.add(ToolboxLoader.class);
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
                Set<Object> exts = extensions.get(key);
                for (Object o : exts) {
                    Interceptor e = (Interceptor) o;
                    InterceptorChain.register(e);
                }
            } else if (key.equals(ToolMetadataResolver.class)) {
                Set<Object> exts = extensions.get(key);
                for (Object o : exts) {
                    ToolMetadataResolver e = (ToolMetadataResolver) o;
                    ResolverRegistry.registerResolver(e);
                }
            } else if (key.equals(DataBusInterface.class)) {
                Set<Object> exts = extensions.get(key);
                for (Object o : exts) {
                    DataBusInterface e = (DataBusInterface) o;
                    DataBus.registerDataBus(e);
                }
            } else if (key.equals(ToolboxLoader.class)) {
                Set<Object> exts = extensions.get(key);
                for (Object o : exts) {
                    ToolboxLoader e = (ToolboxLoader) o;
                    ToolboxLoaderRegistry.registerLoader(e);
                }
            }
        }
    }

    private static void initObjectDeserializers() {
        ObjectDeserializationManager.registerObjectDeserializer(Base64ObjectDeserializer.BASE64_OBJECT_DESERIALIZER,
                new Base64ObjectDeserializer());
    }

    public Map<Class, Set<Object>> getExtensions() {
        return Collections.unmodifiableMap(extensions);
    }

    public Set<Object> getExtensions(Class cls) {
        Set<Object> exts = extensions.get(cls);
        if (exts != null) {
            return Collections.unmodifiableSet(exts);
        }
        return new HashSet<Object>();
    }

    public void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

    public ErrorTracker getMessageBus() {
        return errorTracker;
    }

    private class ShutdownHook extends Thread {

        private void add() {
            try {
                Method shutdownHook = java.lang.Runtime.class
                        .getMethod("addShutdownHook", new Class[]{java.lang.Thread.class});
                shutdownHook.invoke(Runtime.getRuntime(), new Object[]{this});
            } catch (Exception e) {
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
//                if (discoveryTools != null) {
//                    discoveryTools.shutdown();
//                }
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
