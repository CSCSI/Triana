/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.taskgraph.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.creators.type.ClassHierarchy;
import org.trianacode.taskgraph.util.FileUtils;

/**
 * Tool format handler. This handles the following cases:
 * <p/>
 * For xml tools: 1. package/package/Tool.xml 2. package/package/xml/Tool.xml
 * <p/>
 * For java tools: 1. package/package/classes/package/package/Tool.class 2. package/package/target/classes/package/package/Tool.class
 * <p/>
 * If the tool is in a jar file, it handles the above paths inside the jar file.
 * <p/>
 * This also handles jars that are excutable, i.e. a bog standard jar inwhich the classes are top level entries:
 * <p/>
 * package/package/Tool.class
 * <p/>
 * A tool that is jarred up is considered not modifiable because it is considered a distribution.
 * <p/>
 * A Java tool, even if the source is available in the tool, is also not modifiable.
 * <p/>
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class FileToolFormatHandler implements ToolFormatHandler {

    static Logger log = Logger.getLogger("org.trianacode.taskgraph.tool.FileToolFormatHandler");

    private Map<String, ToolUrl> tools = new HashMap<String, ToolUrl>();
    // lists of tools that share the same file (i.e. a jar)
    private Map<String, Set<String>> shared = new HashMap<String, Set<String>>();

    public ToolStatus add(Tool tool) throws ToolException {

        String def = tool.getDefinitionPath();
        if (def == null) {
            return new ToolStatus(tool, ToolStatus.Status.NULL_DEFINITION_PATH);
        } else {
            File file = new File(def);
            if (!file.exists() || file.length() == 0) {
                throw new ToolException("no file found for tool " + tool.getToolName());
            }
            // does not add tools if they are files and the file has not been modified.
            if (!isModified(tool)) {
                return new ToolStatus(tool, ToolStatus.Status.NOT_MODIFIED);
            }
            boolean isJar = false;
            if (file.getName().endsWith(".jar")) {
                try {
                    JarFile jf = new JarFile(file);
                } catch (IOException e) {
                    throw new ToolException(
                            "The file has an extension of '.jar' but the content does not comply with the zip specification.");
                }
                isJar = true;
            }
            if (isJar) {
                URL[] arr = initJar(tool, file);
                if (arr == null) {
                    throw new ToolException("unknown jar format " + tool.getToolName());
                }
                File f = new File(tool.getDefinitionPath());
                tools.put(createId(tool), new ToolUrl(tool, arr[0], arr[1], arr[2], f.lastModified()));
            } else {
                URL[] arr = initFile(tool, file);
                if (arr == null) {
                    throw new ToolException("unknown directory structure " + tool.getToolName());
                }
                File f = new File(tool.getDefinitionPath());
                tools.put(createId(tool), new ToolUrl(tool, arr[0], arr[1], arr[2], f.lastModified()));
            }
        }
        return new ToolStatus(tool, ToolStatus.Status.OK);
    }

    public List<ToolStatus> add(File file, String toolbox) throws ToolException {
        log.fine("FileToolFormatHandler.add file:" + file.getAbsolutePath());
        List<ToolStatus> ret = new ArrayList<ToolStatus>();
        try {
            if (file.getName().endsWith(".xml")) {
                log.fine("file is XML:" + file.getAbsolutePath());
                Tool tool = readXMLStream(new FileInputStream(file), file.getAbsolutePath(), toolbox);
                if (tool != null && tools.get(createId(tool)) == null) {
                    ret.add(initTool(tool, file));
                }
            } else if (file.getName().endsWith(".jar")) {
                log.fine("file is JAR:" + file.getAbsolutePath());
                JarHelper helper = new JarHelper(file);
                List<String> potentials = helper.listEntries();
                for (String potential : potentials) {
                    log.fine("FileToolFormatHandler.add potential:" + potential);
                    if (potential.endsWith(".xml")) {
                        InputStream in = helper.getStream(potential);
                        Tool tool = readXMLStream(in, file.getAbsolutePath(), toolbox);
                        if (tool != null && tools.get(createId(tool)) == null) {
                            ret.add(initJarredTool(tool, file));
                        }
                    } else if (potential.endsWith(".class")) {

                        URL url = createJarURL(file, potential);
                        log.fine("FileToolFormatHandler.add jar url:" + url.toString());
                        if (url != null) {
                            ClassHierarchy ch = TypesMap.isType(url.toString(), Unit.class.getName());
                            if (ch != null) {
                                Tool tool = read(ch.getName(), toolbox, ch.getFile());
                                if (tool != null && tools.get(createId(tool)) == null) {
                                    ret.add(initJarredTool(tool, file));
                                }
                            }
                        }

                    }
                }
            } else if (file.getName().endsWith(".class")) {
                log.fine("file is Java:" + file.getAbsolutePath());
                ClassHierarchy ch = TypesMap.isType(file.getAbsolutePath(), Unit.class.getName());
                log.fine("FileToolFormatHandler.add class hierarchy:" + ch);
                if (ch != null) {
                    Tool tool = read(ch.getName(), toolbox, ch.getFile());
                    if (tool != null) {
                        ret.add(initTool(tool, file));
                    }
                }
            }
        } catch (IOException e) {
            throw new ToolException(e.getMessage());
        }
        return ret;
    }

    public void delete(Tool tool) {
        String id = createId(tool);
        ToolUrl tu = tools.get(id);
        if (tu != null) {
            if (tu.isJar()) {
                // we do not delete jars. There could be other tools in it.
                remove(tool);
            } else {
                File f = new File(tu.getTool().getDefinitionPath());
                if (f != null && f.exists()) {
                    log.fine("got file from URL:" + f.getAbsolutePath());
                    f.delete();
                    tools.remove(id);
                }
            }
        }
    }

    public void remove(Tool tool) {
        new Exception().printStackTrace();
        String id = createId(tool);
        ToolUrl tu = tools.get(id);
        if (tu != null) {
            tools.remove(id);
        }
    }

    public void removeDefinitionPath(String path) {
        shared.remove(path);
    }

    public void clear() {
        tools.clear();
    }

    public String[] getToolNames() {
        return tools.keySet().toArray(new String[tools.size()]);
    }


    public Tool read(String className, String toolbox, String classFile) {
        try {
            ToolImp tool = new ToolImp();
            tool.setDefinitionType(Tool.DEFINITION_JAVA_CLASS);
            tool.setToolName(getClassName(className));
            tool.setToolPackage(getPackageName(className));
            tool.setDefinitionPath(classFile);
            tool.setToolBox(toolbox);

            tool.setProxy(new JavaProxy(tool.getToolName(), tool.getToolPackage()));
            return tool;
        } catch (TaskException e) {
            log.warning("Error creating tool:" + FileUtils.formatThrowable(e));
            return null;
        }
    }


    private ToolStatus initTool(Tool tool, File file) {
        URL[] arr = initFile(tool, file);
        if (arr == null) {
            //what todo?
            return new ToolStatus(tool, ToolStatus.Status.UNKNOWN_FORMAT);
        } else {
            tool.setDefinitionPath(file.getAbsolutePath());
            if (isModified(tool)) {
                tools.put(createId(tool), new ToolUrl(tool, arr[0], arr[1], arr[2], file.lastModified()));
                return new ToolStatus(tool, ToolStatus.Status.OK);
            } else {
                return new ToolStatus(tool, ToolStatus.Status.NOT_MODIFIED);
            }
        }
    }

    private ToolStatus initJarredTool(Tool tool, File file) {
        URL[] arr = initJar(tool, file);
        if (arr == null) {
            //what todo?
            return new ToolStatus(tool, ToolStatus.Status.UNKNOWN_FORMAT);
        } else {
            tool.setDefinitionPath(file.getAbsolutePath());
            if (isModified(tool)) {
                Set<String> others = shared.get(tool.getDefinitionPath());
                if (others == null) {
                    others = new HashSet<String>();
                }
                others.add(createId(tool));
                shared.put(tool.getDefinitionPath(), others);
                tools.put(createId(tool), new ToolUrl(tool, arr[0], arr[1], arr[2], file.lastModified()));
                return new ToolStatus(tool, ToolStatus.Status.OK);
            } else {
                return new ToolStatus(tool, ToolStatus.Status.NOT_MODIFIED);
            }
        }
    }

    private Tool readXMLStream(InputStream in, String filePath, String toolbox) throws ToolException {
        XMLReader reader = new XMLReader(new BufferedReader(new InputStreamReader(in)));
        Tool tool = null;
        try {
            tool = reader.readComponent();
        } catch (Exception e) {
            throw new ToolException("Error reading tool :" + FileUtils.formatThrowable(e));
        }
        if (tool != null) {
            if (tool.getToolPackage().equals("")) {
                //TODO  or not todo
                //tool.setToolPackage(getToolPackageName(filePath, tool.getToolName()));
            }
            tool.setDefinitionPath(filePath);
            tool.setToolBox(toolbox);
            return tool;
        }
        return null;
    }

    private URL[] initJar(Tool tool, File file) {
        String name = tool.getToolName();
        if (name == null) {
            return null;
        }
        String pkg = tool.getToolPackage();
        if (pkg == null) {
            return null;
        } else {
            pkg = pkg.replace(".", "/") + "/";
        }
        JarHelper jf = null;
        try {
            jf = new JarHelper(file);
        } catch (IOException e) {
            return null;
        }
        String defRoot = null;
        String def = jf.getEntry(pkg + name + ".xml");

        if (def != null) {
            defRoot = "";
        } else {
            def = jf.getEntry(pkg + "xml/" + name + ".xml");
            if (def != null) {
                defRoot = "xml/";
            }
        }
        if (def != null) {
            //isXml = true;
        } else {
            def = jf.getEntry(pkg + name + ".class");
            if (def != null) {
                defRoot = "";
            } else {
                def = jf.getEntry(pkg + "classes/" + pkg + name + ".class");
                if (def != null) {
                    defRoot = pkg + "classes/";
                } else {
                    def = jf.getEntry(pkg + "target/classes/" + pkg + name + ".class");
                    if (def != null) {
                        defRoot = pkg + "target/classes/";
                    }
                }
            }
            if (def != null) {
                //isJava = true;
            }
        }
        if (def == null) {
            return null;
        }
        try {
            URL rootURL = file.toURI().toURL();
            URL defRootURL;
            if (defRoot.length() == 0) {
                defRootURL = file.toURI().toURL();
            } else {
                defRootURL = createJarURL(file, defRoot);
            }
            URL defURL = createJarURL(file, def);
            tool.setDefinitionPath(file.getAbsolutePath());
            return new URL[]{rootURL, defRootURL, defURL};
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private URL createJarURL(File jar, String entry) {
        try {
            String jarpath = jar.toURI().toURL().toString();
            String pref = "jar:";
            String post = "!/";
            if (entry != null) {
                post += entry;
            }
            return new URL(pref + jarpath + post);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // shouldn't get this. umm... famous last words...
        }
        return null;
    }

    private URL[] initFile(Tool tool, File file) {

        File[] files = getRootFromDefinition(tool, file);
        if (files == null || files[0] == null || files[1] == null) {
            return null;
        }
        try {
            URL rootURL = files[0].toURI().toURL();
            URL defRootURL = files[1].toURI().toURL();
            URL defURL = file.toURI().toURL();

            return new URL[]{rootURL, defRootURL, defURL};
        } catch (MalformedURLException e) {
            return null;
        }
    }


    private File[] getRootFromDefinition(Tool tool, File file) {
        String name = tool.getToolName();
        if (name == null) {
            return null;
        }
        String pkg = tool.getToolPackage();
        if (pkg == null) {
            return null;
        }
        List<String> names = getReversePackage(pkg);
        File root = null;
        File defRoot = null;
        if (file.getName().endsWith(".xml")) {
            root = matches(names, file);
            if (root != null) {
                defRoot = root;
            } else {
                names.add(0, "xml");
                root = matches(names, file);
                if (root != null) {
                    defRoot = new File(root, "xml");
                }
            }
        } else if (file.getName().endsWith(".class")) {
            root = matches(names, file);
            if (root != null) {
                defRoot = root;
            } else {
                List<String> classed = getReversePackage(pkg);
                classed.add("classes");
                classed.addAll(names);
                root = matches(classed, file);
                if (root != null) {
                    defRoot = new File(root, pkg.replace(".", File.separator) + File.separator + "classes");
                } else {
                    classed = getReversePackage(pkg);
                    classed.add("classes");
                    classed.add("target");
                    classed.addAll(names);
                    root = matches(names, file);
                    if (root != null) {
                        defRoot = new File(root,
                                pkg.replace(".", File.separator) + File.separator + "target" + File.separator
                                        + "classes");
                    }
                }
            }
        }
        if (defRoot == null || root == null) {
            return null;
        }
        return new File[]{root, defRoot};
    }

    private File matches(List<String> revPkg, File def) {
        File parent = def.getParentFile();
        int count = 0;
        while (parent != null && count < revPkg.size()) {
            if (!parent.getName().equals(revPkg.get(count++))) {
                return null;
            }
            parent = parent.getParentFile();
        }
        return parent;
    }

    private List<String> getReversePackage(String pkg) {
        ArrayList<String> p = new ArrayList<String>();
        String[] comps = pkg.split("\\.");
        for (String comp : comps) {
            if (comp.length() > 0) {
                p.add(0, comp);
            }
        }
        return p;
    }

    public Tool getTool(String fullname) {

        ToolUrl tool = tools.get(fullname);
        if (tool != null) {
            return tool.getTool();
        }
        return null;
    }


    public Tool[] getTools() {
        Tool[] t = new Tool[tools.size()];
        int count = 0;
        for (String tool : tools.keySet()) {
            t[count++] = (tools.get(tool).getTool());
        }
        return t;
    }

    public Tool[] getTools(String path) {
        Set<String> names = shared.get(path);
        if (names == null) {
            return new Tool[0];
        }
        List<Tool> ret = new ArrayList<Tool>();
        for (String name : names) {
            ToolUrl tu = tools.get(name);
            if (tu != null && tu.getTool() != null) {
                ret.add(tu.getTool());
            }
        }
        return ret.toArray(new Tool[ret.size()]);
    }

    public URL getRoot(Tool tool) {
        ToolUrl tu = tools.get(createId(tool));
        if (tu != null) {
            return tu.getRoot();
        }
        return null;
    }

    public URL getDefinitionRoot(Tool tool) {
        ToolUrl tu = tools.get(createId(tool));
        if (tu != null) {
            return tu.getDefRoot();
        }
        return null;
    }

    public URL getDefinition(Tool tool) {
        ToolUrl tu = tools.get(createId(tool));
        if (tu != null) {
            return tu.getDef();
        }
        return null;
    }

    public boolean writeTool(Tool tool, URL location) {
        if (location.getProtocol().equalsIgnoreCase("file")) {
            String path = location.getPath();
            File f = new File(path);
        }
        return false;
    }

    public boolean isModifiable(Tool tool) {
        ToolUrl tu = tools.get(createId(tool));
        if (tu != null) {
            return !tu.isJar() && !tu.isJava();
        }
        return false;
    }

    public boolean isModified(Tool tool) {
        ToolUrl tu = tools.get(createId(tool));
        if (tu != null) {
            File f = new File(tool.getDefinitionPath());
            if (f.exists() && f.length() > 0) {
                Tool exist = tu.getTool();
                if (exist == null) {
                    return true;
                }
                String path = exist.getDefinitionPath();
                if (path == null || !path.equals(tool.getDefinitionPath())) {
                    return true;
                }
                return tu.getLastModified() != f.lastModified();
            }
        }
        return true;
    }

    public boolean isMovable(Tool tool) {
        ToolUrl tu = tools.get(createId(tool));
        if (tu != null) {
            return tu.isJar() || tu.isXml();
        }
        return false;
    }

    public File toFile(URL url) {
        if (url == null) {
            return null;
        }
        try {
            URI uri = url.toURI();
            File f = new File(uri);
            if (f.exists() && f.length() > 0) {
                return f;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createId(Tool tool) {
        return tool.getQualifiedToolName();
    }

    private String getPackageName(String fullname) {
        if (fullname.endsWith(".class")) {
            fullname = fullname.substring(0, fullname.length() - 6);
        }
        int index = fullname.indexOf(".");
        if (index > 0) {
            return fullname.substring(0, fullname.lastIndexOf("."));
        }
        return "unknown";
    }

    private String getClassName(String fullname) {
        if (fullname.endsWith(".class")) {
            fullname = fullname.substring(0, fullname.length() - 6);
        }
        int index = fullname.indexOf(".");
        if (index > 0) {
            return fullname.substring(fullname.lastIndexOf(".") + 1, fullname.length());
        }
        return fullname;
    }

    private class ToolUrl {
        private Tool tool;
        private URL root;
        private URL defRoot;
        private URL def;
        private long lastModified = -1;

        private ToolUrl(Tool tool, URL root, URL defRoot, URL def, long lastModified) {
            this.tool = tool;
            this.root = root;
            this.defRoot = defRoot;
            this.def = def;
            this.lastModified = lastModified;

        }

        public Tool getTool() {
            return tool;
        }

        public URL getRoot() {
            return root;
        }

        public URL getDefRoot() {
            return defRoot;
        }

        public URL getDef() {
            return def;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public boolean isJava() {
            return getDef().toString().endsWith(".class");
        }

        public boolean isXml() {
            return getDef().toString().endsWith(".xml");
        }

        public boolean isJar() {
            return getDef().toString().startsWith("jar:");
        }
    }

}
