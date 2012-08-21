//package org.trianacode.shiwaall.handler;
//
//import org.shiwa.desktop.data.description.handler.TransferSignature;
//import org.shiwa.desktop.data.description.workflow.Author;
//import org.shiwa.desktop.data.transfer.WorkflowEngineHandler;
//import org.shiwa.fgi.iwir.AbstractDataPort;
//import org.shiwa.fgi.iwir.IWIR;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
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
//public class TrianaIWIRHandler152 implements WorkflowEngineHandler {
//
//    private IWIR iwir;
//    private InputStream imageInputStream;
//
//    public TrianaIWIRHandler152(IWIR iwir, InputStream imageInputStream) {
//        this.iwir = iwir;
//        this.imageInputStream = imageInputStream;
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
//    public InputStream getWorkflowDefinition() {
//        try {
//            File file = File.createTempFile("iwir", "tmp");
//            file.deleteOnExit();
//            iwir.asXMLFile(file);
//            return new FileInputStream(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public String getWorkflowDefinitionName() {
//        return iwir.getWfname();
//    }
//
//    @Override
//    public InputStream getDisplayImage() {
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
//    public String getImplementationVersion() {
//        return null;
//    }
//}