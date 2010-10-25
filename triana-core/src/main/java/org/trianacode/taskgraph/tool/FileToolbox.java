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

import org.apache.commons.logging.Log;
import org.thinginitself.http.util.MimeHandler;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableFile;
import org.thinginitself.streamable.StreamableStream;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.creators.type.ClassHierarchy;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.taskgraph.util.UrlUtils;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class FileToolbox implements Toolbox {

    private static Log log = Loggers.TOOL_LOGGER;


    /**
     * constants for directory names where native and java libs are stored in a toolbox. For a toolbox to know where
     * these things are and have them loaded, and exposed, these naming convention must be followed.
     * <p/>
     * The OS-specific dir names should all be within a 'nativ' dir.
     */
    public static final String NATIVE_DIR = "nativ";
    public static final String WIN_32_DIR = "win32";
    public static final String WIN_64_DIR = "win64";
    public static final String OSX_DIR = "osx";
    public static final String OSX_32_DIR = "osx32";
    public static final String OSX_64_DIR = "osx64";
    public static final String NUX_32_DIR = "nux32";
    public static final String NUX_64_DIR = "nux64";
    public static final String LIB_DIR = "lib";
    public static final String HELP_DIR = "help";

    public static final String EXT_TASKGRAPH = ".xml";
    public static final String EXT_JAVA_CLASS = ".class";

    public static String[] excludedDirectories = {"CVS", "src", "lib"};
    public static String[] extensions = {".xml", ".class", ".jar"};

    public static String[] nativeDirs =
            {
                    WIN_32_DIR,
                    WIN_64_DIR,
                    OSX_DIR,
                    OSX_32_DIR,
                    OSX_64_DIR,
                    NUX_32_DIR,
                    NUX_64_DIR
            };


    private String path;
    private String type;
    private String name;
    private Map<String, Tool> tools = new HashMap<String, Tool>();

    public static final String INTERNAL = "internal";
    private ToolClassLoader loader = new ToolClassLoader();

    private TrianaProperties properties;

    private Map<URL, Object> resolved = new ConcurrentHashMap<URL, Object>();


    public FileToolbox(String path, String type, String name, TrianaProperties properties) {
        this.path = path;
        this.properties = properties;
        this.type = type;
        this.name = name;
    }


    public FileToolbox(String path, String name, TrianaProperties properties) {
        this(path, "No Type", name, properties);
    }

    public FileToolbox(String path, TrianaProperties properties) {
        this(path, "No Type", UrlUtils.getLastPathComponent(path), properties);
    }

    public FileToolbox(File file, String type) {
        file.mkdirs();
        this.path = file.getAbsolutePath();
        this.type = type;
        this.name = file.getName();
    }

    public FileToolbox(File file) {
        this(file, "No Type");
    }

    public TrianaProperties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(TrianaProperties properties) {
        this.properties = properties;
    }

    public ClassLoader getClassLoader() {
        return loader;
    }

    /**
     * get relative paths pointing to library files (.class, .jar)
     *
     * @return
     */
    public List<String> getLibPaths() {
        return loader.getLibPaths();
    }

    /**
     * get a list of local files that can browsed by a user. Any file not equal to or a child of one of these strings
     * will have access denied.
     *
     * @return
     */
    public List<String> getVisibleRoots() {
        return loader.getVisibleRools();
    }

    /**
     * attempt to get a local file with a path that is a child of the root of the toolbox
     *
     * @param relativePath
     * @return
     */
    public File getFile(String relativePath) {
        return loader.getFile(relativePath);
    }

    public String getClassPath() {
        return loader.getClassPath();
    }

    public void loadTools() throws Exception {
        ClassLoaders.addClassLoader(loader);
        URL url = UrlUtils.toURL(getPath());
        loader.addToolBox(url);
        TypesMap.load(url);
        this.tools = resolveTools();
    }

    @Override
    public void refresh(URL url) throws Exception {
        List<Tool> ts = resolveTools(url);
        for (Tool t : ts) {
            this.tools.put(t.getQualifiedToolName(), t);
        }
    }

    @Override
    public List<Tool> getTools(URL url) {
        List<Tool> t = new ArrayList<Tool>();
        List<Tool> tools = getTools();
        for (Tool tool : tools) {
            if (tool.getDefinitionPath().equals(url)) {
                t.add(tool);
            }
        }
        return t;
    }

    @Override
    public List<Tool> getTools() {
        return new ArrayList(tools.values());
    }

    @Override
    public Tool getTool(String name) {
        return tools.get(name);
    }

    @Override
    public Tool removeTool(String name) {
        return tools.remove(name);
    }

    @Override
    public Tool deleteTool(String name) {
        Tool tool = removeTool(name);
        if (tool != null) {
            deleteLocalTool(tool);
            return tool;
        }
        return null;

    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }


    private boolean deleteLocalTool(Tool tool) {
        File f = UrlUtils.getExistingFile(tool.getDefinitionPath());
        if (f != null && !f.getName().endsWith(".jar")) {
            f.delete();
            return true;
        }
        return false;
    }


    /**
     *
     */
    protected Map<String, Tool> resolveTools() {
        List<URL> urls = new ArrayList<URL>();
        URL toolboxUrl = UrlUtils.toURL(getPath());
        urls.addAll(findLocal(toolboxUrl));
        Map<String, Tool> ret = new HashMap<String, Tool>();
        for (URL url : urls) {
            try {
                List<Tool> tools = resolveTools(url);
                for (Tool tool : tools) {
                    ret.put(tool.getQualifiedToolName(), tool);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * resolve tools from a particular URL. A single URL can contain more than one tool for example if it's a jar.
     *
     * @param url
     * @return
     * @throws Exception
     */
    protected List<Tool> resolveTools(URL url) throws Exception {
        Streamable streamable = getToolStream(url);
        if (streamable == null || !streamable.hasInputStream()) {
            return null;
        }
        String mime = streamable.getMimeType();
        List<Tool> tools = new ArrayList<Tool>();
        if (url.getPath().endsWith(EXT_TASKGRAPH)) {
            tools = processXml(streamable);
        } else if (url.getPath().endsWith(".jar")) {
            tools = processJar(url, streamable);
        } else if (url.getPath().endsWith(EXT_JAVA_CLASS)) {
            tools = processClass(url);
        }
        if (tools != null) {
            for (Tool tool : tools) {
                tool.setDefinitionPath(url);
                tool.setToolBox(this);
            }
        }
        return tools;
    }


    /**
     * get a streamable from a tool URL
     *
     * @param url
     * @return
     * @throws Exception
     */
    protected Streamable getToolStream(URL url) throws Exception {
        if (UrlUtils.isFile(url)) {
            File f = new File(url.toURI());
            if (!f.exists()) {
                return null;
            }
            return new StreamableFile(f, MimeHandler.getMime(f.getName()));
        }
        return null;
    }


    /**
     * find tools on the local file system
     *
     * @param url
     * @return
     */
    private List<URL> findLocal(URL url) {
        ArrayList<URL> files = new ArrayList<URL>();
        File f = UrlUtils.getExistingFile(url);
        if (f == null) {
            return files;
        }
        if (f.getName().startsWith(".")) {
            return files;
        }
        for (String extDir : excludedDirectories) {
            if (f.getName().equals(extDir)) {
                return files;
            }
        }
        if (f.isDirectory()) {
            File[] fs = f.listFiles(new FilenameFilter() {
                public boolean accept(File file, String s) {
                    File child = new File(file, s);
                    if (child.isDirectory()) {
                        return true;
                    } else {
                        for (String ext : extensions) {
                            if (child.getName().endsWith(ext)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
            for (File file : fs) {
                URL furl = UrlUtils.fromFile(file);
                if (furl != null && resolved.get(furl) == null) {
                    files.addAll(findLocal(furl));
                    resolved.put(furl, new Object());
                }
            }
        } else {
            URL furl = UrlUtils.fromFile(f);
            if (furl != null && resolved.get(furl) == null) {
                files.add(furl);
                resolved.put(furl, new Object());
            }
        }
        return files;
    }


    /**
     * process an XML tool description
     *
     * @param streamable
     * @return
     * @throws Exception
     */
    private List<Tool> processXml(Streamable streamable) throws Exception {
        XMLReader reader = null;
        try {
            reader = new XMLReader(new BufferedReader(new InputStreamReader(streamable.getInputStream())));
            Tool tool = reader.readComponent();
            if (tool != null) {
                return Arrays.asList(tool);
            }
            return new ArrayList<Tool>();
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("error reading xml file ", e);
            throw new ToolException("Error reading tool :" + FileUtils.formatThrowable(e));
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * process a jar file
     *
     * @param url
     * @param streamable
     * @return
     * @throws Exception
     */
    private List<Tool> processJar(URL url, Streamable streamable) throws Exception {
        List<Tool> ret = new ArrayList<Tool>();
        InputStream in = streamable.getInputStream();
        if (!(in instanceof ZipInputStream)) {
            in = new ZipInputStream(in);
        }
        ZipInputStream zin = (ZipInputStream) in;
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            if (entry.getName().endsWith(EXT_TASKGRAPH)) {
                byte[] buff = new byte[2048];
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                int c;
                while ((c = zin.read(buff)) != -1) {
                    bout.write(buff, 0, c);
                }
                ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
                List<Tool> xmls = processXml(new StreamableStream(bin));
                ret.addAll(xmls);
            } else if (entry.getName().endsWith(EXT_JAVA_CLASS)) {
                List<Tool> clss = processClass(new URL("jar:" + url.toString() + "!/" + entry.getName()));
                ret.addAll(clss);
            }
        }
        return ret;
    }

    /**
     * process a java class file
     *
     * @param url
     * @return
     * @throws Exception
     */
    private List<Tool> processClass(URL url) throws Exception {
        ClassHierarchy ch = TypesMap.isType(url.toString(), Unit.class.getName());
        log.debug("ToolResolver.add class hierarchy:" + ch);
        if (ch == null) {
            ch = TypesMap.getAnnotated(url.toString());
        }
        log.debug("ToolResolver.add class hierarchy after trying annotations:" + ch + " for url "
                + url);

        if (ch != null) {
            Tool tool = createTool(ch.getName());
            if (tool != null) {
                return Arrays.asList(tool);
            }
        }
        return new ArrayList<Tool>();
    }


    /**
     * instantiate a tool
     *
     * @param className
     * @return
     */
    protected Tool createTool(String className) {
        try {
            ToolImp tool = new ToolImp();
            tool.setDefinitionType(Tool.DEFINITION_JAVA_CLASS);
            tool.setToolName(getClassName(className));
            tool.setToolPackage(getPackageName(className));
            tool.setProxy(new JavaProxy(tool.getToolName(), tool.getToolPackage()));
            return tool;
        } catch (TaskException e) {
            log.warn("Error creating tool:", e);
            return null;
        }
    }


    private String getPackageName(String fullname) {
        if (fullname.endsWith(EXT_JAVA_CLASS)) {
            fullname = fullname.substring(0, fullname.length() - 6);
        }
        int index = fullname.indexOf(".");
        if (index > 0) {
            return fullname.substring(0, fullname.lastIndexOf("."));
        }
        return "unknown";
    }

    private String getClassName(String fullname) {
        if (fullname.endsWith(EXT_JAVA_CLASS)) {
            fullname = fullname.substring(0, fullname.length() - 6);
        }
        int index = fullname.indexOf(".");
        if (index > 0) {
            return fullname.substring(fullname.lastIndexOf(".") + 1, fullname.length());
        }
        return fullname;
    }

}
