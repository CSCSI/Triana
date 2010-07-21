package org.trianacode.taskgraph.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.trianacode.taskgraph.ser.DocumentHandler;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.taskgraph.tool.Toolbox;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 21, 2010
 */

public class Toolboxes {

    private static Logger log = Logger.getLogger(Toolboxes.class.getName());

    public static void saveToolboxes(ToolTable tools) throws IOException {
        File file = new File(Home.home() + File.separator + "toolboxes.xml");
        PrintWriter bw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        DocumentHandler handler = new DocumentHandler();

        Element root = handler.element("toolboxes");
        handler.setRoot(root);


        Toolbox[] toolboxes = tools.getToolBoxes();
        for (int count = 0; count < toolboxes.length; count++) {
            Element toolbox = handler.element("toolbox");
            handler.add(toolboxes[count].getPath(), toolbox);

            toolbox.setAttribute("type", toolboxes[count].getType());
            toolbox.setAttribute("virtual", toolboxes[count].isVirtual() + "");
            toolbox.setAttribute("name", toolboxes[count].getName());
            handler.add(toolbox, root);
        }
        handler.output(bw, true);
    }

    public static void loadToolboxes(ToolTable table) {
        File file = new File(Home.home() + File.separator + "toolboxes.xml");
        if (!file.exists() || file.length() == 0) {
            File defToolbox = new File(Home.home() + File.separator + "toolbox");
            defToolbox.mkdirs();
            table.addToolBox(new Toolbox(defToolbox.getAbsolutePath(), Toolbox.INTERNAL, "user"));
            try {
                saveToolboxes(table);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        List<Toolbox> tbs = new ArrayList<Toolbox>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            DocumentHandler handler = new DocumentHandler(br);

            Element root = handler.root();

            if (!root.getLocalName().equals("toolboxes")) {
                throw (new Exception("Corrupt config file: " + file.getAbsolutePath()));
            }

            List elementList = handler.getChildren(handler.getChild(root, "toolbox"));
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
                if (!v && !new File(toolbox).exists()) {
                    log.severe("Error: Toolbox " + toolbox + " doesn't exists removing from config");
                } else {
                    if (type != null && type.length() > 0) {
                        tbs.add(new Toolbox(toolbox, type, v));
                    } else {
                        tbs.add(new Toolbox(toolbox, name, v));
                    }
                }
            }
            if (tbs.size() > 0) {
                table.addToolBox(tbs.toArray(new Toolbox[tbs.size()]));
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
}
