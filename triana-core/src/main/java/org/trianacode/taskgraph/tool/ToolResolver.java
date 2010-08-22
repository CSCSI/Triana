package org.trianacode.taskgraph.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.w3c.dom.Element;
import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.Resource;
import org.thinginitself.http.Response;
import org.thinginitself.http.util.MimeHandler;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableFile;
import org.thinginitself.streamable.StreamableStream;
import org.trianacode.discovery.ResolverRegistry;
import org.trianacode.discovery.ToolMetadataResolver;
import org.trianacode.discovery.toolinfo.ToolMetadata;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.DocumentHandler;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.creators.type.ClassHierarchy;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.taskgraph.util.Home;
import org.trianacode.taskgraph.util.UrlUtils;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 9, 2010
 */

public class ToolResolver implements ToolMetadataResolver {


    private static Logger log = Logger.getLogger(ToolResolver.class.getName());

    private long resolveInterval = 10 * 1000;

    private Timer timer = new Timer();

    public static final String MIME_ZIP = "application/zip";
    public static final String MIME_JAR = "application/java-archive";
    public static final String EXT_TASKGRAPH = ".xml";
    public static final String EXT_JAVA_CLASS = ".class";

    public static String[] excludedDirectories = {"CVS", "src", "lib"};
    public static String[] extensions = {".xml", ".class", ".jar"};
    private Map<URL, Object> resolved = new ConcurrentHashMap<URL, Object>();


    private HttpPeer peer = new HttpPeer();

    private Map<String, ToolboxTools> tools = new ConcurrentHashMap<String, ToolboxTools>();
    protected Map<String, Toolbox> toolboxes = new ConcurrentHashMap<String, Toolbox>();
    private List<ToolListener> listeners = new ArrayList<ToolListener>();

    public long getResolveInterval() {
        return resolveInterval;
    }

    public void setResolveInterval(long resolveInterval) {
        this.resolveInterval = resolveInterval;
    }

    public void addToolListener(ToolListener listener) {
        listeners.add(listener);
    }

    public void removeToolListener(ToolListener listener) {
        listeners.remove(listener);
    }

    public void addToolbox(Toolbox toolbox) {
        addNewToolBox(toolbox);
        resolve(toolbox);
        saveToolboxes();
        for (ToolListener listener : listeners) {
            listener.toolBoxAdded(toolbox);
        }
    }

    public List<String> getToolboxPaths() {
        List<String> ret = new ArrayList<String>();
        for (String s : toolboxes.keySet()) {
            ret.add(s);
        }
        return ret;
    }

    public Toolbox getToolbox(String path) {
        return toolboxes.get(path);
    }

    public void removeToolbox(Toolbox toolbox) {
        String path = toolbox.getPath();
        if (path == null) {
            return;
        }
        ToolboxTools dated = tools.remove(path);
        dated = null;
        toolboxes.remove(toolbox.getPath());
        saveToolboxes();
        for (ToolListener listener : listeners) {
            listener.toolBoxRemoved(toolbox);
        }

    }

    public void removeTool(Tool tool) {
        String toolbox = tool.getToolBox().getPath();
        if (toolbox == null) {
            return;
        }
        ToolboxTools dated = tools.get(toolbox);
        if (dated == null) {
            return;
        }
        Tool match = dated.removeTool(tool.getQualifiedToolName());
        if (match != null) {
            for (ToolListener listener : listeners) {
                listener.toolRemoved(tool);
            }
        }
    }

    public void deleteTool(Tool tool) {
        String toolbox = tool.getToolBox().getPath();
        if (toolbox == null) {
            return;
        }
        ToolboxTools dated = tools.get(toolbox);
        if (dated == null) {
            return;
        }
        Tool match = dated.removeTool(tool.getQualifiedToolName());
        if (match != null) {
            deleteLocalTool(tool);
            for (ToolListener listener : listeners) {
                listener.toolRemoved(tool);
            }
        }
    }

    public void addTool(Tool tool) {
        Toolbox toolbox = tool.getToolBox();
        if (toolbox == null) {
            return;
        }
        ToolboxTools dated = tools.get(toolbox);
        if (dated == null) {
            dated = new ToolboxTools(toolbox.getPath());
            tools.put(toolbox.getPath(), dated);
        }
        boolean add = dated.addTool(tool);
        if (add) {
            for (ToolListener listener : listeners) {
                listener.toolAdded(tool);
            }
        }
    }

    public List<Tool> getTools() {
        List<Tool> ret = new ArrayList<Tool>();
        for (ToolboxTools toolboxTools : tools.values()) {
            ret.addAll(toolboxTools.getTools());
        }
        return ret;
    }

    public List<ToolMetadata> getToolMetadata() {
        List<ToolMetadata> ret = new ArrayList<ToolMetadata>();
        for (ToolboxTools toolboxTools : tools.values()) {
            List<Tool> tools = toolboxTools.getTools();
            for (Tool tool : tools) {
                ret.add(createMetadata(tool));
            }
        }
        return ret;
    }

    public List<String> getToolNames() {
        List<String> ret = new ArrayList<String>();
        for (ToolboxTools toolboxTools : tools.values()) {
            List<Tool> tts = toolboxTools.getTools();
            for (Tool tt : tts) {
                ret.add(tt.getQualifiedToolName());
            }
        }
        return ret;
    }

    public boolean isModifiable(Tool tool) {
        URL url = tool.getDefinitionPath();
        File f = UrlUtils.getExistingFile(url);
        if (f != null && !f.getName().endsWith(".jar")) {
            return true;
        }
        return false;
    }

    /**
     * gets a list of tools that share the same definition URL.
     *
     * @param definition
     * @return
     */
    public List<Tool> getTools(URL definition) {
        List<Tool> ret = new ArrayList<Tool>();
        for (ToolboxTools toolboxTools : tools.values()) {
            List<Tool> shared = toolboxTools.getToolsByUrl(definition);
            if (shared != null) {
                ret.addAll(shared);
            }
        }
        return ret;
    }

    /**
     * get tools from toolboxes with a local (file) URL
     *
     * @return
     */
    public List<Tool> getLocalTools() {
        List<Tool> ret = new ArrayList<Tool>();
        for (ToolboxTools toolboxTools : tools.values()) {
            String path = toolboxTools.getToolbox();
            if (UrlUtils.getExistingFile(path) != null) {
                ret.addAll(toolboxTools.getTools());
            }
        }
        return ret;
    }

    public List<ToolMetadata> getLocalToolMetadata() {
        List<ToolMetadata> ret = new ArrayList<ToolMetadata>();
        for (ToolboxTools toolboxTools : tools.values()) {
            String path = toolboxTools.getToolbox();
            if (UrlUtils.getExistingFile(path) != null) {
                List<Tool> tools = toolboxTools.getTools();
                for (Tool tool : tools) {
                    ret.add(createMetadata(tool));
                }
            }
        }
        return ret;
    }

    public List<Tool> getTools(String toolbox) {
        ToolboxTools tt = this.tools.get(toolbox);
        if (tt != null) {
            return tt.getTools();
        }
        return new ArrayList<Tool>();
    }

    public List<ToolMetadata> getToolMetadata(String toolbox) {
        List<ToolMetadata> ret = new ArrayList<ToolMetadata>();
        ToolboxTools tt = this.tools.get(toolbox);
        if (tt != null) {
            List<Tool> tools = tt.getTools();
            for (Tool tool : tools) {
                ret.add(createMetadata(tool));
            }
        }
        return ret;
    }

    public Tool getTool(String fullname) {
        for (ToolboxTools tt : tools.values()) {
            Tool t = tt.getTool(fullname);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    public List<Toolbox> getToolboxes() {
        List<Toolbox> ret = new ArrayList<Toolbox>();
        for (Toolbox toolbox : toolboxes.values()) {
            ret.add(toolbox);
        }
        return ret;
    }

    public void refreshTools(URL url, String toolbox) {
        try {
            Toolbox box = toolboxes.get(toolbox);
            if (box != null) {
                resolveTools(url, box);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean deleteLocalTool(Tool tool) {
        File f = UrlUtils.getExistingFile(tool.getDefinitionPath());
        if (f != null && !f.getName().endsWith(".jar")) {
            f.delete();
            return true;
        }
        return false;
    }


    @Override
    public String getName() {
        return "TrianaMetadataResolver";
    }

    @Override
    public List<ToolMetadata> resolve(Toolbox toolbox) {
        List<Tool> tools = resolveToolbox(toolbox);
        notifyToolsAdded(tools);
        List<ToolMetadata> ret = new ArrayList<ToolMetadata>();
        for (Tool tool : tools) {
            ret.add(createMetadata(tool));
        }
        return ret;
    }

    private void notifyToolsAdded(List<Tool> tools) {
        for (ToolListener listener : listeners) {
            listener.toolsAdded(tools);
        }
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
            if (!f.exists() || f.length() == 0) {
                return null;
            }
            return new StreamableFile(f, MimeHandler.getMime(f.getName()));
        } else if (UrlUtils.isHttp(url)) {
            RequestContext context = new RequestContext(url.toString());
            Response resp = peer.get(context);
            int status = resp.getContext().getResponseCode();
            if (status != 200) {
                return null;
            }
            return resp.getResource().getStreamable();
        }
        return null;
    }

    /**
     * primary method of this class. It loads the toolboxes, resolves the tools and notifies listeners.
     */
    public void resolve() {
        loadToolboxes();
        reresolve();
        timer.scheduleAtFixedRate(new ResolveThread(), getResolveInterval(), getResolveInterval());
    }


    private void reresolve() {
        for (String s : toolboxes.keySet()) {
            Toolbox tb = toolboxes.get(s);
            resolve(toolboxes.get(s));
            Collection<ToolMetadataResolver> resolvers = ResolverRegistry.getResolvers();
            for (ToolMetadataResolver resolver : resolvers) {
                List<ToolMetadata> md = resolver.resolve(tb);
                if (md != null && md.size() > 0) {
                    List<Tool> useful = new ArrayList<Tool>();
                    for (ToolMetadata toolMetadata : md) {
                        useful.add(createTool(toolMetadata));
                    }
                    notifyToolsAdded(useful);
                }
            }
        }
    }

    /**
     * TODO - resolve remote tools
     *
     * @param toolbox
     */
    protected List<Tool> resolveToolbox(Toolbox toolbox) {
        List<URL> urls = new ArrayList<URL>();
        URL toolboxUrl = UrlUtils.toURL(toolbox.getPath());
        urls.addAll(findLocal(toolboxUrl));
        //System.out.println("ToolResolver.resolve got back " + urls.size() + " URLs");
        List<Tool> ret = new ArrayList<Tool>();
        for (URL url : urls) {
            try {
                List<Tool> tools = resolveTools(url, toolbox);
                ret.addAll(tools);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Will this work for taskgraph???
     *
     * @param metadata
     * @return
     */
    private Tool createTool(ToolMetadata metadata) {
        try {
            if (!metadata.isTaskgraph()) {
                ToolImp tool = new ToolImp();
                tool.setDefinitionType(Tool.DEFINITION_METADATA);
                tool.setToolName(getClassName(metadata.getToolName()));
                tool.setToolPackage(getPackageName(metadata.getToolName()));
                String cls = metadata.getUnitWrapper();
                if (cls == null) {
                    tool.setProxy(new JavaProxy(tool.getToolName(), tool.getToolPackage()));
                } else {
                    tool.setProxy(new JavaProxy(getClassName(cls), getPackageName(cls)));
                }
                return tool;
            } else {
                TaskGraph taskgraph = TaskGraphManager.createTaskGraph();
                taskgraph.setDefinitionType(Tool.DEFINITION_METADATA);
                taskgraph.setToolName(getClassName(metadata.getToolName()));
                taskgraph.setToolPackage(getPackageName(metadata.getToolName()));
                return taskgraph;
            }
        } catch (TaskException e) {
            log.warning("Error creating tool:" + FileUtils.formatThrowable(e));
            return null;
        }
    }

    private ToolMetadata createMetadata(Tool tool) {
        Proxy proxy = tool.getProxy();
        String cls = null;
        boolean taskgraph = true;
        if (proxy != null && proxy instanceof JavaProxy) {
            cls = tool.getQualifiedToolName();
            taskgraph = false;
        }
        ToolMetadata ret = new ToolMetadata(tool.getQualifiedToolName(), tool.getQualifiedToolName(),
                tool.getDefinitionPath(),
                cls, taskgraph);
        return ret;
    }


    protected void addNewToolBox(Toolbox... box) {
        for (Toolbox toolbox : box) {
            if (!toolbox.isVirtual()) {
                try {
                    if (toolboxes.get(toolbox.getPath()) == null) {
                        toolbox.loadTools();
                        toolboxes.put(toolbox.getPath(), toolbox);
                    }
                } catch (Exception e) {
                    log.warning("Error adding toolbox: " + FileUtils.formatThrowable(e));
                }
            }
        }
    }

    /**
     * resolve tools from a particular URL. A single URL can contain more than one tool for example if it's a jar.
     *
     * @param url
     * @param toolbox
     * @return
     * @throws Exception
     */
    protected List<Tool> resolveTools(URL url, Toolbox toolbox) throws Exception {
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
                tool.setToolBox(toolbox);
            }
            ToolboxTools dated = this.tools.get(toolbox.getPath());
            if (dated == null) {
                dated = new ToolboxTools(toolbox.getPath(), tools);
            } else {
                dated.addTools(tools);
            }
            this.tools.put(toolbox.getPath(), dated);
        }
        return tools;
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
            log.fine("error reading xml file " + FileUtils.formatThrowable(e));
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
            if (entry.getName().endsWith(ToolResolver.EXT_TASKGRAPH)) {
                byte[] buff = new byte[2048];
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                int c;
                while ((c = zin.read(buff)) != -1) {
                    bout.write(buff, 0, c);
                }
                ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
                List<Tool> xmls = processXml(new StreamableStream(bin));
                ret.addAll(xmls);
            } else if (entry.getName().endsWith(ToolResolver.EXT_JAVA_CLASS)) {
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
        log.fine("ToolResolver.add class hierarchy:" + ch);
        if (ch == null) {
            ch = TypesMap.getAnnotated(url.toString());
        }
        log.fine("ToolResolver.add class hierarchy after trying annotations:" + ch + " for url "
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
            log.warning("Error creating tool:" + FileUtils.formatThrowable(e));
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

    protected ToolStatus checkTool(DatedTool datedTool) throws Exception {
        Tool tool = datedTool.getTool();
        long lastModified = datedTool.getLastModified();
        URL url = tool.getDefinitionPath();
        if (url == null) {
            return new ToolStatus(tool, ToolStatus.Status.NULL_DEFINITION_PATH);
        }
        try {
            ToolStatus ok = new ToolStatus(tool, ToolStatus.Status.OK);
            if (UrlUtils.isFile(url)) {
                File f = new File(url.toURI());
                if (!f.exists() || f.length() == 0) {
                    return new ToolStatus(tool, ToolStatus.Status.NON_EXISTENT);
                }
                long lm = f.lastModified();
                if (lm < lastModified) {
                    return new ToolStatus(tool, ToolStatus.Status.NOT_MODIFIED);
                }
                ok.setDefinition(new StreamableFile(f, MimeHandler.getMime(f.getName())));
            } else if (UrlUtils.isHttp(url)) {
                RequestContext context = new RequestContext(url.toString());
                Resource res = new Resource();
                res.setLastModified(new Date(lastModified));
                context.setResource(res);
                Response resp = peer.get(context);
                int status = resp.getContext().getResponseCode();
                if (status == 304) {
                    return new ToolStatus(tool, ToolStatus.Status.NOT_MODIFIED);
                } else if (status == 404) {
                    return new ToolStatus(tool, ToolStatus.Status.NON_EXISTENT);
                } else if (status >= 300 || status < 200) {
                    return new ToolStatus(tool, ToolStatus.Status.ERROR);
                } else if (status == 200) {
                    ok.setDefinition(resp.getResource().getStreamable());
                }
            }
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void saveToolboxes() {
        try {
            File file = new File(Home.home() + File.separator + "toolboxes.xml");
            PrintWriter bw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            DocumentHandler handler = new DocumentHandler();

            Element root = handler.element("toolboxes");
            handler.setRoot(root);


            List<Toolbox> toolboxes = getToolboxes();
            for (int count = 0; count < toolboxes.size(); count++) {
                Element toolbox = handler.element("toolbox");
                handler.add(toolboxes.get(count).getPath(), toolbox);

                toolbox.setAttribute("type", toolboxes.get(count).getType());
                toolbox.setAttribute("virtual", toolboxes.get(count).isVirtual() + "");
                toolbox.setAttribute("name", toolboxes.get(count).getName());
                handler.add(toolbox, root);
            }
            handler.output(bw, true);
            bw.flush();
            FileUtils.closeWriter(bw);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createDefaultToolboxes() throws IOException {
        File file = new File(Home.home() + File.separator + "toolboxes.xml");
        PrintWriter bw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        DocumentHandler handler = new DocumentHandler();

        Element root = handler.element("toolboxes");
        handler.setRoot(root);
        File defToolbox = new File(Home.home() + File.separator + "toolbox");
        defToolbox.mkdirs();

        Toolbox tb = new Toolbox(defToolbox.getAbsolutePath(), Toolbox.INTERNAL, "user");
        Element toolbox = handler.element("toolbox");
        handler.add(tb.getPath(), toolbox);

        toolbox.setAttribute("type", tb.getType());
        toolbox.setAttribute("virtual", tb.isVirtual() + "");
        toolbox.setAttribute("name", tb.getName());
        handler.add(toolbox, root);

        handler.output(bw, true);
        bw.flush();
        FileUtils.closeWriter(bw);
    }

    private String findInternalToolboxes() {
        if (Home.isJarred()) {
            File f = Home.runHome();
            File p = f.getParentFile();
            return new File(p, "toolboxes").getAbsolutePath();
        } else {
            return new File(Home.runHome(), "toolboxes").getAbsolutePath();
        }
    }

    /**
     * load the toolboxes from a file description
     */
    public void loadToolboxes() {
        BufferedReader br = null;
        try {
            File file = new File(Home.home() + File.separator + "toolboxes.xml");
            if (!file.exists() || file.length() == 0) {
                createDefaultToolboxes();

            }
            file = new File(Home.home() + File.separator + "toolboxes.xml");
            List<Toolbox> tbs = new ArrayList<Toolbox>();


            br = new BufferedReader(new FileReader(file));
            DocumentHandler handler = new DocumentHandler(br);

            Element root = handler.root();

            if (!root.getLocalName().equals("toolboxes")) {
                throw (new Exception("Corrupt config file: " + file.getAbsolutePath()));
            }

            List elementList = handler.getChildren(root);
            Iterator iter = elementList.iterator();
            Element elem;
            String toolbox;

            while (iter.hasNext()) {
                elem = ((Element) iter.next());
                toolbox = elem.getTextContent().trim();
                String type = elem.getAttribute("type");
                String virtual = elem.getAttribute("virtual");
                String name = elem.getAttribute("name");
                if (name == null || name.equals("No Type")) {
                    name = UrlUtils.getLastPathComponent(toolbox);
                }
                boolean v = virtual != null && virtual.equals("true");
                if (!v) {
                    if (!new File(toolbox).exists()) {
                        log.severe("Error: Toolbox " + toolbox + " doesn't exists removing from config");
                    } else {
                        if (type != null && type.length() > 0) {
                            tbs.add(new Toolbox(toolbox, type, name, v));
                        } else {
                            tbs.add(new Toolbox(toolbox, name, v));
                        }
                    }
                }
            }
            if (tbs.size() > 0) {
                addNewToolBox(tbs.toArray(new Toolbox[tbs.size()]));
            }
            String internal = findInternalToolboxes();
            if (internal != null) {
                Toolbox intern = new Toolbox(internal, "internal", "default-toolboxes", false);
                addNewToolBox(intern);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeReader(br);
        }

    }

    public void shutdown() {
        timer.cancel();
    }

    private class ResolveThread extends TimerTask {
        public void run() {
            reresolve();
        }
    }


    public static class ToolStatus {

        public static enum Status {
            OK,
            NON_EXISTENT,
            REMOVED,
            NOT_ADDED,
            NOT_MODIFIED,
            NULL_DEFINITION_PATH,
            NULL_TOOL,
            UNKNOWN_FORMAT,
            ERROR

        }

        private Tool tool;
        private Streamable definition;
        private Status status;

        public ToolStatus(Tool tool, Status status) {
            this.tool = tool;
            this.status = status;
        }

        public Tool getTool() {
            return tool;
        }

        public Status getStatus() {
            return status;
        }

        public Streamable getDefinition() {
            return definition;
        }

        public void setDefinition(Streamable definition) {
            this.definition = definition;
        }
    }

    private static class ToolboxTools {

        private String toolbox;
        private Map<String, DatedTool> tools = new HashMap<String, DatedTool>();

        private Map<URL, List<Tool>> toolByUrl = new HashMap<URL, List<Tool>>();

        private ToolboxTools(String toolbox, List<Tool> tools) {
            this.toolbox = toolbox;
            for (Tool tool : tools) {
                this.tools.put(tool.getQualifiedToolName(), new DatedTool(tool));
                List<Tool> shared = toolByUrl.get(tool.getDefinitionPath());
                if (shared == null) {
                    shared = new ArrayList<Tool>();
                }
                if (!shared.contains(tool)) {
                    shared.add(tool);
                }
                toolByUrl.put(tool.getDefinitionPath(), shared);
            }
        }

        private ToolboxTools(String toolbox) {
            this.toolbox = toolbox;
        }

        public String getToolbox() {
            return toolbox;
        }

        public Map<String, DatedTool> getToolsMap() {
            return tools;
        }

        public List<Tool> getTools() {
            List<Tool> ret = new ArrayList<Tool>();
            for (DatedTool datedTool : tools.values()) {
                ret.add(datedTool.getTool());
            }
            return ret;
        }

        public Tool getTool(String qualifiedName) {
            DatedTool dt = tools.get(qualifiedName);
            if (dt != null) {
                return dt.getTool();
            }
            return null;
        }

        public boolean addTool(Tool tool) {
            if (tools.get(tool.getQualifiedToolName()) == null) {
                tools.put(tool.getQualifiedToolName(), new DatedTool(tool));
                List<Tool> shared = toolByUrl.get(tool.getDefinitionPath());
                if (shared == null) {
                    shared = new ArrayList<Tool>();
                }
                if (!shared.contains(tool)) {
                    shared.add(tool);
                }
                toolByUrl.put(tool.getDefinitionPath(), shared);
                return true;
            }
            return false;
        }

        public List<Tool> getToolsByUrl(URL url) {
            return toolByUrl.get(url);
        }

        public boolean addTools(List<Tool> tools) {
            for (Tool tool : tools) {
                this.tools.put(tool.getQualifiedToolName(), new DatedTool(tool));

            }
            return true;
        }

        public Tool removeTool(String qualifiedName) {
            DatedTool dt = tools.remove(qualifiedName);
            if (dt != null) {
                return dt.getTool();
            }
            return null;
        }

    }

    private static class DatedTool {
        private Tool tool;
        private long lastModified;

        private DatedTool(Tool tool) {
            this.tool = tool;
            this.lastModified = System.currentTimeMillis();
        }

        public Tool getTool() {
            return tool;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }
    }
}
