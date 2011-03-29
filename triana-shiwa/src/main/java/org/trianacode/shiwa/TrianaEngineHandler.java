package org.trianacode.shiwa;

import org.shiwa.desktop.data.configuration.Configuration;
import org.shiwa.desktop.data.workflow.description.Dependency;
import org.shiwa.desktop.data.workflow.description.InputPort;
import org.shiwa.desktop.data.workflow.description.OutputPort;
import org.shiwa.desktop.data.workflow.description.Signature;
import org.shiwa.desktop.data.workflow.transfer.WorkflowEngineHandler;
import org.trianacode.TrianaInstance;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.ser.XMLWriter;

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

    private TrianaInstance instance;
    private Task task;

    public TrianaEngineHandler(TrianaInstance instance, Task task) {
        this.instance = instance;
        this.task = task;
    }

    @Override
    public String getEngineVersion(Set<String> version) {
        return "http://shiwa-workflow.eu/concepts#triana3-24,http://shiwa-workflow.eu/concepts#triana2-10";
    }

    @Override
    public String getWorkflowLanguage(Set<String> languages) {
        return "http://shiwa-workflow.eu/concepts#triana-taskgraph";
    }

    @Override
    public Signature getSignature() {
        Signature signature = new Signature();
        signature.setTitle(task.getToolName());
        signature.setInputPorts(getInputPorts());
        signature.setOutputPorts(getOutputPorts());
        return signature;
    }

    @Override
    public List<Dependency> getDependencies() {
        //    return new ArrayList<Dependency>();
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    public String getWorkflowDefinitionPath() {
        return task.getToolName() + ".xml";
    }

    @Override
    public Configuration getConfiguration(List<InputPort> inputPorts, List<Dependency> dependencies) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private ArrayList<InputPort> getInputPorts() {
        ArrayList<InputPort> inputs = new ArrayList<InputPort>();
        for (int i = 0; i < task.getDataInputNodeCount(); i++) {
            String s = task.getDataInputTypes(i)[0];
            InputPort in = new InputPort("" + i);
            in.setTitle("InputPort " + i);
            in.setDataType(s);
            in.setDescription("Input port for a Triana unit. Expects data of type " + s);
            inputs.add(in);
        }
        return inputs;
    }

    private ArrayList<OutputPort> getOutputPorts() {
        ArrayList<OutputPort> outputs = new ArrayList<OutputPort>();
        for (int i = 0; i < task.getDataOutputNodeCount(); i++) {
            String[] ss = task.getDataOutputTypes(i);
            OutputPort out = new OutputPort("" + i);
            out.setTitle("InputPort " + i);
            out.setDataType(ss[0]);
            out.setDescription("Output port for a Triana unit. Produces data of type " + ss[0]);
            outputs.add(out);
        }
        return outputs;
    }
}
