package org.trianacode.shiwa.test;

import org.shiwa.fgi.iwir.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 28/09/2011
 * Time: 23:15
 * To change this template use File | Settings | File Templates.
 */
public class BlockInblock {

    public static void main(String[] args) throws IOException {
        new BlockInblock();
    }

    public BlockInblock() throws IOException {

        IWIR iwir = new IWIR("test");

        BlockScope outerBlockScope = new BlockScope("outerBlock");

        BlockScope innerBlockScope = new BlockScope("innerBlock");

        Task task = new Task("atomic", "pointless");

        iwir.setTask(outerBlockScope);

        outerBlockScope.addTask(innerBlockScope);

        innerBlockScope.addTask(task);

        InputPort outerInputPort = new InputPort("input", SimpleType.STRING);

        outerBlockScope.addInputPort(outerInputPort);

        InputPort innerInputPort = new InputPort("input", SimpleType.STRING);

        innerBlockScope.addInputPort(innerInputPort);

        InputPort taskInputPort = new InputPort("input", SimpleType.STRING);

        task.addInputPort(taskInputPort);

        outerBlockScope.addLink(outerInputPort, innerInputPort);

        innerBlockScope.addLink(innerInputPort, taskInputPort);


        OutputPort taskOutputPort = new OutputPort("output", SimpleType.STRING);
        OutputPort innerOutputPort = new OutputPort("output", SimpleType.STRING);
        OutputPort outerOutputPort = new OutputPort("output", SimpleType.STRING);

        task.addOutputPort(taskOutputPort);

        innerBlockScope.addOutputPort(innerOutputPort);

        outerBlockScope.addOutputPort(outerOutputPort);

        innerBlockScope.addLink(taskOutputPort, innerOutputPort);

        outerBlockScope.addLink(innerOutputPort, outerOutputPort);

        System.out.println(iwir.asXMLString());

        iwir.asXMLFile(new File("BlockInBlock.xml"));


    }
}
