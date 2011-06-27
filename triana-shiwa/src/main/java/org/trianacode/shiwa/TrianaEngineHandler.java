package org.trianacode.shiwa;

import org.shiwa.desktop.data.description.handler.Port;
import org.shiwa.desktop.data.description.handler.Signature;
import org.shiwa.desktop.data.transfer.WorkflowEngineHandler;
import org.shiwa.desktop.gui.SHIWADesktopPanel;
import org.trianacode.TrianaInstance;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoMapping;
import org.trianacode.enactment.io.IoType;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 24/02/2011
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
public class TrianaEngineHandler implements WorkflowEngineHandler {

    private Task task;
    private Signature loadedSignature;

    public TrianaEngineHandler(Task task) {
        this.task = task;
    }

    @Override
    public String getEngineName(Set<String> engines) {
        return "triana";
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
    public Signature getSignature() {
        Signature signature = new Signature();
        signature.setName(task.getToolName());
        setInputPorts(signature);
        setOutputPorts(signature);

        setLoadedSignature(signature);
        return signature;
    }

    private void setOutputPorts(Signature signature) {
        for (int i = 0; i < task.getDataOutputNodeCount(); i++) {
            String s = task.getDataOutputTypes(i)[0];
            signature.addOutput("OutputPort_" + i, ObjectToSchema.getSchemaURIString(s));
        }
    }

    private void setInputPorts(Signature signature) {
        for (int i = 0; i < task.getDataInputNodeCount(); i++) {
            String s = task.getDataInputTypes(i)[0];
            signature.addInput("InputPort_" + i, s);
        }
    }

    @Override
    public InputStream getWorkflowDefinition() {
        try {
            File temp = File.createTempFile("publishedTaskgraphTemp", ".xml");
            temp.deleteOnExit();
            XMLWriter writer = new XMLWriter(new BufferedWriter(new FileWriter(temp)));
            writer.writeComponent(task);
            writer.close();
            System.out.println("Sending temp file inputstream");
            return new FileInputStream(temp);

        } catch (Exception e) {
            System.out.println("Failed to create temp xml file for output to shiwa-desktop : ");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getWorkflowDefinitionName() {
        return task.getQualifiedTaskName();
    }

    public static void main(String[] args) throws IOException, TaskGraphException {
        String wf = args[0];
        File f = new File(wf);

        if (!f.exists()) {
            System.out.println("Cannot find workflow file:" + wf);
            System.exit(1);
        }

        String[] engineArgs = new String[args.length - 1];
        System.arraycopy(args, 1, engineArgs, 0, args.length - 1);

        TrianaInstance engine = new TrianaInstance(engineArgs);
        engine.init();
        XMLReader reader = new XMLReader(new FileReader(f));
        Tool tool = reader.readComponent(engine.getProperties());

        JPanel jPanel = new SHIWADesktopPanel(new TrianaEngineHandler((Task) tool));
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(jPanel);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public void setLoadedSignature(Signature signature) {
        this.loadedSignature = signature;
        ArrayList<IoMapping> inputMappings = new ArrayList<IoMapping>();


        List<Port> inputPorts = signature.getInputs();
        for (Port inputPort : inputPorts) {
            if (inputPort.getValue() != null) {
                String portName = inputPort.getName();
                String portNumberString = portName.substring(portName.indexOf("_") + 1);

                String value = inputPort.getValue();
                boolean reference = inputPort.isReference();


                IoMapping ioMapping = new IoMapping(new IoType(value, "String", reference), portNumberString);
                inputMappings.add(ioMapping);
            }
        }

        IoConfiguration conf = new IoConfiguration(signature.getName(), "0.1", inputMappings, new ArrayList<IoMapping>());

        List<IoMapping> mappings = conf.getInputs();
        for (IoMapping mapping : mappings) {
            System.out.println("  mapping:");
            System.out.println("    name:" + mapping.getNodeName());
            System.out.println("    type:" + mapping.getIoType().getType());
            System.out.println("    val:" + mapping.getIoType().getValue());
            System.out.println("    ref:" + mapping.getIoType().isReference());
        }

        Exec exec = new Exec(null);
        try {
            exec.execute(task, conf);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
