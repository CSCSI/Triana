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
import java.util.Properties;

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
        if(className == null){
            return;
        }
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

        if(vals.hasOption(TrianaOptions.UNIT_PROPERTIES.getShortOpt())) {
            List<String> unitProperties = vals.getOptionValues(TrianaOptions.UNIT_PROPERTIES.getShortOpt());
            if(unitProperties.size() > 0){
                String propertiesLocation = unitProperties.get(0);
                File propertiesFile = new File(propertiesLocation);
                if(propertiesFile.exists()){
                    Properties properties = new Properties();
                    properties.loadFromXML(new FileInputStream(propertiesFile));
                    for(Object property : properties.keySet()){
                        tool.setParameter((String) property, properties.get(property));
                    }
                }
            }

        }

        TrianaRun runner = new TrianaRun(tool);
        runner.runTaskGraph();

        for (int i = 0; i < inputFiles.size(); i++) {
            Object inputObject;
            File inputFile = new File(inputFiles.get(i));
            if(inputFile.exists()){
                try {
                    inputObject = readSerialFile(inputFile);
                    System.out.println("Serialised input : \n" + inputObject);
                } catch (Exception e) {
                    inputObject = readNonSerialFile(inputFile);
                    System.out.println("Raw text input : \n" +  (String)inputObject);
                }
                runner.sendInputData(i, inputObject);
            } else {
                System.out.println("File " + inputFile.getName() + " not found. Exiting");
                return;
            }
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
            } else {
                System.out.println("Not a workflowDataPacket");
            }
            System.out.println("Exec.execute output node " + node.getName() + " data : " + o);
        }
        runner.dispose();

    }

    private String readNonSerialFile(File inputFile) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        StringBuilder stringBuilder = new StringBuilder();
//        byte[] in = new byte[128];
//        int length = 0;
//        while ((length = fileInputStream.read(in)) > 0) {
//            stringBuilder.append(in.toString());
//        }

        int content;
        while ((content = fileInputStream.read()) != -1) {
            // convert to char and display it
//            System.out.print("char " + (char) content);
            stringBuilder.append((char)content);
        }
        return stringBuilder.toString();
    }

    private static Object readSerialFile(File file) throws IOException, ClassNotFoundException {
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
