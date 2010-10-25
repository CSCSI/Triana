package org.trianacode.discovery.protocols.tdp.imp.trianatools;

import org.apache.commons.logging.Log;
import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.Resource;
import org.thinginitself.http.Response;
import org.thinginitself.http.util.MimeHandler;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableFile;
import org.thinginitself.streamable.StreamableStream;
import org.trianacode.config.Locations;
import org.trianacode.config.TrianaProperties;
import org.trianacode.discovery.ResolverRegistry;
import org.trianacode.discovery.ToolMetadataResolver;
import org.trianacode.discovery.toolinfo.ToolMetadata;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.*;
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
 * @author Andrew Harrison
 * @version 1.0.0 Aug 9, 2010
 */

public class ToolResolver implements ToolMetadataResolver {


    private static Log log = Loggers.TOOL_LOGGER;

    private long resolveInterval = 10 * 1000;

    private Timer timer = new Timer();

    private boolean initialResolved = false;

    private HttpPeer peer = new HttpPeer();

    private Map<String, ToolboxTools> tools = new ConcurrentHashMap<String, ToolboxTools>();
    protected Map<String, Toolbox> toolboxes = new ConcurrentHashMap<String, Toolbox>();
    private List<ToolListener> listeners = new ArrayList<ToolListener>();

    TrianaProperties properties;

    // disable the creation without properties.

    private ToolResolver() {
    }

    public ToolResolver(TrianaProperties properties) {
        this.properties = properties;
    }

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

    public TrianaProperties getProperties() {
        return properties;
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
                box.refresh(url);
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


    public String getName() {
        return "TrianaMetadataResolver";
    }

    public List<ToolMetadata> resolve(Toolbox toolbox) {
        List<Tool> tools = toolbox.getTools();
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
     * primary method of this class. It loads the toolboxes, resolves the tools and notifies listeners.
     * <p/>
     * Ian T - changed this to run in a thread upon start up so we can see the GUI quicker
     */
    public void resolve(boolean reresolve, final List<String> extraToolboxes) {
        if (initialResolved) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {

                loadToolboxes(extraToolboxes);
                reresolve();
            }
        }).start();
        initialResolved = true;
        if (reresolve) {
            timer.scheduleAtFixedRate(new ResolveThread(), getResolveInterval(), getResolveInterval());
        }
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
            log.warn("Error creating tool:", e);
            return null;
        }
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
            try {
                if (toolboxes.get(toolbox.getPath()) == null) {
                    toolbox.loadTools();
                    toolboxes.put(toolbox.getPath(), toolbox);
                }
            } catch (Exception e) {
                log.warn("Error adding toolbox:" + toolbox.getPath(), e);
            }
        }
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
                if (!f.exists()) {
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
        properties.setProperty(TrianaProperties.TOOLBOX_SEARCH_PATH_PROPERTY, toCSV());
        try {
            properties.saveProperties();
        } catch (IOException e) {
            System.err.println("WARNING: properties could not be saved to " + Locations.getDefaultConfigFile());
            e.printStackTrace();
        }
    }

    /**
     * converts the toolboxes into a CSV list
     *
     * @return
     */
    private String toCSV() {
        StringBuffer toolboxesstr = new StringBuffer();

        List<Toolbox> toolboxes = getToolboxes();
        for (int count = 0; count < toolboxes.size(); count++) {
            toolboxesstr.append(toolboxes.get(count).getPath());
            toolboxesstr.append(", ");
        }

        return toolboxesstr.toString();
    }

    /**
     * Creates a lilst of toolbox paths from a cvs representation
     *
     * @return
     */
    public String[] csvToStringArray(String csv) {
        String[] paths = csv.split(",");
        List<String> l = new ArrayList<String>();
        int i = 0;
        for (String path : paths) {
            path = path.trim();
            if (path.length() > 0) {
                l.add(path);
            }

        }
        return l.toArray(new String[l.size()]);
    }


    private String[] createDefaultToolboxes() {
        String defaultToolboxPaths = TrianaProperties.getDefaultConfiguration().getProperty(TrianaProperties.TOOLBOX_SEARCH_PATH_PROPERTY);
        return csvToStringArray(defaultToolboxPaths);
    }

    /**
     * load the toolboxes using the TrianaProperties.TOOLBOX_SEARCH_PATH_PROPERTY
     */
    public void loadToolboxes(List<String> extras) {


        String[] toolboxPaths;

        String toolboxPathsCSV = properties.getProperty(TrianaProperties.TOOLBOX_SEARCH_PATH_PROPERTY);

        if ((toolboxPathsCSV == null) || (toolboxPathsCSV.equals("")))
            toolboxPaths = createDefaultToolboxes();
        else
            toolboxPaths = csvToStringArray(toolboxPathsCSV);


        for (String toolboxPath : toolboxPaths) {
            // need test for remote ones here - todo with bonjour stuff ....  for now assume local
            Toolbox intern = new FileToolbox(toolboxPath, "local", "default-toolboxes", properties);
            addNewToolBox(intern);
        }
        if (extras != null) {
            for (String extra : extras) {
                Toolbox intern = new FileToolbox(extra, "local", "default-toolboxes", properties);
                addNewToolBox(intern);
            }
        }
    }

    public void shutdown() {
        if (timer != null) {
            timer.cancel();
        }
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
