package org.trianacode.shiwa;

import org.shiwa.desktop.data.workflow.description.InputPort;
import org.shiwa.desktop.data.workflow.description.OutputPort;
import org.shiwa.desktop.data.workflow.description.Signature;
import org.shiwa.desktop.data.workflow.transfer.WorkflowEngineHandler;
import org.shiwa.desktop.gui.SHIWADesktopPanel;
import org.trianacode.TrianaInstance;
import org.trianacode.taskgraph.Cable;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
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

    public TrianaEngineHandler(Task task) {
        this.task = task;
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

    @Override
    public String getEngineVersion(Set<String> version) {
        return "triana";
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
//        signature.setInputPorts(getInputPorts());
//        signature.setOutputPorts(getOutputPorts());
        return signature;
    }

    private void setOutputPorts(Signature signature) {
        for (int i = 0; i < task.getDataOutputNodeCount(); i++) {
            String s = task.getDataOutputTypes(i)[0];
//            out.setDescription("Output port for a Triana unit. Produces data of type " + s);
            signature.addOutput("OutputPort " + i, ObjectToSchema.getSchemaURIString(s));
        }
    }

    private void setInputPorts(Signature signature) {
        for (int i = 0; i < task.getDataInputNodeCount(); i++) {
            String s = task.getDataInputTypes(i)[0];

            Node node = task.getDataInputNode(i);

            if (node.isConnected()) {
                Cable cable = node.getCable();
                System.out.println("Input node " + i + " is type : " + cable.getType());
                String inputObject = "";
                signature.addInputReference("InputPort " + i, ObjectToSchema.getSchemaURIString(s), inputObject);

            } else {
                signature.addInput("InputPort " + i, ObjectToSchema.getSchemaURIString(s));
            }
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

    private ArrayList<InputPort> getInputPorts() {
        ArrayList<InputPort> inputs = new ArrayList<InputPort>();
        for (int i = 0; i < task.getDataInputNodeCount(); i++) {
            String s = task.getDataInputTypes(i)[0];
            InputPort in = new InputPort("i" + i);
            in.setTitle("InputPort " + i);
//            in.setDataType(s);
            in.setDataType(ObjectToSchema.getSchemaURIString(s));

            in.setDescription("Input port for a Triana unit. Expects data of type " + s);
            inputs.add(in);
        }
        return inputs;
    }

    private ArrayList<OutputPort> getOutputPorts() {
        ArrayList<OutputPort> outputs = new ArrayList<OutputPort>();
        for (int i = 0; i < task.getDataOutputNodeCount(); i++) {
            String s = task.getDataOutputTypes(i)[0];
            OutputPort out = new OutputPort("o" + i);
            out.setTitle("OutputPort " + i);
//            out.setDataType(s);

            out.setDataType(ObjectToSchema.getSchemaURIString(s));

            out.setDescription("Output port for a Triana unit. Produces data of type " + s);
            outputs.add(out);
        }
        return outputs;
    }
}
