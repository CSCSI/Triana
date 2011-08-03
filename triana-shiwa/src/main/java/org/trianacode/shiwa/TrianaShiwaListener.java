package org.trianacode.shiwa;

import org.shiwa.desktop.data.description.handler.Port;
import org.shiwa.desktop.data.description.handler.Signature;
import org.shiwa.desktop.data.transfer.SHIWADesktopExecutionListener;
import org.trianacode.TrianaInstance;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.TrianaRun;
import org.trianacode.enactment.io.*;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.service.SchedulerException;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
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

    public IoConfiguration getIOConfigFromSignature(String name, Signature signature) {
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

        IoConfiguration conf = new IoConfiguration(name, "0.1", inputMappings, new ArrayList<IoMapping>());

        List<IoMapping> mappings = conf.getInputs();
        for (IoMapping mapping : mappings) {
            System.out.println("  mapping:");
            System.out.println("    name:" + mapping.getNodeName());
            System.out.println("    type:" + mapping.getIoType().getType());
            System.out.println("    val:" + mapping.getIoType().getValue());
            System.out.println("    ref:" + mapping.getIoType().isReference());
        }

        return conf;
    }

    @Override
    public void digestWorkflow(File file, Signature signature) {
        try {
            System.out.println("Importing a bundle");
            XMLReader reader = new XMLReader(new FileReader(file));
            Tool tool = reader.readComponent(trianaInstance.getProperties());

            if (signature.hasConfiguration()) {
                if (GUIEnv.getApplicationFrame() != null && tool instanceof TaskGraph) {
                    GUIEnv.getApplicationFrame().addParentTaskGraphPanel((TaskGraph) tool);
                    executeWorkflowInGUI(tool.getQualifiedToolName(), tool, signature);
                } else {
                    System.out.println("No gui found, running headless");
                    exec((Task) tool, getIOConfigFromSignature(((Task) tool).getQualifiedTaskName(), signature));
                }
            } else {
                if (GUIEnv.getApplicationFrame() != null && tool instanceof TaskGraph) {
                    GUIEnv.getApplicationFrame().addParentTaskGraphPanel((TaskGraph) tool);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void executeWorkflowInGUI(String toolName, Tool tool, Signature signature) throws TaskGraphException, SchedulerException {
        TrianaRun runner = new TrianaRun(tool);
//        runner.getScheduler().addExecutionListener(this);

        NodeMappings mappings = null;

        IoHandler handler = new IoHandler();
        IoConfiguration ioc = getIOConfigFromSignature(toolName, signature);
        mappings = handler.map(ioc, runner.getTaskGraph());
        System.out.println("Data mappings size : " + mappings.getMap().size());

        runner.runTaskGraph();
        if (mappings != null) {
            Iterator<Integer> it = mappings.iterator();
            while (it.hasNext()) {
                Integer integer = it.next();
                Object val = mappings.getValue(integer);
                System.out.println("Data : " + val.toString() + " will be sent to input number " + integer);
                runner.sendInputData(integer, val);
                System.out.println("Exec.execute sent input data");
            }
        } else {
            System.out.println("Mappings was null");
        }

        while (!runner.isFinished()) {
            synchronized (this) {
                try {
                    wait(100);
                } catch (InterruptedException e) {

                }
            }
        }
//
//        Node[] nodes = runner.getTaskGraph().getDataOutputNodes();
//        for (Node node : nodes) {
//            Object out = runner.receiveOutputData(0);
//            Object o = null;
//            if (out instanceof WorkflowDataPacket) {
//                try {
//                    DataBusInterface db = DataBus.getDataBus(((WorkflowDataPacket) out).getProtocol());
//                    o = db.get((WorkflowDataPacket) out);
//                } catch (DataNotResolvableException e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println("Exec.execute output:" + o);
//        }
        runner.dispose();

    }

    private void exec(Task loadedTask, IoConfiguration conf) {
        Exec exec = new Exec(null);
        try {
            exec.execute(loadedTask, conf);
        } catch (Exception e) {
            System.out.println("Failed to load workflow back to Triana");
            e.printStackTrace();
        }
    }
}
