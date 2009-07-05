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

import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.XMLReader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * Tool format handler. This handles the following cases:
 * <p/>
 * For xml tools:
 * 1. package/package/Tool.xml
 * 2. package/package/xml/Tool.xml
 * <p/>
 * For java tools:
 * 1. package/package/classes/package/package/Tool.class
 * 2. package/package/target/classes/package/package/Tool.class
 * <p/>
 * If the tool is in a jar file, it handles the above paths inside the jar file.
 * <p/>
 * This also handles jars that are excutable, i.e. a bog standard jar inwhich
 * the classes are top level entries:
 * <p/>
 * package/package/Tool.class
 * <p/>
 * A tool that is jarred up is considered not modifiable because it is considered
 * a distribution.
 * <p/>
 * A Java tool, even if the source is available in the tool, is also not modifiable.
 * <p/>
 * The file constructor is not complete at the moment in that is DOES NOT create Java tools.
 * This is because of dependency issues that are not resolved yet.
 * <p/>
 * Therefore, Java tools are loaded from elsewhere (currently JavaReader in ToolTableImp), and then passed to this in the Tool constructor.
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jul 3, 2009: 4:14:26 PM
 * @date $Date:$ modified by $Author:$
 */

public class FileToolFormatHandler implements ToolFormatHandler {

    private Map<String, ToolUrl> tools = new HashMap<String, ToolUrl>();
    private File file;
    private boolean isJar = false;
    private boolean isJava = false;
    private boolean isXml = false;
    private boolean exists = true;


    public FileToolFormatHandler(Tool tool) throws TaskException {

        String def = tool.getDefinitionPath();
        if (def == null) {
            exists = false;
        } else {
            file = new File(def);
            if (!file.exists() || file.length() == 0) {
                throw new TaskException("no file found for tool " + tool.getToolName());
            }
            isJar = false;
            if (file.getName().endsWith(".jar")) {
                try {
                    JarFile jf = new JarFile(file);
                } catch (IOException e) {
                    throw new TaskException("The file has an extension of '.jar' but the content does not comply with the zip specification.");
                }
                isJar = true;
            }
            if (isJar) {
                URL[] arr = initJar(tool);
                if (arr == null) {
                    throw new TaskException("unknown jar format " + tool.getToolName());
                }
                tools.put(createId(tool), new ToolUrl(tool, arr[0], arr[1], arr[2]));
            } else {
                URL[] arr = initFile(tool);
                if (arr == null) {
                    throw new TaskException("unknown directory structure " + tool.getToolName());
                }
                tools.put(createId(tool), new ToolUrl(tool, arr[0], arr[1], arr[2]));
            }
        }

    }

    public FileToolFormatHandler(File file, String toolbox) throws Exception {
        if (file.getName().endsWith(".xml")) {
            isXml = true;
            Tool tool = readXMLStream(new FileInputStream(file), file.getAbsolutePath(), toolbox);
            if (tool != null) {
                initTool(tool);
            }
        } else if (file.getName().endsWith(".jar")) {
            isJar = true;
            JarHelper helper = new JarHelper(file);
            List<String> potentials = helper.listEntries();
            for (String potential : potentials) {
                if (potential.endsWith(".xml")) {
                    InputStream in = helper.getStream(potential);
                    Tool tool = readXMLStream(in, file.getAbsolutePath(), toolbox);
                    if (tool != null) {
                        initTool(tool);
                    }
                } else if (potential.endsWith(".class")) {
                    // todo - need to load in all the class files in this jar.
                    // This will give us all the Unit descendents in this jar.
                    // then need resolve this class file against these potential
                    // superclasses.
                    // If this fails, need to resolve againts loaded classes.
                    // humph. This is tricky because... what happens with dependencies that are referenced
                    // i.e. via Ivy ot whatever... not in this jar - could be anywhere...
                }
            }
        }
    }


    private void initTool(Tool tool) {
        URL[] arr = initFile(tool);
        if (arr == null) {
            //what todo?
        } else {
            tools.put(createId(tool), new ToolUrl(tool, arr[0], arr[1], arr[2]));
        }
    }

    private Tool readXMLStream(InputStream in, String filePath, String toolbox) throws TaskGraphException, IOException {
        XMLReader reader = new XMLReader(new BufferedReader(new InputStreamReader(in)));
        Tool tool = reader.readComponent();
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

    private URL[] initJar(Tool tool) {
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
            isXml = true;
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
                isJava = true;
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

    private URL[] initFile(Tool tool) {

        File[] files = getRootFromDefinition(tool);
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


    private File[] getRootFromDefinition(Tool tool) {
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
            isXml = true;
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
            isJava = true;
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
                        defRoot = new File(root, pkg.replace(".", File.separator) + File.separator + "target" + File.separator + "classes");
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

    public List<Tool> getTools() {
        List<Tool> t = new ArrayList<Tool>();
        for (String tool : tools.keySet()) {
            t.add(tools.get(tool).getTool());
        }
        return t;
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
        return !isJar && !isJava;
    }

    private String createId(Tool tool) {
        return tool.getToolBox() + ":" + tool.getToolPackage() + ":" + tool.getToolName();
    }

    private class ToolUrl {
        private Tool tool;
        private URL root;
        private URL defRoot;
        private URL def;

        private ToolUrl(Tool tool, URL root, URL defRoot, URL def) {
            this.tool = tool;
            this.root = root;
            this.defRoot = defRoot;
            this.def = def;
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
    }

}
