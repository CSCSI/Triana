package org.trianacode.shiwa.iwir.importer;

import org.shiwa.fgi.iwir.AbstractTask;
import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.config.TrianaProperties;
import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphImporterInterface;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.organize.DaxOrganize;
import org.trianacode.shiwa.iwir.tasks.TaskHolder;
import org.trianacode.shiwa.iwir.tasks.TaskHolderFactory;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.filechooser.FileFilter;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 07/04/2011
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
public class IwirReader extends AbstractFormatFilter implements TaskGraphImporterInterface {

    public IwirReader() {

    }

    @Override
    public String getFilterDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FileFilter[] getChoosableFileFilters() {
        return new FileFilter[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FileFilter getDefaultFileFilter() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasOptions() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int showOptionsDialog(Component parent) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toString() {
        return "IwirReader";
    }



    @Override
    public TaskGraph importWorkflow(File file, TrianaProperties properties) throws TaskGraphException, IOException {

        importIWIR(file, properties);

        return null;
    }



    private TaskGraph importIWIR(File file, TrianaProperties properties) throws FileNotFoundException, TaskException {
        IWIR iwir = new IWIR(file);

        AbstractTask rootTask = iwir.getTask();
        List<AbstractTask> rootTaskChildren = rootTask.getChildren();


        TaskGraph taskGraph = TaskGraphManager.createTaskGraph();
        for (AbstractTask abstractTask : rootTaskChildren) {
            TaskHolder taskHolder = TaskHolderFactory.getTaskHolderFactory().getTaskHolder(abstractTask);

            ToolImp tool = new ToolImp(properties);
            tool.setToolName(taskHolder.getIWIRTask().getName());
            tool.setDataInputNodeCount(taskHolder.getIWIRTask().getInputPorts().size());
            tool.setDataOutputNodeCount(taskHolder.getIWIRTask().getOutputPorts().size());
            taskGraph.createTask(tool);
        }

        return null;
    }



    private TaskGraph parseTool(File file) {
        XMLReader reader;
        Tool tool = null;
        if (file.exists()) {
            try {
                BufferedReader filereader = new BufferedReader(new FileReader(file));
                reader = new XMLReader(filereader);
                System.out.println("Reading tool from file : " + file.getCanonicalPath());
                tool = reader.readComponent(GUIEnv.getApplicationFrame().getEngine().getProperties());

            } catch (IOException e) {
                System.out.println(file + " : not found");
            } catch (TaskGraphException e) {
                e.printStackTrace();
            }
        }
        if (tool instanceof TaskGraph) {
            TaskGraph tg = (TaskGraph) tool;
            DaxOrganize daxOrganize = new DaxOrganize(tg);
            return tg;
        } else {
            return null;
        }
    }
    private TaskGraph importUsingXSLT(File file, TrianaProperties properties) throws IOException {
        String root = "triana-shiwa/src/main/java/org/trianacode/shiwa/xslt/iwir/";

        if (file.exists() && file.canRead()) {
            String iwirPath = file.getAbsolutePath();
            String removeNamespacePath = root + "removeNamespace.xsl";
            String iwirTaskgraphTransformerPath = root + "iwir.xsl";
            String tempFileName = file.getName() + "-outputTemp.xml";
            String taskgraphFileName = file.getName() + "-taskgraph";

            File removeNamespace = new File(removeNamespacePath);
            File iwirTaskgraphTransformer = new File(iwirTaskgraphTransformerPath);

            if (removeNamespace.exists() && iwirTaskgraphTransformer.exists()) {

                xsltTransformer.doTransform(iwirPath, tempFileName, removeNamespacePath);
                System.out.println("Stripped namespace");

                xsltTransformer.doTransform(tempFileName, taskgraphFileName + ".xml", iwirTaskgraphTransformerPath);
                System.out.println("Created taskgraph file " + taskgraphFileName + ".xml");

                return parseTool(new File(taskgraphFileName + ".xml"));
            } else {
                System.out.println("Transform file not available. Attempting to use file from classloader");


                StreamSource iwirFile = new StreamSource(file);
                InputStream removeNamespaceTransformerInputStream = this.getClass().getResourceAsStream("/removeNamespace.xsl");
                StreamSource removeNamespaceTransformerSource = new StreamSource(removeNamespaceTransformerInputStream);
                InputStream transformerInputStream = this.getClass().getResourceAsStream("/iwir.xsl");
                StreamSource transformerSource = new StreamSource(transformerInputStream);

                if (removeNamespaceTransformerInputStream == null && transformerInputStream == null) {

                    System.out.println("Could not read from xslt transformer sources.");
                } else {

                    File removedNamespaceFile = File.createTempFile(taskgraphFileName + "sansNamespace", ".xml");
                    StreamResult streamResult = new StreamResult(removeNamespacePath);
                    xsltTransformer.doTransform(iwirFile, removeNamespaceTransformerSource, streamResult);
                    System.out.println("Created namespace-less file : " + removeNamespacePath);

                    StreamSource removedNamespaceSource = new StreamSource(removedNamespaceFile);
                    File taskgraphTempFile = File.createTempFile(taskgraphFileName, ".xml");
                    StreamResult taskgraphStreamResult = new StreamResult(taskgraphTempFile);
                    xsltTransformer.doTransform(removedNamespaceSource, transformerSource, taskgraphStreamResult);
                    System.out.println("Created taskgraph from iwir : " + taskgraphFileName + ".xml");

                    return parseTool(taskgraphTempFile);
                }
            }
        }
        return null;
    }
}
