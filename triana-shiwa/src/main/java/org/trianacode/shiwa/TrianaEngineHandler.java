//package org.trianacode.shiwa;
//
//import org.trianacode.TrianaInstance;
//import org.trianacode.shiwa.desktop.data.bundle.WorkflowEngineHandler;
//import org.trianacode.shiwa.desktop.data.description.Signature;
//import org.trianacode.taskgraph.Task;
//
//import java.io.InputStream;
//
///**
// * Created by IntelliJ IDEA.
// * User: ian
// * Date: 24/02/2011
// * Time: 12:56
// * To change this template use File | Settings | File Templates.
// */
//public class TrianaEngineHandler implements WorkflowEngineHandler {
//
//    private TrianaInstance instance;
//    private Task task;
//
//    public TrianaEngineHandler(TrianaInstance instance, Task task){
//        this.instance = instance;
//        this.task = task;
//    }
//
//    @Override
//    public String getEngineVersion() {
//        return "v4";
//    }
//
//    @Override
//    public String getEngineName() {
//       return "Triana";
//    }
//
//    @Override
//    public Signature getSignature() {
//
////        Signature signature = new Signature();
////        signature.setName("name");
////        signature.setInputPorts(new ArrayList<InputPort>());
////        signature.setOutputPorts(new ArrayList<OutputPort>());
////        return signature;
//
//        return null;
//    }
//
//    @Override
//    public InputStream getWorkflowDefinition() {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//}
