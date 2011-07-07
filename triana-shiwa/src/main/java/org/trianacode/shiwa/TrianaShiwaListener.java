package org.trianacode.shiwa;

import org.shiwa.desktop.data.description.handler.Port;
import org.shiwa.desktop.data.description.handler.Signature;
import org.shiwa.desktop.data.transfer.SHIWADesktopExecutionListener;
import org.trianacode.TrianaInstance;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoMapping;
import org.trianacode.enactment.io.IoType;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 06/07/2011
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public class TrianaShiwaListener implements SHIWADesktopExecutionListener {

    private TrianaInstance trianaInstance;

    public TrianaShiwaListener(TrianaInstance trianaInstance) {
        this.trianaInstance = trianaInstance;

    }

    public void setLoadedSignature(Task loadedTask, Signature signature) {
        ArrayList<IoMapping> inputMappings = new ArrayList<IoMapping>();

        List<Port> inputPorts = signature.getInputs();
        for (Port inputPort : inputPorts) {
            if (inputPort.getValue() != null) {
                String portName = inputPort.getName();
                String portNumberString = portName.substring(portName.indexOf("_") + 1);

                String value = inputPort.getValue();
                boolean reference = inputPort.isReference();


                IoMapping ioMapping = new IoMapping(new IoType(value, "string", reference), portNumberString);
                inputMappings.add(ioMapping);
            }
        }

        IoConfiguration conf = new IoConfiguration(loadedTask.getQualifiedToolName(), "0.1", inputMappings, new ArrayList<IoMapping>());


        System.out.println("\nTask name : " + loadedTask.getDisplayName() +
                "\n Qualified name : " + loadedTask.getQualifiedToolName()
        );
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
            exec.execute(loadedTask, conf);
        } catch (Exception e) {
            System.out.println("Failed to load workflow back to Triana");
            e.printStackTrace();
        }

    }

    @Override
    public void digestWorkflow(File file, Signature signature) {
        try {
            System.out.println("Importing a bundle");
            XMLReader reader = new XMLReader(new FileReader(file));
            Tool tool = reader.readComponent(trianaInstance.getProperties());

            if (signature.hasConfiguration()) {
                setLoadedSignature((Task) tool, signature);
            } else {
                if (GUIEnv.getApplicationFrame() != null && tool instanceof TaskGraph) {
                    GUIEnv.getApplicationFrame().addParentTaskGraphPanel((TaskGraph) tool);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
