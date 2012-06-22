package org.trianacode.shiwa.handler;

import org.apache.commons.logging.Log;
import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.description.workflow.Author;
import org.shiwa.desktop.data.transfer.WorkflowEngineHandler;
import org.shiwa.desktop.gui.SHIWADesktop;
import org.trianacode.TrianaInstance;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 24/02/2011
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
public class TrianaEngineHandler implements WorkflowEngineHandler {

    private static Log devLog = Loggers.DEV_LOGGER;

    private Task task;
    private InputStream displayImage;
    private TrianaInstance trianaInstance;

    public TrianaEngineHandler(Task task, TrianaInstance trianaInstance, InputStream displayImage) {
        this.task = task;
        this.trianaInstance = trianaInstance;
        this.displayImage = displayImage;
    }

    @Override
    public String getEngineName(Set<String> engines) {
        return "Triana";
    }

    @Override
    public String getEngineVersion() {
        return (String) task.getProperties().get(TrianaProperties.VERSION);
    }

    @Override
    public String getWorkflowLanguage(Set<String> languages) {
        return "triana-taskgraph";
    }

    @Override
    public TransferSignature getSignature() {
        TransferSignature signature = new TransferSignature();
        signature.setName(task.getToolName());
        setInputPorts(signature);
        setOutputPorts(signature);

        return signature;
    }

    private void setOutputPorts(TransferSignature signature) {
        for (int i = 0; i < task.getDataOutputNodeCount(); i++) {
            String s = task.getDataOutputTypes(i)[0];
            signature.addOutput("OutputPort_" + i, ObjectToSchema.getSchemaURIString(s));
        }
    }

    private void setInputPorts(TransferSignature signature) {
        for (int i = 0; i < task.getDataInputNodeCount(); i++) {
            String s = task.getDataInputTypes(i)[0];
            signature.addInput("InputPort_" + i, s);
        }
    }

    @Override
    public InputStream getDefinition() {
        try {
            File temp = File.createTempFile("publishedTaskgraphTemp", ".xml");
            temp.deleteOnExit();
            XMLWriter writer = new XMLWriter(new BufferedWriter(new FileWriter(temp)));
            writer.writeComponent(task);
            writer.close();
            devLog.debug("Sending taskgraph to ShiwaDesktop.");
            return new FileInputStream(temp);

        } catch (Exception e) {
            devLog.debug("Failed to create temp xml for output to shiwa-desktop : ");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDefinitionName() {
        return task.getQualifiedTaskName();
    }

    @Override
    public InputStream getDisplayImage() {
        devLog.debug("Sending display image to ShiwaDesktop");
        return displayImage;
    }

    @Override
    public String getDisplayImageName() {
        return task.getQualifiedTaskName() + "-image.jpg";
    }

    @Override
    public List<Author> getAuthors() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    public static void main(String[] args) throws IOException, TaskGraphException {
        String wf = args[0];
        File f = new File(wf);

        if (!f.exists()) {
            devLog.debug("Cannot find workflow file:" + wf);
            System.exit(1);
        }

        String[] engineArgs = new String[args.length - 1];
        System.arraycopy(args, 1, engineArgs, 0, args.length - 1);

        TrianaInstance engine = new TrianaInstance(engineArgs);
        engine.init();
        XMLReader reader = new XMLReader(new FileReader(f));
        Tool tool = reader.readComponent(engine.getProperties());

//        JPanel jPanel = new SHIWADesktopPanel(
//                new TrianaEngineHandler((Task) tool, engine, null),
//                SHIWADesktopPanel.ButtonOption.SHOW_TOOLBAR
//        );
        SHIWADesktop shiwaDesktop = new SHIWADesktop(
                new TrianaEngineHandler((Task) tool, engine, null),
                SHIWADesktop.ButtonOption.SHOW_TOOLBAR
        );
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(shiwaDesktop.getPanel());
        jFrame.pack();
        jFrame.setVisible(true);
    }


}
