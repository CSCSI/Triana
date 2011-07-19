package org.trianacode.shiwa.iwir.test;

import org.shiwa.fgi.iwir.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 15/07/2011
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
public class IWIRParallelForEach {

    public IWIRParallelForEach() {

        IWIR iwir = new IWIR("testParallelForEach");

//        BlockScope blockScope = new BlockScope("main");
//        InputPort blockIn = new InputPort("in1", new CollectionType(SimpleType.STRING));
//        blockScope.addInputPort(blockIn);
//        OutputPort blockOut = new OutputPort("out1", SimpleType.STRING);
//        blockScope.addOutputPort(blockOut);
//

        ParallelForEachTask parallelForEachTask = new ParallelForEachTask("parallelForEach");
        InputPort parallelIn = new InputPort("in1", SimpleType.STRING);
        parallelForEachTask.addInputPort(parallelIn);
        LoopElement parallelLoop = new LoopElement("lp1", new CollectionType(SimpleType.STRING));
        parallelForEachTask.addLoopElement(parallelLoop);
        OutputPort parallelOut = new OutputPort("out1", new CollectionType(SimpleType.STRING));
        parallelForEachTask.addOutputPort(parallelOut);

        Task atomicTask = new Task("Executor", "bash_script");
        InputPort atomicIn = new InputPort("in1", SimpleType.STRING);
        atomicTask.addInputPort(atomicIn);
        InputPort atomicLoopIn = new InputPort("inLoop", SimpleType.STRING);
        atomicTask.addInputPort(atomicLoopIn);
        OutputPort atomicOut = new OutputPort("out1", SimpleType.STRING);
        atomicTask.addOutputPort(atomicOut);


        parallelForEachTask.addTask(atomicTask);
        parallelForEachTask.addLink(parallelIn, atomicIn);
        parallelForEachTask.addLink(parallelLoop, atomicLoopIn);
        parallelForEachTask.addLink(atomicOut, parallelOut);

//        blockScope.addTask(parallelForEachTask);
//        blockScope.addLink(blockIn, parallelIn);
//        blockScope.addLink(parallelOut, blockOut);
//

        iwir.setTask(parallelForEachTask);

        System.out.println(iwir.asXMLString());

    }

    public static void main(String[] args) {
        new IWIRParallelForEach();
    }
}
