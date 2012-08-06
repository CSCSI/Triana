package org.trianacode.shiwaall.extras;

import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoHandler;
import org.trianacode.enactment.io.IoMapping;
import org.trianacode.enactment.io.IoType;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.ser.DocumentHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 09/11/2011
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class DaxUtils {

    public static File createDummyIOConfigFile(TaskGraph taskGraph) throws IOException {
        List<IoMapping> inputMappings = new ArrayList<IoMapping>();
        for (Node node : taskGraph.getDataInputNodes()) {
            IoMapping ioMapping = new IoMapping(new IoType("dummyValue", "string", false), "" + node.getNodeIndex());
            inputMappings.add(ioMapping);
        }

        IoConfiguration conf = new IoConfiguration(taskGraph.getQualifiedToolName(), "0.1", inputMappings, new ArrayList<IoMapping>());

        List<IoMapping> mappings = conf.getInputs();
        for (IoMapping mapping : mappings) {
            System.out.println("  mapping:");
            System.out.println("    name:" + mapping.getNodeName());
            System.out.println("    type:" + mapping.getIoType().getType());
            System.out.println("    val:" + mapping.getIoType().getValue());
            System.out.println("    ref:" + mapping.getIoType().isReference());
        }

        DocumentHandler documentHandler = new DocumentHandler();
        new IoHandler().serialize(documentHandler, conf);
        File tempConfFile = File.createTempFile(conf.getToolName() + "_confFile", ".dat");
        documentHandler.output(new FileWriter(tempConfFile), true);

        return tempConfFile;
    }
}
