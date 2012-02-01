package org.trianacode.enactment;

import org.trianacode.TrianaInstance;
import org.trianacode.config.Locations;
import org.trianacode.config.cl.ArgumentParsingException;
import org.trianacode.config.cl.OptionValues;
import org.trianacode.config.cl.OptionsHandler;
import org.trianacode.config.cl.TrianaOptions;
import org.trianacode.enactment.addon.CLIaddon;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.databus.DataBus;
import org.trianacode.taskgraph.databus.DataBusInterface;
import org.trianacode.taskgraph.databus.DataNotResolvableException;
import org.trianacode.taskgraph.databus.packet.WorkflowDataPacket;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.service.SchedulerException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 10/11/2011
 * Time: 18:28
 * To change this template use File | Settings | File Templates.
 */
public class RunUnit {
    public RunUnit(String[] args) throws TaskGraphException, ClassNotFoundException, InterruptedException, IOException, ProxyInstantiationException, SchedulerException {

        String os = Locations.os();
        String usage = "./triana.sh";
        if (os.equals("windows")) {
            usage = "triana.bat";
        }
        OptionsHandler parser = new OptionsHandler(usage, TrianaOptions.TRIANA_OPTIONS);
        OptionValues vals = null;
        try {
            vals = parser.parse(args);
        } catch (ArgumentParsingException e) {
            System.out.println(e.getMessage());
            System.out.println(parser.usage());
            System.exit(0);
        }

        TrianaInstance engine = new TrianaInstance(args);
        engine.addExtensionClass(CLIaddon.class);
        engine.init();
        Thread.sleep(1000);

        ArrayList<String> unitArgs = (ArrayList<String>) vals.getOptionValues(TrianaOptions.RUN_UNIT.getShortOpt());

        String className = unitArgs.get(0);
        String[] splitName = new String[]{
                className.substring(0, className.lastIndexOf(".")),
                className.substring(className.lastIndexOf(".") + 1)
        };
        System.out.println(splitName[0]);
        System.out.println(splitName[1]);

        ToolImp tool = AddonUtils.makeTool(splitName[1], splitName[0], "singleUnit", engine.getProperties());
//        unitArgs.remove(0);
//        String[] params = unitArgs.toArray(new String[unitArgs.size()]);

        List<String> inputFiles = vals.getOptionValues(TrianaOptions.INPUT_FILES.getShortOpt());
        tool.setDataInputNodeCount(inputFiles.size());

        List<String> outputFiles = null;
        if (vals.hasOption(TrianaOptions.OUTPUT_FILES.getShortOpt())) {
            outputFiles = vals.getOptionValues(TrianaOptions.OUTPUT_FILES.getShortOpt());
            tool.setDataOutputNodeCount(outputFiles.size());
        }

        TrianaRun runner = new TrianaRun(tool);
        runner.runTaskGraph();

        for (int i = 0; i < inputFiles.size(); i++) {
            Object inputObject = readSerialFile(new File(inputFiles.get(i)));
            System.out.println(inputObject);
            runner.sendInputData(i, inputObject);
        }

        while (!runner.isFinished()) {
            synchronized (this) {
                try {
                    wait(100);
                } catch (InterruptedException e) {

                }
            }
        }


        Node[] nodes = runner.getTaskGraph().getDataOutputNodes();
        for (Node node : nodes) {
            Object out = runner.receiveOutputData(0);
            Object o = null;
            if (out instanceof WorkflowDataPacket) {
                try {
                    DataBusInterface db = DataBus.getDataBus(((WorkflowDataPacket) out).getProtocol());
                    o = db.get((WorkflowDataPacket) out);

                    if (outputFiles != null && outputFiles.get(node.getAbsoluteNodeIndex()) != null) {
                        writeSerialFile(o, outputFiles.get(node.getAbsoluteNodeIndex()));
                    }

                } catch (DataNotResolvableException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Exec.execute output node " + node.getName() + " data : " + o);
        }
        runner.dispose();

    }

    private static Object readSerialFile(File file) throws ClassNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object incomingObject = objectInputStream.readObject();
        return incomingObject;
    }

    private static File writeSerialFile(Object object, String filePath) throws IOException {
        File file = new File(filePath);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);

        return file;
    }


}
