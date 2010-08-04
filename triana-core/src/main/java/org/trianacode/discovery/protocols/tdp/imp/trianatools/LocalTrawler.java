package org.trianacode.discovery.protocols.tdp.imp.trianatools;

import org.thinginitself.http.HttpPeer;
import org.trianacode.discovery.protocols.tdp.TDPRequest;
import org.trianacode.discovery.protocols.tdp.TDPResponse;
import org.trianacode.discovery.protocols.tdp.TDPServer;
import org.trianacode.discovery.toolinfo.ToolMetadata;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.DocumentHandler;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.*;
import org.trianacode.taskgraph.tool.creators.type.ClassHierarchy;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.taskgraph.util.Home;
import org.w3c.dom.Element;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Searches for local Triana tools on this instance of Triana and exposes them to
 * the network.
 *
 * User: scmijt
 * Date: Jul 30, 2010
 * Time: 2:57:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocalTrawler extends TDPServer {

    ArrayList<ToolMetadata> tools = new ArrayList<ToolMetadata>();

    static Logger log = Logger.getLogger("org.trianacode.discovery.protocols.tdp.imp.trianatools.LocalTrawler");

    private Map<String, Set<String>> shared = new HashMap<String, Set<String>>();
    protected Map<String, Toolbox> toolboxes = new ConcurrentHashMap<String, Toolbox>();

    public static String[] excludedDirectories = {"CVS", "src", "lib"};

    public LocalTrawler(HttpPeer httpPeer) {
        super(httpPeer);
    }

    @Override
    public TDPResponse handleRequest(TDPRequest request) {
        if (request.getRequest()==TDPRequest.Request.GET_TOOLS_LIST) {
            loadToolboxes();
            addTools();

        }
        
        return new TDPResponse(tools);
    }

    public String getServiceName() {
        return "TrianaService";
    }


      public void loadToolboxes() {
        File file = new File(Home.home() + File.separator + "toolboxes.xml");
        if (!file.exists() || file.length() == 0) {
            File defToolbox = new File(Home.home() + File.separator + "toolbox");
            defToolbox.mkdirs();
        }
        file = new File(Home.home() + File.separator + "toolboxes.xml");
        List<Toolbox> tbs = new ArrayList<Toolbox>();
        BufferedReader br = null;
        try {
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
                if (name == null) {
                    name = "noname";
                }
                boolean v = virtual != null && virtual.equals("true");
                if (!v) {
                    if (!new File(toolbox).exists()) {
                        log.severe("Error: Toolbox " + toolbox + " doesn't exists removing from config");
                    } else {
                        if (type != null && type.length() > 0) {
                            tbs.add(new Toolbox(toolbox, type, v));
                        } else {
                            tbs.add(new Toolbox(toolbox, name, v));
                        }
                    }
                }
            }
            if (tbs.size() > 0) {
                addToolBox(tbs.toArray(new Toolbox[tbs.size()]));
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


    /**
     * Add a tool box path to the current tool boxes
     */
    public void addToToolBox(Toolbox... boxes) {
        for (Toolbox box : boxes) {
            if (toolboxes.get(box.getPath()) == null) {
                toolboxes.put(box.getPath(), box);
            }
        }
    }


       /**
     * Add a tool box path to the current tool boxes This also gets the tool class loader to find paths within the tool
     * box to add to its classpath And gets the types map to parse all the class files and jar files and categorize them
     * according to their classes,superclass and interfaces.
     */
    public void addToolBox(Toolbox... box) {
        addToToolBox(box);
        for (Toolbox toolbox : box) {
            if (!toolbox.isVirtual()) {
                File f = new File(toolbox.getPath());
                f.mkdirs();
                ToolClassLoader.getLoader().addToolBox(toolbox.getPath());
                if (f.exists() && f.length() > 0) {
                    try {
                        TypesMap.load(f);
                    } catch (IOException e) {
                        log.warning("Error loading types map: " + FileUtils.formatThrowable(e));
                    }
                }
            }
        }
    }

    protected void addTools() {
        for (String s : toolboxes.keySet()) {
            Toolbox tb = toolboxes.get(s);
            if (!tb.isVirtual()) {
                List<File> files = find(new File(s), new String[]{".xml", ".jar", ".class"}, excludedDirectories);
                for (int i = 0; i < files.size(); i++) {
                    addTools(files.get(i), s);
                }
            }
        }
    }

    private List<File> find(File f, final String[] exts, final String[] extDirs) {
        ArrayList<File> files = new ArrayList<File>();
        if (f.getName().startsWith(".")) {
            return files;
        }
        if (f.isDirectory()) {
            File[] fs = f.listFiles(new FilenameFilter() {
                public boolean accept(File file, String s) {
                    if (file.isDirectory()) {
                        for (String extDir : extDirs) {
                            if (file.getName().equals(extDir)) {
                                return false;
                            }
                        }
                        return true;
                    } else {
                        for (String ext : exts) {
                            if (file.getName().endsWith(ext)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
            for (File file : fs) {
                files.addAll(find(file, exts, extDirs));
            }
        } else {
            files.add(f);
        }
        return files;
    }

       /**
     * Add a tool to the tool tables that is located at the specified location
     *
     * @param toolFile the tool file
     * @param toolbox  toolbox to which the tool is to be added
     */
    protected List<Tool> addTools(File toolFile, String toolbox) {
        if ((!toolFile.exists()) || toolFile.isDirectory()) {
            log.fine("dumping file..." + toolFile);

            return null;
        }
        List<ToolFormatHandler.ToolStatus> stats = null;
        try {
            stats = add(toolFile, toolbox);
        } catch (ToolException e) {
            //notifyToolRemoved(stats.getTool());

        }
        List<Tool> ret = new ArrayList<Tool>();
        if (stats != null) {
            for (ToolFormatHandler.ToolStatus stat : stats) {
                log.fine("ToolTableImp.addTool stats:" + stat.getStatus());
                if (stat.getStatus() == ToolFormatHandler.ToolStatus.Status.NOT_MODIFIED) {
                    continue;
                } 
                ret.add(stat.getTool());
            }

        }
        return ret;
    }

    public List<ToolFormatHandler.ToolStatus> add(File file, String toolbox) throws ToolException {
        log.fine("FileToolFormatHandler.add file:" + file.getAbsolutePath());
        List<ToolFormatHandler.ToolStatus> ret = new ArrayList<ToolFormatHandler.ToolStatus>();
        try {
            if (file.getName().endsWith(".xml")) {
                log.fine("file is XML:" + file.getAbsolutePath());
                Tool tool = readXMLStream(new FileInputStream(file), file.getAbsolutePath(), toolbox);
                if (tool != null) {
                    tools.add(new ToolMetadata(tool.getToolName(),tool.getDisplayName(),new URL(file.toString()),null));
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
                        if (tool != null) {
                            tools.add(new ToolMetadata(tool.getToolName(),tool.getDisplayName(),new URL(file.toString()),null));
                        }
                    } else if (potential.endsWith(".class")) {
                        URL url = createJarURL(file, potential);
                        log.fine("FileToolFormatHandler.add jar url:" + url.toString());
                        if (url != null) {
                            ClassHierarchy ch = TypesMap.isType(url.toString(), Unit.class.getName());
                            if (ch == null) {
                                ch = TypesMap.getAnnotated(file.getAbsolutePath());
                            }
                            if (ch != null) {
                                Tool tool = read(ch.getName(), toolbox, ch.getFile());
                                if (tool != null ) {
                                    tools.add(new ToolMetadata(tool.getToolName(),tool.getDisplayName(),new URL(file.toString()),null));
                                }
                            }
                        }

                    }
                }
            } else if (file.getName().endsWith(".class")) {
                log.fine("file is Java:" + file.getAbsolutePath());
                ClassHierarchy ch = TypesMap.isType(file.getAbsolutePath(), Unit.class.getName());
                log.fine("FileToolFormatHandler.add class hierarchy:" + ch);
                if (ch == null) {
                    ch = TypesMap.getAnnotated(file.getAbsolutePath());
                }
                log.fine("FileToolFormatHandler.add class hierarchy after trying annotations:" + ch);

                if (ch != null) {
                    Tool tool = read(ch.getName(), toolbox, ch.getFile());
                    if (tool != null) {
                        tools.add(new ToolMetadata(tool.getToolName(),tool.getDisplayName(),new URL(file.toString()),null));
                    }
                }
            }
        } catch (IOException e) {
            throw new ToolException(e.getMessage());
        }
        return ret;
    }

    public void clear() {
        tools.clear();
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

    private Tool readXMLStream(InputStream in, String filePath, String toolbox) throws ToolException {
        XMLReader reader = null;
        try {
            reader = new XMLReader(new BufferedReader(new InputStreamReader(in)));
        } catch (IOException e) {
            log.fine("error reading xml file " + e.getMessage());
            return null;
        }
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

}
    
