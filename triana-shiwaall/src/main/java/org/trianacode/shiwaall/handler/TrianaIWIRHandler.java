//package org.trianacode.shiwaall.handler;
//
//import org.shiwa.desktop.data.description.handler.TransferSignature;
//import org.shiwa.desktop.data.description.workflow.Author;
//import org.shiwa.desktop.data.transfer.WorkflowEngineHandler;
//import org.shiwa.fgi.iwir.AbstractDataPort;
//import org.shiwa.fgi.iwir.BlockScope;
//import org.shiwa.fgi.iwir.IWIR;
//import org.trianacode.shiwaall.iwir.importer.utils.ExportIwir;
//import org.trianacode.taskgraph.TaskGraph;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.util.List;
//import java.util.Set;
//
///**
// * Created by IntelliJ IDEA.
// * User: Ian Harvey
// * Date: 15/12/2011
// * Time: 14:26
// * To change this template use File | Settings | File Templates.
// */
//public class TrianaIWIRHandler implements WorkflowEngineHandler {
//
//    private IWIR iwir;
//    private InputStream imageInputStream;
//
//    public TrianaIWIRHandler(TaskGraph taskGraph, InputStream imageInputStream) {
//        this.imageInputStream = imageInputStream;
//        try {
//            init(taskGraph);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void init(TaskGraph taskGraph) throws IOException {
//        ExportIwir exportIwir = new ExportIwir();
//        BlockScope blockscope = exportIwir.taskGraphToBlockScope(taskGraph);
//        iwir = new IWIR(taskGraph.getToolName());
//        iwir.setTask(blockscope);
//
////        File file = File.createTempFile("iwir", "tmp");
////        file.deleteOnExit();
////        System.out.println("Created temp iwir " + file.getAbsolutePath());
////        iwir.asXMLFile(file);
//    }
//
//    @Override
//    public String getEngineName(Set<String> strings) {
//        return "Triana";
//    }
//
//    @Override
//    public String getEngineVersion() {
//        return "4.0";
//    }
//
//    @Override
//    public String getWorkflowLanguage(Set<String> strings) {
//        return "IWIR";
//    }
//
//    @Override
//    public TransferSignature getSignature() {
//        TransferSignature signature = new TransferSignature();
//        signature.setName(iwir.getWfname());
//
//        for (AbstractDataPort i : iwir.getTask().getAllInputPorts()) {
//            signature.addInput(i.getName(), i.getType().toString());
//        }
//        for (AbstractDataPort j : iwir.getTask().getAllOutputPorts()) {
//            signature.addInput(j.getName(), j.getType().toString());
//        }
//        return signature;
//    }
//
//    @Override
//    public InputStream getDefinition() {
//        System.out.printf("Returning def bytestream");
//        try {
//            return new ByteArrayInputStream(iwir.asXMLString().getBytes("UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @Override
//    public String getDefinitionName() {
//        return iwir.getWfname();
//    }
//
//    @Override
//    public InputStream getDisplayImage() {
//        System.out.println("Returning representation of iwir graphic");
//        return imageInputStream;
//    }
//
//    @Override
//    public String getDisplayImageName() {
//        return iwir.getWfname() + "-image.jpg";
//    }
//
//    @Override
//    public List<Author> getAuthors() {
//        return null;
//    }
//
//    @Override
//    public String getDescription() {
//        return null;
//    }
//
//    @Override
//    public String getVersion() {
//        return null;
//    }
//}
