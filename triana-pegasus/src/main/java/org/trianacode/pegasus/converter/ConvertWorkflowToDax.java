package org.trianacode.pegasus.converter;

import org.apache.commons.lang.ArrayUtils;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.addon.ConversionAddon;
import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoHandler;
import org.trianacode.enactment.io.IoMapping;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.pegasus.dax.FileUnit;
import org.trianacode.pegasus.execution.CommandLinePegasus;
import org.trianacode.pegasus.extras.DaxUtils;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.TaskFactoryImp;
import org.trianacode.taskgraph.imp.TaskImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.service.ClientException;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 28/10/2011
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class ConvertWorkflowToDax extends AbstractAction implements ActionDisplayOptions, ConversionAddon {

    public ConvertWorkflowToDax() {
        this(DISPLAY_BOTH);
    }

    public ConvertWorkflowToDax(int displayOption) {
        putValue(SHORT_DESCRIPTION, "Convert");
        putValue(NAME, "Convert to Dax");
        if ((displayOption == DISPLAY_ICON) || (displayOption == DISPLAY_BOTH)) {
            putValue(SMALL_ICON, GUIEnv.getIcon("cog.png"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        final ApplicationFrame frame = GUIEnv.getApplicationFrame();
        final TaskGraph tg = frame.getSelectedTaskgraph();
        if (tg == null || tg.getTasks(false).length == 0) {
            JOptionPane.showMessageDialog(frame, "No taskgraph selected," +
                    " or currently selected taskgraph has no tasks");
        } else {
            convert(tg, "untitledDax.xml", null);
        }
    }

    public void convert(final TaskGraph tg, final String daxFilePath, final File configFile) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    DaxifyTaskGraph converter = new DaxifyTaskGraph();
                    TaskGraph daxifiedTaskGraph = converter.convert(tg);
                    CommandLinePegasus.initTaskgraph(daxifiedTaskGraph, daxFilePath, false);

                    GUIEnv.getApplicationFrame().addParentTaskGraphPanel((TaskGraph) daxifiedTaskGraph);
                    try {
                        GUIEnv.getApplicationFrame().getSelectedTrianaClient().run();
                    } catch (ClientException e) {
                        e.printStackTrace();
                    }

                    int errorNumber = runDaxCreatorWorkflow(daxifiedTaskGraph, configFile);
                    System.out.println(errorNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.run();

    }

    private int runDaxCreatorWorkflow(Tool daxifiedTaskGraph, File configFile) throws IOException, TaskGraphException {
        addInputsToDaxifiedTaskGraph((TaskGraph) daxifiedTaskGraph, configFile);

        File file = saveTaskGraph((TaskGraph) daxifiedTaskGraph, daxifiedTaskGraph.getToolName());
        ArrayList<String> optionsStrings = new ArrayList<String>();
        optionsStrings.add("-n");
        optionsStrings.add("-w");
        optionsStrings.add(file.getAbsolutePath());

        if (daxifiedTaskGraph.getDataInputNodeCount() > 0) {
            configFile = DaxUtils.createDummyIOConfigFile((TaskGraph) daxifiedTaskGraph);
            optionsStrings.add("-d");
            optionsStrings.add(configFile.getAbsolutePath());
        }

        String[] args = new String[optionsStrings.size()];
        for (int i = 0; i < optionsStrings.size(); i++) {
            args[i] = optionsStrings.get(i);
        }

        System.out.println(ArrayUtils.toString(args));
        int errorNumber = Exec.exec(args);
        return errorNumber;
    }

    private void addInputsToDaxifiedTaskGraph(TaskGraph daxifiedTaskGraph, File conf) throws IOException, TaskGraphException {
        if (!conf.exists()) {
        } else {
            IoHandler handler = new IoHandler();
            IoConfiguration ioc = handler.deserialize(new FileInputStream(conf));

            for (IoMapping mapping : ioc.getInputs()) {
                System.out.println(mapping.getNodeName() + " has data "
                        + mapping.getIoType().getType()
                        + " : "
                        + mapping.getIoType().getValue()
                        + " reference : "
                        + mapping.getIoType().isReference()
                );
            }


            Node[] inputTaskNodes = new Node[daxifiedTaskGraph.getDataInputNodeCount()];
            for (int i = 0; i < daxifiedTaskGraph.getDataInputNodeCount(); i++) {
                Node taskgraphNode = daxifiedTaskGraph.getDataInputNode(i);
                System.out.println(taskgraphNode.getNodeIndex() +
                        " " + taskgraphNode.getName() +
                        " " + taskgraphNode.getTopLevelNode().getName() +
                        " " + taskgraphNode.getBottomLevelNode().getName()
                );

                IoMapping mapping = ioc.getInputs().get(i);
                if (!mapping.getIoType().isReference()) {

                    Node taskNode = taskgraphNode.getTopLevelNode();
                    inputTaskNodes[i] = taskNode;
                }
            }

            for (int j = 0; j < inputTaskNodes.length; j++) {
                if (j <= ioc.getInputs().size()) {
                    Node taskNode = inputTaskNodes[j];
                    taskNode.disconnect();
                    Task task = addFileUnit(null,
                            taskNode,
                            taskNode.getTask().getParent()
                    );

                    if (task != null) {
                        String url = ioc.getInputs().get(j).getIoType().getValue();
                        task.setParameter(FileUnit.FILE_URL, url);
                        task.setParameter(FileUnit.PHYSICAL_FILE, true);
                        System.out.println(task.getParameter(FileUnit.FILE_URL));
                    }
                }
            }
        }
    }

    private Task addFileUnit(Node sendnode, Node recnode, TaskGraph clone) throws CableException {
        Task fileTask = null;
        Task task = null;
        try {
            fileTask = new TaskImp(
                    AddonUtils.makeTool(FileUnit.class, "" + Math.random() * 100, clone.getProperties()),
                    new TaskFactoryImp(),
                    false
            );

            task = clone.createTask(fileTask, false);

            if (sendnode != null) {
                Node inNode = task.addDataInputNode();
                clone.connect(sendnode, inNode);
            }

            Node outNode = task.addDataOutputNode();
            clone.connect(outNode, recnode);
        } catch (TaskException e) {
            e.printStackTrace();
        } catch (ProxyInstantiationException e) {
            e.printStackTrace();
        }
        return task;
    }

    private File saveTaskGraph(TaskGraph daxifiedTaskGraph, String tempFilePath) {
        File file = null;
        try {
            file = File.createTempFile(tempFilePath, ".txt");
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
            XMLWriter writer = new XMLWriter(fileWriter);
            writer.writeComponent(daxifiedTaskGraph);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public Object toolToWorkflow(Tool tool) {
        return null;
    }

    @Override
    public Tool workflowToTool(Object workflowObject) {
        return null;
    }

    @Override
    public Tool processWorkflow(Tool workflow) {
        return null;
    }

    @Override
    public File toolToWorkflowFile(Tool tool, File configFile, String filePath) throws Exception {
        CommandLinePegasus.initTaskgraph((TaskGraph) tool, filePath, false);
        runDaxCreatorWorkflow(tool, configFile);
        return new File(filePath);
    }

    @Override
    public String getServiceName() {
        return "daxConverter";
    }

    @Override
    public String getLongOption() {
        return "convert-dax";
    }

    @Override
    public String getShortOption() {
        return "dax";
    }

    @Override
    public String getDescription() {
        return "Converts a taskgraph formed of DaxFiles and DaxJobs to a Pegasus .dax file";
    }
}
