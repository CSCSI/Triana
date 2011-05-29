package org.trianacode.config;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.util.UrlUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 16, 2010
 */
public class ModuleClassLoader extends URLClassLoader {

    static Log log = Loggers.TOOL_LOGGER;

    private static ModuleClassLoader instance = new ModuleClassLoader();

    public static ModuleClassLoader getInstance() {
        return instance;
    }

    public ModuleClassLoader(ClassLoader classLoader) {
        super(new URL[0], classLoader);
    }

    public ModuleClassLoader() {
        this(ClassLoader.getSystemClassLoader());
    }

    public void addModule(URL module) {
        addModule(module, false);
    }

    private void addModule(URL module, boolean descend) {
        if (UrlUtils.isFile(module)) {

            try {
                File mod = new File(module.toURI());
                if (!mod.exists() || mod.getName().startsWith(".")) {
                    return;
                }

                if (mod.isDirectory()) {
                    File[] files = mod.listFiles();
                    if (files == null) {
                        return;
                    }
                    for (File file : files) {
                        String name = file.getName();
                        if (name.startsWith(".")) {
                            continue;
                        }
                        if (file.isDirectory()) {
                            if (name.equals("classes")) {
                                addPath(file.getAbsolutePath());
                            } else if (name.equals("doc")) {
                                addPath(file.getAbsolutePath());
                                addModule(file.toURI().toURL(), true);
                            } else if (name.equals("src")) {
                                continue;
                            } else if (name.equals("CVS")) {
                                continue;
                            } else if (name.equals("nativ")) {
                                addPath(file.getAbsolutePath());
                                addModule(file.toURI().toURL(), true);
                            } else {
                                if (descend) {
                                    addPath(file.getAbsolutePath());
                                }
                                addModule(file.toURI().toURL(), descend);
                            }
                        } else {
                            if (name.endsWith(".jar")) {
                                addPath(file.getAbsolutePath());
                            }
                        }
                    }
                } else {
                    if (mod.getName().endsWith(".jar")) {
                        addPath(mod.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            URL[] all = getURLs();
            boolean add = true;
            for (URL url : all) {
                if (url.equals(module)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                addURL(module);
            }
        }

        log.debug(" MODULE CLASS LOADER:" + module + " CLASSPATH:" + getClassPath());
    }


    private void addPath(String path) {
        File f = new File(path);
        if (f.exists()) {
            log.debug("parsing " + f.getAbsoluteFile());
            try {
                String s = f.toURI().toURL().toString();
                if (f.isDirectory() && !s.endsWith("/")) {
                    s += "/";
                }
                URL u = new URL(s);
                URL[] all = getURLs();
                boolean add = true;
                for (URL url : all) {
                    if (url.equals(u)) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    addURL(u);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }


    public List<String> getClassPathList() {
        List<String> list = new ArrayList<String>();
        URL[] paths = getURLs();
        for (int i = 0; i < paths.length; i++) {
            URL path = paths[i];
            String s = path.getFile();
            File f = new File(s);
            if (f.exists()) {
                list.add(f.getAbsolutePath());
            }
        }
        return list;
    }


    public String getClassPath() {
        StringBuilder classPath = new StringBuilder();
        URL[] paths = getURLs();
        for (int i = 0; i < paths.length; i++) {
            URL path = paths[i];
            String s = path.getFile();
            File f = new File(s);
            if (f.exists()) {
                classPath.append(f.getAbsolutePath());
                if (i < paths.length - 1) {
                    classPath.append(System.getProperty("path.separator"));
                }
            }
        }
        return classPath.toString();
    }

    protected String findLibrary(String name) {
        String lib = System.mapLibraryName(name);
        URL url = this.getResource(lib);
        if (url == null) {
            return null;
        }
        try {
            return url.toURI().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
/*

    private String getNativeDir() {
        String binlib = null;
        String os = Locations.os();
        String arch = Locations.arch();
        if (os.equals("osx")) {
            binlib = Toolbox.OSX_DIR;
        } else if (os.equals("windows")) {
            if (arch.equals("x86")) {
                binlib = Toolbox.WIN_32_DIR;
            } else {
                binlib = Toolbox.WIN_64_DIR;
            }
        } else {
            if (arch.equals("i386")) {
                binlib = Toolbox.NUX_32_DIR;
            } else {
                binlib = Toolbox.NUX_64_DIR;
            }
        }
        return binlib;
    }
*/
}
