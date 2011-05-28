package org.trianacode.taskgraph.tool;

import org.apache.commons.logging.Log;
import org.trianacode.config.TrianaProperties;
import org.trianacode.discovery.ResolverRegistry;
import org.trianacode.discovery.ToolMetadataResolver;
import org.trianacode.discovery.toolinfo.ToolMetadata;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.util.ToolUtils;
import org.trianacode.taskgraph.util.UrlUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 9, 2010
 */

public class ToolResolver implements ToolMetadataResolver {


    private static Log log = Loggers.TOOL_LOGGER;

    private long resolveInterval = 10 * 1000;
    private Timer timer = new Timer();
    private boolean initialResolved = false;

    private Map<String, Toolbox> toolboxes = new ConcurrentHashMap<String, Toolbox>();
    private List<ToolListener> listeners = new ArrayList<ToolListener>();

    private TrianaProperties properties;
    private boolean suppressDefaultToolboxes = false;

    public ToolResolver(TrianaProperties properties, boolean suppressDefaultToolboxes) {
        this.properties = properties;
        this.suppressDefaultToolboxes = suppressDefaultToolboxes;
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
        if (toolbox == null) {
            return;
        }
        try {
            addNewToolBox(toolbox);
            resolve(toolbox);
            saveToolboxes();
            for (ToolListener listener : listeners) {
                listener.toolBoxAdded(toolbox);
            }
        } catch (Exception e) {
            log.warn("error adding toolbox", e);
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
        toolboxes.remove(toolbox.getPath());
        saveToolboxes();
        for (ToolListener listener : listeners) {
            listener.toolBoxRemoved(toolbox);
        }

    }

    public void removeTool(Tool tool) {
        Toolbox toolbox = tool.getToolBox();
        if (toolbox == null) {
            return;
        }

        Tool match = toolbox.removeTool(tool.getQualifiedToolName());
        if (match != null) {
            for (ToolListener listener : listeners) {
                listener.toolRemoved(tool);
            }
        }
    }

    public void addTool(Tool tool, Toolbox toolbox) {
        if (toolbox == null) {
            return;
        }
        toolbox.addTool(tool);
        for (ToolListener listener : listeners) {
            listener.toolAdded(tool);
        }
    }

    public void addTools(List<Tool> tools, Toolbox toolbox) {
        if (toolbox == null) {
            return;
        }
        for (Tool tool : tools) {
            toolbox.addTool(tool);
        }
        for (ToolListener listener : listeners) {
            listener.toolsAdded(tools);
        }
    }

    public void deleteTool(Tool tool) {
        Toolbox toolbox = tool.getToolBox();
        if (toolbox == null) {
            return;
        }

        Tool match = toolbox.deleteTool(tool.getQualifiedToolName());
        if (match != null) {
            for (ToolListener listener : listeners) {
                listener.toolRemoved(tool);
            }
        }
    }

    public List<Tool> getTools() {
        List<Tool> ret = new ArrayList<Tool>();
        for (Toolbox toolbox : toolboxes.values()) {
            ret.addAll(toolbox.getTools());
        }
        return ret;
    }

    public List<ToolMetadata> getToolMetadata() {
        List<ToolMetadata> ret = new ArrayList<ToolMetadata>();
        for (Toolbox toolbox : toolboxes.values()) {
            List<Tool> tools = toolbox.getTools();
            for (Tool tool : tools) {
                ret.add(createMetadata(tool));
            }
        }
        return ret;
    }

    public List<String> getToolNames() {
        List<String> ret = new ArrayList<String>();
        for (Toolbox toolbox : toolboxes.values()) {
            List<Tool> tts = toolbox.getTools();
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
        for (Toolbox toolbox : toolboxes.values()) {
            List<Tool> shared = toolbox.getTools(definition);
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
        for (Toolbox toolbox : toolboxes.values()) {
            String path = toolbox.getPath();
            if (UrlUtils.getExistingFile(path) != null) {
                ret.addAll(toolbox.getTools());
            }
        }
        return ret;
    }

    public List<ToolMetadata> getLocalToolMetadata() {
        List<ToolMetadata> ret = new ArrayList<ToolMetadata>();
        for (Toolbox toolbox : toolboxes.values()) {
            String path = toolbox.getPath();
            if (UrlUtils.getExistingFile(path) != null) {
                List<Tool> tools = toolbox.getTools();
                for (Tool tool : tools) {
                    ret.add(createMetadata(tool));
                }
            }
        }
        return ret;
    }

    public List<Tool> getTools(String toolbox) {
        Toolbox tt = this.toolboxes.get(toolbox);
        if (tt != null) {
            return tt.getTools();
        }
        return new ArrayList<Tool>();
    }

    public List<ToolMetadata> getToolMetadata(String toolbox) {
        List<ToolMetadata> ret = new ArrayList<ToolMetadata>();
        Toolbox tt = this.toolboxes.get(toolbox);
        if (tt != null) {
            List<Tool> tools = tt.getTools();
            for (Tool tool : tools) {
                ret.add(createMetadata(tool));
            }
        }
        return ret;
    }

    public Tool getTool(String fullname) {
        for (Toolbox tt : toolboxes.values()) {
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

    public void setToolboxName(Toolbox toolbox, String name) {
        if (name != null && name.length() > 0 && !name.equals(toolbox.getName())) {
            for (ToolListener listener : listeners) {
                listener.toolboxNameChanging(toolbox, name);
            }
            toolbox.setName(name);
            saveToolboxes();
            for (ToolListener listener : listeners) {
                listener.toolboxNameChanged(toolbox, name);
            }
        }
    }

    public List<Toolbox> getToolboxes(String type) {
        List<Toolbox> ret = new ArrayList<Toolbox>();
        for (Toolbox toolbox : toolboxes.values()) {
            if (toolbox.getType().equals(type)) {
                ret.add(toolbox);
            }
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
        try {
            loadToolboxes(extraToolboxes);
            reresolve();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initialResolved = true;
        if (reresolve) {
            timer.scheduleAtFixedRate(new ResolveThread(), getResolveInterval(), getResolveInterval());
        }
    }


    private void reresolve() throws ProxyInstantiationException, TaskException {
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
     * <p/>
     * No exceptions, just a warning ....   need to propagate these though.
     *
     * @param metadata
     * @return
     */
    private Tool createTool(ToolMetadata metadata) throws ProxyInstantiationException, TaskException {
        if (!metadata.isTaskgraph()) {
            ToolImp tool = new ToolImp(getProperties());
            tool.setDefinitionType(Tool.DEFINITION_METADATA);
            tool.setToolName(ToolUtils.getClassName(metadata.getToolName()));
            tool.setToolPackage(ToolUtils.getPackageName(metadata.getToolName()));

            String cls = metadata.getUnitWrapper();
            if (cls == null) {
                tool.setProxy(new JavaProxy(tool.getToolName(), tool.getToolPackage()));
            } else {
                tool.setProxy(new JavaProxy(ToolUtils.getClassName(cls), ToolUtils.getPackageName(cls)));
            }
            return tool;
        } else {
            TaskGraph taskgraph = TaskGraphManager.createTaskGraph();
            taskgraph.setDefinitionType(Tool.DEFINITION_METADATA);
            taskgraph.setToolName(ToolUtils.getClassName(metadata.getToolName()));
            taskgraph.setToolPackage(ToolUtils.getPackageName(metadata.getToolName()));

            return taskgraph;
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


    protected void addNewToolBox(Toolbox... box) throws Exception {
        for (Toolbox toolbox : box) {
            if (toolboxes.get(toolbox.getPath()) == null) {
                if (toolbox.getProperties() == null) {
                    toolbox.setProperties(getProperties());
                }
                toolbox.loadTools();
                toolboxes.put(toolbox.getPath(), toolbox);
            }

        }
    }

    private void saveToolboxes() {
        properties.setProperty(TrianaProperties.TOOLBOX_SEARCH_PATH_PROPERTY, toCSV());
        try {
            properties.saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * converts the toolboxes into a CSV list
     *
     * @return
     */
    private String toCSV() {
        StringBuilder toolboxesstr = new StringBuilder();

        List<Toolbox> toolboxes = getToolboxes();
        if (toolboxes != null && toolboxes.size() > 0) {
            for (int count = 0; count < toolboxes.size() - 1; count++) {
                toolboxesstr.append("{").append(toolboxes.get(count).getType()).append("}");
                if (toolboxes.get(count).getName() != null) {
                    toolboxesstr.append("{").append(toolboxes.get(count).getName()).append("}");
                }
                toolboxesstr.append(toolboxes.get(count).getPath());
                toolboxesstr.append(", ");
            }
            toolboxesstr.append("{").append(toolboxes.get(toolboxes.size() - 1).getType()).append("}");
            if (toolboxes.get(toolboxes.size() - 1).getName() != null) {
                toolboxesstr.append("{").append(toolboxes.get(toolboxes.size() - 1).getName()).append("}");
            }
            toolboxesstr.append(toolboxes.get(toolboxes.size() - 1).getPath());
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
    public void loadToolboxes(List<String> extras) throws Exception {

        String[] toolboxPaths = new String[0];

        String toolboxPathsCSV = properties.getProperty(TrianaProperties.TOOLBOX_SEARCH_PATH_PROPERTY);

        if(!suppressDefaultToolboxes){
            if ((toolboxPathsCSV == null) || (toolboxPathsCSV.equals(""))){
                toolboxPaths = createDefaultToolboxes();
            }else{
                toolboxPaths = csvToStringArray(toolboxPathsCSV);
            }
        }

        for (String toolboxPath : toolboxPaths) {
            Toolbox t = createToolbox(toolboxPath);
            if (t != null) {
                addToolbox(t);
            }
        }
        if (extras != null) {
            for (String extra : extras) {
                Toolbox t = createToolbox(extra);
                if (t != null) {
                    addNewToolBox(t);
                }
            }
        }
    }

    protected Toolbox createToolbox(String path) {
        String type = FileToolboxLoader.LOCAL_TYPE;
        String loc = path;
        String name = null;
        if (path.startsWith("{")) {
            int curlyEnd = path.indexOf("}");
            type = path.substring(1, curlyEnd).trim();
            loc = path.substring(curlyEnd + 1, path.length()).trim();
            if (loc.startsWith("{")) {
                int nameCurlyEnd = loc.indexOf("}");
                if (nameCurlyEnd > -1) {
                    name = loc.substring(1, nameCurlyEnd).trim();
                    loc = loc.substring(nameCurlyEnd + 1, loc.length());
                }
            }
        }

        ToolboxLoader loader = ToolboxLoaderRegistry.getLoader(type);
        if (loader != null) {
            return loader.loadToolbox(loc, name, properties);
        }
        return null;
    }


    public void shutdown() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Where should these errors go ???
     */
    private class ResolveThread extends TimerTask {
        public void run() {
            try {
                reresolve();
            } catch (ProxyInstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (TaskException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

}
