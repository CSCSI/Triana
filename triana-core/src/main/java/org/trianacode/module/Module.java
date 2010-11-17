package org.trianacode.module;

import java.io.File;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 16, 2010
 */
public class Module {

    private String path;
    private String name;
    private ModuleClassLoader classLoader;

    public Module(File file) throws Exception {
        this.path = file.getAbsolutePath();
        this.name = file.getName();
        this.classLoader = new ModuleClassLoader();
        this.classLoader.addModule(file.toURI().toURL());
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public ModuleClassLoader getClassLoader() {
        return classLoader;
    }
}
